package com.puppyracer.backend.config;

import com.puppyracer.backend.model.*;
import com.puppyracer.backend.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

@Configuration
public class DataLoader {

    @Bean
    CommandLineRunner initDatabase(ProductRepository productRepository, 
                                  UserRepository userRepository,
                                  OrderRepository orderRepository) { 
        return args -> {
            System.out.println("=== DATABASE INITIALIZATION ===");
            
            // 1. PRODUCTE LADEN
            loadProducts(productRepository);
            
            // 2. BENUTZER LADEN
            loadUsers(userRepository);
        
            // 3. TESTBESTELLUNGEN LADEN (KORRIGIERT!)
            loadTestOrders(productRepository, userRepository, orderRepository);
            
            System.out.println("=== DATABASE INITIALIZATION COMPLETE ===");
        };
    }
    
    private void loadProducts(ProductRepository productRepository) {
        if (productRepository.count() == 0) {
            System.out.println("Loading products...");
            
            // ===== LEINEN & GESCHIRRE =====
            Product leash1 = new Product();
            leash1.setTitle("Premium Lederleine");
            leash1.setDescription("Hochwertige handgefertigte Lederleine aus dunklem Leder für Hunde, 2m Länge");
            leash1.setPrice(34.99);
            leash1.setCategory(Category.LEINEN);
            leash1.setImageUrl("Hundeleine-dunklesLeder.png");
            productRepository.save(leash1);

            Product leash2 = new Product();
            leash2.setTitle("Flexi-Retractable Leine");
            leash2.setDescription("10m meter ausziehbare Leine mit ergonomischem Griff");
            leash2.setPrice(29.99);
            leash2.setCategory(Category.LEINEN);
            leash2.setImageUrl("Hundeleine-rot.png");
            productRepository.save(leash2);

            Product harness = new Product();
            harness.setTitle("Hundeleine aus Stoff");
            harness.setDescription("Schöne Stoffleine in rot/blau 2m Länge");
            harness.setPrice(42.99);
            harness.setCategory(Category.LEINEN);
            harness.setImageUrl("Hundeleine-Stoff.png");
            productRepository.save(harness);

            // ===== HALSBÄNDER & HALSKETTEN =====
            Product collar1 = new Product();
            collar1.setTitle("Hundehalsband Türkis");
            collar1.setDescription("Schönes Halsband in Türkis, wasserfest");
            collar1.setPrice(22.99);
            collar1.setCategory(Category.HALSBAENDER);
            collar1.setImageUrl("Hundehalsband-Türkis.png");
            productRepository.save(collar1);

            Product collar2 = new Product();
            collar2.setTitle("Lederhalsband mit Gravur");
            collar2.setDescription("Personalisiertes Lederhalsband, wahlweise mit Namen");
            collar2.setPrice(28.99);
            collar2.setCategory(Category.HALSBAENDER);
            collar2.setImageUrl("Hundehalsband-Leder.png");
            productRepository.save(collar2);

            Product necklace = new Product();
            necklace.setTitle("Louis Vuitton Halsband");
            necklace.setDescription("Stilvolles Hundehalsband von Louis Vuitton mit Gravur");
            necklace.setPrice(60.00);
            necklace.setCategory(Category.HALSBAENDER);
            necklace.setImageUrl("Hundehalsband-premium.png");
            productRepository.save(necklace);

            // ===== HUNDEBEKLEIDUNG =====
            Product jacket = new Product();
            jacket.setTitle("Warme Jacke in Blau");
            jacket.setDescription("Wasserdichte Jacke für den Herbst");
            jacket.setPrice(45.99);
            jacket.setCategory(Category.BEKLEIDUNG);
            jacket.setImageUrl("Hundejacke-Blau.png");
            productRepository.save(jacket);

            Product sweater = new Product();
            sweater.setTitle("Winterpullover für Hunde");
            sweater.setDescription("Kuscheliger Wollpullover für kalte Tage");
            sweater.setPrice(32.99);
            sweater.setCategory(Category.BEKLEIDUNG);
            sweater.setImageUrl("HundePulli.png");
            productRepository.save(sweater);

            Product hoodie = new Product();
            hoodie.setTitle("Regenjacke in schwarz");
            hoodie.setDescription("Schöne Regenjacke in Schwarz");
            hoodie.setPrice(38.99);
            hoodie.setCategory(Category.BEKLEIDUNG);
            hoodie.setImageUrl("Hundejacke-Schwarz.png");
            productRepository.save(hoodie);

            // ===== LECKERLIS & SNACKS =====
            Product snack1 = new Product();
            snack1.setTitle("Pedigree Premium Futter");
            snack1.setDescription("Hochwertiges Trockenfutter für ausgewogene Ernährung");
            snack1.setPrice(24.99);
            snack1.setCategory(Category.SNACKS);
            snack1.setImageUrl("Pedigree-Futter.png");
            productRepository.save(snack1);

            Product snack2 = new Product();
            snack2.setTitle("Active Gold Futter");
            snack2.setDescription("Energiereiches Futter für aktive Hunde");
            snack2.setPrice(27.99);
            snack2.setCategory(Category.SNACKS);
            snack2.setImageUrl("Activa-Gold-Futter.png");
            productRepository.save(snack2);

            Product snack3 = new Product();
            snack3.setTitle("Nutrima");
            snack3.setDescription("Bio Futter, reich an Vitaminen");
            snack3.setPrice(15.99);
            snack3.setCategory(Category.SNACKS);
            snack3.setImageUrl("Nutrima-Futter.png");
            productRepository.save(snack3);

            System.out.println(productRepository.count() + " Produkte geladen");
        } else {
            System.out.println("Products already exist, skipping...");
        }
    }
    
