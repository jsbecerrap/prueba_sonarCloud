package co.edu.unbosque.mundial_2026.controller;

import java.util.List;
import java.util.regex.Pattern;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.edu.unbosque.mundial_2026.dto.ApuestaConParticipantesDTO;
import co.edu.unbosque.mundial_2026.dto.ApuestaDTO;
import co.edu.unbosque.mundial_2026.dto.ParticipacionDTO;
import co.edu.unbosque.mundial_2026.dto.PronosticoDTO;
import co.edu.unbosque.mundial_2026.dto.request.ApuestaRequestDTO;
import co.edu.unbosque.mundial_2026.dto.request.PronosticoRequestDTO;
import co.edu.unbosque.mundial_2026.exception.CodigoInvalidoException;
import co.edu.unbosque.mundial_2026.service.ApuestaService;
import jakarta.validation.Valid;

/**
 * Controlador REST que expone los endpoints del módulo de apuestas —
 * permite crear apuestas, unirse con código de invitación, registrar pronósticos
 * y consultar rankings y puntuaciones
 *
 * <p>Base URL: {@code /api/apuestas}</p>
 */
@RestController
@RequestMapping("/api/apuestas")
public class ApuestaRestController {

    /**
     * Patrón de validación del código de invitación — acepta UUID estándar
     * o códigos alfanuméricos de entre 4 y 40 caracteres
     */
    private static final Pattern CODIGO_PATTERN = Pattern.compile(
        "^([0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}|[A-Za-z0-9]{4,40})$"
    );

    private static final String ROL_ADMIN = "hasRole('ADMIN')";
    private final ApuestaService apuestaService;

    public ApuestaRestController(ApuestaService apuestaService) {
        this.apuestaService = apuestaService;
    }

    /**
     * {@code POST /api/apuestas/crear} — Crea una nueva apuesta con su código de invitación
     *
     * @param dto datos de configuración de la apuesta
     * @return apuesta creada con su código único
     */
    @PostMapping("/crear")
    public ResponseEntity<ApuestaDTO> crearApuesta(@Valid @RequestBody ApuestaRequestDTO dto) {
        return ResponseEntity.ok(apuestaService.crearApuesta(dto));
    }

    /**
     * {@code POST /api/apuestas/unirse/{usuarioId}} — Inscribe a un usuario en una apuesta
     * usando el código de invitación — valida el formato del código antes de procesarlo
     *
     * @param usuarioId ID del usuario que quiere unirse
     * @param codigo    código de invitación de la apuesta
     * @return apuesta actualizada con el nuevo participante
     * @throws CodigoInvalidoException si el código no cumple el formato esperado
     */
    @PostMapping("/unirse/{usuarioId}")
    public ResponseEntity<ApuestaDTO> unirseApuesta(@PathVariable Long usuarioId, @RequestBody String codigo) {
        String codigoLimpio = codigo == null ? "" : codigo.replace("\"", "").trim();
        if (!CODIGO_PATTERN.matcher(codigoLimpio).matches()) {
            throw new CodigoInvalidoException("El código de invitación tiene un formato inválido");
        }
        return ResponseEntity.ok(apuestaService.unirseApuesta(codigoLimpio, usuarioId));
    }

    /**
     * {@code POST /api/apuestas/pronostico} — Registra el pronóstico de un participante
     * para un partido dentro de una apuesta
     *
     * @param dto datos del pronóstico: partido, marcador esperado y apuesta
     * @return pronóstico registrado
     */
    @PostMapping("/pronostico")
    public ResponseEntity<PronosticoDTO> registrarPronostico(@Valid @RequestBody PronosticoRequestDTO dto) {
        return ResponseEntity.ok(apuestaService.registrarPronostico(dto));
    }

