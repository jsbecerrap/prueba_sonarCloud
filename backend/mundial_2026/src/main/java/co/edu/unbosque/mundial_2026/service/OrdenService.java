package co.edu.unbosque.mundial_2026.service;

import java.util.List;
import co.edu.unbosque.mundial_2026.dto.request.AgregarItemDTO;
import co.edu.unbosque.mundial_2026.dto.request.ConfirmarOrdenDTO;
import co.edu.unbosque.mundial_2026.dto.response.OrdenResponseDTO;

public interface OrdenService {
    OrdenResponseDTO agregarItem(Long usuarioId, AgregarItemDTO dto);
    OrdenResponseDTO obtenerCarrito(Long usuarioId);
    OrdenResponseDTO eliminarItem(Long usuarioId, Long itemId);
    OrdenResponseDTO confirmarOrden(Long usuarioId, ConfirmarOrdenDTO dto);
    List<OrdenResponseDTO> historial(Long usuarioId);
    OrdenResponseDTO cancelarOrden(Long usuarioId);
    
}