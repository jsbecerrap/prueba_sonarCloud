package co.edu.unbosque.mundial_2026.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import co.edu.unbosque.mundial_2026.dto.request.UsuarioActualizarRequestDTO;
import co.edu.unbosque.mundial_2026.dto.request.UsuarioRequestDTO;
import co.edu.unbosque.mundial_2026.dto.response.PreferenciaDTO;
import co.edu.unbosque.mundial_2026.dto.response.UsuarioResponseDTO;
import co.edu.unbosque.mundial_2026.exception.UsuarioNotFoundException;
import co.edu.unbosque.mundial_2026.security.TokenBlacklist;
import co.edu.unbosque.mundial_2026.service.UsuarioService;

/**
 * Pruebas unitarias para {@link UsuarioController}
 * Verifica el comportamiento del controlador de usuarios usando mocks de {@link UsuarioService}
 * y {@link TokenBlacklist}, con contexto de seguridad simulado para endpoints del usuario autenticado
 */
@ExtendWith(MockitoExtension.class)
class UsuarioRestControllerTest {

    @Mock private UsuarioService service;
    @Mock private TokenBlacklist tokenBlacklist;
    @InjectMocks private UsuarioController controller;

    @Mock private SecurityContext securityContext;
    @Mock private Authentication authentication;

    /** Correo del usuario autenticado simulado en el SecurityContext para todos los tests */
    private static final String USER_CORREO = "user@test.com";

    /** Nombre de prueba usado en la construccion de DTOs de usuario */
    private static final String NOMBRE = "Juan";

    /** Apellido de prueba usado en la construccion de DTOs de usuario */
    private static final String APELLIDO = "Perez";

    /** Mensaje de error usado en las excepciones de usuario no encontrado */
    private static final String M_NO_ENCONTRADO = "no encontrado";

    /** Clave del mapa de respuesta que contiene mensajes informativos al usuario */
    private static final String MENSAJE = "mensaje";

    /** Clave del mapa de resultado del servicio que indica si hubo cambio de correo */
    private static final String CORREO_CAMBIO = "correocambio";

    /** Nombre del header HTTP de autorizacion usado en los tests de logout y actualizacion */
    private static final String AUTHORIZATION = "Authorization";

    /** Clave del mapa de resultado del servicio que contiene el DTO del usuario actualizado */
    private static final String USUARIO_KEY = "usuario";

    /** Nuevo correo usado en los tests de actualizacion de perfil con cambio de correo */
    private static final String NUEVO_CORREO = "nuevo@test.com";

    @BeforeEach
    void setUpSecurityContext() {
        lenient().when(securityContext.getAuthentication()).thenReturn(authentication);
        lenient().when(authentication.getName()).thenReturn(USER_CORREO);
        SecurityContextHolder.setContext(securityContext);
    }

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    private UsuarioResponseDTO responseDTO(Long id, String correo) {
        UsuarioResponseDTO dto = new UsuarioResponseDTO();
        dto.setId(id);
        dto.setCorreoUsuario(correo);
        dto.setNombre(NOMBRE);
        dto.setApellido(APELLIDO);
        return dto;
    }

    private UsuarioRequestDTO requestDTO() {
        UsuarioRequestDTO dto = new UsuarioRequestDTO();
        dto.setNombre(NOMBRE);
        dto.setApellido(APELLIDO);
        dto.setCorreoUsuario(USER_CORREO);
        dto.setContrasena("pass123");
        return dto;
    }

    private UsuarioActualizarRequestDTO actualizarDTO(String correoNuevo) {
        UsuarioActualizarRequestDTO dto = new UsuarioActualizarRequestDTO();
        dto.setNombre(NOMBRE);
        dto.setApellido(APELLIDO);
        dto.setCorreoNuevo(correoNuevo);
        dto.setContrasenaActual("pass123");
        return dto;
    }

