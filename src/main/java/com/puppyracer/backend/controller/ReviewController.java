package com.puppyracer.backend.controller;

import com.puppyracer.backend.model.Review;
import com.puppyracer.backend.repository.ReviewRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/review")
public class ReviewController {

    private final ReviewRepository reviewRepository;

    public ReviewController(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    @PostMapping
    public Review create(@RequestBody Review review) {
        return reviewRepository.save(review);
    }

    @GetMapping("/product/{productId}")
    public List<Review> getByProduct(@PathVariable Long productId) {
        return reviewRepository.findByProductId(productId);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        reviewRepository.deleteById(id);
    }
}