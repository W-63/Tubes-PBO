package com.example.app.service;

import com.example.app.dao.ModuleDAO;
import com.example.app.dao.UserModuleProgressDAO;
import com.example.app.model.Module;
import com.example.app.model.UserModuleProgress;
import com.example.app.LoginController; // Untuk mendapatkan ID user yang login

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

public class ModuleService {
    private final ModuleDAO moduleDAO = new ModuleDAO();
    private final UserModuleProgressDAO userModuleProgressDAO = new UserModuleProgressDAO();

    // Metode untuk Admin menambahkan modul (opsional, bisa juga via UI admin)
    public void addModule(Module module) throws Exception {
        validateModule(module);
        moduleDAO.addModule(module);
    }

    public List<Module> getModulesByCourseId(Integer courseId) throws SQLException {
        return moduleDAO.getModulesByCourseId(courseId);
    }

    public Module getModuleById(Integer moduleId) throws SQLException {
        return moduleDAO.getModuleById(moduleId);
    }

    public void deleteModule(Integer id) throws SQLException {
        moduleDAO.deleteModule(id);
    }

    // --- Metode untuk progres modul pengguna ---

    /**
     * Menandai modul sebagai selesai atau belum selesai untuk pengguna yang sedang login.
     * @param moduleId ID modul yang akan diupdate.
     * @param isCompleted Status selesai (true) atau belum (false).
     * @throws Exception Jika user tidak login atau terjadi error database.
     */
    public void updateModuleCompletionStatus(Integer moduleId, boolean isCompleted) throws Exception {
        if (LoginController.currentLoggedInUser == null) {
            throw new Exception("Pengguna tidak login. Tidak dapat memperbarui progres modul.");
        }
        Integer userId = LoginController.currentLoggedInUser.getId();

        UserModuleProgress progress = userModuleProgressDAO.getUserModuleProgress(userId, moduleId);
        if (progress == null) {
            // Jika belum ada entri progres, buat yang baru
            progress = new UserModuleProgress(userId, moduleId);
        }

        progress.setIsCompleted(isCompleted);
        progress.setCompletionDate(isCompleted ? LocalDateTime.now() : null); // Set tanggal selesai jika completed

        userModuleProgressDAO.addOrUpdateUserModuleProgress(progress);
    }

    /**
     * Mendapatkan progres modul seorang pengguna untuk kelas tertentu, termasuk status selesai.
     * @param userId ID pengguna.
     * @param courseId ID kelas.
     * @return List UserModuleProgress.
     * @throws SQLException Jika terjadi error database.
     */
    public List<UserModuleProgress> getUserModuleProgressByCourse(Integer userId, Integer courseId) throws SQLException {
        return userModuleProgressDAO.getUserProgressByCourseId(userId, courseId);
    }

    /**
     * Mendapatkan jumlah total modul dalam suatu kelas.
     * @param courseId ID kelas.
     * @return Jumlah total modul.
     * @throws SQLException Jika terjadi error database.
     */
    public int getTotalModulesInCourse(Integer courseId) throws SQLException {
        return moduleDAO.getTotalModulesInCourse(courseId);
    }

    /**
     * Mendapatkan jumlah modul yang telah diselesaikan oleh pengguna dalam suatu kelas.
     * @param userId ID pengguna.
     * @param courseId ID kelas.
     * @return Jumlah modul yang diselesaikan.
     * @throws SQLException Jika terjadi error database.
     */
    public int getCompletedModulesCount(Integer userId, Integer courseId) throws SQLException {
        return userModuleProgressDAO.countCompletedModules(userId, courseId);
    }

    private void validateModule(Module module) throws Exception {
        if (module.getCourseId() == null) {
            throw new Exception("Modul harus terkait dengan Kelas.");
        }
        if (module.getModuleNumber() == null || module.getModuleNumber() <= 0) {
            throw new Exception("Nomor modul tidak boleh kosong dan harus lebih dari 0.");
        }
        if (module.getTitle() == null || module.getTitle().trim().isEmpty()) {
            throw new Exception("Judul modul tidak boleh kosong.");
        }
        if (module.getModuleType() == null || module.getModuleType().trim().isEmpty()) {
            throw new Exception("Tipe modul tidak boleh kosong (misal: VIDEO, TEXT, QUIZ).");
        }
    }
}