package com.example.app.service;

import com.example.app.db.DBUtil; // Menggunakan DBUtil dari proyek Anda
import com.example.app.model.Role;
import com.example.app.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class UserService {

    public Role determineInitialRole() throws SQLException {
        String countQuery = "SELECT COUNT(*) AS total_users FROM users";
        try (Connection conn = DBUtil.getConnection();
             Statement countStmt = conn.createStatement();
             ResultSet countRs = countStmt.executeQuery(countQuery)) {
            if (countRs.next()) {
                int totalUsers = countRs.getInt("total_users");
                if (totalUsers == 0) {
                    return Role.ADMIN;
                }
            }
        }
        return Role.USER;
    }

    public boolean isUsernameTaken(String username) throws SQLException {
        String sql = "SELECT id FROM users WHERE username = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next(); // true jika username ditemukan
            }
        }
    }

    public boolean registerUser(String username, String password, Role role) throws SQLException {
        // Sebenarnya, pengecekan isUsernameTaken idealnya dilakukan sebelum memanggil ini,
        // atau sebagai bagian dari transaksi jika memungkinkan.
        // Untuk kesederhanaan, kita asumsikan sudah dicek.

        String sql = "INSERT INTO users (username, password, role) VALUES (?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.setString(3, role.name().toLowerCase()); // Simpan nama enum sebagai string lowercase
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    public User loginUser(String username, String password) throws SQLException {
        String sql = "SELECT id, username, role FROM users WHERE username = ? AND password = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt("id");
                    String dbUsername = rs.getString("username");
                    Role role = Role.fromString(rs.getString("role"));
                    return new User(id, dbUsername, role);
                }
            }
        }
        return null; // User tidak ditemukan atau password salah
    }
}
