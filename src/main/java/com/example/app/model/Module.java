package com.example.app.model;

public class Module {
    private Integer id;
    private Integer courseId;
    private Integer moduleNumber;
    private String title;
    private String description;
    private String contentUrl; 
    private String moduleType; 
    private Integer durationMinutes; 

    public Module() {}

    public Module(Integer id, Integer courseId, Integer moduleNumber, String title, String description, String contentUrl, String moduleType, Integer durationMinutes) {
        this.id = id;
        this.courseId = courseId;
        this.moduleNumber = moduleNumber;
        this.title = title;
        this.description = description;
        this.contentUrl = contentUrl;
        this.moduleType = moduleType;
        this.durationMinutes = durationMinutes;
    }

    public Module(Integer courseId, Integer moduleNumber, String title, String description, String contentUrl, String moduleType, Integer durationMinutes) {
        this.courseId = courseId;
        this.moduleNumber = moduleNumber;
        this.title = title;
        this.description = description;
        this.contentUrl = contentUrl;
        this.moduleType = moduleType;
        this.durationMinutes = durationMinutes;
    }
    public Integer getId() { return id; }
    public Integer getCourseId() { return courseId; }
    public Integer getModuleNumber() { return moduleNumber; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getContentUrl() { return contentUrl; }
    public String getModuleType() { return moduleType; }
    public Integer getDurationMinutes() { return durationMinutes; }
    public void setId(Integer id) { this.id = id; }
    public void setCourseId(Integer courseId) { this.courseId = courseId; }
    public void setModuleNumber(Integer moduleNumber) { this.moduleNumber = moduleNumber; }
    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setContentUrl(String contentUrl) { this.contentUrl = contentUrl; }
    public void setModuleType(String moduleType) { this.moduleType = moduleType; }
    public void setDurationMinutes(Integer durationMinutes) { this.durationMinutes = durationMinutes; }
}