package com.jubilee.workit.util;

import com.jubilee.workit.config.JwtProperties;
import org.junit.jupiter.api.Test;

import java.security.SecureRandom;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

public class JwtUtilTest {

    @Test
    public void createAndParse_withBase64Secret_shouldSucceed() {
        byte[] keyBytes = new byte[32];
        new SecureRandom().nextBytes(keyBytes);
        String base64 = Base64.getEncoder().encodeToString(keyBytes);

        JwtProperties props = new JwtProperties();
        props.setSecret(base64);
        JwtUtil util = new JwtUtil(props);

        String token = util.createToken(123L, "test@example.com");
        assertEquals(123L, util.getUserIdFromToken(token));
    }

    @Test
    public void createAndParse_withRawUtf8SecretOfSufficientLength_shouldSucceed() {
        String secret = "a".repeat(32); // 32 bytes
        JwtProperties props = new JwtProperties();
        props.setSecret(secret);
        JwtUtil util = new JwtUtil(props);

        String token = util.createToken(456L, "x@test.com");
        assertEquals(456L, util.getUserIdFromToken(token));
    }

    @Test
    public void constructor_withTooShortSecret_shouldThrow() {
        JwtProperties props = new JwtProperties();
        props.setSecret("short");
        assertThrows(IllegalArgumentException.class, () -> new JwtUtil(props));
    }
}