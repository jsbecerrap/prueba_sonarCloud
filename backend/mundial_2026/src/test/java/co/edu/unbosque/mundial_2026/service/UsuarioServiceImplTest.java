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

/**
 * Pruebas unitarias para {@link UsuarioServiceImpl}
 * Verifica la logica de negocio del servicio de usuarios usando mocks de repositorios y servicios dependientes
 */
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

    /** Usuario de prueba activo usado como base en los tests */
    private Usuario usuario;

    /** Rol de usuario estandar usado en los tests de registro y actualizacion */
    private Rol rolUsuario;

    /** Correo principal del usuario de prueba usado en la mayoria de los tests */
    private static final String CORREO_TEST = "test@test.com";

    /** Correo inexistente usado en los tests que verifican usuario no encontrado */
    private static final String CORREO_NOPE = "nope@test.com";

    /** Correo nuevo usado en los tests de cambio de correo y registro */
    private static final String CORREO_NUEVO = "nuevo@test.com";

    /** Correo de administrador usado en los tests de registro por admin */
    private static final String CORREO_ADMIN = "admin@test.com";

    /** Contrasena de prueba en texto plano usada en los tests de registro y actualizacion */
    private static final String PASS_123 = "Pass123!";

    /** Contrasena hasheada de prueba almacenada en el usuario de prueba */
    private static final String HASHED = "hashed";

    /** Nombre del rol de usuario estandar usado en los tests de asignacion de roles */
    private static final String ROLE_USUARIO = "ROLE_USUARIO";

    /** Nombre del estadio de prueba usado en los tests de preferencias de estadios */
    private static final String AZTECA = "Azteca";

    /** Nombre del rol de administrador usado en los tests de registro por admin */
    private static final String ROLE_ADMIN = "ROLE_ADMIN";

    /** Nombre de ciudad de prueba usado en los tests de preferencias de ciudades */
    private static final String BOGOTA = "Bogota";

    @BeforeEach
    void setUp() {
        rolUsuario = new Rol();
        rolUsuario.setId(1L);
        rolUsuario.setNombre(ROLE_USUARIO);

        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setCorreoUsuario(CORREO_TEST);
        usuario.setContrasena(HASHED);
        usuario.setNombre("Juan");
        usuario.setApellido("Perez");
        usuario.setRol(rolUsuario);
        usuario.setActivo(true);
    }

    /**
     * Tests del metodo listarTodos — cubre listado con y sin usuarios
     */
    @Nested
    @DisplayName("listarTodos")
    class ListarTodos {

        /**
         * Verifica que cuando hay usuarios registrados se retorna la lista correctamente
         */
        @Test
        void cuandoHayUsuarios_retornaLista() {
            when(repository.findAll()).thenReturn(List.of(usuario));

            List<UsuarioResponseDTO> resultado = usuarioService.listarTodos();

            assertEquals(1, resultado.size());
            assertEquals(CORREO_TEST, resultado.get(0).getCorreoUsuario());
        }

        /**
         * Verifica que cuando no hay usuarios registrados se retorna lista vacia
         */
        @Test
        void cuandoNoHayUsuarios_retornaListaVacia() {
            when(repository.findAll()).thenReturn(Collections.emptyList());

            List<UsuarioResponseDTO> resultado = usuarioService.listarTodos();

            assertTrue(resultado.isEmpty());
        }
    }

    /**
     * Tests del metodo registrarUsuario — cubre registro exitoso, correo duplicado y rol inexistente
     */
    @Nested
    @DisplayName("registrarUsuario")
    class RegistrarUsuario {

        /**
         * Verifica que con datos validos se registra el usuario y se audita el evento
         */
        @Test
        void cuandoDatosValidos_registraYRetornaDTO() {
            UsuarioRequestDTO dto = new UsuarioRequestDTO();
            dto.setCorreoUsuario(CORREO_NUEVO);
            dto.setContrasena(PASS_123);
            dto.setNombre("Ana");
            dto.setApellido("Lopez");

            when(repository.findByCorreoUsuario(CORREO_NUEVO)).thenReturn(Optional.empty());
            when(rolRepository.findByNombre(ROLE_USUARIO)).thenReturn(Optional.of(rolUsuario));
            when(passwordEncoder.encode(PASS_123)).thenReturn("hashedPass");
            when(repository.save(any(Usuario.class))).thenAnswer(inv -> {
                Usuario u = inv.getArgument(0);
                u.setId(2L);
                return u;
            });

            UsuarioResponseDTO resultado = usuarioService.registrarUsuario(dto);

            assertNotNull(resultado);
            assertEquals(CORREO_NUEVO, resultado.getCorreoUsuario());
            verify(repository).save(any(Usuario.class));
            verify(auditoriaService).registrar(eq("USUARIO_REGISTRADO"), anyString(), eq(2L), anyString(), eq("Usuario"));
        }

        /**
         * Verifica que un correo ya registrado lanza {@link CorreoEnUsoException} sin persistir nada
         */
        @Test
        void cuandoCorreoYaExiste_lanzaCorreoEnUsoException() {
            UsuarioRequestDTO dto = new UsuarioRequestDTO();
            dto.setCorreoUsuario(CORREO_TEST);

            when(repository.findByCorreoUsuario(CORREO_TEST)).thenReturn(Optional.of(usuario));

            assertThrows(CorreoEnUsoException.class, () -> usuarioService.registrarUsuario(dto));
            verify(repository, never()).save(any());
        }

        /**
         * Verifica que un rol inexistente en el sistema lanza {@link RolNotFoundException}
         */
        @Test
        void cuandoRolNoExiste_lanzaRolNotFoundException() {
            UsuarioRequestDTO dto = new UsuarioRequestDTO();
            dto.setCorreoUsuario(CORREO_NUEVO);
            dto.setContrasena(PASS_123);

            when(repository.findByCorreoUsuario(CORREO_NUEVO)).thenReturn(Optional.empty());
            when(rolRepository.findByNombre(ROLE_USUARIO)).thenReturn(Optional.empty());

            assertThrows(RolNotFoundException.class, () -> usuarioService.registrarUsuario(dto));
        }
    }

    /**
     * Tests del metodo obtenerUsuario — cubre obtencion exitosa y no existente por ID
     */
    @Nested
    @DisplayName("obtenerUsuario")
    class ObtenerUsuario {

        /**
         * Verifica que un usuario existente retorna el DTO con ID y correo correctos
         */
        @Test
        void cuandoExiste_retornaDTO() {
            when(repository.findById(1L)).thenReturn(Optional.of(usuario));

            UsuarioResponseDTO resultado = usuarioService.obtenerUsuario(1L);

            assertEquals(1L, resultado.getId());
            assertEquals(CORREO_TEST, resultado.getCorreoUsuario());
        }

        /**
         * Verifica que un ID inexistente lanza {@link UsuarioNotFoundException}
         */
        @Test
        void cuandoNoExiste_lanzaUsuarioNotFoundException() {
            when(repository.findById(99L)).thenReturn(Optional.empty());

            assertThrows(UsuarioNotFoundException.class, () -> usuarioService.obtenerUsuario(99L));
        }
    }

    /**
     * Tests del metodo obtenerPorCorreo — cubre obtencion exitosa y no existente por correo
     */
    @Nested
    @DisplayName("obtenerPorCorreo")
    class ObtenerPorCorreo {

        /**
         * Verifica que un correo existente retorna el DTO con el correo correcto
         */
        @Test
        void cuandoExiste_retornaDTO() {
            when(repository.findByCorreoUsuario(CORREO_TEST)).thenReturn(Optional.of(usuario));

            UsuarioResponseDTO resultado = usuarioService.obtenerPorCorreo(CORREO_TEST);

            assertEquals(CORREO_TEST, resultado.getCorreoUsuario());
        }

        /**
         * Verifica que un correo inexistente lanza {@link UsuarioNotFoundException}
         */
        @Test
        void cuandoNoExiste_lanzaUsuarioNotFoundException() {
            when(repository.findByCorreoUsuario(CORREO_NOPE)).thenReturn(Optional.empty());

            assertThrows(UsuarioNotFoundException.class, () -> usuarioService.obtenerPorCorreo(CORREO_NOPE));
        }
    }

    /**
     * Tests del metodo registrarUsuarioComoAdmin — cubre asignacion de roles y casos de error
     */
    @Nested
    @DisplayName("registrarUsuarioComoAdmin")
    class RegistrarUsuarioComoAdmin {

        /**
         * Verifica que cuando se especifica un rol en el DTO se usa ese rol para el nuevo usuario
         */
        @Test
        void cuandoRolEspecificado_usaEseRol() {
            UsuarioRequestDTO dto = new UsuarioRequestDTO();
            dto.setCorreoUsuario(CORREO_ADMIN);
            dto.setContrasena(PASS_123);
            dto.setRol(ROLE_ADMIN);

            Rol rolAdmin = new Rol();
            rolAdmin.setNombre(ROLE_ADMIN);

            when(repository.findByCorreoUsuario(CORREO_ADMIN)).thenReturn(Optional.empty());
            when(rolRepository.findByNombre(ROLE_ADMIN)).thenReturn(Optional.of(rolAdmin));
            when(passwordEncoder.encode(PASS_123)).thenReturn(HASHED);
            when(repository.save(any(Usuario.class))).thenAnswer(inv -> {
                Usuario u = inv.getArgument(0);
                u.setId(3L);
                return u;
            });

            UsuarioResponseDTO resultado = usuarioService.registrarUsuarioComoAdmin(dto);

            assertNotNull(resultado);
            assertEquals(ROLE_ADMIN, resultado.getRol());
        }

        /**
         * Verifica que cuando el rol en el DTO es nulo se asigna ROLE_USUARIO por defecto
         */
        @Test
        void cuandoRolNullOBlanco_usaRoleUsuarioPorDefecto() {
            UsuarioRequestDTO dto = new UsuarioRequestDTO();
            dto.setCorreoUsuario(CORREO_ADMIN);
            dto.setContrasena(PASS_123);
            dto.setRol(null);

            when(repository.findByCorreoUsuario(CORREO_ADMIN)).thenReturn(Optional.empty());
            when(rolRepository.findByNombre(ROLE_USUARIO)).thenReturn(Optional.of(rolUsuario));
            when(passwordEncoder.encode(PASS_123)).thenReturn(HASHED);
            when(repository.save(any(Usuario.class))).thenAnswer(inv -> {
                Usuario u = inv.getArgument(0);
                u.setId(4L);
                return u;
            });

            UsuarioResponseDTO resultado = usuarioService.registrarUsuarioComoAdmin(dto);

            assertEquals(ROLE_USUARIO, resultado.getRol());
        }

        /**
         * Verifica que cuando el rol en el DTO es un string en blanco se asigna ROLE_USUARIO por defecto
         */
        @Test
        void cuandoRolBlanco_usaRoleUsuarioPorDefecto() {
            UsuarioRequestDTO dto = new UsuarioRequestDTO();
            dto.setCorreoUsuario(CORREO_ADMIN);
            dto.setContrasena(PASS_123);
            dto.setRol("   ");

            when(repository.findByCorreoUsuario(CORREO_ADMIN)).thenReturn(Optional.empty());
            when(rolRepository.findByNombre(ROLE_USUARIO)).thenReturn(Optional.of(rolUsuario));
            when(passwordEncoder.encode(PASS_123)).thenReturn(HASHED);
            when(repository.save(any(Usuario.class))).thenAnswer(inv -> {
                Usuario u = inv.getArgument(0);
                u.setId(5L);
                return u;
            });

            UsuarioResponseDTO resultado = usuarioService.registrarUsuarioComoAdmin(dto);

            assertEquals(ROLE_USUARIO, resultado.getRol());
        }

        /**
         * Verifica que un correo ya registrado lanza {@link CorreoEnUsoException}
         */
        @Test
        void cuandoCorreoYaExiste_lanzaCorreoEnUsoException() {
            UsuarioRequestDTO dto = new UsuarioRequestDTO();
            dto.setCorreoUsuario(CORREO_TEST);

            when(repository.findByCorreoUsuario(CORREO_TEST)).thenReturn(Optional.of(usuario));

            assertThrows(CorreoEnUsoException.class, () -> usuarioService.registrarUsuarioComoAdmin(dto));
        }

        /**
         * Verifica que un rol inexistente en el sistema lanza {@link RolNotFoundException}
         */
        @Test
        void cuandoRolNoExiste_lanzaRolNotFoundException() {
            UsuarioRequestDTO dto = new UsuarioRequestDTO();
            dto.setCorreoUsuario(CORREO_NUEVO);
            dto.setContrasena(PASS_123);
            dto.setRol("ROLE_INEXISTENTE");

            when(repository.findByCorreoUsuario(CORREO_NUEVO)).thenReturn(Optional.empty());
            when(rolRepository.findByNombre("ROLE_INEXISTENTE")).thenReturn(Optional.empty());

            assertThrows(RolNotFoundException.class, () -> usuarioService.registrarUsuarioComoAdmin(dto));
        }
    }

    /**
     * Tests del metodo eliminarUsuario — cubre desactivacion exitosa y usuario inexistente
     */
    @Nested
    @DisplayName("eliminarUsuario")
    class EliminarUsuario {

        /**
         * Verifica que eliminar un usuario existente lo desactiva y registra el evento de auditoria
         */
        @Test
        void cuandoExiste_desactivaUsuario() {
            when(repository.findById(1L)).thenReturn(Optional.of(usuario));

            usuarioService.eliminarUsuario(1L);

            assertFalse(usuario.isActivo());
            verify(repository).save(usuario);
            verify(auditoriaService).registrar(eq("USUARIO_ELIMINADO"), anyString(), eq(1L), anyString(), eq("Usuario"));
        }

        /**
         * Verifica que un ID inexistente lanza {@link UsuarioNotFoundException} sin persistir nada
         */
        @Test
        void cuandoNoExiste_lanzaUsuarioNotFoundException() {
            when(repository.findById(99L)).thenReturn(Optional.empty());

            assertThrows(UsuarioNotFoundException.class, () -> usuarioService.eliminarUsuario(99L));
            verify(repository, never()).save(any());
        }
    }

    /**
     * Tests del metodo actualizarPerfil — cubre actualizacion de campos, cambio de correo y casos de error
     */
    @Nested
    @DisplayName("actualizarPerfil")
    class ActualizarPerfil {

        /**
         * Verifica que actualizar todos los campos a la vez funciona correctamente y notifica al usuario
         */
        @Test
        void cuandoActualizaTodosLosCampos_funcionaCorrectamente() {
            UsuarioActualizarRequestDTO dto = new UsuarioActualizarRequestDTO();
            dto.setNombre("NuevoNombre");
            dto.setApellido("NuevoApellido");
            dto.setCorreoNuevo(CORREO_NUEVO);
            dto.setContrasenaActual(PASS_123);
            dto.setContrasenaNueva("NewPass456!");

            when(repository.findByCorreoUsuario(CORREO_TEST)).thenReturn(Optional.of(usuario));
            when(repository.findByCorreoUsuario(CORREO_NUEVO)).thenReturn(Optional.empty());
            when(passwordEncoder.matches(PASS_123, HASHED)).thenReturn(true);
            when(passwordEncoder.encode("NewPass456!")).thenReturn("newHashed");
            when(repository.save(any(Usuario.class))).thenReturn(usuario);

            Map<String, Object> resultado = usuarioService.actualizarPerfil(CORREO_TEST, dto);

            assertNotNull(resultado.get("usuario"));
            assertEquals(true, resultado.get("correocambio"));
            verify(notificacionService).notificarActualizacionPerfil(usuario);
        }

        /**
         * Verifica que actualizar solo el nombre no marca cambio de correo y actualiza el campo correctamente
         */
        @Test
        void cuandoSoloActualizaNombre_noCambiaCorreo() {
            UsuarioActualizarRequestDTO dto = new UsuarioActualizarRequestDTO();
            dto.setNombre("OtroNombre");

            when(repository.findByCorreoUsuario(CORREO_TEST)).thenReturn(Optional.of(usuario));
            when(repository.save(any(Usuario.class))).thenReturn(usuario);

            Map<String, Object> resultado = usuarioService.actualizarPerfil(CORREO_TEST, dto);

            assertEquals(false, resultado.get("correocambio"));
            assertEquals("OtroNombre", usuario.getNombre());
        }

        /**
         * Verifica que actualizar solo el apellido lo actualiza correctamente
         */
        @Test
        void cuandoSoloActualizaApellido_funciona() {
            UsuarioActualizarRequestDTO dto = new UsuarioActualizarRequestDTO();
            dto.setApellido("OtroApellido");

            when(repository.findByCorreoUsuario(CORREO_TEST)).thenReturn(Optional.of(usuario));
            when(repository.save(any(Usuario.class))).thenReturn(usuario);

            usuarioService.actualizarPerfil(CORREO_TEST, dto);

            assertEquals("OtroApellido", usuario.getApellido());
        }

        /**
         * Verifica que campos nulos o en blanco no sobreescriben los datos existentes del usuario
         */
        @Test
        void cuandoCamposNullOBlancos_noCambia() {
            UsuarioActualizarRequestDTO dto = new UsuarioActualizarRequestDTO();
            dto.setNombre("");
            dto.setApellido("   ");
            dto.setCorreoNuevo(null);
            dto.setContrasenaNueva("");

            when(repository.findByCorreoUsuario(CORREO_TEST)).thenReturn(Optional.of(usuario));
            when(repository.save(any(Usuario.class))).thenReturn(usuario);

            Map<String, Object> resultado = usuarioService.actualizarPerfil(CORREO_TEST, dto);

            assertEquals("Juan", usuario.getNombre());
            assertEquals("Perez", usuario.getApellido());
            assertEquals(false, resultado.get("correocambio"));
        }

        /**
         * Verifica que un correo inexistente lanza {@link UsuarioNotFoundException}
         */
        @Test
        void cuandoUsuarioNoExiste_lanzaUsuarioNotFoundException() {
            UsuarioActualizarRequestDTO dto = new UsuarioActualizarRequestDTO();

            when(repository.findByCorreoUsuario(CORREO_NOPE)).thenReturn(Optional.empty());

            assertThrows(UsuarioNotFoundException.class,
                    () -> usuarioService.actualizarPerfil(CORREO_NOPE, dto));
        }

        /**
         * Verifica que intentar cambiar contrasena sin proporcionar la actual lanza {@link ContrasenaIncorrectaException}
         */
        @Test
        void cuandoCambiaContrasenaSinActual_lanzaContrasenaIncorrectaException() {
            UsuarioActualizarRequestDTO dto = new UsuarioActualizarRequestDTO();
            dto.setContrasenaNueva("NewPass!");
            dto.setContrasenaActual(null);

            when(repository.findByCorreoUsuario(CORREO_TEST)).thenReturn(Optional.of(usuario));

            assertThrows(ContrasenaIncorrectaException.class,
                    () -> usuarioService.actualizarPerfil(CORREO_TEST, dto));
        }

        /**
         * Verifica que una contrasena actual incorrecta lanza {@link ContrasenaIncorrectaException}
         */
        @Test
        void cuandoContrasenaActualNoCoincide_lanzaContrasenaIncorrectaException() {
            UsuarioActualizarRequestDTO dto = new UsuarioActualizarRequestDTO();
            dto.setContrasenaNueva("NewPass!");
            dto.setContrasenaActual("WrongPass");

            when(repository.findByCorreoUsuario(CORREO_TEST)).thenReturn(Optional.of(usuario));
            when(passwordEncoder.matches("WrongPass", HASHED)).thenReturn(false);

            assertThrows(ContrasenaIncorrectaException.class,
                    () -> usuarioService.actualizarPerfil(CORREO_TEST, dto));
        }

        /**
         * Verifica que cambiar a un correo ya registrado por otro usuario lanza {@link CorreoEnUsoException}
         */
        @Test
        void cuandoCambiaCorreoYNuevoYaExiste_lanzaCorreoEnUsoException() {
            UsuarioActualizarRequestDTO dto = new UsuarioActualizarRequestDTO();
            dto.setCorreoNuevo("ocupado@test.com");
            dto.setContrasenaActual(PASS_123);

            Usuario otro = new Usuario();
            otro.setCorreoUsuario("ocupado@test.com");

            when(repository.findByCorreoUsuario(CORREO_TEST)).thenReturn(Optional.of(usuario));
            when(passwordEncoder.matches(PASS_123, HASHED)).thenReturn(true);
            when(repository.findByCorreoUsuario("ocupado@test.com")).thenReturn(Optional.of(otro));

            assertThrows(CorreoEnUsoException.class,
                    () -> usuarioService.actualizarPerfil(CORREO_TEST, dto));
        }
    }

    /**
     * Tests del metodo seleccionesUsuario — cubre listado exitoso y usuario inexistente
     */
    @Nested
    @DisplayName("seleccionesUsuario")
    class SeleccionesUsuario {

        /**
         * Verifica que se retornan las selecciones favoritas del usuario existente
         */
        @Test
        void cuandoExiste_retornaSelecciones() {
            Seleccion sel = new Seleccion();
            sel.setId(10L);
            sel.setNombre("Colombia");
            usuario.setSeleccionesU(List.of(sel));

            when(repository.findByCorreoUsuario(CORREO_TEST)).thenReturn(Optional.of(usuario));

            List<PreferenciaDTO> resultado = usuarioService.seleccionesUsuario(CORREO_TEST);

            assertEquals(1, resultado.size());
            assertEquals("Colombia", resultado.get(0).getNombre());
        }

        /**
         * Verifica que un correo inexistente lanza {@link UsuarioNotFoundException}
         */
        @Test
        void cuandoNoExiste_lanzaUsuarioNotFoundException() {
            when(repository.findByCorreoUsuario(CORREO_NOPE)).thenReturn(Optional.empty());

            assertThrows(UsuarioNotFoundException.class,
                    () -> usuarioService.seleccionesUsuario(CORREO_NOPE));
        }
    }

    /**
     * Tests del metodo agregarSeleccion — cubre insercion de multiples selecciones y lista vacia
     */
    @Nested
    @DisplayName("agregarSeleccion")
    class AgregarSeleccion {

        /**
         * Verifica que se llama insertar por cada seleccion en la lista proporcionada
         */
        @Test
        void agregaTodasLasSelecciones() {
            when(repository.findIdByCorreo(CORREO_TEST)).thenReturn(1L);

            usuarioService.agregarSeleccion(CORREO_TEST, Arrays.asList(10L, 11L, 12L));

            verify(repository).insertarSeleccion(1L, 10L);
            verify(repository).insertarSeleccion(1L, 11L);
            verify(repository).insertarSeleccion(1L, 12L);
        }

        /**
         * Verifica que una lista vacia no genera ninguna llamada al metodo insertar
         */
        @Test
        void cuandoListaVacia_noLlamaInsertar() {
            when(repository.findIdByCorreo(CORREO_TEST)).thenReturn(1L);

            usuarioService.agregarSeleccion(CORREO_TEST, Collections.emptyList());

            verify(repository, never()).insertarSeleccion(anyLong(), anyLong());
        }
    }

    /**
     * Tests del metodo eliminarSeleccion — verifica eliminacion de una seleccion favorita
     */
    @Nested
    @DisplayName("eliminarSeleccion")
    class EliminarSeleccion {

        /**
         * Verifica que se llama eliminarSeleccion con el ID de usuario y el ID de seleccion correctos
         */
        @Test
        void eliminaLaSeleccion() {
            when(repository.findIdByCorreo(CORREO_TEST)).thenReturn(1L);

            usuarioService.eliminarSeleccion(CORREO_TEST, 10L);

            verify(repository).eliminarSeleccion(1L, 10L);
        }
    }

    /**
     * Tests del metodo estadiosUsuario — cubre listado exitoso y usuario inexistente
     */
    @Nested
    @DisplayName("estadiosUsuario")
    class EstadiosUsuario {

        /**
         * Verifica que se retornan los estadios favoritos del usuario existente
         */
        @Test
        void cuandoExiste_retornaEstadios() {
            EstadioFavorito est = new EstadioFavorito();
            est.setId(20L);
            est.setNombre(AZTECA);
            usuario.setPreferenciasu(List.of(est));

            when(repository.findByCorreoUsuario(CORREO_TEST)).thenReturn(Optional.of(usuario));

            List<PreferenciaDTO> resultado = usuarioService.estadiosUsuario(CORREO_TEST);

            assertEquals(1, resultado.size());
            assertEquals(AZTECA, resultado.get(0).getNombre());
        }

        /**
         * Verifica que un correo inexistente lanza {@link UsuarioNotFoundException}
         */
        @Test
        void cuandoNoExiste_lanzaUsuarioNotFoundException() {
            when(repository.findByCorreoUsuario(CORREO_NOPE)).thenReturn(Optional.empty());

            assertThrows(UsuarioNotFoundException.class,
                    () -> usuarioService.estadiosUsuario(CORREO_NOPE));
        }
    }

    /**
     * Tests de los metodos agregarEstadio y eliminarEstadio — cubre insercion multiple y eliminacion unitaria
     */
    @Nested
    @DisplayName("agregarEstadio y eliminarEstadio")
    class AgregarEliminarEstadio {

        /**
         * Verifica que se llama insertarEstadio por cada estadio en la lista proporcionada
         */
        @Test
        void agregarEstadio_agregaTodos() {
            when(repository.findIdByCorreo(CORREO_TEST)).thenReturn(1L);

            usuarioService.agregarEstadio(CORREO_TEST, Arrays.asList(20L, 21L));

            verify(repository).insertarEstadio(1L, 20L);
            verify(repository).insertarEstadio(1L, 21L);
        }

        /**
         * Verifica que se llama eliminarEstadio con el ID de usuario y el ID de estadio correctos
         */
        @Test
        void eliminarEstadio_eliminaUno() {
            when(repository.findIdByCorreo(CORREO_TEST)).thenReturn(1L);

            usuarioService.eliminarEstadio(CORREO_TEST, 20L);

            verify(repository).eliminarEstadio(1L, 20L);
        }
    }

    /**
     * Tests de los metodos agregarCiudad y eliminarCiudad — cubre insercion multiple y eliminacion unitaria
     */
    @Nested
    @DisplayName("agregarCiudad y eliminarCiudad")
    class AgregarEliminarCiudad {

        /**
         * Verifica que se llama insertarCiudad por cada ciudad en la lista proporcionada
         */
        @Test
        void agregarCiudad_agregaTodas() {
            when(repository.findIdByCorreo(CORREO_TEST)).thenReturn(1L);

            usuarioService.agregarCiudad(CORREO_TEST, Arrays.asList(30L, 31L));

            verify(repository).insertarCiudad(1L, 30L);
            verify(repository).insertarCiudad(1L, 31L);
        }

        /**
         * Verifica que se llama eliminarCiudad con el ID de usuario y el ID de ciudad correctos
         */
        @Test
        void eliminarCiudad_eliminaUna() {
            when(repository.findIdByCorreo(CORREO_TEST)).thenReturn(1L);

            usuarioService.eliminarCiudad(CORREO_TEST, 30L);

            verify(repository).eliminarCiudad(1L, 30L);
        }
    }

    /**
     * Tests del metodo ciudadesUsuario — cubre listado exitoso y usuario inexistente
     */
    @Nested
    @DisplayName("ciudadesUsuario")
    class CiudadesUsuario {

        /**
         * Verifica que se retornan las ciudades favoritas del usuario existente
         */
        @Test
        void cuandoExiste_retornaCiudades() {
            CiudadFavorita c = new CiudadFavorita();
            c.setId(30L);
            c.setNombre(BOGOTA);
            usuario.setCiudadFavoritas(List.of(c));

            when(repository.findByCorreoUsuario(CORREO_TEST)).thenReturn(Optional.of(usuario));

            List<PreferenciaDTO> resultado = usuarioService.ciudadesUsuario(CORREO_TEST);

            assertEquals(1, resultado.size());
            assertEquals(BOGOTA, resultado.get(0).getNombre());
        }

        /**
         * Verifica que un correo inexistente lanza {@link UsuarioNotFoundException}
         */
        @Test
        void cuandoNoExiste_lanzaUsuarioNotFoundException() {
            when(repository.findByCorreoUsuario(CORREO_NOPE)).thenReturn(Optional.empty());

            assertThrows(UsuarioNotFoundException.class,
                    () -> usuarioService.ciudadesUsuario(CORREO_NOPE));
        }
    }

    /**
     * Tests de los metodos listarEstadios y listarCiudades — verifica listado de catalogos disponibles
     */
    @Nested
    @DisplayName("listarEstadios y listarCiudades")
    class ListarCatalogos {

        /**
         * Verifica que listarEstadios retorna todos los estadios disponibles en el catalogo
         */
        @Test
        void listarEstadios_retornaTodos() {
            EstadioFavorito e = new EstadioFavorito();
            e.setId(1L);
            e.setNombre(AZTECA);
            when(estadioRepository.findAll()).thenReturn(List.of(e));

            List<PreferenciaDTO> resultado = usuarioService.listarEstadios();

            assertEquals(1, resultado.size());
            assertEquals(AZTECA, resultado.get(0).getNombre());
        }

        /**
         * Verifica que listarCiudades retorna todas las ciudades disponibles en el catalogo
         */
        @Test
        void listarCiudades_retornaTodas() {
            CiudadFavorita c = new CiudadFavorita();
            c.setId(1L);
            c.setNombre(BOGOTA);
            when(ciudadRepository.findAll()).thenReturn(List.of(c));

            List<PreferenciaDTO> resultado = usuarioService.listarCiudades();

            assertEquals(1, resultado.size());
            assertEquals(BOGOTA, resultado.get(0).getNombre());
        }
    }

    /**
     * Tests de los metodos obtenerEntidadPorId y obtenerEntidadPorCorreo — cubre obtencion de entidad raw
     */
    @Nested
    @DisplayName("obtenerEntidadPorId y obtenerEntidadPorCorreo")
    class ObtenerEntidad {

        /**
         * Verifica que obtenerEntidadPorId retorna la entidad Usuario con el ID correcto
         */
        @Test
        void obtenerEntidadPorId_cuandoExiste_retornaUsuario() {
            when(repository.findById(1L)).thenReturn(Optional.of(usuario));

            Usuario resultado = usuarioService.obtenerEntidadPorId(1L);

            assertEquals(1L, resultado.getId());
        }

        /**
         * Verifica que obtenerEntidadPorId con ID inexistente lanza {@link UsuarioNotFoundException}
         */
        @Test
        void obtenerEntidadPorId_cuandoNoExiste_lanzaUsuarioNotFoundException() {
            when(repository.findById(99L)).thenReturn(Optional.empty());

            assertThrows(UsuarioNotFoundException.class, () -> usuarioService.obtenerEntidadPorId(99L));
        }

        /**
         * Verifica que obtenerEntidadPorCorreo retorna la entidad Usuario con el correo correcto
         */
        @Test
        void obtenerEntidadPorCorreo_cuandoExiste_retornaUsuario() {
            when(repository.findByCorreoUsuario(CORREO_TEST)).thenReturn(Optional.of(usuario));

            Usuario resultado = usuarioService.obtenerEntidadPorCorreo(CORREO_TEST);

            assertEquals(CORREO_TEST, resultado.getCorreoUsuario());
        }

        /**
         * Verifica que obtenerEntidadPorCorreo con correo inexistente lanza {@link UsuarioNotFoundException}
         */
        @Test
        void obtenerEntidadPorCorreo_cuandoNoExiste_lanzaUsuarioNotFoundException() {
            when(repository.findByCorreoUsuario(CORREO_NOPE)).thenReturn(Optional.empty());

            assertThrows(UsuarioNotFoundException.class,
                    () -> usuarioService.obtenerEntidadPorCorreo(CORREO_NOPE));
        }
    }

    /**
     * Tests del metodo actualizarFcmToken — cubre primer token, token en blanco, actualizacion y usuario inexistente
     */
    @Nested
    @DisplayName("actualizarFcmToken")
    class ActualizarFcmToken {

        /**
         * Verifica que guardar el primer token FCM del usuario dispara la notificacion de registro
         */
        @Test
        void cuandoEsPrimerToken_notificaRegistro() {
            usuario.setFcmtoken(null);

            when(repository.findByCorreoUsuario(CORREO_TEST)).thenReturn(Optional.of(usuario));

            usuarioService.actualizarFcmToken(CORREO_TEST, "newToken123");

            assertEquals("newToken123", usuario.getFcmtoken());
            verify(repository).save(usuario);
            verify(notificacionService).notificarRegistro(usuario);
        }

        /**
         * Verifica que un token anterior en blanco tambien dispara la notificacion de registro
         */
        @Test
        void cuandoTokenAnteriorBlanco_notificaRegistro() {
            usuario.setFcmtoken("   ");

            when(repository.findByCorreoUsuario(CORREO_TEST)).thenReturn(Optional.of(usuario));

            usuarioService.actualizarFcmToken(CORREO_TEST, "newToken123");

            verify(notificacionService).notificarRegistro(usuario);
        }

        /**
         * Verifica que actualizar un token existente no dispara la notificacion de registro
         */
        @Test
        void cuandoYaTeniaToken_noNotifica() {
            usuario.setFcmtoken("tokenViejo");

            when(repository.findByCorreoUsuario(CORREO_TEST)).thenReturn(Optional.of(usuario));

            usuarioService.actualizarFcmToken(CORREO_TEST, "tokenNuevo");

            assertEquals("tokenNuevo", usuario.getFcmtoken());
            verify(notificacionService, never()).notificarRegistro(any());
        }

        /**
         * Verifica que un correo inexistente lanza {@link UsuarioNotFoundException} sin persistir nada
         */
        @Test
        void cuandoUsuarioNoExiste_lanzaUsuarioNotFoundException() {
            when(repository.findByCorreoUsuario(CORREO_NOPE)).thenReturn(Optional.empty());

            assertThrows(UsuarioNotFoundException.class,
                    () -> usuarioService.actualizarFcmToken(CORREO_NOPE, "token"));
            verify(repository, never()).save(any());
        }
    }
}