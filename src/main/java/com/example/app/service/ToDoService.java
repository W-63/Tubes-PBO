package com.example.app.service;

import com.example.app.dao.ToDoDAO;
import com.example.app.dao.ToDoDAOImpl;
import com.example.app.model.ToDoItem;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class ToDoService {
    private final ToDoDAO todoDAO = new ToDoDAOImpl();

    
    public void addToDo(ToDoItem todo) throws Exception, SQLException { 
        validateToDo(todo);
        todoDAO.addToDo(todo);
    }

    
    public void updateToDo(ToDoItem todo) throws Exception, SQLException { 
        validateToDo(todo);
        
    }

    
    public void updateStatus(int id, boolean status) throws SQLException {
        todoDAO.updateStatus(id, status);
    }

    public void deleteToDo(int id) throws SQLException {
        todoDAO.deleteToDo(id);
    }

    public List<ToDoItem> getAllToDos() throws SQLException {
        return todoDAO.getAllToDos();
    }

    private void validateToDo(ToDoItem todo) throws Exception {
        if (todo.getKategori() == null || todo.getKategori().trim().isEmpty()) {
            throw new Exception("Kategori tidak boleh kosong.");
        }
        if (todo.getDeskripsi() == null || todo.getDeskripsi().trim().isEmpty()) {
            throw new Exception("Deskripsi tidak boleh kosong.");
        }
        if (todo.getTanggal() == null) {
            throw new Exception("Tanggal harus diisi.");
        }
        if (todo.getWaktu() == null) {
            throw new Exception("Waktu harus diisi.");
        }
    }

    public Map<String, Integer> getToDoProgressSummary(Integer userId) throws SQLException {
        return todoDAO.getToDoProgressSummaryByUserId(userId);
    }

    public List<ToDoItem> getUpcomingOrOverdueToDos(int userId) throws SQLException { // <-- Tambahkan ini
        return todoDAO.getUpcomingOrOverdueToDos(userId);
    }
}