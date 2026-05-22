package co.edu.unbosque.mundial_2026.security;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import co.edu.unbosque.mundial_2026.entity.Usuario;
import co.edu.unbosque.mundial_2026.repository.UsuarioRepository;
import co.edu.unbosque.mundial_2026.security.filter.JwtAuthenticationFilter;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    private static final String SECRET_BASE64 =
            "dGVzdHNlY3JldGtleXRlc3RzZWNyZXRrZXl0ZXN0c2VjcmV0a2V5dGVzdA==";

    @Mock
    private AuthenticationManager authManager;

    @Mock
    private UsuarioRepository usuarioRepo;

    private JwtAuthenticationFilter filter;

    @BeforeEach
    void setUp() {
        TokenJwt.init(SECRET_BASE64);
        filter = new JwtAuthenticationFilter(authManager, usuarioRepo);
    }

    private MockHttpServletRequest requestConCredenciales(String correo, String contrasena)
            throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        String json = "{\"correoUsuario\":\"" + correo + "\",\"contrasena\":\"" + contrasena + "\"}";
        request.setContent(json.getBytes());
        return request;
    }

    private Usuario usuarioSinBloqueo(String correo) {
        Usuario u = new Usuario();
        u.setCorreoUsuario(correo);
        u.setNombre("Juan");
        u.setApellido("Perez");
        u.setIntentosFallidos(0);
        u.setBloqueadoHasta(null);
        return u;
    }

    private void invokeUnsuccessful(MockHttpServletRequest request,
            MockHttpServletResponse response,
            AuthenticationException ex) throws Exception {
        Method m = JwtAuthenticationFilter.class.getDeclaredMethod(
                "unsuccessfulAuthentication",
                jakarta.servlet.http.HttpServletRequest.class,
                jakarta.servlet.http.HttpServletResponse.class,
                AuthenticationException.class);
        m.setAccessible(true);
        m.invoke(filter, request, response, ex);
    }

    private void invokeSuccessful(MockHttpServletRequest request,
            MockHttpServletResponse response,
            Authentication auth) throws Exception {
        Method m = JwtAuthenticationFilter.class.getDeclaredMethod(
                "successfulAuthentication",
                jakarta.servlet.http.HttpServletRequest.class,
                jakarta.servlet.http.HttpServletResponse.class,
                jakarta.servlet.FilterChain.class,
                Authentication.class);
        m.setAccessible(true);
        m.invoke(filter, request, response, null, auth);
    }

    @Test
    void attemptAuthentication_credencialesValidas_delegaAlAuthManager() throws Exception {
        MockHttpServletRequest request = requestConCredenciales("user@test.com", "pass123");
        MockHttpServletResponse response = new MockHttpServletResponse();
        Authentication auth = new UsernamePasswordAuthenticationToken("user@test.com", "pass123");
        when(authManager.authenticate(any())).thenReturn(auth);

        Authentication result = filter.attemptAuthentication(request, response);

        assertNotNull(result);
        verify(authManager).authenticate(any());
    }

    @Test
    void attemptAuthentication_usuarioBloqueado_lanzaLockedException() throws Exception {
        MockHttpServletRequest request = requestConCredenciales("user@test.com", "pass123");
        MockHttpServletResponse response = new MockHttpServletResponse();
        Usuario usuario = usuarioSinBloqueo("user@test.com");
        usuario.setBloqueadoHasta(LocalDateTime.now().plusMinutes(10));
        when(usuarioRepo.findByCorreoUsuario("user@test.com")).thenReturn(Optional.of(usuario));

        assertThrows(LockedException.class,
                () -> filter.attemptAuthentication(request, response));
    }

    @Test
    void attemptAuthentication_contrasenaDemasiadoLarga_lanzaExcepcion() throws Exception {
        String contrasenaLarga = "a".repeat(73);
        MockHttpServletRequest request = requestConCredenciales("user@test.com", contrasenaLarga);
        MockHttpServletResponse response = new MockHttpServletResponse();
        Usuario usuario = usuarioSinBloqueo("user@test.com");
        when(usuarioRepo.findByCorreoUsuario("user@test.com")).thenReturn(Optional.of(usuario));

        assertThrows(Exception.class,
                () -> filter.attemptAuthentication(request, response));
    }

    @Test
    void unsuccessfulAuthentication_incrementaIntentosFallidos() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        request.setAttribute("correoIntentoLogin", "user@test.com");
        Usuario usuario = usuarioSinBloqueo("user@test.com");
        when(usuarioRepo.findByCorreoUsuario("user@test.com")).thenReturn(Optional.of(usuario));

        invokeUnsuccessful(request, response, new BadCredentialsException("bad credentials"));

        verify(usuarioRepo).save(argThat(u -> u.getIntentosFallidos() == 1));
        assertEquals(401, response.getStatus());
    }

    @Test
    void unsuccessfulAuthentication_bloqueadoPrevio_retorna429() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        invokeUnsuccessful(request, response, new LockedException("bloqueado"));

        assertEquals(429, response.getStatus());
    }

    @Test
    void unsuccessfulAuthentication_sinCorreo_noIncrementaContador() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        invokeUnsuccessful(request, response, new BadCredentialsException("bad credentials"));

        verify(usuarioRepo, never()).findByCorreoUsuario(any());
        assertEquals(401, response.getStatus());
    }

    @Test
    void unsuccessfulAuthentication_quintoIntento_bloqueaCuenta() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        request.setAttribute("correoIntentoLogin", "user@test.com");
        Usuario usuario = usuarioSinBloqueo("user@test.com");
        usuario.setIntentosFallidos(4);
        when(usuarioRepo.findByCorreoUsuario("user@test.com")).thenReturn(Optional.of(usuario));

        invokeUnsuccessful(request, response, new BadCredentialsException("bad credentials"));

        verify(usuarioRepo).save(argThat(u -> u.getBloqueadoHasta() != null));
    }

    @Test
    void successfulAuthentication_emiteTokenEnHeader() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        Usuario usuario = usuarioSinBloqueo("user@test.com");
        when(usuarioRepo.findByCorreoUsuario("user@test.com")).thenReturn(Optional.of(usuario));
        Authentication auth = new UsernamePasswordAuthenticationToken(
                "user@test.com", null,
                List.of(new SimpleGrantedAuthority("ROLE_USER")));

        invokeSuccessful(request, response, auth);

        assertNotNull(response.getHeader("Authorization"));
        assertTrue(response.getHeader("Authorization").startsWith("Bearer "));
    }

    @Test
    void successfulAuthentication_resetIntentosFallidos() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        Usuario usuario = usuarioSinBloqueo("user@test.com");
        usuario.setIntentosFallidos(3);
        when(usuarioRepo.findByCorreoUsuario("user@test.com")).thenReturn(Optional.of(usuario));
        Authentication auth = new UsernamePasswordAuthenticationToken(
                "user@test.com", null,
                List.of(new SimpleGrantedAuthority("ROLE_USER")));

        invokeSuccessful(request, response, auth);

        verify(usuarioRepo).save(argThat(u -> u.getIntentosFallidos() == 0));
    }
}