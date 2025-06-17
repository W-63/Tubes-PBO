package com.example.app.dao;

import com.example.app.model.ToDoItem;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public interface ToDoDAO {
    void addToDo(ToDoItem todo) throws SQLException;
    List<ToDoItem> getAllToDos() throws SQLException;
    void deleteToDo(int id) throws SQLException;
    void updateStatus(int id, boolean status) throws SQLException;
    Map<String, Integer> getToDoProgressSummaryByUserId(Integer userId) throws SQLException;
}