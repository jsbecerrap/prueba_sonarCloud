package co.edu.unbosque.mundial_2026.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import co.edu.unbosque.mundial_2026.dto.NotificacionDTO;
import co.edu.unbosque.mundial_2026.dto.request.NotificacionMasivaRequestDTO;
import co.edu.unbosque.mundial_2026.dto.request.NotificacionRequestDTO;
import co.edu.unbosque.mundial_2026.dto.response.UsuarioResponseDTO;
import co.edu.unbosque.mundial_2026.service.NotificacionService;
import co.edu.unbosque.mundial_2026.service.UsuarioService;

@ExtendWith(MockitoExtension.class)
class NotificacionControllerTest {

    @Mock
    private NotificacionService notificacionService;

    @Mock
    private UsuarioService usuarioService;

    @InjectMocks
    private NotificacionController controller;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

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

    private UsuarioResponseDTO usuarioMock(Long id) {
        UsuarioResponseDTO u = new UsuarioResponseDTO();
        u.setId(id);
        return u;
    }

    private NotificacionDTO notificacionLeida() {
        return new NotificacionDTO(1L, "INFO", "Titulo", "Mensaje",
                "PUSH", "ENVIADO", true, LocalDateTime.now(), 1L);
    }

    private NotificacionDTO notificacionNoLeida() {
        return new NotificacionDTO(2L, "INFO", "Titulo2", "Mensaje2",
                "PUSH", "ENVIADO", false, LocalDateTime.now(), 1L);
    }



    @Test
    void listarMisNotificaciones_retorna200ConPagina() {
        when(usuarioService.obtenerPorCorreo("user@test.com")).thenReturn(usuarioMock(1L));
        when(notificacionService.listarPorUsuarioPaginado(eq(1L), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(notificacionLeida())));

        ResponseEntity<Page<NotificacionDTO>> res = controller.listarMisNotificaciones(0, 20);

        assertEquals(200, res.getStatusCode().value());
        assertNotNull(res.getBody());
        assertEquals(1, res.getBody().getTotalElements());
        verify(notificacionService).listarPorUsuarioPaginado(eq(1L), any(Pageable.class));
    }

    @Test
    void listarMisNotificaciones_paginaVacia_retorna200() {
        when(usuarioService.obtenerPorCorreo("user@test.com")).thenReturn(usuarioMock(1L));
        when(notificacionService.listarPorUsuarioPaginado(eq(1L), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));

        ResponseEntity<Page<NotificacionDTO>> res = controller.listarMisNotificaciones(0, 20);

        assertEquals(200, res.getStatusCode().value());
        assertTrue(res.getBody().isEmpty());
    }

    @Test
    void listarMisNotificaciones_paginacionPersonalizada_respetaParametros() {
        when(usuarioService.obtenerPorCorreo("user@test.com")).thenReturn(usuarioMock(1L));
        when(notificacionService.listarPorUsuarioPaginado(eq(1L), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));

        controller.listarMisNotificaciones(2, 5);

        verify(notificacionService).listarPorUsuarioPaginado(eq(1L),
                argThat(p -> p.getPageNumber() == 2 && p.getPageSize() == 5));
    }

   

    @Test
    void marcarLeida_retorna204() {
        doNothing().when(notificacionService).marcarLeida(1L);

        ResponseEntity<Void> res = controller.marcarLeida(1L);

        assertEquals(204, res.getStatusCode().value());
        assertNull(res.getBody());
        verify(notificacionService).marcarLeida(1L);
    }

    @Test
    void marcarLeida_serviceLanzaExcepcion_propaga() {
        doThrow(new RuntimeException("no encontrada")).when(notificacionService).marcarLeida(99L);

        assertThrows(RuntimeException.class, () -> controller.marcarLeida(99L));
    }

   

