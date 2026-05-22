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

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    private Usuario usuario;

    @BeforeEach
    void setUp() {
        Rol rol = new Rol();
        rol.setId(1L);
        rol.setNombre("ROLE_USUARIO");

        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setCorreoUsuario("test@test.com");
        usuario.setContrasena("hashedPass");
        usuario.setRol(rol);
        usuario.setActivo(true);
    }

    @Nested
    @DisplayName("loadUserByUsername")
    class LoadUserByUsername {

        @Test
        void cuandoUsuarioExisteYActivo_retornaUserDetails() {
            when(usuarioRepository.findByCorreoUsuarioConRol("test@test.com"))
                    .thenReturn(Optional.of(usuario));

            UserDetails resultado = userDetailsService.loadUserByUsername("test@test.com");

            assertNotNull(resultado);
            assertEquals("test@test.com", resultado.getUsername());
            assertEquals("hashedPass", resultado.getPassword());
            assertEquals(1, resultado.getAuthorities().size());
            assertTrue(resultado.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_USUARIO")));
        }

        @Test
        void cuandoUsuarioNoExiste_lanzaUsernameNotFoundException() {
            when(usuarioRepository.findByCorreoUsuarioConRol("nope@test.com"))
                    .thenReturn(Optional.empty());

            assertThrows(UsernameNotFoundException.class,
                    () -> userDetailsService.loadUserByUsername("nope@test.com"));
        }

        @Test
        void cuandoUsuarioInactivo_lanzaUsernameNotFoundException() {
            usuario.setActivo(false);
            when(usuarioRepository.findByCorreoUsuarioConRol("test@test.com"))
                    .thenReturn(Optional.of(usuario));

            assertThrows(UsernameNotFoundException.class,
                    () -> userDetailsService.loadUserByUsername("test@test.com"));
        }

        @Test
        void cuandoUsuarioConRolAdmin_retornaAuthorityAdmin() {
            Rol rolAdmin = new Rol();
            rolAdmin.setNombre("ROLE_ADMIN");
            usuario.setRol(rolAdmin);

            when(usuarioRepository.findByCorreoUsuarioConRol("test@test.com"))
                    .thenReturn(Optional.of(usuario));

            UserDetails resultado = userDetailsService.loadUserByUsername("test@test.com");

            assertTrue(resultado.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
        }
    }
}