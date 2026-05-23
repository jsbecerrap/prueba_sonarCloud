package co.edu.unbosque.mundial_2026.controller;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import co.edu.unbosque.mundial_2026.dto.EventoAuditoriaDTO;
import co.edu.unbosque.mundial_2026.service.EventoAuditoriaService;

/**
 * Controlador REST que expone los endpoints de consulta del registro de auditoría —
 * todos los endpoints son exclusivos para administradores y retornan resultados paginados
 * ordenados por fecha descendente (más recientes primero)
 *
 * <p>Base URL: {@code /api/auditoria}</p>
 */
@RestController
@RequestMapping("/api/auditoria")
@PreAuthorize("hasRole('ADMIN')")
public class EventoAuditoriaController {

    private final EventoAuditoriaService eventoService;

    public EventoAuditoriaController(EventoAuditoriaService eventoService) {
        this.eventoService = eventoService;
    }

    /**
     * Construye un objeto {@link Pageable} con validaciones de seguridad para evitar
     * valores inválidos en la paginación — la página mínima es 0 y el tamaño
     * se limita entre 1 y 100 registros para proteger el rendimiento del sistema
     *
     * @param page número de página solicitado
     * @param size cantidad de registros por página
     * @return configuración de paginación lista para usar en las consultas
     */
    private Pageable buildPageable(int page, int size) {
        int safePage = Math.max(page, 0);
        int safeSize = Math.clamp(size, 1, 100);
        return PageRequest.of(safePage, safeSize, Sort.by(Sort.Direction.DESC, "fecha"));
    }

    /**
     * {@code GET /api/auditoria/todos} — Retorna todos los eventos de auditoría
     * del sistema de forma paginada
     *
     * @param page número de página (por defecto 0)
     * @param size registros por página (por defecto 50)
     * @return página de eventos de auditoría
     */
    @GetMapping("/todos")
    public ResponseEntity<Page<EventoAuditoriaDTO>> obtenerTodos(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        return ResponseEntity.ok(eventoService.obtenerTodos(buildPageable(page, size)));
    }

    /**
     * {@code GET /api/auditoria/buscar} — Búsqueda avanzada con múltiples filtros opcionales
     * que se pueden combinar libremente: usuario, tipos de evento y rango de fechas
     *
     * <p>Todos los parámetros son opcionales — si no se envía ninguno retorna todos los eventos.
     * Las fechas deben enviarse en formato ISO 8601 (ej: {@code 2026-01-15T10:30:00})</p>
     *
     * @param usuarioId   ID del usuario a filtrar, opcional
     * @param tipos       tipos de evento separados por coma (ej: {@code LOGIN,COMPRA}), opcional
     * @param fechaInicio límite inferior del rango de fechas, opcional
     * @param fechaFin    límite superior del rango de fechas, opcional
     * @param page        número de página (por defecto 0)
     * @param size        registros por página (por defecto 50)
     * @return página de eventos que cumplen los filtros indicados
     */
    @GetMapping("/buscar")
    public ResponseEntity<Page<EventoAuditoriaDTO>> buscar(
            @RequestParam(required = false) Long usuarioId,
            @RequestParam(required = false) String tipos,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        return ResponseEntity.ok(eventoService.buscarConFiltros(
                usuarioId, tipos, fechaInicio, fechaFin,
                buildPageable(page, size)));
    }

    /**
     * {@code GET /api/auditoria/usuario/{usuarioId}} — Retorna todos los eventos
     * generados por un usuario específico
     *
     * @param usuarioId ID del usuario
     * @param page      número de página (por defecto 0)
     * @param size      registros por página (por defecto 50)
     * @return página de eventos del usuario
     */
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<Page<EventoAuditoriaDTO>> buscarPorUsuario(
            @PathVariable Long usuarioId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        return ResponseEntity.ok(eventoService.buscarPorUsuario(usuarioId, buildPageable(page, size)));
    }

    /**
     * {@code GET /api/auditoria/tipo/{tipo}} — Filtra los eventos por tipo de acción
     *
     * @param tipo tipo de evento a buscar (ej: LOGIN, COMPRA, MODIFICACION)
     * @param page número de página (por defecto 0)
     * @param size registros por página (por defecto 50)
     * @return página de eventos del tipo indicado
     */
    @GetMapping("/tipo/{tipo}")
    public ResponseEntity<Page<EventoAuditoriaDTO>> buscarPorTipo(
            @PathVariable String tipo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        return ResponseEntity.ok(eventoService.buscarPorTipo(tipo, buildPageable(page, size)));
    }

    /**
     * {@code GET /api/auditoria/correlacion/{correlacion}} — Agrupa y retorna todos los
     * eventos que comparten un identificador de correlación, permitiendo rastrear
     * el flujo completo de una operación a través del sistema
     *
     * @param correlacion identificador de correlación a buscar
     * @param page        número de página (por defecto 0)
     * @param size        registros por página (por defecto 50)
     * @return página de eventos con esa correlación
     */
    @GetMapping("/correlacion/{correlacion}")
    public ResponseEntity<Page<EventoAuditoriaDTO>> buscarPorCorrelacion(
            @PathVariable String correlacion,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        return ResponseEntity.ok(eventoService.buscarPorCorrelacion(correlacion, buildPageable(page, size)));
    }

    /**
     * {@code GET /api/auditoria/fecha} — Filtra los eventos dentro de un rango de fechas —
     * ambas fechas son obligatorias y deben enviarse en formato ISO 8601
     *
     * @param fechaInicio límite inferior del rango (inclusive)
     * @param fechaFin    límite superior del rango (inclusive)
     * @param page        número de página (por defecto 0)
     * @param size        registros por página (por defecto 50)
     * @return página de eventos dentro del rango indicado
     */
    @GetMapping("/fecha")
    public ResponseEntity<Page<EventoAuditoriaDTO>> buscarPorFecha(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        return ResponseEntity.ok(eventoService.buscarPorFecha(fechaInicio, fechaFin, buildPageable(page, size)));
    }

    /**
     * {@code GET /api/auditoria/entidad/{entidad}} — Filtra los eventos asociados
     * a una entidad del dominio específica
     *
     * @param entidad nombre de la entidad (ej: Orden, Entrada, Usuario)
     * @param page    número de página (por defecto 0)
     * @param size    registros por página (por defecto 50)
     * @return página de eventos de esa entidad
     */
    @GetMapping("/entidad/{entidad}")
    public ResponseEntity<Page<EventoAuditoriaDTO>> buscarPorEntidad(
            @PathVariable String entidad,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        return ResponseEntity.ok(eventoService.buscarPorEntidad(entidad, buildPageable(page, size)));
    }
}