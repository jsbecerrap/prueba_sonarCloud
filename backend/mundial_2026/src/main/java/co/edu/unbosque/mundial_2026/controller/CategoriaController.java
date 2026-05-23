package co.edu.unbosque.mundial_2026.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.edu.unbosque.mundial_2026.dto.request.CategoriaRequestDTO;
import co.edu.unbosque.mundial_2026.dto.response.CategoriaResponseDTO;
import co.edu.unbosque.mundial_2026.dto.response.DesactivarCategoriaResponseDTO;
import co.edu.unbosque.mundial_2026.dto.response.ProductoResponseDTO;
import co.edu.unbosque.mundial_2026.dto.response.ReactivarCategoriaResponseDTO;
import co.edu.unbosque.mundial_2026.service.CategoriaService;
import jakarta.validation.Valid;

/**
 * Controlador REST que expone los endpoints de gestión de categorías de productos —
 * las operaciones de escritura están restringidas a administradores mientras que
 * la consulta de categorías activas es pública
 *
 * <p>Base URL: {@code /api/categorias}</p>
 */
@RestController
@RequestMapping("/api/categorias")
public class CategoriaController {

    private final CategoriaService categoriaService;

    private static final String ROL_ADMIN = "hasRole('ADMIN')";

    public CategoriaController(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }

    /**
     * {@code POST /api/categorias} — Crea una nueva categoría de productos — solo ADMIN
     *
     * @param dto datos de la categoría a crear
     * @return categoría creada con HTTP 201
     */
    @PreAuthorize(ROL_ADMIN)
    @PostMapping
    public ResponseEntity<CategoriaResponseDTO> crear(@Valid @RequestBody CategoriaRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(categoriaService.crear(dto));
    }

    /**
     * {@code GET /api/categorias} — Lista únicamente las categorías activas,
     * es decir las que están disponibles para los usuarios en la tienda
     *
     * @return lista de categorías activas
     */
    @GetMapping
    public ResponseEntity<List<CategoriaResponseDTO>> listar() {
        return ResponseEntity.ok(categoriaService.listar());
    }

    /**
     * {@code PUT /api/categorias/{id}} — Actualiza los datos de una categoría existente — solo ADMIN
     *
     * @param id  ID de la categoría a modificar
     * @param dto nuevos datos de la categoría
     * @return categoría con los datos actualizados
     */
    @PreAuthorize(ROL_ADMIN)
    @PutMapping("/{id}")
    public ResponseEntity<CategoriaResponseDTO> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody CategoriaRequestDTO dto) {
        return ResponseEntity.ok(categoriaService.actualizar(id, dto));
    }

    /**
     * {@code PATCH /api/categorias/{id}/desactivar} — Desactiva lógicamente una categoría
     * ocultándola de la tienda sin eliminarla de la base de datos — solo ADMIN
     *
     * <p>Se usa {@code PATCH} porque es una modificación parcial del estado,
     * no una actualización completa del recurso</p>
     *
     * @param id ID de la categoría a desactivar
     * @return respuesta con el resultado de la desactivación
     */
    @PreAuthorize(ROL_ADMIN)
    @PatchMapping("/{id}/desactivar")
    public ResponseEntity<DesactivarCategoriaResponseDTO> desactivar(@PathVariable Long id) {
        return ResponseEntity.ok(categoriaService.desactivar(id));
    }

    /**
     * {@code PATCH /api/categorias/{id}/reactivar} — Reactiva una categoría previamente
     * desactivada dejándola visible nuevamente en la tienda — solo ADMIN
     *
     * @param id ID de la categoría a reactivar
     * @return respuesta con el resultado de la reactivación
     */
    @PreAuthorize(ROL_ADMIN)
    @PatchMapping("/{id}/reactivar")
    public ResponseEntity<ReactivarCategoriaResponseDTO> reactivar(@PathVariable Long id) {
        return ResponseEntity.ok(categoriaService.reactivar(id));
    }

    /**
     * {@code GET /api/categorias/todas} — Lista todas las categorías del sistema
     * incluyendo las desactivadas — útil para la gestión desde el panel administrativo — solo ADMIN
     *
     * @return lista completa de categorías sin importar su estado
     */
    @PreAuthorize(ROL_ADMIN)
    @GetMapping("/todas")
    public ResponseEntity<List<CategoriaResponseDTO>> listarTodas() {
        return ResponseEntity.ok(categoriaService.listarTodas());
    }

    /**
     * {@code GET /api/categorias/{id}/productos} — Retorna todos los productos
     * que pertenecen a una categoría específica — solo ADMIN
     *
     * @param id ID de la categoría
     * @return lista de productos de esa categoría
     */
    @PreAuthorize(ROL_ADMIN)
    @GetMapping("/{id}/productos")
    public ResponseEntity<List<ProductoResponseDTO>> obtenerProductos(@PathVariable Long id) {
        return ResponseEntity.ok(categoriaService.obtenerProductosPorCategoria(id));
    }
}