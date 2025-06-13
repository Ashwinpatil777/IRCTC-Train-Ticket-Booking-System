package com.irctc.controller;

import com.irctc.model.Role;
import com.irctc.model.User;
import com.irctc.service.UserService;
import com.irctc.config.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/")
@RequiredArgsConstructor
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final UserService userService;
    private final JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody User user) {
        try {
            User registeredUser = userService.register(user);
            String token = jwtUtil.generateToken(registeredUser.getEmail(), registeredUser.getRole().name());
            if (user.getRole() == null) {
                user.setRole(Role.USER); // default role
            }
            Map<String, Object> response = new HashMap<>();
            response.put("user", Map.of(
                    "id", registeredUser.getId(),
                    "username", registeredUser.getUsername(),
                    "role", registeredUser.getRole().name(),
                    "email", registeredUser.getEmail(),
                    "fullname", registeredUser.getFullname()
            ));
            response.put("token", token);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Registration failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        try {
            String email = body.get("email");
            String password = body.get("password");

            if (email == null || password == null) {
                return ResponseEntity.badRequest().body(Map.of("message", "Email and password are required"));
            }

            User user = userService.login(email, password);
            String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());

            Map<String, Object> response = new HashMap<>();
            response.put("user", Map.of(
                    "id", user.getId(),
                    "username", user.getUsername(),
                    "role", user.getRole().name(),
                    "email", user.getEmail(),
                    "fullname", user.getFullname()
            ));
            response.put("token", token);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Login failed: {}", e.getMessage());
            return ResponseEntity.status(401).body(Map.of("message", "Invalid credentials"));
        }
    }
}
