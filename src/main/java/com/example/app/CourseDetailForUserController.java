package com.example.app;

import com.example.app.LoginController;
import com.example.app.model.Course;
import com.example.app.model.Module;
import com.example.app.model.ModuleDisplayWrapper;
import com.example.app.model.Enrollment;
import com.example.app.model.User;
import com.example.app.service.CourseService;
import com.example.app.service.EnrollmentService;
import com.example.app.service.ModuleService;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.scene.control.CheckBox; // Ditambahkan untuk CheckBox kustom

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional; // Ditambahkan

public class CourseDetailForUserController {

    @FXML private Label courseTitleLabel;
    @FXML private Label courseDescriptionLabel;
    @FXML private Label coursePriceLabel;
    @FXML private Label courseDurationLabel;
    @FXML private Label courseTotalModulesLabel;
    @FXML private Label courseProgressPercentageLabel;

    @FXML private TableView<ModuleDisplayWrapper> modulesTable;
    @FXML private TableColumn<ModuleDisplayWrapper, Integer> moduleNumberColumn;
    @FXML private TableColumn<ModuleDisplayWrapper, String> moduleTitleColumn;
    @FXML private TableColumn<ModuleDisplayWrapper, String> moduleTypeColumn;
    @FXML private TableColumn<ModuleDisplayWrapper, Integer> moduleDurationColumn;
    @FXML private TableColumn<ModuleDisplayWrapper, Boolean> moduleCompletedColumn;

    @FXML private Button backButton;
    @FXML private Button refreshButton;

    private Course selectedCourse;
    private User currentUser;
    private Enrollment enrollment; // Enrollment yang sedang aktif untuk kelas ini

    private CourseService courseService;
    private ModuleService moduleService;
    private EnrollmentService enrollmentService;

    private ObservableList<ModuleDisplayWrapper> moduleList;

    @FXML
    public void initialize() {
        courseService = new CourseService();
        moduleService = new ModuleService();
        enrollmentService = new EnrollmentService();
        moduleList = FXCollections.observableArrayList();

        // Inisialisasi kolom tabel modul
        moduleNumberColumn.setCellValueFactory(new PropertyValueFactory<>("moduleNumber"));
        moduleTitleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        moduleTypeColumn.setCellValueFactory(new PropertyValueFactory<>("moduleType"));
        moduleDurationColumn.setCellValueFactory(new PropertyValueFactory<>("durationMinutes"));

        moduleCompletedColumn.setCellValueFactory(cellData -> cellData.getValue().completedForUserProperty());
        moduleCompletedColumn.setCellFactory(column -> {
            return new TableCell<ModuleDisplayWrapper, Boolean>() {
                private final CheckBox checkBox = new CheckBox();

                {
                    checkBox.setOnAction(event -> {
                        System.out.println("DEBUG: Checkbox clicked directly for module ID: " + getItem() + " (Item ID: " + (getTableView().getItems().get(getIndex()).getId()) + ")");

                        ModuleDisplayWrapper wrapper = getTableView().getItems().get(getIndex());
                        boolean isNowCompleted = checkBox.isSelected();

                        System.out.println("DEBUG: Attempting to update progress for module: " + wrapper.getTitle() + ", new status: " + isNowCompleted);

                        try {
                            if (currentUser == null || selectedCourse == null || enrollment == null || wrapper.getId() == null) {
                                showAlert("Error", "Sesi atau data tidak valid. Harap login kembali.", Alert.AlertType.ERROR);
                                checkBox.setSelected(!isNowCompleted);
                                return;
                            }

                            moduleService.updateModuleCompletionStatus(wrapper.getId(), isNowCompleted);
                            showAlert("Sukses", "Progres modul '" + wrapper.getTitle() + "' diperbarui.", Alert.AlertType.INFORMATION);

                            if (enrollment.getId() != 0 && currentUser.getId() != 0 && selectedCourse.getId() != 0) {
                                enrollmentService.updateProgressBasedOnModules(enrollment.getId(), currentUser.getId(), selectedCourse.getId());
                                System.out.println("DEBUG: Enrollment progress update call to service successful.");
                            } else {
                                System.err.println("Error: enrollment, currentUser, atau selectedCourse ID null saat update progres kelas.");
                            }

                            loadCourseAndModules(selectedCourse, enrollment);
                            System.out.println("DEBUG: loadCourseAndModules called after update.");

                        } catch (Exception e) {
                            System.err.println("DEBUG: Exception caught during module progress update!");
                            showAlert("Gagal", "Gagal memperbarui progres modul: " + e.getMessage(), Alert.AlertType.ERROR);
                            e.printStackTrace();
                            checkBox.setSelected(!isNowCompleted);
                        }
                    });
                }

                @Override
                protected void updateItem(Boolean completed, boolean empty) {
                    super.updateItem(completed, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        checkBox.setSelected(completed);
                        setGraphic(checkBox);
                    }
                }
            };
        });

        modulesTable.setItems(moduleList);
        modulesTable.setEditable(true);
        moduleCompletedColumn.setEditable(true);
    }

