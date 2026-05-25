package co.edu.unbosque.mundial_2026.security;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.Instant;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import co.edu.unbosque.mundial_2026.security.filter.JwtValidationFilter;
import io.jsonwebtoken.Jwts;

/**
 * Clase de pruebas encargada de validar el comportamiento
 * del filtro de validación JWT en distintos escenarios.
 */
@ExtendWith(MockitoExtension.class)
class JwtValidationFilterTest {

    /**
     * Correo de usuario utilizado en pruebas.
     */
    private static final String USER_EMAIL = "user@test.com";

    /**
     * Prefijo estándar del encabezado Authorization.
     */
    private static final String BEARER_PREFIX = "Bearer ";

    /**
     * Clave secreta codificada en Base64 utilizada para pruebas.
     */
    private static final String SECRET_BASE64 =
            "dGVzdHNlY3JldGtleXRlc3RzZWNyZXRrZXl0ZXN0c2VjcmV0a2V5dGVzdA==";

    /**
     * Administrador de autenticación utilizado en pruebas.
     */
    @Mock
    private AuthenticationManager authManager;

    /**
     * Lista negra de tokens invalidados.
     */
    @Mock
    private TokenBlacklist tokenBlacklist;

    /**
     * Filtro JWT que será probado.
     */
    private JwtValidationFilter filter;

    /**
     * Inicializa dependencias antes de cada prueba.
     */
    @BeforeEach
    void setUp() {
        TokenJwt.init(SECRET_BASE64);
        filter = new JwtValidationFilter(authManager, tokenBlacklist);
        SecurityContextHolder.clearContext();
    }

    /**
     * Genera un token JWT válido para pruebas.
     *
     * @param correo correo del usuario
     * @return token JWT generado
     */
    private String tokenValido(String correo) {
        return Jwts.builder()
                .subject(correo)
                .claim("authorities", List.of("ROLE_USER"))
                .expiration(Date.from(Instant.now().plusSeconds(3600)))
                .signWith(TokenJwt.getSecretKey())
                .compact();
    }

    /**
     * Verifica que sin encabezado Authorization
     * la petición continúe normalmente.
     *
     * @throws Exception si ocurre un error en la prueba
     */
    @Test
    void doFilter_sinHeader_continuaChain() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        filter.doFilter(request, response, chain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        assertNotNull(chain.getRequest());
    }

    /**
     * Verifica que un encabezado sin prefijo Bearer
     * no intente autenticación y continúe la petición.
     *
     * @throws Exception si ocurre un error en la prueba
     */
    @Test
    void doFilter_headerSinPrefixBearer_continuaChain() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Basic abc123");
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        filter.doFilter(request, response, chain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        assertNotNull(chain.getRequest());
    }

    /**
     * Verifica que un token válido
     * registre autenticación en el contexto de seguridad.
     *
     * @throws Exception si ocurre un error en la prueba
     */
    @Test
    void doFilter_tokenValido_setAuthenticationEnContext() throws Exception {
        String token = tokenValido(USER_EMAIL);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", BEARER_PREFIX + token);
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();
        when(tokenBlacklist.estaInvalidado(token)).thenReturn(false);

        filter.doFilter(request, response, chain);

        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals(USER_EMAIL,
                SecurityContextHolder.getContext().getAuthentication().getName());
    }

    /**
     * Verifica que un token invalidado
     * retorne error 401.
     *
     * @throws Exception si ocurre un error en la prueba
     */
    @Test
    void doFilter_tokenInvalidado_retorna401() throws Exception {
        String token = tokenValido(USER_EMAIL);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", BEARER_PREFIX + token);
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();
        when(tokenBlacklist.estaInvalidado(token)).thenReturn(true);

        filter.doFilter(request, response, chain);

        assertEquals(401, response.getStatus());
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    /**
     * Verifica que un token mal formado
     * retorne error 401.
     *
     * @throws Exception si ocurre un error en la prueba
     */
    @Test
    void doFilter_tokenMalformado_retorna401() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer tokenbasura.abc.xyz");
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();
        when(tokenBlacklist.estaInvalidado(any())).thenReturn(false);

        filter.doFilter(request, response, chain);

        assertEquals(401, response.getStatus());
    }

    /**
     * Verifica que un token válido
     * cargue correctamente los roles del usuario.
     *
     * @throws Exception si ocurre un error en la prueba
     */
    @Test
    void doFilter_tokenValido_rolesSetEnAuthentication() throws Exception {
        String token = tokenValido("admin@test.com");
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", BEARER_PREFIX + token);
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();
        when(tokenBlacklist.estaInvalidado(token)).thenReturn(false);

        filter.doFilter(request, response, chain);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(auth);
        assertFalse(auth.getAuthorities().isEmpty());
        assertEquals("ROLE_USER",
                auth.getAuthorities().iterator().next().getAuthority());
    }
}