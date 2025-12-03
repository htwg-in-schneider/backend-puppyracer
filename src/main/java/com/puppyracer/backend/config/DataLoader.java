package com.puppyracer.backend.config;

import com.puppyracer.backend.model.Product;
import com.puppyracer.backend.model.Review;
import com.puppyracer.backend.repository.ProductRepository;
import com.puppyracer.backend.repository.ReviewRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.Arrays;

@Configuration
public class DataLoader {

    @Bean
    CommandLineRunner loadData(ProductRepository pRepo, ReviewRepository rRepo) {
        return args -> {
            if (pRepo.count() == 0) {

                // Produkt 1
                Product p1 = new Product();
                p1.setTitle("Spielzeughund");
                p1.setDescription("Niedlicher Welpe aus Stoff.");
                p1.setPrice(9.99);
                p1.setCategory("spielzeug");
                p1.setImageUrl("Cute_stuffed_Dog.jpg"); // Nur Dateiname
                pRepo.save(p1);

                // Produkt 2
                Product p2 = new Product();
                p2.setTitle("Hundespielzeug Ball");
                p2.setDescription("Robuster Spielball für Hunde");
                p2.setPrice(12.99);
                p2.setCategory("spielzeug");
                p2.setImageUrl("Dog_Ball.jpg");
                pRepo.save(p2);

                // Produkt 3
                Product p3 = new Product();
                p3.setTitle("Premium Hundefutter");
                p3.setDescription("Hochwertiges Trockenfutter, gut für die Zähne");
                p3.setPrice(24.99);
                p3.setCategory("hundefutter");
                p3.setImageUrl("Dog_Food.jpg");
                pRepo.save(p3);

                // Optional: Reviews für Produkt 1
                Review r = new Review();
                r.setUserName("Hans");
                r.setText("Super!");
                r.setStars(5);
                r.setProduct(p1);
                rRepo.save(r);
            }
        };
    }
}