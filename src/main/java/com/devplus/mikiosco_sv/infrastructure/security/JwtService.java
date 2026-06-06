package com.devplus.mikiosco_sv.infrastructure.security;

import com.devplus.mikiosco_sv.domain.model.UserRole;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.UUID;

@Service
public class JwtService {

    @Value("${app.jwt.secret}")
    private String secret;

    @Value("${app.jwt.expiration-ms}")
    private long expirationMs;

    public String generateToken(UUID userId, UUID comedorId, UserRole role, String email) {
        return Jwts.builder()
                .subject(userId.toString())
                .claim("comedorId", comedorId != null ? comedorId.toString() : null)
                .claim("role", role.name())
                .claim("email", email)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(getSignKey())
                .compact();
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSignKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public long getExpirationMs() {
        return expirationMs;
    }

    public boolean isTokenValid(String token) {
        try {
            extractAllClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public AuthenticatedUser toAuthenticatedUser(Claims claims) {
        UUID userId = UUID.fromString(claims.getSubject());
        String comedorIdStr = claims.get("comedorId", String.class);
        UUID comedorId = comedorIdStr != null ? UUID.fromString(comedorIdStr) : null;
        UserRole role = UserRole.valueOf(claims.get("role", String.class));
        String email = claims.get("email", String.class);
        return new AuthenticatedUser(userId, comedorId, role, email);
    }

    private SecretKey getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
