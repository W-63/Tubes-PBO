package com.example.app.dao;

import com.example.app.db.DBUtil;
import com.example.app.model.UserModuleProgress;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class UserModuleProgressDAO {

    private static final DateTimeFormatter DATABASE_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public void addOrUpdateUserModuleProgress(UserModuleProgress progress) throws SQLException {
        String sql = "INSERT INTO user_module_progress (user_id, module_id, is_completed, completion_date, score) VALUES (?, ?, ?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE is_completed = VALUES(is_completed), completion_date = VALUES(completion_date), score = VALUES(score)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, progress.getUserId());
            stmt.setInt(2, progress.getModuleId());
            stmt.setBoolean(3, progress.getIsCompleted());
            if (progress.getCompletionDate() != null) {
                stmt.setString(4, progress.getCompletionDate().format(DATABASE_DATE_TIME_FORMATTER));
            } else {
                stmt.setNull(4, Types.VARCHAR);
            }
            if (progress.getScore() != null) {
                stmt.setInt(5, progress.getScore());
            } else {
                stmt.setNull(5, Types.INTEGER);
            }
            stmt.executeUpdate();
        }
    }

    public UserModuleProgress getUserModuleProgress(Integer userId, Integer moduleId) throws SQLException {
        String sql = "SELECT id, user_id, module_id, is_completed, completion_date, score FROM user_module_progress WHERE user_id = ? AND module_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, moduleId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    LocalDateTime completionDate = null;
                    if (rs.getString("completion_date") != null) {
                        completionDate = LocalDateTime.parse(rs.getString("completion_date"), DATABASE_DATE_TIME_FORMATTER);
                    }
                    Integer score = rs.getObject("score", Integer.class); // Menggunakan getObject untuk null
                    return new UserModuleProgress(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getInt("module_id"),
                        rs.getBoolean("is_completed"),
                        completionDate,
                        score
                    );
                }
            }
        }
        return null;
    }

    public List<UserModuleProgress> getUserProgressByCourseId(Integer userId, Integer courseId) throws SQLException {
        List<UserModuleProgress> progresses = new ArrayList<>();
        String sql = "SELECT ump.id, ump.user_id, ump.module_id, ump.is_completed, ump.completion_date, ump.score, " +
                     "m.title as module_title, m.module_number as module_number " +
                     "FROM user_module_progress ump " +
                     "JOIN modules m ON ump.module_id = m.id " +
                     "WHERE ump.user_id = ? AND m.course_id = ? ORDER BY m.module_number ASC";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, courseId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    LocalDateTime completionDate = null;
                    if (rs.getString("completion_date") != null) {
                        completionDate = LocalDateTime.parse(rs.getString("completion_date"), DATABASE_DATE_TIME_FORMATTER);
                    }
                    Integer score = rs.getObject("score", Integer.class);
                    UserModuleProgress progress = new UserModuleProgress(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getInt("module_id"),
                        rs.getBoolean("is_completed"),
                        completionDate,
                        score
                    );
                    progress.setModuleTitle(rs.getString("module_title"));
                    progress.setModuleNumber(rs.getInt("module_number"));
                    progresses.add(progress);
                }
            }
        }
        return progresses;
    }

    public int countCompletedModules(Integer userId, Integer courseId) throws SQLException {
        String sql = "SELECT COUNT(ump.id) FROM user_module_progress ump " +
                     "JOIN modules m ON ump.module_id = m.id " +
                     "WHERE ump.user_id = ? AND m.course_id = ? AND ump.is_completed = TRUE";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, courseId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }

    public void deleteAllUserModuleProgressForCourse(Integer userId, Integer courseId) {
       
        throw new UnsupportedOperationException("Unimplemented method 'deleteAllUserModuleProgressForCourse'");
    }
}