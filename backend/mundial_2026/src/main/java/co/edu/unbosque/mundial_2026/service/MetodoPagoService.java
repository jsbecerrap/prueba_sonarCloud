package co.edu.unbosque.mundial_2026.service;

import java.util.List;

import co.edu.unbosque.mundial_2026.dto.request.MetodoPagoRequestDTO;
import co.edu.unbosque.mundial_2026.dto.response.MetodoPagoResponseDTO;
import co.edu.unbosque.mundial_2026.entity.MetodoPago;

public interface MetodoPagoService {
    MetodoPagoResponseDTO agregar(MetodoPagoRequestDTO dto);
    List<MetodoPagoResponseDTO> listarPorUsuario(Long usuarioId);
    boolean setDefault(Long usuarioId, Long metodoPagoId);
    MetodoPago obtenerEntidadPorId(Long id);
}