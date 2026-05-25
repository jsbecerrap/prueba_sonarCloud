package co.edu.unbosque.mundial_2026.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import co.edu.unbosque.mundial_2026.dto.response.*;
import co.edu.unbosque.mundial_2026.service.ReporteService;

/**
 * Pruebas unitarias para ReporteController usando Mockito.
 * Verifica respuestas HTTP y datos retornados por cada endpoint.
 */
@ExtendWith(MockitoExtension.class)
class ReporteControllerTest {

    @Mock private ReporteService reportesService;
    @InjectMocks private ReporteController controller;

    /**
     * Verifica obtención correcta de estadísticas generales.
     */
    @Test
    void obtenerEstadisticasGenerales_retornaOkConDTO() {
        ReportesResponseDTO dto = new ReportesResponseDTO();
        dto.setTotalUsuarios(10);
        dto.setTotalPartidos(5);
        dto.setTotalTransacciones(20);
        dto.setUsuariosActivos(8);
        when(reportesService.obtenerEstadisticasGenerales()).thenReturn(dto);

        ResponseEntity<ReportesResponseDTO> res = controller.obtenerEstadisticasGenerales();

        assertEquals(200, res.getStatusCode().value());
        assertNotNull(res.getBody());
        assertEquals(10, res.getBody().getTotalUsuarios());
        assertEquals(5, res.getBody().getTotalPartidos());
        assertEquals(20, res.getBody().getTotalTransacciones());
        assertEquals(8, res.getBody().getUsuariosActivos());
        verify(reportesService).obtenerEstadisticasGenerales();
    }

    /**
     * Verifica estadísticas generales con valores por defecto.
     */
    @Test
    void obtenerEstadisticasGenerales_todosCero_retornaOk() {
        when(reportesService.obtenerEstadisticasGenerales()).thenReturn(new ReportesResponseDTO());

        ResponseEntity<ReportesResponseDTO> res = controller.obtenerEstadisticasGenerales();

        assertEquals(200, res.getStatusCode().value());
        assertNotNull(res.getBody());
    }

    /**
     * Verifica obtención correcta del reporte de compras.
     */
    @Test
    void obtenerReportesCompras_retornaOkConDTO() {
        ReportesComprasDTO dto = new ReportesComprasDTO();
        dto.setIngresoTotal(1500000.0);
        dto.setTotalOrdenes(30);
        dto.setTotalEntradasVendidas(100L);
        dto.setProductosMasVendidos(List.of());
        dto.setVentasPorCategoria(List.of());
        when(reportesService.obtenerReportesCompras()).thenReturn(dto);

        ResponseEntity<ReportesComprasDTO> res = controller.obtenerReportesCompras();

        assertEquals(200, res.getStatusCode().value());
        assertEquals(1500000.0, res.getBody().getIngresoTotal());
        assertEquals(30, res.getBody().getTotalOrdenes());
        assertEquals(100L, res.getBody().getTotalEntradasVendidas());
        verify(reportesService).obtenerReportesCompras();
    }

    /**
     * Verifica reporte de compras sin datos.
     */
    @Test
    void obtenerReportesCompras_sinDatos_retornaOk() {
        ReportesComprasDTO dto = new ReportesComprasDTO();
        dto.setIngresoTotal(0.0);
        dto.setProductosMasVendidos(List.of());
        dto.setVentasPorCategoria(List.of());
        when(reportesService.obtenerReportesCompras()).thenReturn(dto);

        ResponseEntity<ReportesComprasDTO> res = controller.obtenerReportesCompras();

        assertEquals(200, res.getStatusCode().value());
        assertTrue(res.getBody().getProductosMasVendidos().isEmpty());
    }

    /**
     * Verifica obtención de partidos más apostados.
     */
    @Test
    void obtenerPartidosMasApostados_retornaOkConLista() {
        PartidoMasApostadoDTO dto = new PartidoMasApostadoDTO(1L, "Colombia", "Brazil", "Grupo A", 200);
        when(reportesService.obtenerPartidosMasApostados()).thenReturn(List.of(dto));

        ResponseEntity<List<PartidoMasApostadoDTO>> res = controller.obtenerPartidosMasApostados();

        assertEquals(200, res.getStatusCode().value());
        assertEquals(1, res.getBody().size());
        assertEquals("Colombia", res.getBody().get(0).getLocal());
        verify(reportesService).obtenerPartidosMasApostados();
    }

