package main.java.config;

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
                Product a = new Product();
                a.setTitle("Spielzeughund");
                a.setDescription("Niedlicher Welpe aus Stoff.");
                a.setPrice(9.99);
                a.setCategory("spielzeug");
                pRepo.save(a);

                Review r = new Review();
                r.setUserName("Hans");
                r.setText("Super!");
                r.setStars(5);
                r.setProduct(a);
                rRepo.save(r);
            }
        };
    }
}