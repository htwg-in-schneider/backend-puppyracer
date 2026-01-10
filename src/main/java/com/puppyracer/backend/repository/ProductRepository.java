package com.puppyracer.backend.repository;

import com.puppyracer.backend.model.Product;
import com.puppyracer.backend.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // Suche nach Titel (Case-insensitive)
    List<Product> findByTitleContainingIgnoreCase(String title);
    
    // Suche nach Kategorie (Enum)
    List<Product> findByCategory(Category category);
    
    // Suche nach Titel UND Kategorie (Enum)
    List<Product> findByTitleContainingIgnoreCaseAndCategory(String title, Category category);
    
    // Optional: Für Kompatibilität, falls noch irgendwo String verwendet wird
    default List<Product> findByCategoryIgnoreCase(String category) {
        try {
            Category cat = Category.fromApiValue(category);
            return findByCategory(cat);
        } catch (IllegalArgumentException e) {
            return List.of(); // Leere Liste bei ungültiger Kategorie
        }
    }
}