package com.example.app;

import com.example.app.model.Role;
import com.example.app.service.UserService; // Ditambahkan
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

public class RegisterController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    private final UserService userService; // Instance UserService

    public RegisterController() {
        this.userService = new UserService(); // Inisialisasi UserService
    }

    @FXML
    private void handleRegister(ActionEvent event) throws IOException {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Input Tidak Lengkap", "Username dan password harus diisi.");
            return;
        }

        try {
            if (userService.isUsernameTaken(username)) {
                showAlert(Alert.AlertType.ERROR, "Registrasi Gagal", "Username '" + username + "' sudah digunakan!");
                return;
            }

            Role determinedRole = userService.determineInitialRole();
            boolean registrationSuccess = userService.registerUser(username, password, determinedRole);

            if (registrationSuccess) {
                showAlert(Alert.AlertType.INFORMATION, "Registrasi Berhasil", "Akun berhasil dibuat sebagai " + determinedRole.name().toLowerCase() + ". Silakan login.");
                goToLogin(event);
            } else {
                showAlert(Alert.AlertType.ERROR, "Registrasi Gagal", "Tidak dapat membuat akun saat ini. Coba lagi.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Kesalahan Database", "Terjadi kesalahan internal: " + e.getMessage());
        }
    }

    @FXML
    private void goToLogin(ActionEvent event) throws IOException {
        // Pastikan path ke login.fxml benar.
        // Jika login.fxml ada di root 'resources', path-nya "/login.fxml".
        // Jika ada di subfolder 'view', path-nya "/view/login.fxml".
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/login.fxml")); // Sesuaikan jika perlu
        Parent loginPage = loader.load();
        Scene loginScene = new Scene(loginPage);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(loginScene);
        stage.setTitle("Login - EDULIFE+");
        stage.show();
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
