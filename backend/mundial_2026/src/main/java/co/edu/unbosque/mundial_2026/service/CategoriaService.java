package co.edu.unbosque.mundial_2026.service;

import java.util.List;
import co.edu.unbosque.mundial_2026.dto.request.CategoriaRequestDTO;
import co.edu.unbosque.mundial_2026.dto.response.CategoriaResponseDTO;
import co.edu.unbosque.mundial_2026.dto.response.DesactivarCategoriaResponseDTO;
import co.edu.unbosque.mundial_2026.dto.response.ProductoResponseDTO;
import co.edu.unbosque.mundial_2026.dto.response.ReactivarCategoriaResponseDTO;
import co.edu.unbosque.mundial_2026.entity.Categoria;

/**
 * Contrato del servicio de categorías de productos — define las operaciones
 * de gestión incluyendo activación y desactivación lógica
 */
public interface CategoriaService {

    /**
     * Crea una nueva categoría con los datos del DTO
     *
     * @param dto datos de la categoría a crear
     * @return categoría creada
     */
    CategoriaResponseDTO crear(CategoriaRequestDTO dto);

    /**
     * Lista únicamente las categorías activas disponibles para los usuarios
     *
     * @return lista de categorías activas
     */
    List<CategoriaResponseDTO> listar();

    /**
     * Obtiene la entidad {@link Categoria} directamente desde la base de datos —
     * pensado para uso interno entre servicios, no para exponer en el controlador
     *
     * @param id ID de la categoría
     * @return entidad {@link Categoria}
     */
    Categoria obtenerEntidadPorId(Long id);

    /**
     * Actualiza los datos de una categoría existente
     *
     * @param id  ID de la categoría a modificar
     * @param dto nuevos datos de la categoría
     * @return categoría con los datos actualizados
     */
    CategoriaResponseDTO actualizar(Long id, CategoriaRequestDTO dto);

    /**
     * Desactiva lógicamente una categoría sin eliminarla de la base de datos —
     * no se puede desactivar si tiene productos asociados activos
     *
     * @param id ID de la categoría a desactivar
     * @return respuesta con el resultado de la desactivación
     */
    DesactivarCategoriaResponseDTO desactivar(Long id);

    /**
     * Reactiva una categoría previamente desactivada dejándola disponible nuevamente
     *
     * @param id ID de la categoría a reactivar
     * @return respuesta con el resultado de la reactivación
     */
    ReactivarCategoriaResponseDTO reactivar(Long id);

    /**
     * Lista todas las categorías del sistema sin importar su estado,
     * incluyendo las desactivadas — útil para vistas administrativas
     *
     * @return lista completa de categorías
     */
    List<CategoriaResponseDTO> listarTodas();

    /**
     * Retorna todos los productos pertenecientes a una categoría específica
     *
     * @param id ID de la categoría
     * @return lista de productos de esa categoría
     */
    List<ProductoResponseDTO> obtenerProductosPorCategoria(Long id);
}