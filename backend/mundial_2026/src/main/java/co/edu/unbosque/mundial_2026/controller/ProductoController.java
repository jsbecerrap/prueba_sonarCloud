package co.edu.unbosque.mundial_2026.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import co.edu.unbosque.mundial_2026.dto.request.ActivarLoteRequestDTO;
import co.edu.unbosque.mundial_2026.dto.request.ProductoActualizarRequestDTO;
import co.edu.unbosque.mundial_2026.dto.request.ProductoRequestDTO;
import co.edu.unbosque.mundial_2026.dto.response.ProductoListadoDTO;
import co.edu.unbosque.mundial_2026.dto.response.ProductoResponseDTO;
import co.edu.unbosque.mundial_2026.service.ProductoService;
import jakarta.validation.Valid;

/**
 * Controlador REST que expone los endpoints de gestión de productos de la tienda —
 * las operaciones de escritura son exclusivas para administradores, mientras que
 * la consulta de productos activos es pública
 *
 * <p>Base URL: {@code /api/productos}</p>
 */
@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    private final ProductoService productoService;

    private static final String ROL_ADMIN = "hasRole('ADMIN')";

    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    /**
     * {@code POST /api/productos} — Crea un nuevo producto con sus variantes
     * y lo registra en la tienda — solo ADMIN
     *
     * @param dto datos del producto a crear
     * @return producto creado con HTTP 201
     */
    @PreAuthorize(ROL_ADMIN)
    @PostMapping
    public ResponseEntity<ProductoResponseDTO> crear(@Valid @RequestBody ProductoRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productoService.crear(dto));
    }

    /**
     * {@code PUT /api/productos/{id}} — Actualiza los datos de un producto existente — solo ADMIN
     *
     * @param id  ID del producto a modificar
     * @param dto nuevos datos del producto
     * @return producto con los datos actualizados
     */
    @PreAuthorize(ROL_ADMIN)
    @PutMapping("/{id}")
    public ResponseEntity<ProductoResponseDTO> actualizar(@PathVariable Long id,
            @Valid @RequestBody ProductoActualizarRequestDTO dto) {
        return ResponseEntity.ok(productoService.actualizar(id, dto));
    }

    /**
     * {@code DELETE /api/productos/{id}} — Desactiva lógicamente un producto
     * ocultándolo de la tienda sin eliminarlo de la base de datos — solo ADMIN
     *
     * @param id ID del producto a desactivar
     * @return respuesta vacía con HTTP 204
     */
    @PreAuthorize(ROL_ADMIN)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        productoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * {@code PATCH /api/productos/{id}/reactivar} — Reactiva un producto previamente
     * desactivado dejándolo visible nuevamente en la tienda — solo ADMIN
     *
     * @param id ID del producto a reactivar
     * @return respuesta vacía con HTTP 204
     */
    @PreAuthorize(ROL_ADMIN)
    @PatchMapping("/{id}/reactivar")
    public ResponseEntity<Void> reactivar(@PathVariable Long id) {
        productoService.reactivar(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * {@code GET /api/productos/admin/todos} — Lista todos los productos del sistema
     * incluyendo los desactivados — exclusivo para la gestión administrativa — solo ADMIN
     *
     * @return lista completa de productos sin importar su estado
     */
    @PreAuthorize(ROL_ADMIN)
    @GetMapping("/admin/todos")
    public ResponseEntity<List<ProductoResponseDTO>> listarTodosAdmin() {
        return ResponseEntity.ok(productoService.listarTodos(false));
    }

    /**
     * {@code GET /api/productos} — Lista los productos activos disponibles en la tienda —
     * si se envía el parámetro {@code categoriaId}, filtra por esa categoría,
     * de lo contrario retorna todos los productos activos
     *
     * @param categoriaId ID de la categoría a filtrar, opcional
     * @return lista de productos activos, filtrada por categoría si se indica
     */
    @GetMapping
    public ResponseEntity<List<ProductoResponseDTO>> listar(
            @RequestParam(required = false) Long categoriaId) {
        if (categoriaId != null) {
            return ResponseEntity.ok(productoService.listarPorCategoria(categoriaId));
        }
        return ResponseEntity.ok(productoService.listarTodos());
    }

    /**
     * {@code GET /api/productos/{id}} — Obtiene el detalle completo de un producto por su ID
     *
     * @param id ID del producto
     * @return datos completos del producto
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProductoResponseDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(productoService.obtenerPorId(id));
    }

    /**
     * {@code GET /api/productos/listado} — Lista todos los productos en formato resumido
     * con menos datos — útil para cargar listados rápidos en la tienda sin sobrecargar
     * la respuesta con información que no siempre se necesita
     *
     * @return lista liviana de productos con datos básicos
     */
    @GetMapping("/listado")
    public ResponseEntity<List<ProductoListadoDTO>> listarLiviano() {
        return ResponseEntity.ok(productoService.listarTodosLiviano());
    }

    /**
     * {@code PATCH /api/productos/activar-lote} — Activa múltiples productos a la vez
     * a partir de una lista de IDs — útil para habilitar productos en bloque
     * desde el panel administrativo — solo ADMIN
     *
     * @param dto contiene la lista de IDs de los productos a activar
     * @return respuesta vacía con HTTP 204
     */
    @PreAuthorize(ROL_ADMIN)
    @PatchMapping("/activar-lote")
    public ResponseEntity<Void> activarLote(@RequestBody ActivarLoteRequestDTO dto) {
        productoService.activarLote(dto.getIds());
        return ResponseEntity.noContent().build();
    }
}