    @Test
    void marcarTodasLeidas_retorna204() {
        when(usuarioService.obtenerPorCorreo("user@test.com")).thenReturn(usuarioMock(1L));
        doNothing().when(notificacionService).marcarTodasLeidas(1L);

        ResponseEntity<Void> res = controller.marcarTodasLeidas();

        assertEquals(204, res.getStatusCode().value());
        assertNull(res.getBody());
        verify(notificacionService).marcarTodasLeidas(1L);
    }

    @Test
    void marcarTodasLeidas_serviceLanzaExcepcion_propaga() {
        when(usuarioService.obtenerPorCorreo("user@test.com")).thenReturn(usuarioMock(1L));
        doThrow(new RuntimeException("error")).when(notificacionService).marcarTodasLeidas(1L);

        assertThrows(RuntimeException.class, () -> controller.marcarTodasLeidas());
    }

    

    @Test
    void enviarIndividual_retorna204() {
        NotificacionRequestDTO dto = new NotificacionRequestDTO();
        dto.setTipo("INFO");
        dto.setTitulo("Test");
        dto.setMensaje("Mensaje test");
        dto.setUsuarioId(1L);
        doNothing().when(notificacionService).enviarNotificacion(any());

        ResponseEntity<Void> res = controller.enviarIndividual(dto);

        assertEquals(204, res.getStatusCode().value());
        assertNull(res.getBody());
        verify(notificacionService).enviarNotificacion(any());
    }

    @Test
    void enviarIndividual_serviceLanzaExcepcion_propaga() {
        doThrow(new RuntimeException("error")).when(notificacionService).enviarNotificacion(any());

        assertThrows(RuntimeException.class,
                () -> controller.enviarIndividual(new NotificacionRequestDTO()));
    }

  

    @Test
    void enviarMasiva_retorna204() {
        NotificacionMasivaRequestDTO dto = new NotificacionMasivaRequestDTO();
        dto.setTipo("INFO");
        dto.setTitulo("Masiva");
        dto.setMensaje("Mensaje masivo");
        dto.setUsuarioIds(List.of(1L, 2L, 3L));
        doNothing().when(notificacionService).enviarMasiva(any());

        ResponseEntity<Void> res = controller.enviarMasiva(dto);

        assertEquals(204, res.getStatusCode().value());
        assertNull(res.getBody());
        verify(notificacionService).enviarMasiva(any());
    }

    @Test
    void enviarMasiva_serviceLanzaExcepcion_propaga() {
        doThrow(new RuntimeException("error")).when(notificacionService).enviarMasiva(any());

        assertThrows(RuntimeException.class,
                () -> controller.enviarMasiva(new NotificacionMasivaRequestDTO()));
    }

  

    @Test
    void notificarPorPartido_retorna204() {
        NotificacionRequestDTO dto = new NotificacionRequestDTO();
        dto.setTipo("PARTIDO");
        dto.setTitulo("Inicio partido");
        dto.setMensaje("El partido comienza");
        doNothing().when(notificacionService)
                .notificarPorPartido(anyLong(), anyString(), anyString(), anyString());

        ResponseEntity<Void> res = controller.notificarPorPartido(1L, dto);

        assertEquals(204, res.getStatusCode().value());
        assertNull(res.getBody());
        verify(notificacionService).notificarPorPartido(1L, "PARTIDO", "Inicio partido", "El partido comienza");
    }

    @Test
    void notificarPorPartido_serviceLanzaExcepcion_propaga() {
        NotificacionRequestDTO dto = new NotificacionRequestDTO();
        dto.setTipo("PARTIDO");
        dto.setTitulo("Test");
        dto.setMensaje("Msg");
        doThrow(new RuntimeException("partido no encontrado"))
                .when(notificacionService)
                .notificarPorPartido(anyLong(), anyString(), anyString(), anyString());

        assertThrows(RuntimeException.class, () -> controller.notificarPorPartido(99L, dto));
    }

  

