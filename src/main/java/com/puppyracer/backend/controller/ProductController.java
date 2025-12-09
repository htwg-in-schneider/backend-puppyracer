package com.puppyracer.backend.controller;

import com.puppyracer.backend.model.Product;
import com.puppyracer.backend.model.Category; // Import hinzufügen
import com.puppyracer.backend.repository.ProductRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/product")
public class ProductController {

    private final ProductRepository productRepository;

    public ProductController(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @GetMapping
    public ResponseEntity<List<Product>> getProducts(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String category) {

        try {
            List<Product> products;
            
            if (name != null && !name.trim().isEmpty() && 
                category != null && !category.trim().isEmpty()) {
                // Beide Filter: Name UND Kategorie
                Category catEnum = Category.fromApiValue(category.toLowerCase());
                products = productRepository.findByTitleContainingIgnoreCaseAndCategory(
                    name, catEnum);
                    
            } else if (name != null && !name.trim().isEmpty()) {
                // Nur Name-Filter
                products = productRepository.findByTitleContainingIgnoreCase(name);
                
            } else if (category != null && !category.trim().isEmpty()) {
                // Nur Kategorie-Filter
                Category catEnum = Category.fromApiValue(category.toLowerCase());
                products = productRepository.findByCategory(catEnum);
                
            } else {
                // Keine Filter: Alle Produkte
                products = productRepository.findAll();
            }
            
            return ResponseEntity.ok(products);
            
        } catch (IllegalArgumentException e) {
            // Falls ungültige Kategorie angegeben wurde
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

    @PostMapping
    public ResponseEntity<Product> create(@RequestBody Product product) {
        // Optional: Validierung der Kategorie
        if (product.getCategory() == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.status(201).body(productRepository.save(product));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Product> update(@PathVariable Long id, @RequestBody Product product) {
        return productRepository.findById(id)
                .map(existing -> {
                    existing.setTitle(product.getTitle());
                    existing.setDescription(product.getDescription());
                    existing.setCategory(product.getCategory()); // Jetzt Category Enum
                    existing.setPrice(product.getPrice());
                    existing.setImageUrl(product.getImageUrl());
                    return ResponseEntity.ok(productRepository.save(existing));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        if (!productRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        productRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}