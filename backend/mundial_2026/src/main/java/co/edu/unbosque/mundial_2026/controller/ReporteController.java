package co.edu.unbosque.mundial_2026.controller;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import co.edu.unbosque.mundial_2026.dto.response.EntradaPorPartidoDTO;
import co.edu.unbosque.mundial_2026.dto.response.IngresoMetodoPagoDTO;
import co.edu.unbosque.mundial_2026.dto.response.PartidoMasApostadoDTO;
import co.edu.unbosque.mundial_2026.dto.response.PollaRankingDTO;
import co.edu.unbosque.mundial_2026.dto.response.ReportesComprasDTO;
import co.edu.unbosque.mundial_2026.dto.response.ReportesResponseDTO;
import co.edu.unbosque.mundial_2026.dto.response.TopUsuarioEntradaDTO;
import co.edu.unbosque.mundial_2026.dto.response.TopUsuarioSouvenirDTO;
import co.edu.unbosque.mundial_2026.service.ReporteService;

/**
 * Controlador REST que expone los endpoints del módulo de reportes —
 * todos los endpoints son exclusivos para administradores y proporcionan
 * métricas y estadísticas del sistema para el panel administrativo
 *
 * <p>Base URL: {@code /api/reportes}</p>
 */
@RestController
@RequestMapping("/api/reportes")
public class ReporteController {

    private final ReporteService reportesService;
    private static final String ROL_ADMIN = "hasRole('ADMIN')";

    public ReporteController(ReporteService reportesService) {
        this.reportesService = reportesService;
    }

    /**
     * {@code GET /api/reportes/estadisticas-generales} — Retorna un resumen global
     * del sistema con métricas como total de usuarios, entradas vendidas,
     * ingresos y apuestas activas — solo ADMIN
     *
     * @return estadísticas generales del sistema
     */
    @PreAuthorize(ROL_ADMIN)
    @GetMapping("/estadisticas-generales")
    public ResponseEntity<ReportesResponseDTO> obtenerEstadisticasGenerales() {
        return ResponseEntity.ok(reportesService.obtenerEstadisticasGenerales());
    }

    /**
     * {@code GET /api/reportes/compras} — Retorna un reporte consolidado de compras
     * incluyendo ventas de souvenirs, entradas y totales por período — solo ADMIN
     *
     * @return reporte completo de compras
     */
    @PreAuthorize(ROL_ADMIN)
    @GetMapping("/compras")
    public ResponseEntity<ReportesComprasDTO> obtenerReportesCompras() {
        return ResponseEntity.ok(reportesService.obtenerReportesCompras());
    }

    /**
     * {@code GET /api/reportes/partidos-apostados} — Retorna los partidos con mayor
     * cantidad de apuestas registradas, ordenados de mayor a menor — solo ADMIN
     *
     * @return lista de partidos con su conteo de apuestas
     */
    @PreAuthorize(ROL_ADMIN)
    @GetMapping("/partidos-apostados")
    public ResponseEntity<List<PartidoMasApostadoDTO>> obtenerPartidosMasApostados() {
        return ResponseEntity.ok(reportesService.obtenerPartidosMasApostados());
    }

    /**
     * {@code GET /api/reportes/pollas} — Retorna el ranking general de la polla
     * con los usuarios ordenados por puntos obtenidos en sus pronósticos — solo ADMIN
     *
     * @return ranking de usuarios en la polla
     */
    @PreAuthorize(ROL_ADMIN)
    @GetMapping("/pollas")
    public ResponseEntity<List<PollaRankingDTO>> obtenerPollaRanking() {
        return ResponseEntity.ok(reportesService.obtenerPollaRanking());
    }

    /**
     * {@code GET /api/reportes/metodos-pago} — Retorna los ingresos totales agrupados
     * por método de pago para identificar cuáles son los más utilizados — solo ADMIN
     *
     * @return lista de métodos de pago con sus ingresos acumulados
     */
    @PreAuthorize(ROL_ADMIN)
    @GetMapping("/metodos-pago")
    public ResponseEntity<List<IngresoMetodoPagoDTO>> obtenerIngresosPorMetodoPago() {
        return ResponseEntity.ok(reportesService.obtenerIngresosPorMetodoPago());
    }

    /**
     * {@code GET /api/reportes/entradas-por-partido} — Retorna la cantidad de entradas
     * vendidas agrupadas por partido — solo ADMIN
     *
     * @return lista de partidos con su conteo de entradas vendidas
     */
    @PreAuthorize(ROL_ADMIN)
    @GetMapping("/entradas-por-partido")
    public ResponseEntity<List<EntradaPorPartidoDTO>> obtenerEntradasPorPartido() {
        return ResponseEntity.ok(reportesService.obtenerEntradasPorPartido());
    }

    /**
     * {@code GET /api/reportes/top-souvenir} — Retorna los usuarios que más souvenirs
     * han comprado en la tienda — el parámetro {@code size} limita cuántos se muestran — solo ADMIN
     *
     * @param size cantidad de usuarios a retornar (por defecto 5)
     * @return lista de usuarios con su total de souvenirs comprados
     */
    @PreAuthorize(ROL_ADMIN)
    @GetMapping("/top-souvenir")
    public ResponseEntity<List<TopUsuarioSouvenirDTO>> obtenerTopUsuariosSouvenir(
            @RequestParam(defaultValue = "5") int size) {
        return ResponseEntity.ok(reportesService.obtenerTopUsuariosSouvenir(PageRequest.of(0, size)));
    }

    /**
     * {@code GET /api/reportes/top-entradas} — Retorna los usuarios que más entradas
     * han adquirido en el sistema — el parámetro {@code size} limita cuántos se muestran — solo ADMIN
     *
     * @param size cantidad de usuarios a retornar (por defecto 5)
     * @return lista de usuarios con su total de entradas compradas
     */
    @PreAuthorize(ROL_ADMIN)
    @GetMapping("/top-entradas")
    public ResponseEntity<List<TopUsuarioEntradaDTO>> obtenerTopUsuariosEntrada(
            @RequestParam(defaultValue = "5") int size) {
        return ResponseEntity.ok(reportesService.obtenerTopUsuariosEntrada(PageRequest.of(0, size)));
    }
}