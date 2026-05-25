package co.edu.unbosque.mundial_2026.security;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Instant;
import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.jsonwebtoken.Jwts;

/**
 * Clase de pruebas encargada de validar el comportamiento
 * de la lista negra de tokens JWT.
 */
class TokenBlacklistTest {

    /**
     * Clave secreta codificada en Base64 utilizada para pruebas.
     */
    private static final String SECRET_BASE64 =
            "dGVzdHNlY3JldGtleXRlc3RzZWNyZXRrZXl0ZXN0c2VjcmV0a2V5dGVzdA==";

    /**
     * Lista negra de tokens que será probada.
     */
    private TokenBlacklist blacklist;

    /**
     * Inicializa dependencias antes de cada prueba.
     */
    @BeforeEach
    void setUp() {
        TokenJwt.init(SECRET_BASE64);
        blacklist = new TokenBlacklist();
    }

    /**
     * Genera un token JWT válido para pruebas.
     *
     * @return token JWT generado
     */
    private String tokenValido() {
        return Jwts.builder()
                .subject("user@test.com")
                .expiration(Date.from(Instant.now().plusSeconds(3600)))
                .signWith(TokenJwt.getSecretKey())
                .compact();
    }

    /**
     * Verifica que un token no agregado
     * no sea considerado invalidado.
     */
    @Test
    void estaInvalidado_tokenNoAgregado_retornaFalse() {
        String token = tokenValido();
        assertFalse(blacklist.estaInvalidado(token));
    }

    /**
     * Verifica que al agregar un token
     * este quede invalidado.
     */
    @Test
    void agregar_tokenValido_luegoEstaInvalidado() {
        String token = tokenValido();
        blacklist.agregar(token);
        assertTrue(blacklist.estaInvalidado(token));
    }

    /**
     * Verifica que agregar el mismo token dos veces
     * mantenga su estado invalidado.
     */
    @Test
    void agregar_mismoTokenDoVeces_sigueInvalidado() {
        String token = tokenValido();
        blacklist.agregar(token);
        blacklist.agregar(token);
        assertTrue(blacklist.estaInvalidado(token));
    }

    /**
     * Verifica que solo el token agregado
     * sea considerado invalidado.
     */
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