package com.example.app.model;

import java.time.LocalDate;
import java.time.LocalTime;

public class PaketEcommerce {
    private Integer id;
    private String nama;
    private String deskripsi;
    private Integer harga;

    public PaketEcommerce() {
        
    }

    public PaketEcommerce(Integer id, String nama, String deskripsi, Integer harga) {
        this.id = id;
        this.nama = nama;
        this.deskripsi = deskripsi;
        this.harga = harga;
    }

  
    public PaketEcommerce(String nama, String deskripsi, Integer harga) {
        this.nama = nama;
        this.deskripsi = deskripsi;
        this.harga = harga;
    }

    public Integer getId() {
        return id;
    }

    public String getNama() {
        return nama;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public Integer getHarga() {
        return harga;
    }

   
    public void setId(Integer id) {
        this.id = id;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public void setDeskripsi(String deskripsi) {
        this.deskripsi = deskripsi;
    }

    public void setHarga(Integer harga) {
        this.harga = harga;
    }

    @Override
    public String toString() {
        return "PaketEcommerce{" +
               "id=" + id +
               ", nama='" + nama + '\'' +
               ", deskripsi='" + deskripsi + '\'' +
               ", harga=" + harga +
               '}';
    }
}