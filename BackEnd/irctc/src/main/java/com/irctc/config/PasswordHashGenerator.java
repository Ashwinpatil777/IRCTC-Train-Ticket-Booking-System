package com.irctc.config;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordHashGenerator {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String rawPassword = "admin";
        String hashedPassword = encoder.encode(rawPassword);
        System.out.println("BCrypt hash for admin: " + hashedPassword);
    }
}
