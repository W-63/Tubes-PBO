package com.example.app;

import com.example.app.model.Role;
import com.example.app.model.User;
import com.example.app.service.UserService;

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
import javafx.scene.control.ButtonType; 
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;    
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;   
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional; 
import java.util.List;

public class UserManagementController {

    @FXML private TableView<User> userTable;
    @FXML private TableColumn<User, Integer> idColumn;
    @FXML private TableColumn<User, String> usernameColumn;
    @FXML private TableColumn<User, Role> roleColumn;
    @FXML private TableColumn<User, String> lastLoginColumn; 
    @FXML private TableColumn<User, String> statusColumn;   
    @FXML private TableColumn<User, Void> actionColumn; 
    @FXML private Button backButton;
    @FXML private Button refreshButton;
    private ObservableList<User> userList;
    private UserService userService;
    private User currentUser; 

    @FXML
    public void initialize() {
        userService = new UserService();
        userList = FXCollections.observableArrayList();

        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        roleColumn.setCellValueFactory(new PropertyValueFactory<>("role"));
        lastLoginColumn.setCellValueFactory(cellData -> {
            LocalDateTime lastLogin = cellData.getValue().getLastLoginTime();
            if (lastLogin != null) {
                return new javafx.beans.property.SimpleStringProperty(lastLogin.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")));
            }
            return new javafx.beans.property.SimpleStringProperty("N/A");
        });
        statusColumn.setCellValueFactory(cellData -> {
            boolean isLoggedIn = cellData.getValue().isLoggedIn();
            return new javafx.beans.property.SimpleStringProperty(isLoggedIn ? "Online" : "Offline");
        });
        actionColumn.setCellFactory(param -> new TableCell<>() {
            private final Button deleteBtn = new Button("Hapus");
            private final ComboBox<Role> roleComboBox = new ComboBox<>(FXCollections.observableArrayList(Role.values()));

            {
                deleteBtn.setOnAction(e -> {
                    User userToDelete = getTableView().getItems().get(getIndex());
                    if (LoginController.currentLoggedInUser != null && userToDelete.getId() == LoginController.currentLoggedInUser.getId()) {
                        showAlert("Peringatan", "Tidak bisa menghapus akun Anda sendiri!", Alert.AlertType.WARNING);
                        return;
                    }
                    if (confirmAction("Konfirmasi Hapus", "Anda yakin ingin menghapus pengguna " + userToDelete.getUsername() + "?")) {
                        try {
                            if (userService.deleteUser(userToDelete.getId())) {
                                showAlert("Sukses", "Pengguna berhasil dihapus.", Alert.AlertType.INFORMATION);
                                loadUsers();
                            } else {
                                showAlert("Gagal", "Gagal menghapus pengguna.", Alert.AlertType.ERROR);
                            }
                        } catch (SQLException ex) {
                            showAlert("Kesalahan Database", "Gagal menghapus: " + ex.getMessage(), Alert.AlertType.ERROR);
                            ex.printStackTrace();
                        }
                    }
                });

                roleComboBox.setOnAction(e -> {
                    User userToUpdate = getTableView().getItems().get(getIndex());
                    Role newRole = roleComboBox.getSelectionModel().getSelectedItem();
                    if (LoginController.currentLoggedInUser != null && userToUpdate.getId() == LoginController.currentLoggedInUser.getId()) {
                        showAlert("Peringatan", "Tidak bisa mengubah peran akun Anda sendiri!", Alert.AlertType.WARNING);
                        roleComboBox.getSelectionModel().select(userToUpdate.getRole());
                        return;
                    }

                    if (userToUpdate.getRole() != newRole) { 
                        if (confirmAction("Konfirmasi Ubah Peran", "Anda yakin ingin mengubah peran " + userToUpdate.getUsername() + " menjadi " + newRole.name() + "?")) {
                            try {
                                if (userService.updateUserRole(userToUpdate.getId(), newRole)) {
                                    showAlert("Sukses", "Peran pengguna berhasil diubah.", Alert.AlertType.INFORMATION);
                                    loadUsers(); 
                                } else {
                                    showAlert("Gagal", "Gagal mengubah peran pengguna.", Alert.AlertType.ERROR);
                                }
                            } catch (SQLException ex) {
                                showAlert("Kesalahan Database", "Gagal mengubah peran: " + ex.getMessage(), Alert.AlertType.ERROR);
                                ex.printStackTrace();
                            }
                        } else {
                            roleComboBox.getSelectionModel().select(userToUpdate.getRole());
                        }
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    User user = getTableView().getItems().get(getIndex());
                    roleComboBox.getSelectionModel().select(user.getRole()); 
                    setGraphic(new HBox(5, deleteBtn, roleComboBox));
                    if (LoginController.currentLoggedInUser != null && user.getId() == LoginController.currentLoggedInUser.getId()) {
                        deleteBtn.setDisable(true); 
                        roleComboBox.setDisable(true); 
                    } else {
                        deleteBtn.setDisable(false);
                        roleComboBox.setDisable(false);
                    }
                }
            }
        });

        userTable.setItems(userList);
        loadUsers(); 
    }

    public void initUserData(User adminUser) {
        this.currentUser = adminUser;
        
    }

    private void loadUsers() {
        try {
            List<User> users = userService.getAllUsers();
            
            if (LoginController.currentLoggedInUser != null) {
                for (User user : users) {
                    if (user.getId() == LoginController.currentLoggedInUser.getId()) {
                        user.setLoggedIn(LoginController.currentLoggedInUser.isLoggedIn());
                        user.setLastLoginTime(LoginController.currentLoggedInUser.getLastLoginTime());
                    } else {
                        user.setLoggedIn(false); 
                    }
                }
            }
            userList.setAll(users);
        } catch (SQLException e) {
            showAlert("Kesalahan Database", "Gagal memuat pengguna: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    @FXML
    private void handleRefresh() {
        loadUsers();
        showAlert("Pembaruan", "Daftar pengguna diperbarui.", Alert.AlertType.INFORMATION);
    }

    @FXML
    private void handleBack(ActionEvent event) {
        try {
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            HomeController homeController = new HomeController();
            homeController.setupHomeView(currentStage, LoginController.currentLoggedInUser);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Kesalahan Navigasi", "Gagal memuat halaman Beranda: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private boolean confirmAction(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }
}