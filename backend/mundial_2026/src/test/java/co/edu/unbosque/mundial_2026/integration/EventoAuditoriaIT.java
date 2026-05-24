package co.edu.unbosque.mundial_2026.integration;

import co.edu.unbosque.mundial_2026.dto.EventoAuditoriaDTO;
import co.edu.unbosque.mundial_2026.service.EventoAuditoriaService;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class EventoAuditoriaIT extends BaseIntegrationTest {

    @MockitoBean
    private EventoAuditoriaService eventoService;
    private static final String AUTH_HEADER = "Authorization";
private static final String BEARER_PREFIX = "Bearer ";



    @Test
    void obtenerTodos_conRolAdmin_retorna200() throws Exception {
        when(eventoService.obtenerTodos(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(new EventoAuditoriaDTO())));

        mockMvc.perform(get("/api/auditoria/todos")
                        .header(AUTH_HEADER, BEARER_PREFIX + tokenAdmin()))
                .andExpect(status().isOk());
    }

    @Test
    void obtenerTodos_conRolUser_retorna403() throws Exception {
        mockMvc.perform(get("/api/auditoria/todos")
                        .header(AUTH_HEADER, BEARER_PREFIX + tokenUsuario()))
                .andExpect(status().isForbidden());
    }

    @Test
    void obtenerTodos_sinToken_retorna401() throws Exception {
        mockMvc.perform(get("/api/auditoria/todos"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void obtenerTodos_conPaginacion_retorna200() throws Exception {
        when(eventoService.obtenerTodos(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));

        mockMvc.perform(get("/api/auditoria/todos?page=1&size=10")
                        .header(AUTH_HEADER, BEARER_PREFIX + tokenAdmin()))
                .andExpect(status().isOk());
    }

   

    @Test
    void buscar_sinFiltros_retorna200() throws Exception {
        when(eventoService.buscarConFiltros(isNull(), isNull(), isNull(), isNull(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));

        mockMvc.perform(get("/api/auditoria/buscar")
                        .header(AUTH_HEADER, BEARER_PREFIX + tokenAdmin()))
                .andExpect(status().isOk());
    }

    @Test
    void buscar_conFiltroUsuario_retorna200() throws Exception {
        when(eventoService.buscarConFiltros(eq(1L), isNull(), isNull(), isNull(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));

        mockMvc.perform(get("/api/auditoria/buscar?usuarioId=1")
                        .header(AUTH_HEADER, BEARER_PREFIX + tokenAdmin()))
                .andExpect(status().isOk());
    }

    @Test
    void buscar_conFiltroTipos_retorna200() throws Exception {
        when(eventoService.buscarConFiltros(isNull(), eq("LOGIN"), isNull(), isNull(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));

        mockMvc.perform(get("/api/auditoria/buscar?tipos=LOGIN")
                        .header(AUTH_HEADER, BEARER_PREFIX + tokenAdmin()))
                .andExpect(status().isOk());
    }

    @Test
    void buscar_conRolUser_retorna403() throws Exception {
        mockMvc.perform(get("/api/auditoria/buscar")
                        .header(AUTH_HEADER, BEARER_PREFIX + tokenUsuario()))
                .andExpect(status().isForbidden());
    }

    @Test
    void buscar_sinToken_retorna401() throws Exception {
        mockMvc.perform(get("/api/auditoria/buscar"))
                .andExpect(status().isUnauthorized());
    }


    @Test
    void buscarPorUsuario_conRolAdmin_retorna200() throws Exception {
        when(eventoService.buscarPorUsuario(eq(1L), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));

        mockMvc.perform(get("/api/auditoria/usuario/1")
                        .header(AUTH_HEADER, BEARER_PREFIX + tokenAdmin()))
                .andExpect(status().isOk());
    }

    @Test
    void buscarPorUsuario_conRolUser_retorna403() throws Exception {
        mockMvc.perform(get("/api/auditoria/usuario/1")
                        .header(AUTH_HEADER, BEARER_PREFIX + tokenUsuario()))
                .andExpect(status().isForbidden());
    }

    @Test
    void buscarPorUsuario_sinToken_retorna401() throws Exception {
        mockMvc.perform(get("/api/auditoria/usuario/1"))
                .andExpect(status().isUnauthorized());
    }

    

    @Test
    void buscarPorTipo_conRolAdmin_retorna200() throws Exception {
        when(eventoService.buscarPorTipo(eq("LOGIN"), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));

        mockMvc.perform(get("/api/auditoria/tipo/LOGIN")
                        .header(AUTH_HEADER, BEARER_PREFIX + tokenAdmin()))
                .andExpect(status().isOk());
    }

    @Test
    void buscarPorTipo_conRolUser_retorna403() throws Exception {
        mockMvc.perform(get("/api/auditoria/tipo/LOGIN")
                        .header(AUTH_HEADER, BEARER_PREFIX + tokenUsuario()))
                .andExpect(status().isForbidden());
    }

    @Test
    void buscarPorTipo_sinToken_retorna401() throws Exception {
        mockMvc.perform(get("/api/auditoria/tipo/LOGIN"))
                .andExpect(status().isUnauthorized());
    }


    @Test
    void buscarPorCorrelacion_conRolAdmin_retorna200() throws Exception {
        when(eventoService.buscarPorCorrelacion(eq("abc-123"), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));

        mockMvc.perform(get("/api/auditoria/correlacion/abc-123")
                        .header(AUTH_HEADER, BEARER_PREFIX + tokenAdmin()))
                .andExpect(status().isOk());
    }

    @Test
    void buscarPorCorrelacion_conRolUser_retorna403() throws Exception {
        mockMvc.perform(get("/api/auditoria/correlacion/abc-123")
                        .header(AUTH_HEADER, BEARER_PREFIX + tokenUsuario()))
                .andExpect(status().isForbidden());
    }

    @Test
    void buscarPorCorrelacion_sinToken_retorna401() throws Exception {
        mockMvc.perform(get("/api/auditoria/correlacion/abc-123"))
                .andExpect(status().isUnauthorized());
    }

   

    @Test
    void buscarPorFecha_conRolAdmin_retorna200() throws Exception {
        when(eventoService.buscarPorFecha(any(), any(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));

        mockMvc.perform(get("/api/auditoria/fecha")
                        .param("fechaInicio", "2026-01-01T00:00:00")
                        .param("fechaFin", "2026-12-31T23:59:59")
                        .header(AUTH_HEADER, BEARER_PREFIX + tokenAdmin()))
                .andExpect(status().isOk());
    }

    @Test
    void buscarPorFecha_conRolUser_retorna403() throws Exception {
        mockMvc.perform(get("/api/auditoria/fecha")
                        .param("fechaInicio", "2026-01-01T00:00:00")
                        .param("fechaFin", "2026-12-31T23:59:59")
                        .header(AUTH_HEADER, BEARER_PREFIX + tokenUsuario()))
                .andExpect(status().isForbidden());
    }

    @Test
    void buscarPorFecha_sinToken_retorna401() throws Exception {
        mockMvc.perform(get("/api/auditoria/fecha")
                        .param("fechaInicio", "2026-01-01T00:00:00")
                        .param("fechaFin", "2026-12-31T23:59:59"))
                .andExpect(status().isUnauthorized());
    }

    

    @Test
    void buscarPorEntidad_conRolAdmin_retorna200() throws Exception {
        when(eventoService.buscarPorEntidad(eq("Orden"), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));

        mockMvc.perform(get("/api/auditoria/entidad/Orden")
                        .header(AUTH_HEADER, BEARER_PREFIX + tokenAdmin()))
                .andExpect(status().isOk());
    }

    @Test
    void buscarPorEntidad_conRolUser_retorna403() throws Exception {
        mockMvc.perform(get("/api/auditoria/entidad/Orden")
                        .header(AUTH_HEADER, BEARER_PREFIX + tokenUsuario()))
                .andExpect(status().isForbidden());
    }

    @Test
    void buscarPorEntidad_sinToken_retorna401() throws Exception {
        mockMvc.perform(get("/api/auditoria/entidad/Orden"))
                .andExpect(status().isUnauthorized());
    }
}