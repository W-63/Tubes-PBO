package com.example.app.model;

public class Course {
    private Integer id;
    private String title;
    private String description;
    private Integer price;
    private Integer durationWeeks;
    private Integer totalModules;

    public Course() {}

    public Course(Integer id, String title, String description, Integer price, Integer durationWeeks, Integer totalModules) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.price = price;
        this.durationWeeks = durationWeeks;
        this.totalModules = totalModules;
    }

    public Course(String title, String description, Integer price, Integer durationWeeks, Integer totalModules) {
        this.title = title;
        this.description = description;
        this.price = price;
        this.durationWeeks = durationWeeks;
        this.totalModules = totalModules;
    }

    public Integer getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public Integer getPrice() { return price; }
    public Integer getDurationWeeks() { return durationWeeks; }
    public Integer getTotalModules() { return totalModules; }
    public void setId(Integer id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setPrice(Integer price) { this.price = price; }
    public void setDurationWeeks(Integer durationWeeks) { this.durationWeeks = durationWeeks; }
    public void setTotalModules(Integer totalModules) { this.totalModules = totalModules; }
}