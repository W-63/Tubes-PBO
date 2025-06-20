package com.example.app.dao;

import com.example.app.db.DBUtil;
import com.example.app.model.ToDoItem;
import com.example.app.LoginController;

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ToDoDAOImpl implements ToDoDAO {

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
                System.err.println("getAllToDos: User tidak login, tidak dapat mengambil ToDo.");
                return todos;
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    todos.add(new ToDoItem(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getString("kategori"),
                        rs.getString("deskripsi"),
                        rs.getDate("tanggal").toLocalDate(),
                        rs.getTime("waktu").toLocalTime(),
                        rs.getBoolean("status")
                    ));
                }
            }
        }
        return todos;
    }

    @Override
    public void deleteToDo(int id) throws SQLException {
        String sql = "DELETE FROM todo_items WHERE id = ? AND user_id = ?";
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
        String sql = "UPDATE todo_items SET status = ? WHERE id = ? AND user_id = ?";
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

    @Override
    public Map<String, Integer> getToDoProgressSummaryByUserId(Integer userId) throws SQLException {
        Map<String, Integer> summary = new HashMap<>();
        String sql = "SELECT status, COUNT(*) as count FROM todo_items WHERE user_id = ? GROUP BY status";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    boolean status = rs.getBoolean("status");
                    int count = rs.getInt("count");
                    summary.put(status ? "Selesai" : "Belum Selesai", count);
                }
            }
        }
        summary.putIfAbsent("Selesai", 0);
        summary.putIfAbsent("Belum Selesai", 0);
        return summary;
    }

    @Override
    public List<ToDoItem> getUpcomingOrOverdueToDos(int userId) throws SQLException {
        List<ToDoItem> todos = new ArrayList<>();
        // Mengambil item yang belum selesai (status = FALSE) DAN
        // (tanggal hari ini ATAU tanggal sebelumnya)
        // ATAU (tanggal besok DAN waktu sebelum sekarang)
        String sql = "SELECT id, user_id, kategori, deskripsi, tanggal, waktu, status FROM todo_items " +
                     "WHERE user_id = ? AND status = FALSE AND (" +
                     " (tanggal = CURDATE() AND waktu <= CURTIME()) OR " + // Lewat tenggat hari ini
                     " (tanggal < CURDATE()) OR " + // Sudah lewat tanggal
                     " (tanggal = DATE_ADD(CURDATE(), INTERVAL 1 DAY) AND waktu <= CURTIME()) " + // Tenggat besok, tapi waktunya sudah lewat (opsional, bisa disesuaikan)
                     ") ORDER BY tanggal ASC, waktu ASC";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    todos.add(new ToDoItem(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getString("kategori"),
                        rs.getString("deskripsi"),
                        rs.getDate("tanggal").toLocalDate(),
                        rs.getTime("waktu").toLocalTime(),
                        rs.getBoolean("status")
                    ));
                }
            }
        }
        return todos;
    }
}
