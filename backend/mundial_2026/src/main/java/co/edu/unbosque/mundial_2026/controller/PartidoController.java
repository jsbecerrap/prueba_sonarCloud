package co.edu.unbosque.mundial_2026.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.edu.unbosque.mundial_2026.dto.response.EquipoMundialDTO;
import co.edu.unbosque.mundial_2026.dto.response.JugadorDTO;
import co.edu.unbosque.mundial_2026.dto.response.PartidoDTO;
import co.edu.unbosque.mundial_2026.dto.response.PosicionDTO;
import co.edu.unbosque.mundial_2026.dto.response.PreferenciaDTO;
import co.edu.unbosque.mundial_2026.entity.Partido;
import co.edu.unbosque.mundial_2026.service.PartidoService;

/**
 * Controlador REST que expone los endpoints del módulo de partidos —
 * permite consultar partidos, standings, selecciones y jugadores del Mundial,
 * además de filtrar por las preferencias personales del usuario autenticado
 *
 * <p>Base URL: {@code /api/partidos}</p>
 */
@RestController
@RequestMapping("/api/partidos")
public class PartidoController {

    private final PartidoService partidoService;

    public PartidoController(PartidoService partidoService) {
        this.partidoService = partidoService;
    }

    /**
     * {@code GET /api/partidos} — Lista todos los partidos del Mundial disponibles
     *
     * @return lista completa de partidos
     */
    @GetMapping
    public ResponseEntity<List<PartidoDTO>> listarPartidos() {
        return ResponseEntity.ok(partidoService.obtenerPartidos());
    }

    /**
     * {@code GET /api/partidos/equipo/{equipoId}} — Retorna los partidos
     * en los que participa un equipo específico
     *
     * @param equipoId ID del equipo
     * @return lista de partidos del equipo
     */
    @GetMapping("/equipo/{equipoId}")
    public ResponseEntity<List<PartidoDTO>> obtenerPartidosPorEquipo(@PathVariable Long equipoId) {
        return ResponseEntity.ok(partidoService.obtenerPartidosPorEquipo(equipoId));
    }

    /**
     * {@code GET /api/partidos/standings} — Retorna las tablas de posiciones
     * del Mundial agrupadas por grupo
     *
     * @return lista de grupos donde cada grupo contiene su tabla de posiciones
     */
    @GetMapping("/standings")
    public ResponseEntity<List<List<PosicionDTO>>> obtenerStandings() {
        return ResponseEntity.ok(partidoService.obtenerStandings());
    }

    /**
     * {@code GET /api/partidos/selecciones} — Retorna todas las selecciones
     * participantes en el Mundial con su información básica
     *
     * @return lista de selecciones del mundial
     */
    @GetMapping("/selecciones")
    public ResponseEntity<List<EquipoMundialDTO>> obtenerSelecciones() {
        return ResponseEntity.ok(partidoService.obtenerSelecciones());
    }

    /**
     * {@code GET /api/partidos/selecciones/{equipoId}/jugadores} — Retorna los jugadores
     * que pertenecen a un equipo específico
     *
     * @param equipoId ID del equipo
     * @return lista de jugadores del equipo
     */
    @GetMapping("/selecciones/{equipoId}/jugadores")
    public ResponseEntity<List<JugadorDTO>> obtenerJugadoresPorEquipo(@PathVariable Long equipoId) {
        return ResponseEntity.ok(partidoService.obtenerJugadoresPorEquipo(equipoId));
    }

    /**
     * {@code GET /api/partidos/fecha/{fecha}} — Filtra los partidos que se juegan
     * en una fecha específica
     *
     * @param fecha fecha en formato {@code YYYY-MM-DD}
     * @return lista de partidos de esa fecha
     */
    @GetMapping("/fecha/{fecha}")
    public ResponseEntity<List<PartidoDTO>> obtenerPartidosPorFecha(@PathVariable String fecha) {
        return ResponseEntity.ok(partidoService.obtenerPartidosPorFecha(fecha));
    }

    /**
     * {@code GET /api/partidos/envivo} — Retorna los partidos que se están
     * jugando en este momento
     *
     * @return lista de partidos en vivo
     */
    @GetMapping("/envivo")
    public ResponseEntity<List<PartidoDTO>> obtenerPartidosEnVivo() {
        return ResponseEntity.ok(partidoService.obtenerPartidosEnVivo());
    }

