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

@RestController
@RequestMapping("/api/categorias")
public class CategoriaController {

    private final CategoriaService categoriaService;

    private static final String ROL_ADMIN = "hasRole('ADMIN')";
    public CategoriaController(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }

    @PreAuthorize(ROL_ADMIN)
    @PostMapping
    public ResponseEntity<CategoriaResponseDTO> crear(@Valid @RequestBody CategoriaRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(categoriaService.crear(dto));
    }

    @GetMapping
    public ResponseEntity<List<CategoriaResponseDTO>> listar() {
        return ResponseEntity.ok(categoriaService.listar());
    }

    @PreAuthorize(ROL_ADMIN)
    @PutMapping("/{id}")
    public ResponseEntity<CategoriaResponseDTO> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody CategoriaRequestDTO dto) {
        return ResponseEntity.ok(categoriaService.actualizar(id, dto));
    }

    @PreAuthorize(ROL_ADMIN)
    @PatchMapping("/{id}/desactivar")
    public ResponseEntity<DesactivarCategoriaResponseDTO> desactivar(@PathVariable Long id) {
        return ResponseEntity.ok(categoriaService.desactivar(id));
    }

    @PreAuthorize(ROL_ADMIN)
    @PatchMapping("/{id}/reactivar")
    public ResponseEntity<ReactivarCategoriaResponseDTO> reactivar(@PathVariable Long id) {
        return ResponseEntity.ok(categoriaService.reactivar(id));
    }
    @PreAuthorize(ROL_ADMIN)
@GetMapping("/todas")
public ResponseEntity<List<CategoriaResponseDTO>> listarTodas() {
    return ResponseEntity.ok(categoriaService.listarTodas());
}
@PreAuthorize(ROL_ADMIN)
@GetMapping("/{id}/productos")
public ResponseEntity<List<ProductoResponseDTO>> obtenerProductos(@PathVariable Long id) {
    return ResponseEntity.ok(categoriaService.obtenerProductosPorCategoria(id));
}
}