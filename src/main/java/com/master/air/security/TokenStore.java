package com.master.air.security;

import com.master.air.model.User;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;


public class TokenStore {

    private static final long TOKEN_EXPIRY_SECONDS = 3600; // 1h

    private static final Map<String, TokenInfo> tokens = new ConcurrentHashMap<>();

    public static class TokenInfo {
        private final Long userId;
        private final String username;
        private final Instant expiresAt;

        public TokenInfo(Long userId, String username, Instant expiresAt) {
            this.userId = userId;
            this.username = username;
            this.expiresAt = expiresAt;
        }

        public Long getUserId() { return userId; }
        public String getUsername() { return username; }
        public Instant getExpiresAt() { return expiresAt; }
        public boolean isExpired() { return Instant.now().isAfter(expiresAt); }
    }


    public static String generateToken(User user) {
        String token = UUID.randomUUID().toString();
        Instant expiry = Instant.now().plusSeconds(TOKEN_EXPIRY_SECONDS);
        tokens.put(token, new TokenInfo(user.getId(), user.getUsername(), expiry));
        return token;
    }

    public static Optional<TokenInfo> validateToken(String token) {
        TokenInfo info = tokens.get(token);
        if (info == null || info.isExpired()) {
            if (info != null) tokens.remove(token);
            return Optional.empty();
        }
        return Optional.of(info);
    }


    public static void removeToken(String token) {
        tokens.remove(token);
    }


    public static void cleanExpiredTokens() {
        tokens.entrySet().removeIf(e -> e.getValue().isExpired());
    }
}
