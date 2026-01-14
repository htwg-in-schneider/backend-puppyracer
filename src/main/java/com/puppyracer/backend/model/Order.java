package com.puppyracer.backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Order {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String orderNumber;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnoreProperties({"orders", "oauthId"})
    @NotNull(message = "Benutzer ist erforderlich")
    private User user;
    
    private LocalDateTime orderDate;
    
    @NotNull(message = "Zwischensumme ist erforderlich")
    @DecimalMin(value = "0.01", message = "Zwischensumme muss mindestens 0.01 € sein")
    private Double subtotal;
    
    @NotNull(message = "Versandkosten sind erforderlich")
    @DecimalMin(value = "0.00", message = "Versandkosten dürfen nicht negativ sein")
    private Double shippingCost;
    
    @NotNull(message = "Gesamtbetrag ist erforderlich")
    @DecimalMin(value = "0.01", message = "Gesamtbetrag muss mindestens 0.01 € sein")
    private Double totalAmount;
    
    @NotBlank(message = "Status ist erforderlich")
    private String status = "PENDING"; // Standardwert
    
    // Persönliche Daten
    @NotBlank(message = "Vorname ist erforderlich")
    @Size(min = 2, max = 50, message = "Vorname muss zwischen 2 und 50 Zeichen haben")
    private String firstName;
    
    @NotBlank(message = "Nachname ist erforderlich")
    @Size(min = 2, max = 50, message = "Nachname muss zwischen 2 und 50 Zeichen haben")
    private String lastName;
    
    @NotBlank(message = "E-Mail ist erforderlich")
    @Email(message = "Ungültige E-Mail-Adresse")
    private String email;
    
    private String phone = ""; // Optional
    
    // Lieferadresse
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
    private String country = "Deutschland"; // Standardwert
    
    // Zahlungsmethode - VEREINFACHT
    @NotBlank(message = "Zahlungsmethode ist erforderlich")
    private String paymentMethod = "INVOICE"; // Standardwert Rechnung
    
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"order"})
    @NotEmpty(message = "Bestellung muss mindestens einen Artikel enthalten")
    private List<OrderItem> items = new ArrayList<>();
    
    // Konstruktoren
    public Order() {
        this.orderDate = LocalDateTime.now();
        this.status = "PENDING";
        this.orderNumber = "ORD-" + System.currentTimeMillis() + "-" + (int)(Math.random() * 1000);
        this.country = "Deutschland";
        this.paymentMethod = "INVOICE";
    }
    
    // Getter & Setter (alle vorhanden, gleiche wie vorher)
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getOrderNumber() { return orderNumber; }
    public void setOrderNumber(String orderNumber) { this.orderNumber = orderNumber; }
    
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    
    public LocalDateTime getOrderDate() { return orderDate; }
    public void setOrderDate(LocalDateTime orderDate) { this.orderDate = orderDate; }
    
    public Double getSubtotal() { return subtotal; }
    public void setSubtotal(Double subtotal) { this.subtotal = subtotal; }
    
    public Double getShippingCost() { return shippingCost; }
    public void setShippingCost(Double shippingCost) { this.shippingCost = shippingCost; }
    
    public Double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(Double totalAmount) { this.totalAmount = totalAmount; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
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
    public void setPaymentMethod(String paymentMethod) { 
        // Akzeptiere verschiedene Schreibweisen, konvertiere zu uppercase
        if (paymentMethod != null) {
            this.paymentMethod = paymentMethod.toUpperCase();
        }
    }
    
    public List<OrderItem> getItems() { return items; }
    public void setItems(List<OrderItem> items) { this.items = items; }
    
    // Hilfsmethoden
    public void addItem(OrderItem item) {
        items.add(item);
        item.setOrder(this);
    }
    
    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", orderNumber='" + orderNumber + '\'' +
                ", status='" + status + '\'' +
                ", totalAmount=" + totalAmount +
                ", orderDate=" + orderDate +
                '}';
    }
}