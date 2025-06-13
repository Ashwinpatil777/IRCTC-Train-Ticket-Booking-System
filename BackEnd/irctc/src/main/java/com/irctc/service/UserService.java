package com.irctc.service;

import com.irctc.Exception.UserNotFoundException;
import com.irctc.model.Role;
import com.irctc.model.User;
import com.irctc.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // Register user
    public User register(User user) {
        logger.info("Registering user with email: {}", user.getEmail());

        // Check if email or username exists
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Email already in use");
        }
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new IllegalArgumentException("Username already in use");
        }

        if (user.getRole() == null) {
           user.setRole(Role.USER);
      }
       


        // Encode password
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Save and return
        return userRepository.save(user);
    }

    // Login user and validate credentials
    public User login(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Invalid email or password"));
        logger.info("User found: {}", user.getEmail());
        boolean matches = passwordEncoder.matches(password, user.getPassword());
        logger.info("Password match result: {}", matches);
        if (!matches) {
            throw new UserNotFoundException("Invalid email or password");
        }
        return user;
    }


    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));
    }
}
