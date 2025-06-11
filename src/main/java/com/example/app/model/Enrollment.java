package com.example.app.model;

import java.time.LocalDateTime;

public class Enrollment {
    private Integer id;
    private Integer userId;
    private Integer courseId;
    private LocalDateTime enrollmentDate;
    private Integer progressPercentage; 
    private Boolean isCompleted;
    private String courseTitle; 
    private String username;   

    public Enrollment() {}

    public Enrollment(Integer id, Integer userId, Integer courseId, LocalDateTime enrollmentDate, Integer progressPercentage, Boolean isCompleted) {
        this.id = id;
        this.userId = userId;
        this.courseId = courseId;
        this.enrollmentDate = enrollmentDate;
        this.progressPercentage = progressPercentage;
        this.isCompleted = isCompleted;
    }

    public Enrollment(Integer userId, Integer courseId, LocalDateTime enrollmentDate) {
        this.userId = userId;
        this.courseId = courseId;
        this.enrollmentDate = enrollmentDate;
        this.progressPercentage = 0;
        this.isCompleted = false;    
    }

    public Integer getId() { return id; }
    public Integer getUserId() { return userId; }
    public Integer getCourseId() { return courseId; }
    public LocalDateTime getEnrollmentDate() { return enrollmentDate; }
    public Integer getProgressPercentage() { return progressPercentage; }
    public Boolean getIsCompleted() { return isCompleted; }
    public void setId(Integer id) { this.id = id; }
    public void setUserId(Integer userId) { this.userId = userId; }
    public void setCourseId(Integer courseId) { this.courseId = courseId; }
    public void setEnrollmentDate(LocalDateTime enrollmentDate) { this.enrollmentDate = enrollmentDate; }
    public void setProgressPercentage(Integer progressPercentage) { this.progressPercentage = progressPercentage; }
    public void setIsCompleted(Boolean completed) { isCompleted = completed; }
    public String getCourseTitle() { return courseTitle; }
    public void setCourseTitle(String courseTitle) { this.courseTitle = courseTitle; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
}