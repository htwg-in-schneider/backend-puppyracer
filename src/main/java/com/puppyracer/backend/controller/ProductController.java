package com.puppyracer.backend.controller;

import com.puppyracer.backend.model.*;
import com.puppyracer.backend.repository.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/product")
public class ProductController {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;

    public ProductController(ProductRepository productRepository, 
                            UserRepository userRepository,
                            OrderRepository orderRepository) {
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
    }

    private boolean userFromJwtIsAdmin(Jwt jwt) {
        if (jwt == null || jwt.getSubject() == null) {
            return false;
        }
        Optional<User> user = userRepository.findByOauthId(jwt.getSubject());
        if (!user.isPresent() || user.get().getRole() != Role.ADMIN) {
            return false;
        }
        return true;
    }

    // ========== PRODUKT-ENDPOINTS ==========

    @PostMapping
    public ResponseEntity<?> create(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody Product product,
            BindingResult result) {
        
        if (!userFromJwtIsAdmin(jwt)) {
            return ResponseEntity.status(403).build();
        }
        
        // VALIDIERUNG: Prüft automatisch alle @NotNull, @Size, etc. in Product.java
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body(getValidationErrors(result));
        }
        
        if (product.getCategory() == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Kategorie ist erforderlich"));
        }
        
        return ResponseEntity.status(201).body(productRepository.save(product));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long id, 
            @Valid @RequestBody Product product,
            BindingResult result) {
        
        if (!userFromJwtIsAdmin(jwt)) {
            return ResponseEntity.status(403).build();
        }
        
        // VALIDIERUNG
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body(getValidationErrors(result));
        }
        
        return productRepository.findById(id)
                .map(existing -> {
                    existing.setTitle(product.getTitle());
                    existing.setDescription(product.getDescription());
                    existing.setCategory(product.getCategory());
                    existing.setPrice(product.getPrice());
                    existing.setImageUrl(product.getImageUrl());
                    return ResponseEntity.ok(productRepository.save(existing));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long id) {
        
        if (!userFromJwtIsAdmin(jwt)) {
            return ResponseEntity.status(403).build();
        }
        
        if (!productRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        productRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<Product>> getProducts(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String category) {
        
        try {
            List<Product> products;
            
            if (name != null && !name.trim().isEmpty() && 
                category != null && !category.trim().isEmpty()) {
                Category catEnum = Category.fromApiValue(category.toLowerCase());
                products = productRepository.findByTitleContainingIgnoreCaseAndCategory(
                    name, catEnum);
                    
            } else if (name != null && !name.trim().isEmpty()) {
                products = productRepository.findByTitleContainingIgnoreCase(name);
                
            } else if (category != null && !category.trim().isEmpty()) {
                Category catEnum = Category.fromApiValue(category.toLowerCase());
                products = productRepository.findByCategory(catEnum);
                
            } else {
                products = productRepository.findAll();
            }
            
            return ResponseEntity.ok(products);
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(null);
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Product> getById(@PathVariable Long id) {
        return productRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ========== BESTELLUNGS-ENDPOINTS ==========

    @PostMapping("/checkout")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> checkout(
            @Valid @RequestBody CheckoutRequest checkoutRequest,
            BindingResult result,
            @AuthenticationPrincipal Jwt jwt) {
        
        // VALIDIERUNG: Prüft alle @Valid Annotationen in CheckoutRequest
        if (result.hasErrors()) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Validierungsfehler");
            errorResponse.put("errors", getValidationErrors(result));
            return ResponseEntity.badRequest().body(errorResponse);
        }
        
        try {
            String oauthId = jwt.getSubject();
            String email = jwt.getClaim("email");
            
            Optional<User> userOpt = userRepository.findByOauthId(oauthId);
            if (!userOpt.isPresent()) {
                Map<String, Object> error = new HashMap<>();
                error.put("success", false);
                error.put("message", "User nicht gefunden");
                return ResponseEntity.status(404).body(error);
            }
            
            User user = userOpt.get();
            
            // Manuelle Validierung der Items
            if (checkoutRequest.getItems() == null || checkoutRequest.getItems().isEmpty()) {
                Map<String, Object> error = new HashMap<>();
                error.put("success", false);
                error.put("message", "Warenkorb ist leer");
                return ResponseEntity.badRequest().body(error);
            }
            
            Order order = new Order();
            order.setUser(user);
            order.setOrderDate(LocalDateTime.now());
            order.setStatus("PENDING");
            
            order.setFirstName(checkoutRequest.getFirstName());
            order.setLastName(checkoutRequest.getLastName());
            order.setEmail(checkoutRequest.getEmail() != null ? checkoutRequest.getEmail() : email);
            order.setPhone(checkoutRequest.getPhone());
            
            order.setStreet(checkoutRequest.getStreet());
            order.setZipCode(checkoutRequest.getZipCode());
            order.setCity(checkoutRequest.getCity());
            order.setCountry(checkoutRequest.getCountry());
            
            order.setPaymentMethod(checkoutRequest.getPaymentMethod());
            order.setSubtotal(checkoutRequest.getSubtotal());
            order.setShippingCost(checkoutRequest.getShippingCost());
            order.setTotalAmount(checkoutRequest.getTotal());
            
            for (CheckoutItem item : checkoutRequest.getItems()) {
                OrderItem orderItem = new OrderItem();
                orderItem.setProductId(item.getId());
                orderItem.setProductName(item.getName());
                orderItem.setPrice(item.getPrice());
                orderItem.setQuantity(item.getQuantity());
                orderItem.setOrder(order);
                
                Optional<Product> productOpt = productRepository.findById(item.getId());
                if (productOpt.isPresent()) {
                    orderItem.setProductImage(productOpt.get().getImageUrl());
                }
                
                order.getItems().add(orderItem);
            }
            
            Order savedOrder = orderRepository.save(order);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("orderId", savedOrder.getId());
            response.put("orderNumber", savedOrder.getOrderNumber());
            response.put("total", savedOrder.getTotalAmount());
            response.put("message", "Bestellung erfolgreich gespeichert");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Fehler beim Checkout: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/user/orders")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Order>> getUserOrders(@AuthenticationPrincipal Jwt jwt) {
        try {
            String oauthId = jwt.getSubject();
            Optional<User> userOpt = userRepository.findByOauthId(oauthId);
            
            if (!userOpt.isPresent()) {
                return ResponseEntity.status(404).body(Collections.emptyList());
            }
            
            List<Order> orders = orderRepository.findByUserOrderByOrderDateDesc(userOpt.get());
            return ResponseEntity.ok(orders);
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Collections.emptyList());
        }
    }

    @GetMapping("/admin/orders")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Order>> getAllOrders(@AuthenticationPrincipal Jwt jwt) {
        if (!userFromJwtIsAdmin(jwt)) {
            return ResponseEntity.status(403).body(Collections.emptyList());
        }
        
        List<Order> orders = orderRepository.findAllByOrderByOrderDateDesc();
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/order/{orderId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Order> getOrderById(@PathVariable Long orderId, 
                                              @AuthenticationPrincipal Jwt jwt) {
        Optional<Order> orderOpt = orderRepository.findById(orderId);
        
        if (!orderOpt.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        
        Order order = orderOpt.get();
        String oauthId = jwt.getSubject();
        
        Optional<User> userOpt = userRepository.findByOauthId(oauthId);
        if (!userOpt.isPresent()) {
            return ResponseEntity.status(403).build();
        }
        
        User user = userOpt.get();
        if (!order.getUser().getId().equals(user.getId()) && user.getRole() != Role.ADMIN) {
            return ResponseEntity.status(403).build();
        }
        
        return ResponseEntity.ok(order);
    }

    // ========== HILFSMETHODEN ==========

    private Map<String, String> getValidationErrors(BindingResult result) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError error : result.getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }
        return errors;
    }

    // ========== INNERE KLASSEN ==========

    public static class CheckoutRequest {
        @NotBlank(message = "Vorname ist erforderlich")
        @Size(min = 2, max = 50, message = "Vorname muss zwischen 2 und 50 Zeichen haben")
        private String firstName;
        
        @NotBlank(message = "Nachname ist erforderlich")
        @Size(min = 2, max = 50, message = "Nachname muss zwischen 2 und 50 Zeichen haben")
        private String lastName;
        
        @Email(message = "Ungültige E-Mail-Adresse")
        private String email;
        
        @NotBlank(message = "Telefonnummer ist erforderlich")
        @Pattern(regexp = "^[+]?[0-9\\s\\-()]{10,20}$", 
                 message = "Ungültige Telefonnummer")
        private String phone;
        
        @NotBlank(message = "Straße ist erforderlich")
        @Size(min = 5, max = 100, message = "Straße muss zwischen 5 und 100 Zeichen haben")
        private String street;
        
        @NotBlank(message = "Postleitzahl ist erforderlich")
        @Pattern(regexp = "^[0-9]{5}$", message = "Postleitzahl muss 5-stellig sein")
        private String zipCode;
        
        @NotBlank(message = "Stadt ist erforderlich")
        @Size(min = 2, max = 50, message = "Stadt muss zwischen 2 und 50 Zeichen haben")
        private String city;
        
        @NotBlank(message = "Land ist erforderlich")
        @Size(min = 2, max = 50, message = "Land muss zwischen 2 und 50 Zeichen haben")
        private String country;
        
        @NotBlank(message = "Zahlungsmethode ist erforderlich")
        @Pattern(regexp = "PAYPAL|CREDITCARD|INVOICE|DEBIT", 
                 message = "Zahlungsmethode muss PAYPAL, CREDITCARD, INVOICE oder DEBIT sein")
        private String paymentMethod;
        
        @NotNull(message = "Zwischensumme ist erforderlich")
        @DecimalMin(value = "0.01", message = "Zwischensumme muss mindestens 0.01 € sein")
        private Double subtotal;
        
        @NotNull(message = "Versandkosten sind erforderlich")
        @DecimalMin(value = "0.00", message = "Versandkosten dürfen nicht negativ sein")
        private Double shippingCost;
        
        @NotNull(message = "Gesamtbetrag ist erforderlich")
        @DecimalMin(value = "0.01", message = "Gesamtbetrag muss mindestens 0.01 € sein")
        private Double total;
        
        @NotEmpty(message = "Warenkorb darf nicht leer sein")
        private List<CheckoutItem> items;

        // Getter & Setter
        public String getFirstName() { return firstName; }
        public void setFirstName(String firstName) { this.firstName = firstName; }
        
        public String getLastName() { return lastName; }
        public void setLastName(String lastName) { this.lastName = lastName; }
        
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        
        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }
        
        public String getStreet() { return street; }
        public void setStreet(String street) { this.street = street; }
        
        public String getZipCode() { return zipCode; }
        public void setZipCode(String zipCode) { this.zipCode = zipCode; }
        
        public String getCity() { return city; }
        public void setCity(String city) { this.city = city; }
        
        public String getCountry() { return country; }
        public void setCountry(String country) { this.country = country; }
        
        public String getPaymentMethod() { return paymentMethod; }
        public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
        
        public Double getSubtotal() { return subtotal; }
        public void setSubtotal(Double subtotal) { this.subtotal = subtotal; }
        
        public Double getShippingCost() { return shippingCost; }
        public void setShippingCost(Double shippingCost) { this.shippingCost = shippingCost; }
        
        public Double getTotal() { return total; }
        public void setTotal(Double total) { this.total = total; }
        
        public List<CheckoutItem> getItems() { return items; }
        public void setItems(List<CheckoutItem> items) { this.items = items; }
    }

    public static class CheckoutItem {
        @NotNull(message = "Produkt-ID ist erforderlich")
        private Long id;
        
        @NotBlank(message = "Produktname ist erforderlich")
        @Size(min = 3, max = 100, message = "Produktname muss zwischen 3 und 100 Zeichen haben")
        private String name;
        
        @NotNull(message = "Preis ist erforderlich")
        @DecimalMin(value = "0.01", message = "Preis muss mindestens 0.01 € sein")
        private Double price;
        
        @NotNull(message = "Menge ist erforderlich")
        @Min(value = 1, message = "Menge muss mindestens 1 sein")
        @Max(value = 99, message = "Menge darf maximal 99 sein")
        private Integer quantity;

        // Getter & Setter
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public Double getPrice() { return price; }
        public void setPrice(Double price) { this.price = price; }
        
        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }
    }
}