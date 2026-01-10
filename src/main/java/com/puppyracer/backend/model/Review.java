package com.puppyracer.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Bewertung ist erforderlich")
    @Min(value = 1, message = "Bewertung muss mindestens 1 Stern sein")
    @Max(value = 5, message = "Bewertung darf maximal 5 Sterne sein")
    private int stars;

    @NotBlank(message = "Text ist erforderlich")
    @Size(min = 10, max = 500, message = "Text muss zwischen 10 und 500 Zeichen haben")
    private String text;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    @JsonIgnoreProperties({"reviews"}) // Verhindert zirkuläre Referenzen
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnoreProperties({"reviews"})
    private User user;

    // Getter + Setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public int getStars() { return stars; }
    public void setStars(int stars) { this.stars = stars; }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    // Hilfsmethode für den Username
    public String getUserName() {
        return user != null ? user.getName() : null;
    }
}