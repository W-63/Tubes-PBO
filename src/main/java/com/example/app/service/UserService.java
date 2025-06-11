package com.example.app.service;

import com.example.app.db.DBUtil; 
import com.example.app.model.Role; 
import com.example.app.model.User; 

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList; 
import java.util.List;     

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
                return rs.next(); 
            }
        }
    }

    public boolean registerUser(String username, String password, Role role) throws SQLException {
        String sql = "INSERT INTO users (username, password, role) VALUES (?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.setString(3, role.name().toLowerCase()); 
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
        return null; 
    }

    public List<User> getAllUsers() throws SQLException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT id, username, role FROM users";
        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                int id = rs.getInt("id");
                String username = rs.getString("username");
                Role role = Role.fromString(rs.getString("role"));
                users.add(new User(id, username, role));
            }
        }
        return users;
    }

    public boolean deleteUser(int userId) throws SQLException {
        String sql = "DELETE FROM users WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    public boolean updateUserRole(int userId, Role newRole) throws SQLException {
        String sql = "UPDATE users SET role = ? WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, newRole.name().toLowerCase());
            stmt.setInt(2, userId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }
}