    /**
     * Verifica que listar todos los usuarios retorna HTTP 200 y la lista contiene exactamente un elemento
     */
    @Test
    void listarTodos_retornaOkConLista() {
        when(service.listarTodos()).thenReturn(List.of(responseDTO(1L, USER_CORREO)));

        ResponseEntity<List<UsuarioResponseDTO>> res = controller.listarTodos();

        assertEquals(200, res.getStatusCode().value());
        assertEquals(1, res.getBody().size());
        verify(service).listarTodos();
    }

    /**
     * Verifica que listar usuarios cuando no hay ninguno retorna HTTP 200 con lista vacia
     */
    @Test
    void listarTodos_listaVacia_retornaOkVacio() {
        when(service.listarTodos()).thenReturn(List.of());

        ResponseEntity<List<UsuarioResponseDTO>> res = controller.listarTodos();

        assertEquals(200, res.getStatusCode().value());
        assertTrue(res.getBody().isEmpty());
    }

    /**
     * Verifica que obtener un usuario existente por ID retorna HTTP 200
     * y el ID del DTO coincide con el solicitado
     */
    @Test
    void obtenerUsuario_existente_retornaOk() {
        when(service.obtenerUsuario(1L)).thenReturn(responseDTO(1L, USER_CORREO));

        ResponseEntity<UsuarioResponseDTO> res = controller.obtenerUsuario(1L);

        assertEquals(200, res.getStatusCode().value());
        assertEquals(1L, res.getBody().getId());
        verify(service).obtenerUsuario(1L);
    }

    /**
     * Verifica que obtener un usuario inexistente lanza {@link UsuarioNotFoundException}
     */
    @Test
    void obtenerUsuario_noExistente_propagaExcepcion() {
        when(service.obtenerUsuario(99L)).thenThrow(new UsuarioNotFoundException(M_NO_ENCONTRADO));

        assertThrows(UsuarioNotFoundException.class, () -> controller.obtenerUsuario(99L));
    }

    /**
     * Verifica que obtener el perfil del usuario autenticado retorna HTTP 200
     * y el correo del DTO coincide con el del usuario en sesion
     */
    @Test
    void obtenerPerfil_retornaOkConDatosDelAutenticado() {
        when(service.obtenerPorCorreo(USER_CORREO)).thenReturn(responseDTO(1L, USER_CORREO));

        ResponseEntity<UsuarioResponseDTO> res = controller.obtenerPerfil();

        assertEquals(200, res.getStatusCode().value());
        assertEquals(USER_CORREO, res.getBody().getCorreoUsuario());
        verify(service).obtenerPorCorreo(USER_CORREO);
    }

    /**
     * Verifica que obtener el perfil cuando el usuario autenticado no existe lanza {@link UsuarioNotFoundException}
     */
    @Test
    void obtenerPerfil_usuarioNoExistente_propagaExcepcion() {
        when(service.obtenerPorCorreo(USER_CORREO)).thenThrow(new UsuarioNotFoundException(M_NO_ENCONTRADO));

        assertThrows(UsuarioNotFoundException.class, () -> controller.obtenerPerfil());
    }

    /**
     * Verifica que registrar un usuario con datos validos retorna HTTP 201 y el cuerpo no es nulo
     */
    @Test
    void registrarUsuario_exitoso_retorna201() {
        UsuarioRequestDTO dto = requestDTO();
        when(service.registrarUsuario(dto)).thenReturn(responseDTO(1L, USER_CORREO));

        ResponseEntity<UsuarioResponseDTO> res = controller.registrarUsuario(dto);

        assertEquals(201, res.getStatusCode().value());
        assertNotNull(res.getBody());
        verify(service).registrarUsuario(dto);
    }

    /**
     * Verifica que registrar un usuario por un administrador retorna HTTP 201 y el cuerpo no es nulo
     */
    @Test
    void registrarUsuarioPorAdmin_exitoso_retorna201() {
        UsuarioRequestDTO dto = requestDTO();
        when(service.registrarUsuarioComoAdmin(dto)).thenReturn(responseDTO(2L, "admin@test.com"));

        ResponseEntity<UsuarioResponseDTO> res = controller.registrarUsuarioPorAdmin(dto);

        assertEquals(201, res.getStatusCode().value());
        assertNotNull(res.getBody());
        verify(service).registrarUsuarioComoAdmin(dto);
    }

