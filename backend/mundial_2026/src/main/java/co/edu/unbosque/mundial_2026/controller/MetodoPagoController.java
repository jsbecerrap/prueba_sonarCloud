package co.edu.unbosque.mundial_2026.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.edu.unbosque.mundial_2026.dto.request.MetodoPagoRequestDTO;
import co.edu.unbosque.mundial_2026.dto.response.MetodoPagoResponseDTO;
import co.edu.unbosque.mundial_2026.service.MetodoPagoService;
import jakarta.validation.Valid;

/**
 * Controlador REST que expone los endpoints de gestión de métodos de pago —
 * cada usuario solo puede ver y modificar sus propios métodos de pago,
 * identificados automáticamente a través del token JWT
 *
 * <p>Base URL: {@code /payments}</p>
 */
@RestController
@RequestMapping("/payments")
public class MetodoPagoController {

    private final MetodoPagoService metodoPagoService;

    public MetodoPagoController(MetodoPagoService metodoPagoService) {
        this.metodoPagoService = metodoPagoService;
    }

    /**
     * {@code GET /payments} — Lista todos los métodos de pago registrados
     * por el usuario autenticado
     *
     * @param username correo del usuario autenticado extraído del token JWT
     * @return lista de métodos de pago del usuario
     */
    @GetMapping
    public ResponseEntity<List<MetodoPagoResponseDTO>> listar(
            @AuthenticationPrincipal String username) {
        return ResponseEntity.ok(metodoPagoService.listarPorCorreo(username));
    }

    /**
     * {@code POST /payments} — Agrega un nuevo método de pago al perfil del usuario —
     * retorna HTTP 400 si los datos enviados no son válidos para crear el método de pago
     *
     * @param username correo del usuario autenticado extraído del token JWT
     * @param dto      datos del método de pago a registrar
     * @return método de pago creado, o HTTP 400 si no pudo crearse
     */
    @PostMapping
    public ResponseEntity<MetodoPagoResponseDTO> agregar(
            @AuthenticationPrincipal String username,
            @Valid @RequestBody MetodoPagoRequestDTO dto) {
        MetodoPagoResponseDTO resultado = metodoPagoService.agregar(username, dto);
        if (resultado == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(resultado);
    }

    /**
     * {@code PATCH /payments/{id}/default} — Establece un método de pago como predeterminado,
     * desmarcando automáticamente el que estuviera activo previamente
     *
     * @param username correo del usuario autenticado extraído del token JWT
     * @param id       ID del método de pago a marcar como predeterminado
     * @return respuesta vacía con HTTP 204
     */
    @PatchMapping("/{id}/default")
    public ResponseEntity<Void> setDefault(
            @AuthenticationPrincipal String username,
            @PathVariable Long id) {
        metodoPagoService.setDefaultPorCorreo(username, id);
        return ResponseEntity.noContent().build();
    }

    /**
     * {@code DELETE /payments/{id}} — Elimina un método de pago del perfil del usuario
     * validando que le pertenezca
     *
     * @param username correo del usuario autenticado extraído del token JWT
     * @param id       ID del método de pago a eliminar
     * @return respuesta vacía con HTTP 204
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @AuthenticationPrincipal String username,
            @PathVariable Long id) {
        metodoPagoService.eliminar(username, id);
        return ResponseEntity.noContent().build();
    }

    /**
     * {@code PATCH /payments/{id}} — Actualiza los datos de un método de pago existente
     * del usuario autenticado
     *
     * @param username correo del usuario autenticado extraído del token JWT
     * @param id       ID del método de pago a actualizar
     * @param dto      nuevos datos del método de pago
     * @return método de pago con los datos actualizados
     */
    @PatchMapping("/{id}")
    public ResponseEntity<MetodoPagoResponseDTO> actualizar(
            @AuthenticationPrincipal String username,
            @PathVariable Long id,
            @Valid @RequestBody MetodoPagoRequestDTO dto) {
        return ResponseEntity.ok(metodoPagoService.actualizar(username, id, dto));
    }
}