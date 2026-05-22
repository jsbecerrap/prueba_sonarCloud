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

@ExtendWith(MockitoExtension.class)
class UsuarioRestControllerTest {

    @Mock private UsuarioService service;
    @Mock private TokenBlacklist tokenBlacklist;
    @InjectMocks private UsuarioRestController controller;

    @Mock private SecurityContext securityContext;
    @Mock private Authentication authentication;

    @BeforeEach
void setUpSecurityContext() {
    lenient().when(securityContext.getAuthentication()).thenReturn(authentication);
    lenient().when(authentication.getName()).thenReturn("user@test.com");
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
        dto.setNombre("Juan");
        dto.setApellido("Perez");
        return dto;
    }

    private UsuarioRequestDTO requestDTO() {
        UsuarioRequestDTO dto = new UsuarioRequestDTO();
        dto.setNombre("Juan");
        dto.setApellido("Perez");
        dto.setCorreoUsuario("user@test.com");
        dto.setContrasena("pass123");
        return dto;
    }

    private UsuarioActualizarRequestDTO actualizarDTO(String correoNuevo) {
        UsuarioActualizarRequestDTO dto = new UsuarioActualizarRequestDTO();
        dto.setNombre("Juan");
        dto.setApellido("Perez");
        dto.setCorreoNuevo(correoNuevo);
        dto.setContrasenaActual("pass123");
        return dto;
    }

    @Test
    void listarTodos_retornaOkConLista() {
        when(service.listarTodos()).thenReturn(List.of(responseDTO(1L, "user@test.com")));

        ResponseEntity<List<UsuarioResponseDTO>> res = controller.listarTodos();

        assertEquals(200, res.getStatusCode().value());
        assertEquals(1, res.getBody().size());
        verify(service).listarTodos();
    }

    @Test
    void listarTodos_listaVacia_retornaOkVacio() {
        when(service.listarTodos()).thenReturn(List.of());

        ResponseEntity<List<UsuarioResponseDTO>> res = controller.listarTodos();

        assertEquals(200, res.getStatusCode().value());
        assertTrue(res.getBody().isEmpty());
    }

    @Test
    void obtenerUsuario_existente_retornaOk() {
        when(service.obtenerUsuario(1L)).thenReturn(responseDTO(1L, "user@test.com"));

        ResponseEntity<UsuarioResponseDTO> res = controller.obtenerUsuario(1L);

        assertEquals(200, res.getStatusCode().value());
        assertEquals(1L, res.getBody().getId());
        verify(service).obtenerUsuario(1L);
    }

    @Test
    void obtenerUsuario_noExistente_propagaExcepcion() {
        when(service.obtenerUsuario(99L)).thenThrow(new UsuarioNotFoundException("no encontrado"));

        assertThrows(UsuarioNotFoundException.class, () -> controller.obtenerUsuario(99L));
    }

    @Test
    void obtenerPerfil_retornaOkConDatosDelAutenticado() {
        when(service.obtenerPorCorreo("user@test.com")).thenReturn(responseDTO(1L, "user@test.com"));

        ResponseEntity<UsuarioResponseDTO> res = controller.obtenerPerfil();

        assertEquals(200, res.getStatusCode().value());
        assertEquals("user@test.com", res.getBody().getCorreoUsuario());
        verify(service).obtenerPorCorreo("user@test.com");
    }

    @Test
    void obtenerPerfil_usuarioNoExistente_propagaExcepcion() {
        when(service.obtenerPorCorreo("user@test.com")).thenThrow(new UsuarioNotFoundException("no encontrado"));

        assertThrows(UsuarioNotFoundException.class, () -> controller.obtenerPerfil());
    }

    @Test
    void registrarUsuario_exitoso_retorna201() {
        UsuarioRequestDTO dto = requestDTO();
        when(service.registrarUsuario(dto)).thenReturn(responseDTO(1L, "user@test.com"));

        ResponseEntity<UsuarioResponseDTO> res = controller.registrarUsuario(dto);

        assertEquals(201, res.getStatusCode().value());
        assertNotNull(res.getBody());
        verify(service).registrarUsuario(dto);
    }

