package co.edu.unbosque.mundial_2026.integration;

import co.edu.unbosque.mundial_2026.dto.response.EquipoMundialDTO;
import co.edu.unbosque.mundial_2026.dto.response.JugadorDTO;
import co.edu.unbosque.mundial_2026.dto.response.PartidoDTO;
import co.edu.unbosque.mundial_2026.dto.response.PosicionDTO;
import co.edu.unbosque.mundial_2026.dto.response.PreferenciaDTO;
import co.edu.unbosque.mundial_2026.entity.Partido;
import co.edu.unbosque.mundial_2026.service.PartidoService;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class PartidoIT extends BaseIntegrationTest {

    private static final String BASE_URL = "/api/partidos";
    private static final String URL_EQUIPO = "/api/partidos/equipo/1";
    private static final String URL_STANDINGS = "/api/partidos/standings";
    private static final String URL_SELECCIONES = "/api/partidos/selecciones";
    private static final String URL_JUGADORES = "/api/partidos/selecciones/1/jugadores";
    private static final String URL_FECHA = "/api/partidos/fecha/2026-06-10";
    private static final String URL_ENVIVO = "/api/partidos/envivo";
    private static final String URL_PREF_SELECCIONES = "/api/partidos/preferencias/selecciones";
    private static final String URL_PREF_ESTADIOS = "/api/partidos/preferencias/estadios";
    private static final String URL_PREF_CIUDADES = "/api/partidos/preferencias/ciudades";
    private static final String URL_SINCRONIZAR = "/api/partidos/sincronizar/1/2026/2026-06-10";
    private static final String URL_RESULTADO = "/api/partidos/1/resultado/2/1/90";
    private static final String URL_POR_ID = "/api/partidos/1";
    private static final String URL_CATALOGO = "/api/partidos/catalogo/selecciones";
    private static final String URL_BD_TODOS = "/api/partidos/bd/todos";

    @MockitoBean
    private PartidoService partidoService;

    void listarPartidos_sinToken_retorna401() throws Exception {
    mockMvc.perform(get(BASE_URL))
            .andExpect(status().isUnauthorized());
}

    @Test
    void obtenerPartidosPorEquipo_conToken_retorna200() throws Exception {
        when(partidoService.obtenerPartidosPorEquipo(1L)).thenReturn(List.of());

        mockMvc.perform(get(URL_EQUIPO)
                        .header("Authorization", "Bearer " + tokenUsuario()))
                .andExpect(status().isOk());
    }

    @Test
    void obtenerPartidosPorEquipo_sinToken_retorna401() throws Exception {
        mockMvc.perform(get(URL_EQUIPO))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void obtenerStandings_conToken_retorna200() throws Exception {
        when(partidoService.obtenerStandings()).thenReturn(List.of());

        mockMvc.perform(get(URL_STANDINGS)
                        .header("Authorization", "Bearer " + tokenUsuario()))
                .andExpect(status().isOk());
    }

    @Test
    void obtenerStandings_sinToken_retorna401() throws Exception {
        mockMvc.perform(get(URL_STANDINGS))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void obtenerSelecciones_conToken_retorna200() throws Exception {
        when(partidoService.obtenerSelecciones()).thenReturn(List.of(new EquipoMundialDTO()));

        mockMvc.perform(get(URL_SELECCIONES)
                        .header("Authorization", "Bearer " + tokenUsuario()))
                .andExpect(status().isOk());
    }

    @Test
    void obtenerSelecciones_sinToken_retorna401() throws Exception {
        mockMvc.perform(get(URL_SELECCIONES))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void obtenerJugadoresPorEquipo_conToken_retorna200() throws Exception {
        when(partidoService.obtenerJugadoresPorEquipo(1L)).thenReturn(List.of(new JugadorDTO()));

        mockMvc.perform(get(URL_JUGADORES)
                        .header("Authorization", "Bearer " + tokenUsuario()))
                .andExpect(status().isOk());
    }

    @Test
    void obtenerJugadoresPorEquipo_sinToken_retorna401() throws Exception {
        mockMvc.perform(get(URL_JUGADORES))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void obtenerPartidosPorFecha_conToken_retorna200() throws Exception {
        when(partidoService.obtenerPartidosPorFecha("2026-06-10")).thenReturn(List.of());

        mockMvc.perform(get(URL_FECHA)
                        .header("Authorization", "Bearer " + tokenUsuario()))
                .andExpect(status().isOk());
    }

    @Test
    void obtenerPartidosPorFecha_sinToken_retorna401() throws Exception {
        mockMvc.perform(get(URL_FECHA))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void obtenerPartidosEnVivo_conToken_retorna200() throws Exception {
        when(partidoService.obtenerPartidosEnVivo()).thenReturn(List.of());

        mockMvc.perform(get(URL_ENVIVO)
                        .header("Authorization", "Bearer " + tokenUsuario()))
                .andExpect(status().isOk());
    }

    @Test
    void obtenerPartidosEnVivo_sinToken_retorna401() throws Exception {
        mockMvc.perform(get(URL_ENVIVO))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void obtenerPartidosPorSeleccionesFav_conToken_retorna200() throws Exception {
        when(partidoService.obtenerPartidosPorSeleccionesFav(USER_EMAIL)).thenReturn(List.of());

        mockMvc.perform(get(URL_PREF_SELECCIONES)
                        .header("Authorization", "Bearer " + tokenUsuario()))
                .andExpect(status().isOk());
    }

    @Test
    void obtenerPartidosPorSeleccionesFav_sinToken_retorna401() throws Exception {
        mockMvc.perform(get(URL_PREF_SELECCIONES))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void obtenerPartidosPorEstadiosFav_conToken_retorna200() throws Exception {
        when(partidoService.obtenerPartidosPorEstadiosFav(USER_EMAIL)).thenReturn(List.of());

        mockMvc.perform(get(URL_PREF_ESTADIOS)
                        .header("Authorization", "Bearer " + tokenUsuario()))
                .andExpect(status().isOk());
    }

    @Test
    void obtenerPartidosPorEstadiosFav_sinToken_retorna401() throws Exception {
        mockMvc.perform(get(URL_PREF_ESTADIOS))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void obtenerPartidosPorCiudadesFav_conToken_retorna200() throws Exception {
        when(partidoService.obtenerPartidosPorCiudadesFav(USER_EMAIL)).thenReturn(List.of());

        mockMvc.perform(get(URL_PREF_CIUDADES)
                        .header("Authorization", "Bearer " + tokenUsuario()))
                .andExpect(status().isOk());
    }

    @Test
    void obtenerPartidosPorCiudadesFav_sinToken_retorna401() throws Exception {
        mockMvc.perform(get(URL_PREF_CIUDADES))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void sincronizar_conRolAdmin_retorna200() throws Exception {
        when(partidoService.sincronizarPorFechaYLiga("2026-06-10", 1, 2026)).thenReturn(5);

        mockMvc.perform(get(URL_SINCRONIZAR)
                        .header("Authorization", "Bearer " + tokenAdmin()))
                .andExpect(status().isOk());
    }

    @Test
    void sincronizar_conRolUser_retorna403() throws Exception {
        mockMvc.perform(get(URL_SINCRONIZAR)
                        .header("Authorization", "Bearer " + tokenUsuario()))
                .andExpect(status().isForbidden());
    }

    @Test
    void sincronizar_sinToken_retorna401() throws Exception {
        mockMvc.perform(get(URL_SINCRONIZAR))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void actualizarResultado_conToken_retorna200() throws Exception {
        when(partidoService.actualizarResultado(1L, 2, 1, 90)).thenReturn(1);

        mockMvc.perform(put(URL_RESULTADO)
                        .header("Authorization", "Bearer " + tokenUsuario()))
                .andExpect(status().isOk());
    }

    @Test
    void actualizarResultado_sinToken_retorna401() throws Exception {
        mockMvc.perform(put(URL_RESULTADO))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void obtenerPorId_conToken_retorna200() throws Exception {
        when(partidoService.obtenerPartidoPorId(1L)).thenReturn(new PartidoDTO());

        mockMvc.perform(get(URL_POR_ID)
                        .header("Authorization", "Bearer " + tokenUsuario()))
                .andExpect(status().isOk());
    }

    @Test
    void obtenerPorId_sinToken_retorna401() throws Exception {
        mockMvc.perform(get(URL_POR_ID))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void obtenerCatalogoSelecciones_conToken_retorna200() throws Exception {
        when(partidoService.obtenerCatalogoSelecciones()).thenReturn(List.of(new PreferenciaDTO(1L, "Test")));

        mockMvc.perform(get(URL_CATALOGO)
                        .header("Authorization", "Bearer " + tokenUsuario()))
                .andExpect(status().isOk());
    }

    @Test
    void obtenerCatalogoSelecciones_sinToken_retorna401() throws Exception {
        mockMvc.perform(get(URL_CATALOGO))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void listarDesdeBD_conRolAdmin_retorna200() throws Exception {
        when(partidoService.listarDesdeBD()).thenReturn(List.of(new Partido()));

        mockMvc.perform(get(URL_BD_TODOS)
                        .header("Authorization", "Bearer " + tokenAdmin()))
                .andExpect(status().isOk());
    }

    @Test
    void listarDesdeBD_conRolUser_retorna403() throws Exception {
        mockMvc.perform(get(URL_BD_TODOS)
                        .header("Authorization", "Bearer " + tokenUsuario()))
                .andExpect(status().isForbidden());
    }

    @Test
    void listarDesdeBD_sinToken_retorna401() throws Exception {
        mockMvc.perform(get(URL_BD_TODOS))
                .andExpect(status().isUnauthorized());
    }
}