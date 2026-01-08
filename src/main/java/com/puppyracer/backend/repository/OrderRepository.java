package com.puppyracer.backend.repository;

import com.puppyracer.backend.model.Order;
import com.puppyracer.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    
    // Alle Bestellungen eines Users, sortiert nach Datum (neueste zuerst)
    List<Order> findByUserOrderByOrderDateDesc(User user);
    
    // Alle Bestellungen, sortiert nach Datum (neueste zuerst)
    List<Order> findAllByOrderByOrderDateDesc();
    
    // Bestellung anhand der Bestellnummer finden
    Order findByOrderNumber(String orderNumber);
    
    // Zählen wie viele Bestellungen ein User hat
    Long countByUser(User user);

    List<Order> findByUserIdOrderByOrderDateDesc(Long userId);

    // Optional: Nach Status filtern
    List<Order> findByStatusOrderByOrderDateDesc(String status);        
}