    @Test
    void registrarUsuarioPorAdmin_exitoso_retorna201() {
        UsuarioRequestDTO dto = requestDTO();
        when(service.registrarUsuarioComoAdmin(dto)).thenReturn(responseDTO(2L, "admin@test.com"));

        ResponseEntity<UsuarioResponseDTO> res = controller.registrarUsuarioPorAdmin(dto);

        assertEquals(201, res.getStatusCode().value());
        assertNotNull(res.getBody());
        verify(service).registrarUsuarioComoAdmin(dto);
    }

    @Test
    void eliminarUsuario_exitoso_retorna204() {
        doNothing().when(service).eliminarUsuario(1L);

        ResponseEntity<Void> res = controller.eliminarUsuario(1L);

        assertEquals(204, res.getStatusCode().value());
        assertNull(res.getBody());
        verify(service).eliminarUsuario(1L);
    }

    @Test
    void eliminarUsuario_noExistente_propagaExcepcion() {
        doThrow(new UsuarioNotFoundException("no encontrado")).when(service).eliminarUsuario(99L);

        assertThrows(UsuarioNotFoundException.class, () -> controller.eliminarUsuario(99L));
    }

    @Test
    void actualizarPerfil_sinCambioDeCorrco_retornaOkConDTO() {
        Map<String, Object> resultado = new HashMap<>();
        resultado.put("correocambio", false);
        resultado.put("usuario", responseDTO(1L, "user@test.com"));
        when(service.actualizarPerfil(eq("user@test.com"), any())).thenReturn(resultado);

        MockHttpServletRequest request = new MockHttpServletRequest();
        ResponseEntity<Object> res = controller.actualizarPerfil(actualizarDTO(null), request);

        assertEquals(200, res.getStatusCode().value());
        assertNotNull(res.getBody());
        verifyNoInteractions(tokenBlacklist);
    }

