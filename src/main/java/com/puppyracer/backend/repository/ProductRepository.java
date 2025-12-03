package com.puppyracer.backend.repository;

import com.puppyracer.backend.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByTitleContainingIgnoreCase(String title);

    List<Product> findByCategoryIgnoreCase(String category);

    List<Product> findByTitleContainingIgnoreCaseAndCategoryIgnoreCase(String title, String category);
}