    /**
     * Verifica que eliminar un usuario existente retorna HTTP 204 y el cuerpo es nulo
     */
    @Test
    void eliminarUsuario_exitoso_retorna204() {
        doNothing().when(service).eliminarUsuario(1L);

        ResponseEntity<Void> res = controller.eliminarUsuario(1L);

        assertEquals(204, res.getStatusCode().value());
        assertNull(res.getBody());
        verify(service).eliminarUsuario(1L);
    }

    /**
     * Verifica que eliminar un usuario inexistente lanza {@link UsuarioNotFoundException}
     */
    @Test
    void eliminarUsuario_noExistente_propagaExcepcion() {
        doThrow(new UsuarioNotFoundException(M_NO_ENCONTRADO)).when(service).eliminarUsuario(99L);

        assertThrows(UsuarioNotFoundException.class, () -> controller.eliminarUsuario(99L));
    }

    /**
     * Verifica que actualizar el perfil sin cambio de correo retorna HTTP 200
     * y no interactua con la blacklist de tokens
     */
    @Test
    void actualizarPerfil_sinCambioDeCorrco_retornaOkConDTO() {
        Map<String, Object> resultado = new HashMap<>();
        resultado.put(CORREO_CAMBIO, false);
        resultado.put(USUARIO_KEY, responseDTO(1L, USER_CORREO));
        when(service.actualizarPerfil(eq(USER_CORREO), any())).thenReturn(resultado);

        MockHttpServletRequest request = new MockHttpServletRequest();
        ResponseEntity<Object> res = controller.actualizarPerfil(actualizarDTO(null), request);

        assertEquals(200, res.getStatusCode().value());
        assertNotNull(res.getBody());
        verifyNoInteractions(tokenBlacklist);
    }

