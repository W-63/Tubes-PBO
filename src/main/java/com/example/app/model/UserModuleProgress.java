package com.example.app.model;

import java.time.LocalDateTime;

public class UserModuleProgress {
    private Integer id;
    private Integer userId;
    private Integer moduleId;
    private Boolean isCompleted;
    private LocalDateTime completionDate;
    private Integer score; // For quizzes/assignments, can be null

    // Tambahan untuk tampilan di UI (join data)
    private String moduleTitle;
    private Integer moduleNumber;
    private String courseTitle; // Untuk tahu modul ini dari kelas mana

    public UserModuleProgress() {}

    public UserModuleProgress(Integer id, Integer userId, Integer moduleId, Boolean isCompleted, LocalDateTime completionDate, Integer score) {
        this.id = id;
        this.userId = userId;
        this.moduleId = moduleId;
        this.isCompleted = isCompleted;
        this.completionDate = completionDate;
        this.score = score;
    }

    // Constructor for new progress entry (without ID or completionDate/score)
    public UserModuleProgress(Integer userId, Integer moduleId) {
        this.userId = userId;
        this.moduleId = moduleId;
        this.isCompleted = false; // Default: not completed
        this.completionDate = null;
        this.score = null;
    }

    // Getters
    public Integer getId() { return id; }
    public Integer getUserId() { return userId; }
    public Integer getModuleId() { return moduleId; }
    public Boolean getIsCompleted() { return isCompleted; }
    public LocalDateTime getCompletionDate() { return completionDate; }
    public Integer getScore() { return score; }

    // Setters
    public void setId(Integer id) { this.id = id; }
    public void setUserId(Integer userId) { this.userId = userId; }
    public void setModuleId(Integer moduleId) { this.moduleId = moduleId; }
    public void setIsCompleted(Boolean completed) { isCompleted = completed; }
    public void setCompletionDate(LocalDateTime completionDate) { this.completionDate = completionDate; }
    public void setScore(Integer score) { this.score = score; }

    // Getters/Setters for UI display properties
    public String getModuleTitle() { return moduleTitle; }
    public void setModuleTitle(String moduleTitle) { this.moduleTitle = moduleTitle; }
    public Integer getModuleNumber() { return moduleNumber; }
    public void setModuleNumber(Integer moduleNumber) { this.moduleNumber = moduleNumber; }
    public String getCourseTitle() { return courseTitle; }
    public void setCourseTitle(String courseTitle) { this.courseTitle = courseTitle; }
}