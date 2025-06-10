package com.example.app.dao;

import com.example.app.db.DBUtil;
import com.example.app.model.ToDoItem;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class ToDoDAOImpl implements ToDoDAO {

    @Override
    public void addToDo(ToDoItem todo) throws SQLException {
        String sql = "INSERT INTO todo_items (kategori, deskripsi, tanggal, waktu, status) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, todo.getKategori());
            stmt.setString(2, todo.getDeskripsi());
            stmt.setDate(3, Date.valueOf(todo.getTanggal()));
            stmt.setTime(4, Time.valueOf(todo.getWaktu()));
            stmt.setBoolean(5, todo.isStatus());
            stmt.executeUpdate();
        }
    }

    @Override
    public void updateToDo(ToDoItem todo) throws SQLException {
        String sql = "UPDATE todo_items SET kategori = ?, deskripsi = ?, tanggal = ?, waktu = ?, status = ? WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, todo.getKategori());
            stmt.setString(2, todo.getDeskripsi());
            stmt.setDate(3, Date.valueOf(todo.getTanggal()));
            stmt.setTime(4, Time.valueOf(todo.getWaktu()));
            stmt.setBoolean(5, todo.isStatus());
            stmt.setInt(6, todo.getId());
            stmt.executeUpdate();
        }
    }

    @Override
    public void deleteToDo(int id) throws SQLException {
        String sql = "DELETE FROM todo_items WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    @Override
    public List<ToDoItem> getAllToDos() throws SQLException {
        List<ToDoItem> list = new ArrayList<>();
        String sql = "SELECT * FROM todo_items ORDER BY tanggal, waktu";
        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                ToDoItem item = new ToDoItem();
                item.setId(rs.getInt("id"));
                item.setKategori(rs.getString("kategori"));
                item.setDeskripsi(rs.getString("deskripsi"));
                item.setTanggal(rs.getDate("tanggal").toLocalDate());
                item.setWaktu(rs.getTime("waktu").toLocalTime());
                item.setStatus(rs.getBoolean("status"));
                list.add(item);
            }
        }
        return list;
    }

    @Override
    public void updateStatus(int id, boolean status) throws SQLException {
        String sql = "UPDATE todo_items SET status = ? WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setBoolean(1, status);
            stmt.setInt(2, id);
            stmt.executeUpdate();
        }
    }
}
