package co.edu.unbosque.mundial_2026.service;

import java.util.List;
import java.util.Map;
 import org.springframework.data.domain.Pageable;
import co.edu.unbosque.mundial_2026.dto.request.ProductoActualizarRequestDTO;
import co.edu.unbosque.mundial_2026.dto.request.ProductoRequestDTO;
import co.edu.unbosque.mundial_2026.dto.response.ProductoListadoDTO;
import co.edu.unbosque.mundial_2026.dto.response.ProductoResponseDTO;
import co.edu.unbosque.mundial_2026.entity.Producto;

public interface ProductoService {
    ProductoResponseDTO crear(ProductoRequestDTO dto);
    ProductoResponseDTO actualizar(Long id, ProductoActualizarRequestDTO dto);
    void eliminar(Long id);
    void reactivar(Long id);
    List<ProductoResponseDTO> listarTodos();
    List<ProductoResponseDTO> listarTodos(boolean soloActivos);
    List<ProductoResponseDTO> listarPorCategoria(Long categoriaId);
    ProductoResponseDTO obtenerPorId(Long id);
    Producto obtenerEntidadPorId(Long id);
    void actualizarStock(Long productoId, int cantidad);
   
List<ProductoListadoDTO> listarTodosLiviano();
void activarLote(List<Long> ids);
}