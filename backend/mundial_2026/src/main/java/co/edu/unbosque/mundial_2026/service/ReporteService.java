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

/**
 * Contrato del servicio de reportes — centraliza la generación de estadísticas
 * y métricas del sistema para el panel administrativo
 */
public interface ReporteService {

    /**
     * Retorna un resumen general del sistema con métricas globales como
     * total de usuarios, entradas vendidas, ingresos y apuestas activas
     *
     * @return estadísticas generales del sistema
     */
    ReportesResponseDTO obtenerEstadisticasGenerales();

    /**
     * Retorna un reporte consolidado de compras incluyendo ventas de
     * souvenirs, entradas y totales por período
     *
     * @return reporte completo de compras
     */
    ReportesComprasDTO obtenerReportesCompras();

    /**
     * Retorna los partidos que tienen mayor cantidad de apuestas registradas,
     * ordenados de mayor a menor
     *
     * @return lista de partidos con su conteo de apuestas
     */
    List<PartidoMasApostadoDTO> obtenerPartidosMasApostados();

    /**
     * Retorna el ranking general de la polla con los usuarios ordenados
     * por puntos obtenidos en sus pronósticos
     *
     * @return ranking de usuarios en la polla
     */
    List<PollaRankingDTO> obtenerPollaRanking();

    /**
     * Retorna los ingresos totales agrupados por método de pago
     * para identificar cuáles son los más utilizados
     *
     * @return lista de métodos de pago con sus ingresos acumulados
     */
    List<IngresoMetodoPagoDTO> obtenerIngresosPorMetodoPago();

    /**
     * Retorna la cantidad de entradas vendidas agrupadas por partido
     *
     * @return lista de partidos con su conteo de entradas vendidas
     */
    List<EntradaPorPartidoDTO> obtenerEntradasPorPartido();

    /**
     * Retorna los usuarios que más souvenirs han comprado en la tienda
     *
     * @param pageable configuración de paginación para limitar el top
     * @return lista de usuarios con su total de souvenirs comprados
     */
    List<TopUsuarioSouvenirDTO> obtenerTopUsuariosSouvenir(Pageable pageable);

    /**
     * Retorna los usuarios que más entradas han adquirido en el sistema
     *
     * @param pageable configuración de paginación para limitar el top
     * @return lista de usuarios con su total de entradas compradas
     */
    List<TopUsuarioEntradaDTO> obtenerTopUsuariosEntrada(Pageable pageable);
}