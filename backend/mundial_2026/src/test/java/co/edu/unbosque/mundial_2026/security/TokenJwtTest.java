package co.edu.unbosque.mundial_2026.security;

import static org.junit.jupiter.api.Assertions.*;

import javax.crypto.SecretKey;

import org.junit.jupiter.api.Test;

class TokenJwtTest {

    private static final String SECRET_BASE64 =
            "dGVzdHNlY3JldGtleXRlc3RzZWNyZXRrZXl0ZXN0c2VjcmV0a2V5dGVzdA==";

    @Test
    void init_conSecretValido_inicializaSecretKey() {
        TokenJwt.init(SECRET_BASE64);
        assertNotNull(TokenJwt.getSecretKey());
    }

    @Test
    void getSecretKey_despuesDeInit_retornaInstanciaDeSecretKey() {
        TokenJwt.init(SECRET_BASE64);
        SecretKey key = TokenJwt.getSecretKey();
        assertInstanceOf(SecretKey.class, key);
    }

    @Test
    void init_llamadoDoVeces_sobreescribeKey() {
        TokenJwt.init(SECRET_BASE64);
        SecretKey primera = TokenJwt.getSecretKey();
        TokenJwt.init(SECRET_BASE64);
        SecretKey segunda = TokenJwt.getSecretKey();
        assertNotNull(primera);
        assertNotNull(segunda);
    }

    @Test
    void prefixToken_esBearer() {
        assertEquals("Bearer ", TokenJwt.PREFIX_TOKEN);
    }

    @Test
    void headerAuthorization_esAuthorization() {
        assertEquals("Authorization", TokenJwt.HEADER_AUTHORIZATION);
    }

    @Test
    void contentType_esApplicationJson() {
        assertEquals("application/json", TokenJwt.CONTENT_TYPE);
    }
}