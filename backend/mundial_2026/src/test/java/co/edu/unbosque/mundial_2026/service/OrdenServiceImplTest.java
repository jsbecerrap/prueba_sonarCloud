package co.edu.unbosque.mundial_2026.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import co.edu.unbosque.mundial_2026.dto.request.AgregarItemDTO;
import co.edu.unbosque.mundial_2026.dto.request.ConfirmarOrdenDTO;
import co.edu.unbosque.mundial_2026.dto.response.OrdenHistorialDTO;
import co.edu.unbosque.mundial_2026.dto.response.OrdenResponseDTO;
import co.edu.unbosque.mundial_2026.entity.*;
import co.edu.unbosque.mundial_2026.exception.*;
import co.edu.unbosque.mundial_2026.repository.*;
import co.edu.unbosque.mundial_2026.service.*;

@ExtendWith(MockitoExtension.class)
class OrdenServiceImplTest {

    @Mock private OrdenRepository ordenRepository;
    @Mock private ItemOrdenRepository itemOrdenRepository;
    @Mock private VarianteProductoRepository varianteRepository;
    @Mock private UsuarioService usuarioService;
    @Mock private ProductoService productoService;
    @Mock private MetodoPagoService metodoPagoService;
    @Mock private EventoAuditoriaService auditoriaService;
    @Mock private NotificacionService notificacionService;