    private void loadUsers(UserRepository userRepository) {
        System.out.println("Loading users...");
        
        // KORREKTE AUTH0 IDs (aus deiner Datenbank)
        String adminOauthId = "auth0|6942b5a749c4d1b989dbf9ad";
        String buyerOauthId = "auth0|6942b6bd49c4d1b989dbfa45";
        
        // 1. ADMIN USER
        if (userRepository.findByOauthId(adminOauthId).isEmpty()) {
            User admin = new User();
            admin.setName("Admin Max");
            admin.setEmail("admin@puppyracer.com");
            admin.setOauthId(adminOauthId);
            admin.setRole(Role.ADMIN);
            userRepository.save(admin);
            System.out.println("Admin user created with ID: " + adminOauthId);
        } else {
            System.out.println("Admin user already exists: " + adminOauthId);
        }
        
        // 2. BUYER USER (Kunde)
        if (userRepository.findByOauthId(buyerOauthId).isEmpty()) {
            User buyer = new User();
            buyer.setName("Kunde Anna");
            buyer.setEmail("kunde@puppyracer.com");
            buyer.setOauthId(buyerOauthId);
            buyer.setRole(Role.BUYER);
            userRepository.save(buyer);
            System.out.println("Buyer user created with ID: " + buyerOauthId);
        } else {
            System.out.println("Buyer user already exists: " + buyerOauthId);
        }
        
        System.out.println("Total users in database: " + userRepository.count());
    }
    
