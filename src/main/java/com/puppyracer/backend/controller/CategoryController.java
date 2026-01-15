package com.puppyracer.backend.controller;

import com.puppyracer.backend.model.Category;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/category")
public class CategoryController {
    
    @GetMapping
    public List<String> getCategories() {
        return Arrays.stream(Category.values())
                .map(Category::getApiValue)
                .collect(Collectors.toList());
    }
    
    @GetMapping("/with-display-names")
    public Map<String, String> getCategoriesWithDisplayNames() {
        Map<String, String> categories = new LinkedHashMap<>();
        categories.put("leinen", "Leinen & Geschirre");
        categories.put("halsbaender", "Halsbänder & Halsketten");
        categories.put("bekleidung", "Hundebekleidung");
        categories.put("snacks", "Leckerlis & Snacks");
        return categories;
    }
}
