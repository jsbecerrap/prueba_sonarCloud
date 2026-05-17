package co.edu.unbosque.mundial_2026.service;

import java.util.List;

import co.edu.unbosque.mundial_2026.dto.NotificacionDTO;
import co.edu.unbosque.mundial_2026.dto.request.NotificacionMasivaRequestDTO;
import co.edu.unbosque.mundial_2026.dto.request.NotificacionRequestDTO;
import co.edu.unbosque.mundial_2026.entity.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.time.LocalDateTime;
public interface NotificacionService {

    void enviarNotificacion(NotificacionRequestDTO dto);

    void enviarMasiva(NotificacionMasivaRequestDTO dto);

    List<NotificacionDTO> listarPorUsuario(Long usuarioId);

    void marcarLeida(Long notificacionId);

    void marcarTodasLeidas(Long usuarioId);

    void notificarPorPartido(Long partidoId, String tipo, String titulo, String mensaje);

    void notificarEntradaPagada(Usuario usuario, String partido, String categoria, String sector, String fila);

void notificarEntradaPagoFallido(Usuario usuario);

void notificarOrdenConfirmada(Usuario usuario, double total);

void notificarEntradaReembolsada(Usuario usuario, Long entradaId);

void notificarEntradaReembolsoFallido(Usuario usuario, Long entradaId);

void notificarEntradaTransferida(Usuario usuarioOrigen, String correoDestino, String partido);

void notificarEntradaRecibida(Usuario usuarioDestino, String correoOrigen, String partido);

void notificarReservaExpirada(Usuario usuario, String partido);

void notificarReservaPorExpirar(Usuario usuario, String partido);

void notificarApuestaUnirse(Usuario usuarioNuevo, Usuario creador, String nombreApuesta);

void notificarApuestaCerrada(List<Usuario> participantes, String nombreApuesta);

void notificarPuntosCalculados(Usuario usuario, String nombreApuesta, int posicion, int puntos);

void notificarCarritoAbandonado(Usuario usuario);

    void notificarActualizacionPerfil(Usuario usuario);
    void notificarRegistro(Usuario usuario);
    void notificarOrdenFallida(Usuario usuario);
void notificarReservaCreada(Usuario usuario, String partido);
Page<NotificacionDTO> listarPorUsuarioPaginado(Long usuarioId, Pageable pageable);

Page<NotificacionDTO> listarPorFecha(Long usuarioId, LocalDateTime desde, 
    LocalDateTime hasta, Pageable pageable);
}