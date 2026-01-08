package com.puppyracer.backend.controller;

import com.puppyracer.backend.model.User;
import com.puppyracer.backend.model.Role;
import com.puppyracer.backend.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {
    
    private final UserRepository userRepository;
    
    // Constructor Injection (besser als @Autowired)
    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    
    // NUR FÜR ADMINS: Alle Benutzer anzeigen
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")  // Spring Security prüft automatisch
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userRepository.findAll();
        return ResponseEntity.ok(users);
    }
    
    // NUR FÜR ADMINS: Bestimmten Benutzer bearbeiten (z.B. Rolle ändern)
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> updateUser(
            @PathVariable Long id,
            @RequestBody User updatedUser) {
        
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        User user = optionalUser.get();
        // Nur bestimmte Felder dürfen geändert werden
        user.setName(updatedUser.getName());
        user.setEmail(updatedUser.getEmail());
        user.setRole(updatedUser.getRole());  // Wichtig: Admin kann Rolle ändern!
        
        User savedUser = userRepository.save(user);
        return ResponseEntity.ok(savedUser);
    }
    
    // OPTIONAL: Benutzer löschen (nur Admin)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        if (!userRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        userRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}