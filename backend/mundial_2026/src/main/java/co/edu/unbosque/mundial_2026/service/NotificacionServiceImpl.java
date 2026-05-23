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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

/**
 * Implementación del servicio encargado de gestionar el envío y consulta de notificaciones
 * dentro de la plataforma del Mundial 2026.
 * Centraliza todas las notificaciones del sistema: confirmaciones de pago, reservas,
 * transferencias, reembolsos, pollas y órdenes de tienda.
 * Cada notificación se persiste en base de datos y, si el usuario tiene un token FCM registrado,
 * se envía también como notificación push a través de Firebase.
 * Los métodos de notificación se ejecutan de forma asíncrona para no bloquear el flujo principal
 */
@Service
public class NotificacionServiceImpl implements NotificacionService {

    private static final Logger log = LoggerFactory.getLogger(NotificacionServiceImpl.class);
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

    /**
     * Envía una notificación individual a un usuario específico,
     * persiste el registro y lanza el push por Firebase si tiene token FCM
     *
     * @param dto datos de la notificación: id de usuario, tipo, título, mensaje y canal
     * @throws UsuarioNotFoundException si el usuario no existe
     */
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

    /**
     * Envía una notificación masiva a todos los usuarios activos del sistema,
     * o a un subconjunto si se proporcionan ids específicos.
     * Persiste un registro por cada destinatario y registra la operación en auditoría
     *
     * @param dto datos de la notificación masiva: tipo, título, mensaje, canal y lista opcional de ids
     */
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

    /**
     * Retorna todas las notificaciones de un usuario sin paginación
     *
     * @param usuarioId id del usuario a consultar
     * @return lista de {@link NotificacionDTO} con todas las notificaciones del usuario
     * @throws UsuarioNotFoundException si el usuario no existe
     */
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

    /**
     * Marca una notificación específica como leída
     *
     * @param notificacionId id de la notificación a marcar
     * @throws RuntimeException si la notificación no existe
     */
    @Override
    @Transactional
    public void marcarLeida(Long notificacionId) {
        Notificacion notificacion = notificacionRepository.findById(notificacionId)
                .orElseThrow(() -> new RuntimeException("Notificación no encontrada"));
        notificacion.setLeida(true);
        notificacionRepository.save(notificacion);
    }

    /**
     * Marca todas las notificaciones de un usuario como leídas en una sola operación
     *
     * @param usuarioId id del usuario cuyas notificaciones se marcarán como leídas
     */
    @Override
    @Transactional
    public void marcarTodasLeidas(Long usuarioId) {
        notificacionRepository.marcarTodasLeidasPorUsuario(usuarioId);
    }

    /**
     * Envía una notificación a todos los usuarios activos que tengan en sus favoritos
     * alguna de las selecciones que participan en el partido indicado.
     * La operación queda registrada en auditoría
     *
     * @param partidoId id del partido que origina la notificación
     * @param tipo      tipo de la notificación
     * @param titulo    título de la notificación
     * @param mensaje   cuerpo del mensaje
     * @throws PartidoNotFoundException si el partido no existe
     */
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

    /**
     * Notifica al usuario que su cuenta fue creada exitosamente.
     * Se ejecuta de forma asíncrona al completar el registro
     *
     * @param usuario usuario recién registrado
     */
    @Async
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

    /**
     * Notifica al usuario que su perfil fue actualizado exitosamente
     *
     * @param usuario usuario que realizó la actualización
     */
    @Async
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

    /**
     * Notifica al usuario que su entrada fue pagada exitosamente,
     * incluyendo los detalles de zona, sector y fila asignados
     *
     * @param usuario   usuario comprador
     * @param partido   nombre del partido (local vs visitante)
     * @param categoria zona de la entrada (BARRA, GENERAL, PALCO, ESQUINA)
     * @param sector    sector asignado
     * @param fila      fila asignada
     */
    @Async
    @Override
    @Transactional
    public void notificarEntradaPagada(Usuario usuario, String partido, String categoria, String sector, String fila) {
        String titulo = "Entrada confirmada";
        String mensaje = "Tu entrada para " + partido + " fue confirmada. Categoria: " + categoria + ", Sector: "
                + sector + ", Fila: " + fila + ".";
        Notificacion notificacion = new Notificacion("ENTRADA_PAGADA", titulo, mensaje, CANAL_SISTEMA, ESTADO_ENVIADA,
                usuario);
        notificacionRepository.save(notificacion);
        enviarPush(usuario, titulo, mensaje);
    }