    /**
     * {@code GET /api/apuestas/ranking/{apuestaId}} — Retorna los participantes de una
     * apuesta ordenados por puntos de mayor a menor
     *
     * @param apuestaId ID de la apuesta
     * @return ranking actualizado de participantes
     */
    @GetMapping("/ranking/{apuestaId}")
    public ResponseEntity<List<ParticipacionDTO>> obtenerRanking(@PathVariable Long apuestaId) {
        return ResponseEntity.ok(apuestaService.obtenerRanking(apuestaId));
    }

    /**
     * {@code POST /api/apuestas/cerrar/{apuestaId}} — Cierra manualmente una apuesta
     * impidiendo nuevas participaciones y pronósticos — solo ADMIN
     *
     * @param apuestaId ID de la apuesta a cerrar
     * @return apuesta con estado actualizado a cerrado
     */
    @PreAuthorize(ROL_ADMIN)
    @PostMapping("/cerrar/{apuestaId}")
    public ResponseEntity<ApuestaDTO> cerrarApuesta(@PathVariable Long apuestaId) {
        return ResponseEntity.ok(apuestaService.cerrarApuesta(apuestaId));
    }

    /**
     * {@code GET /api/apuestas/puntos/{apuestaId}} — Calcula y retorna los puntos
     * de todos los pronósticos comparándolos con los resultados reales — solo ADMIN
     *
     * @param apuestaId ID de la apuesta a evaluar
     * @return lista de pronósticos con puntos actualizados
     */
    @PreAuthorize(ROL_ADMIN)
    @GetMapping("/puntos/{apuestaId}")
    public ResponseEntity<List<PronosticoDTO>> calcularPuntos(@PathVariable Long apuestaId) {
        return ResponseEntity.ok(apuestaService.calcularPuntos(apuestaId));
    }

    /**
     * {@code GET /api/apuestas/usuario/{usuarioId}} — Lista todas las apuestas
     * en las que participa un usuario
     *
     * @param usuarioId ID del usuario
     * @return lista de apuestas del usuario
     */
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<ApuestaDTO>> listarApuestasPorUsuario(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(apuestaService.listarApuestasPorUsuario(usuarioId));
    }

    /**
     * {@code GET /api/apuestas/{apuestaId}} — Obtiene el detalle completo de una apuesta
     *
     * @param apuestaId ID de la apuesta
     * @return datos completos de la apuesta
     */
    @GetMapping("/{apuestaId}")
    public ResponseEntity<ApuestaDTO> obtenerApuesta(@PathVariable Long apuestaId) {
        return ResponseEntity.ok(apuestaService.obtenerApuesta(apuestaId));
    }

    /**
     * {@code GET /api/apuestas/participantes/{apuestaId}} — Lista todos los participantes
     * registrados en una apuesta
     *
     * @param apuestaId ID de la apuesta
     * @return lista de participaciones con sus datos
     */
    @GetMapping("/participantes/{apuestaId}")
    public ResponseEntity<List<ParticipacionDTO>> listarParticipantes(@PathVariable Long apuestaId) {
        return ResponseEntity.ok(apuestaService.listarParticipantes(apuestaId));
    }

    /**
     * {@code GET /api/apuestas/verificar/{pronosticoId}} — Consulta el estado de un
     * pronóstico e indica si ya fue evaluado contra el resultado real del partido
     *
     * @param pronosticoId ID del pronóstico
     * @return pronóstico con su estado de verificación
     */
    @GetMapping("/verificar/{pronosticoId}")
    public ResponseEntity<PronosticoDTO> verificarPronostico(@PathVariable Long pronosticoId) {
        return ResponseEntity.ok(apuestaService.verificarPronostico(pronosticoId));
    }

    /**
     * {@code GET /api/apuestas/mis-pronosticos/{apuestaId}/{usuarioId}} — Retorna
     * los pronósticos que un usuario registró dentro de una apuesta específica
     *
     * @param apuestaId ID de la apuesta
     * @param usuarioId ID del usuario
     * @return lista de pronósticos del usuario en esa apuesta
     */
    @GetMapping("/mis-pronosticos/{apuestaId}/{usuarioId}")
    public ResponseEntity<List<PronosticoDTO>> misPronosticos(
            @PathVariable Long apuestaId, @PathVariable Long usuarioId) {
        return ResponseEntity.ok(apuestaService.misPronosticos(apuestaId, usuarioId));
    }

