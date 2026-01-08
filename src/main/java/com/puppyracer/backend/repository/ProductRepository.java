package com.puppyracer.backend.repository;

import com.puppyracer.backend.model.Product;
import com.puppyracer.backend.model.Category; // WICHTIG: Import hinzufügen
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // Bestehende Methode beibehalten
    List<Product> findByTitleContainingIgnoreCase(String title);

    // NEUE Methoden für Category Enum
    List<Product> findByCategory(Category category);
    
    List<Product> findByTitleContainingIgnoreCaseAndCategory(String title, Category category);
    
    // Option 1: Alte Methoden für Migration behalten (empfohlen)
    @Query("SELECT p FROM Product p WHERE LOWER(p.category) = LOWER(:category)")
    List<Product> findByCategoryIgnoreCase(@Param("category") String category);
    
    @Query("SELECT p FROM Product p WHERE " +
           "LOWER(p.title) LIKE LOWER(CONCAT('%', :title, '%')) AND " +
           "LOWER(p.category) = LOWER(:category)")
    List<Product> findByTitleContainingIgnoreCaseAndCategoryIgnoreCase(
        @Param("title") String title,
        @Param("category") String category
    );
    
    // Option 2: Alte Methoden entfernen und durch diese ersetzen
    // (Wenn du die alten Methoden nicht mehr brauchst)
    /*
    List<Product> findByCategoryIgnoreCase(String category);
    
    List<Product> findByTitleContainingIgnoreCaseAndCategoryIgnoreCase(String title, String category);
    */
}