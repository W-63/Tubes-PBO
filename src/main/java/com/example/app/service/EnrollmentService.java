package com.example.app.service;

import com.example.app.LoginController;
import com.example.app.dao.CourseDAO;
import com.example.app.dao.EnrollmentDAO;
import com.example.app.dao.UserModuleProgressDAO;
import com.example.app.model.Course;
import com.example.app.model.Enrollment;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class EnrollmentService {
    private final EnrollmentDAO enrollmentDAO = new EnrollmentDAO();
    private final CourseDAO courseDAO = new CourseDAO();
    private final ModuleService moduleService = new ModuleService();
    private final UserModuleProgressDAO userModuleProgressDAO = new UserModuleProgressDAO();

    public void enrollUserInCourse(Integer userId, Integer courseId) throws Exception {
        Optional<Enrollment> existingEnrollment = enrollmentDAO.getEnrollmentByUserIdAndCourseId(userId, courseId);

        if (existingEnrollment.isPresent()) {
            Enrollment enrollmentToReset = existingEnrollment.get();
            enrollmentToReset.setProgressPercentage(0);
            enrollmentToReset.setIsCompleted(false);
            enrollmentToReset.setEnrollmentDate(LocalDateTime.now());
            enrollmentDAO.updateEnrollmentProgress(enrollmentToReset.getId(), 0, false);

            userModuleProgressDAO.deleteAllUserModuleProgressForCourse(userId, courseId);

            throw new Exception("Anda sudah terdaftar di kelas ini. Progres Anda telah direset.");
        }

        Course course = courseDAO.getCourseById(courseId);
        if (course == null) {
            throw new Exception("Kelas tidak ditemukan.");
        }

        Enrollment newEnrollment = new Enrollment(userId, courseId, LocalDateTime.now());
        enrollmentDAO.addEnrollment(newEnrollment);
    }

    public List<Enrollment> getUserEnrollments(Integer userId) throws SQLException {
        return enrollmentDAO.getEnrollmentsByUserId(userId);
    }

    public void updateProgressBasedOnModules(Integer enrollmentId, Integer userId, Integer courseId) throws Exception {
        int totalModules = moduleService.getTotalModulesInCourse(courseId);
        int completedModules = moduleService.getCompletedModulesCount(userId, courseId);

        if (totalModules == 0) {
            enrollmentDAO.updateEnrollmentProgress(enrollmentId, 0, true);
            return;
        }

        int newProgressPercentage = (int) Math.round((double) completedModules / totalModules * 100);
        if (newProgressPercentage > 100) newProgressPercentage = 100;

        boolean isCompleted = (newProgressPercentage == 100);

        enrollmentDAO.updateEnrollmentProgress(enrollmentId, newProgressPercentage, isCompleted);
    }

    public List<Enrollment> getAllEnrollments() throws SQLException {
        return enrollmentDAO.getAllEnrollments();
    }

    public boolean isUserEnrolled(Integer userId, Integer courseId) throws SQLException {
        return enrollmentDAO.isUserEnrolled(userId, courseId);
    }

    public Map<String, Integer> getCourseProgressSummary(Integer userId) throws SQLException {
        return enrollmentDAO.getCourseProgressSummaryByUserId(userId);
    }

    // --- BARU: Metode ini yang diperlukan oleh CourseDetailForUserController ---
    public Optional<Enrollment> getEnrollmentByUserIdAndCourseId(Integer userId, Integer courseId) throws SQLException {
        return enrollmentDAO.getEnrollmentByUserIdAndCourseId(userId, courseId);
    }
}