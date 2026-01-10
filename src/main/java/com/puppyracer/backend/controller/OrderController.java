package com.puppyracer.backend.controller;

import com.puppyracer.backend.model.Order;
import com.puppyracer.backend.model.User;
import com.puppyracer.backend.repository.OrderRepository;
import com.puppyracer.backend.repository.UserRepository;
import com.puppyracer.backend.model.Role;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    
    private static final Logger log = LoggerFactory.getLogger(OrderController.class);
    
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    
    public OrderController(OrderRepository orderRepository, UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
    }
    
    // ============ HELPER METHOD ============
    private boolean userFromJwtIsAdmin(Jwt jwt) {
        log.info("Checking if user is admin...");
        
        if (jwt == null) {
            log.error("JWT is null");
            return false;
        }
        
        String oauthId = jwt.getSubject();
        log.info("OAuth ID from JWT: {}", oauthId);
        
        if (oauthId == null) {
            log.error("OAuth ID is null");
            return false;
        }
        
        Optional<User> user = userRepository.findByOauthId(oauthId);
        
        if (user.isEmpty()) {
            log.error("User not found in DB for OAuth ID: {}", oauthId);
            return false;
        }
        
        boolean isAdmin = user.get().getRole() == Role.ADMIN;
        log.info("User role: {}, Is admin: {}", user.get().getRole(), isAdmin);
        
        return isAdmin;
    }
    
    // ===================== ADMIN ENDPOINTS =====================
    
    @GetMapping("/admin")
    public List<Order> getAllOrdersAdmin(@AuthenticationPrincipal Jwt jwt) {
        log.info("GET /api/orders/admin called");
        
        if (!userFromJwtIsAdmin(jwt)) {
            log.error("Access denied - User is not admin");
            throw new org.springframework.security.access.AccessDeniedException("Access denied");
        }
        
        List<Order> orders = orderRepository.findAllByOrderByOrderDateDesc();
        log.info("Returning {} orders for admin", orders.size());
        return orders;
    }
    
    @PutMapping("/admin/{id}/status")
    public Order updateOrderStatus(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long id,
            @RequestBody String newStatus) {
        
        log.info("PUT /api/orders/admin/{}/status called", id);
        
        if (!userFromJwtIsAdmin(jwt)) {
            log.error("Access denied - User is not admin");
            throw new org.springframework.security.access.AccessDeniedException("Access denied");
        }
        
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Order not found with ID: {}", id);
                    return new RuntimeException("Bestellung nicht gefunden");
                });
        
        log.info("Updating order {} status from {} to {}", id, order.getStatus(), newStatus);
        order.setStatus(newStatus);
        
        return orderRepository.save(order);
    }
    
    // ===================== USER ENDPOINTS =====================
    
    @GetMapping("/my-orders")
    public List<Order> getMyOrders(@AuthenticationPrincipal Jwt jwt) {
        log.info("GET /api/orders/my-orders called");
        
        String oauthId = jwt.getSubject();
        log.info("OAuth ID: {}", oauthId);
        
        User user = userRepository.findByOauthId(oauthId)
                .orElseThrow(() -> {
                    log.error("User not found for OAuth ID: {}", oauthId);
                    return new RuntimeException("User not found");
                });
        
        log.info("👤 Found user: {} (Role: {})", user.getEmail(), user.getRole());
        
        // WICHTIG: Diese Methode muss in OrderRepository existieren!
        // Falls nicht, ändere zu: return orderRepository.findByUser(user);
        List<Order> orders = orderRepository.findByUserOrderByOrderDateDesc(user);
        
        log.info("Returning {} orders for user {}", orders.size(), user.getEmail());
        return orders;
    }
}