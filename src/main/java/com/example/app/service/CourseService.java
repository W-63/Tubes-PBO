package com.example.app.service;

import com.example.app.dao.CourseDAO;
import com.example.app.model.Course;

import java.sql.SQLException;
import java.util.List;

public class CourseService {
    private final CourseDAO courseDAO = new CourseDAO();

    public void addCourse(Course course) throws Exception {
        validateCourse(course);
        courseDAO.addCourse(course);
    }

    public List<Course> getAllCourses() throws SQLException {
        return courseDAO.getAllCourses();
    }

    public Course getCourseById(Integer id) throws SQLException {
        return courseDAO.getCourseById(id);
    }

    public void deleteCourse(Integer id) throws SQLException {
        courseDAO.deleteCourse(id);
    }

    private void validateCourse(Course course) throws Exception {
        if (course.getTitle() == null || course.getTitle().trim().isEmpty()) {
            throw new Exception("Judul kelas tidak boleh kosong.");
        }
        if (course.getDescription() == null || course.getDescription().trim().isEmpty()) {
            throw new Exception("Deskripsi kelas tidak boleh kosong.");
        }
        if (course.getPrice() == null || course.getPrice() < 0) {
            throw new Exception("Harga kelas harus diisi dan tidak boleh negatif.");
        }
        if (course.getDurationWeeks() == null || course.getDurationWeeks() <= 0) {
            throw new Exception("Durasi kelas harus diisi dan lebih dari 0 minggu.");
        }
        if (course.getTotalModules() == null || course.getTotalModules() <= 0) {
            throw new Exception("Jumlah modul harus diisi dan lebih dari 0.");
        }
    }

    public void initializeDummyCourses() throws SQLException {
        courseDAO.initializeDummyCourses();
    }
}