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

/**
 * Clase de pruebas encargada de validar el comportamiento
 * del filtro de autenticación JWT en distintos escenarios.
 */
@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    /**
     * Clave secreta codificada en Base64 utilizada para pruebas.
     */
    private static final String SECRET_BASE64 =
            "dGVzdHNlY3JldGtleXRlc3RzZWNyZXRrZXl0ZXN0c2VjcmV0a2V5dGVzdA==";

    /**
     * Administrador encargado de autenticar credenciales.
     */
    @Mock
    private AuthenticationManager authManager;

    /**
     * Repositorio para consultar y actualizar usuarios.
     */
    @Mock
    private UsuarioRepository usuarioRepo;

    /**
     * Filtro JWT que será probado.
     */
    private JwtAuthenticationFilter filter;

    /**
     * Inicializa dependencias antes de cada prueba.
     */
    @BeforeEach
    void setUp() {
        TokenJwt.init(SECRET_BASE64);
        filter = new JwtAuthenticationFilter(authManager, usuarioRepo);
    }

    /**
     * Crea una solicitud HTTP con credenciales en formato JSON.
     *
     * @param correo correo del usuario
     * @param contrasena contraseña del usuario
     * @return solicitud configurada con credenciales
     * @throws Exception si ocurre un error al crear la solicitud
     */
    private MockHttpServletRequest requestConCredenciales(String correo, String contrasena)
            throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        String json = "{\"correoUsuario\":\"" + correo + "\",\"contrasena\":\"" + contrasena + "\"}";
        request.setContent(json.getBytes(java.nio.charset.StandardCharsets.UTF_8));
        return request;
    }

    /**
     * Crea un usuario de prueba sin bloqueo activo.
     *
     * @param correo correo del usuario
     * @return usuario configurado para pruebas
     */
    private Usuario usuarioSinBloqueo(String correo) {
        Usuario u = new Usuario();
        u.setCorreoUsuario(correo);
        u.setNombre("Juan");
        u.setApellido("Perez");
        u.setIntentosFallidos(0);
        u.setBloqueadoHasta(null);
        return u;
    }

    /**
     * Invoca manualmente el método de autenticación fallida.
     *
     * @param request solicitud HTTP
     * @param response respuesta HTTP
     * @param ex excepción de autenticación
     * @throws Exception si ocurre un error durante la invocación
     */
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

    /**
     * Invoca manualmente el método de autenticación exitosa.
     *
     * @param request solicitud HTTP
     * @param response respuesta HTTP
     * @param auth autenticación exitosa
     * @throws Exception si ocurre un error durante la invocación
     */
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

    /**
     * Verifica que credenciales válidas sean delegadas al autenticador.
     *
     * @throws Exception si ocurre un error en la prueba
     */
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

    /**
     * Verifica que un usuario bloqueado no pueda autenticarse.
     *
     * @throws Exception si ocurre un error en la prueba
     */
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

    /**
     * Verifica que contraseñas demasiado largas generen excepción.
     *
     * @throws Exception si ocurre un error en la prueba
     */
    @Test
    void attemptAuthentication_contrasenaDemasiadoLarga_lanzaExcepcion() throws Exception {
        String contrasenaLarga = "a".repeat(73);
        MockHttpServletRequest request = requestConCredenciales("user@test.com", contrasenaLarga);
        MockHttpServletResponse response = new MockHttpServletResponse();

        assertThrows(Exception.class,
                () -> filter.attemptAuthentication(request, response));
    }

    /**
     * Verifica que un fallo incremente el contador de intentos fallidos.
     *
     * @throws Exception si ocurre un error en la prueba
     */
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

    /**
     * Verifica que un usuario previamente bloqueado reciba código 429.
     *
     * @throws Exception si ocurre un error en la prueba
     */
    @Test
    void unsuccessfulAuthentication_bloqueadoPrevio_retorna429() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        invokeUnsuccessful(request, response, new LockedException("bloqueado"));

        assertEquals(429, response.getStatus());
    }

    /**
     * Verifica que sin correo no se incrementen intentos fallidos.
     *
     * @throws Exception si ocurre un error en la prueba
     */
    @Test
    void unsuccessfulAuthentication_sinCorreo_noIncrementaContador() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        invokeUnsuccessful(request, response, new BadCredentialsException("bad credentials"));

        verify(usuarioRepo, never()).findByCorreoUsuario(any());
        assertEquals(401, response.getStatus());
    }

    /**
     * Verifica que al quinto intento fallido la cuenta se bloquee.
     *
     * @throws Exception si ocurre un error en la prueba
     */
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

    /**
     * Verifica que una autenticación exitosa genere token JWT.
     *
     * @throws Exception si ocurre un error en la prueba
     */
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

    /**
     * Verifica que una autenticación exitosa reinicie intentos fallidos.
     *
     * @throws Exception si ocurre un error en la prueba
     */
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