    /**
     * Notifica al usuario que su pago no pudo ser procesado
     * y lo invita a revisar los datos de su tarjeta
     *
     * @param usuario usuario al que falló el pago
     */
    @Async
    @Override
    @Transactional
    public void notificarEntradaPagoFallido(Usuario usuario) {
        String titulo = "Pago no procesado";
        String mensaje = "No pudimos procesar tu pago. Revisa los datos de tu tarjeta e intenta de nuevo.";
        Notificacion notificacion = new Notificacion("ENTRADA_PAGO_FALLIDO", titulo, mensaje, CANAL_SISTEMA,
                ESTADO_ENVIADA, usuario);
        notificacionRepository.save(notificacion);
        enviarPush(usuario, titulo, mensaje);
    }

    /**
     * Notifica al usuario que su compra en la tienda fue procesada exitosamente
     *
     * @param usuario usuario comprador
     * @param total   valor total de la orden
     */
    @Async
    @Override
    @Transactional
    public void notificarOrdenConfirmada(Usuario usuario, double total) {
        String titulo = "Compra exitosa";
        String mensaje = "Tu compra en tienda fue procesada exitosamente por $" + total + ".";
        Notificacion notificacion = new Notificacion("ORDEN_CONFIRMADA", titulo, mensaje, CANAL_SISTEMA, ESTADO_ENVIADA,
                usuario);
        notificacionRepository.save(notificacion);
        enviarPush(usuario, titulo, mensaje);
    }

    /**
     * Notifica al usuario que su reembolso fue procesado exitosamente
     *
     * @param usuario   usuario que solicitó el reembolso
     * @param entradaId id de la entrada reembolsada
     */
    @Async
    @Override
    @Transactional
    public void notificarEntradaReembolsada(Usuario usuario, Long entradaId) {
        String titulo = "Reembolso procesado";
        String mensaje = "Tu reembolso fue procesado exitosamente.";
        Notificacion notificacion = new Notificacion("ENTRADA_REEMBOLSADA", titulo, mensaje, CANAL_SISTEMA,
                ESTADO_ENVIADA, usuario);
        notificacionRepository.save(notificacion);
        enviarPush(usuario, titulo, mensaje);
    }

    /**
     * Notifica al usuario que su reembolso no pudo ser procesado
     * y lo invita a intentarlo más tarde
     *
     * @param usuario   usuario afectado
     * @param entradaId id de la entrada cuyo reembolso falló
     */
    @Async
    @Override
    @Transactional
    public void notificarEntradaReembolsoFallido(Usuario usuario, Long entradaId) {
        String titulo = "Reembolso no procesado";
        String mensaje = "No pudimos procesar tu reembolso. Intenta de nuevo mas tarde.";
        Notificacion notificacion = new Notificacion("ENTRADA_REEMBOLSO_FALLIDO", titulo, mensaje, CANAL_SISTEMA,
                ESTADO_ENVIADA, usuario);
        notificacionRepository.save(notificacion);
        enviarPush(usuario, titulo, mensaje);
    }

    /**
     * Notifica al usuario que transfirió una entrada a otro usuario
     *
     * @param usuarioOrigen usuario que realizó la transferencia
     * @param correoDestino correo del usuario destinatario
     * @param partido       nombre del partido de la entrada transferida
     */
    @Async
    @Override
    @Transactional
    public void notificarEntradaTransferida(Usuario usuarioOrigen, String correoDestino, String partido) {
        String titulo = "Entrada transferida";
        String mensaje = "Transferiste tu entrada para " + partido + " a " + correoDestino + ".";
        Notificacion notificacion = new Notificacion("ENTRADA_TRANSFERIDA", titulo, mensaje, CANAL_SISTEMA,
                ESTADO_ENVIADA, usuarioOrigen);
        notificacionRepository.save(notificacion);
        enviarPush(usuarioOrigen, titulo, mensaje);
    }

    /**
     * Notifica al usuario que recibió una entrada transferida por otro usuario
     *
     * @param usuarioDestino usuario que recibe la entrada
     * @param correoOrigen   correo del usuario que realizó la transferencia
     * @param partido        nombre del partido de la entrada recibida
     */
    @Async
    @Override
    @Transactional
    public void notificarEntradaRecibida(Usuario usuarioDestino, String correoOrigen, String partido) {
        String titulo = "Recibiste una entrada";
        String mensaje = correoOrigen + " te transfirió una entrada para " + partido + ".";
        Notificacion notificacion = new Notificacion("ENTRADA_RECIBIDA", titulo, mensaje, CANAL_SISTEMA, ESTADO_ENVIADA,
                usuarioDestino);
        notificacionRepository.save(notificacion);
        enviarPush(usuarioDestino, titulo, mensaje);
    }

