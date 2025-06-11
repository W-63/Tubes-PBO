package com.example.app;

import com.example.app.dao.ToDoDAO;
import com.example.app.dao.ToDoDAOImpl;
import com.example.app.model.ToDoItem;
import com.example.app.model.User; 
import com.example.app.model.Role; 

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

    private ToDoDAO todoDAO;
    private ObservableList<ToDoItem> todoList;
    private User currentUser; 

    @FXML
    public void initialize() {
        todoDAO = new ToDoDAOImpl();
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
                    
                    tampilkanAlert("Peringatan", "Fungsi update status belum sepenuhnya diimplementasikan di DAO.", Alert.AlertType.INFORMATION);

                } catch (Exception e) { 
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

    public void initUserData(User user) {
        this.currentUser = user;
      
    }


    private void muatDataToDo() {
        try {
            todoList.setAll(todoDAO.getAllToDos());
        } catch (SQLException e) {
            tampilkanAlert("Kesalahan", "Gagal memuat data: " + e.getMessage(), Alert.AlertType.ERROR);
        } catch (Exception e) { 
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
            waktu = LocalTime.parse(waktuStr); 
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
        } catch (Exception e) { 
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
        } catch (Exception e) { 
            tampilkanAlert("Kesalahan", "Gagal menghapus data: " + e.getMessage(), Alert.AlertType.ERROR);
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

            HomeController homeController = new HomeController();
            homeController.setupHomeView(currentStage, LoginController.currentLoggedInUser);

        } catch (IOException e) {
            e.printStackTrace();
            tampilkanAlert("Kesalahan Navigasi", "Gagal memuat halaman Beranda: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
}