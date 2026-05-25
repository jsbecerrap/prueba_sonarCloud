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

/**
 * Pruebas unitarias para {@link NotificacionController}
 * Verifica el comportamiento del controlador de notificaciones usando mocks de
 * {@link NotificacionService} y {@link UsuarioService}, con contexto de seguridad simulado
 */
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

    /** Correo del usuario autenticado simulado en el SecurityContext para todos los tests */
    private static final String USER_CORREO = "user@test.com";

    /** Tipo de notificacion informativa usado como constante en los tests */
    private static final String INFO = "INFO";

    /** Clave del mapa de respuesta que contiene el conteo de notificaciones sin leer */
    private static final String TOTAL = "total";

    /** Mensaje de error generico usado en los tests que verifican propagacion de excepciones */
    private static final String ERROR = "error";

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

    private UsuarioResponseDTO usuarioMock(Long id) {
        UsuarioResponseDTO u = new UsuarioResponseDTO();
        u.setId(id);
        return u;
    }

    private NotificacionDTO notificacionLeida() {
        return new NotificacionDTO(1L, INFO, "Titulo", "Mensaje",
                "PUSH", "ENVIADO", true, LocalDateTime.now(), 1L);
    }

    private NotificacionDTO notificacionNoLeida() {
        return new NotificacionDTO(2L, INFO, "Titulo2", "Mensaje2",
                "PUSH", "ENVIADO", false, LocalDateTime.now(), 1L);
    }

    /**
     * Verifica que listar las notificaciones del usuario autenticado retorna HTTP 200
     * y la pagina contiene exactamente un elemento
     */
    @Test
    void listarMisNotificaciones_retorna200ConPagina() {
        when(usuarioService.obtenerPorCorreo(USER_CORREO)).thenReturn(usuarioMock(1L));
        when(notificacionService.listarPorUsuarioPaginado(eq(1L), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(notificacionLeida())));

        ResponseEntity<Page<NotificacionDTO>> res = controller.listarMisNotificaciones(0, 20);

        assertEquals(200, res.getStatusCode().value());
        assertNotNull(res.getBody());
        assertEquals(1, res.getBody().getTotalElements());
        verify(notificacionService).listarPorUsuarioPaginado(eq(1L), any(Pageable.class));
    }

    /**
     * Verifica que listar notificaciones cuando el usuario no tiene ninguna retorna HTTP 200 con pagina vacia
     */
    @Test
    void listarMisNotificaciones_paginaVacia_retorna200() {
        when(usuarioService.obtenerPorCorreo(USER_CORREO)).thenReturn(usuarioMock(1L));
        when(notificacionService.listarPorUsuarioPaginado(eq(1L), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));

        ResponseEntity<Page<NotificacionDTO>> res = controller.listarMisNotificaciones(0, 20);

        assertEquals(200, res.getStatusCode().value());
        assertTrue(res.getBody().isEmpty());
    }

    /**
     * Verifica que los parametros de paginacion personalizados se pasan correctamente al servicio
     */
    @Test
    void listarMisNotificaciones_paginacionPersonalizada_respetaParametros() {
        when(usuarioService.obtenerPorCorreo(USER_CORREO)).thenReturn(usuarioMock(1L));
        when(notificacionService.listarPorUsuarioPaginado(eq(1L), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));

        controller.listarMisNotificaciones(2, 5);

        verify(notificacionService).listarPorUsuarioPaginado(eq(1L),
                argThat(p -> p.getPageNumber() == 2 && p.getPageSize() == 5));
    }

    /**
     * Verifica que marcar una notificacion como leida retorna HTTP 204 y el cuerpo es nulo
     */
    @Test
    void marcarLeida_retorna204() {
        doNothing().when(notificacionService).marcarLeida(1L);

        ResponseEntity<Void> res = controller.marcarLeida(1L);

        assertEquals(204, res.getStatusCode().value());
        assertNull(res.getBody());
        verify(notificacionService).marcarLeida(1L);
    }

    /**
     * Verifica que si el servicio lanza una excepcion al marcar como leida, el controlador la propaga
     */
    @Test
    void marcarLeida_serviceLanzaExcepcion_propaga() {
        doThrow(new RuntimeException("no encontrada")).when(notificacionService).marcarLeida(99L);

        assertThrows(RuntimeException.class, () -> controller.marcarLeida(99L));
    }

    /**
     * Verifica que marcar todas las notificaciones del usuario autenticado como leidas retorna HTTP 204
     */
    @Test
    void marcarTodasLeidas_retorna204() {
        when(usuarioService.obtenerPorCorreo(USER_CORREO)).thenReturn(usuarioMock(1L));
        doNothing().when(notificacionService).marcarTodasLeidas(1L);

        ResponseEntity<Void> res = controller.marcarTodasLeidas();

        assertEquals(204, res.getStatusCode().value());
        assertNull(res.getBody());
        verify(notificacionService).marcarTodasLeidas(1L);
    }

    /**
     * Verifica que si el servicio lanza una excepcion al marcar todas como leidas, el controlador la propaga
     */
    @Test
    void marcarTodasLeidas_serviceLanzaExcepcion_propaga() {
        when(usuarioService.obtenerPorCorreo(USER_CORREO)).thenReturn(usuarioMock(1L));
        doThrow(new RuntimeException(ERROR)).when(notificacionService).marcarTodasLeidas(1L);

        assertThrows(RuntimeException.class, () -> controller.marcarTodasLeidas());
    }

    /**
     * Verifica que enviar una notificacion individual retorna HTTP 204 y el cuerpo es nulo
     */
    @Test
    void enviarIndividual_retorna204() {
        NotificacionRequestDTO dto = new NotificacionRequestDTO();
        dto.setTipo(INFO);
        dto.setTitulo("Test");
        dto.setMensaje("Mensaje test");
        dto.setUsuarioId(1L);
        doNothing().when(notificacionService).enviarNotificacion(any());

        ResponseEntity<Void> res = controller.enviarIndividual(dto);

        assertEquals(204, res.getStatusCode().value());
        assertNull(res.getBody());
        verify(notificacionService).enviarNotificacion(any());
    }

    /**
     * Verifica que si el servicio lanza una excepcion al enviar notificacion individual, el controlador la propaga
     */
    @Test
    void enviarIndividual_serviceLanzaExcepcion_propaga() {
        doThrow(new RuntimeException(ERROR)).when(notificacionService).enviarNotificacion(any());

        assertThrows(RuntimeException.class,
                () -> controller.enviarIndividual(new NotificacionRequestDTO()));
    }

    /**
     * Verifica que enviar una notificacion masiva a multiples usuarios retorna HTTP 204 y el cuerpo es nulo
     */
    @Test
    void enviarMasiva_retorna204() {
        NotificacionMasivaRequestDTO dto = new NotificacionMasivaRequestDTO();
        dto.setTipo(INFO);
        dto.setTitulo("Masiva");
        dto.setMensaje("Mensaje masivo");
        dto.setUsuarioIds(List.of(1L, 2L, 3L));
        doNothing().when(notificacionService).enviarMasiva(any());

        ResponseEntity<Void> res = controller.enviarMasiva(dto);

        assertEquals(204, res.getStatusCode().value());
        assertNull(res.getBody());
        verify(notificacionService).enviarMasiva(any());
    }

    /**
     * Verifica que si el servicio lanza una excepcion al enviar notificacion masiva, el controlador la propaga
     */
    @Test
    void enviarMasiva_serviceLanzaExcepcion_propaga() {
        doThrow(new RuntimeException(ERROR)).when(notificacionService).enviarMasiva(any());

        assertThrows(RuntimeException.class,
                () -> controller.enviarMasiva(new NotificacionMasivaRequestDTO()));
    }

    /**
     * Verifica que notificar a los participantes de un partido retorna HTTP 204 y el cuerpo es nulo
     */
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

    /**
     * Verifica que si el servicio lanza una excepcion al notificar por partido, el controlador la propaga
     */
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

    /**
     * Verifica que buscar notificaciones del usuario autenticado por rango de fechas retorna HTTP 200
     * y la pagina contiene exactamente un elemento
     */
    @Test
    void buscarPorFecha_retorna200ConPagina() {
        when(usuarioService.obtenerPorCorreo(USER_CORREO)).thenReturn(usuarioMock(1L));
        when(notificacionService.listarPorFecha(eq(1L), any(LocalDateTime.class),
                any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(notificacionLeida())));

        ResponseEntity<Page<NotificacionDTO>> res =
                controller.buscarPorFecha("2026-01-01", "2026-12-31", 0, 20);

        assertEquals(200, res.getStatusCode().value());
        assertEquals(1, res.getBody().getTotalElements());
        verify(notificacionService).listarPorFecha(eq(1L), any(), any(), any(Pageable.class));
    }

    /**
     * Verifica que buscar notificaciones por fecha sin resultados retorna HTTP 200 con pagina vacia
     */
    @Test
    void buscarPorFecha_paginaVacia_retorna200() {
        when(usuarioService.obtenerPorCorreo(USER_CORREO)).thenReturn(usuarioMock(1L));
        when(notificacionService.listarPorFecha(eq(1L), any(LocalDateTime.class),
                any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));

        ResponseEntity<Page<NotificacionDTO>> res =
                controller.buscarPorFecha("2026-05-01", "2026-05-31", 0, 10);

        assertEquals(200, res.getStatusCode().value());
        assertTrue(res.getBody().isEmpty());
    }

    /**
     * Verifica que los parametros de paginacion personalizados al buscar por fecha se pasan correctamente al servicio
     */
    @Test
    void buscarPorFecha_paginacionPersonalizada_respetaParametros() {
        when(usuarioService.obtenerPorCorreo(USER_CORREO)).thenReturn(usuarioMock(1L));
        when(notificacionService.listarPorFecha(any(), any(), any(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));

        controller.buscarPorFecha("2026-01-01", "2026-12-31", 2, 10);

        verify(notificacionService).listarPorFecha(eq(1L), any(), any(),
                argThat(p -> p.getPageNumber() == 2 && p.getPageSize() == 10));
    }

    /**
     * Verifica que contar notificaciones sin leer retorna HTTP 200
     * y el total refleja correctamente solo las no leidas
     */
    @Test
    void contarSinLeer_retorna200ConTotal() {
        when(usuarioService.obtenerPorCorreo(USER_CORREO)).thenReturn(usuarioMock(1L));
        when(notificacionService.listarPorUsuario(1L))
                .thenReturn(List.of(notificacionLeida(), notificacionNoLeida()));

        ResponseEntity<Map<String, Long>> res = controller.contarSinLeer();

        assertEquals(200, res.getStatusCode().value());
        assertEquals(1L, res.getBody().get(TOTAL));
        verify(notificacionService).listarPorUsuario(1L);
    }

    /**
     * Verifica que contar sin leer cuando todas estan leidas retorna HTTP 200 con total en cero
     */
    @Test
    void contarSinLeer_todasLeidas_retorna0() {
        when(usuarioService.obtenerPorCorreo(USER_CORREO)).thenReturn(usuarioMock(1L));
        when(notificacionService.listarPorUsuario(1L))
                .thenReturn(List.of(notificacionLeida(), notificacionLeida()));

        ResponseEntity<Map<String, Long>> res = controller.contarSinLeer();

        assertEquals(200, res.getStatusCode().value());
        assertEquals(0L, res.getBody().get(TOTAL));
    }

    /**
     * Verifica que contar sin leer cuando la lista esta vacia retorna HTTP 200 con total en cero
     */
    @Test
    void contarSinLeer_listaVacia_retorna0() {
        when(usuarioService.obtenerPorCorreo(USER_CORREO)).thenReturn(usuarioMock(1L));
        when(notificacionService.listarPorUsuario(1L)).thenReturn(List.of());

        ResponseEntity<Map<String, Long>> res = controller.contarSinLeer();

        assertEquals(200, res.getStatusCode().value());
        assertEquals(0L, res.getBody().get(TOTAL));
    }

    /**
     * Verifica que contar sin leer cuando todas estan sin leer retorna HTTP 200
     * y el total coincide con el numero total de notificaciones
     */
    @Test
    void contarSinLeer_todasSinLeer_retornaTodas() {
        when(usuarioService.obtenerPorCorreo(USER_CORREO)).thenReturn(usuarioMock(1L));
        when(notificacionService.listarPorUsuario(1L))
                .thenReturn(List.of(notificacionNoLeida(), notificacionNoLeida(), notificacionNoLeida()));

        ResponseEntity<Map<String, Long>> res = controller.contarSinLeer();

        assertEquals(200, res.getStatusCode().value());
        assertEquals(3L, res.getBody().get(TOTAL));
    }

    /**
     * Verifica que si el servicio lanza una excepcion al contar sin leer, el controlador la propaga
     */
    @Test
    void contarSinLeer_serviceLanzaExcepcion_propaga() {
        when(usuarioService.obtenerPorCorreo(USER_CORREO)).thenReturn(usuarioMock(1L));
        when(notificacionService.listarPorUsuario(1L)).thenThrow(new RuntimeException(ERROR));

        assertThrows(RuntimeException.class, () -> controller.contarSinLeer());
    }
}