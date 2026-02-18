package com.master.air.security;

import com.master.air.model.User;
import org.junit.jupiter.api.*;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class TokenStoreTest {

    @Test
    @DisplayName("Generate et valider un token")
    void testGenerateAndValidate() {
        User user = new User("testuser", "t@t.com", "pass");
        user.setId(42L);

        String token = TokenStore.generateToken(user);
        assertNotNull(token);

        Optional<TokenStore.TokenInfo> info = TokenStore.validateToken(token);
        assertTrue(info.isPresent());
        assertEquals(42L, info.get().getUserId());
        assertEquals("testuser", info.get().getUsername());
    }

    @Test
    @DisplayName("Token invalide retourne empty")
    void testInvalidToken() {
        Optional<TokenStore.TokenInfo> info = TokenStore.validateToken("fake-token-xyz");
        assertTrue(info.isEmpty());
    }

    @Test
    @DisplayName("Remove token (logout)")
    void testRemoveToken() {
        User user = new User("logoutuser", "l@t.com", "pass");
        user.setId(99L);

        String token = TokenStore.generateToken(user);
        assertTrue(TokenStore.validateToken(token).isPresent());

        TokenStore.removeToken(token);
        assertTrue(TokenStore.validateToken(token).isEmpty());
    }
}
