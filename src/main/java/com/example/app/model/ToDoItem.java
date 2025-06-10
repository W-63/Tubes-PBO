package com.example.app.model;

import java.time.LocalDate;
import java.time.LocalTime;

public class ToDoItem {
    private int id;
    private int userId;
    private String kategori;
    private String deskripsi;
    private LocalDate tanggal;
    private LocalTime waktu;
    private boolean status;

    public ToDoItem() {}

    public ToDoItem(int id, int userId, String kategori, String deskripsi, LocalDate tanggal, LocalTime waktu, boolean status) {
        this.id = id;
        this.userId = userId;
        this.kategori = kategori;
        this.deskripsi = deskripsi;
        this.tanggal = tanggal;
        this.waktu = waktu;
        this.status = status;
    }

    public ToDoItem(String kategori, String deskripsi, LocalDate tanggal, LocalTime waktu) {
        this.kategori = kategori;
        this.deskripsi = deskripsi;
        this.tanggal = tanggal;
        this.waktu = waktu;
        this.status = false;
    }

    public int getId() { return id; }
    public int getUserId() { return userId; }
    public String getKategori() { return kategori; }
    public String getDeskripsi() { return deskripsi; }
    public LocalDate getTanggal() { return tanggal; }
    public LocalTime getWaktu() { return waktu; }
    public boolean isStatus() { return status; }

    public void setId(int id) { this.id = id; }
    public void setUserId(int userId) { this.userId = userId; }
    public void setKategori(String kategori) { this.kategori = kategori; }
    public void setDeskripsi(String deskripsi) { this.deskripsi = deskripsi; }
    public void setTanggal(LocalDate tanggal) { this.tanggal = tanggal; }
    public void setWaktu(LocalTime waktu) { this.waktu = waktu; }
    public void setStatus(boolean status) { this.status = status; }
}
