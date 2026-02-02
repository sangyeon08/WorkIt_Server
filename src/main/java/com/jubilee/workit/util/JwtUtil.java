package com.jubilee.workit.util;

import com.jubilee.workit.config.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory; 

@Component
public class JwtUtil {

    private final JwtProperties properties;
    private final SecretKey key;

    private static final Logger log = LoggerFactory.getLogger(JwtUtil.class);

    public JwtUtil(JwtProperties properties) {
        this.properties = properties;
        byte[] keyBytes;
        String secret = properties.getSecret();
        // Try Base64 decode first; if it fails, fall back to UTF-8 bytes
        try {
            byte[] decoded = Base64.getDecoder().decode(secret);
            // If decoded bytes are long enough, treat it as intended Base64 key
            if (decoded.length >= 32) {
                keyBytes = decoded;
                log.debug("JWT secret provided as Base64; decoded {} bytes.", keyBytes.length);
            } else {
                // Likely the secret was not intended as Base64; fallback to UTF-8
                keyBytes = secret.getBytes(StandardCharsets.UTF_8);
                log.debug("Base64 decode produced too-short key ({} bytes), fallback to UTF-8 length {}.", decoded.length, keyBytes.length);
            }
        } catch (IllegalArgumentException e) {
            keyBytes = secret.getBytes(StandardCharsets.UTF_8);
            log.debug("JWT secret not Base64; using UTF-8 bytes length {}.", keyBytes.length);
        }

        // Ensure at least 256 bits (32 bytes)
        if (keyBytes.length * 8 < 256) {
            String msg = "JWT secret is too short. Provide at least 256 bits (32 bytes) key.";
            log.error(msg + " Current length: {} bits.", keyBytes.length * 8);
            throw new IllegalArgumentException(msg);
        }

        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public String createToken(Long userId, String email) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + properties.getExpirationMs());

        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("email", email)
                .issuedAt(now)
                .expiration(expiry)
                .signWith(key)
                .compact();
    }

    public Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public Long getUserIdFromToken(String token) {
        return Long.parseLong(parseToken(token).getSubject());
    }

    public long getExpirationMs() {
        return properties.getExpirationMs();
    }
}
