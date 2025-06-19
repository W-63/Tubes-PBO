package com.example.app.model;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import java.time.LocalDateTime;

public class ModuleDisplayWrapper {
    private final SimpleIntegerProperty id;
    private final SimpleIntegerProperty courseId;
    private final SimpleIntegerProperty moduleNumber;
    private final SimpleStringProperty title;
    private final SimpleStringProperty description;
    private final SimpleStringProperty contentUrl;
    private final SimpleStringProperty moduleType;
    private final SimpleIntegerProperty durationMinutes;
    private final SimpleBooleanProperty completedForUser; // Status selesai untuk user yang sedang login
    private LocalDateTime completionDate; // Tanggal selesai (dari UserModuleProgress)

    public ModuleDisplayWrapper(Module module, UserModuleProgress userProgress) {
        this.id = new SimpleIntegerProperty(module.getId());
        this.courseId = new SimpleIntegerProperty(module.getCourseId());
        this.moduleNumber = new SimpleIntegerProperty(module.getModuleNumber());
        this.title = new SimpleStringProperty(module.getTitle());
        this.description = new SimpleStringProperty(module.getDescription());
        this.contentUrl = new SimpleStringProperty(module.getContentUrl());
        this.moduleType = new SimpleStringProperty(module.getModuleType());
        this.durationMinutes = new SimpleIntegerProperty(module.getDurationMinutes());
        // Inisialisasi status completedForUser berdasarkan userProgress
        this.completedForUser = new SimpleBooleanProperty(userProgress != null && userProgress.getIsCompleted());
        this.completionDate = userProgress != null ? userProgress.getCompletionDate() : null;
    }

    // Getters untuk properti JavaFX (penting untuk PropertyValueFactory)
    public SimpleIntegerProperty idProperty() { return id; }
    public SimpleIntegerProperty courseIdProperty() { return courseId; }
    public SimpleIntegerProperty moduleNumberProperty() { return moduleNumber; }
    public SimpleStringProperty titleProperty() { return title; }
    public SimpleStringProperty descriptionProperty() { return description; }
    public SimpleStringProperty contentUrlProperty() { return contentUrl; }
    public SimpleStringProperty moduleTypeProperty() { return moduleType; }
    public SimpleIntegerProperty durationMinutesProperty() { return durationMinutes; }
    public SimpleBooleanProperty completedForUserProperty() { return completedForUser; } // Ini yang digunakan oleh CheckBoxTableCell

    // Getters untuk nilai primitif
    public Integer getId() { return id.get(); }
    public Integer getCourseId() { return courseId.get(); }
    public Integer getModuleNumber() { return moduleNumber.get(); }
    public String getTitle() { return title.get(); }
    public String getDescription() { return description.get(); }
    public String getContentUrl() { return contentUrl.get(); }
    public String getModuleType() { return moduleType.get(); }
    public Integer getDurationMinutes() { return durationMinutes.get(); }
    public Boolean getCompletedForUser() { return completedForUser.get(); }
    public LocalDateTime getCompletionDate() { return completionDate; }

    // Setters (jika ada yang perlu diubah dari UI, terutama untuk SimpleBooleanProperty)
    public void setId(int id) { this.id.set(id); }
    public void setCourseId(int courseId) { this.courseId.set(courseId); }
    public void setModuleNumber(int moduleNumber) { this.moduleNumber.set(moduleNumber); }
    public void setTitle(String title) { this.title.set(title); }
    public void setDescription(String description) { this.description.set(description); }
    public void setContentUrl(String contentUrl) { this.contentUrl.set(contentUrl); }
    public void setModuleType(String moduleType) { this.moduleType.set(moduleType); }
    public void setDurationMinutes(int durationMinutes) { this.durationMinutes.set(durationMinutes); }
    public void setCompletedForUser(boolean completedForUser) { this.completedForUser.set(completedForUser); } // Setter penting untuk update dari UI
    public void setCompletionDate(LocalDateTime completionDate) { this.completionDate = completionDate; }
}