package com.puppyracer.backend.config;

import com.puppyracer.backend.model.Product;
import com.puppyracer.backend.model.Review;
import com.puppyracer.backend.model.Category; // WICHTIG: Import hinzufügen
import com.puppyracer.backend.repository.ProductRepository;
import com.puppyracer.backend.repository.ReviewRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataLoader {

    @Bean
    CommandLineRunner loadData(ProductRepository pRepo, ReviewRepository rRepo) {
        return args -> {
            if (pRepo.count() == 0) {
                System.out.println("Lade Testdaten...");

                // ===== LEINEN & GESCHIRRE =====
                Product leash1 = new Product();
                leash1.setTitle("Premium Lederleine");
                leash1.setDescription("Hochwertige handgefertigte Lederleine für Hunde, 2m Länge");
                leash1.setPrice(34.99);
                leash1.setCategory(Category.LEINEN); // ENUM verwenden!
                leash1.setImageUrl("leather-leash.jpg");
                pRepo.save(leash1);

                Product leash2 = new Product();
                leash2.setTitle("Flexi-Retractable Leine");
                leash2.setDescription("5m ausziehbare Leine mit ergonomischem Griff");
                leash2.setPrice(29.99);
                leash2.setCategory(Category.LEINEN);
                leash2.setImageUrl("flexi-leash.jpg");
                pRepo.save(leash2);

                Product harness = new Product();
                harness.setTitle("Geschirr mit Brustpolster");
                harness.setDescription("Bequemes Geschirr mit Brustpolster, verstellbar");
                harness.setPrice(42.99);
                harness.setCategory(Category.LEINEN);
                harness.setImageUrl("dog-harness.jpg");
                pRepo.save(harness);

                // ===== HALSBÄNDER & HALSKETTEN =====
                Product collar1 = new Product();
                collar1.setTitle("Reflektor Sicherheitshalsband");
                collar1.setDescription("Für sichere Abendspaziergänge, wasserfest");
                collar1.setPrice(22.99);
                collar1.setCategory(Category.HALSBAENDER);
                collar1.setImageUrl("reflective-collar.jpg");
                pRepo.save(collar1);

                Product collar2 = new Product();
                collar2.setTitle("Lederhalsband mit Gravur");
                collar2.setDescription("Personalisiertes Lederhalsband, wahlweise mit Namen");
                collar2.setPrice(28.99);
                collar2.setCategory(Category.HALSBAENDER);
                collar2.setImageUrl("leather-collar.jpg");
                pRepo.save(collar2);

                Product necklace = new Product();
                necklace.setTitle("Halskette mit Charme");
                necklace.setDescription("Stilvolle Halskette mit Herz-Anhänger, nickel-frei");
                necklace.setPrice(19.99);
                necklace.setCategory(Category.HALSBAENDER);
                necklace.setImageUrl("dog-necklace.jpg");
                pRepo.save(necklace);

                // ===== HUNDEBEKLEIDUNG =====
                Product jacket = new Product();
                jacket.setTitle("Regenjacke für Hunde");
                jacket.setDescription("Wasserdichte Jacke mit Reflektorstreifen, Größe M");
                jacket.setPrice(45.99);
                jacket.setCategory(Category.BEKLEIDUNG);
                jacket.setImageUrl("rain-jacket.jpg");
                pRepo.save(jacket);

                Product sweater = new Product();
                sweater.setTitle("Winterpullover für Hunde");
                sweater.setDescription("Kuscheliger Wollpullover für kalte Tage");
                sweater.setPrice(32.99);
                sweater.setCategory(Category.BEKLEIDUNG);
                sweater.setImageUrl("dog-sweater.jpg");
                pRepo.save(sweater);

                Product hoodie = new Product();
                hoodie.setTitle("Kapuzenpullover mit Taschen");
                hoodie.setDescription("Stylisher Hoodie mit praktischen Seitentaschen");
                hoodie.setPrice(38.99);
                hoodie.setCategory(Category.BEKLEIDUNG);
                hoodie.setImageUrl("dog-hoodie.jpg");
                pRepo.save(hoodie);

                // ===== LECKERLIS & SNACKS =====
                Product snack1 = new Product();
                snack1.setTitle("Rinderhaut-Kausnack");
                snack1.setDescription("Langanhaltender Kauartikel für Zahnpflege");
                snack1.setPrice(12.99);
                snack1.setCategory(Category.SNACKS);
                snack1.setImageUrl("beef-snack.jpg");
                pRepo.save(snack1);

                Product snack2 = new Product();
                snack2.setTitle("Trainingsleckerlis Huhn");
                snack2.setDescription("Kleine, kalorienarme Belohnungen fürs Training");
                snack2.setPrice(8.99);
                snack2.setCategory(Category.SNACKS);
                snack2.setImageUrl("training-treats.jpg");
                pRepo.save(snack2);

                Product snack3 = new Product();
                snack3.setTitle("Zahnpflege-Kaustange");
                snack3.setDescription("Reduziert Zahnstein und fördert Zahngesundheit");
                snack3.setPrice(15.99);
                snack3.setCategory(Category.SNACKS);
                snack3.setImageUrl("dental-stick.jpg");
                pRepo.save(snack3);

                // ===== REVIEWS =====
                Review r1 = new Review();
                r1.setUserName("Max");
                r1.setText("Super Leine, sehr robust!");
                r1.setStars(5);
                r1.setProduct(leash1);
                rRepo.save(r1);

                Review r2 = new Review();
                r2.setUserName("Lisa");
                r2.setText("Mein Hund liebt die Kausnacks!");
                r2.setStars(4);
                r2.setProduct(snack1);
                rRepo.save(r2);

                Review r3 = new Review();
                r3.setUserName("Thomas");
                r3.setText("Perfekte Passform, tolle Qualität");
                r3.setStars(5);
                r3.setProduct(jacket);
                rRepo.save(r3);

                System.out.println("Testdaten erfolgreich geladen!");
            }
        };
    }
}