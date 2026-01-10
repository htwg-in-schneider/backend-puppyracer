package com.puppyracer.backend.repository;

import com.puppyracer.backend.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    
    // Bestehende Methode
    List<Review> findByProductId(Long productId);
    
    // Neue Methoden für bessere Abfragen
    List<Review> findByUserId(Long userId);
    
    List<Review> findByProductIdOrderByCreatedAtDesc(Long productId);
    
    // Prüfen ob User bereits Produkt bewertet hat
    boolean existsByProductIdAndUserId(Long productId, Long userId);
}