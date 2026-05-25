package co.edu.unbosque.mundial_2026.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
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

/**
 * Pruebas unitarias para ReporteServiceImpl.
 */
@ExtendWith(MockitoExtension.class)
class ReporteServiceImplTest {

    @Mock private UsuarioRepository usuarioRepository;
    @Mock private PartidoRepository partidoRepository;
    @Mock private OrdenRepository ordenRepository;
    @Mock private ItemOrdenRepository itemOrdenRepository;
    @Mock private EntradaRepository entradaRepository;
    @Mock private PronosticoRepository pronosticoRepository;
    @Mock private ParticipacionRepository participacionRepository;
private static final String COLOMBIA = "Colombia";
private static final String BRAZIL = "Brazil";
    @InjectMocks private ReporteServiceImpl service;

    /**
     * Crea un usuario de prueba.
     *
     * @param id identificador del usuario
     * @return usuario de prueba creado
     */
    private Usuario crearUsuario(Long id) {
        Usuario u = new Usuario();
        u.setId(id);
        u.setCorreoUsuario("user" + id + "@test.com");
        u.setActivo(true);
        return u;
    }

    /**
     * Crea una lista de filas simuladas.
     *
     * @param valores valores de la fila
     * @return lista con la fila creada
     */
    private List<Object[]> filas(Object... valores) {
        List<Object[]> lista = new ArrayList<>();
        lista.add(valores);
        return lista;
    }

    /**
     * Verifica que se retornen estadísticas generales correctas.
     */
    @Test
    void obtenerEstadisticasGenerales_retornaValoresCorrectos() {
        when(usuarioRepository.count()).thenReturn(10L);
        when(partidoRepository.count()).thenReturn(5L);
        when(ordenRepository.count()).thenReturn(20L);
        when(usuarioRepository.findByActivoTrue()).thenReturn(List.of(crearUsuario(1L), crearUsuario(2L)));

        ReportesResponseDTO resultado = service.obtenerEstadisticasGenerales();

        assertNotNull(resultado);
        assertEquals(10, resultado.getTotalUsuarios());
        assertEquals(5, resultado.getTotalPartidos());
        assertEquals(20, resultado.getTotalTransacciones());
        assertEquals(2, resultado.getUsuariosActivos());
    }

    /**
     * Verifica que se retornen ceros cuando no hay usuarios.
     */
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

