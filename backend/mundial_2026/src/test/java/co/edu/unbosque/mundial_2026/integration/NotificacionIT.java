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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class NotificacionIT extends BaseIntegrationTest {

    private static final String BASE_URL = "/api/notificaciones";
    private static final String URL_LEIDA = "/api/notificaciones/1/leida";
    private static final String URL_TODAS_LEIDAS = "/api/notificaciones/leidas";
    private static final String URL_ENVIAR = "/api/notificaciones/enviar";
    private static final String URL_MASIVA = "/api/notificaciones/masiva";
    private static final String URL_PARTIDO = "/api/notificaciones/partido/1";
    private static final String URL_BUSCAR = "/api/notificaciones/buscar";
    private static final String URL_CONTEO = "/api/notificaciones/sin-leer/conteo";
    private static final Long USUARIO_ID = 1L;

    @MockitoBean
    private NotificacionService notificacionService;

    @MockitoBean
    private UsuarioService usuarioService;

    private void mockUsuario() {
        UsuarioResponseDTO usuario = new UsuarioResponseDTO();
        usuario.setId(USUARIO_ID);
        when(usuarioService.obtenerPorCorreo(USER_EMAIL)).thenReturn(usuario);
    }

    private NotificacionRequestDTO notificacionRequest() {
        NotificacionRequestDTO dto = new NotificacionRequestDTO();
        dto.setTipo("SISTEMA");
        dto.setTitulo("Titulo prueba");
        dto.setMensaje("Mensaje prueba");
        dto.setUsuarioId(USUARIO_ID);
        return dto;
    }

    @Test
    void listarMisNotificaciones_conToken_retorna200() throws Exception {
        mockUsuario();
        when(notificacionService.listarPorUsuarioPaginado(eq(USUARIO_ID), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(new NotificacionDTO())));

        mockMvc.perform(get(BASE_URL)
                        .header("Authorization", "Bearer " + tokenUsuario()))
                .andExpect(status().isOk());
    }

    @Test
    void listarMisNotificaciones_sinToken_retorna401() throws Exception {
        mockMvc.perform(get(BASE_URL))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void listarMisNotificaciones_conPaginacion_retorna200() throws Exception {
        mockUsuario();
        when(notificacionService.listarPorUsuarioPaginado(eq(USUARIO_ID), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));

        mockMvc.perform(get(BASE_URL + "?page=1&size=10")
                        .header("Authorization", "Bearer " + tokenUsuario()))
                .andExpect(status().isOk());
    }

    @Test
    void marcarLeida_conToken_retorna204() throws Exception {
        doNothing().when(notificacionService).marcarLeida(1L);

        mockMvc.perform(put(URL_LEIDA)
                        .header("Authorization", "Bearer " + tokenUsuario()))
                .andExpect(status().isNoContent());
    }

    @Test
    void marcarLeida_sinToken_retorna401() throws Exception {
        mockMvc.perform(put(URL_LEIDA))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void marcarTodasLeidas_conToken_retorna204() throws Exception {
        mockUsuario();
        doNothing().when(notificacionService).marcarTodasLeidas(USUARIO_ID);

        mockMvc.perform(put(URL_TODAS_LEIDAS)
                        .header("Authorization", "Bearer " + tokenUsuario()))
                .andExpect(status().isNoContent());
    }

    @Test
    void marcarTodasLeidas_sinToken_retorna401() throws Exception {
        mockMvc.perform(put(URL_TODAS_LEIDAS))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void enviarIndividual_conRolAdmin_retorna204() throws Exception {
        doNothing().when(notificacionService).enviarNotificacion(any());

        mockMvc.perform(post(URL_ENVIAR)
                        .header("Authorization", "Bearer " + tokenAdmin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(notificacionRequest())))
                .andExpect(status().isNoContent());
    }

    @Test
    void enviarIndividual_conRolUser_retorna403() throws Exception {
        mockMvc.perform(post(URL_ENVIAR)
                        .header("Authorization", "Bearer " + tokenUsuario())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(notificacionRequest())))
                .andExpect(status().isForbidden());
    }

    @Test
    void enviarIndividual_sinToken_retorna401() throws Exception {
        mockMvc.perform(post(URL_ENVIAR)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(notificacionRequest())))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void enviarMasiva_conRolAdmin_retorna204() throws Exception {
        doNothing().when(notificacionService).enviarMasiva(any());

        mockMvc.perform(post(URL_MASIVA)
                        .header("Authorization", "Bearer " + tokenAdmin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new NotificacionMasivaRequestDTO())))
                .andExpect(status().isNoContent());
    }

    @Test
    void enviarMasiva_conRolUser_retorna403() throws Exception {
        mockMvc.perform(post(URL_MASIVA)
                        .header("Authorization", "Bearer " + tokenUsuario())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void enviarMasiva_sinToken_retorna401() throws Exception {
        mockMvc.perform(post(URL_MASIVA)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void notificarPorPartido_conRolAdmin_retorna204() throws Exception {
        doNothing().when(notificacionService).notificarPorPartido(any(), any(), any(), any());

        mockMvc.perform(post(URL_PARTIDO)
                        .header("Authorization", "Bearer " + tokenAdmin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(notificacionRequest())))
                .andExpect(status().isNoContent());
    }

    @Test
    void notificarPorPartido_conRolUser_retorna403() throws Exception {
        mockMvc.perform(post(URL_PARTIDO)
                        .header("Authorization", "Bearer " + tokenUsuario())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(notificacionRequest())))
                .andExpect(status().isForbidden());
    }

    @Test
    void notificarPorPartido_sinToken_retorna401() throws Exception {
        mockMvc.perform(post(URL_PARTIDO)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(notificacionRequest())))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void buscarPorFecha_conToken_retorna200() throws Exception {
        mockUsuario();
        when(notificacionService.listarPorFecha(eq(USUARIO_ID), any(), any(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));

        mockMvc.perform(get(URL_BUSCAR)
                        .param("desde", "2026-01-01")
                        .param("hasta", "2026-12-31")
                        .header("Authorization", "Bearer " + tokenUsuario()))
                .andExpect(status().isOk());
    }

    @Test
    void buscarPorFecha_sinToken_retorna401() throws Exception {
        mockMvc.perform(get(URL_BUSCAR)
                        .param("desde", "2026-01-01")
                        .param("hasta", "2026-12-31"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void contarSinLeer_conToken_retorna200() throws Exception {
        mockUsuario();
        when(notificacionService.listarPorUsuario(USUARIO_ID)).thenReturn(List.of());

        mockMvc.perform(get(URL_CONTEO)
                        .header("Authorization", "Bearer " + tokenUsuario()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(0));
    }

    @Test
    void contarSinLeer_sinToken_retorna401() throws Exception {
        mockMvc.perform(get(URL_CONTEO))
                .andExpect(status().isUnauthorized());
    }
}