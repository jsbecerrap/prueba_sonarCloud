package co.edu.unbosque.mundial_2026.security;

import static org.junit.jupiter.api.Assertions.*;

import javax.crypto.SecretKey;

import org.junit.jupiter.api.Test;

/**
 * Pruebas unitarias para la clase TokenJwt.
 * Verifica la inicialización de la clave secreta
 * y los valores de las constantes utilizadas en JWT.
 */
class TokenJwtTest {

    private static final String SECRET_BASE64 =
            "dGVzdHNlY3JldGtleXRlc3RzZWNyZXRrZXl0ZXN0c2VjcmV0a2V5dGVzdA==";

    /**
     * Verifica que init inicialice correctamente la clave secreta.
     */
    @Test
    void init_conSecretValido_inicializaSecretKey() {
        TokenJwt.init(SECRET_BASE64);
        assertNotNull(TokenJwt.getSecretKey());
    }

    /**
     * Verifica que getSecretKey retorne una instancia válida de SecretKey
     * después de inicializar el token.
     */
    @Test
    void getSecretKey_despuesDeInit_retornaInstanciaDeSecretKey() {
        TokenJwt.init(SECRET_BASE64);
        SecretKey key = TokenJwt.getSecretKey();
        assertInstanceOf(SecretKey.class, key);
    }

    /**
     * Verifica que múltiples llamadas a init mantengan una clave válida.
     */
    @Test
    void init_llamadoDoVeces_sobreescribeKey() {
        TokenJwt.init(SECRET_BASE64);
        SecretKey primera = TokenJwt.getSecretKey();
        TokenJwt.init(SECRET_BASE64);
        SecretKey segunda = TokenJwt.getSecretKey();
        assertNotNull(primera);
        assertNotNull(segunda);
    }

    /**
     * Verifica que el prefijo del token sea Bearer.
     */
    @Test
    void prefixToken_esBearer() {
        assertEquals("Bearer ", TokenJwt.PREFIX_TOKEN);
    }

    /**
     * Verifica que el header de autorización tenga el nombre correcto.
     */
    @Test
    void headerAuthorization_esAuthorization() {
        assertEquals("Authorization", TokenJwt.HEADER_AUTHORIZATION);
    }

    /**
     * Verifica que el content type configurado sea JSON.
     */
    @Test
    void contentType_esApplicationJson() {
        assertEquals("application/json", TokenJwt.CONTENT_TYPE);
    }
}