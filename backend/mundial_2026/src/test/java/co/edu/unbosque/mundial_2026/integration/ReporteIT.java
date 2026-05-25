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

/**
 * Pruebas de integracion para los endpoints de reportes
 * 
 * Valida autenticacion
 * autorizacion por roles
 * y respuestas exitosas de los endpoints administrativos
 */
class ReporteIT extends BaseIntegrationTest {

    /**
     * URL del endpoint de estadisticas generales
     */
    private static final String URL_ESTADISTICAS = "/api/reportes/estadisticas-generales";

    /**
     * URL del endpoint de reportes de compras
     */
    private static final String URL_COMPRAS = "/api/reportes/compras";

    /**
     * URL del endpoint de partidos mas apostados
     */
    private static final String URL_PARTIDOS_APOSTADOS = "/api/reportes/partidos-apostados";

    /**
     * URL del endpoint de ranking de pollas
     */
    private static final String URL_POLLAS = "/api/reportes/pollas";

    /**
     * URL del endpoint de ingresos por metodo de pago
     */
    private static final String URL_METODOS_PAGO = "/api/reportes/metodos-pago";

    /**
     * URL del endpoint de entradas por partido
     */
    private static final String URL_ENTRADAS_PARTIDO = "/api/reportes/entradas-por-partido";

    /**
     * URL del endpoint de top usuarios souvenir
     */
    private static final String URL_TOP_SOUVENIR = "/api/reportes/top-souvenir";

    /**
     * URL del endpoint de top usuarios entradas
     */
    private static final String URL_TOP_ENTRADAS = "/api/reportes/top-entradas";

    /**
     * Nombre del header de autorizacion
     */
    private static final String AUTH_HEADER = "Authorization";

    /**
     * Prefijo Bearer para tokens JWT
     */
    private static final String BEARER_PREFIX = "Bearer ";

    @MockitoBean
    private ReporteService reportesService;

    /**
     * Verifica acceso exitoso a estadisticas generales
     * con rol administrador
     * 
     * @throws Exception error durante la prueba
     */
    @Test
    void obtenerEstadisticasGenerales_conRolAdmin_retorna200() throws Exception {
        when(reportesService.obtenerEstadisticasGenerales())
                .thenReturn(new ReportesResponseDTO());

        mockMvc.perform(get(URL_ESTADISTICAS)
                        .header(AUTH_HEADER, BEARER_PREFIX + tokenAdmin()))
                .andExpect(status().isOk());
    }

    /**
     * Verifica acceso denegado a estadisticas generales
     * para usuarios sin permisos administrativos
     * 
     * @throws Exception error durante la prueba
     */
    @Test
    void obtenerEstadisticasGenerales_conRolUser_retorna403() throws Exception {
        mockMvc.perform(get(URL_ESTADISTICAS)
                        .header(AUTH_HEADER, BEARER_PREFIX + tokenUsuario()))
                .andExpect(status().isForbidden());
    }

    /**
     * Verifica acceso no autorizado
     * cuando no se envia token
     * 
     * @throws Exception error durante la prueba
     */
    @Test
    void obtenerEstadisticasGenerales_sinToken_retorna401() throws Exception {
        mockMvc.perform(get(URL_ESTADISTICAS))
                .andExpect(status().isUnauthorized());
    }

    /**
     * Verifica acceso exitoso al reporte de compras
     * con rol administrador
     * 
     * @throws Exception error durante la prueba
     */
    @Test
    void obtenerReportesCompras_conRolAdmin_retorna200() throws Exception {
        when(reportesService.obtenerReportesCompras())
                .thenReturn(new ReportesComprasDTO());

        mockMvc.perform(get(URL_COMPRAS)
                        .header(AUTH_HEADER, BEARER_PREFIX + tokenAdmin()))
                .andExpect(status().isOk());
    }

    /**
     * Verifica acceso denegado al reporte de compras
     * para usuarios sin permisos
     * 
     * @throws Exception error durante la prueba
     */
    @Test
    void obtenerReportesCompras_conRolUser_retorna403() throws Exception {
        mockMvc.perform(get(URL_COMPRAS)
                        .header(AUTH_HEADER, BEARER_PREFIX + tokenUsuario()))
                .andExpect(status().isForbidden());
    }

    /**
     * Verifica acceso no autorizado al reporte de compras
     * cuando no se envia token
     * 
     * @throws Exception error durante la prueba
     */
    @Test
    void obtenerReportesCompras_sinToken_retorna401() throws Exception {
        mockMvc.perform(get(URL_COMPRAS))
                .andExpect(status().isUnauthorized());
    }

