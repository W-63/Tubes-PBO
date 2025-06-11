package com.example.app;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextField;
import javafx.scene.control.TextArea; 
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import com.example.app.model.Course;
import com.example.app.model.User;
import com.example.app.model.Role;
import com.example.app.service.CourseService;
import com.example.app.service.EnrollmentService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.io.IOException;
import java.sql.SQLException;

public class EcommerceController {

    @FXML private TextField titleField;
    @FXML private TextArea descriptionField; 
    @FXML private TextField priceField;
    @FXML private TextField durationField;
    @FXML private TextField modulesField;
    @FXML private VBox formInputCourse;
    @FXML private Button addCourseButton;
    @FXML private TableView<Course> courseTable;
    @FXML private TableColumn<Course, String> titleColumn;
    @FXML private TableColumn<Course, String> descriptionColumn;
    @FXML private TableColumn<Course, Integer> priceColumn;
    @FXML private TableColumn<Course, Integer> durationColumn;
    @FXML private TableColumn<Course, Void> actionColumn;

    private ObservableList<Course> courseList;
    private CourseService courseService;
    private EnrollmentService enrollmentService;
    private User currentUser; 

    @FXML
    public void initialize() {
        courseService = new CourseService();
        enrollmentService = new EnrollmentService();
        courseList = FXCollections.observableArrayList();
        this.currentUser = LoginController.currentLoggedInUser;
        try {
            courseService.initializeDummyCourses();
        } catch (SQLException e) {
            showAlert("Kesalahan Database", "Gagal menginisialisasi dummy courses: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }

        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        durationColumn.setCellValueFactory(new PropertyValueFactory<>("durationWeeks"));
        actionColumn.setCellFactory(param -> new TableCell<>() {
            private final Button deleteBtn = new Button("Hapus");
            private final Button enrollBtn = new Button("Enroll");
            private final Label enrolledLabel = new Label("Terdaftar");

            {
                deleteBtn.setOnAction(e -> {
                    Course course = getTableView().getItems().get(getIndex());
                    try {
                        courseService.deleteCourse(course.getId());
                        loadCourses();
                        showAlert("Sukses", "Kelas berhasil dihapus!", Alert.AlertType.INFORMATION);
                    } catch (SQLException ex) {
                        showAlert("Gagal", "Gagal menghapus kelas: " + ex.getMessage(), Alert.AlertType.ERROR);
                        ex.printStackTrace();
                    }
                });

                enrollBtn.setOnAction(e -> {
                    Course course = getTableView().getItems().get(getIndex());
                    if (currentUser == null) { 
                         currentUser = LoginController.currentLoggedInUser;
                    }
                    if (currentUser == null) {
                        showAlert("Peringatan", "Anda harus login untuk mendaftar kelas.", Alert.AlertType.WARNING);
                        return;
                    }
                    try {
                        enrollmentService.enrollUserInCourse(currentUser.getId(), course.getId());
                        showAlert("Sukses", "Berhasil mendaftar ke kelas " + course.getTitle() + "!", Alert.AlertType.INFORMATION);
                        loadCourses(); 
                    } catch (Exception ex) {
                        showAlert("Gagal", "Gagal mendaftar kelas: " + ex.getMessage(), Alert.AlertType.ERROR);
                        ex.printStackTrace();
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    User activeUser = LoginController.currentLoggedInUser; 
                    if (activeUser != null && activeUser.getRole() == Role.ADMIN) {
                        setGraphic(deleteBtn);
                        deleteBtn.setVisible(true);
                        deleteBtn.setManaged(true);
                        enrollBtn.setVisible(false);
                        enrollBtn.setManaged(false);
                        enrolledLabel.setVisible(false); 
                        enrolledLabel.setManaged(false);
                    } else { 
                        Course course = getTableView().getItems().get(getIndex());
                        boolean isEnrolled = false;
                        if (activeUser != null) {
                            try {
                                isEnrolled = enrollmentService.isUserEnrolled(activeUser.getId(), course.getId());
                            } catch (SQLException e) {
                                System.err.println("Error checking enrollment status: " + e.getMessage());
                                e.printStackTrace();
                            }
                        }

                        if (isEnrolled) {
                            setGraphic(enrolledLabel); 
                            enrollBtn.setVisible(false);
                            enrollBtn.setManaged(false);
                            deleteBtn.setVisible(false);
                            deleteBtn.setManaged(false);
                            enrolledLabel.setVisible(true);
                            enrolledLabel.setManaged(true);
                        } else {
                            setGraphic(enrollBtn);
                            enrollBtn.setVisible(true);
                            enrollBtn.setManaged(true);
                            deleteBtn.setVisible(false);
                            deleteBtn.setManaged(false);
                            enrolledLabel.setVisible(false);
                            enrolledLabel.setManaged(false);
                        }
                    }
                }
            }
        });

        courseTable.setItems(courseList);
        loadCourses();
    }

    /**
     * Metode ini dipanggil dari HomeController untuk mengatur user yang login.
     * Namun, untuk memastikan konsistensi, kita juga mengambil dari LoginController.currentLoggedInUser
     * @param user Objek User yang berisi data pengguna yang login.
     */
    public void initUserData(User user) {
        this.currentUser = user; 
        if (this.currentUser != null) {
            System.out.println("EcommerceController: Diterima user " + currentUser.getUsername() + " dengan peran " + currentUser.getRole());
            if (currentUser.getRole() == Role.ADMIN) {
                if (formInputCourse != null) {
                    formInputCourse.setVisible(true);
                    formInputCourse.setManaged(true);
                }
                actionColumn.setVisible(true);
            } else {
                if (formInputCourse != null) {
                    formInputCourse.setVisible(false);
                    formInputCourse.setManaged(false);
                }
                actionColumn.setVisible(true);
            }
            courseTable.refresh(); 
        }
    }

    @FXML
    private void addCourse() {
        String title = titleField.getText().trim();
        String description = descriptionField.getText().trim();
        String priceStr = priceField.getText().trim();
        String durationStr = durationField.getText().trim();
        String modulesStr = modulesField.getText().trim();

        if (title.isEmpty() || description.isEmpty() || priceStr.isEmpty() || durationStr.isEmpty() || modulesStr.isEmpty()) {
            showAlert("Peringatan", "Semua field harus diisi!", Alert.AlertType.WARNING);
            return;
        }

        try {
            int price = Integer.parseInt(priceStr);
            int duration = Integer.parseInt(durationStr);
            int modules = Integer.parseInt(modulesStr);
            Course course = new Course(title, description, price, duration, modules);
            courseService.addCourse(course);
            showAlert("Sukses", "Kelas berhasil ditambahkan!", Alert.AlertType.INFORMATION);
            clearForm();
            loadCourses();
        } catch (NumberFormatException e) {
            showAlert("Format Salah", "Harga, Durasi, dan Modul harus berupa angka!", Alert.AlertType.WARNING);
            e.printStackTrace();
        } catch (Exception e) {
            showAlert("Gagal", "Gagal menyimpan kelas: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    private void loadCourses() {
        try {
            courseList.setAll(courseService.getAllCourses());
            courseTable.refresh();
        } catch (SQLException e) {
            showAlert("Gagal", "Gagal memuat data kelas: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    private void clearForm() {
        titleField.clear();
        descriptionField.clear();
        priceField.clear();
        durationField.clear();
        modulesField.clear();
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.setHeaderText(null);
        alert.showAndWait();
    }

    @FXML
    private void handleBack(ActionEvent event) {
        try {
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            String fxmlPath;
            String title;
            User userToNavigate = LoginController.currentLoggedInUser;

            if (userToNavigate != null && userToNavigate.getRole() == Role.ADMIN) {
                fxmlPath = "/admin_home.fxml";
                title = "Admin Dashboard - EDULIFE+";
            } else {
                fxmlPath = "/user_home.fxml";
                title = "Beranda Pengguna - EDULIFE+";
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent homeRoot = loader.load();
            HomeController homeController = loader.getController();
            if (homeController != null && userToNavigate != null) {
                homeController.setWelcomeLabel(userToNavigate); 
            }

            Scene homeScene = new Scene(homeRoot);
            currentStage.setScene(homeScene);
            currentStage.setTitle(title + " (" + (userToNavigate != null ? userToNavigate.getUsername() : "Guest") + ")");
            currentStage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Kesalahan Navigasi", "Gagal memuat halaman Beranda: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
}