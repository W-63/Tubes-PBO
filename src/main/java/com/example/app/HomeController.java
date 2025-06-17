package com.example.app;

import com.example.app.model.Role;
import com.example.app.model.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;

public class HomeController {

    @FXML
    private Label welcomeLabel;

    private User currentUser;

    /**
     * Menginisialisasi controller dan mengatur tampilan berdasarkan user yang login.
     * Metode ini dipanggil oleh LoginController.
     * @param stage Stage utama aplikasi.
     * @param user Objek User yang berisi data pengguna yang login.
     */
    public void setupHomeView(Stage stage, User user) throws IOException {
        this.currentUser = user; // Tetapkan currentUser di instance HomeController ini
        String fxmlPath;
        String title;

        if (currentUser.getRole() == Role.ADMIN) {
            fxmlPath = "/admin_home.fxml"; // Path FXML untuk Admin Dashboard
            title = "Admin Dashboard - EDULIFE+";
            System.out.println("Memuat halaman Admin untuk: " + currentUser.getUsername());
        } else {
            fxmlPath = "/user_home.fxml"; // Path FXML untuk User Dashboard
            title = "Beranda Pengguna - EDULIFE+";
            System.out.println("Memuat halaman Pengguna untuk: " + currentUser.getUsername());
        }

        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        Parent root = loader.load();

        // Dapatkan controller dari FXML yang baru dimuat, yang merupakan instance HomeController ini
        HomeController loadedController = loader.getController();
        if (loadedController != null) {
            loadedController.setWelcomeLabel(this.currentUser); // Panggil setter untuk label sambutan
        }

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle(title + " (" + currentUser.getUsername() + ")");
        stage.show();
    }

    /**
     * Metode setter untuk welcomeLabel di FXML yang baru dimuat.
     * Dipanggil setelah FXML dimuat untuk memperbarui label.
     * @param user Objek User yang berisi data pengguna yang login.
     */
    public void setWelcomeLabel(User user) {
        this.currentUser = user; // Pastikan currentUser juga disetel di instance controller yang dimuat
        if (welcomeLabel != null && currentUser != null) {
            welcomeLabel.setText("Selamat datang, " + currentUser.getUsername() + "! (Peran: " + currentUser.getRole().name() + ")");
        } else if (welcomeLabel != null) {
            welcomeLabel.setText("Selamat datang!");
        }
    }

    @FXML
    private void handleEcommerce(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ecommerce.fxml")); // Satu FXML untuk Ecommerce
            Parent root = loader.load();

            EcommerceController ecommerceController = loader.getController();
            if (ecommerceController != null && currentUser != null) {
                ecommerceController.initUserData(currentUser); // Teruskan user yang login
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/dailyactivity.fxml")); // Sesuaikan path jika ada di subfolder 'view'
            Parent root = loader.load();

            DailyActivityController activityController = loader.getController();
            if (activityController != null && currentUser != null) {
                 activityController.initUserData(currentUser); // Teruskan user yang login
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
            // Set status isLoggedIn ke false saat logout
            if (LoginController.currentLoggedInUser != null) {
                LoginController.currentLoggedInUser.setLoggedIn(false);
                LoginController.currentLoggedInUser.setLastLoginTime(null); // Atau atur ke waktu logout
            }
            LoginController.currentLoggedInUser = null; // Hapus referensi user yang login
            this.currentUser = null; // Reset juga user di HomeController

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
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/user_management.fxml")); // Path FXML Manajemen Pengguna
                Parent root = loader.load();

                UserManagementController userManController = loader.getController();
                if (userManController != null) {
                    userManController.initUserData(currentUser); // Teruskan user yang login (admin)
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
            if (currentUser != null && currentUser.getRole() == Role.USER) { // Hanya untuk user biasa
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/user_my_courses.fxml"));
                Parent root = loader.load();

                UserMyCoursesController myCoursesController = loader.getController();
                if (myCoursesController != null) {
                    myCoursesController.initUserData(currentUser); // Teruskan user yang login
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