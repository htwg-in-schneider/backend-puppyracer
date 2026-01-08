package com.puppyracer.backend.controller;

import com.puppyracer.backend.model.User;
import com.puppyracer.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {
    
    @Autowired
    private UserRepository userRepository;
    
    @GetMapping
    public ResponseEntity<User> getProfile(@AuthenticationPrincipal Jwt jwt) {
        String oauthId = jwt.getSubject();
        if (oauthId == null) {
            return ResponseEntity.badRequest().build();
        }
        
        return userRepository.findByOauthId(oauthId)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    @PutMapping
    public ResponseEntity<User> updateProfile(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody User updatedUser) {
        
        String oauthId = jwt.getSubject();
        if (oauthId == null) {
            return ResponseEntity.badRequest().build();
        }
        
        return userRepository.findByOauthId(oauthId)
            .map(user -> {
                // Nur bestimmte Felder updaten
                if (updatedUser.getName() != null) {
                    user.setName(updatedUser.getName());
                }
                if (updatedUser.getEmail() != null) {
                    user.setEmail(updatedUser.getEmail());
                }
                // Rolle kann NUR Admin ändern (separater Endpoint)
                return ResponseEntity.ok(userRepository.save(user));
            })
            .orElse(ResponseEntity.notFound().build());
    }
}