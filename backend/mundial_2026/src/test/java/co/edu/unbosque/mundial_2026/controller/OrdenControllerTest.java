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

import co.edu.unbosque.mundial_2026.controller.OrdenController;
import co.edu.unbosque.mundial_2026.dto.request.AgregarItemDTO;
import co.edu.unbosque.mundial_2026.dto.request.ConfirmarOrdenDTO;
import co.edu.unbosque.mundial_2026.dto.response.OrdenHistorialDTO;
import co.edu.unbosque.mundial_2026.dto.response.OrdenResponseDTO;
import co.edu.unbosque.mundial_2026.exception.*;
import co.edu.unbosque.mundial_2026.service.OrdenService;

@ExtendWith(MockitoExtension.class)
class OrdenControllerTest {

    @Mock private OrdenService ordenService;
    @InjectMocks private OrdenController controller;

    private OrdenResponseDTO responseDTO(String estado) {
        OrdenResponseDTO dto = new OrdenResponseDTO();
        dto.setId(1L);
        dto.setEstado(estado);
        dto.setTotal(100.0);
        return dto;
    }

    private AgregarItemDTO agregarItemDTO() {
        AgregarItemDTO dto = new AgregarItemDTO();
        dto.setProductoId(1L);
        dto.setVarianteId(1L);
        dto.setCantidad(2);
        return dto;
    }

    private ConfirmarOrdenDTO confirmarDTO() {
        ConfirmarOrdenDTO dto = new ConfirmarOrdenDTO();
        dto.setMetodoPagoId(1L);
        return dto;
    }

    @Test
    void agregar_exitoso_retornaOk() {
        AgregarItemDTO dto = agregarItemDTO();
        when(ordenService.agregarItem("user@test.com", dto)).thenReturn(responseDTO("PENDIENTE"));

        ResponseEntity<OrdenResponseDTO> res = controller.agregar("user@test.com", dto);

        assertEquals(200, res.getStatusCode().value());
        assertNotNull(res.getBody());
        assertEquals("PENDIENTE", res.getBody().getEstado());
        verify(ordenService).agregarItem("user@test.com", dto);
    }

    @Test
    void agregar_varianteNoEncontrada_propagaExcepcion() {
        when(ordenService.agregarItem(any(), any())).thenThrow(new ProductoNotFoundException("variante no encontrada"));

        assertThrows(ProductoNotFoundException.class, () -> controller.agregar("user@test.com", agregarItemDTO()));
    }

    @Test
    void agregar_stockInsuficiente_propagaExcepcion() {
        when(ordenService.agregarItem(any(), any())).thenThrow(new StockInsuficienteException("sin stock"));

        assertThrows(StockInsuficienteException.class, () -> controller.agregar("user@test.com", agregarItemDTO()));
    }

    @Test
    void agregar_productoInactivo_propagaExcepcion() {
        when(ordenService.agregarItem(any(), any())).thenThrow(new ProductoNotFoundException("producto inactivo"));

        assertThrows(ProductoNotFoundException.class, () -> controller.agregar("user@test.com", agregarItemDTO()));
    }

    @Test
    void carrito_existente_retornaOk() {
        when(ordenService.obtenerCarrito("user@test.com")).thenReturn(responseDTO("PENDIENTE"));

        ResponseEntity<OrdenResponseDTO> res = controller.carrito("user@test.com");

        assertEquals(200, res.getStatusCode().value());
        assertEquals("PENDIENTE", res.getBody().getEstado());
        verify(ordenService).obtenerCarrito("user@test.com");
    }

    @Test
    void carrito_sinCarrito_propagaExcepcion() {
        when(ordenService.obtenerCarrito(any())).thenThrow(new OrdenNotFoundException("no hay carrito"));

        assertThrows(OrdenNotFoundException.class, () -> controller.carrito("user@test.com"));
    }

    @Test
    void eliminarItem_exitoso_retornaOk() {
        when(ordenService.eliminarItem("user@test.com", 1L)).thenReturn(responseDTO("PENDIENTE"));

        ResponseEntity<OrdenResponseDTO> res = controller.eliminarItem("user@test.com", 1L);

        assertEquals(200, res.getStatusCode().value());
        verify(ordenService).eliminarItem("user@test.com", 1L);
    }

    @Test
    void eliminarItem_sinCarrito_propagaExcepcion() {
        when(ordenService.eliminarItem(any(), any())).thenThrow(new OrdenNotFoundException("sin carrito"));

        assertThrows(OrdenNotFoundException.class, () -> controller.eliminarItem("user@test.com", 1L));
    }

    @Test
    void eliminarItem_itemNoExistente_propagaExcepcion() {
        when(ordenService.eliminarItem(any(), any())).thenThrow(new ItemNotFoundException("item no existe"));

        assertThrows(ItemNotFoundException.class, () -> controller.eliminarItem("user@test.com", 99L));
    }

