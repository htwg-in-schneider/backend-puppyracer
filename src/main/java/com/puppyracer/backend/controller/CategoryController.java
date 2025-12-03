package com.puppyracer.backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/category")
public class CategoryController {
    @GetMapping
    public Map<String, String> getCategories() {
        // Kategorien passend zu deinen Produkten
        Map<String, String> categories = new LinkedHashMap<>();
        categories.put("spielzeug", "Spielzeug");
        categories.put("hundefutter", "Hundefutter");
        categories.put("hundeaccessoires", "Hundeaccessoires");
        return categories;
    }
}