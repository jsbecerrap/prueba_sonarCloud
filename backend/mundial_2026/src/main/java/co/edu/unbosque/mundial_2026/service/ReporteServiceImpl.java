package co.edu.unbosque.mundial_2026.service;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.unbosque.mundial_2026.dto.response.EntradaPorPartidoDTO;
import co.edu.unbosque.mundial_2026.dto.response.IngresoMetodoPagoDTO;
import co.edu.unbosque.mundial_2026.dto.response.PartidoMasApostadoDTO;
import co.edu.unbosque.mundial_2026.dto.response.PollaRankingDTO;
import co.edu.unbosque.mundial_2026.dto.response.ProductoMasVendidoDTO;
import co.edu.unbosque.mundial_2026.dto.response.ReportesComprasDTO;
import co.edu.unbosque.mundial_2026.dto.response.ReportesResponseDTO;
import co.edu.unbosque.mundial_2026.dto.response.TopUsuarioEntradaDTO;
import co.edu.unbosque.mundial_2026.dto.response.TopUsuarioSouvenirDTO;
import co.edu.unbosque.mundial_2026.dto.response.VentasPorCategoriaDTO;
import co.edu.unbosque.mundial_2026.repository.EntradaRepository;
import co.edu.unbosque.mundial_2026.repository.ItemOrdenRepository;
import co.edu.unbosque.mundial_2026.repository.OrdenRepository;
import co.edu.unbosque.mundial_2026.repository.ParticipacionRepository;
import co.edu.unbosque.mundial_2026.repository.PartidoRepository;
import co.edu.unbosque.mundial_2026.repository.PronosticoRepository;
import co.edu.unbosque.mundial_2026.repository.UsuarioRepository;

/**
 * Implementación del servicio encargado de generar los reportes y estadísticas
 * administrativas de la plataforma del Mundial 2026.
 * Consolida información de múltiples módulos: usuarios, partidos, entradas, pollas
 * y tienda, para ofrecer una visión general del estado del sistema y métricas
 * clave como ingresos, productos más vendidos, partidos más apostados y top usuarios
 */
@Service
public class ReporteServiceImpl implements ReporteService {

    private final UsuarioRepository usuarioRepository;
    private final PartidoRepository partidoRepository;
    private final OrdenRepository ordenRepository;
    private final ItemOrdenRepository itemOrdenRepository;
    private final EntradaRepository entradaRepository;
    private final PronosticoRepository pronosticoRepository;
    private final ParticipacionRepository participacionRepository;

    public ReporteServiceImpl(UsuarioRepository usuarioRepository,
            PartidoRepository partidoRepository,
            OrdenRepository ordenRepository,
            ItemOrdenRepository itemOrdenRepository,
            EntradaRepository entradaRepository,
            PronosticoRepository pronosticoRepository,
            ParticipacionRepository participacionRepository) {
        this.usuarioRepository = usuarioRepository;
        this.partidoRepository = partidoRepository;
        this.ordenRepository = ordenRepository;
        this.itemOrdenRepository = itemOrdenRepository;
        this.entradaRepository = entradaRepository;
        this.pronosticoRepository = pronosticoRepository;
        this.participacionRepository = participacionRepository;
    }

    /**
     * Retorna las estadísticas generales de la plataforma: total de usuarios registrados,
     * usuarios activos, total de partidos y total de transacciones realizadas
     *
     * @return {@link ReportesResponseDTO} con los conteos generales del sistema
     */
    @Override
    @Transactional(readOnly = true)
    public ReportesResponseDTO obtenerEstadisticasGenerales() {
        ReportesResponseDTO dto = new ReportesResponseDTO();
        dto.setTotalUsuarios((int) usuarioRepository.count());
        dto.setTotalPartidos((int) partidoRepository.count());
        dto.setTotalTransacciones((int) ordenRepository.count());
        dto.setUsuariosActivos(usuarioRepository.findByActivoTrue().size());
        return dto;
    }

    /**
     * Retorna el reporte de compras de la tienda incluyendo: ingreso total,
     * cantidad de órdenes pagadas, total de entradas vendidas, los 5 productos
     * más vendidos y las ventas agrupadas por categoría
     *
     * @return {@link ReportesComprasDTO} con las métricas de ventas de la tienda
     */
    @Override
    @Transactional(readOnly = true)
    public ReportesComprasDTO obtenerReportesCompras() {
        ReportesComprasDTO dto = new ReportesComprasDTO();

        Double ingreso = ordenRepository.sumIngresoTotal();
        dto.setIngresoTotal(ingreso != null ? ingreso : 0.0);
        dto.setTotalOrdenes((int) ordenRepository.countByFechaPagoIsNotNull());

        Long entradas = entradaRepository.sumEntradasVendidas();
        dto.setTotalEntradasVendidas(entradas != null ? entradas : 0L);

        List<ProductoMasVendidoDTO> productos = itemOrdenRepository
                .findTopProductosMasVendidos(PageRequest.of(0, 5))
                .stream()
                .map(row -> new ProductoMasVendidoDTO(
                        (Long) row[0],
                        (String) row[1],
                        (String) row[2],
                        ((Number) row[3]).intValue(),
                        ((Number) row[4]).doubleValue()))
                .toList();
        dto.setProductosMasVendidos(productos);

        List<VentasPorCategoriaDTO> porCategoria = itemOrdenRepository
                .findVentasPorCategoria()
                .stream()
                .map(row -> new VentasPorCategoriaDTO(
                        (String) row[0],
                        ((Number) row[1]).intValue(),
                        ((Number) row[2]).doubleValue()))
                .toList();
        dto.setVentasPorCategoria(porCategoria);

        return dto;
    }

