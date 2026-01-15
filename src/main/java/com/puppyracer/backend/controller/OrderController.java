package com.puppyracer.backend.controller;

import com.puppyracer.backend.model.Order;
import com.puppyracer.backend.model.OrderItem;
import com.puppyracer.backend.model.User;
import com.puppyracer.backend.model.Role;
import com.puppyracer.backend.repository.OrderRepository;
import com.puppyracer.backend.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
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
    
    
    @PostMapping
    public ResponseEntity<Order> createOrder(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody Order orderRequest) {

        log.info("POST /api/orders called");

        try {
            // 1. Authentifizierten User holen
            User user = getUserFromJwt(jwt);

            // 2. User in die Bestellung setzen
            orderRequest.setUser(user);

            // 3. Order-Items validieren und Order zuweisen
            if (orderRequest.getItems() != null) {
                for (OrderItem item : orderRequest.getItems()) {
                    item.setOrder(orderRequest);
                }
            }

            // 4. Status + Nummer + Datum setzen (falls nicht gesetzt)
            if (orderRequest.getOrderNumber() == null) {
                orderRequest.setOrderNumber("ORD-" + System.currentTimeMillis() + "-" + (int)(Math.random() * 1000));
            }
            if (orderRequest.getOrderDate() == null) {
                orderRequest.setOrderDate(LocalDateTime.now());
            }
            if (orderRequest.getStatus() == null) {
                orderRequest.setStatus("PENDING");
            }
            if (orderRequest.getCountry() == null || orderRequest.getCountry().isEmpty()) {
                orderRequest.setCountry("Deutschland");
            }
            
            // 5. Zahlungsmethode normalisieren (falls lowercase)
            if (orderRequest.getPaymentMethod() != null) {
                orderRequest.setPaymentMethod(orderRequest.getPaymentMethod().toUpperCase());
            }

            // 6. Speichern
            Order savedOrder = orderRepository.save(orderRequest);
            log.info("Order created: {} for user {}", savedOrder.getOrderNumber(), user.getEmail());

            return ResponseEntity.ok(savedOrder);

        } catch (Exception e) {
            log.error("Error creating order: {}", e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    
    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long id) {
        
        log.info("GET /api/orders/{} called", id);
        
        try {
            User user = getUserFromJwt(jwt);
            Order order = orderRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Bestellung nicht gefunden"));
            
            // Nur Admin oder eigener User darf Bestellung sehen
            if (!userFromJwtIsAdmin(jwt) && !order.getUser().getId().equals(user.getId())) {
                return ResponseEntity.status(403).build();
            }
            
            return ResponseEntity.ok(order);
            
        } catch (Exception e) {
            log.error("Error loading order: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/number/{orderNumber}")
    public ResponseEntity<Order> getOrderByNumber(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable String orderNumber) {
        
        log.info("GET /api/orders/number/{} called", orderNumber);
        
        try {
            User user = getUserFromJwt(jwt);
            Order order = orderRepository.findByOrderNumber(orderNumber);
            
            if (order == null) {
                return ResponseEntity.notFound().build();
            }
            
            // Nur Admin oder eigener User darf Bestellung sehen
            if (!userFromJwtIsAdmin(jwt) && !order.getUser().getId().equals(user.getId())) {
                return ResponseEntity.status(403).build();
            }
            
            return ResponseEntity.ok(order);
            
        } catch (Exception e) {
            log.error("Error loading order by number: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
    
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
    
    @GetMapping("/my-orders")
    public ResponseEntity<List<Order>> getMyOrders(@AuthenticationPrincipal Jwt jwt) {
        log.info("GET /api/orders/my-orders called");
        
        try {
            User user = getUserFromJwt(jwt);
            log.info("Found user: {} (Role: {})", user.getEmail(), user.getRole());
            
            List<Order> orders = orderRepository.findByUser(user);
            
            log.info("Returning {} orders for user {}", orders.size(), user.getEmail());
            return ResponseEntity.ok(orders);
            
        } catch (Exception e) {
            log.error("Error getting user orders: {}", e.getMessage());
            return ResponseEntity.status(403).build();
        }
    }

    
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