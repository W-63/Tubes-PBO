package com.example.app.model;

import java.time.LocalDate;
import java.time.LocalTime;

public class ToDoItem {
    private int id;
    private int userId; // Ditambahkan: untuk melacak pemilik aktivitas
    private String kategori;
    private String deskripsi;
    private LocalDate tanggal;
    private LocalTime waktu;
    private boolean status;

    public ToDoItem() {}

    // Konstruktor lengkap
    public ToDoItem(int id, int userId, String kategori, String deskripsi, LocalDate tanggal, LocalTime waktu, boolean status) {
        this.id = id;
        this.userId = userId;
        this.kategori = kategori;
        this.deskripsi = deskripsi;
        this.tanggal = tanggal;
        this.waktu = waktu;
        this.status = status;
    }

    // Konstruktor untuk item baru (tanpa ID dari DB, tanpa status awal)
    public ToDoItem(String kategori, String deskripsi, LocalDate tanggal, LocalTime waktu) {
        this.kategori = kategori;
        this.deskripsi = deskripsi;
        this.tanggal = tanggal;
        this.waktu = waktu;
        this.status = false; // Default status: belum selesai
    }

    // Getters
    public int getId() { return id; }
    public int getUserId() { return userId; } // Getter baru
    public String getKategori() { return kategori; }
    public String getDeskripsi() { return deskripsi; }
    public LocalDate getTanggal() { return tanggal; }
    public LocalTime getWaktu() { return waktu; }
    public boolean isStatus() { return status; } // isStatus() untuk boolean

    // Setters
    public void setId(int id) { this.id = id; }
    public void setUserId(int userId) { this.userId = userId; } // Setter baru
    public void setKategori(String kategori) { this.kategori = kategori; }
    public void setDeskripsi(String deskripsi) { this.deskripsi = deskripsi; }
    public void setTanggal(LocalDate tanggal) { this.tanggal = tanggal; }
    public void setWaktu(LocalTime waktu) { this.waktu = waktu; }
    public void setStatus(boolean status) { this.status = status; }

    @Override
    public String toString() {
        return "ToDoItem{" +
               "id=" + id +
               ", userId=" + userId +
               ", kategori='" + kategori + '\'' +
               ", deskripsi='" + deskripsi + '\'' +
               ", tanggal=" + tanggal +
               ", waktu=" + waktu +
               ", status=" + status +
               '}';
    }
}