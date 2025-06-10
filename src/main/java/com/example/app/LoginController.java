package com.example.app;

import com.example.app.model.User;
import com.example.app.service.UserService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.io.IOException;

public class LoginController {

    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;

    private final UserService userService;

    public static User currentLoggedInUser = null; // Menyimpan user yang sedang login

    public LoginController() {
        this.userService = new UserService();
    }

    @FXML
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Input Kosong", "Harap isi semua field.");
            return;
        }

        try {
            User user = userService.loginUser(username, password);

            if (user != null) {
                currentLoggedInUser = user; // Simpan info user yang login

                showAlert(Alert.AlertType.INFORMATION, "Login Berhasil", "Selamat datang, " + user.getUsername() + "! Peran Anda: " + user.getRole().name());

                Stage stage = (Stage) usernameField.getScene().getWindow();

                // Panggil setupHomeView di HomeController untuk memuat FXML yang sesuai peran
                HomeController homeController = new HomeController(); // Buat instance HomeController
                homeController.setupHomeView(stage, currentLoggedInUser); // Panggil metode setupHomeView

            } else {
                showAlert(Alert.AlertType.ERROR, "Login Gagal", "Username atau password salah.");
                currentLoggedInUser = null; // Reset jika gagal
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Kesalahan Database", "Koneksi gagal: " + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) { // Tangkap IOException untuk FXMLLoader
            showAlert(Alert.AlertType.ERROR, "Kesalahan Aplikasi", "Gagal memuat halaman Home: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Kesalahan Aplikasi", "Terjadi kesalahan tak terduga saat login: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleRegister() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/register.fxml"));
            Parent registerRoot = loader.load();
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(new Scene(registerRoot));
            stage.setTitle("Registrasi - EDULIFE+");
            stage.show(); // Penting: tambahkan .show()
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Kesalahan Aplikasi", "Gagal membuka halaman registrasi: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}