    public void initData(Course course, Enrollment enrollment, User user) {
        this.selectedCourse = course;
        this.enrollment = enrollment;
        this.currentUser = user;

        if (selectedCourse != null && enrollment != null && currentUser != null) {
            loadCourseAndModules(selectedCourse, enrollment);
        } else {
            showAlert("Error", "Data kelas atau pengguna tidak ditemukan.", Alert.AlertType.ERROR);
        }
    }

    private void loadCourseAndModules(Course course, Enrollment enrollment) {
        try {
            // --- LOGIKA DETEKSI DAN RESET MODUL BARU ---
            int currentTotalModulesInCourse = moduleService.getTotalModulesInCourse(course.getId());
            if (currentTotalModulesInCourse > course.getTotalModules()) { // Jika total modul di DB > total modul saat enrollment
                showAlert("Pembaruan Kelas", "Kelas ini telah diperbarui dengan modul baru! Progres Anda di kelas ini akan direset.", Alert.AlertType.INFORMATION);
                
                // Reset progres enrollment
                if (enrollment.getId() != 0 && currentUser.getId() != 0 && selectedCourse.getId() != 0) {
                    enrollmentService.enrollUserInCourse(currentUser.getId(), selectedCourse.getId()); // Panggil enrollUserInCourse untuk reset
                    // enrollmentService.updateProgressBasedOnModules(enrollment.getId(), currentUser.getId(), selectedCourse.getId()); // Ini akan dipanggil di enrollUserInCourse
                    
                    // Update objek course dengan total modul yang baru (dari DB)
                    this.selectedCourse = courseService.getCourseById(course.getId());
                    if(this.selectedCourse != null) {
                        course.setTotalModules(this.selectedCourse.getTotalModules());
                    }

                    // Ambil kembali enrollment yang sudah direset
                    Optional<Enrollment> updatedEnrollmentOpt = enrollmentService.getEnrollmentByUserIdAndCourseId(currentUser.getId(), selectedCourse.getId());
                    if(updatedEnrollmentOpt.isPresent()) {
                        this.enrollment = updatedEnrollmentOpt.get();
                    }
                } else {
                    System.err.println("Error: Tidak bisa mereset progres karena ID tidak valid.");
                }
            }


            // Tampilkan detail kelas
            courseTitleLabel.setText(course.getTitle());
            courseDescriptionLabel.setText(course.getDescription());
            coursePriceLabel.setText("Harga: Rp" + course.getPrice());
            courseDurationLabel.setText("Durasi: " + course.getDurationWeeks() + " Minggu");
            courseTotalModulesLabel.setText("Total Modul: " + course.getTotalModules()); // Pastikan ini updated
            
            // Ambil enrollment terbaru untuk mendapatkan progres yang akurat setelah update
            Enrollment currentEnrollmentStatus = enrollmentService.getUserEnrollments(currentUser.getId())
                                                                 .stream()
                                                                 .filter(e -> e.getCourseId().equals(course.getId()))
                                                                 .findFirst()
                                                                 .orElse(null);
            if (currentEnrollmentStatus != null) {
                courseProgressPercentageLabel.setText("Progres Kelas: " + currentEnrollmentStatus.getProgressPercentage() + "%");
                this.enrollment = currentEnrollmentStatus; // Perbarui objek enrollment di controller
            } else {
                courseProgressPercentageLabel.setText("Progres Kelas: 0%");
            }

            // Muat daftar modul untuk kelas ini beserta status progres user
            List<ModuleDisplayWrapper> wrappers = moduleService.getModulesWithProgressForUser(currentUser.getId(), course.getId());
            moduleList.setAll(wrappers);

            modulesTable.refresh();
        } catch (SQLException e) {
            showAlert("Kesalahan Database", "Gagal memuat detail kelas atau modul: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        } catch (Exception e) {
            showAlert("Kesalahan", "Terjadi kesalahan tak terduga saat memuat kelas: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    @FXML
    private void handleRefresh() {
        if (selectedCourse != null && enrollment != null && currentUser != null) {
            loadCourseAndModules(selectedCourse, enrollment);
            showAlert("Pembaruan", "Data modul diperbarui.", Alert.AlertType.INFORMATION);
        }
    }

    @FXML
    private void handleBack(ActionEvent event) {
        try {
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/user_my_courses.fxml"));
            Parent myCoursesRoot = loader.load();

            UserMyCoursesController myCoursesController = loader.getController();
            if (myCoursesController != null) {
                myCoursesController.initUserData(LoginController.currentLoggedInUser);
            }

            Scene myCoursesScene = new Scene(myCoursesRoot);
            currentStage.setScene(myCoursesScene);
            currentStage.setTitle("Kelas Saya - EDULIFE+");
            currentStage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Kesalahan Navigasi", "Gagal memuat halaman Kelas Saya: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.setHeaderText(null);
        alert.showAndWait();
    }
}