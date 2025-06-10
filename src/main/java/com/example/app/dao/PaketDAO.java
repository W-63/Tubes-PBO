package com.example.app.dao;

import com.example.app.model.PaketEcommerce;
import java.util.List;

public interface PaketDAO {
    void addPaket(PaketEcommerce paket) throws Exception;
    List<PaketEcommerce> getAllPaket() throws Exception;
    void deletePaket(Integer id) throws Exception;
    // Anda bisa tambahkan metode lain seperti updatePaket jika diperlukan
}