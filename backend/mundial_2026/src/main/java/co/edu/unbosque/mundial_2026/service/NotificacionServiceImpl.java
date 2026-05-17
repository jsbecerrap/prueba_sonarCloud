package co.edu.unbosque.mundial_2026.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;

import co.edu.unbosque.mundial_2026.dto.NotificacionDTO;
import co.edu.unbosque.mundial_2026.dto.request.NotificacionMasivaRequestDTO;
import co.edu.unbosque.mundial_2026.dto.request.NotificacionRequestDTO;
import co.edu.unbosque.mundial_2026.entity.Notificacion;
import co.edu.unbosque.mundial_2026.entity.Partido;
import co.edu.unbosque.mundial_2026.entity.Usuario;
import co.edu.unbosque.mundial_2026.exception.PartidoNotFoundException;
import co.edu.unbosque.mundial_2026.exception.UsuarioNotFoundException;
import co.edu.unbosque.mundial_2026.repository.NotificacionRepository;
import co.edu.unbosque.mundial_2026.repository.PartidoRepository;
import co.edu.unbosque.mundial_2026.repository.UsuarioRepository;

@Service
public class NotificacionServiceImpl implements NotificacionService {

    private static final String ESTADO_ENVIADA = "ENVIADA";
    private static final String CANAL_SISTEMA = "SISTEMA";
    private static final String USUARIO_NO_ENCONTRADO = "Usuario no encontrado";

   private final NotificacionRepository notificacionRepository;
private final UsuarioRepository usuarioRepository;
private final PartidoRepository partidoRepository;
private final EventoAuditoriaService auditoriaService;

public NotificacionServiceImpl(NotificacionRepository notificacionRepository,
        UsuarioRepository usuarioRepository,
        PartidoRepository partidoRepository,
        @Lazy EventoAuditoriaService auditoriaService) {
    this.notificacionRepository = notificacionRepository;
    this.usuarioRepository = usuarioRepository;
    this.partidoRepository = partidoRepository;
    this.auditoriaService = auditoriaService;
}

    @Override
    @Transactional
    public void enviarNotificacion(NotificacionRequestDTO dto) {
        Usuario usuario = usuarioRepository.findById(dto.getUsuarioId())
                .orElseThrow(() -> new UsuarioNotFoundException(USUARIO_NO_ENCONTRADO));
        Notificacion notificacion = new Notificacion(
                dto.getTipo(), dto.getTitulo(), dto.getMensaje(),
                dto.getCanal(), ESTADO_ENVIADA, usuario);
        notificacionRepository.save(notificacion);
        enviarPush(usuario, dto.getTitulo(), dto.getMensaje());
    }

   @Override
@Transactional
public void enviarMasiva(NotificacionMasivaRequestDTO dto) {
    List<Usuario> destinatarios;

    if (dto.getUsuarioIds() == null || dto.getUsuarioIds().isEmpty()) {
        destinatarios = usuarioRepository.findAll()
                .stream()
                .filter(Usuario::isActivo)
                .toList();
    } else {
        destinatarios = usuarioRepository.findAllById(dto.getUsuarioIds())
                .stream()
                .filter(Usuario::isActivo)
                .toList();
    }

    List<Notificacion> notificaciones = new ArrayList<>();
    for (Usuario usuario : destinatarios) {
        notificaciones.add(new Notificacion(
                dto.getTipo(), dto.getTitulo(), dto.getMensaje(),
                dto.getCanal(), ESTADO_ENVIADA, usuario));
        enviarPush(usuario, dto.getTitulo(), dto.getMensaje());
    }

    notificacionRepository.saveAll(notificaciones);

    auditoriaService.registrar(
            "NOTIFICACION_MASIVA",
            "Notificacion masiva enviada | tipo: " + dto.getTipo()
                    + " | titulo: " + dto.getTitulo()
                    + " | destinatarios: " + destinatarios.size(),
            null,
            null,
            "NOTIFICACION");
}

    @Override
    @Transactional(readOnly = true)
    public List<NotificacionDTO> listarPorUsuario(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new UsuarioNotFoundException(USUARIO_NO_ENCONTRADO));
        List<Notificacion> lista = notificacionRepository.findByUsuario(usuario);
        List<NotificacionDTO> resultado = new ArrayList<>();
        for (Notificacion n : lista) {
            resultado.add(toDTO(n));
        }
        return resultado;
    }

    @Override
    @Transactional
    public void marcarLeida(Long notificacionId) {
        Notificacion notificacion = notificacionRepository.findById(notificacionId)
                .orElseThrow(() -> new RuntimeException("Notificación no encontrada"));
        notificacion.setLeida(true);
        notificacionRepository.save(notificacion);
    }

    @Override
