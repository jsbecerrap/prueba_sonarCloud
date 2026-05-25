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
import org.springframework.http.ResponseEntity;

import co.edu.unbosque.mundial_2026.dto.request.AgregarItemDTO;
import co.edu.unbosque.mundial_2026.dto.request.ConfirmarOrdenDTO;
import co.edu.unbosque.mundial_2026.dto.response.OrdenHistorialDTO;
import co.edu.unbosque.mundial_2026.dto.response.OrdenResponseDTO;
import co.edu.unbosque.mundial_2026.exception.*;
import co.edu.unbosque.mundial_2026.service.OrdenService;

@ExtendWith(MockitoExtension.class)
class OrdenControllerTest {

    @Mock
    private OrdenService ordenService;

    @InjectMocks
    private OrdenController controller;

    private static final String USER_CORREO = "user@test.com";
    private static final String PENDIENTE = "PENDIENTE";
    private static final String PAGADA = "PAGADA";

    /**
     * Crea un DTO de respuesta de orden para pruebas.
     *
     * @param estado estado de la orden
     * @return objeto OrdenResponseDTO con datos de prueba
     */
    private OrdenResponseDTO responseDTO(String estado) {
        OrdenResponseDTO dto = new OrdenResponseDTO();
        dto.setId(1L);
        dto.setEstado(estado);
        dto.setTotal(100.0);
        return dto;
    }

    /**
     * Crea un DTO válido para agregar ítems.
     *
     * @return objeto AgregarItemDTO con datos de prueba
     */
    private AgregarItemDTO agregarItemDTO() {
        AgregarItemDTO dto = new AgregarItemDTO();
        dto.setProductoId(1L);
        dto.setVarianteId(1L);
        dto.setCantidad(2);
        return dto;
    }

    /**
     * Crea un DTO válido para confirmar orden.
     *
     * @return objeto ConfirmarOrdenDTO con datos de prueba
     */
    private ConfirmarOrdenDTO confirmarDTO() {
        ConfirmarOrdenDTO dto = new ConfirmarOrdenDTO();
        dto.setMetodoPagoId(1L);
        return dto;
    }

    /**
     * Verifica que agregar retorne código 200 cuando es exitoso.
     */
    @Test
    void agregar_exitoso_retornaOk() {
        AgregarItemDTO dto = agregarItemDTO();
        when(ordenService.agregarItem(USER_CORREO, dto)).thenReturn(responseDTO(PENDIENTE));

        ResponseEntity<OrdenResponseDTO> res = controller.agregar(USER_CORREO, dto);

        assertEquals(200, res.getStatusCode().value());
        assertNotNull(res.getBody());
        assertEquals(PENDIENTE, res.getBody().getEstado());
        verify(ordenService).agregarItem(USER_CORREO, dto);
    }

    /**
     * Verifica que se propague excepción cuando la variante no existe.
     */
    @Test
    void agregar_varianteNoEncontrada_propagaExcepcion() {
        AgregarItemDTO dto = agregarItemDTO();
        when(ordenService.agregarItem(any(), any())).thenThrow(new ProductoNotFoundException("variante no encontrada"));

        assertThrows(ProductoNotFoundException.class, () -> controller.agregar(USER_CORREO, dto));
    }

    /**
     * Verifica que se propague excepción cuando no hay stock.
     */
    @Test
    void agregar_stockInsuficiente_propagaExcepcion() {
        AgregarItemDTO dto = agregarItemDTO();
        when(ordenService.agregarItem(any(), any())).thenThrow(new StockInsuficienteException("sin stock"));

        assertThrows(StockInsuficienteException.class, () -> controller.agregar(USER_CORREO, dto));
    }

    /**
     * Verifica que se propague excepción cuando el producto está inactivo.
     */
    @Test
    void agregar_productoInactivo_propagaExcepcion() {
        AgregarItemDTO dto = agregarItemDTO();
        when(ordenService.agregarItem(any(), any())).thenThrow(new ProductoNotFoundException("producto inactivo"));

        assertThrows(ProductoNotFoundException.class, () -> controller.agregar(USER_CORREO, dto));
    }

