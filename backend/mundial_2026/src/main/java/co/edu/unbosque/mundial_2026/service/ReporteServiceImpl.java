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