    /**
     * Verifica que se retornen reportes de compras correctamente.
     */
    @Test
    void obtenerReportesCompras_conDatos_retornaDTO() {
        List<Object[]> filasProducto = filas(1L, "Camiseta", "Ropa", 50, 500000.0);
        List<Object[]> filasCategoria = filas("Ropa", 80, 800000.0);

        when(ordenRepository.sumIngresoTotal()).thenReturn(1500000.0);
        when(ordenRepository.countByFechaPagoIsNotNull()).thenReturn(30L);
        when(entradaRepository.sumEntradasVendidas()).thenReturn(100L);
        when(itemOrdenRepository.findTopProductosMasVendidos(any(Pageable.class))).thenReturn(filasProducto);
        when(itemOrdenRepository.findVentasPorCategoria()).thenReturn(filasCategoria);

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

    /**
     * Verifica que se use cero cuando el ingreso es null.
     */
    @Test
    void obtenerReportesCompras_ingresoNull_usaCero() {
        when(ordenRepository.sumIngresoTotal()).thenReturn(null);
        when(ordenRepository.countByFechaPagoIsNotNull()).thenReturn(0L);
        when(entradaRepository.sumEntradasVendidas()).thenReturn(null);
        when(itemOrdenRepository.findTopProductosMasVendidos(any(Pageable.class))).thenReturn(new ArrayList<>());
        when(itemOrdenRepository.findVentasPorCategoria()).thenReturn(new ArrayList<>());

        ReportesComprasDTO resultado = service.obtenerReportesCompras();

        assertNotNull(resultado);
        assertEquals(0.0, resultado.getIngresoTotal());
        assertEquals(0L, resultado.getTotalEntradasVendidas());
    }

    /**
     * Verifica que se retornen listas vacías cuando no hay productos.
     */
    @Test
    void obtenerReportesCompras_sinProductos_retornaListasVacias() {
        when(ordenRepository.sumIngresoTotal()).thenReturn(0.0);
        when(ordenRepository.countByFechaPagoIsNotNull()).thenReturn(0L);
        when(entradaRepository.sumEntradasVendidas()).thenReturn(0L);
        when(itemOrdenRepository.findTopProductosMasVendidos(any(Pageable.class))).thenReturn(new ArrayList<>());
        when(itemOrdenRepository.findVentasPorCategoria()).thenReturn(new ArrayList<>());

        ReportesComprasDTO resultado = service.obtenerReportesCompras();

        assertNotNull(resultado);
        assertTrue(resultado.getProductosMasVendidos().isEmpty());
        assertTrue(resultado.getVentasPorCategoria().isEmpty());
    }

    /**
     * Verifica que se retornen partidos más apostados.
     */
    @Test
    void obtenerPartidosMasApostados_conDatos_retornaLista() {
        List<Object[]> filas = filas(1L, COLOMBIA, BRAZIL, "Grupo A", 200);
        when(pronosticoRepository.findPartidosMasApostados(any(Pageable.class))).thenReturn(filas);

        List<PartidoMasApostadoDTO> resultado = service.obtenerPartidosMasApostados();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(COLOMBIA, resultado.get(0).getLocal());
        assertEquals(BRAZIL, resultado.get(0).getVisitante());
        assertEquals(200, resultado.get(0).getTotalPronosticos());
    }

    /**
     * Verifica que se retorne lista vacía cuando no hay partidos apostados.
     */
    @Test
    void obtenerPartidosMasApostados_sinDatos_retornaListaVacia() {
        when(pronosticoRepository.findPartidosMasApostados(any(Pageable.class))).thenReturn(new ArrayList<>());

        List<PartidoMasApostadoDTO> resultado = service.obtenerPartidosMasApostados();

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    /**
     * Verifica que se retorne ranking de pollas.
     */
    @Test
    void obtenerPollaRanking_conDatos_retornaLista() {
        List<Object[]> filas = filas(1L, "Polla Mundial", "ACTIVA", 15);
        when(participacionRepository.findPollaRanking(any(Pageable.class))).thenReturn(filas);

        List<PollaRankingDTO> resultado = service.obtenerPollaRanking();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("Polla Mundial", resultado.get(0).getNombre());
        assertEquals("ACTIVA", resultado.get(0).getEstado());
        assertEquals(15, resultado.get(0).getTotalParticipantes());
    }

    /**
     * Verifica que se retorne lista vacía cuando no hay ranking.
     */
    @Test
    void obtenerPollaRanking_sinDatos_retornaListaVacia() {
        when(participacionRepository.findPollaRanking(any(Pageable.class))).thenReturn(new ArrayList<>());

        List<PollaRankingDTO> resultado = service.obtenerPollaRanking();

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    /**
     * Verifica ingresos por método de pago.
     */
    @Test
    void obtenerIngresosPorMetodoPago_conDatos_retornaLista() {
        List<Object[]> filas = filas("TARJETA", 40, 4000000.0);
        when(ordenRepository.findIngresosPorMetodoPago()).thenReturn(filas);

        List<IngresoMetodoPagoDTO> resultado = service.obtenerIngresosPorMetodoPago();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("TARJETA", resultado.get(0).getTipo());
        assertEquals(40, resultado.get(0).getTotalOrdenes());
        assertEquals(4000000.0, resultado.get(0).getIngresoTotal());
    }

    /**
     * Verifica que se retorne lista vacía cuando no hay ingresos.
     */
    @Test
    void obtenerIngresosPorMetodoPago_sinDatos_retornaListaVacia() {
        when(ordenRepository.findIngresosPorMetodoPago()).thenReturn(new ArrayList<>());

        List<IngresoMetodoPagoDTO> resultado = service.obtenerIngresosPorMetodoPago();

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    /**
     * Verifica entradas vendidas por partido.
     */
    @Test
    void obtenerEntradasPorPartido_conDatos_retornaLista() {
        List<Object[]> filas = filas(1L, COLOMBIA, BRAZIL, "Grupo A", "MetLife Stadium", 300, 9000000.0);
        when(entradaRepository.findEntradasPorPartido()).thenReturn(filas);

        List<EntradaPorPartidoDTO> resultado = service.obtenerEntradasPorPartido();

        assertNotNull(resultado);
        assertEquals(COLOMBIA, resultado.get(0).getLocal());
        assertEquals("MetLife Stadium", resultado.get(0).getEstadio());
        assertEquals(300, resultado.get(0).getCantidadVendida());
        assertEquals(9000000.0, resultado.get(0).getIngresoTotal());
    }

    /**
     * Verifica que se retorne lista vacía cuando no hay entradas.
     */
    @Test
    void obtenerEntradasPorPartido_sinDatos_retornaListaVacia() {
        when(entradaRepository.findEntradasPorPartido()).thenReturn(new ArrayList<>());

        List<EntradaPorPartidoDTO> resultado = service.obtenerEntradasPorPartido();

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    /**
     * Verifica top usuarios en compras de souvenirs.
     */
    @Test
    void obtenerTopUsuariosSouvenir_conDatos_retornaLista() {
        List<Object[]> filas = filas(1L, "Juan", "Perez", "juan@test.com", 5, 250000.0);
        Pageable pageable = PageRequest.of(0, 5);
        when(ordenRepository.findTopUsuariosSouvenir(pageable)).thenReturn(filas);

        List<TopUsuarioSouvenirDTO> resultado = service.obtenerTopUsuariosSouvenir(pageable);

        assertNotNull(resultado);
        assertEquals("Juan", resultado.get(0).getNombre());
        assertEquals("Perez", resultado.get(0).getApellido());
        assertEquals(5, resultado.get(0).getTotalOrdenes());
        assertEquals(250000.0, resultado.get(0).getTotalGastado());
    }

    /**
     * Verifica que se retorne lista vacía cuando no hay usuarios.
     */
    @Test
    void obtenerTopUsuariosSouvenir_sinDatos_retornaListaVacia() {
        Pageable pageable = PageRequest.of(0, 5);
        when(ordenRepository.findTopUsuariosSouvenir(pageable)).thenReturn(new ArrayList<>());

        List<TopUsuarioSouvenirDTO> resultado = service.obtenerTopUsuariosSouvenir(pageable);

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    /**
     * Verifica top usuarios en compra de entradas.
     */
    @Test
    void obtenerTopUsuariosEntrada_conDatos_retornaLista() {
        List<Object[]> filas = filas(2L, "Maria", "Lopez", "maria@test.com", 8, 640000.0);
        Pageable pageable = PageRequest.of(0, 5);
        when(entradaRepository.findTopUsuariosEntrada(pageable)).thenReturn(filas);

        List<TopUsuarioEntradaDTO> resultado = service.obtenerTopUsuariosEntrada(pageable);

        assertNotNull(resultado);
        assertEquals("Maria", resultado.get(0).getNombre());
        assertEquals(8, resultado.get(0).getTotalEntradas());
        assertEquals(640000.0, resultado.get(0).getTotalGastado());
    }

    /**
     * Verifica que se retorne lista vacía cuando no hay usuarios.
     */
    @Test
    void obtenerTopUsuariosEntrada_sinDatos_retornaListaVacia() {
        Pageable pageable = PageRequest.of(0, 5);
        when(entradaRepository.findTopUsuariosEntrada(pageable)).thenReturn(new ArrayList<>());

        List<TopUsuarioEntradaDTO> resultado = service.obtenerTopUsuariosEntrada(pageable);

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }
}