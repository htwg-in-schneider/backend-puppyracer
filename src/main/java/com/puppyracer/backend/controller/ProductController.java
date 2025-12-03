package com.puppyracer.backend.controller;

import com.puppyracer.backend.model.Product;
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
    public List<Product> getProducts(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String category) {

        if (name != null && category != null) {
            return productRepository
                    .findByTitleContainingIgnoreCaseAndCategoryIgnoreCase(name, category);
        }

        if (name != null) {
            return productRepository.findByTitleContainingIgnoreCase(name);
        }

        if (category != null) {
            return productRepository.findByCategoryIgnoreCase(category);
        }

        return productRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getById(@PathVariable Long id) {
        return productRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Product> create(@RequestBody Product product) {
        return ResponseEntity.status(201).body(productRepository.save(product));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Product> update(@PathVariable Long id, @RequestBody Product product) {
        return productRepository.findById(id)
                .map(existing -> {
                    existing.setTitle(product.getTitle());
                    existing.setDescription(product.getDescription());
                    existing.setCategory(product.getCategory());
                    existing.setPrice(product.getPrice());
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