    /**
     * Notifica al usuario que su reserva expiró por no completar el pago
     * dentro del tiempo límite de 15 minutos
     *
     * @param usuario usuario con la reserva expirada
     * @param partido nombre del partido de la reserva
     */
    @Async
    @Override
    @Transactional
    public void notificarReservaExpirada(Usuario usuario, String partido) {
        String titulo = "Reserva expirada";
        String mensaje = "Tu reserva para " + partido + " expiro. El cupo fue liberado.";
        Notificacion notificacion = new Notificacion("RESERVA_EXPIRADA", titulo, mensaje, CANAL_SISTEMA, ESTADO_ENVIADA,
                usuario);
        notificacionRepository.save(notificacion);
        enviarPush(usuario, titulo, mensaje);
    }

    /**
     * Advierte al usuario que su reserva está a punto de vencer (entre 4 y 6 minutos)
     * para que complete el pago antes de perder el cupo
     *
     * @param usuario usuario con la reserva próxima a vencer
     * @param partido nombre del partido de la reserva
     */
    @Async
    @Override
    @Transactional
    public void notificarReservaPorExpirar(Usuario usuario, String partido) {
        String titulo = "Tu reserva vence pronto";
        String mensaje = "Quedan 5 minutos para que tu reserva para " + partido
                + " expire. Completa el pago antes de que se libere el cupo.";
        Notificacion notificacion = new Notificacion("RESERVA_POR_EXPIRAR", titulo, mensaje, CANAL_SISTEMA,
                ESTADO_ENVIADA, usuario);
        notificacionRepository.save(notificacion);
        enviarPush(usuario, titulo, mensaje);
    }

    /**
     * Notifica a dos usuarios cuando uno se une a una polla:
     * al nuevo participante se le confirma su ingreso y al creador
     * se le informa que alguien nuevo se unió
     *
     * @param usuarioNuevo  usuario que se unió a la polla
     * @param creador       usuario que creó la polla
     * @param nombreApuesta nombre de la polla
     */
    @Async
    @Override
    @Transactional
    public void notificarApuestaUnirse(Usuario usuarioNuevo, Usuario creador, String nombreApuesta) {
        String tituloNuevo = "Te uniste a una polla";
        String mensajeNuevo = "Te uniste a la polla " + nombreApuesta + ".";
        Notificacion notifNuevo = new Notificacion("APUESTA_UNIRSE", tituloNuevo, mensajeNuevo, CANAL_SISTEMA,
                ESTADO_ENVIADA, usuarioNuevo);
        notificacionRepository.save(notifNuevo);
        enviarPush(usuarioNuevo, tituloNuevo, mensajeNuevo);

        String tituloCreador = "Nuevo participante";
        String mensajeCreador = usuarioNuevo.getCorreoUsuario() + " se unio a tu polla " + nombreApuesta + ".";
        Notificacion notifCreador = new Notificacion("APUESTA_UNIRSE", tituloCreador, mensajeCreador, CANAL_SISTEMA,
                ESTADO_ENVIADA, creador);
        notificacionRepository.save(notifCreador);
        enviarPush(creador, tituloCreador, mensajeCreador);
    }

    /**
     * Notifica a todos los participantes de una polla que fue cerrada
     * y que ya no se aceptan más pronósticos
     *
     * @param participantes lista de usuarios participantes de la polla
     * @param nombreApuesta nombre de la polla cerrada
     */
    @Async
    @Override
    @Transactional
    public void notificarApuestaCerrada(List<Usuario> participantes, String nombreApuesta) {
        String titulo = "Polla cerrada";
        String mensaje = "La polla " + nombreApuesta + " cerro. Ya no se aceptan pronosticos.";
        List<Notificacion> notificaciones = new ArrayList<>();
        for (Usuario participante : participantes) {
            notificaciones.add(
                    new Notificacion("APUESTA_CERRADA", titulo, mensaje, CANAL_SISTEMA, ESTADO_ENVIADA, participante));
            enviarPush(participante, titulo, mensaje);
        }
        notificacionRepository.saveAll(notificaciones);
    }

    /**
     * Notifica al usuario su posición final y puntos obtenidos
     * después de que se calcularon los resultados de una polla
     *
     * @param usuario       usuario participante
     * @param nombreApuesta nombre de la polla finalizada
     * @param posicion      posición final en el ranking
     * @param puntos        puntos totales obtenidos
     */
    @Async
    @Override
    @Transactional
    public void notificarPuntosCalculados(Usuario usuario, String nombreApuesta, int posicion, int puntos) {
        String titulo = "Resultados de tu polla";
        String mensaje = "Se calcularon los puntos de " + nombreApuesta + ". Tu posicion: #" + posicion + " con "
                + puntos + " pts.";
        Notificacion notificacion = new Notificacion("PUNTOS_CALCULADOS", titulo, mensaje, CANAL_SISTEMA,
                ESTADO_ENVIADA, usuario);
        notificacionRepository.save(notificacion);
        enviarPush(usuario, titulo, mensaje);
    }

