package com.puppyracer.backend.controller;

import com.puppyracer.backend.model.Order;
import com.puppyracer.backend.model.User;
import com.puppyracer.backend.repository.OrderRepository;
import com.puppyracer.backend.repository.UserRepository;
import com.puppyracer.backend.model.Role;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
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
        if (jwt == null) {
            return false;
        }
        
        String oauthId = jwt.getSubject();
        if (oauthId == null) {
            return false;
        }
        
        Optional<User> user = userRepository.findByOauthId(oauthId);
        return user.isPresent() && user.get().getRole() == Role.ADMIN;
    }
    
    private User getUserFromJwt(Jwt jwt) {
        String oauthId = jwt.getSubject();
        return userRepository.findByOauthId(oauthId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
    
    // ===================== ADMIN ENDPOINTS =====================
    
    @GetMapping("/admin")
    public ResponseEntity<List<Order>> getAllOrdersAdmin(@AuthenticationPrincipal Jwt jwt) {
        log.info("GET /api/orders/admin called");
        
        if (!userFromJwtIsAdmin(jwt)) {
            log.error("Access denied - User is not admin");
            return ResponseEntity.status(403).build();
        }
        
        List<Order> orders = orderRepository.findAll();
        log.info("Returning {} orders for admin", orders.size());
        return ResponseEntity.ok(orders);
    }
    
    @PutMapping("/{id}/status")
    public ResponseEntity<Order> updateOrderStatus(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long id,
            @RequestBody StatusUpdateRequest statusUpdate) {
        
        log.info("PUT /api/orders/{}/status called", id);
        
        if (!userFromJwtIsAdmin(jwt)) {
            log.error("Access denied - User is not admin");
            return ResponseEntity.status(403).build();
        }
        
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Bestellung nicht gefunden"));
        
        order.setStatus(statusUpdate.getStatus());
        Order savedOrder = orderRepository.save(order);
        
        return ResponseEntity.ok(savedOrder);
    }
    
    // ===================== USER ENDPOINTS =====================
    
    @GetMapping("/my-orders")
    public ResponseEntity<List<Order>> getMyOrders(@AuthenticationPrincipal Jwt jwt) {
        log.info("GET /api/orders/my-orders called");
        
        try {
            User user = getUserFromJwt(jwt);
            log.info("Found user: {} (Role: {})", user.getEmail(), user.getRole());
            
            // WICHTIG: Verwendet die findByUser Methode aus dem Repository
            List<Order> orders = orderRepository.findByUser(user);
            
            log.info("Returning {} orders for user {}", orders.size(), user.getEmail());
            return ResponseEntity.ok(orders);
            
        } catch (Exception e) {
            log.error("Error getting user orders: {}", e.getMessage());
            return ResponseEntity.status(403).build();
        }
    }
    
    // ===================== DTO =====================
    
    public static class StatusUpdateRequest {
        private String status;
        
        public String getStatus() {
            return status;
        }
        
        public void setStatus(String status) {
            this.status = status;
        }
    }
}