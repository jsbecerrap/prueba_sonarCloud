package co.edu.unbosque.mundial_2026.service;

import java.util.List;

import co.edu.unbosque.mundial_2026.dto.PartidoCapacidadDTO;
import co.edu.unbosque.mundial_2026.dto.request.EntradaRequestDTO;
import co.edu.unbosque.mundial_2026.dto.request.TransferenciaRequestDTO;
import co.edu.unbosque.mundial_2026.dto.response.EntradaResponseDTO;

public interface EntradaService {

    
    EntradaResponseDTO reservarEntrada(Long usuarioId, EntradaRequestDTO dto);

    // Confirmar pago con Stripe (sandbox)
    EntradaResponseDTO confirmarPago(Long entradaId, String paymentRef);

    // Cancelar reserva manualmente
    EntradaResponseDTO cancelarReserva(Long usuarioId, Long entradaId);

    // Transferir entrada a otro usuario
    EntradaResponseDTO transferirEntrada(Long entradaId, TransferenciaRequestDTO dto,Long usuarioId) ;

    // Solicitar reembolso
    EntradaResponseDTO reembolsarEntrada(Long usuarioId, Long entradaId);

    // Listar entradas del usuario
    List<EntradaResponseDTO> listarEntradasUsuario(Long usuarioId);

    // Obtener entrada por id
    EntradaResponseDTO obtenerEntrada(Long entradaId);

    // Scheduler: expirar reservas vencidas por TTL
    void expirarReservasVencidas();
    List<PartidoCapacidadDTO> listarPartidosConCapacidad();
}