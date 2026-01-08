package com.puppyracer.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.util.List;
import java.util.Objects;

@Entity
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Titel ist erforderlich")
    @Size(min = 3, max = 100, message = "Titel muss zwischen 3 und 100 Zeichen haben")
    @Column(nullable = false)
    private String title;

    @NotBlank(message = "Beschreibung ist erforderlich")
    @Size(min = 10, max = 1000, message = "Beschreibung muss zwischen 10 und 1000 Zeichen haben")
    @Column(nullable = false, length = 1000)
    private String description;

    @NotNull(message = "Preis ist erforderlich")
    @DecimalMin(value = "0.01", message = "Preis muss mindestens 0.01 € sein")
    @DecimalMax(value = "9999.99", message = "Preis darf maximal 9999.99 € sein")
    @Column(nullable = false)
    private Double price;
    
    @NotNull(message = "Kategorie ist erforderlich")
    @Enumerated(EnumType.STRING)
    private Category category;
    
    @NotBlank(message = "Bild-URL ist erforderlich")
    @Pattern(regexp = ".*\\.(png|jpg|jpeg|gif|webp)$", message = "Bild-URL muss mit .png, .jpg, .jpeg, .gif oder .webp enden")
    @Column(nullable = false)
    private String imageUrl;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Review> reviews;

    // Getter + Setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public List<Review> getReviews() { return reviews; }
    public void setReviews(List<Review> reviews) { this.reviews = reviews; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return id != null && id.equals(product.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", category=" + category +
                ", price=" + price +
                ", imageUrl='" + imageUrl + '\'' +
                '}';
    }
}