    private OrdenServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new OrdenServiceImpl(
                ordenRepository, itemOrdenRepository, varianteRepository,
                usuarioService, productoService, metodoPagoService,
                auditoriaService, notificacionService, "sk_test_dummy");
    }

    private Usuario crearUsuario(Long id) {
        Usuario u = new Usuario();
        u.setId(id);
        u.setNombre("Juan");
        u.setApellido("Perez");
        u.setCorreoUsuario("user" + id + "@test.com");
        Rol rol = new Rol();
        rol.setNombre("ROLE_USUARIO");
        u.setRol(rol);
        return u;
    }

    private Categoria crearCategoria() {
        Categoria c = new Categoria();
        c.setId(1L);
        c.setNombre("Souvenirs");
        return c;
    }

    private Producto crearProducto(Long id, boolean activo) {
        Producto p = new Producto();
        p.setId(id);
        p.setNombre("Camiseta");
        p.setPrecio(50.0);
        p.setActivo(activo);
        p.setImagenUrl("img.jpg");
        p.setCategoria(crearCategoria());
        return p;
    }

    private VarianteProducto crearVariante(Long id, Producto producto, int stock) {
        VarianteProducto v = new VarianteProducto();
        v.setId(id);
        v.setProducto(producto);
        v.setStock(stock);
        v.setEspecificacion("M");
        return v;
    }

    private Orden crearOrden(Long id, String estado, Usuario usuario) {
        Orden o = new Orden();
        o.setId(id);
        o.setEstado(estado);
        o.setTotal(0.0);
        o.setFechaCreacion(LocalDateTime.now());
        o.setUsuario(usuario);
        return o;
    }

    private ItemOrden crearItem(Long id, Orden orden, Producto producto, VarianteProducto variante, int cantidad) {
        ItemOrden item = new ItemOrden();
        item.setId(id);
        item.setOrden(orden);
        item.setProducto(producto);
        item.setVariante(variante);
        item.setCantidad(cantidad);
        item.setPrecioUnitario(producto.getPrecio());
        return item;
    }

    private AgregarItemDTO crearAgregarItemDTO(Long productoId, Long varianteId, int cantidad) {
        AgregarItemDTO dto = new AgregarItemDTO();
        dto.setProductoId(productoId);
        dto.setVarianteId(varianteId);
        dto.setCantidad(cantidad);
        return dto;
    }

    @Test
    void agregarItem_carritoNuevo_itemNuevo_creaOrdenYAgregaItem() {
        Usuario usuario = crearUsuario(1L);
        Producto producto = crearProducto(1L, true);
        VarianteProducto variante = crearVariante(1L, producto, 10);
        Orden ordenNueva = crearOrden(1L, "PENDIENTE", usuario);
        ItemOrden item = crearItem(1L, ordenNueva, producto, variante, 2);

        when(usuarioService.obtenerEntidadPorCorreo("user1@test.com")).thenReturn(usuario);
        when(varianteRepository.findByIdWithProductoYCategoria(1L)).thenReturn(Optional.of(variante));
        when(ordenRepository.findByUsuarioIdAndEstado(1L, "PENDIENTE")).thenReturn(Optional.empty());
        when(ordenRepository.save(any(Orden.class))).thenReturn(ordenNueva);
        when(itemOrdenRepository.findByOrdenIdAndProductoIdAndVarianteId(1L, 1L, 1L)).thenReturn(Optional.empty());
        when(itemOrdenRepository.save(any(ItemOrden.class))).thenReturn(item);
        when(itemOrdenRepository.findByOrdenIdConDetalles(1L)).thenReturn(List.of(item));
        doNothing().when(auditoriaService).registrar(any(), any(), any(), any(), any());

        OrdenResponseDTO resultado = service.agregarItem("user1@test.com", crearAgregarItemDTO(1L, 1L, 2));

        assertNotNull(resultado);
        verify(itemOrdenRepository).save(any(ItemOrden.class));
    }

    @Test
    void agregarItem_carritoExistente_itemExistente_actualizaCantidad() {
        Usuario usuario = crearUsuario(1L);
        Producto producto = crearProducto(1L, true);
        VarianteProducto variante = crearVariante(1L, producto, 10);
        Orden orden = crearOrden(1L, "PENDIENTE", usuario);
        ItemOrden itemExistente = crearItem(1L, orden, producto, variante, 1);

        when(usuarioService.obtenerEntidadPorCorreo("user1@test.com")).thenReturn(usuario);
        when(varianteRepository.findByIdWithProductoYCategoria(1L)).thenReturn(Optional.of(variante));
        when(ordenRepository.findByUsuarioIdAndEstado(1L, "PENDIENTE")).thenReturn(Optional.of(orden));
        when(itemOrdenRepository.findByOrdenIdAndProductoIdAndVarianteId(1L, 1L, 1L)).thenReturn(Optional.of(itemExistente));
        when(itemOrdenRepository.save(any(ItemOrden.class))).thenReturn(itemExistente);
        when(itemOrdenRepository.findByOrdenIdConDetalles(1L)).thenReturn(List.of(itemExistente));
        doNothing().when(auditoriaService).registrar(any(), any(), any(), any(), any());

        service.agregarItem("user1@test.com", crearAgregarItemDTO(1L, 1L, 2));

        assertEquals(3, itemExistente.getCantidad());
    }

    @Test
    void agregarItem_varianteNoEncontrada_lanzaExcepcion() {
        Usuario usuario = crearUsuario(1L);

        when(usuarioService.obtenerEntidadPorCorreo("user1@test.com")).thenReturn(usuario);
        when(varianteRepository.findByIdWithProductoYCategoria(99L)).thenReturn(Optional.empty());

        assertThrows(ProductoNotFoundException.class,
                () -> service.agregarItem("user1@test.com", crearAgregarItemDTO(1L, 99L, 1)));
    }

    @Test
    void agregarItem_varianteProductoIdNoCoincide_lanzaExcepcion() {
        Usuario usuario = crearUsuario(1L);
        Producto producto = crearProducto(2L, true);
        VarianteProducto variante = crearVariante(1L, producto, 10);

        when(usuarioService.obtenerEntidadPorCorreo("user1@test.com")).thenReturn(usuario);
        when(varianteRepository.findByIdWithProductoYCategoria(1L)).thenReturn(Optional.of(variante));

        assertThrows(ProductoNotFoundException.class,
                () -> service.agregarItem("user1@test.com", crearAgregarItemDTO(1L, 1L, 1)));
    }

    @Test
    void agregarItem_stockInsuficiente_lanzaExcepcion() {
        Usuario usuario = crearUsuario(1L);
        Producto producto = crearProducto(1L, true);
        VarianteProducto variante = crearVariante(1L, producto, 1);

        when(usuarioService.obtenerEntidadPorCorreo("user1@test.com")).thenReturn(usuario);
        when(varianteRepository.findByIdWithProductoYCategoria(1L)).thenReturn(Optional.of(variante));

        assertThrows(StockInsuficienteException.class,
                () -> service.agregarItem("user1@test.com", crearAgregarItemDTO(1L, 1L, 5)));
    }

    @Test
    void agregarItem_productoInactivo_lanzaExcepcion() {
        Usuario usuario = crearUsuario(1L);
        Producto producto = crearProducto(1L, false);
        VarianteProducto variante = crearVariante(1L, producto, 10);

        when(usuarioService.obtenerEntidadPorCorreo("user1@test.com")).thenReturn(usuario);
        when(varianteRepository.findByIdWithProductoYCategoria(1L)).thenReturn(Optional.of(variante));

        assertThrows(ProductoNotFoundException.class,
                () -> service.agregarItem("user1@test.com", crearAgregarItemDTO(1L, 1L, 1)));
    }

    @Test
    void agregarItem_especificacionNull_noFalla() {
        Usuario usuario = crearUsuario(1L);
        Producto producto = crearProducto(1L, true);
        VarianteProducto variante = crearVariante(1L, producto, 10);
        variante.setEspecificacion(null);
        Orden ordenNueva = crearOrden(1L, "PENDIENTE", usuario);
        ItemOrden item = crearItem(1L, ordenNueva, producto, variante, 1);

        when(usuarioService.obtenerEntidadPorCorreo("user1@test.com")).thenReturn(usuario);
        when(varianteRepository.findByIdWithProductoYCategoria(1L)).thenReturn(Optional.of(variante));
        when(ordenRepository.findByUsuarioIdAndEstado(1L, "PENDIENTE")).thenReturn(Optional.empty());
        when(ordenRepository.save(any(Orden.class))).thenReturn(ordenNueva);
        when(itemOrdenRepository.findByOrdenIdAndProductoIdAndVarianteId(1L, 1L, 1L)).thenReturn(Optional.empty());
        when(itemOrdenRepository.save(any(ItemOrden.class))).thenReturn(item);
        when(itemOrdenRepository.findByOrdenIdConDetalles(1L)).thenReturn(List.of(item));
        doNothing().when(auditoriaService).registrar(any(), any(), any(), any(), any());

        OrdenResponseDTO resultado = service.agregarItem("user1@test.com", crearAgregarItemDTO(1L, 1L, 1));

        assertNotNull(resultado);
    }

    @Test
    void obtenerCarrito_carritoExistente_retornaDTO() {
        Usuario usuario = crearUsuario(1L);
        Producto producto = crearProducto(1L, true);
        VarianteProducto variante = crearVariante(1L, producto, 10);
        Orden orden = crearOrden(1L, "PENDIENTE", usuario);
        ItemOrden item = crearItem(1L, orden, producto, variante, 2);

        when(usuarioService.obtenerEntidadPorCorreo("user1@test.com")).thenReturn(usuario);
        when(ordenRepository.findByUsuarioIdAndEstado(1L, "PENDIENTE")).thenReturn(Optional.of(orden));
        when(itemOrdenRepository.findByOrdenIdConDetalles(1L)).thenReturn(List.of(item));

        OrdenResponseDTO resultado = service.obtenerCarrito("user1@test.com");

        assertNotNull(resultado);
        assertEquals("PENDIENTE", resultado.getEstado());
        assertEquals(1, resultado.getItems().size());
    }

    @Test
    void obtenerCarrito_sinCarrito_lanzaExcepcion() {
        Usuario usuario = crearUsuario(1L);

        when(usuarioService.obtenerEntidadPorCorreo("user1@test.com")).thenReturn(usuario);
        when(ordenRepository.findByUsuarioIdAndEstado(1L, "PENDIENTE")).thenReturn(Optional.empty());

        assertThrows(OrdenNotFoundException.class, () -> service.obtenerCarrito("user1@test.com"));
    }

    @Test
    void obtenerCarrito_conMetodoPago_mapeaLabelCorrectamente() {
        Usuario usuario = crearUsuario(1L);
        Orden orden = crearOrden(1L, "PENDIENTE", usuario);
        MetodoPago metodoPago = new MetodoPago();
        metodoPago.setLabel("Visa *4242");
        orden.setMetodoPago(metodoPago);

        when(usuarioService.obtenerEntidadPorCorreo("user1@test.com")).thenReturn(usuario);
        when(ordenRepository.findByUsuarioIdAndEstado(1L, "PENDIENTE")).thenReturn(Optional.of(orden));
        when(itemOrdenRepository.findByOrdenIdConDetalles(1L)).thenReturn(List.of());

        OrdenResponseDTO resultado = service.obtenerCarrito("user1@test.com");

        assertEquals("Visa *4242", resultado.getMetodoPagoLabel());
    }

    @Test
    void eliminarItem_carritoConMasItems_eliminaItemYActualizaTotal() {
        Usuario usuario = crearUsuario(1L);
        Producto producto = crearProducto(1L, true);
        VarianteProducto variante = crearVariante(1L, producto, 10);
        Orden orden = crearOrden(1L, "PENDIENTE", usuario);
        orden.setTotal(150.0);
        ItemOrden itemAEliminar = crearItem(1L, orden, producto, variante, 2);
        ItemOrden itemRestante = crearItem(2L, orden, producto, variante, 1);

        when(usuarioService.obtenerEntidadPorCorreo("user1@test.com")).thenReturn(usuario);
        when(ordenRepository.findByUsuarioIdAndEstado(1L, "PENDIENTE")).thenReturn(Optional.of(orden));
        when(itemOrdenRepository.findById(1L)).thenReturn(Optional.of(itemAEliminar));
        when(itemOrdenRepository.findByOrdenIdConDetalles(1L)).thenReturn(List.of(itemRestante));
        when(ordenRepository.save(any(Orden.class))).thenReturn(orden);

        OrdenResponseDTO resultado = service.eliminarItem("user1@test.com", 1L);

        assertNotNull(resultado);
        verify(itemOrdenRepository).delete(itemAEliminar);
        verify(ordenRepository).save(orden);
    }

    @Test
    void eliminarItem_ultimoItem_eliminaOrdenCompleta() {
        Usuario usuario = crearUsuario(1L);
        Producto producto = crearProducto(1L, true);
        VarianteProducto variante = crearVariante(1L, producto, 10);
        Orden orden = crearOrden(1L, "PENDIENTE", usuario);
        orden.setTotal(50.0);
        ItemOrden item = crearItem(1L, orden, producto, variante, 1);

        when(usuarioService.obtenerEntidadPorCorreo("user1@test.com")).thenReturn(usuario);
        when(ordenRepository.findByUsuarioIdAndEstado(1L, "PENDIENTE")).thenReturn(Optional.of(orden));
        when(itemOrdenRepository.findById(1L)).thenReturn(Optional.of(item));
        when(itemOrdenRepository.findByOrdenIdConDetalles(1L)).thenReturn(List.of());

        service.eliminarItem("user1@test.com", 1L);

        verify(ordenRepository).delete(orden);
        verify(ordenRepository, never()).save(any());
    }

    @Test
    void eliminarItem_sinCarrito_lanzaExcepcion() {
        Usuario usuario = crearUsuario(1L);

        when(usuarioService.obtenerEntidadPorCorreo("user1@test.com")).thenReturn(usuario);
        when(ordenRepository.findByUsuarioIdAndEstado(1L, "PENDIENTE")).thenReturn(Optional.empty());

        assertThrows(OrdenNotFoundException.class, () -> service.eliminarItem("user1@test.com", 1L));
    }

    @Test
    void eliminarItem_itemNoExistente_lanzaExcepcion() {
        Usuario usuario = crearUsuario(1L);
        Orden orden = crearOrden(1L, "PENDIENTE", usuario);

        when(usuarioService.obtenerEntidadPorCorreo("user1@test.com")).thenReturn(usuario);
        when(ordenRepository.findByUsuarioIdAndEstado(1L, "PENDIENTE")).thenReturn(Optional.of(orden));
        when(itemOrdenRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ItemNotFoundException.class, () -> service.eliminarItem("user1@test.com", 99L));
    }

    @Test
    void confirmarOrden_sinCarritoActivo_lanzaExcepcion() {
        Usuario usuario = crearUsuario(1L);
        ConfirmarOrdenDTO dto = new ConfirmarOrdenDTO();
        dto.setMetodoPagoId(1L);

        when(usuarioService.obtenerEntidadPorCorreo("user1@test.com")).thenReturn(usuario);
        when(ordenRepository.findByUsuarioIdAndEstado(1L, "PENDIENTE")).thenReturn(Optional.empty());

        assertThrows(OrdenNotFoundException.class, () -> service.confirmarOrden("user1@test.com", dto));
    }

    @Test
    void confirmarOrden_carritoVacio_lanzaExcepcion() {
        Usuario usuario = crearUsuario(1L);
        Orden orden = crearOrden(1L, "PENDIENTE", usuario);
        ConfirmarOrdenDTO dto = new ConfirmarOrdenDTO();
        dto.setMetodoPagoId(1L);

        when(usuarioService.obtenerEntidadPorCorreo("user1@test.com")).thenReturn(usuario);
        when(ordenRepository.findByUsuarioIdAndEstado(1L, "PENDIENTE")).thenReturn(Optional.of(orden));
        when(itemOrdenRepository.findByOrdenId(1L)).thenReturn(List.of());

        assertThrows(CarritoVacioException.class, () -> service.confirmarOrden("user1@test.com", dto));
    }

    @Test
    void confirmarOrden_metodoPagoDeOtroUsuario_lanzaExcepcion() {
        Usuario usuario = crearUsuario(1L);
        Usuario otroUsuario = crearUsuario(2L);
        Producto producto = crearProducto(1L, true);
        VarianteProducto variante = crearVariante(1L, producto, 10);
        Orden orden = crearOrden(1L, "PENDIENTE", usuario);
        ItemOrden item = crearItem(1L, orden, producto, variante, 1);
        MetodoPago metodoPago = new MetodoPago();
        metodoPago.setId(1L);
        metodoPago.setUsuario(otroUsuario);
        ConfirmarOrdenDTO dto = new ConfirmarOrdenDTO();
        dto.setMetodoPagoId(1L);

        when(usuarioService.obtenerEntidadPorCorreo("user1@test.com")).thenReturn(usuario);
        when(ordenRepository.findByUsuarioIdAndEstado(1L, "PENDIENTE")).thenReturn(Optional.of(orden));
        when(itemOrdenRepository.findByOrdenId(1L)).thenReturn(List.of(item));
        when(metodoPagoService.obtenerEntidadPorId(1L)).thenReturn(metodoPago);

        assertThrows(MetodoPagoInvalidoException.class, () -> service.confirmarOrden("user1@test.com", dto));
    }

    @Test
    void confirmarOrden_stockInsuficiente_lanzaExcepcion() {
        Usuario usuario = crearUsuario(1L);
        Producto producto = crearProducto(1L, true);
        VarianteProducto variante = crearVariante(1L, producto, 1);
        Orden orden = crearOrden(1L, "PENDIENTE", usuario);
        ItemOrden item = crearItem(1L, orden, producto, variante, 5);
        MetodoPago metodoPago = new MetodoPago();
        metodoPago.setId(1L);
        metodoPago.setUsuario(usuario);
        ConfirmarOrdenDTO dto = new ConfirmarOrdenDTO();
        dto.setMetodoPagoId(1L);

        when(usuarioService.obtenerEntidadPorCorreo("user1@test.com")).thenReturn(usuario);
        when(ordenRepository.findByUsuarioIdAndEstado(1L, "PENDIENTE")).thenReturn(Optional.of(orden));
        when(itemOrdenRepository.findByOrdenId(1L)).thenReturn(List.of(item));
        when(metodoPagoService.obtenerEntidadPorId(1L)).thenReturn(metodoPago);

        assertThrows(StockInsuficienteException.class, () -> service.confirmarOrden("user1@test.com", dto));
    }

    @Test
    void confirmarOrden_stockInsuficienteConEspecificacionNull_lanzaExcepcion() {
        Usuario usuario = crearUsuario(1L);
        Producto producto = crearProducto(1L, true);
        VarianteProducto variante = crearVariante(1L, producto, 1);
        variante.setEspecificacion(null);
        Orden orden = crearOrden(1L, "PENDIENTE", usuario);
        ItemOrden item = crearItem(1L, orden, producto, variante, 5);
        MetodoPago metodoPago = new MetodoPago();
        metodoPago.setId(1L);
        metodoPago.setUsuario(usuario);
        ConfirmarOrdenDTO dto = new ConfirmarOrdenDTO();
        dto.setMetodoPagoId(1L);

        when(usuarioService.obtenerEntidadPorCorreo("user1@test.com")).thenReturn(usuario);
        when(ordenRepository.findByUsuarioIdAndEstado(1L, "PENDIENTE")).thenReturn(Optional.of(orden));
        when(itemOrdenRepository.findByOrdenId(1L)).thenReturn(List.of(item));
        when(metodoPagoService.obtenerEntidadPorId(1L)).thenReturn(metodoPago);

        assertThrows(StockInsuficienteException.class, () -> service.confirmarOrden("user1@test.com", dto));
    }

    @Test
    void historial_conOrdenes_retornaLista() {
        Usuario usuario = crearUsuario(1L);
        Producto producto = crearProducto(1L, true);
        VarianteProducto variante = crearVariante(1L, producto, 10);
        Orden orden = crearOrden(1L, "PAGADA", usuario);
        ItemOrden item = crearItem(1L, orden, producto, variante, 1);
        orden.getItems().add(item);

        when(usuarioService.obtenerEntidadPorCorreo("user1@test.com")).thenReturn(usuario);
        when(ordenRepository.findByUsuarioIdAndEstadoNot(1L, "PENDIENTE")).thenReturn(List.of(orden));

        List<OrdenResponseDTO> resultado = service.historial("user1@test.com");

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("PAGADA", resultado.get(0).getEstado());
    }

    @Test
    void historial_sinOrdenes_retornaListaVacia() {
        Usuario usuario = crearUsuario(1L);

        when(usuarioService.obtenerEntidadPorCorreo("user1@test.com")).thenReturn(usuario);
        when(ordenRepository.findByUsuarioIdAndEstadoNot(1L, "PENDIENTE")).thenReturn(List.of());

        List<OrdenResponseDTO> resultado = service.historial("user1@test.com");

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    @Test
    void cancelarOrden_carritoExistente_eliminaOrden() {
        Usuario usuario = crearUsuario(1L);
        Producto producto = crearProducto(1L, true);
        VarianteProducto variante = crearVariante(1L, producto, 10);
        Orden orden = crearOrden(1L, "PENDIENTE", usuario);
        ItemOrden item = crearItem(1L, orden, producto, variante, 2);

        when(usuarioService.obtenerEntidadPorCorreo("user1@test.com")).thenReturn(usuario);
        when(ordenRepository.findByUsuarioIdAndEstado(1L, "PENDIENTE")).thenReturn(Optional.of(orden));
        when(itemOrdenRepository.findByOrdenIdConDetalles(1L)).thenReturn(List.of(item));
        doNothing().when(auditoriaService).registrar(any(), any(), any(), any(), any());

        OrdenResponseDTO resultado = service.cancelarOrden("user1@test.com");

        assertNotNull(resultado);
        verify(itemOrdenRepository).deleteAll(anyList());
        verify(ordenRepository).delete(orden);
    }

    @Test
    void cancelarOrden_sinCarritoActivo_lanzaExcepcion() {
        Usuario usuario = crearUsuario(1L);

        when(usuarioService.obtenerEntidadPorCorreo("user1@test.com")).thenReturn(usuario);
        when(ordenRepository.findByUsuarioIdAndEstado(1L, "PENDIENTE")).thenReturn(Optional.empty());

        assertThrows(OrdenNotFoundException.class, () -> service.cancelarOrden("user1@test.com"));
    }

    @Test
    void historialLiviano_conOrdenes_retornaLista() {
        Usuario usuario = crearUsuario(1L);
        Producto producto = crearProducto(1L, true);
        VarianteProducto variante = crearVariante(1L, producto, 10);
        Orden orden = crearOrden(1L, "PAGADA", usuario);
        MetodoPago metodoPago = new MetodoPago();
        metodoPago.setLabel("Visa *1234");
        orden.setMetodoPago(metodoPago);
        ItemOrden item = crearItem(1L, orden, producto, variante, 1);
        orden.getItems().add(item);

        when(usuarioService.obtenerEntidadPorCorreo("user1@test.com")).thenReturn(usuario);
        when(ordenRepository.findHistorialByUsuarioIdAndEstadoIn(eq(1L), anyList())).thenReturn(List.of(orden));

        List<OrdenHistorialDTO> resultado = service.historialLiviano("user1@test.com");

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("Visa *1234", resultado.get(0).getMetodoPagoLabel());
        assertEquals(1, resultado.get(0).getItems().size());
    }

    @Test
    void historialLiviano_ordenSinMetodoPago_labelNull() {
        Usuario usuario = crearUsuario(1L);
        Orden orden = crearOrden(1L, "PAGADA", usuario);
        orden.setMetodoPago(null);

        when(usuarioService.obtenerEntidadPorCorreo("user1@test.com")).thenReturn(usuario);
        when(ordenRepository.findHistorialByUsuarioIdAndEstadoIn(eq(1L), anyList())).thenReturn(List.of(orden));

        List<OrdenHistorialDTO> resultado = service.historialLiviano("user1@test.com");

        assertNotNull(resultado);
        assertNull(resultado.get(0).getMetodoPagoLabel());
    }

    @Test
    void historialLiviano_sinOrdenes_retornaListaVacia() {
        Usuario usuario = crearUsuario(1L);

        when(usuarioService.obtenerEntidadPorCorreo("user1@test.com")).thenReturn(usuario);
        when(ordenRepository.findHistorialByUsuarioIdAndEstadoIn(eq(1L), anyList())).thenReturn(List.of());

        List<OrdenHistorialDTO> resultado = service.historialLiviano("user1@test.com");

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    @Test
    void notificarCarritosAbandonados_conOrdenesConItems_notificaYMarca() {
        Usuario usuario = crearUsuario(1L);
        Producto producto = crearProducto(1L, true);
        VarianteProducto variante = crearVariante(1L, producto, 10);
        Orden orden = crearOrden(1L, "PENDIENTE", usuario);
        orden.setNotificadoAbandonado(false);
        ItemOrden item = crearItem(1L, orden, producto, variante, 1);

        when(ordenRepository.findByEstadoAndFechaCreacionBeforeAndNotificadoAbandonadoFalse(
                eq("PENDIENTE"), any(LocalDateTime.class))).thenReturn(List.of(orden));
        when(itemOrdenRepository.findByOrdenId(1L)).thenReturn(List.of(item));
        doNothing().when(notificacionService).notificarCarritoAbandonado(usuario);
        when(ordenRepository.save(any(Orden.class))).thenReturn(orden);

        service.notificarCarritosAbandonados();

        assertTrue(orden.isNotificadoAbandonado());
        verify(notificacionService).notificarCarritoAbandonado(usuario);
        verify(ordenRepository).save(orden);
    }

    @Test
    void notificarCarritosAbandonados_ordenSinItems_noNotifica() {
        Orden orden = crearOrden(1L, "PENDIENTE", crearUsuario(1L));

        when(ordenRepository.findByEstadoAndFechaCreacionBeforeAndNotificadoAbandonadoFalse(
                eq("PENDIENTE"), any(LocalDateTime.class))).thenReturn(List.of(orden));
        when(itemOrdenRepository.findByOrdenId(1L)).thenReturn(List.of());

        service.notificarCarritosAbandonados();

        verify(notificacionService, never()).notificarCarritoAbandonado(any());
    }

    @Test
    void notificarCarritosAbandonados_sinOrdenes_noHaceNada() {
        when(ordenRepository.findByEstadoAndFechaCreacionBeforeAndNotificadoAbandonadoFalse(
                eq("PENDIENTE"), any(LocalDateTime.class))).thenReturn(List.of());

        service.notificarCarritosAbandonados();

        verify(notificacionService, never()).notificarCarritoAbandonado(any());
    }
}