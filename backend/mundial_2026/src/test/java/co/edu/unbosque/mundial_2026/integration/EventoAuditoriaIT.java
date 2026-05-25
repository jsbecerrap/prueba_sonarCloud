package co.edu.unbosque.mundial_2026.integration;

import co.edu.unbosque.mundial_2026.dto.EventoAuditoriaDTO;
import co.edu.unbosque.mundial_2026.service.EventoAuditoriaService;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Pruebas de integración para los endpoints de auditoría.
 * <p>
 * Valida:
 * <ul>
 *     <li>Acceso permitido para administradores.</li>
 *     <li>Restricción de acceso para usuarios normales.</li>
 *     <li>Autenticación obligatoria.</li>
 *     <li>Búsquedas y filtros disponibles.</li>
 *     <li>Paginación de resultados.</li>
 * </ul>
 */
class EventoAuditoriaIT extends BaseIntegrationTest {

    private static final String AUTH_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    private static final String URL_TODOS = "/api/auditoria/todos";
    private static final String URL_BUSCAR = "/api/auditoria/buscar";
    private static final String URL_USUARIO = "/api/auditoria/usuario/1";
    private static final String URL_TIPO = "/api/auditoria/tipo/LOGIN";
    private static final String URL_CORRELACION = "/api/auditoria/correlacion/abc-123";
    private static final String URL_FECHA = "/api/auditoria/fecha";
    private static final String URL_ENTIDAD = "/api/auditoria/entidad/Orden";

    @MockitoBean
    private EventoAuditoriaService eventoService;

    /**
     * Verifica que un administrador pueda consultar todos los eventos.
     *
     * @throws Exception si ocurre un error en la petición
     */
    @Test
    void obtenerTodos_conRolAdmin_retorna200() throws Exception {
        when(eventoService.obtenerTodos(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(new EventoAuditoriaDTO())));

        mockMvc.perform(get(URL_TODOS)
                        .header(AUTH_HEADER, BEARER_PREFIX + tokenAdmin()))
                .andExpect(status().isOk());
    }

    /**
     * Verifica que un usuario normal no pueda consultar todos los eventos.
     *
     * @throws Exception si ocurre un error en la petición
     */
    @Test
    void obtenerTodos_conRolUser_retorna403() throws Exception {
        mockMvc.perform(get(URL_TODOS)
                        .header(AUTH_HEADER, BEARER_PREFIX + tokenUsuario()))
                .andExpect(status().isForbidden());
    }

    /**
     * Verifica que consultar todos los eventos sin token retorne 401.
     *
     * @throws Exception si ocurre un error en la petición
     */
    @Test
    void obtenerTodos_sinToken_retorna401() throws Exception {
        mockMvc.perform(get(URL_TODOS))
                .andExpect(status().isUnauthorized());
    }

    /**
     * Verifica la consulta paginada de eventos de auditoría.
     *
     * @throws Exception si ocurre un error en la petición
     */
    @Test
    void obtenerTodos_conPaginacion_retorna200() throws Exception {
        when(eventoService.obtenerTodos(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));

        mockMvc.perform(get(URL_TODOS + "?page=1&size=10")
                        .header(AUTH_HEADER, BEARER_PREFIX + tokenAdmin()))
                .andExpect(status().isOk());
    }

