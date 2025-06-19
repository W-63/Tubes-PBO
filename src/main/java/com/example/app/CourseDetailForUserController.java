package com.example.app;

import com.example.app.LoginController;
import com.example.app.model.Course;
import com.example.app.model.Module; // Tetap diimpor karena ModuleDisplayWrapper membungkusnya
import com.example.app.model.ModuleDisplayWrapper; // Ditambahkan
import com.example.app.model.Enrollment;
import com.example.app.model.User;
import com.example.app.model.UserModuleProgress;
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

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CourseDetailForUserController {

    @FXML private Label courseTitleLabel;
    @FXML private Label courseDescriptionLabel;
    @FXML private Label coursePriceLabel;
    @FXML private Label courseDurationLabel;
    @FXML private Label courseTotalModulesLabel;
    @FXML private Label courseProgressPercentageLabel;

    @FXML private TableView<ModuleDisplayWrapper> modulesTable; // Diubah ke ModuleDisplayWrapper
    @FXML private TableColumn<ModuleDisplayWrapper, Integer> moduleNumberColumn;
    @FXML private TableColumn<ModuleDisplayWrapper, String> moduleTitleColumn;
    @FXML private TableColumn<ModuleDisplayWrapper, String> moduleTypeColumn;
    @FXML private TableColumn<ModuleDisplayWrapper, Integer> moduleDurationColumn;
    @FXML private TableColumn<ModuleDisplayWrapper, Boolean> moduleCompletedColumn; // Checkbox selesai

    @FXML private Button backButton;
    @FXML private Button refreshButton;

    private Course selectedCourse;
    private User currentUser;
    private Enrollment enrollment;

    private CourseService courseService;
    private ModuleService moduleService;
    private EnrollmentService enrollmentService;

    private ObservableList<ModuleDisplayWrapper> moduleList; // Diubah ke ModuleDisplayWrapper

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

        // Kolom checkbox "Selesai" menggunakan properti dari ModuleDisplayWrapper
        // PropertyValueFactory akan mencari "completedForUserProperty()" atau "getCompletedForUser()"
        moduleCompletedColumn.setCellValueFactory(new PropertyValueFactory<>("completedForUser"));
        moduleCompletedColumn.setCellFactory(CheckBoxTableCell.forTableColumn(moduleCompletedColumn));

        // Listener untuk perubahan status checkbox
        moduleCompletedColumn.setOnEditCommit(event -> {
            ModuleDisplayWrapper wrapper = event.getRowValue();
            boolean isNowCompleted = event.getNewValue(); // Nilai baru dari checkbox

            try {
                if (currentUser == null || selectedCourse == null || enrollment == null || wrapper.getId() == null) {
                    showAlert("Error", "Sesi atau data tidak valid. Harap login kembali.", Alert.AlertType.ERROR);
                    modulesTable.refresh(); // Refresh untuk reset checkbox jika ada masalah
                    return;
                }

                // Update status modul di database
                moduleService.updateModuleCompletionStatus(wrapper.getId(), isNowCompleted);
                showAlert("Sukses", "Progres modul '" + wrapper.getTitle() + "' diperbarui.", Alert.AlertType.INFORMATION);

                // Setelah modul diperbarui, perbarui juga progres kelas keseluruhan
                if (enrollment.getId() != 0 && currentUser.getId() != 0 && selectedCourse.getId() != 0) {
                    enrollmentService.updateProgressBasedOnModules(enrollment.getId(), currentUser.getId(), selectedCourse.getId());
                } else {
                    System.err.println("Error: enrollment, currentUser, atau selectedCourse ID bernilai 0 saat update progres kelas.");
                }

                // Muat ulang data untuk update UI, termasuk persentase progres kelas
                loadCourseAndModules(selectedCourse, enrollment); // Pastikan ini akan memuat ulang data terbaru
            } catch (Exception e) {
                showAlert("Gagal", "Gagal memperbarui progres modul: " + e.getMessage(), Alert.AlertType.ERROR);
                e.printStackTrace();
                // Kembalikan nilai checkbox di UI jika terjadi error
                wrapper.setCompletedForUser(!isNowCompleted); // Set kembali nilai lama
                modulesTable.refresh(); // Refresh tabel untuk menampilkan nilai lama
            }
        });

        modulesTable.setItems(moduleList);
        modulesTable.setEditable(true); // Agar checkbox bisa diinteraksi
        moduleCompletedColumn.setEditable(true); // Agar kolom checkbox bisa diedit

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
            // Tampilkan detail kelas
            courseTitleLabel.setText(course.getTitle());
            courseDescriptionLabel.setText(course.getDescription());
            coursePriceLabel.setText("Harga: Rp" + course.getPrice());
            courseDurationLabel.setText("Durasi: " + course.getDurationWeeks() + " Minggu");
            courseTotalModulesLabel.setText("Total Modul: " + course.getTotalModules());

            // Ambil enrollment terbaru untuk mendapatkan progres yang akurat setelah update
            // Ini penting karena objek `enrollment` yang diteruskan mungkin bukan yang terbaru setelah update progres
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

            modulesTable.refresh(); // Penting untuk me-refresh cell factories
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

            // Kembali ke halaman Kelas Saya
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/user_my_courses.fxml"));
            Parent myCoursesRoot = loader.load();

            UserMyCoursesController myCoursesController = loader.getController();
            if (myCoursesController != null) {
                myCoursesController.initUserData(LoginController.currentLoggedInUser); // Teruskan user
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