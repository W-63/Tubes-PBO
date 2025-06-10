package com.example.app;

import java.io.IOException;

import com.example.app.dao.PaketDAOImpl;
import com.example.app.model.PaketEcommerce;
import com.example.app.model.Role; 
import com.example.app.model.User; 

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
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox; 
import javafx.stage.Stage;

public class EcommerceController {

    @FXML private TextField namaField;
    @FXML private TextField deskripsiField;
    @FXML private TextField hargaField;
    @FXML private TableView<PaketEcommerce> paketTable;
    @FXML private TableColumn<PaketEcommerce, String> namaColumn;
    @FXML private TableColumn<PaketEcommerce, String> deskripsiColumn;
    @FXML private TableColumn<PaketEcommerce, Integer> hargaColumn;
    @FXML private TableColumn<PaketEcommerce, Void> aksiColumn;
    @FXML private Button tambahButton;
    @FXML private VBox formInputPaket; 

    private ObservableList<PaketEcommerce> paketList;
    private PaketDAOImpl paketDAO;
    private User currentUser; 

    @FXML
    public void initialize() {
        paketDAO = new PaketDAOImpl();
        paketList = FXCollections.observableArrayList();

        namaColumn.setCellValueFactory(new PropertyValueFactory<>("nama"));
        deskripsiColumn.setCellValueFactory(new PropertyValueFactory<>("deskripsi"));
        hargaColumn.setCellValueFactory(new PropertyValueFactory<>("harga"));

        aksiColumn.setCellFactory(param -> new TableCell<>() {
            private final Button hapusBtn = new Button("Hapus");

            {
                
                hapusBtn.setOnAction(e -> {
                    PaketEcommerce item = getTableView().getItems().get(getIndex());
                    try {
                        paketDAO.deletePaket(item.getId());
                        loadPaket(); 
                        showAlert("Sukses", "Paket berhasil dihapus!", Alert.AlertType.INFORMATION);
                    } catch (Exception ex) {
                        showAlert("Gagal", "Gagal menghapus data: " + ex.getMessage(), Alert.AlertType.ERROR);
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(hapusBtn);
                    
                    if (currentUser != null && currentUser.getRole() == Role.ADMIN) {
                        hapusBtn.setVisible(true);
                        hapusBtn.setManaged(true); 
                    } else {
                        hapusBtn.setVisible(false);
                        hapusBtn.setManaged(false); 
                    }
                }
            }
        });

        paketTable.setItems(paketList);
        loadPaket(); 
    }

    /**
     * Metode untuk menginisialisasi controller dengan data pengguna yang login.
     * Dipanggil dari HomeController saat navigasi.
     * @param user Objek User yang berisi data pengguna yang login.
     */
    public void initUserData(User user) {
        this.currentUser = user;
        if (this.currentUser != null) {
            System.out.println("EcommerceController: Diterima user " + currentUser.getUsername() + " dengan peran " + currentUser.getRole());

            if (currentUser.getRole() == Role.ADMIN) {
                if (formInputPaket != null) {
                    formInputPaket.setVisible(true);
                    formInputPaket.setManaged(true);
                }
                aksiColumn.setVisible(true); 
            } else {
                
                if (formInputPaket != null) {
                    formInputPaket.setVisible(false);
                    formInputPaket.setManaged(false);
                }
                aksiColumn.setVisible(false); 
                aksiColumn.setVisible(false); 
            }
            
            paketTable.refresh();
        }
    }

    @FXML
    private void tambahPaket() {
        String nama = namaField.getText().trim();
        String deskripsi = deskripsiField.getText().trim();
        String hargaStr = hargaField.getText().trim();

        if (nama.isEmpty() || deskripsi.isEmpty() || hargaStr.isEmpty()) {
            showAlert("Peringatan", "Semua field harus diisi!", Alert.AlertType.WARNING);
            return;
        }

        try {
            int harga = Integer.parseInt(hargaStr);
            PaketEcommerce paket = new PaketEcommerce(nama, deskripsi, harga); 

            paketDAO.addPaket(paket);
            showAlert("Sukses", "Paket berhasil ditambahkan!", Alert.AlertType.INFORMATION);
            clearForm();
            loadPaket(); 
        } catch (NumberFormatException e) {
            showAlert("Format Salah", "Harga harus berupa angka!", Alert.AlertType.WARNING);
        } catch (Exception e) {
            showAlert("Gagal", "Gagal menyimpan: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void loadPaket() {
        try {
            paketList.setAll(paketDAO.getAllPaket());
        } catch (Exception e) {
            showAlert("Gagal", "Gagal memuat data: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void clearForm() {
        namaField.clear();
        deskripsiField.clear();
        hargaField.clear();
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.setHeaderText(null);
        alert.showAndWait();
    }

    /**
     * Menangani aksi tombol kembali untuk navigasi ke halaman Home.
     * @param event ActionEvent dari tombol yang diklik.
     */
    @FXML
    private void handleBack(ActionEvent event) {
        try {
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            String fxmlPath;
            String title;
            
            if (currentUser != null && currentUser.getRole() == Role.ADMIN) {
                fxmlPath = "/admin_home.fxml"; 
                title = "Admin Dashboard - EDULIFE+";
            } else {
                fxmlPath = "/user_home.fxml"; 
                title = "Beranda Pengguna - EDULIFE+";
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent homeRoot = loader.load();

            Scene homeScene = new Scene(homeRoot);
            currentStage.setScene(homeScene);
            currentStage.setTitle(title + " (" + (currentUser != null ? currentUser.getUsername() : "Guest") + ")");
            currentStage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Kesalahan Navigasi", "Gagal memuat halaman Beranda: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
}