    /**
     * Verifica la búsqueda sin filtros.
     *
     * @throws Exception si ocurre un error en la petición
     */
    @Test
    void buscar_sinFiltros_retorna200() throws Exception {
        when(eventoService.buscarConFiltros(isNull(), isNull(), isNull(), isNull(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));

        mockMvc.perform(get(URL_BUSCAR)
                        .header(AUTH_HEADER, BEARER_PREFIX + tokenAdmin()))
                .andExpect(status().isOk());
    }

    /**
     * Verifica la búsqueda filtrando por usuario.
     *
     * @throws Exception si ocurre un error en la petición
     */
    @Test
    void buscar_conFiltroUsuario_retorna200() throws Exception {
        when(eventoService.buscarConFiltros(eq(1L), isNull(), isNull(), isNull(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));

        mockMvc.perform(get(URL_BUSCAR + "?usuarioId=1")
                        .header(AUTH_HEADER, BEARER_PREFIX + tokenAdmin()))
                .andExpect(status().isOk());
    }

    /**
     * Verifica la búsqueda filtrando por tipo de evento.
     *
     * @throws Exception si ocurre un error en la petición
     */
    @Test
    void buscar_conFiltroTipos_retorna200() throws Exception {
        when(eventoService.buscarConFiltros(isNull(), eq("LOGIN"), isNull(), isNull(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));

        mockMvc.perform(get(URL_BUSCAR + "?tipos=LOGIN")
                        .header(AUTH_HEADER, BEARER_PREFIX + tokenAdmin()))
                .andExpect(status().isOk());
    }

    /**
     * Verifica que un usuario normal no pueda usar la búsqueda.
     *
     * @throws Exception si ocurre un error en la petición
     */
    @Test
    void buscar_conRolUser_retorna403() throws Exception {
        mockMvc.perform(get(URL_BUSCAR)
                        .header(AUTH_HEADER, BEARER_PREFIX + tokenUsuario()))
                .andExpect(status().isForbidden());
    }

    /**
     * Verifica que buscar sin token retorne 401.
     *
     * @throws Exception si ocurre un error en la petición
     */
    @Test
    void buscar_sinToken_retorna401() throws Exception {
        mockMvc.perform(get(URL_BUSCAR))
                .andExpect(status().isUnauthorized());
    }

    /**
     * Verifica la búsqueda de eventos por usuario.
     *
     * @throws Exception si ocurre un error en la petición
     */
    @Test
    void buscarPorUsuario_conRolAdmin_retorna200() throws Exception {
        when(eventoService.buscarPorUsuario(eq(1L), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));

        mockMvc.perform(get(URL_USUARIO)
                        .header(AUTH_HEADER, BEARER_PREFIX + tokenAdmin()))
                .andExpect(status().isOk());
    }

    /**
     * Verifica que un usuario normal no pueda buscar por usuario.
     *
     * @throws Exception si ocurre un error en la petición
     */
    @Test
    void buscarPorUsuario_conRolUser_retorna403() throws Exception {
        mockMvc.perform(get(URL_USUARIO)
                        .header(AUTH_HEADER, BEARER_PREFIX + tokenUsuario()))
                .andExpect(status().isForbidden());
    }

    /**
     * Verifica que buscar por usuario sin token retorne 401.
     *
     * @throws Exception si ocurre un error en la petición
     */
    @Test
    void buscarPorUsuario_sinToken_retorna401() throws Exception {
        mockMvc.perform(get(URL_USUARIO))
                .andExpect(status().isUnauthorized());
    }

    /**
     * Verifica la búsqueda de eventos por tipo.
     *
     * @throws Exception si ocurre un error en la petición
     */
    @Test
    void buscarPorTipo_conRolAdmin_retorna200() throws Exception {
        when(eventoService.buscarPorTipo(eq("LOGIN"), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));

        mockMvc.perform(get(URL_TIPO)
                        .header(AUTH_HEADER, BEARER_PREFIX + tokenAdmin()))
                .andExpect(status().isOk());
    }

    /**
     * Verifica que un usuario normal no pueda buscar por tipo.
     *
     * @throws Exception si ocurre un error en la petición
     */
    @Test
    void buscarPorTipo_conRolUser_retorna403() throws Exception {
        mockMvc.perform(get(URL_TIPO)
                        .header(AUTH_HEADER, BEARER_PREFIX + tokenUsuario()))
                .andExpect(status().isForbidden());
    }

    /**
     * Verifica que buscar por tipo sin token retorne 401.
     *
     * @throws Exception si ocurre un error en la petición
     */
    @Test
    void buscarPorTipo_sinToken_retorna401() throws Exception {
        mockMvc.perform(get(URL_TIPO))
                .andExpect(status().isUnauthorized());
    }

    /**
     * Verifica la búsqueda por correlación.
     *
     * @throws Exception si ocurre un error en la petición
     */
    @Test
    void buscarPorCorrelacion_conRolAdmin_retorna200() throws Exception {
        when(eventoService.buscarPorCorrelacion(eq("abc-123"), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));

        mockMvc.perform(get(URL_CORRELACION)
                        .header(AUTH_HEADER, BEARER_PREFIX + tokenAdmin()))
                .andExpect(status().isOk());
    }

    /**
     * Verifica que un usuario normal no pueda buscar por correlación.
     *
     * @throws Exception si ocurre un error en la petición
     */
    @Test
    void buscarPorCorrelacion_conRolUser_retorna403() throws Exception {
        mockMvc.perform(get(URL_CORRELACION)
                        .header(AUTH_HEADER, BEARER_PREFIX + tokenUsuario()))
                .andExpect(status().isForbidden());
    }

    /**
     * Verifica que buscar por correlación sin token retorne 401.
     *
     * @throws Exception si ocurre un error en la petición
     */
    @Test
    void buscarPorCorrelacion_sinToken_retorna401() throws Exception {
        mockMvc.perform(get(URL_CORRELACION))
                .andExpect(status().isUnauthorized());
    }

    /**
     * Verifica la búsqueda de eventos por rango de fechas.
     *
     * @throws Exception si ocurre un error en la petición
     */
    @Test
    void buscarPorFecha_conRolAdmin_retorna200() throws Exception {
        when(eventoService.buscarPorFecha(any(), any(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));

        mockMvc.perform(get(URL_FECHA)
                        .param("fechaInicio", "2026-01-01T00:00:00")
                        .param("fechaFin", "2026-12-31T23:59:59")
                        .header(AUTH_HEADER, BEARER_PREFIX + tokenAdmin()))
                .andExpect(status().isOk());
    }

    /**
     * Verifica que un usuario normal no pueda buscar por fechas.
     *
     * @throws Exception si ocurre un error en la petición
     */
    @Test
    void buscarPorFecha_conRolUser_retorna403() throws Exception {
        mockMvc.perform(get(URL_FECHA)
                        .param("fechaInicio", "2026-01-01T00:00:00")
                        .param("fechaFin", "2026-12-31T23:59:59")
                        .header(AUTH_HEADER, BEARER_PREFIX + tokenUsuario()))
                .andExpect(status().isForbidden());
    }

    /**
     * Verifica que buscar por fechas sin token retorne 401.
     *
     * @throws Exception si ocurre un error en la petición
     */
    @Test
    void buscarPorFecha_sinToken_retorna401() throws Exception {
        mockMvc.perform(get(URL_FECHA)
                        .param("fechaInicio", "2026-01-01T00:00:00")
                        .param("fechaFin", "2026-12-31T23:59:59"))
                .andExpect(status().isUnauthorized());
    }

    /**
     * Verifica la búsqueda de eventos por entidad.
     *
     * @throws Exception si ocurre un error en la petición
     */
    @Test
    void buscarPorEntidad_conRolAdmin_retorna200() throws Exception {
        when(eventoService.buscarPorEntidad(eq("Orden"), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));

        mockMvc.perform(get(URL_ENTIDAD)
                        .header(AUTH_HEADER, BEARER_PREFIX + tokenAdmin()))
                .andExpect(status().isOk());
    }

    /**
     * Verifica que un usuario normal no pueda buscar por entidad.
     *
     * @throws Exception si ocurre un error en la petición
     */
    @Test
    void buscarPorEntidad_conRolUser_retorna403() throws Exception {
        mockMvc.perform(get(URL_ENTIDAD)
                        .header(AUTH_HEADER, BEARER_PREFIX + tokenUsuario()))
                .andExpect(status().isForbidden());
    }

    /**
     * Verifica que buscar por entidad sin token retorne 401.
     *
     * @throws Exception si ocurre un error en la petición
     */
    @Test
    void buscarPorEntidad_sinToken_retorna401() throws Exception {
        mockMvc.perform(get(URL_ENTIDAD))
                .andExpect(status().isUnauthorized());
    }
}