    /**
     * {@code GET /api/partidos/preferencias/selecciones} — Retorna los partidos
     * de las selecciones que el usuario autenticado marcó como favoritas en su perfil
     *
     * @return lista de partidos de sus selecciones favoritas
     */
    @GetMapping("/preferencias/selecciones")
    public ResponseEntity<List<PartidoDTO>> obtenerPartidosPorSeleccionesFav() {
        final String correo = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(partidoService.obtenerPartidosPorSeleccionesFav(correo));
    }

    /**
     * {@code GET /api/partidos/preferencias/estadios} — Retorna los partidos
     * que se juegan en los estadios favoritos del usuario autenticado
     *
     * @return lista de partidos en sus estadios favoritos
     */
    @GetMapping("/preferencias/estadios")
    public ResponseEntity<List<PartidoDTO>> obtenerPartidosPorEstadiosFav() {
        final String correo = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(partidoService.obtenerPartidosPorEstadiosFav(correo));
    }

    /**
     * {@code GET /api/partidos/preferencias/ciudades} — Retorna los partidos
     * que se juegan en las ciudades favoritas del usuario autenticado
     *
     * @return lista de partidos en sus ciudades favoritas
     */
    @GetMapping("/preferencias/ciudades")
    public ResponseEntity<List<PartidoDTO>> obtenerPartidosPorCiudadesFav() {
        final String correo = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(partidoService.obtenerPartidosPorCiudadesFav(correo));
    }

    /**
     * {@code GET /api/partidos/sincronizar/{liga}/{temporada}/{fecha}} — Sincroniza
     * los partidos de una liga, temporada y fecha específicas desde la API externa
     * y los persiste en la base de datos — solo ADMIN
     *
     * @param liga      ID de la liga en la API externa
     * @param temporada año de la temporada
     * @param fecha     fecha en formato {@code YYYY-MM-DD}
     * @return número de partidos sincronizados
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/sincronizar/{liga}/{temporada}/{fecha}")
    public ResponseEntity<Integer> sincronizarPorFechaYLiga(
            @PathVariable int liga,
            @PathVariable int temporada,
            @PathVariable String fecha) {
        return ResponseEntity.ok(partidoService.sincronizarPorFechaYLiga(fecha, liga, temporada));
    }

    /**
     * {@code PUT /api/partidos/{id}/resultado/{gol1}/{gol2}/{estado}} — Actualiza
     * el resultado de un partido con los goles de cada equipo y el estado actual
     * (ej: en juego, finalizado)
     *
     * @param id     ID del partido
     * @param gol1   goles del equipo local
     * @param gol2   goles del equipo visitante
     * @param estado código del estado del partido
     * @return número de registros actualizados
     */
    @PutMapping("/{id}/resultado/{gol1}/{gol2}/{estado}")
    public ResponseEntity<Integer> actualizarResultado(
            @PathVariable Long id,
            @PathVariable int gol1,
            @PathVariable int gol2,
            @PathVariable int estado) {
        return ResponseEntity.ok(partidoService.actualizarResultado(id, gol1, gol2, estado));
    }

    /**
     * {@code GET /api/partidos/{fixtureId}} — Obtiene el detalle completo
     * de un partido por su ID de fixture
     *
     * @param fixtureId ID del partido en la API externa
     * @return datos completos del partido
     */
    @GetMapping("/{fixtureId}")
    public ResponseEntity<PartidoDTO> obtenerPorId(@PathVariable Long fixtureId) {
        return ResponseEntity.ok(partidoService.obtenerPartidoPorId(fixtureId));
    }

    /**
     * {@code GET /api/partidos/catalogo/selecciones} — Retorna el catálogo de selecciones
     * disponibles para que el usuario las marque como favoritas en su perfil
     *
     * @return lista de selecciones con su ID y nombre
     */
    @GetMapping("/catalogo/selecciones")
    public ResponseEntity<List<PreferenciaDTO>> obtenerCatalogoSelecciones() {
        return ResponseEntity.ok(partidoService.obtenerCatalogoSelecciones());
    }

    /**
     * {@code GET /api/partidos/bd/todos} — Lista todos los partidos almacenados
     * directamente en la base de datos como entidades — solo ADMIN
     *
     * @return lista de entidades {@link Partido}
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/bd/todos")
    public ResponseEntity<List<Partido>> listarDesdeBD() {
        return ResponseEntity.ok(partidoService.listarDesdeBD());
    }
}