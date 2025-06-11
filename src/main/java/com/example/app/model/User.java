package com.example.app.model;

import java.time.LocalDateTime; 

public class User {
    private int id;
    private String username;
    private Role role;
    private LocalDateTime lastLoginTime; 
    private boolean isLoggedIn;          

    public User(int id, String username, Role role) {
        this.id = id;
        this.username = username;
        this.role = role;
        this.lastLoginTime = null; 
        this.isLoggedIn = false;   
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public Role getRole() {
        return role;
    }

    public LocalDateTime getLastLoginTime() { 
        return lastLoginTime;
    }

    public boolean isLoggedIn() { 
        return isLoggedIn;
    }

    
    public void setId(int id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public void setLastLoginTime(LocalDateTime lastLoginTime) { 
        this.lastLoginTime = lastLoginTime;
    }

    public void setLoggedIn(boolean loggedIn) { 
        isLoggedIn = loggedIn;
    }

    @Override
    public String toString() {
        return "User{" +
               "id=" + id +
               ", username='" + username + '\'' +
               ", role=" + role +
               ", lastLoginTime=" + lastLoginTime +
               ", isLoggedIn=" + isLoggedIn +
               '}';
    }
}