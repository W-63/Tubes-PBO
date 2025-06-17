package com.example.app.dao;

import com.example.app.model.ToDoItem;
import java.sql.SQLException; // Pastikan ini diimpor
import java.util.List;

public interface ToDoDAO {
    void addToDo(ToDoItem todo) throws SQLException; // Mengubah Exception menjadi SQLException
    List<ToDoItem> getAllToDos() throws SQLException; // Mengubah Exception menjadi SQLException
    void deleteToDo(int id) throws SQLException; // Mengubah Exception menjadi SQLException
    void updateStatus(int id, boolean status) throws SQLException; // BARU: Untuk update status checkbox
    // Anda bisa tambahkan metode lain seperti updateToDo(ToDoItem todo) jika diperlukan
}