    /**
     * Verifica que carrito retorne código 200 cuando existe.
     */
    @Test
    void carrito_existente_retornaOk() {
        when(ordenService.obtenerCarrito(USER_CORREO)).thenReturn(responseDTO(PENDIENTE));

        ResponseEntity<OrdenResponseDTO> res = controller.carrito(USER_CORREO);

        assertEquals(200, res.getStatusCode().value());
        assertEquals(PENDIENTE, res.getBody().getEstado());
        verify(ordenService).obtenerCarrito(USER_CORREO);
    }

    /**
     * Verifica que se propague excepción cuando no existe carrito.
     */
    @Test
    void carrito_sinCarrito_propagaExcepcion() {
        when(ordenService.obtenerCarrito(any())).thenThrow(new OrdenNotFoundException("no hay carrito"));

        assertThrows(OrdenNotFoundException.class, () -> controller.carrito(USER_CORREO));
    }

    /**
     * Verifica que eliminarItem retorne código 200 cuando es exitoso.
     */
    @Test
    void eliminarItem_exitoso_retornaOk() {
        when(ordenService.eliminarItem(USER_CORREO, 1L)).thenReturn(responseDTO(PENDIENTE));

        ResponseEntity<OrdenResponseDTO> res = controller.eliminarItem(USER_CORREO, 1L);

        assertEquals(200, res.getStatusCode().value());
        verify(ordenService).eliminarItem(USER_CORREO, 1L);
    }

    /**
     * Verifica que se propague excepción cuando no hay carrito al eliminar.
     */
    @Test
    void eliminarItem_sinCarrito_propagaExcepcion() {
        when(ordenService.eliminarItem(any(), any())).thenThrow(new OrdenNotFoundException("sin carrito"));

        assertThrows(OrdenNotFoundException.class, () -> controller.eliminarItem(USER_CORREO, 1L));
    }

    /**
     * Verifica que se propague excepción cuando el item no existe.
     */
    @Test
    void eliminarItem_itemNoExistente_propagaExcepcion() {
        when(ordenService.eliminarItem(any(), any())).thenThrow(new ItemNotFoundException("item no existe"));

        assertThrows(ItemNotFoundException.class, () -> controller.eliminarItem(USER_CORREO, 99L));
    }

    /**
     * Verifica que confirmar retorne código 200 cuando es exitoso.
     */
    @Test
    void confirmar_exitoso_retornaOk() {
        ConfirmarOrdenDTO dto = confirmarDTO();
        when(ordenService.confirmarOrden(USER_CORREO, dto)).thenReturn(responseDTO(PAGADA));

        ResponseEntity<OrdenResponseDTO> res = controller.confirmar(USER_CORREO, dto);

        assertEquals(200, res.getStatusCode().value());
        assertEquals(PAGADA, res.getBody().getEstado());
        verify(ordenService).confirmarOrden(USER_CORREO, dto);
    }

    /**
     * Verifica que se propague excepción cuando el carrito está vacío.
     */
    @Test
    void confirmar_carritoVacio_propagaExcepcion() {
        ConfirmarOrdenDTO dto = confirmarDTO();
        when(ordenService.confirmarOrden(any(), any())).thenThrow(new CarritoVacioException("carrito vacío"));

        assertThrows(CarritoVacioException.class, () -> controller.confirmar(USER_CORREO, dto));
    }

    /**
     * Verifica que se propague excepción cuando el método de pago es inválido.
     */
    @Test
    void confirmar_metodoPagoInvalido_propagaExcepcion() {
        ConfirmarOrdenDTO dto = confirmarDTO();
        when(ordenService.confirmarOrden(any(), any())).thenThrow(new MetodoPagoInvalidoException("método inválido"));

        assertThrows(MetodoPagoInvalidoException.class, () -> controller.confirmar(USER_CORREO, dto));
    }

    /**
     * Verifica que se propague excepción cuando no hay stock al confirmar.
     */
    @Test
    void confirmar_stockInsuficiente_propagaExcepcion() {
        ConfirmarOrdenDTO dto = confirmarDTO();
        when(ordenService.confirmarOrden(any(), any())).thenThrow(new StockInsuficienteException("sin stock"));

        assertThrows(StockInsuficienteException.class, () -> controller.confirmar(USER_CORREO, dto));
    }

