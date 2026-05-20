package co.edu.unbosque.mundial_2026.service;

import java.util.List;

import co.edu.unbosque.mundial_2026.dto.request.MetodoPagoRequestDTO;
import co.edu.unbosque.mundial_2026.dto.response.MetodoPagoResponseDTO;
import co.edu.unbosque.mundial_2026.entity.MetodoPago;

public interface MetodoPagoService {

    MetodoPagoResponseDTO agregar(String correo, MetodoPagoRequestDTO dto);

    List<MetodoPagoResponseDTO> listarPorCorreo(String correo);

    void setDefaultPorCorreo(String correo, Long metodoPagoId);

    void eliminar(String correo, Long id);

    MetodoPagoResponseDTO actualizar(String correo, Long id, MetodoPagoRequestDTO dto);

    MetodoPago obtenerEntidadPorId(Long id);
}