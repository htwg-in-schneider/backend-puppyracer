package com.puppyracer.backend.model;

import jakarta.persistence.*;
import java.util.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "app_user")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"}) // ← WICHTIG für JSON Serialisierung
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    @Column(nullable = false)
    private String email;
    
    @Column(nullable = false)
    private String name;
    
    @Column(unique = true, nullable = false)
    private String oauthId;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;
    
    // ==== PROFILE FIELDS ====
    @Column(length = 20)
    private String phone;
    
    @Column(length = 255)
    private String address;
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore // ← VERSTECKT Orders komplett in JSON
    private List<Order> orders = new ArrayList<>();
    
    // ===== KONSTRUKTOREN =====
    public User() {}
    
    public User(String email, String name, String oauthId, Role role) {
        this.email = email;
        this.name = name;
        this.oauthId = oauthId;
        this.role = role;
    }
    
    // Vollständiger Konstruktor
    public User(String email, String name, String oauthId, Role role, String phone, String address) {
        this.email = email;
        this.name = name;
        this.oauthId = oauthId;
        this.role = role;
        this.phone = phone;
        this.address = address;
    }
    
    // ===== GETTER & SETTER =====
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getOauthId() { return oauthId; }
    public void setOauthId(String oauthId) { this.oauthId = oauthId; }
    
    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
    
    // ==== PROFILE GETTER & SETTER ====
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    
    public List<Order> getOrders() { return orders; }
    public void setOrders(List<Order> orders) { this.orders = orders; }
    
    // ===== HELPER METHODS =====
    public void addOrder(Order order) {
        orders.add(order);
        order.setUser(this);
    }
    
    public boolean isAdmin() {
        return Role.ADMIN.equals(this.role);
    }
    
    // ===== EQUALS & HASHCODE =====
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id) && 
               Objects.equals(oauthId, user.oauthId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id, oauthId);
    }
    
    // ===== TO STRING =====
    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", role=" + role +
                '}';
    }
}