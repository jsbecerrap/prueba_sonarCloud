package co.edu.unbosque.mundial_2026.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import co.edu.unbosque.mundial_2026.dto.response.*;
import co.edu.unbosque.mundial_2026.entity.Usuario;
import co.edu.unbosque.mundial_2026.repository.*;
import co.edu.unbosque.mundial_2026.service.ReporteServiceImpl;

@ExtendWith(MockitoExtension.class)
class ReporteServiceImplTest {

    @Mock private UsuarioRepository usuarioRepository;
    @Mock private PartidoRepository partidoRepository;
    @Mock private OrdenRepository ordenRepository;
    @Mock private ItemOrdenRepository itemOrdenRepository;
    @Mock private EntradaRepository entradaRepository;
    @Mock private PronosticoRepository pronosticoRepository;
    @Mock private ParticipacionRepository participacionRepository;

    @InjectMocks private ReporteServiceImpl service;

    private Usuario crearUsuario(Long id) {
        Usuario u = new Usuario();
        u.setId(id);
        u.setCorreoUsuario("user" + id + "@test.com");
        u.setActivo(true);
        return u;
    }

    @Test
    void obtenerEstadisticasGenerales_retornaValoresCorrectos() {
        Usuario u = crearUsuario(1L);
        when(usuarioRepository.count()).thenReturn(10L);
        when(partidoRepository.count()).thenReturn(5L);
        when(ordenRepository.count()).thenReturn(20L);
        when(usuarioRepository.findByActivoTrue()).thenReturn(List.of(u, crearUsuario(2L)));

        ReportesResponseDTO resultado = service.obtenerEstadisticasGenerales();

        assertNotNull(resultado);
        assertEquals(10, resultado.getTotalUsuarios());
        assertEquals(5, resultado.getTotalPartidos());
        assertEquals(20, resultado.getTotalTransacciones());
        assertEquals(2, resultado.getUsuariosActivos());
    }

    @Test
    void obtenerEstadisticasGenerales_sinUsuarios_retornaCeros() {
        when(usuarioRepository.count()).thenReturn(0L);
        when(partidoRepository.count()).thenReturn(0L);
        when(ordenRepository.count()).thenReturn(0L);
        when(usuarioRepository.findByActivoTrue()).thenReturn(List.of());

        ReportesResponseDTO resultado = service.obtenerEstadisticasGenerales();

        assertNotNull(resultado);
        assertEquals(0, resultado.getTotalUsuarios());
        assertEquals(0, resultado.getUsuariosActivos());
    }

    @Test
    void obtenerReportesCompras_conDatos_retornaDTO() {
        Object[] rowProducto = new Object[]{ 1L, "Camiseta", "Ropa", 50, 500000.0 };
        Object[] rowCategoria = new Object[]{ "Ropa", 80, 800000.0 };

        when(ordenRepository.sumIngresoTotal()).thenReturn(1500000.0);
        when(ordenRepository.countByFechaPagoIsNotNull()).thenReturn(30L);
        when(entradaRepository.sumEntradasVendidas()).thenReturn(100L);
        when(itemOrdenRepository.findTopProductosMasVendidos(any(Pageable.class)))
                .thenReturn(List.of(rowProducto));
        when(itemOrdenRepository.findVentasPorCategoria())
                .thenReturn(List.of(rowCategoria));

        ReportesComprasDTO resultado = service.obtenerReportesCompras();

        assertNotNull(resultado);
        assertEquals(1500000.0, resultado.getIngresoTotal());
        assertEquals(30, resultado.getTotalOrdenes());
        assertEquals(100L, resultado.getTotalEntradasVendidas());
        assertEquals(1, resultado.getProductosMasVendidos().size());
        assertEquals("Camiseta", resultado.getProductosMasVendidos().get(0).getNombre());
        assertEquals(1, resultado.getVentasPorCategoria().size());
        assertEquals("Ropa", resultado.getVentasPorCategoria().get(0).getCategoria());
    }

    @Test
    void obtenerReportesCompras_ingresoNull_usaCero() {
        when(ordenRepository.sumIngresoTotal()).thenReturn(null);
        when(ordenRepository.countByFechaPagoIsNotNull()).thenReturn(0L);
        when(entradaRepository.sumEntradasVendidas()).thenReturn(null);
        when(itemOrdenRepository.findTopProductosMasVendidos(any(Pageable.class))).thenReturn(List.of());
        when(itemOrdenRepository.findVentasPorCategoria()).thenReturn(List.of());

        ReportesComprasDTO resultado = service.obtenerReportesCompras();

        assertNotNull(resultado);
        assertEquals(0.0, resultado.getIngresoTotal());
        assertEquals(0L, resultado.getTotalEntradasVendidas());
    }

    @Test
    void obtenerReportesCompras_sinProductos_retornaListasVacias() {
        when(ordenRepository.sumIngresoTotal()).thenReturn(0.0);
        when(ordenRepository.countByFechaPagoIsNotNull()).thenReturn(0L);
        when(entradaRepository.sumEntradasVendidas()).thenReturn(0L);
        when(itemOrdenRepository.findTopProductosMasVendidos(any(Pageable.class))).thenReturn(List.of());
        when(itemOrdenRepository.findVentasPorCategoria()).thenReturn(List.of());

        ReportesComprasDTO resultado = service.obtenerReportesCompras();

        assertNotNull(resultado);
        assertTrue(resultado.getProductosMasVendidos().isEmpty());
        assertTrue(resultado.getVentasPorCategoria().isEmpty());
    }

