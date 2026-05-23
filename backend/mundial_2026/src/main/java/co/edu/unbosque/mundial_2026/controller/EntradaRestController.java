package co.edu.unbosque.mundial_2026.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import co.edu.unbosque.mundial_2026.dto.CuposZonaDTO;
import co.edu.unbosque.mundial_2026.dto.PartidoCapacidadDTO;
import co.edu.unbosque.mundial_2026.dto.request.EntradaRequestDTO;
import co.edu.unbosque.mundial_2026.dto.request.TransferenciaRequestDTO;
import co.edu.unbosque.mundial_2026.dto.response.EntradaResponseDTO;
import co.edu.unbosque.mundial_2026.service.EntradaService;

/**
 * Controlador REST que expone los endpoints del módulo de entradas —
 * gestiona el ciclo completo de una entrada: reserva, pago, cancelación,
 * transferencia y reembolso
 *
 * <p>El usuario autenticado se identifica automáticamente a través del token JWT
 * en los endpoints que lo requieren, sin necesidad de enviarlo en el cuerpo</p>
 *
 * <p>Base URL: {@code /api/entradas}</p>
 */
@RestController
@RequestMapping("/api/entradas")
public class EntradaRestController {

    private final EntradaService entradaService;

    public EntradaRestController(EntradaService entradaService) {
        this.entradaService = entradaService;
    }

    /**
     * {@code GET /api/entradas/cupos-zona/{partidoId}} — Consulta los cupos disponibles
     * por zona para un partido específico, útil para mostrar disponibilidad antes de reservar
     *
     * @param partidoId ID del partido
     * @return lista de zonas con sus cupos disponibles y ocupados
     */
    @GetMapping("/cupos-zona/{partidoId}")
    public ResponseEntity<List<CuposZonaDTO>> cuposPorZona(@PathVariable Long partidoId) {
        return ResponseEntity.ok(entradaService.obtenerCuposPorZona(partidoId));
    }

    /**
     * {@code GET /api/entradas/{entradaId}} — Obtiene el detalle completo de una entrada
     *
     * @param entradaId ID de la entrada
     * @return datos completos de la entrada
     */
    @GetMapping("/{entradaId}")
    public ResponseEntity<EntradaResponseDTO> obtener(@PathVariable Long entradaId) {
        return ResponseEntity.ok(entradaService.obtenerEntrada(entradaId));
    }

    /**
     * {@code POST /api/entradas/reservar} — Crea una reserva de entrada para el usuario
     * autenticado — la entrada queda pendiente de pago hasta que se confirme o expire
     *
     * @param username correo del usuario autenticado extraído del token JWT
     * @param dto      datos del partido, zona y cantidad de entradas
     * @return entrada en estado de reserva pendiente
     */
    @PostMapping("/reservar")
    public ResponseEntity<EntradaResponseDTO> reservar(
            @AuthenticationPrincipal String username,
            @RequestBody EntradaRequestDTO dto) {
        return ResponseEntity.ok(entradaService.reservarEntrada(username, dto));
    }

    /**
     * {@code PATCH /api/entradas/pagar/{entradaId}} — Confirma el pago de una reserva
     * asociándole la referencia generada por el proveedor de pagos externo
     *
     * <p>Se usa {@code PATCH} porque solo se actualiza el estado de pago,
     * no todos los datos de la entrada</p>
     *
     * @param entradaId  ID de la entrada a confirmar
     * @param paymentRef referencia del pago del proveedor externo
     * @return entrada con estado actualizado a pagada
     */
    @PatchMapping("/pagar/{entradaId}")
    public ResponseEntity<EntradaResponseDTO> pagar(
            @PathVariable Long entradaId,
            @RequestParam String paymentRef) {
        return ResponseEntity.ok(entradaService.confirmarPago(entradaId, paymentRef));
    }

    /**
     * {@code PATCH /api/entradas/cancelar/{entradaId}} — Cancela una reserva pendiente
     * de pago y libera los cupos ocupados
     *
     * @param username  correo del usuario autenticado extraído del token JWT
     * @param entradaId ID de la entrada a cancelar
     * @return entrada con estado cancelado
     */
    @PatchMapping("/cancelar/{entradaId}")
    public ResponseEntity<EntradaResponseDTO> cancelar(
            @AuthenticationPrincipal String username,
            @PathVariable Long entradaId) {
        return ResponseEntity.ok(entradaService.cancelarReserva(username, entradaId));
    }

    /**
     * {@code PATCH /api/entradas/transferir/{entradaId}} — Transfiere una entrada pagada
     * a otro usuario — valida que el solicitante sea el dueño actual y que el partido
     * aún no haya iniciado
     *
     * @param username  correo del usuario autenticado extraído del token JWT
     * @param entradaId ID de la entrada a transferir
     * @param dto       datos del usuario destinatario
     * @return entrada actualizada con el nuevo propietario
     */
    @PatchMapping("/transferir/{entradaId}")
    public ResponseEntity<EntradaResponseDTO> transferir(
            @AuthenticationPrincipal String username,
            @PathVariable Long entradaId,
            @RequestBody TransferenciaRequestDTO dto) {
        return ResponseEntity.ok(entradaService.transferirEntrada(entradaId, dto, username));
    }

    /**
     * {@code PATCH /api/entradas/reembolsar/{entradaId}} — Solicita el reembolso
     * de una entrada pagada y la marca como reembolsada
     *
     * @param username  correo del usuario autenticado extraído del token JWT
     * @param entradaId ID de la entrada a reembolsar
     * @return entrada con estado reembolsado
     */
    @PatchMapping("/reembolsar/{entradaId}")
    public ResponseEntity<EntradaResponseDTO> reembolsar(
            @AuthenticationPrincipal String username,
            @PathVariable Long entradaId) {
        return ResponseEntity.ok(entradaService.reembolsarEntrada(username, entradaId));
    }

    /**
     * {@code GET /api/entradas/usuario} — Lista todas las entradas del usuario autenticado
     * sin importar su estado (reservadas, pagadas, canceladas, etc.)
     *
     * @param username correo del usuario autenticado extraído del token JWT
     * @return lista de entradas del usuario
     */
    @GetMapping("/usuario")
    public ResponseEntity<List<EntradaResponseDTO>> listar(
            @AuthenticationPrincipal String username) {
        return ResponseEntity.ok(entradaService.listarEntradasUsuario(username));
    }

    /**
     * {@code GET /api/entradas/partidos} — Lista todos los partidos disponibles
     * con su capacidad total, ocupada y disponible por zona
     *
     * @return lista de partidos con información de capacidad
     */
    @GetMapping("/partidos")
    public ResponseEntity<List<PartidoCapacidadDTO>> listarPartidos() {
        return ResponseEntity.ok(entradaService.listarPartidosConCapacidad());
    }
}