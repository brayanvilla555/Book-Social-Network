package com.practicetec.book.service.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;


import java.security.Key;
import java.util.*;
import java.util.function.Function;

@Service
public class JwtServiceImpl implements JwtService {

    @Value("${application.security.jwt.time-expiration-ninutes}")
    private long JWT_EXPIRATION;
    @Value("${application.security.jwt.secre-key}")
    private String SECRE_KEY;
    private final Date JWT_TIME_GENERATE = new Date(System.currentTimeMillis());

    @Override
    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<String, Object>(), userDetails);
    }

    public String generateToken(HashMap<String,Object> claims, UserDetails userDetails) {
        return buildToken(claims, userDetails);
    }

    private String buildToken(HashMap<String, Object> extraClaims, UserDetails userDetails) {
        List<String> authorities = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
        return Jwts.builder()
                .claims(extraClaims)
                .subject(userDetails.getUsername())
                .issuedAt(JWT_TIME_GENERATE)
                .expiration(new Date(System.currentTimeMillis() + JWT_EXPIRATION * 60 * 60))
                .claim("authorities", authorities)
                .signWith(getSignInKey()) //firma del jwt
                .compact();
    }

    @Override
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !tokenExpire(token);
    }

    private boolean tokenExpire(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }


    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRE_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String extractUsername(String jwt) {
        return extractClaim(jwt, Claims::getSubject);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .setSigningKey(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

}