    @Test
    void obtenerPartidosMasApostados_conDatos_retornaLista() {
        Object[] row = new Object[]{ 1L, "Colombia", "Brazil", "Grupo A", 200 };
        when(pronosticoRepository.findPartidosMasApostados(any(Pageable.class))).thenReturn(List.of(row));

        List<PartidoMasApostadoDTO> resultado = service.obtenerPartidosMasApostados();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("Colombia", resultado.get(0).getLocal());
        assertEquals("Brazil", resultado.get(0).getVisitante());
        assertEquals(200, resultado.get(0).getTotalPronosticos());
    }

    @Test
    void obtenerPartidosMasApostados_sinDatos_retornaListaVacia() {
        when(pronosticoRepository.findPartidosMasApostados(any(Pageable.class))).thenReturn(List.of());

        List<PartidoMasApostadoDTO> resultado = service.obtenerPartidosMasApostados();

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    @Test
    void obtenerPollaRanking_conDatos_retornaLista() {
        Object[] row = new Object[]{ 1L, "Polla Mundial", "ACTIVA", 15 };
        when(participacionRepository.findPollaRanking(any(Pageable.class))).thenReturn(List.of(row));

        List<PollaRankingDTO> resultado = service.obtenerPollaRanking();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("Polla Mundial", resultado.get(0).getNombre());
        assertEquals("ACTIVA", resultado.get(0).getEstado());
        assertEquals(15, resultado.get(0).getTotalParticipantes());
    }

    @Test
    void obtenerPollaRanking_sinDatos_retornaListaVacia() {
        when(participacionRepository.findPollaRanking(any(Pageable.class))).thenReturn(List.of());

        List<PollaRankingDTO> resultado = service.obtenerPollaRanking();

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    @Test
    void obtenerIngresosPorMetodoPago_conDatos_retornaLista() {
        Object[] row = new Object[]{ "TARJETA", 40, 4000000.0 };
        when(ordenRepository.findIngresosPorMetodoPago()).thenReturn(List.of(row));

        List<IngresoMetodoPagoDTO> resultado = service.obtenerIngresosPorMetodoPago();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("TARJETA", resultado.get(0).getTipo());
        assertEquals(40, resultado.get(0).getTotalOrdenes());
        assertEquals(4000000.0, resultado.get(0).getIngresoTotal());
    }

    @Test
    void obtenerIngresosPorMetodoPago_sinDatos_retornaListaVacia() {
        when(ordenRepository.findIngresosPorMetodoPago()).thenReturn(List.of());

        List<IngresoMetodoPagoDTO> resultado = service.obtenerIngresosPorMetodoPago();

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    @Test
    void obtenerEntradasPorPartido_conDatos_retornaLista() {
        Object[] row = new Object[]{ 1L, "Colombia", "Brazil", "Grupo A", "MetLife Stadium", 300, 9000000.0 };
        when(entradaRepository.findEntradasPorPartido()).thenReturn(List.of(row));

        List<EntradaPorPartidoDTO> resultado = service.obtenerEntradasPorPartido();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("Colombia", resultado.get(0).getLocal());
        assertEquals("MetLife Stadium", resultado.get(0).getEstadio());
        assertEquals(300, resultado.get(0).getCantidadVendida());
        assertEquals(9000000.0, resultado.get(0).getIngresoTotal());
    }

    @Test
    void obtenerEntradasPorPartido_sinDatos_retornaListaVacia() {
        when(entradaRepository.findEntradasPorPartido()).thenReturn(List.of());

        List<EntradaPorPartidoDTO> resultado = service.obtenerEntradasPorPartido();

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    @Test
    void obtenerTopUsuariosSouvenir_conDatos_retornaLista() {
        Object[] row = new Object[]{ 1L, "Juan", "Perez", "juan@test.com", 5, 250000.0 };
        Pageable pageable = PageRequest.of(0, 5);
        when(ordenRepository.findTopUsuariosSouvenir(pageable)).thenReturn(List.of(row));

        List<TopUsuarioSouvenirDTO> resultado = service.obtenerTopUsuariosSouvenir(pageable);

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("Juan", resultado.get(0).getNombre());
        assertEquals("Perez", resultado.get(0).getApellido());
        assertEquals(5, resultado.get(0).getTotalOrdenes());
        assertEquals(250000.0, resultado.get(0).getTotalGastado());
    }

    @Test
    void obtenerTopUsuariosSouvenir_sinDatos_retornaListaVacia() {
        Pageable pageable = PageRequest.of(0, 5);
        when(ordenRepository.findTopUsuariosSouvenir(pageable)).thenReturn(List.of());

        List<TopUsuarioSouvenirDTO> resultado = service.obtenerTopUsuariosSouvenir(pageable);

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    @Test
    void obtenerTopUsuariosEntrada_conDatos_retornaLista() {
        Object[] row = new Object[]{ 2L, "Maria", "Lopez", "maria@test.com", 8, 640000.0 };
        Pageable pageable = PageRequest.of(0, 5);
        when(entradaRepository.findTopUsuariosEntrada(pageable)).thenReturn(List.of(row));

        List<TopUsuarioEntradaDTO> resultado = service.obtenerTopUsuariosEntrada(pageable);

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("Maria", resultado.get(0).getNombre());
        assertEquals(8, resultado.get(0).getTotalEntradas());
        assertEquals(640000.0, resultado.get(0).getTotalGastado());
    }

    @Test
    void obtenerTopUsuariosEntrada_sinDatos_retornaListaVacia() {
        Pageable pageable = PageRequest.of(0, 5);
        when(entradaRepository.findTopUsuariosEntrada(pageable)).thenReturn(List.of());

        List<TopUsuarioEntradaDTO> resultado = service.obtenerTopUsuariosEntrada(pageable);

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }
}