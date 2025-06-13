package com.irctc.config;

import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.SignatureAlgorithm;

import java.security.Key;
import java.util.Base64;

public class JwtKeyGenerator {
    public static void main(String[] args) {
        Key key = Keys.secretKeyFor(SignatureAlgorithm.HS512); // generates a secure random key for HS512
        String base64Key = Base64.getEncoder().encodeToString(key.getEncoded());

        System.out.println("Base64 encoded HS512 key:");
        System.out.println(base64Key);
        System.out.println("Key length in bits: " + (key.getEncoded().length * 8));
    }
}
