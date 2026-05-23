package co.edu.unbosque.mundial_2026.integration;

import co.edu.unbosque.mundial_2026.dto.response.EntradaPorPartidoDTO;
import co.edu.unbosque.mundial_2026.dto.response.IngresoMetodoPagoDTO;
import co.edu.unbosque.mundial_2026.dto.response.PartidoMasApostadoDTO;
import co.edu.unbosque.mundial_2026.dto.response.PollaRankingDTO;
import co.edu.unbosque.mundial_2026.dto.response.ReportesComprasDTO;
import co.edu.unbosque.mundial_2026.dto.response.ReportesResponseDTO;
import co.edu.unbosque.mundial_2026.dto.response.TopUsuarioEntradaDTO;
import co.edu.unbosque.mundial_2026.dto.response.TopUsuarioSouvenirDTO;
import co.edu.unbosque.mundial_2026.service.ReporteService;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ReporteIT extends BaseIntegrationTest {

    private static final String URL_ESTADISTICAS = "/api/reportes/estadisticas-generales";
    private static final String URL_COMPRAS = "/api/reportes/compras";
    private static final String URL_PARTIDOS_APOSTADOS = "/api/reportes/partidos-apostados";
    private static final String URL_POLLAS = "/api/reportes/pollas";
    private static final String URL_METODOS_PAGO = "/api/reportes/metodos-pago";
    private static final String URL_ENTRADAS_PARTIDO = "/api/reportes/entradas-por-partido";
    private static final String URL_TOP_SOUVENIR = "/api/reportes/top-souvenir";
    private static final String URL_TOP_ENTRADAS = "/api/reportes/top-entradas";

    @MockitoBean
    private ReporteService reportesService;

    @Test
    void obtenerEstadisticasGenerales_conRolAdmin_retorna200() throws Exception {
        when(reportesService.obtenerEstadisticasGenerales()).thenReturn(new ReportesResponseDTO());

        mockMvc.perform(get(URL_ESTADISTICAS)
                        .header("Authorization", "Bearer " + tokenAdmin()))
                .andExpect(status().isOk());
    }

    @Test
    void obtenerEstadisticasGenerales_conRolUser_retorna403() throws Exception {
        mockMvc.perform(get(URL_ESTADISTICAS)
                        .header("Authorization", "Bearer " + tokenUsuario()))
                .andExpect(status().isForbidden());
    }

    @Test
    void obtenerEstadisticasGenerales_sinToken_retorna401() throws Exception {
        mockMvc.perform(get(URL_ESTADISTICAS))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void obtenerReportesCompras_conRolAdmin_retorna200() throws Exception {
        when(reportesService.obtenerReportesCompras()).thenReturn(new ReportesComprasDTO());

        mockMvc.perform(get(URL_COMPRAS)
                        .header("Authorization", "Bearer " + tokenAdmin()))
                .andExpect(status().isOk());
    }

    @Test
    void obtenerReportesCompras_conRolUser_retorna403() throws Exception {
        mockMvc.perform(get(URL_COMPRAS)
                        .header("Authorization", "Bearer " + tokenUsuario()))
                .andExpect(status().isForbidden());
    }

    @Test
    void obtenerReportesCompras_sinToken_retorna401() throws Exception {
        mockMvc.perform(get(URL_COMPRAS))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void obtenerPartidosMasApostados_conRolAdmin_retorna200() throws Exception {
        when(reportesService.obtenerPartidosMasApostados()).thenReturn(List.of(new PartidoMasApostadoDTO()));

        mockMvc.perform(get(URL_PARTIDOS_APOSTADOS)
                        .header("Authorization", "Bearer " + tokenAdmin()))
                .andExpect(status().isOk());
    }

    @Test
    void obtenerPartidosMasApostados_conRolUser_retorna403() throws Exception {
        mockMvc.perform(get(URL_PARTIDOS_APOSTADOS)
                        .header("Authorization", "Bearer " + tokenUsuario()))
                .andExpect(status().isForbidden());
    }

    @Test
    void obtenerPartidosMasApostados_sinToken_retorna401() throws Exception {
        mockMvc.perform(get(URL_PARTIDOS_APOSTADOS))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void obtenerPollaRanking_conRolAdmin_retorna200() throws Exception {
        when(reportesService.obtenerPollaRanking()).thenReturn(List.of(new PollaRankingDTO()));

        mockMvc.perform(get(URL_POLLAS)
                        .header("Authorization", "Bearer " + tokenAdmin()))
                .andExpect(status().isOk());
    }

    @Test
    void obtenerPollaRanking_conRolUser_retorna403() throws Exception {
        mockMvc.perform(get(URL_POLLAS)
                        .header("Authorization", "Bearer " + tokenUsuario()))
                .andExpect(status().isForbidden());
    }

    @Test
    void obtenerPollaRanking_sinToken_retorna401() throws Exception {
        mockMvc.perform(get(URL_POLLAS))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void obtenerIngresosPorMetodoPago_conRolAdmin_retorna200() throws Exception {
        when(reportesService.obtenerIngresosPorMetodoPago()).thenReturn(List.of(new IngresoMetodoPagoDTO()));

        mockMvc.perform(get(URL_METODOS_PAGO)
                        .header("Authorization", "Bearer " + tokenAdmin()))
                .andExpect(status().isOk());
    }

    @Test
    void obtenerIngresosPorMetodoPago_conRolUser_retorna403() throws Exception {
        mockMvc.perform(get(URL_METODOS_PAGO)
                        .header("Authorization", "Bearer " + tokenUsuario()))
                .andExpect(status().isForbidden());
    }

    @Test
    void obtenerIngresosPorMetodoPago_sinToken_retorna401() throws Exception {
        mockMvc.perform(get(URL_METODOS_PAGO))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void obtenerEntradasPorPartido_conRolAdmin_retorna200() throws Exception {
        when(reportesService.obtenerEntradasPorPartido()).thenReturn(List.of(new EntradaPorPartidoDTO()));

        mockMvc.perform(get(URL_ENTRADAS_PARTIDO)
                        .header("Authorization", "Bearer " + tokenAdmin()))
                .andExpect(status().isOk());
    }

    @Test
    void obtenerEntradasPorPartido_conRolUser_retorna403() throws Exception {
        mockMvc.perform(get(URL_ENTRADAS_PARTIDO)
                        .header("Authorization", "Bearer " + tokenUsuario()))
                .andExpect(status().isForbidden());
    }

    @Test
    void obtenerEntradasPorPartido_sinToken_retorna401() throws Exception {
        mockMvc.perform(get(URL_ENTRADAS_PARTIDO))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void obtenerTopSouvenir_conRolAdmin_retorna200() throws Exception {
        when(reportesService.obtenerTopUsuariosSouvenir(any(Pageable.class)))
                .thenReturn(List.of(new TopUsuarioSouvenirDTO()));

        mockMvc.perform(get(URL_TOP_SOUVENIR)
                        .header("Authorization", "Bearer " + tokenAdmin()))
                .andExpect(status().isOk());
    }

    @Test
    void obtenerTopSouvenir_conSize_retorna200() throws Exception {
        when(reportesService.obtenerTopUsuariosSouvenir(any(Pageable.class)))
                .thenReturn(List.of());

        mockMvc.perform(get(URL_TOP_SOUVENIR + "?size=10")
                        .header("Authorization", "Bearer " + tokenAdmin()))
                .andExpect(status().isOk());
    }

    @Test
    void obtenerTopSouvenir_conRolUser_retorna403() throws Exception {
        mockMvc.perform(get(URL_TOP_SOUVENIR)
                        .header("Authorization", "Bearer " + tokenUsuario()))
                .andExpect(status().isForbidden());
    }

    @Test
    void obtenerTopSouvenir_sinToken_retorna401() throws Exception {
        mockMvc.perform(get(URL_TOP_SOUVENIR))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void obtenerTopEntradas_conRolAdmin_retorna200() throws Exception {
        when(reportesService.obtenerTopUsuariosEntrada(any(Pageable.class)))
                .thenReturn(List.of(new TopUsuarioEntradaDTO()));

        mockMvc.perform(get(URL_TOP_ENTRADAS)
                        .header("Authorization", "Bearer " + tokenAdmin()))
                .andExpect(status().isOk());
    }

    @Test
    void obtenerTopEntradas_conSize_retorna200() throws Exception {
        when(reportesService.obtenerTopUsuariosEntrada(any(Pageable.class)))
                .thenReturn(List.of());

        mockMvc.perform(get(URL_TOP_ENTRADAS + "?size=10")
                        .header("Authorization", "Bearer " + tokenAdmin()))
                .andExpect(status().isOk());
    }

    @Test
    void obtenerTopEntradas_conRolUser_retorna403() throws Exception {
        mockMvc.perform(get(URL_TOP_ENTRADAS)
                        .header("Authorization", "Bearer " + tokenUsuario()))
                .andExpect(status().isForbidden());
    }

    @Test
    void obtenerTopEntradas_sinToken_retorna401() throws Exception {
        mockMvc.perform(get(URL_TOP_ENTRADAS))
                .andExpect(status().isUnauthorized());
    }
}