    /**
     * Verifica acceso exitoso al reporte
     * de partidos mas apostados
     * 
     * @throws Exception error durante la prueba
     */
    @Test
    void obtenerPartidosMasApostados_conRolAdmin_retorna200() throws Exception {
        when(reportesService.obtenerPartidosMasApostados())
                .thenReturn(List.of(new PartidoMasApostadoDTO()));

        mockMvc.perform(get(URL_PARTIDOS_APOSTADOS)
                        .header(AUTH_HEADER, BEARER_PREFIX + tokenAdmin()))
                .andExpect(status().isOk());
    }

    /**
     * Verifica acceso denegado al reporte
     * de partidos mas apostados
     * 
     * @throws Exception error durante la prueba
     */
    @Test
    void obtenerPartidosMasApostados_conRolUser_retorna403() throws Exception {
        mockMvc.perform(get(URL_PARTIDOS_APOSTADOS)
                        .header(AUTH_HEADER, BEARER_PREFIX + tokenUsuario()))
                .andExpect(status().isForbidden());
    }

    /**
     * Verifica acceso no autorizado al reporte
     * de partidos mas apostados
     * 
     * @throws Exception error durante la prueba
     */
    @Test
    void obtenerPartidosMasApostados_sinToken_retorna401() throws Exception {
        mockMvc.perform(get(URL_PARTIDOS_APOSTADOS))
                .andExpect(status().isUnauthorized());
    }

    /**
     * Verifica acceso exitoso al ranking de pollas
     * con rol administrador
     * 
     * @throws Exception error durante la prueba
     */
    @Test
    void obtenerPollaRanking_conRolAdmin_retorna200() throws Exception {
        when(reportesService.obtenerPollaRanking())
                .thenReturn(List.of(new PollaRankingDTO()));

        mockMvc.perform(get(URL_POLLAS)
                        .header(AUTH_HEADER, BEARER_PREFIX + tokenAdmin()))
                .andExpect(status().isOk());
    }

    /**
     * Verifica acceso denegado al ranking de pollas
     * para usuarios sin permisos
     * 
     * @throws Exception error durante la prueba
     */
    @Test
    void obtenerPollaRanking_conRolUser_retorna403() throws Exception {
        mockMvc.perform(get(URL_POLLAS)
                        .header(AUTH_HEADER, BEARER_PREFIX + tokenUsuario()))
                .andExpect(status().isForbidden());
    }

    /**
     * Verifica acceso no autorizado al ranking de pollas
     * cuando no se envia token
     * 
     * @throws Exception error durante la prueba
     */
    @Test
    void obtenerPollaRanking_sinToken_retorna401() throws Exception {
        mockMvc.perform(get(URL_POLLAS))
                .andExpect(status().isUnauthorized());
    }

    /**
     * Verifica acceso exitoso al reporte
     * de ingresos por metodo de pago
     * 
     * @throws Exception error durante la prueba
     */
    @Test
    void obtenerIngresosPorMetodoPago_conRolAdmin_retorna200() throws Exception {
        when(reportesService.obtenerIngresosPorMetodoPago())
                .thenReturn(List.of(new IngresoMetodoPagoDTO()));

        mockMvc.perform(get(URL_METODOS_PAGO)
                        .header(AUTH_HEADER, BEARER_PREFIX + tokenAdmin()))
                .andExpect(status().isOk());
    }

    /**
     * Verifica acceso denegado al reporte
     * de ingresos por metodo de pago
     * 
     * @throws Exception error durante la prueba
     */
    @Test
    void obtenerIngresosPorMetodoPago_conRolUser_retorna403() throws Exception {
        mockMvc.perform(get(URL_METODOS_PAGO)
                        .header(AUTH_HEADER, BEARER_PREFIX + tokenUsuario()))
                .andExpect(status().isForbidden());
    }

    /**
     * Verifica acceso no autorizado al reporte
     * de ingresos por metodo de pago
     * 
     * @throws Exception error durante la prueba
     */
    @Test
    void obtenerIngresosPorMetodoPago_sinToken_retorna401() throws Exception {
        mockMvc.perform(get(URL_METODOS_PAGO))
                .andExpect(status().isUnauthorized());
    }

    /**
     * Verifica acceso exitoso al reporte
     * de entradas por partido
     * 
     * @throws Exception error durante la prueba
     */
    @Test
    void obtenerEntradasPorPartido_conRolAdmin_retorna200() throws Exception {
        when(reportesService.obtenerEntradasPorPartido())
                .thenReturn(List.of(new EntradaPorPartidoDTO()));

        mockMvc.perform(get(URL_ENTRADAS_PARTIDO)
                        .header(AUTH_HEADER, BEARER_PREFIX + tokenAdmin()))
                .andExpect(status().isOk());
    }

