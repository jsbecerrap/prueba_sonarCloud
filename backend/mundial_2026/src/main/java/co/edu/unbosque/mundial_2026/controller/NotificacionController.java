package co.edu.unbosque.mundial_2026.controller;

import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RequestParam;
import java.time.LocalDateTime;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.edu.unbosque.mundial_2026.dto.NotificacionDTO;
import co.edu.unbosque.mundial_2026.dto.request.NotificacionMasivaRequestDTO;
import co.edu.unbosque.mundial_2026.dto.request.NotificacionRequestDTO;
import co.edu.unbosque.mundial_2026.service.NotificacionService;
import co.edu.unbosque.mundial_2026.service.UsuarioService;

/**
 * Controlador REST que expone los endpoints del módulo de notificaciones —
 * permite a los usuarios consultar y gestionar sus notificaciones, y a los
 * administradores enviar alertas individuales, masivas o por partido
 *
 * <p>El usuario autenticado se identifica a través del {@link SecurityContextHolder},
 * que extrae el correo directamente del token JWT sin necesidad de recibirlo
 * como parámetro en la petición</p>
 *
 * <p>Base URL: {@code /api/notificaciones}</p>
 */
@RestController
@RequestMapping("/api/notificaciones")
public class NotificacionController {

    private final NotificacionService notificacionService;
    private final UsuarioService usuarioService;

    public NotificacionController(NotificacionService notificacionService,
            UsuarioService usuarioService) {
        this.notificacionService = notificacionService;
        this.usuarioService = usuarioService;
    }

    /**
     * {@code GET /api/notificaciones} — Lista las notificaciones del usuario autenticado
     * de forma paginada, ordenadas de más reciente a más antigua
     *
     * @param page número de página (por defecto 0)
     * @param size registros por página (por defecto 20)
     * @return página de notificaciones del usuario
     */
    @GetMapping
    public ResponseEntity<Page<NotificacionDTO>> listarMisNotificaciones(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        String correo = SecurityContextHolder.getContext().getAuthentication().getName();
        Long usuarioId = usuarioService.obtenerPorCorreo(correo).getId();
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(notificacionService.listarPorUsuarioPaginado(usuarioId, pageable));
    }

    /**
     * {@code PUT /api/notificaciones/{id}/leida} — Marca una notificación específica
     * como leída
     *
     * @param id ID de la notificación
     * @return respuesta vacía con HTTP 204
     */
    @PutMapping("/{id}/leida")
    public ResponseEntity<Void> marcarLeida(@PathVariable Long id) {
        notificacionService.marcarLeida(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * {@code PUT /api/notificaciones/leidas} — Marca todas las notificaciones
     * del usuario autenticado como leídas en una sola operación
     *
     * @return respuesta vacía con HTTP 204
     */
    @PutMapping("/leidas")
    public ResponseEntity<Void> marcarTodasLeidas() {
        String correo = SecurityContextHolder.getContext().getAuthentication().getName();
        Long usuarioId = usuarioService.obtenerPorCorreo(correo).getId();
        notificacionService.marcarTodasLeidas(usuarioId);
        return ResponseEntity.noContent().build();
    }

    /**
     * {@code POST /api/notificaciones/enviar} — Envía una notificación individual
     * a un usuario específico — solo ADMIN
     *
     * @param dto datos de la notificación: destinatario, título y mensaje
     * @return respuesta vacía con HTTP 204
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/enviar")
    public ResponseEntity<Void> enviarIndividual(@RequestBody NotificacionRequestDTO dto) {
        notificacionService.enviarNotificacion(dto);
        return ResponseEntity.noContent().build();
    }

    /**
     * {@code POST /api/notificaciones/masiva} — Envía una misma notificación
     * a múltiples usuarios a la vez — solo ADMIN
     *
     * @param dto datos de la notificación con la lista de destinatarios
     * @return respuesta vacía con HTTP 204
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/masiva")
    public ResponseEntity<Void> enviarMasiva(@RequestBody NotificacionMasivaRequestDTO dto) {
        notificacionService.enviarMasiva(dto);
        return ResponseEntity.noContent().build();
    }

    /**
     * {@code POST /api/notificaciones/partido/{partidoId}} — Notifica a todos los usuarios
     * con entrada en un partido sobre un evento específico (ej: cambio de hora, cancelación) — solo ADMIN
     *
     * @param partidoId ID del partido cuyos asistentes serán notificados
     * @param dto       datos de la notificación: tipo, título y mensaje
     * @return respuesta vacía con HTTP 204
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/partido/{partidoId}")
    public ResponseEntity<Void> notificarPorPartido(
            @PathVariable Long partidoId,
            @RequestBody NotificacionRequestDTO dto) {
        notificacionService.notificarPorPartido(
                partidoId, dto.getTipo(), dto.getTitulo(), dto.getMensaje());
        return ResponseEntity.noContent().build();
    }

    /**
     * {@code GET /api/notificaciones/buscar} — Filtra las notificaciones del usuario
     * autenticado dentro de un rango de fechas — las fechas se reciben como texto
     * en formato {@code YYYY-MM-DD} y se convierten internamente a inicio y fin del día
     *
     * @param desde fecha de inicio en formato {@code YYYY-MM-DD}
     * @param hasta fecha de fin en formato {@code YYYY-MM-DD}
     * @param page  número de página (por defecto 0)
     * @param size  registros por página (por defecto 20)
     * @return página de notificaciones dentro del rango indicado
     */
    @GetMapping("/buscar")
    public ResponseEntity<Page<NotificacionDTO>> buscarPorFecha(
            @RequestParam String desde,
            @RequestParam String hasta,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        String correo = SecurityContextHolder.getContext().getAuthentication().getName();
        Long usuarioId = usuarioService.obtenerPorCorreo(correo).getId();
        LocalDateTime fechaDesde = LocalDateTime.parse(desde + "T00:00:00");
        LocalDateTime fechaHasta = LocalDateTime.parse(hasta + "T23:59:59");
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(notificacionService.listarPorFecha(
                usuarioId, fechaDesde, fechaHasta, pageable));
    }

    /**
     * {@code GET /api/notificaciones/sin-leer/conteo} — Retorna la cantidad de
     * notificaciones no leídas del usuario autenticado — útil para mostrar
     * el contador de alertas pendientes en la interfaz
     *
     * @return mapa con la clave {@code "total"} y el número de notificaciones sin leer
     */
    @GetMapping("/sin-leer/conteo")
    public ResponseEntity<Map<String, Long>> contarSinLeer() {
        String correo = SecurityContextHolder.getContext().getAuthentication().getName();
        Long usuarioId = usuarioService.obtenerPorCorreo(correo).getId();
        long total = notificacionService.listarPorUsuario(usuarioId)
                .stream().filter(n -> !n.isLeida()).count();
        return ResponseEntity.ok(Map.of("total", total));
    }
}