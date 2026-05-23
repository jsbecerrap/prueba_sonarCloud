package co.edu.unbosque.mundial_2026.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.edu.unbosque.mundial_2026.dto.request.AgregarItemDTO;
import co.edu.unbosque.mundial_2026.dto.request.ConfirmarOrdenDTO;
import co.edu.unbosque.mundial_2026.dto.response.OrdenHistorialDTO;
import co.edu.unbosque.mundial_2026.dto.response.OrdenResponseDTO;
import co.edu.unbosque.mundial_2026.service.OrdenService;
import jakarta.validation.Valid;

/**
 * Controlador REST que expone los endpoints del carrito de compras y órdenes —
 * gestiona el flujo completo desde agregar productos hasta confirmar el pago,
 * y permite consultar el historial de compras del usuario autenticado
 *
 * <p>Base URL: {@code /api/ordenes}</p>
 */
@RestController
@RequestMapping("/api/ordenes")
public class OrdenController {

    private final OrdenService ordenService;

    public OrdenController(OrdenService ordenService) {
        this.ordenService = ordenService;
    }

    /**
     * {@code POST /api/ordenes/carrito/agregar} — Agrega un producto al carrito activo
     * del usuario — si no tiene carrito abierto, se crea uno automáticamente
     *
     * @param username correo del usuario autenticado extraído del token JWT
     * @param dto      producto y cantidad a agregar
     * @return carrito actualizado con el nuevo ítem
     */
    @PostMapping("/carrito/agregar")
    public ResponseEntity<OrdenResponseDTO> agregar(
            @AuthenticationPrincipal String username,
            @Valid @RequestBody AgregarItemDTO dto) {
        return ResponseEntity.ok(ordenService.agregarItem(username, dto));
    }

    /**
     * {@code GET /api/ordenes/carrito} — Retorna el carrito activo del usuario
     * con todos sus ítems y el total acumulado
     *
     * @param username correo del usuario autenticado extraído del token JWT
     * @return carrito actual del usuario
     */
    @GetMapping("/carrito")
    public ResponseEntity<OrdenResponseDTO> carrito(
            @AuthenticationPrincipal String username) {
        return ResponseEntity.ok(ordenService.obtenerCarrito(username));
    }

    /**
     * {@code DELETE /api/ordenes/carrito/item/{itemId}} — Elimina un ítem específico
     * del carrito activo del usuario
     *
     * @param username correo del usuario autenticado extraído del token JWT
     * @param itemId   ID del ítem a eliminar
     * @return carrito actualizado sin el ítem eliminado
     */
    @DeleteMapping("/carrito/item/{itemId}")
    public ResponseEntity<OrdenResponseDTO> eliminarItem(
            @AuthenticationPrincipal String username,
            @PathVariable Long itemId) {
        return ResponseEntity.ok(ordenService.eliminarItem(username, itemId));
    }

    /**
     * {@code POST /api/ordenes/carrito/confirmar} — Confirma y procesa el pago
     * de la orden activa — descuenta el stock, aplica el método de pago y cierra el carrito
     *
     * @param username correo del usuario autenticado extraído del token JWT
     * @param dto      método de pago y datos necesarios para confirmar la orden
     * @return orden confirmada con su resumen de compra
     */
    @PostMapping("/carrito/confirmar")
    public ResponseEntity<OrdenResponseDTO> confirmar(
            @AuthenticationPrincipal String username,
            @Valid @RequestBody ConfirmarOrdenDTO dto) {
        return ResponseEntity.ok(ordenService.confirmarOrden(username, dto));
    }

    /**
     * {@code GET /api/ordenes/historial} — Retorna el historial completo de órdenes
     * del usuario con todos sus detalles
     *
     * @param username correo del usuario autenticado extraído del token JWT
     * @return lista de órdenes con información completa
     */
    @GetMapping("/historial")
    public ResponseEntity<List<OrdenResponseDTO>> historial(
            @AuthenticationPrincipal String username) {
        return ResponseEntity.ok(ordenService.historial(username));
    }

    /**
     * {@code DELETE /api/ordenes/carrito} — Cancela la orden activa del usuario
     * y devuelve el stock de los productos reservados
     *
     * @param username correo del usuario autenticado extraído del token JWT
     * @return orden con estado cancelado
     */
    @DeleteMapping("/carrito")
    public ResponseEntity<OrdenResponseDTO> cancelar(
            @AuthenticationPrincipal String username) {
        return ResponseEntity.ok(ordenService.cancelarOrden(username));
    }

    /**
     * {@code GET /api/ordenes/historial/liviano} — Retorna el historial de órdenes
     * en formato resumido con menos datos — útil para listados rápidos donde
     * no se necesita el detalle completo de cada orden
     *
     * @param username correo del usuario autenticado extraído del token JWT
     * @return lista liviana de órdenes con datos básicos
     */
    @GetMapping("/historial/liviano")
    public ResponseEntity<List<OrdenHistorialDTO>> historialLiviano(
            @AuthenticationPrincipal String username) {
        return ResponseEntity.ok(ordenService.historialLiviano(username));
    }
}