    /**
     * Verifica acceso denegado al reporte
     * de entradas por partido
     * 
     * @throws Exception error durante la prueba
     */
    @Test
    void obtenerEntradasPorPartido_conRolUser_retorna403() throws Exception {
        mockMvc.perform(get(URL_ENTRADAS_PARTIDO)
                        .header(AUTH_HEADER, BEARER_PREFIX + tokenUsuario()))
                .andExpect(status().isForbidden());
    }

    /**
     * Verifica acceso no autorizado al reporte
     * de entradas por partido
     * 
     * @throws Exception error durante la prueba
     */
    @Test
    void obtenerEntradasPorPartido_sinToken_retorna401() throws Exception {
        mockMvc.perform(get(URL_ENTRADAS_PARTIDO))
                .andExpect(status().isUnauthorized());
    }

    /**
     * Verifica acceso exitoso al top de usuarios
     * compradores de souvenirs
     * 
     * @throws Exception error durante la prueba
     */
    @Test
    void obtenerTopSouvenir_conRolAdmin_retorna200() throws Exception {
        when(reportesService.obtenerTopUsuariosSouvenir(any(Pageable.class)))
                .thenReturn(List.of(new TopUsuarioSouvenirDTO()));

        mockMvc.perform(get(URL_TOP_SOUVENIR)
                        .header(AUTH_HEADER, BEARER_PREFIX + tokenAdmin()))
                .andExpect(status().isOk());
    }

    /**
     * Verifica acceso exitoso al top de souvenirs
     * usando parametro size
     * 
     * @throws Exception error durante la prueba
     */
    @Test
    void obtenerTopSouvenir_conSize_retorna200() throws Exception {
        when(reportesService.obtenerTopUsuariosSouvenir(any(Pageable.class)))
                .thenReturn(List.of());

        mockMvc.perform(get(URL_TOP_SOUVENIR + "?size=10")
                        .header(AUTH_HEADER, BEARER_PREFIX + tokenAdmin()))
                .andExpect(status().isOk());
    }

    /**
     * Verifica acceso denegado al top de souvenirs
     * para usuarios sin permisos
     * 
     * @throws Exception error durante la prueba
     */
    @Test
    void obtenerTopSouvenir_conRolUser_retorna403() throws Exception {
        mockMvc.perform(get(URL_TOP_SOUVENIR)
                        .header(AUTH_HEADER, BEARER_PREFIX + tokenUsuario()))
                .andExpect(status().isForbidden());
    }

    /**
     * Verifica acceso no autorizado al top de souvenirs
     * cuando no se envia token
     * 
     * @throws Exception error durante la prueba
     */
    @Test
    void obtenerTopSouvenir_sinToken_retorna401() throws Exception {
        mockMvc.perform(get(URL_TOP_SOUVENIR))
                .andExpect(status().isUnauthorized());
    }

    /**
     * Verifica acceso exitoso al top de usuarios
     * compradores de entradas
     * 
     * @throws Exception error durante la prueba
     */
    @Test
    void obtenerTopEntradas_conRolAdmin_retorna200() throws Exception {
        when(reportesService.obtenerTopUsuariosEntrada(any(Pageable.class)))
                .thenReturn(List.of(new TopUsuarioEntradaDTO()));

        mockMvc.perform(get(URL_TOP_ENTRADAS)
                        .header(AUTH_HEADER, BEARER_PREFIX + tokenAdmin()))
                .andExpect(status().isOk());
    }

    /**
     * Verifica acceso exitoso al top de entradas
     * usando parametro size
     * 
     * @throws Exception error durante la prueba
     */
    @Test
    void obtenerTopEntradas_conSize_retorna200() throws Exception {
        when(reportesService.obtenerTopUsuariosEntrada(any(Pageable.class)))
                .thenReturn(List.of());

        mockMvc.perform(get(URL_TOP_ENTRADAS + "?size=10")
                        .header(AUTH_HEADER, BEARER_PREFIX + tokenAdmin()))
                .andExpect(status().isOk());
    }

    /**
     * Verifica acceso denegado al top de entradas
     * para usuarios sin permisos
     * 
     * @throws Exception error durante la prueba
     */
    @Test
    void obtenerTopEntradas_conRolUser_retorna403() throws Exception {
        mockMvc.perform(get(URL_TOP_ENTRADAS)
                        .header(AUTH_HEADER, BEARER_PREFIX + tokenUsuario()))
                .andExpect(status().isForbidden());
    }

    /**
     * Verifica acceso no autorizado al top de entradas
     * cuando no se envia token
     * 
     * @throws Exception error durante la prueba
     */
    @Test
    void obtenerTopEntradas_sinToken_retorna401() throws Exception {
        mockMvc.perform(get(URL_TOP_ENTRADAS))
                .andExpect(status().isUnauthorized());
    }
}