package com.example.app.dao;

import com.example.app.model.PaketEcommerce;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;

public class PaketDAOImpl implements PaketDAO { 

    private List<PaketEcommerce> paketDatabase;
    private static int nextId = 1; 

    public PaketDAOImpl() {
        paketDatabase = new ArrayList<>();
        
        addPaket(new PaketEcommerce(null, "Paket Belajar Dasar", "Akses ke modul dasar dan latihan.", 100000));
        addPaket(new PaketEcommerce(null, "Paket Belajar Menengah", "Akses ke modul menengah dan proyek.", 250000));
        addPaket(new PaketEcommerce(null, "Paket Belajar Mahir", "Akses ke semua modul, proyek, dan mentorship.", 500000));
    }

    @Override
    public void addPaket(PaketEcommerce paket) {
        if (paket.getId() == null) {
            paket.setId(nextId++);
        }
        paketDatabase.add(paket);
        System.out.println("Paket ditambahkan: " + paket.getNama() + " (ID: " + paket.getId() + ")");
    }

    @Override
    public List<PaketEcommerce> getAllPaket() {
        return new ArrayList<>(paketDatabase); 
    }

    @Override
    public void deletePaket(Integer id) {
        Iterator<PaketEcommerce> iterator = paketDatabase.iterator();
        boolean found = false;
        while (iterator.hasNext()) {
            PaketEcommerce paket = iterator.next();
            if (paket.getId() != null && paket.getId().equals(id)) {
                iterator.remove();
                found = true;
                System.out.println("Paket dengan ID " + id + " dihapus.");
                break;
            }
        }
        if (!found) {
            System.out.println("Paket dengan ID " + id + " tidak ditemukan.");
            // throw new Exception("Paket dengan ID " + id + " tidak ditemukan."); // Bisa dilempar exception jika ingin lebih ketat
        }
    }
}