    /**
     * Retorna los 8 partidos con más pronósticos registrados en las pollas,
     * ordenados de mayor a menor cantidad de apuestas
     *
     * @return lista de {@link PartidoMasApostadoDTO} con los partidos más apostados
     */
    @Override
    @Transactional(readOnly = true)
    public List<PartidoMasApostadoDTO> obtenerPartidosMasApostados() {
        return pronosticoRepository
                .findPartidosMasApostados(PageRequest.of(0, 8))
                .stream()
                .map(row -> new PartidoMasApostadoDTO(
                        (Long) row[0],
                        (String) row[1],
                        (String) row[2],
                        (String) row[3],
                        ((Number) row[4]).intValue()))
                .toList();
    }

    /**
     * Retorna el ranking de las 8 pollas con mayor cantidad de participantes,
     * incluyendo el nombre de la polla y quién la creó
     *
     * @return lista de {@link PollaRankingDTO} con las pollas más populares
     */
    @Override
    @Transactional(readOnly = true)
    public List<PollaRankingDTO> obtenerPollaRanking() {
        return participacionRepository
                .findPollaRanking(PageRequest.of(0, 8))
                .stream()
                .map(row -> new PollaRankingDTO(
                        (Long) row[0],
                        (String) row[1],
                        (String) row[2],
                        ((Number) row[3]).intValue()))
                .toList();
    }

    /**
     * Retorna el ingreso total agrupado por método de pago,
     * mostrando cuántas órdenes se pagaron con cada tipo y el monto acumulado
     *
     * @return lista de {@link IngresoMetodoPagoDTO} con los ingresos por método de pago
     */
    @Override
    @Transactional(readOnly = true)
    public List<IngresoMetodoPagoDTO> obtenerIngresosPorMetodoPago() {
        return ordenRepository
                .findIngresosPorMetodoPago()
                .stream()
                .map(row -> new IngresoMetodoPagoDTO(
                        (String) row[0],
                        ((Number) row[1]).intValue(),
                        ((Number) row[2]).doubleValue()))
                .toList();
    }

    /**
     * Retorna el total de entradas vendidas agrupadas por partido,
     * incluyendo nombre del partido, estadio, ronda, cantidad y valor total recaudado
     *
     * @return lista de {@link EntradaPorPartidoDTO} con las entradas vendidas por partido
     */
    @Override
    @Transactional(readOnly = true)
    public List<EntradaPorPartidoDTO> obtenerEntradasPorPartido() {
        return entradaRepository
                .findEntradasPorPartido()
                .stream()
                .map(row -> new EntradaPorPartidoDTO(
                        (Long) row[0],
                        (String) row[1],
                        (String) row[2],
                        (String) row[3],
                        (String) row[4],
                        ((Number) row[5]).intValue(),
                        ((Number) row[6]).doubleValue()))
                .toList();
    }

    /**
     * Retorna los usuarios que más han comprado en la tienda de souvenirs,
     * ordenados por total gastado de forma descendente y paginados
     *
     * @param pageable configuración de paginación y ordenamiento
     * @return lista de {@link TopUsuarioSouvenirDTO} con los mayores compradores de tienda
     */
    @Override
    @Transactional(readOnly = true)
    public List<TopUsuarioSouvenirDTO> obtenerTopUsuariosSouvenir(Pageable pageable) {
        return ordenRepository
                .findTopUsuariosSouvenir(pageable)
                .stream()
                .map(row -> new TopUsuarioSouvenirDTO(
                        (Long) row[0],
                        (String) row[1],
                        (String) row[2],
                        (String) row[3],
                        ((Number) row[4]).intValue(),
                        ((Number) row[5]).doubleValue()))
                .toList();
    }

    /**
     * Retorna los usuarios que más entradas han comprado para los partidos,
     * ordenados por cantidad de entradas de forma descendente y paginados
     *
     * @param pageable configuración de paginación y ordenamiento
     * @return lista de {@link TopUsuarioEntradaDTO} con los mayores compradores de entradas
     */
    @Override
    @Transactional(readOnly = true)
    public List<TopUsuarioEntradaDTO> obtenerTopUsuariosEntrada(Pageable pageable) {
        return entradaRepository
                .findTopUsuariosEntrada(pageable)
                .stream()
                .map(row -> new TopUsuarioEntradaDTO(
                        (Long) row[0],
                        (String) row[1],
                        (String) row[2],
                        (String) row[3],
                        ((Number) row[4]).intValue(),
                        ((Number) row[5]).doubleValue()))
                .toList();
    }
}