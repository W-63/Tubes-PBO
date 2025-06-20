// Ini adalah versi HomeController.java yang HARUS ANDA GUNAKAN
package com.example.app;

import com.example.app.model.Role;
import com.example.app.model.User;
import com.example.app.service.EnrollmentService;
import com.example.app.service.ToDoService;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.chart.PieChart;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;

public class HomeController {

    @FXML private Label welcomeLabel;

    // FXML elements untuk Dashboard
    @FXML private Label welcomeDashboardLabel;
    @FXML private Label totalCoursesLabel;
    @FXML private Label completedCoursesLabel;
    @FXML private Label inProgressCoursesLabel;
    @FXML private Label totalActivitiesLabel;
    @FXML private Label completedActivitiesLabel;
    @FXML private Label pendingActivitiesLabel;
    @FXML private PieChart courseProgressChart;
    @FXML private PieChart activityProgressChart;

    private User currentUser;
    private EnrollmentService enrollmentService;
    private ToDoService toDoService;

    // Konstruktor, inisialisasi services di sini
    public HomeController() {
        this.enrollmentService = new EnrollmentService();
        this.toDoService = new ToDoService();
    }

    public void setupHomeView(Stage stage, User user) throws IOException {
        this.currentUser = user;
        String fxmlPath;
        String title;

        if (currentUser.getRole() == Role.ADMIN) {
            fxmlPath = "/admin_home.fxml";
            title = "Admin Dashboard - EDULIFE+";
            System.out.println("Memuat halaman Admin untuk: " + currentUser.getUsername());
        } else {
            fxmlPath = "/user_home.fxml";
            title = "Beranda Pengguna - EDULIFE+";
            System.out.println("Memuat halaman Pengguna untuk: " + currentUser.getUsername());
        }

        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        Parent root = loader.load();

        HomeController loadedController = loader.getController();
        if (loadedController != null) {
            loadedController.setWelcomeLabel(this.currentUser);
            
            loadedController.loadDashboardData(); //  memastikan dashboard diperbarui
        }

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle(title + " (" + currentUser.getUsername() + ")");
        stage.show();
    }

    public void setWelcomeLabel(User user) {
        this.currentUser = user;
        if (welcomeLabel != null && currentUser != null) {
            welcomeLabel.setText("Selamat datang, " + currentUser.getUsername() + "! (Peran: " + currentUser.getRole().name() + ")");
        } else if (welcomeLabel != null) {
            welcomeLabel.setText("Selamat datang!");
        }
        if (welcomeDashboardLabel != null) {
            welcomeDashboardLabel.setText("Ringkasan Progres Pengguna");
        }
    }

