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

@RestController
@RequestMapping("/api/reportes")
public class ReporteController {

    private final ReporteService reportesService;

   

    public ReporteController(ReporteService reportesService) {
        this.reportesService = reportesService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/estadisticas-generales")
    public ResponseEntity<ReportesResponseDTO> obtenerEstadisticasGenerales() {
        return ResponseEntity.ok(reportesService.obtenerEstadisticasGenerales());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/compras")
    public ResponseEntity<ReportesComprasDTO> obtenerReportesCompras() {
        return ResponseEntity.ok(reportesService.obtenerReportesCompras());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/partidos-apostados")
    public ResponseEntity<List<PartidoMasApostadoDTO>> obtenerPartidosMasApostados() {
        return ResponseEntity.ok(reportesService.obtenerPartidosMasApostados());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/pollas")
    public ResponseEntity<List<PollaRankingDTO>> obtenerPollaRanking() {
        return ResponseEntity.ok(reportesService.obtenerPollaRanking());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/metodos-pago")
    public ResponseEntity<List<IngresoMetodoPagoDTO>> obtenerIngresosPorMetodoPago() {
        return ResponseEntity.ok(reportesService.obtenerIngresosPorMetodoPago());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/entradas-por-partido")
    public ResponseEntity<List<EntradaPorPartidoDTO>> obtenerEntradasPorPartido() {
        return ResponseEntity.ok(reportesService.obtenerEntradasPorPartido());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/top-souvenir")
    public ResponseEntity<List<TopUsuarioSouvenirDTO>> obtenerTopUsuariosSouvenir(
            @RequestParam(defaultValue = "5") int size) {
        return ResponseEntity.ok(reportesService.obtenerTopUsuariosSouvenir(PageRequest.of(0, size)));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/top-entradas")
    public ResponseEntity<List<TopUsuarioEntradaDTO>> obtenerTopUsuariosEntrada(
            @RequestParam(defaultValue = "5") int size) {
        return ResponseEntity.ok(reportesService.obtenerTopUsuariosEntrada(PageRequest.of(0, size)));
    }
}