    /**
     * Notifica al usuario que dejó productos en el carrito sin completar la compra
     *
     * @param usuario usuario con el carrito abandonado
     */
    @Async
    @Override
    @Transactional
    public void notificarCarritoAbandonado(Usuario usuario) {
        String titulo = "Tienes productos en tu carrito";
        String mensaje = "Dejaste productos en tu carrito. Completa tu compra antes de que se agoten.";
        Notificacion notificacion = new Notificacion("CARRITO_ABANDONADO", titulo, mensaje, CANAL_SISTEMA,
                ESTADO_ENVIADA, usuario);
        notificacionRepository.save(notificacion);
        enviarPush(usuario, titulo, mensaje);
    }

    /**
     * Notifica al usuario que el pago de su orden en la tienda no pudo ser procesado
     *
     * @param usuario usuario afectado
     */
    @Async
    @Override
    @Transactional
    public void notificarOrdenFallida(Usuario usuario) {
        String titulo = "Pago no procesado";
        String mensaje = "No pudimos procesar el pago de tu orden. Revisa los datos de tu tarjeta e intenta de nuevo.";
        Notificacion notificacion = new Notificacion("ORDEN_FALLIDA", titulo, mensaje, CANAL_SISTEMA, ESTADO_ENVIADA,
                usuario);
        notificacionRepository.save(notificacion);
        enviarPush(usuario, titulo, mensaje);
    }

    /**
     * Intenta enviar una notificación push a través de Firebase al dispositivo del usuario.
     * Si el usuario no tiene token FCM registrado, el envío se omite silenciosamente.
     * Si Firebase falla, registra una advertencia en el log sin interrumpir el flujo
     *
     * @param usuario usuario destinatario del push
     * @param titulo  título de la notificación push
     * @param mensaje cuerpo del mensaje push
     */
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
            log.warn("Fallo al enviar push a usuario {}: {}", usuario.getId(), e.getMessage());
        }
    }

    /**
     * Convierte una entidad {@link Notificacion} a su representación DTO de respuesta
     *
     * @param n entidad a convertir
     * @return {@link NotificacionDTO} con los datos de la notificación
     */
    private NotificacionDTO toDTO(Notificacion n) {
        return new NotificacionDTO(
                n.getId(), n.getTipo(), n.getTitulo(), n.getMensaje(),
                n.getCanal(), n.getEstado(), n.isLeida(), n.getFecha(),
                n.getUsuario().getId());
    }

    /**
     * Notifica al usuario que su reserva fue creada y que tiene 15 minutos
     * para completar el pago antes de que el cupo sea liberado
     *
     * @param usuario usuario que realizó la reserva
     * @param partido nombre del partido reservado
     */
    @Async
    @Override
    @Transactional
    public void notificarReservaCreada(Usuario usuario, String partido) {
        String titulo = "Reserva confirmada";
        String mensaje = "Tienes 15 minutos para pagar tu entrada para " + partido + ". ¡No pierdas el cupo!";
        Notificacion notificacion = new Notificacion("RESERVA_CREADA", titulo, mensaje, CANAL_SISTEMA, ESTADO_ENVIADA,
                usuario);
        notificacionRepository.save(notificacion);
        enviarPush(usuario, titulo, mensaje);
    }

    /**
     * Retorna las notificaciones de un usuario de forma paginada,
     * ordenadas de la más reciente a la más antigua
     *
     * @param usuarioId id del usuario a consultar
     * @param pageable  configuración de paginación y ordenamiento
     * @return página de {@link NotificacionDTO} con las notificaciones del usuario
     */
    @Override
    @Transactional(readOnly = true)
    public Page<NotificacionDTO> listarPorUsuarioPaginado(Long usuarioId, Pageable pageable) {
        return notificacionRepository.findByUsuarioIdOrderByFechaDesc(usuarioId, pageable)
                .map(this::toDTO);
    }

    /**
     * Retorna las notificaciones de un usuario dentro de un rango de fechas, paginadas
     * y ordenadas de la más reciente a la más antigua
     *
     * @param usuarioId id del usuario a consultar
     * @param desde     fecha y hora de inicio del rango
     * @param hasta     fecha y hora de fin del rango
     * @param pageable  configuración de paginación y ordenamiento
     * @return página de {@link NotificacionDTO} con las notificaciones en ese rango
     */
    @Override
    @Transactional(readOnly = true)
    public Page<NotificacionDTO> listarPorFecha(Long usuarioId, LocalDateTime desde,
            LocalDateTime hasta, Pageable pageable) {
        return notificacionRepository.findByUsuarioIdAndFechaBetweenOrderByFechaDesc(
                usuarioId, desde, hasta, pageable)
                .map(this::toDTO);
    }
}