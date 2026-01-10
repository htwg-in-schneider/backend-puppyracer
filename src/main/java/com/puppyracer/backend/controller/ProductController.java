package com.puppyracer.backend.controller;

import com.puppyracer.backend.model.*;
import com.puppyracer.backend.repository.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.*;

@RestController
@RequestMapping("/api/product")
public class ProductController {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public ProductController(ProductRepository productRepository, 
                            UserRepository userRepository) {
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    // ========== HELPER METHOD ==========
    private boolean userFromJwtIsAdmin(Jwt jwt) {
        if (jwt == null || jwt.getSubject() == null) {
            return false;
        }
        Optional<User> user = userRepository.findByOauthId(jwt.getSubject());
        return user.isPresent() && user.get().getRole() == Role.ADMIN;
    }

    // ========== ÖFFENTLICHE ENDPUNKTE ==========

    @GetMapping
    public ResponseEntity<List<Product>> getProducts(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String category) {
        
        try {
            List<Product> products;
            
            if (name != null && !name.trim().isEmpty() && 
                category != null && !category.trim().isEmpty()) {
                // Suche nach Name UND Kategorie
                Category catEnum = Category.fromApiValue(category.toLowerCase());
                products = productRepository.findByTitleContainingIgnoreCaseAndCategory(name, catEnum);
                    
            } else if (name != null && !name.trim().isEmpty()) {
                // Suche nur nach Name
                products = productRepository.findByTitleContainingIgnoreCase(name);
                
            } else if (category != null && !category.trim().isEmpty()) {
                // Suche nur nach Kategorie
                Category catEnum = Category.fromApiValue(category.toLowerCase());
                products = productRepository.findByCategory(catEnum);
                
            } else {
                // Alle Produkte
                products = productRepository.findAll();
            }
            
            return ResponseEntity.ok(products);
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(null);
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Product> getById(@PathVariable Long id) {
        return productRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ========== ADMIN ENDPUNKTE ==========

    @PostMapping
    public ResponseEntity<?> createProduct(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody Product product,
            BindingResult result) {
        
        // 1. Admin-Prüfung
        if (!userFromJwtIsAdmin(jwt)) {
            return ResponseEntity.status(403).body(Map.of("error", "Nur Administratoren dürfen Produkte erstellen"));
        }
        
        // 2. Validierung
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body(getValidationErrors(result));
        }
        
        // 3. Zusätzliche Validierung
        if (product.getCategory() == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Kategorie ist erforderlich"));
        }
        
        try {
            Product savedProduct = productRepository.save(product);
            return ResponseEntity.status(201).body(savedProduct);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Fehler beim Speichern: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateProduct(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long id, 
            @Valid @RequestBody Product product,
            BindingResult result) {
        
        // 1. Admin-Prüfung
        if (!userFromJwtIsAdmin(jwt)) {
            return ResponseEntity.status(403).body(Map.of("error", "Nur Administratoren dürfen Produkte bearbeiten"));
        }
        
        // 2. Validierung
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body(getValidationErrors(result));
        }
        
        return productRepository.findById(id)
                .map(existing -> {
                    // 3. Nur erlaubte Felder aktualisieren
                    existing.setTitle(product.getTitle());
                    existing.setDescription(product.getDescription());
                    existing.setCategory(product.getCategory());
                    existing.setPrice(product.getPrice());
                    existing.setImageUrl(product.getImageUrl());
                    
                    Product saved = productRepository.save(existing);
                    return ResponseEntity.ok(saved);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long id) {
        
        // 1. Admin-Prüfung
        if (!userFromJwtIsAdmin(jwt)) {
            return ResponseEntity.status(403).body(Map.of("error", "Nur Administratoren dürfen Produkte löschen"));
        }
        
        // 2. Existenzprüfung
        if (!productRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        
        try {
            productRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Fehler beim Löschen: " + e.getMessage()));
        }
    }

    // ========== HILFSMETHODEN ==========

    private Map<String, String> getValidationErrors(BindingResult result) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError error : result.getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }
        return errors;
    }
}