    /**
     * Verifica que actualizar el perfil con cambio de correo invalida el token actual
     * y retorna HTTP 200 con un mensaje indicando que debe iniciar sesion nuevamente
     */
    @Test
    void actualizarPerfil_conCambioDeCorreo_invalidaTokenYRetornaMensaje() {
        Map<String, Object> resultado = new HashMap<>();
        resultado.put(CORREO_CAMBIO, true);
        resultado.put(USUARIO_KEY, responseDTO(1L, NUEVO_CORREO));
        when(service.actualizarPerfil(eq(USER_CORREO), any())).thenReturn(resultado);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(AUTHORIZATION, "Bearer token-valido");

        ResponseEntity<Object> res = controller.actualizarPerfil(actualizarDTO(NUEVO_CORREO), request);

        assertEquals(200, res.getStatusCode().value());
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) res.getBody();
        assertTrue(body.containsKey(MENSAJE));
        assertEquals("Correo actualizado, inicia sesión nuevamente", body.get(MENSAJE));
        verify(tokenBlacklist).agregar("token-valido");
    }

    /**
     * Verifica que actualizar el perfil con cambio de correo pero sin header Authorization
     * retorna HTTP 200 sin intentar invalidar ningun token
     */
    @Test
    void actualizarPerfil_conCambioDeCorreo_sinHeader_noInvalidaToken() {
        Map<String, Object> resultado = new HashMap<>();
        resultado.put(CORREO_CAMBIO, true);
        resultado.put(USUARIO_KEY, responseDTO(1L, NUEVO_CORREO));
        when(service.actualizarPerfil(eq(USER_CORREO), any())).thenReturn(resultado);

        MockHttpServletRequest request = new MockHttpServletRequest();

        ResponseEntity<Object> res = controller.actualizarPerfil(actualizarDTO(NUEVO_CORREO), request);

        assertEquals(200, res.getStatusCode().value());
        verifyNoInteractions(tokenBlacklist);
    }

    /**
     * Verifica que actualizar el perfil con cambio de correo y header sin prefijo Bearer
     * retorna HTTP 200 sin intentar invalidar ningun token
     */
    @Test
    void actualizarPerfil_conCambioDeCorreo_headerSinPrefijo_noInvalidaToken() {
        Map<String, Object> resultado = new HashMap<>();
        resultado.put(CORREO_CAMBIO, true);
        resultado.put(USUARIO_KEY, responseDTO(1L, NUEVO_CORREO));
        when(service.actualizarPerfil(eq(USER_CORREO), any())).thenReturn(resultado);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(AUTHORIZATION, "token-sin-prefijo");

        ResponseEntity<Object> res = controller.actualizarPerfil(actualizarDTO(NUEVO_CORREO), request);

        assertEquals(200, res.getStatusCode().value());
        verifyNoInteractions(tokenBlacklist);
    }

    /**
     * Verifica que hacer logout con un token Bearer valido lo invalida en la blacklist
     * y retorna HTTP 200 con mensaje de sesion cerrada
     */
    @Test
    void logout_conToken_invalidaTokenYRetornaMensaje() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(AUTHORIZATION, "Bearer token-de-sesion");

        ResponseEntity<Object> res = controller.logout(request);

        assertEquals(200, res.getStatusCode().value());
        @SuppressWarnings("unchecked")
        Map<String, String> body = (Map<String, String>) res.getBody();
        assertEquals("Sesión cerrada correctamente", body.get(MENSAJE));
        verify(tokenBlacklist).agregar("token-de-sesion");
    }

    /**
     * Verifica que hacer logout sin header Authorization retorna HTTP 200
     * sin intentar invalidar ningun token
     */
    @Test
    void logout_sinHeader_noInvalidaTokenYRetornaMensaje() {
        MockHttpServletRequest request = new MockHttpServletRequest();

        ResponseEntity<Object> res = controller.logout(request);

        assertEquals(200, res.getStatusCode().value());
        verifyNoInteractions(tokenBlacklist);
    }

    /**
     * Verifica que hacer logout con header sin prefijo Bearer retorna HTTP 200
     * sin intentar invalidar ningun token
     */
    @Test
    void logout_headerSinPrefijo_noInvalidaToken() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(AUTHORIZATION, "token-sin-bearer");

        ResponseEntity<Object> res = controller.logout(request);

        assertEquals(200, res.getStatusCode().value());
        verifyNoInteractions(tokenBlacklist);
    }

    /**
     * Verifica que obtener las selecciones favoritas del usuario autenticado retorna HTTP 200
     * y la lista contiene exactamente un elemento
     */
    @Test
    void obtenerSelecciones_retornaOkConLista() {
        when(service.seleccionesUsuario(USER_CORREO))
                .thenReturn(List.of(new PreferenciaDTO(1L, "Colombia")));

        ResponseEntity<List<PreferenciaDTO>> res = controller.obtenerSelecciones();

        assertEquals(200, res.getStatusCode().value());
        assertEquals(1, res.getBody().size());
        verify(service).seleccionesUsuario(USER_CORREO);
    }

    /**
     * Verifica que obtener selecciones favoritas cuando el usuario no tiene ninguna retorna HTTP 200 con lista vacia
     */
    @Test
    void obtenerSelecciones_listaVacia_retornaOkVacio() {
        when(service.seleccionesUsuario(USER_CORREO)).thenReturn(List.of());

        ResponseEntity<List<PreferenciaDTO>> res = controller.obtenerSelecciones();

        assertEquals(200, res.getStatusCode().value());
        assertTrue(res.getBody().isEmpty());
    }

    /**
     * Verifica que agregar selecciones favoritas al usuario autenticado retorna HTTP 204
     */
    @Test
    void agregarSeleccion_exitoso_retorna204() {
        doNothing().when(service).agregarSeleccion(USER_CORREO, List.of(1L, 2L));

        ResponseEntity<Void> res = controller.agregarSeleccion(List.of(1L, 2L));

        assertEquals(204, res.getStatusCode().value());
        verify(service).agregarSeleccion(USER_CORREO, List.of(1L, 2L));
    }

    /**
     * Verifica que eliminar una seleccion favorita del usuario autenticado retorna HTTP 204
     */
    @Test
    void eliminarSeleccion_exitoso_retorna204() {
        doNothing().when(service).eliminarSeleccion(USER_CORREO, 1L);

        ResponseEntity<Void> res = controller.eliminarSeleccion(1L);

        assertEquals(204, res.getStatusCode().value());
        verify(service).eliminarSeleccion(USER_CORREO, 1L);
    }

    /**
     * Verifica que obtener los estadios favoritos del usuario autenticado retorna HTTP 200
     * y la lista contiene exactamente un elemento
     */
    @Test
    void obtenerEstadios_retornaOkConLista() {
        when(service.estadiosUsuario(USER_CORREO))
                .thenReturn(List.of(new PreferenciaDTO(1L, "MetLife Stadium")));

        ResponseEntity<List<PreferenciaDTO>> res = controller.obtenerEstadios();

        assertEquals(200, res.getStatusCode().value());
        assertEquals(1, res.getBody().size());
        verify(service).estadiosUsuario(USER_CORREO);
    }

    /**
     * Verifica que obtener estadios favoritos cuando el usuario no tiene ninguno retorna HTTP 200 con lista vacia
     */
    @Test
    void obtenerEstadios_listaVacia_retornaOkVacio() {
        when(service.estadiosUsuario(USER_CORREO)).thenReturn(List.of());

        ResponseEntity<List<PreferenciaDTO>> res = controller.obtenerEstadios();

        assertEquals(200, res.getStatusCode().value());
        assertTrue(res.getBody().isEmpty());
    }

    /**
     * Verifica que agregar estadios favoritos al usuario autenticado retorna HTTP 204
     */
    @Test
    void agregarEstadio_exitoso_retorna204() {
        doNothing().when(service).agregarEstadio(USER_CORREO, List.of(1L));

        ResponseEntity<Void> res = controller.agregarEstadio(List.of(1L));

        assertEquals(204, res.getStatusCode().value());
        verify(service).agregarEstadio(USER_CORREO, List.of(1L));
    }

    /**
     * Verifica que eliminar un estadio favorito del usuario autenticado retorna HTTP 204
     */
    @Test
    void eliminarEstadio_exitoso_retorna204() {
        doNothing().when(service).eliminarEstadio(USER_CORREO, 1L);

        ResponseEntity<Void> res = controller.eliminarEstadio(1L);

        assertEquals(204, res.getStatusCode().value());
        verify(service).eliminarEstadio(USER_CORREO, 1L);
    }

    /**
     * Verifica que obtener las ciudades favoritas del usuario autenticado retorna HTTP 200
     * y la lista contiene exactamente un elemento
     */
    @Test
    void obtenerCiudades_retornaOkConLista() {
        when(service.ciudadesUsuario(USER_CORREO))
                .thenReturn(List.of(new PreferenciaDTO(1L, "Bogotá")));

        ResponseEntity<List<PreferenciaDTO>> res = controller.obtenerCiudades();

        assertEquals(200, res.getStatusCode().value());
        assertEquals(1, res.getBody().size());
        verify(service).ciudadesUsuario(USER_CORREO);
    }

    /**
     * Verifica que obtener ciudades favoritas cuando el usuario no tiene ninguna retorna HTTP 200 con lista vacia
     */
    @Test
    void obtenerCiudades_listaVacia_retornaOkVacio() {
        when(service.ciudadesUsuario(USER_CORREO)).thenReturn(List.of());

        ResponseEntity<List<PreferenciaDTO>> res = controller.obtenerCiudades();

        assertEquals(200, res.getStatusCode().value());
        assertTrue(res.getBody().isEmpty());
    }

    /**
     * Verifica que agregar ciudades favoritas al usuario autenticado retorna HTTP 204
     */
    @Test
    void agregarCiudad_exitoso_retorna204() {
        doNothing().when(service).agregarCiudad(USER_CORREO, List.of(1L));

        ResponseEntity<Void> res = controller.agregarCiudad(List.of(1L));

        assertEquals(204, res.getStatusCode().value());
        verify(service).agregarCiudad(USER_CORREO, List.of(1L));
    }

    /**
     * Verifica que eliminar una ciudad favorita del usuario autenticado retorna HTTP 204
     */
    @Test
    void eliminarCiudad_exitoso_retorna204() {
        doNothing().when(service).eliminarCiudad(USER_CORREO, 1L);

        ResponseEntity<Void> res = controller.eliminarCiudad(1L);

        assertEquals(204, res.getStatusCode().value());
        verify(service).eliminarCiudad(USER_CORREO, 1L);
    }

    /**
     * Verifica que listar todos los estadios disponibles retorna HTTP 200
     * y la lista contiene exactamente un elemento
     */
    @Test
    void listarEstadios_retornaOkConLista() {
        when(service.listarEstadios()).thenReturn(List.of(new PreferenciaDTO(1L, "MetLife Stadium")));

        ResponseEntity<List<PreferenciaDTO>> res = controller.listarEstadios();

        assertEquals(200, res.getStatusCode().value());
        assertEquals(1, res.getBody().size());
        verify(service).listarEstadios();
    }

    /**
     * Verifica que listar estadios cuando no hay ninguno retorna HTTP 200 con lista vacia
     */
    @Test
    void listarEstadios_listaVacia_retornaOkVacio() {
        when(service.listarEstadios()).thenReturn(List.of());

        ResponseEntity<List<PreferenciaDTO>> res = controller.listarEstadios();

        assertEquals(200, res.getStatusCode().value());
        assertTrue(res.getBody().isEmpty());
    }

    /**
     * Verifica que listar todas las ciudades disponibles retorna HTTP 200
     * y la lista contiene exactamente un elemento
     */
    @Test
    void listarCiudades_retornaOkConLista() {
        when(service.listarCiudades()).thenReturn(List.of(new PreferenciaDTO(1L, "East Rutherford")));

        ResponseEntity<List<PreferenciaDTO>> res = controller.listarCiudades();

        assertEquals(200, res.getStatusCode().value());
        assertEquals(1, res.getBody().size());
        verify(service).listarCiudades();
    }

    /**
     * Verifica que listar ciudades cuando no hay ninguna retorna HTTP 200 con lista vacia
     */
    @Test
    void listarCiudades_listaVacia_retornaOkVacio() {
        when(service.listarCiudades()).thenReturn(List.of());

        ResponseEntity<List<PreferenciaDTO>> res = controller.listarCiudades();

        assertEquals(200, res.getStatusCode().value());
        assertTrue(res.getBody().isEmpty());
    }

    /**
     * Verifica que actualizar el token FCM del usuario autenticado retorna HTTP 204
     */
    @Test
    void actualizarFcmToken_exitoso_retorna204() {
        doNothing().when(service).actualizarFcmToken(USER_CORREO, "token-fcm-nuevo");
        Map<String, String> body = Map.of("fcmToken", "token-fcm-nuevo");

        ResponseEntity<Void> res = controller.actualizarFcmToken(body);

        assertEquals(204, res.getStatusCode().value());
        verify(service).actualizarFcmToken(USER_CORREO, "token-fcm-nuevo");
    }

    /**
     * Verifica que obtener el nombre de un usuario existente retorna HTTP 200
     * y el mapa contiene el nombre y apellido correctos
     */
    @Test
    void obtenerNombreUsuario_existente_retornaOkConNombreYApellido() {
        when(service.obtenerUsuario(1L)).thenReturn(responseDTO(1L, USER_CORREO));

        ResponseEntity<Map<String, String>> res = controller.obtenerNombreUsuario(1L);

        assertEquals(200, res.getStatusCode().value());
        assertEquals(NOMBRE, res.getBody().get("nombre"));
        assertEquals(APELLIDO, res.getBody().get("apellido"));
        verify(service).obtenerUsuario(1L);
    }

    /**
     * Verifica que obtener el nombre de un usuario inexistente lanza {@link UsuarioNotFoundException}
     */
    @Test
    void obtenerNombreUsuario_noExistente_propagaExcepcion() {
        when(service.obtenerUsuario(99L)).thenThrow(new UsuarioNotFoundException(M_NO_ENCONTRADO));

        assertThrows(UsuarioNotFoundException.class, () -> controller.obtenerNombreUsuario(99L));
    }
}