package com.example.edupro.Security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
@Service
public class JwtService {

    private static final String SECRET_KEY = "ApMbc7Hz2mJ48+wQ00R+DGbnpIvcune7I6mznS7gqzmayBey3ytE3I7SiS9SrSP4LdECpjsEb4tURQJfM3CDHxyoGrhb9FzJY0G/JmKMZXw=";

    public String extractUsername(String token) {
        Claims claims = extractAllClaims(token);
        System.out.println("JWT Claims: " + claims); // Log claims
        return claims.getSubject();
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        try {
            final Claims claims = extractAllClaims(token);
            return claimsResolver.apply(claims);
        } catch (Exception e) {
            System.out.println("Error extracting claims: " + e.getMessage());
            throw e;
        }
    }


    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        var authorities = userDetails.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
        System.out.println("Generating token for: " + userDetails.getUsername() + " with claims: " + extraClaims); // Debugging
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .claim("authorities", authorities)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10))
                .signWith(getSignInKey())
                .compact();
    }


    public boolean isTokenValid(String token, UserDetails userDetails) {
        try {
            final String username = extractUsername(token);
            boolean isValid = (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
            System.out.println("Token valid: " + isValid); // Log token validity
            return isValid;
        } catch (Exception e) {
            System.out.println("Token validation error: " + e.getMessage()); // Log validation errors
            return false;
        }
    }

    private boolean isTokenExpired(String token) {
        Date expiration = extractExpiration(token);
        System.out.println("Token expiration date: " + expiration); // Debugging
        return expiration.before(new Date());
    }


    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token) {
        Claims claims = Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        System.out.println("Extracted Claims: " + claims); // Debugging
        return claims;
    }


    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

}
