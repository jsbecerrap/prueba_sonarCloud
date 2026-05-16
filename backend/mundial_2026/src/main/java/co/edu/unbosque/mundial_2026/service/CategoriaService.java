package co.edu.unbosque.mundial_2026.service;

import java.util.List;
import co.edu.unbosque.mundial_2026.dto.request.CategoriaRequestDTO;
import co.edu.unbosque.mundial_2026.dto.response.CategoriaResponseDTO;
import co.edu.unbosque.mundial_2026.dto.response.DesactivarCategoriaResponseDTO;
import co.edu.unbosque.mundial_2026.dto.response.ProductoResponseDTO;
import co.edu.unbosque.mundial_2026.dto.response.ReactivarCategoriaResponseDTO;
import co.edu.unbosque.mundial_2026.entity.Categoria;

public interface CategoriaService {
    CategoriaResponseDTO crear(CategoriaRequestDTO dto);
    List<CategoriaResponseDTO> listar();
    Categoria obtenerEntidadPorId(Long id);
    CategoriaResponseDTO actualizar(Long id, CategoriaRequestDTO dto);
    DesactivarCategoriaResponseDTO desactivar(Long id);
    ReactivarCategoriaResponseDTO reactivar(Long id);
    List<CategoriaResponseDTO> listarTodas();
    List<ProductoResponseDTO> obtenerProductosPorCategoria(Long id);
}