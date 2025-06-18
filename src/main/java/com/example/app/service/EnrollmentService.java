// Ini adalah versi EnrollmentService.java yang HARUS ANDA GUNAKAN
package com.example.app.service;

import com.example.app.LoginController;
import com.example.app.dao.CourseDAO;
import com.example.app.dao.EnrollmentDAO;
import com.example.app.model.Course;
import com.example.app.model.Enrollment;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class EnrollmentService {
    private final EnrollmentDAO enrollmentDAO = new EnrollmentDAO();
    private final CourseDAO courseDAO = new CourseDAO();
    private final ModuleService moduleService = new ModuleService(); // Untuk info modul

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

    /**
     * Memperbarui progres enrollment berdasarkan jumlah modul yang diselesaikan.
     * Metode ini akan dipanggil saat progres modul individual berubah.
     * @param enrollmentId ID pendaftaran kelas.
     * @param userId ID pengguna.
     * @param courseId ID kelas.
     * @throws Exception Jika pendaftaran atau kelas tidak ditemukan, atau error database.
     */
    public void updateProgressBasedOnModules(Integer enrollmentId, Integer userId, Integer courseId) throws Exception {
        int totalModules = moduleService.getTotalModulesInCourse(courseId);
        int completedModules = moduleService.getCompletedModulesCount(userId, courseId);

        if (totalModules == 0) {
            // Jika tidak ada modul, anggap 0% progres atau selesaikan jika tidak ada yang harus diselesaikan
            enrollmentDAO.updateEnrollmentProgress(enrollmentId, 0, true); // Anggap selesai jika tidak ada modul
            return;
        }

        int newProgressPercentage = (int) Math.round((double) completedModules / totalModules * 100);
        if (newProgressPercentage > 100) newProgressPercentage = 100; // Pastikan tidak lebih dari 100

        boolean isCompleted = (newProgressPercentage == 100);

        enrollmentDAO.updateEnrollmentProgress(enrollmentId, newProgressPercentage, isCompleted);
    }

    // Metode updateProgress sebelumnya yang menerima persentase langsung, harus dihapus
    // Jika Anda masih memiliki ini, hapus:
    /*
    public void updateProgress(Integer enrollmentId, Integer newProgressValue) throws Exception {
        // ... (kode lama) ...
    }
    */

    public List<Enrollment> getAllEnrollments() throws SQLException {
        return enrollmentDAO.getAllEnrollments();
    }

    public boolean isUserEnrolled(Integer userId, Integer courseId) throws SQLException {
        return enrollmentDAO.isUserEnrolled(userId, courseId);
    }

    public Map<String, Integer> getCourseProgressSummary(Integer userId) throws SQLException {
        return enrollmentDAO.getCourseProgressSummaryByUserId(userId);
    }
}