    @Test
    void actualizarPerfil_conCambioDeCorreo_invalidaTokenYRetornaMensaje() {
        Map<String, Object> resultado = new HashMap<>();
        resultado.put("correocambio", true);
        resultado.put("usuario", responseDTO(1L, "nuevo@test.com"));
        when(service.actualizarPerfil(eq("user@test.com"), any())).thenReturn(resultado);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer token-valido");

        ResponseEntity<Object> res = controller.actualizarPerfil(actualizarDTO("nuevo@test.com"), request);

        assertEquals(200, res.getStatusCode().value());
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) res.getBody();
        assertTrue(body.containsKey("mensaje"));
        assertEquals("Correo actualizado, inicia sesión nuevamente", body.get("mensaje"));
        verify(tokenBlacklist).agregar("token-valido");
    }

    @Test
    void actualizarPerfil_conCambioDeCorreo_sinHeader_noInvalidaToken() {
        Map<String, Object> resultado = new HashMap<>();
        resultado.put("correocambio", true);
        resultado.put("usuario", responseDTO(1L, "nuevo@test.com"));
        when(service.actualizarPerfil(eq("user@test.com"), any())).thenReturn(resultado);

        MockHttpServletRequest request = new MockHttpServletRequest();

        ResponseEntity<Object> res = controller.actualizarPerfil(actualizarDTO("nuevo@test.com"), request);

        assertEquals(200, res.getStatusCode().value());
        verifyNoInteractions(tokenBlacklist);
    }

    @Test
    void actualizarPerfil_conCambioDeCorreo_headerSinPrefijo_noInvalidaToken() {
        Map<String, Object> resultado = new HashMap<>();
        resultado.put("correocambio", true);
        resultado.put("usuario", responseDTO(1L, "nuevo@test.com"));
        when(service.actualizarPerfil(eq("user@test.com"), any())).thenReturn(resultado);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "token-sin-prefijo");

        ResponseEntity<Object> res = controller.actualizarPerfil(actualizarDTO("nuevo@test.com"), request);

        assertEquals(200, res.getStatusCode().value());
        verifyNoInteractions(tokenBlacklist);
    }

    @Test
    void logout_conToken_invalidaTokenYRetornaMensaje() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer token-de-sesion");

        ResponseEntity<Object> res = controller.logout(request);

        assertEquals(200, res.getStatusCode().value());
        @SuppressWarnings("unchecked")
        Map<String, String> body = (Map<String, String>) res.getBody();
        assertEquals("Sesión cerrada correctamente", body.get("mensaje"));
        verify(tokenBlacklist).agregar("token-de-sesion");
    }

    @Test
    void logout_sinHeader_noInvalidaTokenYRetornaMensaje() {
        MockHttpServletRequest request = new MockHttpServletRequest();

        ResponseEntity<Object> res = controller.logout(request);

        assertEquals(200, res.getStatusCode().value());
        verifyNoInteractions(tokenBlacklist);
    }

    @Test
    void logout_headerSinPrefijo_noInvalidaToken() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "token-sin-bearer");

        ResponseEntity<Object> res = controller.logout(request);

        assertEquals(200, res.getStatusCode().value());
        verifyNoInteractions(tokenBlacklist);
    }

    @Test
    void obtenerSelecciones_retornaOkConLista() {
        when(service.seleccionesUsuario("user@test.com"))
                .thenReturn(List.of(new PreferenciaDTO(1L, "Colombia")));

        ResponseEntity<List<PreferenciaDTO>> res = controller.obtenerSelecciones();

        assertEquals(200, res.getStatusCode().value());
        assertEquals(1, res.getBody().size());
        verify(service).seleccionesUsuario("user@test.com");
    }

    @Test
    void obtenerSelecciones_listaVacia_retornaOkVacio() {
        when(service.seleccionesUsuario("user@test.com")).thenReturn(List.of());

        ResponseEntity<List<PreferenciaDTO>> res = controller.obtenerSelecciones();

        assertEquals(200, res.getStatusCode().value());
        assertTrue(res.getBody().isEmpty());
    }

    @Test
    void agregarSeleccion_exitoso_retorna204() {
        doNothing().when(service).agregarSeleccion("user@test.com", List.of(1L, 2L));

        ResponseEntity<Void> res = controller.agregarSeleccion(List.of(1L, 2L));

        assertEquals(204, res.getStatusCode().value());
        verify(service).agregarSeleccion("user@test.com", List.of(1L, 2L));
    }

    @Test
    void eliminarSeleccion_exitoso_retorna204() {
        doNothing().when(service).eliminarSeleccion("user@test.com", 1L);

        ResponseEntity<Void> res = controller.eliminarSeleccion(1L);

        assertEquals(204, res.getStatusCode().value());
        verify(service).eliminarSeleccion("user@test.com", 1L);
    }

    @Test
    void obtenerEstadios_retornaOkConLista() {
        when(service.estadiosUsuario("user@test.com"))
                .thenReturn(List.of(new PreferenciaDTO(1L, "MetLife Stadium")));

        ResponseEntity<List<PreferenciaDTO>> res = controller.obtenerEstadios();

        assertEquals(200, res.getStatusCode().value());
        assertEquals(1, res.getBody().size());
        verify(service).estadiosUsuario("user@test.com");
    }

    @Test
    void obtenerEstadios_listaVacia_retornaOkVacio() {
        when(service.estadiosUsuario("user@test.com")).thenReturn(List.of());

        ResponseEntity<List<PreferenciaDTO>> res = controller.obtenerEstadios();

        assertEquals(200, res.getStatusCode().value());
        assertTrue(res.getBody().isEmpty());
    }

    @Test
    void agregarEstadio_exitoso_retorna204() {
        doNothing().when(service).agregarEstadio("user@test.com", List.of(1L));

        ResponseEntity<Void> res = controller.agregarEstadio(List.of(1L));

        assertEquals(204, res.getStatusCode().value());
        verify(service).agregarEstadio("user@test.com", List.of(1L));
    }

    @Test
    void eliminarEstadio_exitoso_retorna204() {
        doNothing().when(service).eliminarEstadio("user@test.com", 1L);

        ResponseEntity<Void> res = controller.eliminarEstadio(1L);

        assertEquals(204, res.getStatusCode().value());
        verify(service).eliminarEstadio("user@test.com", 1L);
    }

    @Test
    void obtenerCiudades_retornaOkConLista() {
        when(service.ciudadesUsuario("user@test.com"))
                .thenReturn(List.of(new PreferenciaDTO(1L, "Bogotá")));

        ResponseEntity<List<PreferenciaDTO>> res = controller.obtenerCiudades();

        assertEquals(200, res.getStatusCode().value());
        assertEquals(1, res.getBody().size());
        verify(service).ciudadesUsuario("user@test.com");
    }

    @Test
    void obtenerCiudades_listaVacia_retornaOkVacio() {
        when(service.ciudadesUsuario("user@test.com")).thenReturn(List.of());

        ResponseEntity<List<PreferenciaDTO>> res = controller.obtenerCiudades();

        assertEquals(200, res.getStatusCode().value());
        assertTrue(res.getBody().isEmpty());
    }

    @Test
    void agregarCiudad_exitoso_retorna204() {
        doNothing().when(service).agregarCiudad("user@test.com", List.of(1L));

        ResponseEntity<Void> res = controller.agregarCiudad(List.of(1L));

        assertEquals(204, res.getStatusCode().value());
        verify(service).agregarCiudad("user@test.com", List.of(1L));
    }

    @Test
    void eliminarCiudad_exitoso_retorna204() {
        doNothing().when(service).eliminarCiudad("user@test.com", 1L);

        ResponseEntity<Void> res = controller.eliminarCiudad(1L);

        assertEquals(204, res.getStatusCode().value());
        verify(service).eliminarCiudad("user@test.com", 1L);
    }

    @Test
    void listarEstadios_retornaOkConLista() {
        when(service.listarEstadios()).thenReturn(List.of(new PreferenciaDTO(1L, "MetLife Stadium")));

        ResponseEntity<List<PreferenciaDTO>> res = controller.listarEstadios();

        assertEquals(200, res.getStatusCode().value());
        assertEquals(1, res.getBody().size());
        verify(service).listarEstadios();
    }

    @Test
    void listarEstadios_listaVacia_retornaOkVacio() {
        when(service.listarEstadios()).thenReturn(List.of());

        ResponseEntity<List<PreferenciaDTO>> res = controller.listarEstadios();

        assertEquals(200, res.getStatusCode().value());
        assertTrue(res.getBody().isEmpty());
    }

    @Test
    void listarCiudades_retornaOkConLista() {
        when(service.listarCiudades()).thenReturn(List.of(new PreferenciaDTO(1L, "East Rutherford")));

        ResponseEntity<List<PreferenciaDTO>> res = controller.listarCiudades();

        assertEquals(200, res.getStatusCode().value());
        assertEquals(1, res.getBody().size());
        verify(service).listarCiudades();
    }

    @Test
    void listarCiudades_listaVacia_retornaOkVacio() {
        when(service.listarCiudades()).thenReturn(List.of());

        ResponseEntity<List<PreferenciaDTO>> res = controller.listarCiudades();

        assertEquals(200, res.getStatusCode().value());
        assertTrue(res.getBody().isEmpty());
    }

    @Test
    void actualizarFcmToken_exitoso_retorna204() {
        doNothing().when(service).actualizarFcmToken("user@test.com", "token-fcm-nuevo");
        Map<String, String> body = Map.of("fcmToken", "token-fcm-nuevo");

        ResponseEntity<Void> res = controller.actualizarFcmToken(body);

        assertEquals(204, res.getStatusCode().value());
        verify(service).actualizarFcmToken("user@test.com", "token-fcm-nuevo");
    }

    @Test
    void obtenerNombreUsuario_existente_retornaOkConNombreYApellido() {
        when(service.obtenerUsuario(1L)).thenReturn(responseDTO(1L, "user@test.com"));

        ResponseEntity<Map<String, String>> res = controller.obtenerNombreUsuario(1L);

        assertEquals(200, res.getStatusCode().value());
        assertEquals("Juan", res.getBody().get("nombre"));
        assertEquals("Perez", res.getBody().get("apellido"));
        verify(service).obtenerUsuario(1L);
    }

    @Test
    void obtenerNombreUsuario_noExistente_propagaExcepcion() {
        when(service.obtenerUsuario(99L)).thenThrow(new UsuarioNotFoundException("no encontrado"));

        assertThrows(UsuarioNotFoundException.class, () -> controller.obtenerNombreUsuario(99L));
    }
}