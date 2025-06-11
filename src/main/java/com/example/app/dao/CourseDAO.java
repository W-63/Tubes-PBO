package com.example.app.dao;

import com.example.app.db.DBUtil;
import com.example.app.model.Course;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CourseDAO {

    public void addCourse(Course course) throws SQLException {
        String sql = "INSERT INTO courses (title, description, price, duration_weeks, total_modules) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, course.getTitle());
            stmt.setString(2, course.getDescription());
            stmt.setInt(3, course.getPrice());
            stmt.setInt(4, course.getDurationWeeks());
            stmt.setInt(5, course.getTotalModules());
            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    course.setId(generatedKeys.getInt(1));
                }
            }
        }
    }

    public List<Course> getAllCourses() throws SQLException {
        List<Course> courses = new ArrayList<>();
        String sql = "SELECT id, title, description, price, duration_weeks, total_modules FROM courses";
        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                courses.add(new Course(
                    rs.getInt("id"),
                    rs.getString("title"),
                    rs.getString("description"),
                    rs.getInt("price"),
                    rs.getInt("duration_weeks"),
                    rs.getInt("total_modules")
                ));
            }
        }
        return courses;
    }

    public Course getCourseById(Integer id) throws SQLException {
        String sql = "SELECT id, title, description, price, duration_weeks, total_modules FROM courses WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Course(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getInt("price"),
                        rs.getInt("duration_weeks"),
                        rs.getInt("total_modules")
                    );
                }
            }
        }
        return null;
    }

    public void deleteCourse(Integer id) throws SQLException {
        String sql = "DELETE FROM courses WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    public void initializeDummyCourses() throws SQLException {
        if (getAllCourses().isEmpty()) {
            addCourse(new Course("Dasar Pemrograman Java", "Pelajari dasar-dasar Java untuk pemula.", 150000, 4, 10));
            addCourse(new Course("Pengembangan Web dengan Spring Boot", "Membangun aplikasi web backend modern.", 300000, 8, 15));
            addCourse(new Course("UI/UX Design Fundamentals", "Prinsip dasar desain antarmuka pengguna.", 200000, 6, 8));
            System.out.println("Dummy courses added to database.");
        }
    }
}