@Transactional
public void marcarTodasLeidas(Long usuarioId) {
    notificacionRepository.marcarTodasLeidasPorUsuario(usuarioId);
}

  @Override
@Transactional
public void notificarPorPartido(Long partidoId, String tipo, String titulo, String mensaje) {
    Partido partido = partidoRepository.findById(partidoId)
            .orElseThrow(() -> new PartidoNotFoundException("Partido no encontrado"));

    List<String> selecciones = new ArrayList<>();
    selecciones.add(partido.getSeleccionLocal());
    selecciones.add(partido.getSeleccionVisitante());

    List<Usuario> todosActivos = usuarioRepository.findAll()
            .stream()
            .filter(Usuario::isActivo)
            .toList();

    List<Usuario> destinatarios = todosActivos.stream()
            .filter(u -> u.getSeleccionesU() != null &&
                    u.getSeleccionesU().stream()
                            .anyMatch(s -> selecciones.contains(s.getNombre())))
            .toList();

    List<Notificacion> notificaciones = new ArrayList<>();
    for (Usuario usuario : destinatarios) {
        notificaciones.add(new Notificacion(tipo, titulo, mensaje, CANAL_SISTEMA, ESTADO_ENVIADA, usuario));
        enviarPush(usuario, titulo, mensaje);
    }

    notificacionRepository.saveAll(notificaciones);

    auditoriaService.registrar(
            "NOTIFICACION_POR_PARTIDO",
            "Notificacion enviada para " + partido.getSeleccionLocal() + " vs " + partido.getSeleccionVisitante()
                    + " | titulo: " + titulo
                    + " | destinatarios: " + destinatarios.size(),
            null,
            String.valueOf(partidoId),
            "PARTIDO");
}

    @Override
    @Transactional
    public void notificarRegistro(Usuario usuario) {
        Notificacion notificacion = new Notificacion(
                CANAL_SISTEMA,
                "Bienvenido(a) a Mundial 2026 Hub",
                "Tu cuenta ha sido creada exitosamente.",
                CANAL_SISTEMA, ESTADO_ENVIADA, usuario);
        notificacionRepository.save(notificacion);
        enviarPush(usuario, "Bienvenido(a) a Mundial 2026 Hub", "Tu cuenta ha sido creada exitosamente.");
    }

    @Override
    @Transactional
    public void notificarActualizacionPerfil(Usuario usuario) {
        Notificacion notificacion = new Notificacion(
                CANAL_SISTEMA,
                "Perfil actualizado",
                "Tu perfil ha sido actualizado exitosamente.",
                CANAL_SISTEMA, ESTADO_ENVIADA, usuario);
        notificacionRepository.save(notificacion);
        enviarPush(usuario, "Perfil actualizado", "Tu perfil ha sido actualizado exitosamente.");
    }

    @Override
    @Transactional
    public void notificarEntradaPagada(Usuario usuario, String partido, String categoria, String sector, String fila) {
        String titulo = "Entrada confirmada";
        String mensaje = "Tu entrada para " + partido + " fue confirmada. Categoria: " + categoria + ", Sector: " + sector + ", Fila: " + fila + ".";
        Notificacion notificacion = new Notificacion("ENTRADA_PAGADA", titulo, mensaje, CANAL_SISTEMA, ESTADO_ENVIADA, usuario);
        notificacionRepository.save(notificacion);
        enviarPush(usuario, titulo, mensaje);
    }

    @Override
    @Transactional
    public void notificarEntradaPagoFallido(Usuario usuario) {
        String titulo = "Pago no procesado";
        String mensaje = "No pudimos procesar tu pago. Revisa los datos de tu tarjeta e intenta de nuevo.";
        Notificacion notificacion = new Notificacion("ENTRADA_PAGO_FALLIDO", titulo, mensaje, CANAL_SISTEMA, ESTADO_ENVIADA, usuario);
        notificacionRepository.save(notificacion);
        enviarPush(usuario, titulo, mensaje);
    }

    @Override
    @Transactional
    public void notificarOrdenConfirmada(Usuario usuario, double total) {
        String titulo = "Compra exitosa";
        String mensaje = "Tu compra en tienda fue procesada exitosamente por $" + total + ".";
        Notificacion notificacion = new Notificacion("ORDEN_CONFIRMADA", titulo, mensaje, CANAL_SISTEMA, ESTADO_ENVIADA, usuario);
        notificacionRepository.save(notificacion);
        enviarPush(usuario, titulo, mensaje);
    }

    @Override
    @Transactional
    public void notificarEntradaReembolsada(Usuario usuario, Long entradaId) {
        String titulo = "Reembolso procesado";
        String mensaje = "Tu reembolso por la entrada " + entradaId + " fue procesado exitosamente.";
        Notificacion notificacion = new Notificacion("ENTRADA_REEMBOLSADA", titulo, mensaje, CANAL_SISTEMA, ESTADO_ENVIADA, usuario);
        notificacionRepository.save(notificacion);
        enviarPush(usuario, titulo, mensaje);
    }

    @Override
    @Transactional
    public void notificarEntradaReembolsoFallido(Usuario usuario, Long entradaId) {
        String titulo = "Reembolso no procesado";
        String mensaje = "No pudimos procesar el reembolso de tu entrada " + entradaId + ". Intenta de nuevo mas tarde.";
        Notificacion notificacion = new Notificacion("ENTRADA_REEMBOLSO_FALLIDO", titulo, mensaje, CANAL_SISTEMA, ESTADO_ENVIADA, usuario);
        notificacionRepository.save(notificacion);
        enviarPush(usuario, titulo, mensaje);
    }

    @Override
    @Transactional
    public void notificarEntradaTransferida(Usuario usuarioOrigen, String correoDestino, String partido) {
        String titulo = "Entrada transferida";
        String mensaje = "Transferiste tu entrada para " + partido + " a " + correoDestino + ".";
        Notificacion notificacion = new Notificacion("ENTRADA_TRANSFERIDA", titulo, mensaje, CANAL_SISTEMA, ESTADO_ENVIADA, usuarioOrigen);
        notificacionRepository.save(notificacion);
        enviarPush(usuarioOrigen, titulo, mensaje);
    }

    @Override
    @Transactional
    public void notificarEntradaRecibida(Usuario usuarioDestino, String correoOrigen, String partido) {
        String titulo = "Recibiste una entrada";
        String mensaje = correoOrigen + " te transfirió una entrada para " + partido + ".";
        Notificacion notificacion = new Notificacion("ENTRADA_RECIBIDA", titulo, mensaje, CANAL_SISTEMA, ESTADO_ENVIADA, usuarioDestino);
        notificacionRepository.save(notificacion);
        enviarPush(usuarioDestino, titulo, mensaje);
    }

    @Override
    @Transactional
    public void notificarReservaExpirada(Usuario usuario, String partido) {
        String titulo = "Reserva expirada";
        String mensaje = "Tu reserva para " + partido + " expiro. El cupo fue liberado.";
        Notificacion notificacion = new Notificacion("RESERVA_EXPIRADA", titulo, mensaje, CANAL_SISTEMA, ESTADO_ENVIADA, usuario);
        notificacionRepository.save(notificacion);
        enviarPush(usuario, titulo, mensaje);
    }

    @Override
    @Transactional
    public void notificarReservaPorExpirar(Usuario usuario, String partido) {
        String titulo = "Tu reserva vence pronto";
        String mensaje = "Quedan 5 minutos para que tu reserva para " + partido + " expire. Completa el pago antes de que se libere el cupo.";
        Notificacion notificacion = new Notificacion("RESERVA_POR_EXPIRAR", titulo, mensaje, CANAL_SISTEMA, ESTADO_ENVIADA, usuario);
        notificacionRepository.save(notificacion);
        enviarPush(usuario, titulo, mensaje);
    }
