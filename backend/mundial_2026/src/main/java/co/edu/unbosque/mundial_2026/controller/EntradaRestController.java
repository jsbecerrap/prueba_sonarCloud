package co.edu.unbosque.mundial_2026.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import co.edu.unbosque.mundial_2026.dto.PartidoCapacidadDTO;
import co.edu.unbosque.mundial_2026.dto.request.EntradaRequestDTO;
import co.edu.unbosque.mundial_2026.dto.request.TransferenciaRequestDTO;
import co.edu.unbosque.mundial_2026.dto.response.EntradaResponseDTO;
import co.edu.unbosque.mundial_2026.service.EntradaService;

@RestController
@RequestMapping("/api/entradas")
public class EntradaRestController {

    private final EntradaService entradaService;

    public EntradaRestController(EntradaService entradaService) {
        this.entradaService = entradaService;
    }

    @PostMapping("/reservar/{usuarioId}")
    public ResponseEntity<EntradaResponseDTO> reservar(
            @PathVariable Long usuarioId,
            @RequestBody EntradaRequestDTO dto) {
        EntradaResponseDTO resultado = entradaService.reservarEntrada(usuarioId, dto);
        if (resultado == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(resultado);
    }

    @PatchMapping("/pagar/{entradaId}")
    public ResponseEntity<EntradaResponseDTO> pagar(
            @PathVariable Long entradaId,
            @RequestParam String paymentRef) {
        EntradaResponseDTO resultado = entradaService.confirmarPago(entradaId, paymentRef);
        if (resultado == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(resultado);
    }

    @PatchMapping("/cancelar/{usuarioId}/{entradaId}")
    public ResponseEntity<EntradaResponseDTO> cancelar(
            @PathVariable Long usuarioId,
            @PathVariable Long entradaId) {
        EntradaResponseDTO resultado = entradaService.cancelarReserva(usuarioId, entradaId);
        if (resultado == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(resultado);
    }

    @PatchMapping("/transferir/{usuarioId}/{entradaId}")
    public ResponseEntity<EntradaResponseDTO> transferir(
            @PathVariable Long usuarioId,
            @PathVariable Long entradaId,
            @RequestBody TransferenciaRequestDTO dto) {
        EntradaResponseDTO resultado = entradaService.transferirEntrada(entradaId, dto, usuarioId);
        if (resultado == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(resultado);
    }

    @PatchMapping("/reembolsar/{usuarioId}/{entradaId}")
    public ResponseEntity<EntradaResponseDTO> reembolsar(
            @PathVariable Long usuarioId,
            @PathVariable Long entradaId) {
        EntradaResponseDTO resultado = entradaService.reembolsarEntrada(usuarioId, entradaId);
        if (resultado == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(resultado);
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<EntradaResponseDTO>> listar(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(entradaService.listarEntradasUsuario(usuarioId));
    }

    @GetMapping("/{entradaId}")
    public ResponseEntity<EntradaResponseDTO> obtener(@PathVariable Long entradaId) {
        return ResponseEntity.ok(entradaService.obtenerEntrada(entradaId));
    }
    @GetMapping("/partidos")
public ResponseEntity<List<PartidoCapacidadDTO>> listarPartidos() {
    return ResponseEntity.ok(entradaService.listarPartidosConCapacidad());
}
}