package com.example.app.dao;

import com.example.app.db.DBUtil; // Asumsi DBUtil ada untuk koneksi DB nyata
import com.example.app.model.ToDoItem;
import com.example.app.LoginController; // Untuk mendapatkan userId dari user yang login

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Date;  
import java.sql.Time;  
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ToDoDAOImpl implements ToDoDAO {

    // --- Menggunakan JDBC untuk database nyata ---
    // Pastikan Anda sudah membuat tabel 'todo_items' di database MySQL Anda:
    /*
    CREATE TABLE IF NOT EXISTS todo_items (
        id INT PRIMARY KEY AUTO_INCREMENT,
        user_id INT NOT NULL, -- Penting untuk aktivitas spesifik user
        kategori VARCHAR(50) NOT NULL,
        deskripsi TEXT NOT NULL,
        tanggal DATE NOT NULL,
        waktu TIME NOT NULL,
        status BOOLEAN DEFAULT FALSE,
        FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
    );
    */

    @Override
    public void addToDo(ToDoItem todo) throws SQLException {
        String sql = "INSERT INTO todo_items (user_id, kategori, deskripsi, tanggal, waktu, status) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            if (LoginController.currentLoggedInUser != null) {
                todo.setUserId(LoginController.currentLoggedInUser.getId());
            } else {
                System.err.println("addToDo: User tidak login, ToDoItem mungkin tidak terkait.");
            }

            stmt.setInt(1, todo.getUserId());
            stmt.setString(2, todo.getKategori());
            stmt.setString(3, todo.getDeskripsi());
            stmt.setDate(4, Date.valueOf(todo.getTanggal())); 
            stmt.setTime(5, Time.valueOf(todo.getWaktu()));   
            stmt.setBoolean(6, todo.isStatus());
            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    todo.setId(generatedKeys.getInt(1));
                }
            }
        }
    }

    @Override
    public List<ToDoItem> getAllToDos() throws SQLException {
        List<ToDoItem> todos = new ArrayList<>();
        String sql = "SELECT id, user_id, kategori, deskripsi, tanggal, waktu, status FROM todo_items WHERE user_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            if (LoginController.currentLoggedInUser != null) {
                stmt.setInt(1, LoginController.currentLoggedInUser.getId());
            } else {
                // Jika tidak ada user login, kembalikan list kosong atau throw error
                System.err.println("getAllToDos: User tidak login, tidak dapat mengambil ToDo.");
                return todos; // Kembalikan list kosong
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    todos.add(new ToDoItem(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getString("kategori"),
                        rs.getString("deskripsi"),
                        rs.getDate("tanggal").toLocalDate(), // Konversi java.sql.Date ke LocalDate
                        rs.getTime("waktu").toLocalTime(),   // Konversi java.sql.Time ke LocalTime
                        rs.getBoolean("status")
                    ));
                }
            }
        }
        return todos;
    }

    @Override
    public void deleteToDo(int id) throws SQLException {
        String sql = "DELETE FROM todo_items WHERE id = ? AND user_id = ?"; // Hapus hanya milik user yang login
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);

            if (LoginController.currentLoggedInUser != null) {
                stmt.setInt(2, LoginController.currentLoggedInUser.getId());
            } else {
                throw new SQLException("User tidak login, tidak dapat menghapus ToDo.");
            }
            stmt.executeUpdate();
        }
    }

    @Override
    public void updateStatus(int id, boolean status) throws SQLException {
        String sql = "UPDATE todo_items SET status = ? WHERE id = ? AND user_id = ?"; // Update hanya milik user yang login
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setBoolean(1, status);
            stmt.setInt(2, id);

            if (LoginController.currentLoggedInUser != null) {
                stmt.setInt(3, LoginController.currentLoggedInUser.getId());
            } else {
                throw new SQLException("User tidak login, tidak dapat memperbarui status ToDo.");
            }
            stmt.executeUpdate();
        }
    }
}