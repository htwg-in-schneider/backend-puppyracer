package com.puppyracer.backend.controller;

import com.puppyracer.backend.model.Review;
import com.puppyracer.backend.model.User;
import com.puppyracer.backend.repository.ReviewRepository;
import com.puppyracer.backend.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/review")
public class ReviewController {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository; // Neu hinzufügen
    
    public ReviewController(ReviewRepository reviewRepository, UserRepository userRepository) {
        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository; // Neu
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()") // Nur authentifizierte User
    public ResponseEntity<Review> create(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody Review review) {
        
        // User aus JWT finden
        String oauthId = jwt.getSubject();
        Optional<User> userOpt = userRepository.findByOauthId(oauthId);
        
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(403).build();
        }
        
        // User zum Review hinzufügen
        review.setUser(userOpt.get());
        return ResponseEntity.ok(reviewRepository.save(review));
    }

    @GetMapping("/product/{productId}")
    public List<Review> getByProduct(@PathVariable Long productId) {
        // Öffentlich lesbar
        return reviewRepository.findByProductId(productId);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')") // Nur Admin kann löschen
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!reviewRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        reviewRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}