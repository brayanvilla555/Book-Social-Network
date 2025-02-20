package com.practicetec.book.service.security;

import org.springframework.security.core.userdetails.UserDetails;

import java.util.HashMap;

public interface JwtService {
    String generateToken(UserDetails userDetails);
    String generateToken(HashMap<String, Object> claims, UserDetails userDetails);
    String extractUsername(String jwt);
    boolean isTokenValid(String token, UserDetails userDetails);
}
