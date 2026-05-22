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

@RestController
@RequestMapping("/api/auditoria")
@PreAuthorize("hasRole('ADMIN')")
public class EventoAuditoriaController {

    private final EventoAuditoriaService eventoService;

    public EventoAuditoriaController(EventoAuditoriaService eventoService) {
        this.eventoService = eventoService;
    }

    private Pageable buildPageable(int page, int size) {
        int safePage = Math.max(page, 0);
      int safeSize = Math.clamp(size, 1, 100);
        return PageRequest.of(safePage, safeSize, Sort.by(Sort.Direction.DESC, "fecha"));
    }

    @GetMapping("/todos")
    public ResponseEntity<Page<EventoAuditoriaDTO>> obtenerTodos(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        return ResponseEntity.ok(eventoService.obtenerTodos(buildPageable(page, size)));
    }

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

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<Page<EventoAuditoriaDTO>> buscarPorUsuario(
            @PathVariable Long usuarioId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        return ResponseEntity.ok(eventoService.buscarPorUsuario(usuarioId, buildPageable(page, size)));
    }

    @GetMapping("/tipo/{tipo}")
    public ResponseEntity<Page<EventoAuditoriaDTO>> buscarPorTipo(
            @PathVariable String tipo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        return ResponseEntity.ok(eventoService.buscarPorTipo(tipo, buildPageable(page, size)));
    }

    @GetMapping("/correlacion/{correlacion}")
    public ResponseEntity<Page<EventoAuditoriaDTO>> buscarPorCorrelacion(
            @PathVariable String correlacion,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        return ResponseEntity.ok(eventoService.buscarPorCorrelacion(correlacion, buildPageable(page, size)));
    }

    @GetMapping("/fecha")
    public ResponseEntity<Page<EventoAuditoriaDTO>> buscarPorFecha(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        return ResponseEntity.ok(eventoService.buscarPorFecha(fechaInicio, fechaFin, buildPageable(page, size)));
    }

    @GetMapping("/entidad/{entidad}")
    public ResponseEntity<Page<EventoAuditoriaDTO>> buscarPorEntidad(
            @PathVariable String entidad,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        return ResponseEntity.ok(eventoService.buscarPorEntidad(entidad, buildPageable(page, size)));
    }
}