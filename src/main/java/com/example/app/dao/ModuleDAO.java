package com.example.app.dao;

import com.example.app.db.DBUtil;
import com.example.app.model.Module;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ModuleDAO {

    public void addModule(Module module) throws SQLException {
        String sql = "INSERT INTO modules (course_id, module_number, title, description, content_url, module_type, duration_minutes) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, module.getCourseId());
            stmt.setInt(2, module.getModuleNumber());
            stmt.setString(3, module.getTitle());
            stmt.setString(4, module.getDescription());
            stmt.setString(5, module.getContentUrl());
            stmt.setString(6, module.getModuleType());
            stmt.setInt(7, module.getDurationMinutes());
            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    module.setId(generatedKeys.getInt(1));
                }
            }
        }
    }

    public List<Module> getModulesByCourseId(Integer courseId) throws SQLException {
        List<Module> modules = new ArrayList<>();
        String sql = "SELECT id, course_id, module_number, title, description, content_url, module_type, duration_minutes FROM modules WHERE course_id = ? ORDER BY module_number ASC";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, courseId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    modules.add(new Module(
                        rs.getInt("id"),
                        rs.getInt("course_id"),
                        rs.getInt("module_number"),
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getString("content_url"),
                        rs.getString("module_type"),
                        rs.getInt("duration_minutes")
                    ));
                }
            }
        }
        return modules;
    }

    public Module getModuleById(Integer moduleId) throws SQLException {
        String sql = "SELECT id, course_id, module_number, title, description, content_url, module_type, duration_minutes FROM modules WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, moduleId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Module(
                        rs.getInt("id"),
                        rs.getInt("course_id"),
                        rs.getInt("module_number"),
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getString("content_url"),
                        rs.getString("module_type"),
                        rs.getInt("duration_minutes")
                    );
                }
            }
        }
        return null;
    }

    public void deleteModule(Integer id) throws SQLException {
        String sql = "DELETE FROM modules WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    public int getTotalModulesInCourse(Integer courseId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM modules WHERE course_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, courseId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }
}