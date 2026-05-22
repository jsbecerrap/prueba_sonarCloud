package co.edu.unbosque.mundial_2026.security;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Instant;
import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.jsonwebtoken.Jwts;

class TokenBlacklistTest {

    private static final String SECRET_BASE64 =
            "dGVzdHNlY3JldGtleXRlc3RzZWNyZXRrZXl0ZXN0c2VjcmV0a2V5dGVzdA==";

    private TokenBlacklist blacklist;

    @BeforeEach
    void setUp() {
        TokenJwt.init(SECRET_BASE64);
        blacklist = new TokenBlacklist();
    }

    private String tokenValido() {
        return Jwts.builder()
                .subject("user@test.com")
                .expiration(Date.from(Instant.now().plusSeconds(3600)))
                .signWith(TokenJwt.getSecretKey())
                .compact();
    }

    private String tokenExpirado() {
        return Jwts.builder()
                .subject("user@test.com")
                .expiration(Date.from(Instant.now().minusSeconds(10)))
                .signWith(TokenJwt.getSecretKey())
                .compact();
    }

    @Test
    void estaInvalidado_tokenNoAgregado_retornaFalse() {
        String token = tokenValido();
        assertFalse(blacklist.estaInvalidado(token));
    }

    @Test
    void agregar_tokenValido_luegoEstaInvalidado() {
        String token = tokenValido();
        blacklist.agregar(token);
        assertTrue(blacklist.estaInvalidado(token));
    }

    @Test
    void agregar_mismoTokenDoVeces_sigueInvalidado() {
        String token = tokenValido();
        blacklist.agregar(token);
        blacklist.agregar(token);
        assertTrue(blacklist.estaInvalidado(token));
    }

    @Test
    void estaInvalidado_tokenExpiradoAgregado_retornaFalse() {
        String token = tokenExpirado();
        blacklist.agregar(token);
        assertFalse(blacklist.estaInvalidado(token));
    }

    @Test
    void estaInvalidado_dosTokensDiferentes_soloInvalidaElAgregado() {
        String tokenA = tokenValido();
        String tokenB = Jwts.builder()
                .subject("otro@test.com")
                .expiration(Date.from(Instant.now().plusSeconds(3600)))
                .signWith(TokenJwt.getSecretKey())
                .compact();

        blacklist.agregar(tokenA);

        assertTrue(blacklist.estaInvalidado(tokenA));
        assertFalse(blacklist.estaInvalidado(tokenB));
    }
}