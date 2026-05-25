package co.edu.unbosque.mundial_2026.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import co.edu.unbosque.mundial_2026.entity.Rol;
import co.edu.unbosque.mundial_2026.entity.Usuario;
import co.edu.unbosque.mundial_2026.repository.UsuarioRepository;

/**
 * Pruebas unitarias para {@link UserDetailsServiceImpl}.
 * Verifica el comportamiento del servicio encargado de cargar usuarios
 * para autenticación.
 */
@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    private Usuario usuario;
    private static final String CORREO_TEST = "test@test.com";
    private static final String ROLE_USUARIO = "ROLE_USUARIO";
    private static final String CORREO_NOPE = "nope@test.com";

    /**
     * Configura los datos base antes de cada prueba.
     */
    @BeforeEach
    void setUp() {
        Rol rol = new Rol();
        rol.setId(1L);
        rol.setNombre(ROLE_USUARIO);

        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setCorreoUsuario(CORREO_TEST);
        usuario.setContrasena("hashedPass");
        usuario.setRol(rol);
        usuario.setActivo(true);
    }

    /**
     * Grupo de pruebas para el método loadUserByUsername.
     */
    @Nested
    @DisplayName("loadUserByUsername")
    class LoadUserByUsername {

        /**
         * Verifica que si el usuario existe y está activo,
         * se retorne correctamente un UserDetails.
         */
        @Test
        void cuandoUsuarioExisteYActivo_retornaUserDetails() {
            when(usuarioRepository.findByCorreoUsuarioConRol(CORREO_TEST))
                    .thenReturn(Optional.of(usuario));

            UserDetails resultado = userDetailsService.loadUserByUsername(CORREO_TEST);

            assertNotNull(resultado);
            assertEquals(CORREO_TEST, resultado.getUsername());
            assertEquals("hashedPass", resultado.getPassword());
            assertEquals(1, resultado.getAuthorities().size());
            assertTrue(resultado.getAuthorities().stream()
                    .anyMatch(a -> ROLE_USUARIO.equals(a.getAuthority())));
        }

        /**
         * Verifica que si el usuario no existe,
         * se lance UsernameNotFoundException.
         */
        @Test
        void cuandoUsuarioNoExiste_lanzaUsernameNotFoundException() {
            when(usuarioRepository.findByCorreoUsuarioConRol(CORREO_NOPE))
                    .thenReturn(Optional.empty());

            assertThrows(UsernameNotFoundException.class,
                    () -> userDetailsService.loadUserByUsername(CORREO_NOPE));
        }

        /**
         * Verifica que si el usuario está inactivo,
         * se lance UsernameNotFoundException.
         */
        @Test
        void cuandoUsuarioInactivo_lanzaUsernameNotFoundException() {
            usuario.setActivo(false);
            when(usuarioRepository.findByCorreoUsuarioConRol(CORREO_TEST))
                    .thenReturn(Optional.of(usuario));

            assertThrows(UsernameNotFoundException.class,
                    () -> userDetailsService.loadUserByUsername(CORREO_TEST));
        }

        /**
         * Verifica que si el usuario tiene rol administrador,
         * se retorne correctamente la autoridad ROLE_ADMIN.
         */
        @Test
        void cuandoUsuarioConRolAdmin_retornaAuthorityAdmin() {
            Rol rolAdmin = new Rol();
            rolAdmin.setNombre("ROLE_ADMIN");
            usuario.setRol(rolAdmin);

            when(usuarioRepository.findByCorreoUsuarioConRol(CORREO_TEST))
                    .thenReturn(Optional.of(usuario));

            UserDetails resultado = userDetailsService.loadUserByUsername(CORREO_TEST);

            assertTrue(resultado.getAuthorities().stream()
                    .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority())));
        }
    }
}