    /**
     * Verifica que se propague excepción cuando Stripe falla.
     */
    @Test
    void confirmar_pagoStripe_propagaExcepcion() {
        ConfirmarOrdenDTO dto = confirmarDTO();
        when(ordenService.confirmarOrden(any(), any())).thenThrow(new PagoStripeException("stripe error"));

        assertThrows(PagoStripeException.class, () -> controller.confirmar(USER_CORREO, dto));
    }

    /**
     * Verifica que se propague excepción cuando no hay carrito al confirmar.
     */
    @Test
    void confirmar_sinCarrito_propagaExcepcion() {
        ConfirmarOrdenDTO dto = confirmarDTO();
        when(ordenService.confirmarOrden(any(), any())).thenThrow(new OrdenNotFoundException("sin carrito"));

        assertThrows(OrdenNotFoundException.class, () -> controller.confirmar(USER_CORREO, dto));
    }

    /**
     * Verifica que historial retorne código 200 con órdenes.
     */
    @Test
    void historial_conOrdenes_retornaOk() {
        when(ordenService.historial(USER_CORREO)).thenReturn(List.of(responseDTO(PAGADA)));

        ResponseEntity<List<OrdenResponseDTO>> res = controller.historial(USER_CORREO);

        assertEquals(200, res.getStatusCode().value());
        assertEquals(1, res.getBody().size());
        verify(ordenService).historial(USER_CORREO);
    }

    /**
     * Verifica que historial retorne código 200 cuando está vacío.
     */
    @Test
    void historial_sinOrdenes_retornaOkVacio() {
        when(ordenService.historial(USER_CORREO)).thenReturn(List.of());

        ResponseEntity<List<OrdenResponseDTO>> res = controller.historial(USER_CORREO);

        assertEquals(200, res.getStatusCode().value());
        assertTrue(res.getBody().isEmpty());
    }

    /**
     * Verifica que cancelar retorne código 200 cuando es exitoso.
     */
    @Test
    void cancelar_exitoso_retornaOk() {
        when(ordenService.cancelarOrden(USER_CORREO)).thenReturn(responseDTO("CANCELADA"));

        ResponseEntity<OrdenResponseDTO> res = controller.cancelar(USER_CORREO);

        assertEquals(200, res.getStatusCode().value());
        assertEquals("CANCELADA", res.getBody().getEstado());
        verify(ordenService).cancelarOrden(USER_CORREO);
    }

    /**
     * Verifica que se propague excepción cuando no hay carrito al cancelar.
     */
    @Test
    void cancelar_sinCarrito_propagaExcepcion() {
        when(ordenService.cancelarOrden(any())).thenThrow(new OrdenNotFoundException("sin carrito"));

        assertThrows(OrdenNotFoundException.class, () -> controller.cancelar(USER_CORREO));
    }

    /**
     * Verifica que historialLiviano retorne código 200 con órdenes.
     */
    @Test
    void historialLiviano_conOrdenes_retornaOk() {
        OrdenHistorialDTO dto = new OrdenHistorialDTO();
        dto.setId(1L);
        dto.setEstado(PAGADA);
        when(ordenService.historialLiviano(USER_CORREO)).thenReturn(List.of(dto));

        ResponseEntity<List<OrdenHistorialDTO>> res = controller.historialLiviano(USER_CORREO);

        assertEquals(200, res.getStatusCode().value());
        assertEquals(1, res.getBody().size());
        assertEquals(PAGADA, res.getBody().get(0).getEstado());
        verify(ordenService).historialLiviano(USER_CORREO);
    }

    /**
     * Verifica que historialLiviano retorne código 200 cuando está vacío.
     */
    @Test
    void historialLiviano_sinOrdenes_retornaOkVacio() {
        when(ordenService.historialLiviano(USER_CORREO)).thenReturn(List.of());

        ResponseEntity<List<OrdenHistorialDTO>> res = controller.historialLiviano(USER_CORREO);

        assertEquals(200, res.getStatusCode().value());
        assertTrue(res.getBody().isEmpty());
    }
}