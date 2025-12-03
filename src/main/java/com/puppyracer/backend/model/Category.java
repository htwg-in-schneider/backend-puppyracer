package com.puppyracer.backend.model;

public enum Category {
    SPIELZEUG("Spielzeug"),
    ZUBEHOER("Zubehör");

    private final String displayName;

    Category(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}