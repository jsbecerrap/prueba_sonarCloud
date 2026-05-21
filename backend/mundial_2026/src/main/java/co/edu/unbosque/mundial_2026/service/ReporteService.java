package co.edu.unbosque.mundial_2026.service;

import java.util.List;

import org.springframework.data.domain.Pageable;

import co.edu.unbosque.mundial_2026.dto.response.EntradaPorPartidoDTO;
import co.edu.unbosque.mundial_2026.dto.response.IngresoMetodoPagoDTO;
import co.edu.unbosque.mundial_2026.dto.response.PartidoMasApostadoDTO;
import co.edu.unbosque.mundial_2026.dto.response.PollaRankingDTO;
import co.edu.unbosque.mundial_2026.dto.response.ReportesComprasDTO;
import co.edu.unbosque.mundial_2026.dto.response.ReportesResponseDTO;
import co.edu.unbosque.mundial_2026.dto.response.TopUsuarioEntradaDTO;
import co.edu.unbosque.mundial_2026.dto.response.TopUsuarioSouvenirDTO;

public interface ReporteService {

    ReportesResponseDTO obtenerEstadisticasGenerales();

    ReportesComprasDTO obtenerReportesCompras();

    List<PartidoMasApostadoDTO> obtenerPartidosMasApostados();

    List<PollaRankingDTO> obtenerPollaRanking();

    List<IngresoMetodoPagoDTO> obtenerIngresosPorMetodoPago();

    List<EntradaPorPartidoDTO> obtenerEntradasPorPartido();

    List<TopUsuarioSouvenirDTO> obtenerTopUsuariosSouvenir(Pageable pageable);

    List<TopUsuarioEntradaDTO> obtenerTopUsuariosEntrada(Pageable pageable);
}