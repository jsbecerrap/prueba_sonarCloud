package co.edu.unbosque.mundial_2026.service;

import java.util.List;

import co.edu.unbosque.mundial_2026.dto.NotificacionDTO;
import co.edu.unbosque.mundial_2026.dto.request.NotificacionMasivaRequestDTO;
import co.edu.unbosque.mundial_2026.dto.request.NotificacionRequestDTO;
import co.edu.unbosque.mundial_2026.entity.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.time.LocalDateTime;

/**
 * Contrato del servicio de notificaciones — centraliza el envío de alertas
 * a los usuarios sobre eventos del sistema como pagos, reservas, apuestas y órdenes
 */
public interface NotificacionService {

    /**
     * Envía una notificación individual a un usuario específico
     *
     * @param dto datos de la notificación: destinatario, título y mensaje
     */
    void enviarNotificacion(NotificacionRequestDTO dto);

    /**
     * Envía una misma notificación a múltiples usuarios a la vez
     *
     * @param dto datos de la notificación masiva con la lista de destinatarios
     */
    void enviarMasiva(NotificacionMasivaRequestDTO dto);

    /**
     * Retorna todas las notificaciones de un usuario sin paginación
     *
     * @param usuarioId ID del usuario
     * @return lista de notificaciones del usuario
     */
    List<NotificacionDTO> listarPorUsuario(Long usuarioId);

    /**
     * Marca una notificación específica como leída
     *
     * @param notificacionId ID de la notificación
     */
    void marcarLeida(Long notificacionId);

    /**
     * Marca todas las notificaciones de un usuario como leídas
     *
     * @param usuarioId ID del usuario
     */
    void marcarTodasLeidas(Long usuarioId);

    /**
     * Notifica a todos los usuarios con entrada en un partido sobre un evento específico
     *
     * @param partidoId ID del partido
     * @param tipo      tipo de evento (ej: CANCELACION, CAMBIO_HORA)
     * @param titulo    título de la notificación
     * @param mensaje   cuerpo del mensaje
     */
    void notificarPorPartido(Long partidoId, String tipo, String titulo, String mensaje);

    /** Notifica al usuario que su pago de entrada fue procesado exitosamente */
    void notificarEntradaPagada(Usuario usuario, String partido, String categoria, String sector, String fila);

    /** Notifica al usuario que el pago de su entrada falló */
    void notificarEntradaPagoFallido(Usuario usuario);

    /** Notifica al usuario que su orden de compra fue confirmada con el total pagado */
    void notificarOrdenConfirmada(Usuario usuario, double total);

    /** Notifica al usuario que el reembolso de su entrada fue procesado exitosamente */
    void notificarEntradaReembolsada(Usuario usuario, Long entradaId);

    /** Notifica al usuario que el reembolso de su entrada no pudo completarse */
    void notificarEntradaReembolsoFallido(Usuario usuario, Long entradaId);

    /** Notifica al usuario que transfirió una entrada a otro usuario */
    void notificarEntradaTransferida(Usuario usuarioOrigen, String correoDestino, String partido);

    /** Notifica al usuario que recibió una entrada transferida por otro usuario */
    void notificarEntradaRecibida(Usuario usuarioDestino, String correoOrigen, String partido);

    /** Notifica al usuario que su reserva expiró por falta de pago y los cupos fueron liberados */
    void notificarReservaExpirada(Usuario usuario, String partido);

    /** Notifica al usuario que su reserva está próxima a vencer para que confirme el pago a tiempo */
    void notificarReservaPorExpirar(Usuario usuario, String partido);

    /** Notifica al creador de una apuesta que un nuevo usuario se ha unido */
    void notificarApuestaUnirse(Usuario usuarioNuevo, Usuario creador, String nombreApuesta);

    /** Notifica a todos los participantes que una apuesta fue cerrada */
    void notificarApuestaCerrada(List<Usuario> participantes, String nombreApuesta);

    /** Notifica al usuario su posición y puntos obtenidos tras el cálculo de una apuesta */
    void notificarPuntosCalculados(Usuario usuario, String nombreApuesta, int posicion, int puntos);

    /** Notifica al usuario que dejó productos en el carrito sin completar la compra */
    void notificarCarritoAbandonado(Usuario usuario);

    /** Notifica al usuario que su perfil fue actualizado exitosamente */
    void notificarActualizacionPerfil(Usuario usuario);

    /** Notifica al usuario que su registro en la plataforma fue exitoso */
    void notificarRegistro(Usuario usuario);

    /** Notifica al usuario que su orden de compra no pudo procesarse */
    void notificarOrdenFallida(Usuario usuario);

    /** Notifica al usuario que su reserva de entrada fue creada y está pendiente de pago */
    void notificarReservaCreada(Usuario usuario, String partido);

    /**
     * Retorna las notificaciones de un usuario de forma paginada
     *
     * @param usuarioId ID del usuario
     * @param pageable  configuración de paginación
     * @return página de notificaciones
     */
    Page<NotificacionDTO> listarPorUsuarioPaginado(Long usuarioId, Pageable pageable);

    /**
     * Filtra las notificaciones de un usuario dentro de un rango de fechas
     *
     * @param usuarioId ID del usuario
     * @param desde     fecha de inicio del rango
     * @param hasta     fecha de fin del rango
     * @param pageable  configuración de paginación
     * @return página de notificaciones dentro del rango indicado
     */
    Page<NotificacionDTO> listarPorFecha(Long usuarioId, LocalDateTime desde,
            LocalDateTime hasta, Pageable pageable);
}