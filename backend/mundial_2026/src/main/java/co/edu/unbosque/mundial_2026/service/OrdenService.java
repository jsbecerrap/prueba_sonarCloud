package co.edu.unbosque.mundial_2026.service;

import java.util.List;
import co.edu.unbosque.mundial_2026.dto.request.AgregarItemDTO;
import co.edu.unbosque.mundial_2026.dto.request.ConfirmarOrdenDTO;
import co.edu.unbosque.mundial_2026.dto.response.OrdenHistorialDTO;
import co.edu.unbosque.mundial_2026.dto.response.OrdenResponseDTO;

/**
 * Contrato del servicio de órdenes — gestiona el carrito de compras y el flujo
 * completo de una orden: agregar productos, confirmar pago y consultar historial
 */
public interface OrdenService {

    /**
     * Agrega un producto al carrito activo del usuario — si no tiene un carrito
     * abierto, crea uno nuevo automáticamente
     *
     * @param correo correo del usuario
     * @param dto    producto y cantidad a agregar
     * @return carrito actualizado con el nuevo ítem
     */
    OrdenResponseDTO agregarItem(String correo, AgregarItemDTO dto);

    /**
     * Retorna el carrito activo del usuario con todos sus ítems y el total acumulado
     *
     * @param correo correo del usuario
     * @return carrito actual del usuario
     */
    OrdenResponseDTO obtenerCarrito(String correo);

    /**
     * Elimina un ítem específico del carrito activo del usuario
     *
     * @param correo correo del usuario
     * @param itemId ID del ítem a eliminar
     * @return carrito actualizado sin el ítem eliminado
     */
    OrdenResponseDTO eliminarItem(String correo, Long itemId);

    /**
     * Confirma y procesa el pago de la orden activa del usuario — descuenta el stock,
     * aplica el método de pago y cierra el carrito
     *
     * @param correo correo del usuario
     * @param dto    método de pago y datos necesarios para confirmar
     * @return orden confirmada con su resumen de compra
     */
    OrdenResponseDTO confirmarOrden(String correo, ConfirmarOrdenDTO dto);

    /**
     * Retorna el historial completo de órdenes del usuario incluyendo todos sus detalles
     *
     * @param correo correo del usuario
     * @return lista de órdenes con información completa
     */
    List<OrdenResponseDTO> historial(String correo);

    /**
     * Cancela la orden activa del usuario y devuelve el stock de los productos reservados
     *
     * @param correo correo del usuario
     * @return orden con estado cancelado
     */
    OrdenResponseDTO cancelarOrden(String correo);

    /**
     * Retorna el historial de órdenes del usuario en formato resumido,
     * con menos datos que {@link #historial} — útil para listados rápidos
     *
     * @param correo correo del usuario
     * @return lista liviana de órdenes con datos básicos
     */
    List<OrdenHistorialDTO> historialLiviano(String correo);
}