    @Test
    void buscarPorFecha_retorna200ConPagina() {
        when(usuarioService.obtenerPorCorreo("user@test.com")).thenReturn(usuarioMock(1L));
        when(notificacionService.listarPorFecha(eq(1L), any(LocalDateTime.class),
                any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(notificacionLeida())));

        ResponseEntity<Page<NotificacionDTO>> res =
                controller.buscarPorFecha("2026-01-01", "2026-12-31", 0, 20);

        assertEquals(200, res.getStatusCode().value());
        assertEquals(1, res.getBody().getTotalElements());
        verify(notificacionService).listarPorFecha(eq(1L), any(), any(), any(Pageable.class));
    }

    @Test
    void buscarPorFecha_paginaVacia_retorna200() {
        when(usuarioService.obtenerPorCorreo("user@test.com")).thenReturn(usuarioMock(1L));
        when(notificacionService.listarPorFecha(eq(1L), any(LocalDateTime.class),
                any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));

        ResponseEntity<Page<NotificacionDTO>> res =
                controller.buscarPorFecha("2026-05-01", "2026-05-31", 0, 10);

        assertEquals(200, res.getStatusCode().value());
        assertTrue(res.getBody().isEmpty());
    }

    @Test
    void buscarPorFecha_paginacionPersonalizada_respetaParametros() {
        when(usuarioService.obtenerPorCorreo("user@test.com")).thenReturn(usuarioMock(1L));
        when(notificacionService.listarPorFecha(any(), any(), any(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));

        controller.buscarPorFecha("2026-01-01", "2026-12-31", 2, 10);

        verify(notificacionService).listarPorFecha(eq(1L), any(), any(),
                argThat(p -> p.getPageNumber() == 2 && p.getPageSize() == 10));
    }

  

    @Test
    void contarSinLeer_retorna200ConTotal() {
        when(usuarioService.obtenerPorCorreo("user@test.com")).thenReturn(usuarioMock(1L));
        when(notificacionService.listarPorUsuario(1L))
                .thenReturn(List.of(notificacionLeida(), notificacionNoLeida()));

        ResponseEntity<Map<String, Long>> res = controller.contarSinLeer();

        assertEquals(200, res.getStatusCode().value());
        assertEquals(1L, res.getBody().get("total"));
        verify(notificacionService).listarPorUsuario(1L);
    }

    @Test
    void contarSinLeer_todasLeidas_retorna0() {
        when(usuarioService.obtenerPorCorreo("user@test.com")).thenReturn(usuarioMock(1L));
        when(notificacionService.listarPorUsuario(1L))
                .thenReturn(List.of(notificacionLeida(), notificacionLeida()));

        ResponseEntity<Map<String, Long>> res = controller.contarSinLeer();

        assertEquals(200, res.getStatusCode().value());
        assertEquals(0L, res.getBody().get("total"));
    }

    @Test
    void contarSinLeer_listaVacia_retorna0() {
        when(usuarioService.obtenerPorCorreo("user@test.com")).thenReturn(usuarioMock(1L));
        when(notificacionService.listarPorUsuario(1L)).thenReturn(List.of());

        ResponseEntity<Map<String, Long>> res = controller.contarSinLeer();

        assertEquals(200, res.getStatusCode().value());
        assertEquals(0L, res.getBody().get("total"));
    }

    @Test
    void contarSinLeer_todasSinLeer_retornaTodas() {
        when(usuarioService.obtenerPorCorreo("user@test.com")).thenReturn(usuarioMock(1L));
        when(notificacionService.listarPorUsuario(1L))
                .thenReturn(List.of(notificacionNoLeida(), notificacionNoLeida(), notificacionNoLeida()));

        ResponseEntity<Map<String, Long>> res = controller.contarSinLeer();

        assertEquals(200, res.getStatusCode().value());
        assertEquals(3L, res.getBody().get("total"));
    }

    @Test
    void contarSinLeer_serviceLanzaExcepcion_propaga() {
        when(usuarioService.obtenerPorCorreo("user@test.com")).thenReturn(usuarioMock(1L));
        when(notificacionService.listarPorUsuario(1L)).thenThrow(new RuntimeException("error"));

        assertThrows(RuntimeException.class, () -> controller.contarSinLeer());
    }
}