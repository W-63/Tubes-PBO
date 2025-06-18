package com.example.app;

import com.example.app.dao.CourseDAO; // Untuk mendapatkan daftar kelas
import com.example.app.model.Course;
import com.example.app.model.Module;
import com.example.app.model.User;
import com.example.app.model.Role;
import com.example.app.service.ModuleService;

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
import javafx.scene.control.TextField;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional; // Ditambahkan

public class ModuleManagementController {

    @FXML private ComboBox<Course> courseComboBox; // Untuk memilih kelas
    @FXML private TextField moduleNumberField;
    @FXML private TextField moduleTitleField;
    @FXML private TextArea moduleDescriptionField;
    @FXML private TextField moduleContentUrlField;
    @FXML private TextField moduleTypeField;
    @FXML private TextField moduleDurationField;
    @FXML private Button addModuleButton;

    @FXML private TableView<Module> modulesTable;
    @FXML private TableColumn<Module, Integer> idColumn; // Untuk ID modul
    @FXML private TableColumn<Module, Integer> moduleNumberColumn;
    @FXML private TableColumn<Module, String> moduleTitleColumn;
    @FXML private TableColumn<Module, String> moduleTypeColumn;
    @FXML private TableColumn<Module, Void> actionColumn; // Untuk delete module

    @FXML private Button backButton;

    private ModuleService moduleService;
    private CourseDAO courseDAO; // Diperlukan untuk mengisi ComboBox kelas
    private ObservableList<Module> moduleList;
    private ObservableList<Course> courseList; // Untuk ComboBox kelas

    private User currentUser; // Admin yang sedang login

    @FXML
    public void initialize() {
        moduleService = new ModuleService();
        courseDAO = new CourseDAO(); // Inisialisasi CourseDAO
        moduleList = FXCollections.observableArrayList();
        courseList = FXCollections.observableArrayList();

        // Inisialisasi ComboBox kelas
        courseComboBox.setItems(courseList);
        courseComboBox.setConverter(new javafx.util.StringConverter<Course>() {
            @Override
            public String toString(Course course) {
                return course != null ? course.getTitle() : "";
            }
            @Override
            public Course fromString(String string) {
                // Tidak digunakan untuk ComboBox ini
                return null;
            }
        });

        // Listener saat kelas dipilih dari ComboBox
        courseComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                loadModulesByCourse(newVal.getId());
            } else {
                moduleList.clear(); // Bersihkan tabel jika tidak ada kelas dipilih
            }
        });

        // Inisialisasi kolom tabel modul
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        moduleNumberColumn.setCellValueFactory(new PropertyValueFactory<>("moduleNumber"));
        moduleTitleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        moduleTypeColumn.setCellValueFactory(new PropertyValueFactory<>("moduleType"));

        actionColumn.setCellFactory(param -> new TableCell<>() {
            private final Button deleteBtn = new Button("Hapus");

            {
                deleteBtn.setOnAction(e -> {
                    Module moduleToDelete = getTableView().getItems().get(getIndex());
                    if (confirmAction("Konfirmasi Hapus Modul", "Yakin ingin menghapus modul '" + moduleToDelete.getTitle() + "'?")) {
                        try {
                            moduleService.deleteModule(moduleToDelete.getId());
                            // Setelah menghapus, muat ulang daftar modul untuk kelas yang sama
                            if (courseComboBox.getSelectionModel().getSelectedItem() != null) {
                                loadModulesByCourse(courseComboBox.getSelectionModel().getSelectedItem().getId());
                            }
                            showAlert("Sukses", "Modul berhasil dihapus!", Alert.AlertType.INFORMATION);
                        } catch (SQLException ex) {
                            showAlert("Gagal", "Gagal menghapus modul: " + ex.getMessage(), Alert.AlertType.ERROR);
                            ex.printStackTrace();
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
                    setGraphic(deleteBtn);
                }
            }
        });

        modulesTable.setItems(moduleList);

        loadCoursesForComboBox(); // Muat daftar kelas saat inisialisasi
    }

    public void initUserData(User adminUser) {
        this.currentUser = adminUser;
        // Tidak banyak yang perlu dilakukan di sini selain menyimpan currentUser.
        // Data akan dimuat saat initialize()
    }

    private void loadCoursesForComboBox() {
        try {
            courseList.setAll(courseDAO.getAllCourses());
        } catch (SQLException e) {
            showAlert("Kesalahan Database", "Gagal memuat daftar kelas: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    private void loadModulesByCourse(Integer courseId) {
        try {
            moduleList.setAll(moduleService.getModulesByCourseId(courseId));
        } catch (SQLException e) {
            showAlert("Kesalahan Database", "Gagal memuat modul untuk kelas ini: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAddModule() {
        Course selectedCourse = courseComboBox.getSelectionModel().getSelectedItem();
        if (selectedCourse == null) {
            showAlert("Peringatan", "Pilih kelas terlebih dahulu.", Alert.AlertType.WARNING);
            return;
        }

        String moduleNumberStr = moduleNumberField.getText().trim();
        String moduleTitle = moduleTitleField.getText().trim();
        String moduleDescription = moduleDescriptionField.getText().trim();
        String moduleContentUrl = moduleContentUrlField.getText().trim();
        String moduleType = moduleTypeField.getText().trim();
        String moduleDurationStr = moduleDurationField.getText().trim();

        if (moduleNumberStr.isEmpty() || moduleTitle.isEmpty() || moduleType.isEmpty()) {
            showAlert("Peringatan", "Nomor Modul, Judul, dan Tipe harus diisi.", Alert.AlertType.WARNING);
            return;
        }

        try {
            Integer moduleNumber = Integer.parseInt(moduleNumberStr);
            Integer durationMinutes = moduleDurationStr.isEmpty() ? 0 : Integer.parseInt(moduleDurationStr);

            Module newModule = new Module(
                selectedCourse.getId(),
                moduleNumber,
                moduleTitle,
                moduleDescription.isEmpty() ? null : moduleDescription,
                moduleContentUrl.isEmpty() ? null : moduleContentUrl,
                moduleType,
                durationMinutes
            );

            moduleService.addModule(newModule);
            showAlert("Sukses", "Modul berhasil ditambahkan!", Alert.AlertType.INFORMATION);
            clearModuleForm();
            loadModulesByCourse(selectedCourse.getId()); // Muat ulang modul setelah ditambah
        } catch (NumberFormatException e) {
            showAlert("Format Salah", "Nomor Modul dan Durasi harus berupa angka.", Alert.AlertType.WARNING);
            e.printStackTrace();
        } catch (Exception e) { // Menangkap Exception dari validateModule di ModuleService
            showAlert("Gagal", "Gagal menambahkan modul: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    private void clearModuleForm() {
        moduleNumberField.clear();
        moduleTitleField.clear();
        moduleDescriptionField.clear();
        moduleContentUrlField.clear();
        moduleTypeField.clear();
        moduleDurationField.clear();
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