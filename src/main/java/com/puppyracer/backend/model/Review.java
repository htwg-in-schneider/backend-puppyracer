package com.puppyracer.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

@Entity
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userName;
    private String text;
    private int stars;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    // Getter + Setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    public int getStars() { return stars; }
    public void setStars(int stars) { this.stars = stars; }

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }
}