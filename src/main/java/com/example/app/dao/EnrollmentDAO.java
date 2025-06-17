package com.example.app.dao;

import com.example.app.db.DBUtil;
import com.example.app.model.Enrollment;
import com.example.app.model.Course;
import com.example.app.model.User;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EnrollmentDAO {

    private static final DateTimeFormatter DATABASE_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public void addEnrollment(Enrollment enrollment) throws SQLException {
        String sql = "INSERT INTO enrollments (user_id, course_id, enrollment_date, progress_percentage, is_completed) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, enrollment.getUserId());
            stmt.setInt(2, enrollment.getCourseId());
            stmt.setString(3, enrollment.getEnrollmentDate().format(DATABASE_DATE_TIME_FORMATTER));
            stmt.setInt(4, enrollment.getProgressPercentage());
            stmt.setBoolean(5, enrollment.getIsCompleted());
            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    enrollment.setId(generatedKeys.getInt(1));
                }
            }
        }
    }

    public List<Enrollment> getEnrollmentsByUserId(Integer userId) throws SQLException {
        List<Enrollment> enrollments = new ArrayList<>();
        String sql = "SELECT e.id, e.user_id, e.course_id, e.enrollment_date, e.progress_percentage, e.is_completed, " +
                     "c.title as course_title " +
                     "FROM enrollments e JOIN courses c ON e.course_id = c.id WHERE e.user_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    LocalDateTime enrollmentDateTime = LocalDateTime.parse(rs.getString("enrollment_date"), DATABASE_DATE_TIME_FORMATTER);
                    Enrollment enrollment = new Enrollment(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getInt("course_id"),
                        enrollmentDateTime,
                        rs.getInt("progress_percentage"),
                        rs.getBoolean("is_completed")
                    );
                    enrollment.setCourseTitle(rs.getString("course_title"));
                    enrollments.add(enrollment);
                }
            }
        }
        return enrollments;
    }

    public boolean isUserEnrolled(Integer userId, Integer courseId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM enrollments WHERE user_id = ? AND course_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, courseId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    public void updateEnrollmentProgress(Integer enrollmentId, Integer newProgress, Boolean isCompleted) throws SQLException {
        String sql = "UPDATE enrollments SET progress_percentage = ?, is_completed = ? WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, newProgress);
            stmt.setBoolean(2, isCompleted);
            stmt.setInt(3, enrollmentId);
            stmt.executeUpdate();
        }
    }

    public Enrollment getEnrollmentById(Integer id) throws SQLException {
        String sql = "SELECT e.id, e.user_id, e.course_id, e.enrollment_date, e.progress_percentage, e.is_completed, " +
                     "c.title as course_title " +
                     "FROM enrollments e JOIN courses c ON e.course_id = c.id WHERE e.id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    LocalDateTime enrollmentDateTime = LocalDateTime.parse(rs.getString("enrollment_date"), DATABASE_DATE_TIME_FORMATTER);
                    Enrollment enrollment = new Enrollment(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getInt("course_id"),
                        enrollmentDateTime,
                        rs.getInt("progress_percentage"),
                        rs.getBoolean("is_completed")
                    );
                    enrollment.setCourseTitle(rs.getString("course_title"));
                    return enrollment;
                }
            }
        }
        return null;
    }

    public List<Enrollment> getAllEnrollments() throws SQLException {
        List<Enrollment> enrollments = new ArrayList<>();
        String sql = "SELECT e.id, e.user_id, e.course_id, e.enrollment_date, e.progress_percentage, e.is_completed, " +
                     "c.title as course_title, u.username as username " +
                     "FROM enrollments e " +
                     "JOIN courses c ON e.course_id = c.id " +
                     "JOIN users u ON e.user_id = u.id";
        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                LocalDateTime enrollmentDateTime = LocalDateTime.parse(rs.getString("enrollment_date"), DATABASE_DATE_TIME_FORMATTER);
                Enrollment enrollment = new Enrollment(
                    rs.getInt("id"),
                    rs.getInt("user_id"),
                    rs.getInt("course_id"),
                    enrollmentDateTime,
                    rs.getInt("progress_percentage"),
                    rs.getBoolean("is_completed")
                );
                enrollment.setCourseTitle(rs.getString("course_title"));
                enrollment.setUsername(rs.getString("username"));
                enrollments.add(enrollment);
            }
        }
        return enrollments;
    }

    public Map<String, Integer> getCourseProgressSummaryByUserId(Integer userId) throws SQLException {
        Map<String, Integer> summary = new HashMap<>();
        String sql = "SELECT is_completed, COUNT(*) as count FROM enrollments WHERE user_id = ? GROUP BY is_completed";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    boolean isCompleted = rs.getBoolean("is_completed");
                    int count = rs.getInt("count");
                    summary.put(isCompleted ? "Selesai" : "Berjalan", count);
                }
            }
        }
        summary.putIfAbsent("Selesai", 0);
        summary.putIfAbsent("Berjalan", 0);
        return summary;
    }
}