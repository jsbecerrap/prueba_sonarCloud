package co.edu.unbosque.mundial_2026.integration;

import co.edu.unbosque.mundial_2026.dto.NotificacionDTO;
import co.edu.unbosque.mundial_2026.dto.request.NotificacionMasivaRequestDTO;
import co.edu.unbosque.mundial_2026.dto.request.NotificacionRequestDTO;
import co.edu.unbosque.mundial_2026.dto.response.UsuarioResponseDTO;
import co.edu.unbosque.mundial_2026.service.NotificacionService;
import co.edu.unbosque.mundial_2026.service.UsuarioService;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Pruebas de integración para los endpoints de notificaciones
 *
 * Se valida acceso, autenticación, roles y consultas relacionadas
 * con notificaciones individuales y masivas
 */
class NotificacionIT extends BaseIntegrationTest {

    /**
     * URL base de notificaciones
     */
    private static final String BASE_URL = "/api/notificaciones";

    /**
     * URL para marcar una notificación como leída
     */
    private static final String URL_LEIDA = "/api/notificaciones/1/leida";

    /**
     * URL para marcar todas las notificaciones como leídas
     */
    private static final String URL_TODAS_LEIDAS = "/api/notificaciones/leidas";

    /**
     * URL para envío individual de notificaciones
     */
    private static final String URL_ENVIAR = "/api/notificaciones/enviar";

    /**
     * URL para envío masivo de notificaciones
     */
    private static final String URL_MASIVA = "/api/notificaciones/masiva";

    /**
     * URL para notificaciones relacionadas con partidos
     */
    private static final String URL_PARTIDO = "/api/notificaciones/partido/1";

    /**
     * URL para búsqueda de notificaciones por fecha
     */
    private static final String URL_BUSCAR = "/api/notificaciones/buscar";

    /**
     * URL para consultar cantidad de notificaciones sin leer
     */
    private static final String URL_CONTEO = "/api/notificaciones/sin-leer/conteo";

    /**
     * Identificador de usuario usado en pruebas
     */
    private static final Long USUARIO_ID = 1L;

    /**
     * Header de autenticación
     */
    private static final String AUTH_HEADER = "Authorization";

    /**
     * Prefijo Bearer usado en JWT
     */
    private static final String BEARER_PREFIX = "Bearer ";

    /**
     * Servicio de notificaciones simulado
     */
    @MockitoBean
    private NotificacionService notificacionService;

    /**
     * Servicio de usuarios simulado
     */
    @MockitoBean
    private UsuarioService usuarioService;

    /**
     * Simula un usuario autenticado para las pruebas
     */
    private void mockUsuario() {
        UsuarioResponseDTO usuario = new UsuarioResponseDTO();
        usuario.setId(USUARIO_ID);
        when(usuarioService.obtenerPorCorreo(USER_EMAIL)).thenReturn(usuario);
    }

    /**
     * Construye un request válido de notificación
     *
     * @return DTO válido para pruebas
     */
    private NotificacionRequestDTO notificacionRequest() {
        NotificacionRequestDTO dto = new NotificacionRequestDTO();
        dto.setTipo("SISTEMA");
        dto.setTitulo("Titulo prueba");
        dto.setMensaje("Mensaje prueba");
        dto.setUsuarioId(USUARIO_ID);
        return dto;
    }

    /**
     * Verifica consulta paginada de notificaciones autenticado
     */
    @Test
    void listarMisNotificaciones_conToken_retorna200() throws Exception {
        mockUsuario();
        when(notificacionService.listarPorUsuarioPaginado(eq(USUARIO_ID), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(new NotificacionDTO())));

        mockMvc.perform(get(BASE_URL)
                        .header(AUTH_HEADER, BEARER_PREFIX + tokenUsuario()))
                .andExpect(status().isOk());
    }

    /**
     * Verifica acceso sin token a notificaciones
     */
    @Test
    void listarMisNotificaciones_sinToken_retorna401() throws Exception {
        mockMvc.perform(get(BASE_URL))
                .andExpect(status().isUnauthorized());
    }

    /**
     * Verifica paginación de notificaciones
     */
    @Test
    void listarMisNotificaciones_conPaginacion_retorna200() throws Exception {
        mockUsuario();
        when(notificacionService.listarPorUsuarioPaginado(eq(USUARIO_ID), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));

        mockMvc.perform(get(BASE_URL + "?page=1&size=10")
                        .header(AUTH_HEADER, BEARER_PREFIX + tokenUsuario()))
                .andExpect(status().isOk());
    }

    /**
     * Verifica marcado individual de notificación
     */
    @Test
    void marcarLeida_conToken_retorna204() throws Exception {
        doNothing().when(notificacionService).marcarLeida(1L);

        mockMvc.perform(put(URL_LEIDA)
                        .header(AUTH_HEADER, BEARER_PREFIX + tokenUsuario()))
                .andExpect(status().isNoContent());
    }

    /**
     * Verifica acceso sin token al marcar notificaciones
     */
    @Test
    void marcarLeida_sinToken_retorna401() throws Exception {
        mockMvc.perform(put(URL_LEIDA))
                .andExpect(status().isUnauthorized());
    }

