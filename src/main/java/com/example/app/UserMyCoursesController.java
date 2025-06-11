package com.example.app;

import com.example.app.model.Course;
import com.example.app.model.Enrollment;
import com.example.app.model.User;
import com.example.app.model.Role; 
import com.example.app.service.EnrollmentService;
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
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.scene.control.ProgressBar; 
import javafx.scene.layout.HBox; 
import javafx.scene.control.Label; 
import java.io.IOException;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;

public class UserMyCoursesController {

    @FXML private TableView<Enrollment> myCoursesTable;
    @FXML private TableColumn<Enrollment, String> courseTitleColumn;
    @FXML private TableColumn<Enrollment, String> enrollmentDateColumn;
    @FXML private TableColumn<Enrollment, Number> progressColumn; 
    @FXML private TableColumn<Enrollment, String> statusColumn; 
    @FXML private TableColumn<Enrollment, Void> actionColumn; 
    @FXML private Button backButton;
    @FXML private Button refreshButton;
    private ObservableList<Enrollment> myEnrollmentList;
    private EnrollmentService enrollmentService;
    private User currentUser;

    @FXML
    public void initialize() {
        enrollmentService = new EnrollmentService();
        myEnrollmentList = FXCollections.observableArrayList();

        courseTitleColumn.setCellValueFactory(new PropertyValueFactory<>("courseTitle"));
        enrollmentDateColumn.setCellValueFactory(cellData -> {
            return new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getEnrollmentDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
            );
        });

        progressColumn.setCellValueFactory(new PropertyValueFactory<>("progressPercentage"));
        progressColumn.setCellFactory(column -> new TableCell<Enrollment, Number>() {
            private final ProgressBar progressBar = new ProgressBar();
            private final Label progressLabel = new Label();
            private final HBox container = new HBox(5, progressBar, progressLabel);

            @Override
            protected void updateItem(Number progress, boolean empty) {
                super.updateItem(progress, empty);
                if (empty || progress == null) {
                    setGraphic(null);
                } else {
                    progressBar.setProgress(progress.doubleValue() / 100.0);
                    progressLabel.setText(String.format("%.0f%%", progress.doubleValue()));
                    setGraphic(container);
                }
            }
        });


        statusColumn.setCellValueFactory(cellData -> {
            Boolean isCompleted = cellData.getValue().getIsCompleted();
            return new javafx.beans.property.SimpleStringProperty(isCompleted ? "Selesai" : "Berjalan");
        });

        actionColumn.setCellFactory(param -> new TableCell<>() {
            private final Button continueBtn = new Button("Lanjutkan Belajar");
            private final Button completeBtn = new Button("Tandai Selesai"); 
            private final Button certificateBtn = new Button("Lihat Sertifikat"); 
            {
                continueBtn.setOnAction(e -> {
                    Enrollment enrollment = getTableView().getItems().get(getIndex());
            
                    int currentProgress = enrollment.getProgressPercentage();
                    int newProgress = currentProgress + 10; 
                    if (newProgress > 100) newProgress = 100;

                    try {
                        enrollmentService.updateProgress(enrollment.getId(), newProgress); 
                        showAlert("Progres", "Progres kelas " + enrollment.getCourseTitle() + " diperbarui menjadi " + newProgress + "%.", Alert.AlertType.INFORMATION);
                        loadMyEnrollments(); 
                    } catch (Exception ex) {
                        showAlert("Gagal", "Gagal memperbarui progres: " + ex.getMessage(), Alert.AlertType.ERROR);
                    }
                });

                completeBtn.setOnAction(e -> {
                    Enrollment enrollment = getTableView().getItems().get(getIndex());
                    if (!enrollment.getIsCompleted()) {
                        try {
                            enrollmentService.updateProgress(enrollment.getId(), 100); 
                            showAlert("Sukses", "Kelas " + enrollment.getCourseTitle() + " ditandai selesai!", Alert.AlertType.INFORMATION);
                            loadMyEnrollments();
                        } catch (Exception ex) {
                            showAlert("Gagal", "Gagal menandai selesai: " + ex.getMessage(), Alert.AlertType.ERROR);
                        }
                    }
                });
                
                certificateBtn.setOnAction(e -> {
                    Enrollment enrollment = getTableView().getItems().get(getIndex());
                    if (enrollment.getIsCompleted()) {
                        showAlert("Sertifikat", "Selamat! Anda telah menyelesaikan kelas " + enrollment.getCourseTitle() + ".\nSertifikat Anda telah dibuat (simulasi).", Alert.AlertType.INFORMATION);
                    } else {
                        showAlert("Peringatan", "Kelas belum selesai untuk mendapatkan sertifikat.", Alert.AlertType.WARNING);
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Enrollment enrollment = getTableView().getItems().get(getIndex());
                    if (enrollment.getIsCompleted()) {
                        setGraphic(certificateBtn);
                    } else {
                        setGraphic(new HBox(5, continueBtn, completeBtn)); 
                    }
                }
            }
        });

        myCoursesTable.setItems(myEnrollmentList);
    }

    /**
     * Dipanggil dari HomeController untuk mengatur user yang login.
     * @param user User yang sedang login.
     */
    public void initUserData(User user) {
        this.currentUser = user;
        if (this.currentUser != null) {
            loadMyEnrollments();
        } else {
            showAlert("Error", "Data pengguna tidak ditemukan. Harap login kembali.", Alert.AlertType.ERROR);
        }
    }

    private void loadMyEnrollments() {
        if (currentUser == null) return;
        try {
            myEnrollmentList.setAll(enrollmentService.getUserEnrollments(currentUser.getId()));
        } catch (SQLException e) {
            showAlert("Kesalahan", "Gagal memuat kelas saya: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    @FXML
    private void handleRefresh() {
        loadMyEnrollments();
        showAlert("Pembaruan", "Daftar kelas diperbarui.", Alert.AlertType.INFORMATION);
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

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.setHeaderText(null);
        alert.showAndWait();
    }
}