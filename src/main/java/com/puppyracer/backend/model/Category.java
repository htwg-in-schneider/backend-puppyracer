package com.puppyracer.backend.model;

public enum Category {
    // WICHTIG: apiValue muss mit deinen Frontend-Werten übereinstimmen!
    LEINEN("leinen", "Leinen & Geschirre"),
    HALSBAENDER("halsbaender", "Halsbänder & Halsketten"),
    BEKLEIDUNG("bekleidung", "Hundebekleidung"),
    SNACKS("snacks", "Leckerlis & Snacks");
    
    private final String apiValue;
    private final String displayName;
    
    Category(String apiValue, String displayName) {
        this.apiValue = apiValue;
        this.displayName = displayName;
    }
    
    public String getApiValue() {
        return apiValue;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    // Konvertiert String zu Enum
    public static Category fromApiValue(String apiValue) {
        if (apiValue == null || apiValue.trim().isEmpty()) {
            return null;
        }
        
        for (Category cat : Category.values()) {
            if (cat.apiValue.equalsIgnoreCase(apiValue)) {
                return cat;
            }
        }
        
        throw new IllegalArgumentException("Unbekannte Kategorie: " + apiValue);
    }
}