@Async
    @Override
    @Transactional
    public void notificarApuestaUnirse(Usuario usuarioNuevo, Usuario creador, String nombreApuesta) {
        String tituloNuevo = "Te uniste a una polla";
        String mensajeNuevo = "Te uniste a la polla " + nombreApuesta + ".";
        Notificacion notifNuevo = new Notificacion("APUESTA_UNIRSE", tituloNuevo, mensajeNuevo, CANAL_SISTEMA, ESTADO_ENVIADA, usuarioNuevo);
        notificacionRepository.save(notifNuevo);
        enviarPush(usuarioNuevo, tituloNuevo, mensajeNuevo);

        String tituloCreador = "Nuevo participante";
        String mensajeCreador = usuarioNuevo.getCorreoUsuario() + " se unio a tu polla " + nombreApuesta + ".";
        Notificacion notifCreador = new Notificacion("APUESTA_UNIRSE", tituloCreador, mensajeCreador, CANAL_SISTEMA, ESTADO_ENVIADA, creador);
        notificacionRepository.save(notifCreador);
        enviarPush(creador, tituloCreador, mensajeCreador);
    }

    @Override
    @Transactional
    public void notificarApuestaCerrada(List<Usuario> participantes, String nombreApuesta) {
        String titulo = "Polla cerrada";
        String mensaje = "La polla " + nombreApuesta + " cerro. Ya no se aceptan pronosticos.";
        List<Notificacion> notificaciones = new ArrayList<>();
        for (Usuario participante : participantes) {
            notificaciones.add(new Notificacion("APUESTA_CERRADA", titulo, mensaje, CANAL_SISTEMA, ESTADO_ENVIADA, participante));
            enviarPush(participante, titulo, mensaje);
        }
        notificacionRepository.saveAll(notificaciones);
    }

    @Override
    @Transactional
    public void notificarPuntosCalculados(Usuario usuario, String nombreApuesta, int posicion, int puntos) {
        String titulo = "Resultados de tu polla";
        String mensaje = "Se calcularon los puntos de " + nombreApuesta + ". Tu posicion: #" + posicion + " con " + puntos + " pts.";
        Notificacion notificacion = new Notificacion("PUNTOS_CALCULADOS", titulo, mensaje, CANAL_SISTEMA, ESTADO_ENVIADA, usuario);
        notificacionRepository.save(notificacion);
        enviarPush(usuario, titulo, mensaje);
    }

    @Override
    @Transactional
    public void notificarCarritoAbandonado(Usuario usuario) {
        String titulo = "Tienes productos en tu carrito";
        String mensaje = "Dejaste productos en tu carrito. Completa tu compra antes de que se agoten.";
        Notificacion notificacion = new Notificacion("CARRITO_ABANDONADO", titulo, mensaje, CANAL_SISTEMA, ESTADO_ENVIADA, usuario);
        notificacionRepository.save(notificacion);
        enviarPush(usuario, titulo, mensaje);
    }

    @Override
    @Transactional
    public void notificarOrdenFallida(Usuario usuario) {
        String titulo = "Pago no procesado";
        String mensaje = "No pudimos procesar el pago de tu orden. Revisa los datos de tu tarjeta e intenta de nuevo.";
        Notificacion notificacion = new Notificacion("ORDEN_FALLIDA", titulo, mensaje, CANAL_SISTEMA, ESTADO_ENVIADA, usuario);
        notificacionRepository.save(notificacion);
        enviarPush(usuario, titulo, mensaje);
    }

    private void enviarPush(Usuario usuario, String titulo, String mensaje) {
        if (usuario.getFcmtoken() == null || usuario.getFcmtoken().isBlank()) {
            return;
        }
        try {
            Message message = Message.builder()
                    .setToken(usuario.getFcmtoken())
                    .setNotification(Notification.builder()
                            .setTitle(titulo)
                            .setBody(mensaje)
                            .build())
                    .build();
            FirebaseMessaging.getInstance().send(message);
        } catch (FirebaseMessagingException e) {
            // El fallo del push no interrumpe la operación de negocio
        }
    }

    private NotificacionDTO toDTO(Notificacion n) {
        return new NotificacionDTO(
                n.getId(), n.getTipo(), n.getTitulo(), n.getMensaje(),
                n.getCanal(), n.getEstado(), n.isLeida(), n.getFecha(),
                n.getUsuario().getId());
    }
    @Override
@Transactional
public void notificarReservaCreada(Usuario usuario, String partido) {
    String titulo = "Reserva confirmada";
    String mensaje = "Tienes 15 minutos para pagar tu entrada para " + partido + ". ¡No pierdas el cupo!";
    Notificacion notificacion = new Notificacion("RESERVA_CREADA", titulo, mensaje, CANAL_SISTEMA, ESTADO_ENVIADA, usuario);
    notificacionRepository.save(notificacion);
    enviarPush(usuario, titulo, mensaje);
}
@Override
@Transactional(readOnly = true)
public Page<NotificacionDTO> listarPorUsuarioPaginado(Long usuarioId, Pageable pageable) {
    return notificacionRepository.findByUsuarioIdOrderByFechaDesc(usuarioId, pageable)
            .map(this::toDTO);
}

@Override
@Transactional(readOnly = true)
public Page<NotificacionDTO> listarPorFecha(Long usuarioId, LocalDateTime desde,
        LocalDateTime hasta, Pageable pageable) {
    return notificacionRepository.findByUsuarioIdAndFechaBetweenOrderByFechaDesc(
            usuarioId, desde, hasta, pageable)
            .map(this::toDTO);
}
}