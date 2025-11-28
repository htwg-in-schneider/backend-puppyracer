package main.java.model;

import jakarta.persistence.*;

@Entity
public class Review {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String author;
    private String text;

    @ManyToOne
    private Product product;

    // getter + setter

    public Long getId() {
        return id;
    }   
    public void setId(Long id) {
        this.id = id;
    }   
    public String getAuthor() {
        return author;
    }   
    public void setAuthor(String author) {
        this.author = author;   
    }
    public String getText() {
        return text;
    }   
    public void setText(String text) {
        this.text = text;   
    }
    public Product getProduct() {
        return product;
    }   
    public void setProduct(Product product) {
        this.product = product;     
    }

}