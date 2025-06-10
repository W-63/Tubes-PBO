package com.example.app.model;

public enum Role {
    ADMIN,
    USER;

    // Helper untuk konversi string ke enum (case-insensitive)
    public static Role fromString(String text) {
        if (text != null) {
            for (Role r : Role.values()) {
                if (text.equalsIgnoreCase(r.name())) {
                    return r;
                }
            }
        }
        return USER; // Default ke USER jika tidak ditemukan atau null
    }
}
