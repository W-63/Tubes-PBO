package com.example.app.service;

import com.example.app.LoginController;
import com.example.app.dao.CourseDAO;
import com.example.app.dao.EnrollmentDAO;
import com.example.app.model.Course;
import com.example.app.model.Enrollment;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

public class EnrollmentService {
    private final EnrollmentDAO enrollmentDAO = new EnrollmentDAO();
    private final CourseDAO courseDAO = new CourseDAO(); // Perlu untuk mengambil info kelas

    public void enrollUserInCourse(Integer userId, Integer courseId) throws Exception {
        if (enrollmentDAO.isUserEnrolled(userId, courseId)) {
            throw new Exception("Anda sudah terdaftar di kelas ini.");
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

    public void updateProgress(Integer enrollmentId, Integer newProgressValue) throws Exception {
    if (newProgressValue < 0 || newProgressValue > 100) {
        throw new Exception("Nilai progres harus antara 0 dan 100.");
    }

    Enrollment enrollment = enrollmentDAO.getEnrollmentById(enrollmentId); // <-- LEBIH EFISIEN

    if (enrollment == null) {
        throw new Exception("Pendaftaran kelas tidak ditemukan.");
    }

    boolean isCompleted = (newProgressValue == 100);
    enrollmentDAO.updateEnrollmentProgress(enrollmentId, newProgressValue, isCompleted);
}
    public List<Enrollment> getAllEnrollments() throws SQLException {
        return enrollmentDAO.getAllEnrollments();
    }

   public boolean isUserEnrolled(Integer userId, Integer courseId) throws SQLException {
        
        return enrollmentDAO.isUserEnrolled(userId, courseId);
    }
   

}