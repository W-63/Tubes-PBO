package com.example.app;

import com.example.app.dao.ToDoDAO;
import com.example.app.dao.ToDoDAOImpl;
import com.example.app.model.ToDoItem;
import com.example.app.model.User;
import com.example.app.model.Role;
import com.example.app.service.ToDoService; // Import ToDoService
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List; // Import List

public class DailyActivityController {

    @FXML private ComboBox<String> kategoriComboBox;
    @FXML private TextArea deskripsiField;
    @FXML private DatePicker tanggalPicker;
    @FXML private TextField waktuField;
    @FXML private TableView<ToDoItem> todoTable;
    @FXML private TableColumn<ToDoItem, String> kategoriColumn;
    @FXML private TableColumn<ToDoItem, String> deskripsiColumn;
    @FXML private TableColumn<ToDoItem, LocalDate> tanggalColumn;
    @FXML private TableColumn<ToDoItem, LocalTime> waktuColumn;
    @FXML private TableColumn<ToDoItem, Boolean> statusColumn;
    @FXML private Button tambahButton;
    @FXML private Button hapusButton;
    @FXML private Button kembaliButton;

    private ToDoService todoService; // Gunakan ToDoService
    private ObservableList<ToDoItem> todoList;
    private User currentUser;

    @FXML
    public void initialize() {
        todoService = new ToDoService(); // Inisialisasi ToDoService
        todoList = FXCollections.observableArrayList();

        ObservableList<String> kategoriOptions =
            FXCollections.observableArrayList(
                "Pekerjaan",
                "Belajar",
                "Olahraga",
                "Pribadi",
                "Lainnya"
            );
        kategoriComboBox.setItems(kategoriOptions);

        kategoriColumn.setCellValueFactory(new PropertyValueFactory<>("kategori"));
        deskripsiColumn.setCellValueFactory(new PropertyValueFactory<>("deskripsi"));
        tanggalColumn.setCellValueFactory(new PropertyValueFactory<>("tanggal"));
        waktuColumn.setCellValueFactory(new PropertyValueFactory<>("waktu"));

        statusColumn.setCellValueFactory(cellData -> {
            ToDoItem todo = cellData.getValue();
            SimpleBooleanProperty booleanProp = new SimpleBooleanProperty(todo.isStatus());
            booleanProp.addListener((obs, wasSelected, isNowSelected) -> {
                todo.setStatus(isNowSelected);
                try {
                    // *** Ganti baris ini dengan pemanggilan yang benar ***
                    todoService.updateStatus(todo.getId(), isNowSelected); // Panggil metode updateStatus dari ToDoService
                } catch (SQLException e) {
                    tampilkanAlert("Kesalahan Database", "Gagal memperbarui status: " + e.getMessage(), Alert.AlertType.ERROR);
                    booleanProp.set(wasSelected);
                    e.printStackTrace();
                } catch (Exception e) {
                    tampilkanAlert("Kesalahan", "Terjadi kesalahan tak terduga saat memperbarui status: " + e.getMessage(), Alert.AlertType.ERROR);
                    booleanProp.set(wasSelected);
                    e.printStackTrace();
                }
            });
            return booleanProp;
        });
        statusColumn.setCellFactory(CheckBoxTableCell.forTableColumn(statusColumn));

        todoTable.setItems(todoList);
        todoTable.setEditable(true);
        statusColumn.setEditable(true);

        hapusButton.setDisable(true);
        todoTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            hapusButton.setDisable(newSelection == null);
        });

        muatDataToDo();
        cekDanTampilkanNotifikasi();
    }

    public void initUserData(User user) {
        this.currentUser = user;
        if (this.currentUser != null) {
            System.out.println("DailyActivityController: Diterima user " + currentUser.getUsername() + " dengan peran " + currentUser.getRole());
            cekDanTampilkanNotifikasi();
        }
    }

    private void muatDataToDo() {
        try {
            todoList.setAll(todoService.getAllToDos());
        } catch (SQLException e) {
            tampilkanAlert("Kesalahan", "Gagal memuat data: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        } catch (Exception e) {
            tampilkanAlert("Kesalahan", "Gagal memuat data: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    @FXML
    private void tambahTarget() {
        String kategori = kategoriComboBox.getValue();
        String deskripsi = deskripsiField.getText().trim();
        LocalDate tanggal = tanggalPicker.getValue();
        String waktuStr = waktuField.getText().trim();

        if (kategori == null || kategori.isEmpty()) {
            tampilkanAlert("Peringatan", "Kategori harus dipilih.", Alert.AlertType.WARNING);
            return;
        }

        if (deskripsi.isEmpty() || tanggal == null || waktuStr.isEmpty()) {
            tampilkanAlert("Peringatan", "Semua field harus diisi.", Alert.AlertType.WARNING);
            return;
        }

        LocalTime waktu;
        try {
            waktu = LocalTime.parse(waktuStr);
        } catch (Exception e) {
            tampilkanAlert("Format Salah", "Waktu harus dalam format HH:mm, contoh: 09:30", Alert.AlertType.WARNING);
            return;
        }

        ToDoItem todo = new ToDoItem(kategori, deskripsi, tanggal, waktu);
        if (currentUser != null) {
            todo.setUserId(currentUser.getId()); // Set userId untuk ToDoItem
        }

        try {
            todoService.addToDo(todo);
            tampilkanAlert("Sukses", "Target berhasil ditambahkan.", Alert.AlertType.INFORMATION);
            bersihkanInput();
            muatDataToDo();
            cekDanTampilkanNotifikasi();
        } catch (SQLException e) {
            tampilkanAlert("Kesalahan Database", "Gagal menyimpan data: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        } catch (Exception e) {
            tampilkanAlert("Kesalahan Validasi", e.getMessage(), Alert.AlertType.WARNING);
            e.printStackTrace();
        }
    }

    @FXML
    private void hapusTarget() {
        ToDoItem selected = todoTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            tampilkanAlert("Peringatan", "Pilih target yang ingin dihapus.", Alert.AlertType.WARNING);
            return;
        }

        try {
            todoService.deleteToDo(selected.getId());
            tampilkanAlert("Sukses", "Target berhasil dihapus.", Alert.AlertType.INFORMATION);
            muatDataToDo();
            cekDanTampilkanNotifikasi();
        } catch (SQLException e) {
            tampilkanAlert("Kesalahan Database", "Gagal menghapus data: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        } catch (Exception e) {
            tampilkanAlert("Kesalahan", "Gagal menghapus data: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    private void bersihkanInput() {
        kategoriComboBox.setValue(null);
        deskripsiField.clear();
        tanggalPicker.setValue(null);
        waktuField.clear();
    }

    private void tampilkanAlert(String judul, String isi, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(judul);
        alert.setHeaderText(null);
        alert.setContentText(isi);
        alert.showAndWait();
    }

    @FXML
    private void handleKembaliKeHome(ActionEvent event) {
        try {
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            if (LoginController.currentLoggedInUser != null) {
                HomeController homeController = new HomeController();
                homeController.setupHomeView(currentStage, LoginController.currentLoggedInUser);
            } else {
                tampilkanAlert("Kesalahan", "Informasi pengguna tidak tersedia. Harap login kembali.", Alert.AlertType.ERROR);
            }

        } catch (IOException e) {
            e.printStackTrace();
            tampilkanAlert("Kesalahan Navigasi", "Gagal memuat halaman Beranda: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void cekDanTampilkanNotifikasi() {
        if (currentUser == null) {
            System.err.println("cekDanTampilkanNotifikasi: Pengguna belum login atau data pengguna belum diinisialisasi.");
            return;
        }

        try {
            List<ToDoItem> notifications = todoService.getUpcomingOrOverdueToDos(currentUser.getId());
            if (!notifications.isEmpty()) {
                StringBuilder notificationMessage = new StringBuilder("Perhatian! Anda memiliki kegiatan yang mendekati/telah melewati tenggat:\n\n");
                for (ToDoItem item : notifications) {
                    notificationMessage.append("  - Kategori: ").append(item.getKategori())
                                       .append(", Deskripsi: ").append(item.getDeskripsi())
                                       .append(", Tenggat: ").append(item.getTanggal())
                                       .append(" Pukul ").append(item.getWaktu()).append("\n");
                }
                tampilkanAlert("Notifikasi Kegiatan", notificationMessage.toString(), Alert.AlertType.WARNING);
            }
        } catch (SQLException e) {
            tampilkanAlert("Kesalahan Notifikasi", "Gagal mengambil notifikasi: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }
}