    /**
     * Verifica lista vacía de partidos más apostados.
     */
    @Test
    void obtenerPartidosMasApostados_listaVacia_retornaOkVacio() {
        when(reportesService.obtenerPartidosMasApostados()).thenReturn(List.of());

        ResponseEntity<List<PartidoMasApostadoDTO>> res = controller.obtenerPartidosMasApostados();

        assertEquals(200, res.getStatusCode().value());
        assertTrue(res.getBody().isEmpty());
    }

    /**
     * Verifica obtención de ranking de pollas.
     */
    @Test
    void obtenerPollaRanking_retornaOkConLista() {
        PollaRankingDTO dto = new PollaRankingDTO(1L, "Polla Mundial", "ACTIVA", 15);
        when(reportesService.obtenerPollaRanking()).thenReturn(List.of(dto));

        ResponseEntity<List<PollaRankingDTO>> res = controller.obtenerPollaRanking();

        assertEquals(200, res.getStatusCode().value());
        assertEquals(1, res.getBody().size());
        assertEquals("Polla Mundial", res.getBody().get(0).getNombre());
        verify(reportesService).obtenerPollaRanking();
    }

    /**
     * Verifica ranking vacío.
     */
    @Test
    void obtenerPollaRanking_listaVacia_retornaOkVacio() {
        when(reportesService.obtenerPollaRanking()).thenReturn(List.of());

        ResponseEntity<List<PollaRankingDTO>> res = controller.obtenerPollaRanking();

        assertEquals(200, res.getStatusCode().value());
        assertTrue(res.getBody().isEmpty());
    }

    /**
     * Verifica ingresos por método de pago.
     */
    @Test
    void obtenerIngresosPorMetodoPago_retornaOkConLista() {
        IngresoMetodoPagoDTO dto = new IngresoMetodoPagoDTO("TARJETA", 40, 4000000.0);
        when(reportesService.obtenerIngresosPorMetodoPago()).thenReturn(List.of(dto));

        ResponseEntity<List<IngresoMetodoPagoDTO>> res = controller.obtenerIngresosPorMetodoPago();

        assertEquals(200, res.getStatusCode().value());
        assertEquals(1, res.getBody().size());
        assertEquals("TARJETA", res.getBody().get(0).getTipo());
        verify(reportesService).obtenerIngresosPorMetodoPago();
    }

    /**
     * Verifica ingresos por método de pago vacíos.
     */
    @Test
    void obtenerIngresosPorMetodoPago_listaVacia_retornaOkVacio() {
        when(reportesService.obtenerIngresosPorMetodoPago()).thenReturn(List.of());

        ResponseEntity<List<IngresoMetodoPagoDTO>> res = controller.obtenerIngresosPorMetodoPago();

        assertEquals(200, res.getStatusCode().value());
        assertTrue(res.getBody().isEmpty());
    }

    /**
     * Verifica entradas vendidas por partido.
     */
    @Test
    void obtenerEntradasPorPartido_retornaOkConLista() {
        EntradaPorPartidoDTO dto = new EntradaPorPartidoDTO(1L, "Colombia", "Brazil", "Grupo A", "MetLife Stadium", 300, 9000000.0);
        when(reportesService.obtenerEntradasPorPartido()).thenReturn(List.of(dto));

        ResponseEntity<List<EntradaPorPartidoDTO>> res = controller.obtenerEntradasPorPartido();

        assertEquals(200, res.getStatusCode().value());
        assertEquals(1, res.getBody().size());
        assertEquals("MetLife Stadium", res.getBody().get(0).getEstadio());
        verify(reportesService).obtenerEntradasPorPartido();
    }

    /**
     * Verifica lista vacía de entradas por partido.
     */
    @Test
    void obtenerEntradasPorPartido_listaVacia_retornaOkVacio() {
        when(reportesService.obtenerEntradasPorPartido()).thenReturn(List.of());

        ResponseEntity<List<EntradaPorPartidoDTO>> res = controller.obtenerEntradasPorPartido();

        assertEquals(200, res.getStatusCode().value());
        assertTrue(res.getBody().isEmpty());
    }