    @Test
    void confirmar_exitoso_retornaOk() {
        ConfirmarOrdenDTO dto = confirmarDTO();
        when(ordenService.confirmarOrden("user@test.com", dto)).thenReturn(responseDTO("PAGADA"));

        ResponseEntity<OrdenResponseDTO> res = controller.confirmar("user@test.com", dto);

        assertEquals(200, res.getStatusCode().value());
        assertEquals("PAGADA", res.getBody().getEstado());
        verify(ordenService).confirmarOrden("user@test.com", dto);
    }

    @Test
    void confirmar_carritoVacio_propagaExcepcion() {
        when(ordenService.confirmarOrden(any(), any())).thenThrow(new CarritoVacioException("carrito vacío"));

        assertThrows(CarritoVacioException.class, () -> controller.confirmar("user@test.com", confirmarDTO()));
    }

    @Test
    void confirmar_metodoPagoInvalido_propagaExcepcion() {
        when(ordenService.confirmarOrden(any(), any())).thenThrow(new MetodoPagoInvalidoException("método inválido"));

        assertThrows(MetodoPagoInvalidoException.class, () -> controller.confirmar("user@test.com", confirmarDTO()));
    }

    @Test
    void confirmar_stockInsuficiente_propagaExcepcion() {
        when(ordenService.confirmarOrden(any(), any())).thenThrow(new StockInsuficienteException("sin stock"));

        assertThrows(StockInsuficienteException.class, () -> controller.confirmar("user@test.com", confirmarDTO()));
    }

    @Test
    void confirmar_pagoStripe_propagaExcepcion() {
        when(ordenService.confirmarOrden(any(), any())).thenThrow(new PagoStripeException("stripe error"));

        assertThrows(PagoStripeException.class, () -> controller.confirmar("user@test.com", confirmarDTO()));
    }

    @Test
    void confirmar_sinCarrito_propagaExcepcion() {
        when(ordenService.confirmarOrden(any(), any())).thenThrow(new OrdenNotFoundException("sin carrito"));

        assertThrows(OrdenNotFoundException.class, () -> controller.confirmar("user@test.com", confirmarDTO()));
    }

    @Test
    void historial_conOrdenes_retornaOk() {
        when(ordenService.historial("user@test.com")).thenReturn(List.of(responseDTO("PAGADA")));

        ResponseEntity<List<OrdenResponseDTO>> res = controller.historial("user@test.com");

        assertEquals(200, res.getStatusCode().value());
        assertEquals(1, res.getBody().size());
        verify(ordenService).historial("user@test.com");
    }

    @Test
    void historial_sinOrdenes_retornaOkVacio() {
        when(ordenService.historial("user@test.com")).thenReturn(List.of());

        ResponseEntity<List<OrdenResponseDTO>> res = controller.historial("user@test.com");

        assertEquals(200, res.getStatusCode().value());
        assertTrue(res.getBody().isEmpty());
    }

    @Test
    void cancelar_exitoso_retornaOk() {
        when(ordenService.cancelarOrden("user@test.com")).thenReturn(responseDTO("CANCELADA"));

        ResponseEntity<OrdenResponseDTO> res = controller.cancelar("user@test.com");

        assertEquals(200, res.getStatusCode().value());
        assertEquals("CANCELADA", res.getBody().getEstado());
        verify(ordenService).cancelarOrden("user@test.com");
    }

    @Test
    void cancelar_sinCarrito_propagaExcepcion() {
        when(ordenService.cancelarOrden(any())).thenThrow(new OrdenNotFoundException("sin carrito"));

        assertThrows(OrdenNotFoundException.class, () -> controller.cancelar("user@test.com"));
    }

    @Test
    void historialLiviano_conOrdenes_retornaOk() {
        OrdenHistorialDTO dto = new OrdenHistorialDTO();
        dto.setId(1L);
        dto.setEstado("PAGADA");
        when(ordenService.historialLiviano("user@test.com")).thenReturn(List.of(dto));

        ResponseEntity<List<OrdenHistorialDTO>> res = controller.historialLiviano("user@test.com");

        assertEquals(200, res.getStatusCode().value());
        assertEquals(1, res.getBody().size());
        assertEquals("PAGADA", res.getBody().get(0).getEstado());
        verify(ordenService).historialLiviano("user@test.com");
    }

    @Test
    void historialLiviano_sinOrdenes_retornaOkVacio() {
        when(ordenService.historialLiviano("user@test.com")).thenReturn(List.of());

        ResponseEntity<List<OrdenHistorialDTO>> res = controller.historialLiviano("user@test.com");

        assertEquals(200, res.getStatusCode().value());
        assertTrue(res.getBody().isEmpty());
    }
}