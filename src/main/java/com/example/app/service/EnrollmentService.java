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

    public void updateProgress(Integer enrollmentId, Integer currentModuleIndex) throws Exception {
        if (currentModuleIndex < 0) {
            throw new Exception("Indeks modul tidak boleh negatif.");
        }

        // Ambil enrollment yang ada
        Enrollment enrollment = null;
        // Ini membutuhkan metode getEnrollmentById di DAO jika tidak ada
        // Untuk demo, kita ambil dari list user dan cari
        List<Enrollment> userEnrollments = enrollmentDAO.getEnrollmentsByUserId(LoginController.currentLoggedInUser.getId());
        for(Enrollment e : userEnrollments){
            if(e.getId().equals(enrollmentId)){
                enrollment = e;
                break;
            }
        }

        if (enrollment == null) {
            throw new Exception("Pendaftaran kelas tidak ditemukan.");
        }

        // Ambil info kelas untuk total modul
        Course course = courseDAO.getCourseById(enrollment.getCourseId());
        if (course == null) {
            throw new Exception("Informasi kelas tidak ditemukan.");
        }

        int totalModules = course.getTotalModules();
        if (totalModules <= 0) {
            throw new Exception("Kelas tidak memiliki modul yang terdefinisi.");
        }

        // Hitung persentase baru
        int newProgress = (int) Math.round((double) currentModuleIndex / totalModules * 100);
        if (newProgress > 100) newProgress = 100;

        boolean isCompleted = (newProgress == 100);

        enrollmentDAO.updateEnrollmentProgress(enrollmentId, newProgress, isCompleted);
    }

    public List<Enrollment> getAllEnrollments() throws SQLException {
        return enrollmentDAO.getAllEnrollments();
    }

   public boolean isUserEnrolled(Integer userId, Integer courseId) throws SQLException {
        
        return enrollmentDAO.isUserEnrolled(userId, courseId);
    }
   

}