    /**
     * Verifica top usuarios de souvenir con tamaño por defecto.
     */
    @Test
    void obtenerTopUsuariosSouvenir_sizeDefault_retornaOkConLista() {
        TopUsuarioSouvenirDTO dto = new TopUsuarioSouvenirDTO(1L, "Juan", "Perez", "juan@test.com", 5, 250000.0);
        when(reportesService.obtenerTopUsuariosSouvenir(any(Pageable.class))).thenReturn(List.of(dto));

        ResponseEntity<List<TopUsuarioSouvenirDTO>> res = controller.obtenerTopUsuariosSouvenir(5);

        assertEquals(200, res.getStatusCode().value());
        assertEquals(1, res.getBody().size());
        assertEquals("Juan", res.getBody().get(0).getNombre());
        verify(reportesService).obtenerTopUsuariosSouvenir(any(Pageable.class));
    }

    /**
     * Verifica pageable personalizado en top usuarios souvenir.
     */
    @Test
    void obtenerTopUsuariosSouvenir_sizePersonalizado_usaPageableCorrecto() {
        when(reportesService.obtenerTopUsuariosSouvenir(any(Pageable.class))).thenReturn(List.of());

        ResponseEntity<List<TopUsuarioSouvenirDTO>> res = controller.obtenerTopUsuariosSouvenir(10);

        assertEquals(200, res.getStatusCode().value());
        verify(reportesService).obtenerTopUsuariosSouvenir(argThat(p -> p.getPageSize() == 10 && p.getPageNumber() == 0));
    }

    /**
     * Verifica top usuarios souvenir vacío.
     */
    @Test
    void obtenerTopUsuariosSouvenir_listaVacia_retornaOkVacio() {
        when(reportesService.obtenerTopUsuariosSouvenir(any(Pageable.class))).thenReturn(List.of());

        ResponseEntity<List<TopUsuarioSouvenirDTO>> res = controller.obtenerTopUsuariosSouvenir(5);

        assertEquals(200, res.getStatusCode().value());
        assertTrue(res.getBody().isEmpty());
    }

    /**
     * Verifica top usuarios de entradas con tamaño por defecto.
     */
    @Test
    void obtenerTopUsuariosEntrada_sizeDefault_retornaOkConLista() {
        TopUsuarioEntradaDTO dto = new TopUsuarioEntradaDTO(2L, "Maria", "Lopez", "maria@test.com", 8, 640000.0);
        when(reportesService.obtenerTopUsuariosEntrada(any(Pageable.class))).thenReturn(List.of(dto));

        ResponseEntity<List<TopUsuarioEntradaDTO>> res = controller.obtenerTopUsuariosEntrada(5);

        assertEquals(200, res.getStatusCode().value());
        assertEquals(1, res.getBody().size());
        assertEquals("Maria", res.getBody().get(0).getNombre());
        verify(reportesService).obtenerTopUsuariosEntrada(any(Pageable.class));
    }

    /**
     * Verifica pageable personalizado en top usuarios entrada.
     */
    @Test
    void obtenerTopUsuariosEntrada_sizePersonalizado_usaPageableCorrecto() {
        when(reportesService.obtenerTopUsuariosEntrada(any(Pageable.class))).thenReturn(List.of());

        ResponseEntity<List<TopUsuarioEntradaDTO>> res = controller.obtenerTopUsuariosEntrada(3);

        assertEquals(200, res.getStatusCode().value());
        verify(reportesService).obtenerTopUsuariosEntrada(argThat(p -> p.getPageSize() == 3 && p.getPageNumber() == 0));
    }

    /**
     * Verifica top usuarios entrada vacío.
     */
    @Test
    void obtenerTopUsuariosEntrada_listaVacia_retornaOkVacio() {
        when(reportesService.obtenerTopUsuariosEntrada(any(Pageable.class))).thenReturn(List.of());

        ResponseEntity<List<TopUsuarioEntradaDTO>> res = controller.obtenerTopUsuariosEntrada(5);

        assertEquals(200, res.getStatusCode().value());
        assertTrue(res.getBody().isEmpty());
    }
}