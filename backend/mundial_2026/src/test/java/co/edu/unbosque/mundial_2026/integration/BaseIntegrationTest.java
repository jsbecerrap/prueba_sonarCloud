package co.edu.unbosque.mundial_2026.integration;

import co.edu.unbosque.mundial_2026.security.TokenJwt;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Date;
import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public abstract class BaseIntegrationTest {

    private static final String JWT_SECRET_TEST =
            "dGVzdHNlY3JldGtleXRlc3RzZWNyZXRrZXl0ZXN0c2VjcmV0a2V5dGVzdA==";

    protected static final String USER_EMAIL = "user@test.com";
    protected static final String ADMIN_EMAIL = "admin@test.com";

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @BeforeAll
    static void initJwt() {
        TokenJwt.init(JWT_SECRET_TEST);
    }

    /**
     * Configura el ObjectMapper para soportar tipos de fecha y hora de Java.
     *
     * @param objectMapper mapper utilizado en las pruebas
     */
    @Autowired
    void configureObjectMapper(ObjectMapper objectMapper) {
        objectMapper.registerModule(new JavaTimeModule());
    }

    /**
     * Genera un token JWT con rol de usuario.
     *
     * @return token JWT de usuario
     */
    protected String tokenUsuario() {
        return generarToken(USER_EMAIL, "ROLE_USER");
    }

    /**
     * Genera un token JWT con rol de administrador.
     *
     * @return token JWT de administrador
     */
    protected String tokenAdmin() {
        return generarToken(ADMIN_EMAIL, "ROLE_ADMIN");
    }

    /**
     * Genera un token JWT para el correo y rol indicados.
     *
     * @param correo correo asociado al token
     * @param rol rol asignado al token
     * @return token JWT generado
     */
    private String generarToken(String correo, String rol) {
        return Jwts.builder()
                .subject(correo)
                .claim("authorities", List.of(rol))
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 3_600_000))
                .signWith(TokenJwt.getSecretKey())
                .compact();
    }
}