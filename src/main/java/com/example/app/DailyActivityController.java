package com.example.app;

import com.example.app.dao.ToDoDAO;
import com.example.app.dao.ToDoDAOImpl;
import com.example.app.model.ToDoItem;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent; // Ditambahkan
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader; // Ditambahkan
import javafx.scene.Node; // Ditambahkan
import javafx.scene.Parent; // Ditambahkan
import javafx.scene.Scene; // Ditambahkan
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage; // Ditambahkan

import java.io.IOException; // Ditambahkan
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;

public class DailyActivityController {

    // Perubahan: TextField menjadi ComboBox
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
    @FXML private Button kembaliButton; // Ditambahkan untuk tombol kembali

    private ToDoDAO todoDAO;
    private ObservableList<ToDoItem> todoList;

    @FXML
    public void initialize() {
        todoDAO = new ToDoDAOImpl();
        todoList = FXCollections.observableArrayList();

        // Inisialisasi ComboBox dengan opsi kategori
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
                    todoDAO.updateStatus(todo.getId(), isNowSelected);
                } catch (SQLException e) {
                    tampilkanAlert("Kesalahan", "Gagal update status: " + e.getMessage(), Alert.AlertType.ERROR);
                    booleanProp.set(wasSelected);
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
    }

    private void muatDataToDo() {
        try {
            todoList.setAll(todoDAO.getAllToDos());
        } catch (SQLException e) {
            tampilkanAlert("Kesalahan", "Gagal memuat data: " + e.getMessage(), Alert.AlertType.ERROR);
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
            tampilkanAlert("Peringatan", "Semua field harus diisi (selain kategori yang sudah dipilih).", Alert.AlertType.WARNING);
            return;
        }

        LocalTime waktu;
        try {
            waktu = LocalTime.parse(waktuStr); // Format HH:mm
        } catch (Exception e) {
            tampilkanAlert("Format Salah", "Waktu harus dalam format HH:mm, contoh: 09:30", Alert.AlertType.WARNING);
            return;
        }

        ToDoItem todo = new ToDoItem(kategori, deskripsi, tanggal, waktu);

        try {
            todoDAO.addToDo(todo);
            tampilkanAlert("Sukses", "Target berhasil ditambahkan.", Alert.AlertType.INFORMATION);
            bersihkanInput();
            muatDataToDo();
        } catch (SQLException e) {
            tampilkanAlert("Kesalahan", "Gagal menyimpan data: " + e.getMessage(), Alert.AlertType.ERROR);
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
            todoDAO.deleteToDo(selected.getId());
            tampilkanAlert("Sukses", "Target berhasil dihapus.", Alert.AlertType.INFORMATION);
            muatDataToDo();
        } catch (SQLException e) {
            tampilkanAlert("Kesalahan", "Gagal menghapus data: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void bersihkanInput() {
        kategoriComboBox.setValue(null);
        deskripsiField.clear();
        tanggalPicker.setValue(null);
        waktuField.clear();
    }

    /**
     * Menampilkan alert dialog.
     * @param judul Judul alert.
     * @param isi Isi pesan alert.
     * @param alertType Jenis alert (INFORMATION, WARNING, ERROR, dll.).
     */
    private void tampilkanAlert(String judul, String isi, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(judul);
        alert.setHeaderText(null);
        alert.setContentText(isi);
        alert.showAndWait();
    }

    /**
     * Menangani aksi tombol kembali untuk navigasi ke halaman Home.
     * @param event ActionEvent dari tombol yang diklik.
     */
    @FXML
    private void handleKembaliKeHome(ActionEvent event) {
        try {
            // Muat file FXML untuk halaman home.
            // Pastikan path ini benar sesuai dengan lokasi home.fxml Anda.
            // Jika home.fxml ada di root 'resources', path-nya adalah "/home.fxml".
            // Jika ada di subfolder 'view' di dalam 'resources', path-nya adalah "/view/home.fxml".
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/home.fxml")); // Ganti jika path berbeda
            
            Parent homeRoot = loader.load();
            Scene homeScene = new Scene(homeRoot);

            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            currentStage.setScene(homeScene);
            currentStage.setTitle("Beranda - EDULIFE+"); // Sesuaikan judul jika perlu
            currentStage.show();

        } catch (IOException e) {
            e.printStackTrace();
            tampilkanAlert("Kesalahan Navigasi", "Gagal memuat halaman Beranda: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
}