    @Transactional
    private void loadTestOrders(ProductRepository productRepository, 
                               UserRepository userRepository,
                               OrderRepository orderRepository) {
        
        if (orderRepository.count() == 0) {
            System.out.println("Creating test orders...");
            
            // KORREKTE ID VERWENDEN (aus loadUsers())
            String buyerOauthId = "auth0|6942b6bd49c4d1b989dbfa45";
            
            System.out.println("Looking for buyer with oauthId: " + buyerOauthId);
            
            // 1. Benutzer finden
            var buyerOpt = userRepository.findByOauthId(buyerOauthId);
            var products = productRepository.findAll();
            
            System.out.println("Buyer found: " + buyerOpt.isPresent());
            System.out.println("Products found: " + products.size());
            
            if (buyerOpt.isPresent() && !products.isEmpty()) {
                User buyer = buyerOpt.get();
                System.out.println("Found buyer: " + buyer.getEmail() + " (ID: " + buyer.getId() + ")");
                
                // BESTELLUNG 1: Bezahlte Bestellung
                Order order1 = new Order();
                order1.setUser(buyer);
                order1.setFirstName("Anna");
                order1.setLastName("Schmidt");
                order1.setEmail("anna.schmidt@example.com");
                order1.setPhone("0151-12345678");
                order1.setStreet("Hauptstraße 123");
                order1.setZipCode("10115");
                order1.setCity("Berlin");
                order1.setCountry("Deutschland");
                order1.setPaymentMethod("PAYPAL");
                order1.setStatus("PAID");
                
                // Order Items für Bestellung 1
                OrderItem item1 = new OrderItem();
                item1.setProductId(products.get(0).getId());
                item1.setProductName(products.get(0).getTitle());
                item1.setProductImage(products.get(0).getImageUrl());
                item1.setPrice(products.get(0).getPrice());
                item1.setQuantity(2);
                order1.addItem(item1);
                
                OrderItem item2 = new OrderItem();
                item2.setProductId(products.get(1).getId());
                item2.setProductName(products.get(1).getTitle());
                item2.setProductImage(products.get(1).getImageUrl());
                item2.setPrice(products.get(1).getPrice());
                item2.setQuantity(1);
                order1.addItem(item2);
                
                // Preise berechnen
                double subtotal1 = item1.getPrice() * item1.getQuantity() + 
                                 item2.getPrice() * item2.getQuantity();
                order1.setSubtotal(subtotal1);
                order1.setShippingCost(4.99);
                order1.setTotalAmount(subtotal1 + 4.99);
                
                orderRepository.save(order1);
                System.out.println("Test order 1 created: " + order1.getOrderNumber());
                
                // BESTELLUNG 2: Ausstehende Bestellung
                Order order2 = new Order();
                order2.setUser(buyer);
                order2.setFirstName("Anna");
                order2.setLastName("Schmidt");
                order2.setEmail("anna.schmidt@example.com");
                order2.setPhone("0151-87654321");
                order2.setStreet("Musterweg 45");
                order2.setZipCode("20255");
                order2.setCity("Hamburg");
                order2.setCountry("Deutschland");
                order2.setPaymentMethod("CREDITCARD");
                order2.setStatus("PENDING");
                
                OrderItem item3 = new OrderItem();
                item3.setProductId(products.get(2).getId());
                item3.setProductName(products.get(2).getTitle());
                item3.setProductImage(products.get(2).getImageUrl());
                item3.setPrice(products.get(2).getPrice());
                item3.setQuantity(3);
                order2.addItem(item3);
                
                double subtotal2 = item3.getPrice() * item3.getQuantity();
                order2.setSubtotal(subtotal2);
                order2.setShippingCost(0.0);
                order2.setTotalAmount(subtotal2);
                
                orderRepository.save(order2);
                System.out.println("Test order 2 created: " + order2.getOrderNumber());
                
                // BESTELLUNG 3: Versendete Bestellung (für Admin-Test)
                Order order3 = new Order();
                order3.setUser(buyer);
                order3.setFirstName("Max");
                order3.setLastName("Mustermann");
                order3.setEmail("max.mustermann@example.com");
                order3.setPhone("0176-98765432");
                order3.setStreet("Teststraße 99");
                order3.setZipCode("80331");
                order3.setCity("München");
                order3.setCountry("Deutschland");
                order3.setPaymentMethod("INVOICE");
                order3.setStatus("SHIPPED");
                
                OrderItem item4 = new OrderItem();
                item4.setProductId(products.get(3).getId());
                item4.setProductName(products.get(3).getTitle());
                item4.setProductImage(products.get(3).getImageUrl());
                item4.setPrice(products.get(3).getPrice());
                item4.setQuantity(1);
                order3.addItem(item4);
                
                OrderItem item5 = new OrderItem();
                item5.setProductId(products.get(4).getId());
                item5.setProductName(products.get(4).getTitle());
                item5.setProductImage(products.get(4).getImageUrl());
                item5.setPrice(products.get(4).getPrice());
                item5.setQuantity(2);
                order3.addItem(item5);
                
                double subtotal3 = (item4.getPrice() * item4.getQuantity()) + 
                                  (item5.getPrice() * item5.getQuantity());
                order3.setSubtotal(subtotal3);
                order3.setShippingCost(5.99);
                order3.setTotalAmount(subtotal3 + 5.99);
                
                orderRepository.save(order3);
                System.out.println("Test order 3 created: " + order3.getOrderNumber());
                
                System.out.println("Total test orders created: " + orderRepository.count());
                
            } else {
                System.out.println("ERROR: Cannot create test orders!");
                if (!buyerOpt.isPresent()) {
                    System.out.println("Buyer not found with oauthId: " + buyerOauthId);
                }
                if (products.isEmpty()) {
                    System.out.println("No products available");
                }
            }
        } else {
            System.out.println("Orders already exist, skipping...");
            System.out.println("Current order count: " + orderRepository.count());
        }
    }
}