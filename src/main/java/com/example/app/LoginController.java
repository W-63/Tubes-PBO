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
import java.time.LocalDateTime; 

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;

    private final UserService userService;

    public static User currentLoggedInUser = null; 

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
                user.setLoggedIn(true);
                user.setLastLoginTime(LocalDateTime.now());
                currentLoggedInUser = user; 

                showAlert(Alert.AlertType.INFORMATION, "Login Berhasil", "Selamat datang, " + user.getUsername() + "! Peran Anda: " + user.getRole().name());

                Stage stage = (Stage) usernameField.getScene().getWindow();

                
                HomeController homeController = new HomeController(); 
                homeController.setupHomeView(stage, currentLoggedInUser); 

            } else {
                showAlert(Alert.AlertType.ERROR, "Login Gagal", "Username atau password salah.");
                currentLoggedInUser = null; 
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Kesalahan Database", "Koneksi gagal: " + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
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
            stage.show(); 
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