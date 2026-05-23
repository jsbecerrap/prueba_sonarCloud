package co.edu.unbosque.mundial_2026.service;

import java.util.List;

import co.edu.unbosque.mundial_2026.dto.CuposZonaDTO;
import co.edu.unbosque.mundial_2026.dto.PartidoCapacidadDTO;
import co.edu.unbosque.mundial_2026.dto.request.EntradaRequestDTO;
import co.edu.unbosque.mundial_2026.dto.request.TransferenciaRequestDTO;
import co.edu.unbosque.mundial_2026.dto.response.EntradaResponseDTO;

/**
 * Contrato del servicio de entradas — define el ciclo de vida completo de una entrada:
 * reserva, pago, cancelación, transferencia, reembolso y expiración automática
 */
public interface EntradaService {

    /**
     * Crea una reserva de entrada para un partido asociada al correo del usuario —
     * la reserva queda pendiente de pago hasta que se confirme o expire
     *
     * @param correo correo del usuario que reserva
     * @param dto    datos del partido, zona y cantidad de entradas
     * @return entrada en estado de reserva pendiente
     */
    EntradaResponseDTO reservarEntrada(String correo, EntradaRequestDTO dto);

    /**
     * Confirma el pago de una reserva asociándole la referencia del pago externo
     *
     * @param entradaId  ID de la entrada reservada
     * @param paymentRef referencia del pago generada por el proveedor de pagos
     * @return entrada con estado actualizado a pagada
     */
    EntradaResponseDTO confirmarPago(Long entradaId, String paymentRef);

    /**
     * Cancela una reserva pendiente de pago liberando los cupos ocupados
     *
     * @param correo    correo del usuario dueño de la reserva
     * @param entradaId ID de la entrada a cancelar
     * @return entrada con estado cancelado
     */
    EntradaResponseDTO cancelarReserva(String correo, Long entradaId);

    /**
     * Transfiere una entrada pagada a otro usuario — valida que el solicitante
     * sea el dueño actual y que el partido aún no haya iniciado
     *
     * @param entradaId ID de la entrada a transferir
     * @param dto       datos del destinatario
     * @param correo    correo del dueño actual de la entrada
     * @return entrada actualizada con el nuevo propietario
     */
    EntradaResponseDTO transferirEntrada(Long entradaId, TransferenciaRequestDTO dto, String correo);

    /**
     * Procesa el reembolso de una entrada pagada y la marca como reembolsada
     *
     * @param correo    correo del usuario que solicita el reembolso
     * @param entradaId ID de la entrada a reembolsar
     * @return entrada con estado reembolsado
     */
    EntradaResponseDTO reembolsarEntrada(String correo, Long entradaId);

    /**
     * Lista todas las entradas asociadas a un usuario sin importar su estado
     *
     * @param correo correo del usuario
     * @return lista de entradas del usuario
     */
    List<EntradaResponseDTO> listarEntradasUsuario(String correo);

    /**
     * Obtiene el detalle de una entrada por su ID
     *
     * @param entradaId ID de la entrada
     * @return datos completos de la entrada
     */
    EntradaResponseDTO obtenerEntrada(Long entradaId);

    /**
     * Expira automáticamente las reservas pendientes de pago que superaron
     * su tiempo límite, liberando los cupos — diseñado para ejecutarse desde un scheduler
     */
    void expirarReservasVencidas();

    /**
     * Retorna todos los partidos con su capacidad total, ocupada y disponible por zona
     *
     * @return lista de partidos con información de capacidad
     */
    List<PartidoCapacidadDTO> listarPartidosConCapacidad();

    /**
     * Consulta los cupos disponibles por zona para un partido específico
     *
     * @param partidoId ID del partido
     * @return lista de zonas con sus cupos disponibles y ocupados
     */
    List<CuposZonaDTO> obtenerCuposPorZona(Long partidoId);

    /**
     * Notifica a los usuarios cuyas reservas están próximas a expirar
     * para que confirmen el pago antes de que se liberen los cupos —
     * diseñado para ejecutarse desde un scheduler
     */
    void avisarReservasPorExpirar();
}