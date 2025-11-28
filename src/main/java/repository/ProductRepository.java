package main.java.repository;

import com.puppyracer.backend.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByTitleContainingIgnoreCase(String title);
    List<Product> findByCategory(String category); // optional
    List<Product> findByTitleContainingIgnoreCaseAndCategory(String title, String category);
}