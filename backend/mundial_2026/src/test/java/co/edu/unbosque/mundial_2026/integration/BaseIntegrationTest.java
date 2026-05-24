package co.edu.unbosque.mundial_2026.integration;

import co.edu.unbosque.mundial_2026.security.TokenJwt;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Date;
import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public abstract class BaseIntegrationTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    private static final String JWT_SECRET_TEST = "dGVzdHNlY3JldGtleXRlc3RzZWNyZXRrZXl0ZXN0c2VjcmV0a2V5dGVzdA==";
    protected static final String USER_EMAIL = "user@test.com";
    protected static final String ADMIN_EMAIL = "admin@test.com";
    

    @BeforeAll
    static void initJwt() {
        TokenJwt.init(JWT_SECRET_TEST);
    }

    @Autowired
    void configureObjectMapper(ObjectMapper objectMapper) {
        objectMapper.registerModule(new JavaTimeModule());
    }

    protected String tokenUsuario() {
        return generarToken(USER_EMAIL, "ROLE_USER");
    }

    protected String tokenAdmin() {
        return generarToken(ADMIN_EMAIL, "ROLE_ADMIN");
    }

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