    /**
     * {@code PUT /api/apuestas/pronostico/{pronosticoId}} — Edita un pronóstico existente
     * validando que el usuario autenticado sea su dueño
     *
     * @param pronosticoId ID del pronóstico a modificar
     * @param dto          nuevos datos del pronóstico
     * @param username     correo del usuario autenticado extraído del token JWT
     * @return pronóstico actualizado
     */
    @PutMapping("/pronostico/{pronosticoId}")
    public ResponseEntity<PronosticoDTO> editarPronostico(
            @PathVariable Long pronosticoId,
            @Valid @RequestBody PronosticoRequestDTO dto,
            @AuthenticationPrincipal String username) {
        return ResponseEntity.ok(apuestaService.editarPronostico(pronosticoId, dto, username));
    }

    /**
     * {@code DELETE /api/apuestas/pronostico/{pronosticoId}} — Elimina un pronóstico
     * validando que el usuario autenticado sea su dueño
     *
     * @param pronosticoId ID del pronóstico a eliminar
     * @param username     correo del usuario autenticado extraído del token JWT
     * @return respuesta vacía con HTTP 204
     */
    @DeleteMapping("/pronostico/{pronosticoId}")
    public ResponseEntity<Void> eliminarPronostico(
            @PathVariable Long pronosticoId,
            @AuthenticationPrincipal String username) {
        apuestaService.eliminarPronostico(pronosticoId, username);
        return ResponseEntity.noContent().build();
    }

    /**
     * {@code GET /api/apuestas/puntos-parciales/{apuestaId}} — Calcula los puntos
     * considerando solo los partidos que ya tienen resultado, sin esperar el cierre
     *
     * @param apuestaId ID de la apuesta
     * @return lista de pronósticos con puntos parciales
     */
    @GetMapping("/puntos-parciales/{apuestaId}")
    public ResponseEntity<List<PronosticoDTO>> calcularPuntosParciales(@PathVariable Long apuestaId) {
        return ResponseEntity.ok(apuestaService.calcularPuntosParciales(apuestaId));
    }

    /**
     * {@code GET /api/apuestas/todas} — Lista todas las apuestas del sistema
     * sin ningún filtro — solo ADMIN
     *
     * @return lista completa de apuestas
     */
    @PreAuthorize(ROL_ADMIN)
    @GetMapping("/todas")
    public ResponseEntity<List<ApuestaDTO>> listarTodas() {
        return ResponseEntity.ok(apuestaService.listarTodas());
    }

    /**
     * {@code DELETE /api/apuestas/{apuestaId}} — Elimina permanentemente una apuesta
     * y todos sus datos asociados — solo ADMIN
     *
     * @param apuestaId ID de la apuesta a eliminar
     * @return respuesta vacía con HTTP 204
     */
    @PreAuthorize(ROL_ADMIN)
    @DeleteMapping("/{apuestaId}")
    public ResponseEntity<Void> eliminarApuesta(@PathVariable Long apuestaId) {
        apuestaService.eliminarApuesta(apuestaId);
        return ResponseEntity.noContent().build();
    }

    /**
     * {@code GET /api/apuestas/usuario/{usuarioId}/completo} — Lista las apuestas
     * de un usuario incluyendo el detalle completo de participantes y pronósticos
     *
     * @param usuarioId ID del usuario
     * @return lista de apuestas con participantes anidados
     */
    @GetMapping("/usuario/{usuarioId}/completo")
    public ResponseEntity<List<ApuestaConParticipantesDTO>> listarApuestasPorUsuarioCompleto(
            @PathVariable Long usuarioId) {
        return ResponseEntity.ok(apuestaService.listarApuestasPorUsuarioCompleto(usuarioId));
    }
}