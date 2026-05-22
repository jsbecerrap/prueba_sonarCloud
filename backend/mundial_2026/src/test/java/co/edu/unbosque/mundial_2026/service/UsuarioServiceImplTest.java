package co.edu.unbosque.mundial_2026.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import co.edu.unbosque.mundial_2026.dto.request.UsuarioActualizarRequestDTO;
import co.edu.unbosque.mundial_2026.dto.request.UsuarioRequestDTO;
import co.edu.unbosque.mundial_2026.dto.response.PreferenciaDTO;
import co.edu.unbosque.mundial_2026.dto.response.UsuarioResponseDTO;
import co.edu.unbosque.mundial_2026.entity.CiudadFavorita;
import co.edu.unbosque.mundial_2026.entity.EstadioFavorito;
import co.edu.unbosque.mundial_2026.entity.Rol;
import co.edu.unbosque.mundial_2026.entity.Seleccion;
import co.edu.unbosque.mundial_2026.entity.Usuario;
import co.edu.unbosque.mundial_2026.exception.ContrasenaIncorrectaException;
import co.edu.unbosque.mundial_2026.exception.CorreoEnUsoException;
import co.edu.unbosque.mundial_2026.exception.RolNotFoundException;
import co.edu.unbosque.mundial_2026.exception.UsuarioNotFoundException;
import co.edu.unbosque.mundial_2026.repository.CiudadRepository;
import co.edu.unbosque.mundial_2026.repository.EstadioRepository;
import co.edu.unbosque.mundial_2026.repository.RolRepository;
import co.edu.unbosque.mundial_2026.repository.SeleccionRepository;
import co.edu.unbosque.mundial_2026.repository.UsuarioRepository;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceImplTest {

    @Mock
    private UsuarioRepository repository;

    @Mock
    private RolRepository rolRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private SeleccionRepository seleccionRepository;

    @Mock
    private EstadioRepository estadioRepository;

    @Mock
    private CiudadRepository ciudadRepository;

    @Mock
    private NotificacionService notificacionService;

    @Mock
    private EventoAuditoriaService auditoriaService;

    @InjectMocks
    private UsuarioServiceImpl usuarioService;

    private Usuario usuario;
    private Rol rolUsuario;

    @BeforeEach
    void setUp() {
        rolUsuario = new Rol();
        rolUsuario.setId(1L);
        rolUsuario.setNombre("ROLE_USUARIO");

        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setCorreoUsuario("test@test.com");
        usuario.setContrasena("hashed");
        usuario.setNombre("Juan");
        usuario.setApellido("Perez");
        usuario.setRol(rolUsuario);
        usuario.setActivo(true);
    }

    @Nested
    @DisplayName("listarTodos")
    class ListarTodos {

        @Test
        void cuandoHayUsuarios_retornaLista() {
            when(repository.findAll()).thenReturn(List.of(usuario));

            List<UsuarioResponseDTO> resultado = usuarioService.listarTodos();

            assertEquals(1, resultado.size());
            assertEquals("test@test.com", resultado.get(0).getCorreoUsuario());
        }

        @Test
        void cuandoNoHayUsuarios_retornaListaVacia() {
            when(repository.findAll()).thenReturn(Collections.emptyList());

            List<UsuarioResponseDTO> resultado = usuarioService.listarTodos();

            assertTrue(resultado.isEmpty());
        }
    }

    @Nested
    @DisplayName("registrarUsuario")
    class RegistrarUsuario {

        @Test
        void cuandoDatosValidos_registraYRetornaDTO() {
            UsuarioRequestDTO dto = new UsuarioRequestDTO();
            dto.setCorreoUsuario("nuevo@test.com");
            dto.setContrasena("Pass123!");
            dto.setNombre("Ana");
            dto.setApellido("Lopez");

            when(repository.findByCorreoUsuario("nuevo@test.com")).thenReturn(Optional.empty());
            when(rolRepository.findByNombre("ROLE_USUARIO")).thenReturn(Optional.of(rolUsuario));
            when(passwordEncoder.encode("Pass123!")).thenReturn("hashedPass");
            when(repository.save(any(Usuario.class))).thenAnswer(inv -> {
                Usuario u = inv.getArgument(0);
                u.setId(2L);
                return u;
            });

            UsuarioResponseDTO resultado = usuarioService.registrarUsuario(dto);

            assertNotNull(resultado);
            assertEquals("nuevo@test.com", resultado.getCorreoUsuario());
            verify(repository).save(any(Usuario.class));
            verify(auditoriaService).registrar(eq("USUARIO_REGISTRADO"), anyString(), eq(2L), anyString(), eq("Usuario"));
        }

        @Test
        void cuandoCorreoYaExiste_lanzaCorreoEnUsoException() {
            UsuarioRequestDTO dto = new UsuarioRequestDTO();
            dto.setCorreoUsuario("test@test.com");

            when(repository.findByCorreoUsuario("test@test.com")).thenReturn(Optional.of(usuario));

            assertThrows(CorreoEnUsoException.class, () -> usuarioService.registrarUsuario(dto));
            verify(repository, never()).save(any());
        }

        @Test
        void cuandoRolNoExiste_lanzaRolNotFoundException() {
            UsuarioRequestDTO dto = new UsuarioRequestDTO();
            dto.setCorreoUsuario("nuevo@test.com");
            dto.setContrasena("Pass123!");

            when(repository.findByCorreoUsuario("nuevo@test.com")).thenReturn(Optional.empty());
            when(rolRepository.findByNombre("ROLE_USUARIO")).thenReturn(Optional.empty());

            assertThrows(RolNotFoundException.class, () -> usuarioService.registrarUsuario(dto));
        }
    }

    @Nested
    @DisplayName("obtenerUsuario")
    class ObtenerUsuario {

        @Test
        void cuandoExiste_retornaDTO() {
            when(repository.findById(1L)).thenReturn(Optional.of(usuario));

            UsuarioResponseDTO resultado = usuarioService.obtenerUsuario(1L);

            assertEquals(1L, resultado.getId());
            assertEquals("test@test.com", resultado.getCorreoUsuario());
        }

        @Test
        void cuandoNoExiste_lanzaUsuarioNotFoundException() {
            when(repository.findById(99L)).thenReturn(Optional.empty());

            assertThrows(UsuarioNotFoundException.class, () -> usuarioService.obtenerUsuario(99L));
        }
    }

    @Nested
    @DisplayName("obtenerPorCorreo")
    class ObtenerPorCorreo {

        @Test
        void cuandoExiste_retornaDTO() {
            when(repository.findByCorreoUsuario("test@test.com")).thenReturn(Optional.of(usuario));

            UsuarioResponseDTO resultado = usuarioService.obtenerPorCorreo("test@test.com");

            assertEquals("test@test.com", resultado.getCorreoUsuario());
        }

        @Test
        void cuandoNoExiste_lanzaUsuarioNotFoundException() {
            when(repository.findByCorreoUsuario("nope@test.com")).thenReturn(Optional.empty());

            assertThrows(UsuarioNotFoundException.class, () -> usuarioService.obtenerPorCorreo("nope@test.com"));
        }
    }

    @Nested
    @DisplayName("registrarUsuarioComoAdmin")
    class RegistrarUsuarioComoAdmin {

        @Test
        void cuandoRolEspecificado_usaEseRol() {
            UsuarioRequestDTO dto = new UsuarioRequestDTO();
            dto.setCorreoUsuario("admin@test.com");
            dto.setContrasena("Pass123!");
            dto.setRol("ROLE_ADMIN");

            Rol rolAdmin = new Rol();
            rolAdmin.setNombre("ROLE_ADMIN");

            when(repository.findByCorreoUsuario("admin@test.com")).thenReturn(Optional.empty());
            when(rolRepository.findByNombre("ROLE_ADMIN")).thenReturn(Optional.of(rolAdmin));
            when(passwordEncoder.encode("Pass123!")).thenReturn("hashed");
            when(repository.save(any(Usuario.class))).thenAnswer(inv -> {
                Usuario u = inv.getArgument(0);
                u.setId(3L);
                return u;
            });

            UsuarioResponseDTO resultado = usuarioService.registrarUsuarioComoAdmin(dto);

            assertNotNull(resultado);
            assertEquals("ROLE_ADMIN", resultado.getRol());
        }

        @Test
        void cuandoRolNullOBlanco_usaRoleUsuarioPorDefecto() {
            UsuarioRequestDTO dto = new UsuarioRequestDTO();
            dto.setCorreoUsuario("admin@test.com");
            dto.setContrasena("Pass123!");
            dto.setRol(null);

            when(repository.findByCorreoUsuario("admin@test.com")).thenReturn(Optional.empty());
            when(rolRepository.findByNombre("ROLE_USUARIO")).thenReturn(Optional.of(rolUsuario));
            when(passwordEncoder.encode("Pass123!")).thenReturn("hashed");
            when(repository.save(any(Usuario.class))).thenAnswer(inv -> {
                Usuario u = inv.getArgument(0);
                u.setId(4L);
                return u;
            });

            UsuarioResponseDTO resultado = usuarioService.registrarUsuarioComoAdmin(dto);

            assertEquals("ROLE_USUARIO", resultado.getRol());
        }

        @Test
        void cuandoRolBlanco_usaRoleUsuarioPorDefecto() {
            UsuarioRequestDTO dto = new UsuarioRequestDTO();
            dto.setCorreoUsuario("admin@test.com");
            dto.setContrasena("Pass123!");
            dto.setRol("   ");

            when(repository.findByCorreoUsuario("admin@test.com")).thenReturn(Optional.empty());
            when(rolRepository.findByNombre("ROLE_USUARIO")).thenReturn(Optional.of(rolUsuario));
            when(passwordEncoder.encode("Pass123!")).thenReturn("hashed");
            when(repository.save(any(Usuario.class))).thenAnswer(inv -> {
                Usuario u = inv.getArgument(0);
                u.setId(5L);
                return u;
            });

            UsuarioResponseDTO resultado = usuarioService.registrarUsuarioComoAdmin(dto);

            assertEquals("ROLE_USUARIO", resultado.getRol());
        }

        @Test
        void cuandoCorreoYaExiste_lanzaCorreoEnUsoException() {
            UsuarioRequestDTO dto = new UsuarioRequestDTO();
            dto.setCorreoUsuario("test@test.com");

            when(repository.findByCorreoUsuario("test@test.com")).thenReturn(Optional.of(usuario));

            assertThrows(CorreoEnUsoException.class, () -> usuarioService.registrarUsuarioComoAdmin(dto));
        }

        @Test
        void cuandoRolNoExiste_lanzaRolNotFoundException() {
            UsuarioRequestDTO dto = new UsuarioRequestDTO();
            dto.setCorreoUsuario("nuevo@test.com");
            dto.setContrasena("Pass123!");
            dto.setRol("ROLE_INEXISTENTE");

            when(repository.findByCorreoUsuario("nuevo@test.com")).thenReturn(Optional.empty());
            when(rolRepository.findByNombre("ROLE_INEXISTENTE")).thenReturn(Optional.empty());

            assertThrows(RolNotFoundException.class, () -> usuarioService.registrarUsuarioComoAdmin(dto));
        }
    }

    @Nested
    @DisplayName("eliminarUsuario")
    class EliminarUsuario {

        @Test
        void cuandoExiste_desactivaUsuario() {
            when(repository.findById(1L)).thenReturn(Optional.of(usuario));

            usuarioService.eliminarUsuario(1L);

            assertFalse(usuario.isActivo());
            verify(repository).save(usuario);
            verify(auditoriaService).registrar(eq("USUARIO_ELIMINADO"), anyString(), eq(1L), anyString(), eq("Usuario"));
        }

        @Test
        void cuandoNoExiste_lanzaUsuarioNotFoundException() {
            when(repository.findById(99L)).thenReturn(Optional.empty());

            assertThrows(UsuarioNotFoundException.class, () -> usuarioService.eliminarUsuario(99L));
            verify(repository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("actualizarPerfil")
    class ActualizarPerfil {

        @Test
        void cuandoActualizaTodosLosCampos_funcionaCorrectamente() {
            UsuarioActualizarRequestDTO dto = new UsuarioActualizarRequestDTO();
            dto.setNombre("NuevoNombre");
            dto.setApellido("NuevoApellido");
            dto.setCorreoNuevo("nuevo@test.com");
            dto.setContrasenaActual("Pass123!");
            dto.setContrasenaNueva("NewPass456!");

            when(repository.findByCorreoUsuario("test@test.com")).thenReturn(Optional.of(usuario));
            when(repository.findByCorreoUsuario("nuevo@test.com")).thenReturn(Optional.empty());
            when(passwordEncoder.matches("Pass123!", "hashed")).thenReturn(true);
            when(passwordEncoder.encode("NewPass456!")).thenReturn("newHashed");
            when(repository.save(any(Usuario.class))).thenReturn(usuario);

            Map<String, Object> resultado = usuarioService.actualizarPerfil("test@test.com", dto);

            assertNotNull(resultado.get("usuario"));
            assertEquals(true, resultado.get("correocambio"));
            verify(notificacionService).notificarActualizacionPerfil(usuario);
        }

        @Test
        void cuandoSoloActualizaNombre_noCambiaCorreo() {
            UsuarioActualizarRequestDTO dto = new UsuarioActualizarRequestDTO();
            dto.setNombre("OtroNombre");

            when(repository.findByCorreoUsuario("test@test.com")).thenReturn(Optional.of(usuario));
            when(repository.save(any(Usuario.class))).thenReturn(usuario);

            Map<String, Object> resultado = usuarioService.actualizarPerfil("test@test.com", dto);

            assertEquals(false, resultado.get("correocambio"));
            assertEquals("OtroNombre", usuario.getNombre());
        }

        @Test
        void cuandoSoloActualizaApellido_funciona() {
            UsuarioActualizarRequestDTO dto = new UsuarioActualizarRequestDTO();
            dto.setApellido("OtroApellido");

            when(repository.findByCorreoUsuario("test@test.com")).thenReturn(Optional.of(usuario));
            when(repository.save(any(Usuario.class))).thenReturn(usuario);

            usuarioService.actualizarPerfil("test@test.com", dto);

            assertEquals("OtroApellido", usuario.getApellido());
        }

        @Test
        void cuandoCamposNullOBlancos_noCambia() {
            UsuarioActualizarRequestDTO dto = new UsuarioActualizarRequestDTO();
            dto.setNombre("");
            dto.setApellido("   ");
            dto.setCorreoNuevo(null);
            dto.setContrasenaNueva("");

            when(repository.findByCorreoUsuario("test@test.com")).thenReturn(Optional.of(usuario));
            when(repository.save(any(Usuario.class))).thenReturn(usuario);

            Map<String, Object> resultado = usuarioService.actualizarPerfil("test@test.com", dto);

            assertEquals("Juan", usuario.getNombre());
            assertEquals("Perez", usuario.getApellido());
            assertEquals(false, resultado.get("correocambio"));
        }

        @Test
        void cuandoUsuarioNoExiste_lanzaUsuarioNotFoundException() {
            UsuarioActualizarRequestDTO dto = new UsuarioActualizarRequestDTO();

            when(repository.findByCorreoUsuario("nope@test.com")).thenReturn(Optional.empty());

            assertThrows(UsuarioNotFoundException.class,
                    () -> usuarioService.actualizarPerfil("nope@test.com", dto));
        }

        @Test
        void cuandoCambiaContrasenaSinActual_lanzaContrasenaIncorrectaException() {
            UsuarioActualizarRequestDTO dto = new UsuarioActualizarRequestDTO();
            dto.setContrasenaNueva("NewPass!");
            dto.setContrasenaActual(null);

            when(repository.findByCorreoUsuario("test@test.com")).thenReturn(Optional.of(usuario));

            assertThrows(ContrasenaIncorrectaException.class,
                    () -> usuarioService.actualizarPerfil("test@test.com", dto));
        }

        @Test
        void cuandoContrasenaActualNoCoincide_lanzaContrasenaIncorrectaException() {
            UsuarioActualizarRequestDTO dto = new UsuarioActualizarRequestDTO();
            dto.setContrasenaNueva("NewPass!");
            dto.setContrasenaActual("WrongPass");

            when(repository.findByCorreoUsuario("test@test.com")).thenReturn(Optional.of(usuario));
            when(passwordEncoder.matches("WrongPass", "hashed")).thenReturn(false);

            assertThrows(ContrasenaIncorrectaException.class,
                    () -> usuarioService.actualizarPerfil("test@test.com", dto));
        }

        @Test
        void cuandoCambiaCorreoYNuevoYaExiste_lanzaCorreoEnUsoException() {
            UsuarioActualizarRequestDTO dto = new UsuarioActualizarRequestDTO();
            dto.setCorreoNuevo("ocupado@test.com");
            dto.setContrasenaActual("Pass123!");

            Usuario otro = new Usuario();
            otro.setCorreoUsuario("ocupado@test.com");

            when(repository.findByCorreoUsuario("test@test.com")).thenReturn(Optional.of(usuario));
            when(passwordEncoder.matches("Pass123!", "hashed")).thenReturn(true);
            when(repository.findByCorreoUsuario("ocupado@test.com")).thenReturn(Optional.of(otro));

            assertThrows(CorreoEnUsoException.class,
                    () -> usuarioService.actualizarPerfil("test@test.com", dto));
        }
    }

    @Nested
    @DisplayName("seleccionesUsuario")
    class SeleccionesUsuario {

        @Test
        void cuandoExiste_retornaSelecciones() {
            Seleccion sel = new Seleccion();
            sel.setId(10L);
            sel.setNombre("Colombia");
            usuario.setSeleccionesU(List.of(sel));

            when(repository.findByCorreoUsuario("test@test.com")).thenReturn(Optional.of(usuario));

            List<PreferenciaDTO> resultado = usuarioService.seleccionesUsuario("test@test.com");

            assertEquals(1, resultado.size());
            assertEquals("Colombia", resultado.get(0).getNombre());
        }

        @Test
        void cuandoNoExiste_lanzaUsuarioNotFoundException() {
            when(repository.findByCorreoUsuario("nope@test.com")).thenReturn(Optional.empty());

            assertThrows(UsuarioNotFoundException.class,
                    () -> usuarioService.seleccionesUsuario("nope@test.com"));
        }
    }

    @Nested
    @DisplayName("agregarSeleccion")
    class AgregarSeleccion {

        @Test
        void agregaTodasLasSelecciones() {
            when(repository.findIdByCorreo("test@test.com")).thenReturn(1L);

            usuarioService.agregarSeleccion("test@test.com", Arrays.asList(10L, 11L, 12L));

            verify(repository).insertarSeleccion(1L, 10L);
            verify(repository).insertarSeleccion(1L, 11L);
            verify(repository).insertarSeleccion(1L, 12L);
        }

        @Test
        void cuandoListaVacia_noLlamaInsertar() {
            when(repository.findIdByCorreo("test@test.com")).thenReturn(1L);

            usuarioService.agregarSeleccion("test@test.com", Collections.emptyList());

            verify(repository, never()).insertarSeleccion(anyLong(), anyLong());
        }
    }

    @Nested
    @DisplayName("eliminarSeleccion")
    class EliminarSeleccion {

        @Test
        void eliminaLaSeleccion() {
            when(repository.findIdByCorreo("test@test.com")).thenReturn(1L);

            usuarioService.eliminarSeleccion("test@test.com", 10L);

            verify(repository).eliminarSeleccion(1L, 10L);
        }
    }

    @Nested
    @DisplayName("estadiosUsuario")
    class EstadiosUsuario {

        @Test
        void cuandoExiste_retornaEstadios() {
            EstadioFavorito est = new EstadioFavorito();
            est.setId(20L);
            est.setNombre("Azteca");
            usuario.setPreferenciasu(List.of(est));

            when(repository.findByCorreoUsuario("test@test.com")).thenReturn(Optional.of(usuario));

            List<PreferenciaDTO> resultado = usuarioService.estadiosUsuario("test@test.com");

            assertEquals(1, resultado.size());
            assertEquals("Azteca", resultado.get(0).getNombre());
        }

        @Test
        void cuandoNoExiste_lanzaUsuarioNotFoundException() {
            when(repository.findByCorreoUsuario("nope@test.com")).thenReturn(Optional.empty());

            assertThrows(UsuarioNotFoundException.class,
                    () -> usuarioService.estadiosUsuario("nope@test.com"));
        }
    }

    @Nested
    @DisplayName("agregarEstadio y eliminarEstadio")
    class AgregarEliminarEstadio {

        @Test
        void agregarEstadio_agregaTodos() {
            when(repository.findIdByCorreo("test@test.com")).thenReturn(1L);

            usuarioService.agregarEstadio("test@test.com", Arrays.asList(20L, 21L));

            verify(repository).insertarEstadio(1L, 20L);
            verify(repository).insertarEstadio(1L, 21L);
        }

        @Test
        void eliminarEstadio_eliminaUno() {
            when(repository.findIdByCorreo("test@test.com")).thenReturn(1L);

            usuarioService.eliminarEstadio("test@test.com", 20L);

            verify(repository).eliminarEstadio(1L, 20L);
        }
    }

    @Nested
    @DisplayName("agregarCiudad y eliminarCiudad")
    class AgregarEliminarCiudad {

        @Test
        void agregarCiudad_agregaTodas() {
            when(repository.findIdByCorreo("test@test.com")).thenReturn(1L);

            usuarioService.agregarCiudad("test@test.com", Arrays.asList(30L, 31L));

            verify(repository).insertarCiudad(1L, 30L);
            verify(repository).insertarCiudad(1L, 31L);
        }

        @Test
        void eliminarCiudad_eliminaUna() {
            when(repository.findIdByCorreo("test@test.com")).thenReturn(1L);

            usuarioService.eliminarCiudad("test@test.com", 30L);

            verify(repository).eliminarCiudad(1L, 30L);
        }
    }

    @Nested
    @DisplayName("ciudadesUsuario")
    class CiudadesUsuario {

        @Test
        void cuandoExiste_retornaCiudades() {
            CiudadFavorita c = new CiudadFavorita();
            c.setId(30L);
            c.setNombre("Bogota");
            usuario.setCiudadFavoritas(List.of(c));

            when(repository.findByCorreoUsuario("test@test.com")).thenReturn(Optional.of(usuario));

            List<PreferenciaDTO> resultado = usuarioService.ciudadesUsuario("test@test.com");

            assertEquals(1, resultado.size());
            assertEquals("Bogota", resultado.get(0).getNombre());
        }

        @Test
        void cuandoNoExiste_lanzaUsuarioNotFoundException() {
            when(repository.findByCorreoUsuario("nope@test.com")).thenReturn(Optional.empty());

            assertThrows(UsuarioNotFoundException.class,
                    () -> usuarioService.ciudadesUsuario("nope@test.com"));
        }
    }

    @Nested
    @DisplayName("listarEstadios y listarCiudades")
    class ListarCatalogos {

        @Test
        void listarEstadios_retornaTodos() {
            EstadioFavorito e = new EstadioFavorito();
            e.setId(1L);
            e.setNombre("Azteca");
            when(estadioRepository.findAll()).thenReturn(List.of(e));

            List<PreferenciaDTO> resultado = usuarioService.listarEstadios();

            assertEquals(1, resultado.size());
            assertEquals("Azteca", resultado.get(0).getNombre());
        }

        @Test
        void listarCiudades_retornaTodas() {
            CiudadFavorita c = new CiudadFavorita();
            c.setId(1L);
            c.setNombre("Bogota");
            when(ciudadRepository.findAll()).thenReturn(List.of(c));

            List<PreferenciaDTO> resultado = usuarioService.listarCiudades();

            assertEquals(1, resultado.size());
            assertEquals("Bogota", resultado.get(0).getNombre());
        }
    }

    @Nested
    @DisplayName("obtenerEntidadPorId y obtenerEntidadPorCorreo")
    class ObtenerEntidad {

        @Test
        void obtenerEntidadPorId_cuandoExiste_retornaUsuario() {
            when(repository.findById(1L)).thenReturn(Optional.of(usuario));

            Usuario resultado = usuarioService.obtenerEntidadPorId(1L);

            assertEquals(1L, resultado.getId());
        }

        @Test
        void obtenerEntidadPorId_cuandoNoExiste_lanzaUsuarioNotFoundException() {
            when(repository.findById(99L)).thenReturn(Optional.empty());

            assertThrows(UsuarioNotFoundException.class, () -> usuarioService.obtenerEntidadPorId(99L));
        }

        @Test
        void obtenerEntidadPorCorreo_cuandoExiste_retornaUsuario() {
            when(repository.findByCorreoUsuario("test@test.com")).thenReturn(Optional.of(usuario));

            Usuario resultado = usuarioService.obtenerEntidadPorCorreo("test@test.com");

            assertEquals("test@test.com", resultado.getCorreoUsuario());
        }

        @Test
        void obtenerEntidadPorCorreo_cuandoNoExiste_lanzaUsuarioNotFoundException() {
            when(repository.findByCorreoUsuario("nope@test.com")).thenReturn(Optional.empty());

            assertThrows(UsuarioNotFoundException.class,
                    () -> usuarioService.obtenerEntidadPorCorreo("nope@test.com"));
        }
    }

    @Nested
    @DisplayName("actualizarFcmToken")
    class ActualizarFcmToken {

        @Test
        void cuandoEsPrimerToken_notificaRegistro() {
            usuario.setFcmtoken(null);

            when(repository.findByCorreoUsuario("test@test.com")).thenReturn(Optional.of(usuario));

            usuarioService.actualizarFcmToken("test@test.com", "newToken123");

            assertEquals("newToken123", usuario.getFcmtoken());
            verify(repository).save(usuario);
            verify(notificacionService).notificarRegistro(usuario);
        }

        @Test
        void cuandoTokenAnteriorBlanco_notificaRegistro() {
            usuario.setFcmtoken("   ");

            when(repository.findByCorreoUsuario("test@test.com")).thenReturn(Optional.of(usuario));

            usuarioService.actualizarFcmToken("test@test.com", "newToken123");

            verify(notificacionService).notificarRegistro(usuario);
        }

        @Test
        void cuandoYaTeniaToken_noNotifica() {
            usuario.setFcmtoken("tokenViejo");

            when(repository.findByCorreoUsuario("test@test.com")).thenReturn(Optional.of(usuario));

            usuarioService.actualizarFcmToken("test@test.com", "tokenNuevo");

            assertEquals("tokenNuevo", usuario.getFcmtoken());
            verify(notificacionService, never()).notificarRegistro(any());
        }

        @Test
        void cuandoUsuarioNoExiste_lanzaUsuarioNotFoundException() {
            when(repository.findByCorreoUsuario("nope@test.com")).thenReturn(Optional.empty());

            assertThrows(UsuarioNotFoundException.class,
                    () -> usuarioService.actualizarFcmToken("nope@test.com", "token"));
            verify(repository, never()).save(any());
        }
    }
}