    private void loadDashboardData() {
        if (currentUser == null) {
            System.err.println("Dashboard data cannot be loaded: currentUser is null.");
            return;
        }

        try {
            // Data Progres Kelas
            Map<String, Integer> courseSummary = enrollmentService.getCourseProgressSummary(currentUser.getId());
            int completedCourses = courseSummary.getOrDefault("Selesai", 0);
            int inProgressCourses = courseSummary.getOrDefault("Berjalan", 0);
            int totalCourses = completedCourses + inProgressCourses;

            if (totalCoursesLabel != null) totalCoursesLabel.setText("Total Kelas: " + totalCourses);
            if (completedCoursesLabel != null) completedCoursesLabel.setText("Selesai: " + completedCourses);
            if (inProgressCoursesLabel != null) inProgressCoursesLabel.setText("Berjalan: " + inProgressCourses);

            if (courseProgressChart != null) {
                ObservableList<PieChart.Data> courseChartData = FXCollections.observableArrayList(
                    new PieChart.Data("Selesai (" + completedCourses + ")", completedCourses),
                    new PieChart.Data("Berjalan (" + inProgressCourses + ")", inProgressCourses)
                );
                courseProgressChart.setData(courseChartData);
                courseProgressChart.setTitle("Progres Kelas");
                courseProgressChart.setLabelsVisible(true);
                courseProgressChart.setLegendVisible(true);
            }

            // Data Progres Aktivitas
            Map<String, Integer> activitySummary = toDoService.getToDoProgressSummary(currentUser.getId());
            int completedActivities = activitySummary.getOrDefault("Selesai", 0);
            int pendingActivities = activitySummary.getOrDefault("Belum Selesai", 0);
            int totalActivities = completedActivities + pendingActivities;

            if (totalActivitiesLabel != null) totalActivitiesLabel.setText("Total Aktivitas: " + totalActivities);
            if (completedActivitiesLabel != null) completedActivitiesLabel.setText("Selesai: " + completedActivities);
            if (pendingActivitiesLabel != null) pendingActivitiesLabel.setText("Belum Selesai: " + pendingActivities);

            if (activityProgressChart != null) {
                ObservableList<PieChart.Data> activityChartData = FXCollections.observableArrayList(
                    new PieChart.Data("Selesai (" + completedActivities + ")", completedActivities),
                    new PieChart.Data("Belum Selesai (" + pendingActivities + ")", pendingActivities)
                );
                activityProgressChart.setData(activityChartData);
                activityProgressChart.setTitle("Progres Aktivitas Harian");
                activityProgressChart.setLabelsVisible(true);
                activityProgressChart.setLegendVisible(true);
            }

        } catch (SQLException e) {
            showAlert("Kesalahan Database", "Gagal memuat data dashboard: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        } catch (Exception e) {
            showAlert("Kesalahan", "Terjadi kesalahan tak terduga saat memuat dashboard: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    @FXML
    private void handleEcommerce(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ecommerce.fxml"));
            Parent root = loader.load();

            EcommerceController ecommerceController = loader.getController();
            if (ecommerceController != null && currentUser != null) {
                ecommerceController.initUserData(currentUser);
            }

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Katalog Kelas - EDULIFE+");
            stage.show();
        } catch (IOException e) {
            showError("Gagal membuka halaman E-Commerce.", e);
        } catch (Exception e) {
            showError("Terjadi kesalahan tak terduga.", e);
        }
    }

    @FXML
    private void handleDailyActivity(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/dailyactivity.fxml"));
            Parent root = loader.load();

            DailyActivityController activityController = loader.getController();
            if (activityController != null && currentUser != null) {
                 activityController.initUserData(currentUser);
            }

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Daily Activity - EDULIFE+");
            stage.show();
        } catch (IOException e) {
            showError("Gagal membuka halaman Daily Activity.", e);
        } catch (Exception e) {
            showError("Terjadi kesalahan tak terduga.", e);
        }
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        try {
            if (LoginController.currentLoggedInUser != null) {
                LoginController.currentLoggedInUser.setLoggedIn(false);
                LoginController.currentLoggedInUser.setLastLoginTime(null);
            }
            LoginController.currentLoggedInUser = null;
            this.currentUser = null;

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/login.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Login - EDULIFE+");
            stage.show();
        } catch (IOException e) {
            showError("Gagal logout dan kembali ke halaman login.", e);
        } catch (Exception e) {
            showError("Terjadi kesalahan tak terduga saat logout.", e);
        }
    }

    @FXML
    private void handleUserManagement(ActionEvent event) {
        try {
            if (currentUser != null && currentUser.getRole() == Role.ADMIN) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/user_management.fxml"));
                Parent root = loader.load();

                UserManagementController userManController = loader.getController();
                if (userManController != null) {
                    userManController.initUserData(currentUser);
                }

                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.setTitle("Manajemen Pengguna - EDULIFE+");
                stage.show();
            } else {
                showAlert("Akses Ditolak", "Anda tidak memiliki izin untuk mengakses fitur ini.", Alert.AlertType.WARNING);
            }
        } catch (IOException e) {
            showError("Gagal membuka halaman Manajemen Pengguna.", e);
        } catch (Exception e) {
            showError("Terjadi kesalahan tak terduga.", e);
        }
    }

    @FXML
    private void handleMyCourses(ActionEvent event) {
        try {
            if (currentUser != null && currentUser.getRole() == Role.USER) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/user_my_courses.fxml"));
                Parent root = loader.load();

                UserMyCoursesController myCoursesController = loader.getController();
                if (myCoursesController != null) {
                    myCoursesController.initUserData(currentUser);
                }

                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.setTitle("Kelas Saya - EDULIFE+");
                stage.show();
            } else {
                showAlert("Akses Ditolak", "Fitur ini hanya untuk pengguna biasa.", Alert.AlertType.WARNING);
            }
        } catch (IOException e) {
            showError("Gagal membuka halaman Kelas Saya.", e);
        } catch (Exception e) {
            showError("Terjadi kesalahan tak terduga.", e);
        }
    }

    @FXML
    private void handleModuleManagement(ActionEvent event) {
        try {
            if (currentUser != null && currentUser.getRole() == Role.ADMIN) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/module_management_view.fxml"));
                Parent root = loader.load();

                ModuleManagementController moduleManController = loader.getController();
                if (moduleManController != null) {
                    moduleManController.initUserData(currentUser); // Teruskan admin yang login
                }

                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.setTitle("Manajemen Modul - EDULIFE+");
                stage.show();
            } else {
                showAlert("Akses Ditolak", "Anda tidak memiliki izin untuk mengakses fitur ini.", Alert.AlertType.WARNING);
            }
        } catch (IOException e) {
            showError("Gagal membuka halaman Manajemen Modul.", e);
        } catch (Exception e) {
            showError("Terjadi kesalahan tak terduga.", e);
        }
    }

    private void showError(String message, Exception e) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Kesalahan");
        alert.setHeaderText(null);
        String content = message;
        if (e != null) {
            content += "\nDetail: " + e.getMessage();
            e.printStackTrace();
        }
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}