    /**
     * Verifica marcado masivo de notificaciones leídas
     */
    @Test
    void marcarTodasLeidas_conToken_retorna204() throws Exception {
        mockUsuario();
        doNothing().when(notificacionService).marcarTodasLeidas(USUARIO_ID);

        mockMvc.perform(put(URL_TODAS_LEIDAS)
                        .header(AUTH_HEADER, BEARER_PREFIX + tokenUsuario()))
                .andExpect(status().isNoContent());
    }

    /**
     * Verifica acceso sin token al marcar todas las notificaciones
     */
    @Test
    void marcarTodasLeidas_sinToken_retorna401() throws Exception {
        mockMvc.perform(put(URL_TODAS_LEIDAS))
                .andExpect(status().isUnauthorized());
    }

    /**
     * Verifica envío individual de notificaciones con rol administrador
     */
    @Test
    void enviarIndividual_conRolAdmin_retorna204() throws Exception {
        doNothing().when(notificacionService).enviarNotificacion(any());

        mockMvc.perform(post(URL_ENVIAR)
                        .header(AUTH_HEADER, BEARER_PREFIX + tokenAdmin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(notificacionRequest())))
                .andExpect(status().isNoContent());
    }

    /**
     * Verifica restricción de envío individual para usuarios normales
     */
    @Test
    void enviarIndividual_conRolUser_retorna403() throws Exception {
        mockMvc.perform(post(URL_ENVIAR)
                        .header(AUTH_HEADER, BEARER_PREFIX + tokenUsuario())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(notificacionRequest())))
                .andExpect(status().isForbidden());
    }

    /**
     * Verifica acceso sin token al envío individual
     */
    @Test
    void enviarIndividual_sinToken_retorna401() throws Exception {
        mockMvc.perform(post(URL_ENVIAR)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(notificacionRequest())))
                .andExpect(status().isUnauthorized());
    }

    /**
     * Verifica envío masivo con rol administrador
     */
    @Test
    void enviarMasiva_conRolAdmin_retorna204() throws Exception {
        doNothing().when(notificacionService).enviarMasiva(any());

        mockMvc.perform(post(URL_MASIVA)
                        .header(AUTH_HEADER, BEARER_PREFIX + tokenAdmin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new NotificacionMasivaRequestDTO())))
                .andExpect(status().isNoContent());
    }

    /**
     * Verifica restricción de envío masivo para usuarios normales
     */
    @Test
    void enviarMasiva_conRolUser_retorna403() throws Exception {
        mockMvc.perform(post(URL_MASIVA)
                        .header(AUTH_HEADER, BEARER_PREFIX + tokenUsuario())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isForbidden());
    }

    /**
     * Verifica acceso sin token al envío masivo
     */
    @Test
    void enviarMasiva_sinToken_retorna401() throws Exception {
        mockMvc.perform(post(URL_MASIVA)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isUnauthorized());
    }

    /**
     * Verifica envío de notificaciones por partido con rol administrador
     */
    @Test
    void notificarPorPartido_conRolAdmin_retorna204() throws Exception {
        doNothing().when(notificacionService).notificarPorPartido(any(), any(), any(), any());

        mockMvc.perform(post(URL_PARTIDO)
                        .header(AUTH_HEADER, BEARER_PREFIX + tokenAdmin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(notificacionRequest())))
                .andExpect(status().isNoContent());
    }

    /**
     * Verifica restricción de notificaciones por partido para usuarios normales
     */
    @Test
    void notificarPorPartido_conRolUser_retorna403() throws Exception {
        mockMvc.perform(post(URL_PARTIDO)
                        .header(AUTH_HEADER, BEARER_PREFIX + tokenUsuario())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(notificacionRequest())))
                .andExpect(status().isForbidden());
    }

    /**
     * Verifica acceso sin token a notificaciones por partido
     */
    @Test
    void notificarPorPartido_sinToken_retorna401() throws Exception {
        mockMvc.perform(post(URL_PARTIDO)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(notificacionRequest())))
                .andExpect(status().isUnauthorized());
    }

    /**
     * Verifica búsqueda de notificaciones por fecha
     */
    @Test
    void buscarPorFecha_conToken_retorna200() throws Exception {
        mockUsuario();
        when(notificacionService.listarPorFecha(eq(USUARIO_ID), any(), any(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));

        mockMvc.perform(get(URL_BUSCAR)
                        .param("desde", "2026-01-01")
                        .param("hasta", "2026-12-31")
                        .header(AUTH_HEADER, BEARER_PREFIX + tokenUsuario()))
                .andExpect(status().isOk());
    }

    /**
     * Verifica acceso sin token a búsqueda por fecha
     */
    @Test
    void buscarPorFecha_sinToken_retorna401() throws Exception {
        mockMvc.perform(get(URL_BUSCAR)
                        .param("desde", "2026-01-01")
                        .param("hasta", "2026-12-31"))
                .andExpect(status().isUnauthorized());
    }

    /**
     * Verifica consulta de notificaciones sin leer
     */
    @Test
    void contarSinLeer_conToken_retorna200() throws Exception {
        mockUsuario();
        when(notificacionService.listarPorUsuario(USUARIO_ID)).thenReturn(List.of());

        mockMvc.perform(get(URL_CONTEO)
                        .header(AUTH_HEADER, BEARER_PREFIX + tokenUsuario()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(0));
    }

    /**
     * Verifica acceso sin token al conteo de notificaciones
     */
    @Test
    void contarSinLeer_sinToken_retorna401() throws Exception {
        mockMvc.perform(get(URL_CONTEO))
                .andExpect(status().isUnauthorized());
    }
}