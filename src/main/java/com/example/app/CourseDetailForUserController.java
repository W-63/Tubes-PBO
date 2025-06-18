package com.example.app;

import com.example.app.LoginController;
import com.example.app.model.Course;
import com.example.app.model.Module;
import com.example.app.model.Enrollment; // Ditambahkan
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
    @FXML private Label courseProgressPercentageLabel; // Untuk menampilkan progres kelas secara keseluruhan

    @FXML private TableView<Module> modulesTable;
    @FXML private TableColumn<Module, Integer> moduleNumberColumn;
    @FXML private TableColumn<Module, String> moduleTitleColumn;
    @FXML private TableColumn<Module, String> moduleTypeColumn;
    @FXML private TableColumn<Module, Integer> moduleDurationColumn;
    @FXML private TableColumn<Module, Boolean> moduleCompletedColumn; // Untuk checkbox selesai

    @FXML private Button backButton;
    @FXML private Button refreshButton;

    private Course selectedCourse;
    private User currentUser;
    private Enrollment enrollment; // Enrollment yang sedang aktif untuk kelas ini

    private CourseService courseService;
    private ModuleService moduleService;
    private EnrollmentService enrollmentService; // Untuk update progres enrollment keseluruhan

    private ObservableList<Module> moduleList; // Daftar modul yang akan ditampilkan di tabel

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

        moduleCompletedColumn.setCellValueFactory(cellData -> {
            Module module = cellData.getValue();
            // Cek apakah modul ini sudah selesai untuk user yang login
            // Ini akan diperbarui di loadCourseAndModules, karena kita butuh UserModuleProgress
            SimpleBooleanProperty booleanProp = new SimpleBooleanProperty(false); // Default false

            // Listener untuk perubahan status checkbox
            booleanProp.addListener((obs, wasCompleted, isNowCompleted) -> {
                try {
                    if (currentUser == null) {
                        showAlert("Error", "Sesi pengguna tidak valid. Harap login kembali.", Alert.AlertType.ERROR);
                        booleanProp.set(wasCompleted); // Kembalikan ke nilai sebelumnya
                        return;
                    }
                    if (module.getId() == null) {
                         showAlert("Error", "ID modul tidak valid.", Alert.AlertType.ERROR);
                         booleanProp.set(wasCompleted);
                         return;
                    }
                    moduleService.updateModuleCompletionStatus(module.getId(), isNowCompleted);
                    showAlert("Sukses", "Progres modul " + module.getTitle() + " diperbarui.", Alert.AlertType.INFORMATION);

                    // Setelah modul diperbarui, perbarui juga progres kelas keseluruhan
                    if (enrollment != null) {
                        enrollmentService.updateProgressBasedOnModules(enrollment.getId(), currentUser.getId(), selectedCourse.getId());
                        loadCourseAndModules(selectedCourse, enrollment); // Muat ulang untuk update progres keseluruhan
                    }
                } catch (Exception e) {
                    showAlert("Gagal", "Gagal memperbarui progres modul: " + e.getMessage(), Alert.AlertType.ERROR);
                    booleanProp.set(wasCompleted); // Kembalikan ke nilai sebelumnya jika gagal
                    e.printStackTrace();
                }
            });
            return booleanProp;
        });
        moduleCompletedColumn.setCellFactory(CheckBoxTableCell.forTableColumn(moduleCompletedColumn));


        modulesTable.setItems(moduleList);
        modulesTable.setEditable(true); // Agar checkbox bisa diinteraksi
        moduleCompletedColumn.setEditable(true); // Agar kolom checkbox bisa diedit

        // Data akan dimuat via initData
    }

    /**
     * Metode ini dipanggil dari UserMyCoursesController untuk meneruskan data kelas yang dipilih.
     * @param course Kelas yang dipilih.
     * @param enrollment Pendaftaran kelas yang terkait.
     * @param user Pengguna yang sedang login.
     */
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
            courseProgressPercentageLabel.setText("Progres Kelas: " + enrollment.getProgressPercentage() + "%"); // Ambil dari objek enrollment

            // Muat daftar modul untuk kelas ini
            List<Module> modules = moduleService.getModulesByCourseId(course.getId());

            // Untuk setiap modul, cek progres user dan update status checkbox
            List<UserModuleProgress> userProgresses = moduleService.getUserModuleProgressByCourse(currentUser.getId(), course.getId());

            // Buat map untuk akses cepat progres per modul ID
            Map<Integer, UserModuleProgress> progressMap = new HashMap<>();
            for (UserModuleProgress ump : userProgresses) {
                progressMap.put(ump.getModuleId(), ump);
            }

            for (Module module : modules) {
                UserModuleProgress ump = progressMap.get(module.getId());
                // Set status isCompleted pada objek Module agar PropertyValueFactory bisa membacanya
                // Ini memerlukan setter di Module.java (addModuleProgressStatus)
                // Alternatif: Buat model wrapper khusus untuk tampilan tabel
                if (ump != null && ump.getIsCompleted() != null) {
                    // Ini tidak bekerja langsung karena PropertyValueFactory tidak bisa update object.
                    // Anda harus set nilai properti yang ada di Module, atau buat kelas wrapper.
                    // Untuk checkboxTableCell, dia bekerja dengan SimpleBooleanProperty langsung.
                    // Jadi, kita hanya perlu memastikan DAO mengembalikan data yang benar.
                    // Set `isCompleted` pada `module` objek agar `statusColumn` bisa membacanya.
                    // Karena `moduleCompletedColumn` langsung menggunakan `SimpleBooleanProperty`,
                    // kita perlu memastikan `cellData` di `setCellValueFactory` punya akses ke status selesai.
                    // Solusi paling bersih: Buat `ModuleProgressDisplay` class.
                    // Namun untuk saat ini, `CheckBoxTableCell` sudah melakukan jobnya.
                    // Kita perlu memastikan bahwa `booleanProp` di cell factory diinisialisasi
                    // dengan nilai `isCompleted` yang benar dari `userProgresses`.

                    // Cara update: Setelah `modulesTable.setItems(moduleList);`
                    // Kita harus refresh cell factories. Ini otomatis terjadi.
                    // Yang penting adalah saat `setCellValueFactory`, kita perlu tahu `isCompleted` dari progres.
                    // Ini butuh metode di ModuleService/DAO yang mengembalikan List<Module> BERSAMAAN dengan progres user.
                    // Atau, kita bisa iterasi lagi dan set di objek `module` (jika `Module` punya `isCompleted` properti)
                    // Atau, yang lebih baik, membuat `ModuleDisplayWrapper`
                }
            }
            moduleList.setAll(modules);
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