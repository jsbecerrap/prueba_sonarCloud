package co.edu.unbosque.mundial_2026.service;

import java.util.List;
import co.edu.unbosque.mundial_2026.dto.request.ProductoActualizarRequestDTO;
import co.edu.unbosque.mundial_2026.dto.request.ProductoRequestDTO;
import co.edu.unbosque.mundial_2026.dto.response.ProductoListadoDTO;
import co.edu.unbosque.mundial_2026.dto.response.ProductoResponseDTO;
import co.edu.unbosque.mundial_2026.entity.Producto;

/**
 * Contrato del servicio de productos — gestiona el ciclo de vida de los productos
 * de la tienda incluyendo creación, actualización, stock y activación por lote
 */
public interface ProductoService {

    /**
     * Crea un nuevo producto con sus variantes y lo registra en el sistema
     *
     * @param dto datos del producto a crear
     * @return producto creado
     */
    ProductoResponseDTO crear(ProductoRequestDTO dto);

    /**
     * Actualiza los datos de un producto existente sin afectar su stock
     *
     * @param id  ID del producto a modificar
     * @param dto nuevos datos del producto
     * @return producto con los datos actualizados
     */
    ProductoResponseDTO actualizar(Long id, ProductoActualizarRequestDTO dto);

    /**
     * Desactiva lógicamente un producto ocultándolo de la tienda sin eliminarlo
     *
     * @param id ID del producto a desactivar
     */
    void eliminar(Long id);

    /**
     * Reactiva un producto previamente desactivado dejándolo visible en la tienda
     *
     * @param id ID del producto a reactivar
     */
    void reactivar(Long id);

    /**
     * Lista todos los productos del sistema sin importar su estado
     *
     * @return lista completa de productos
     */
    List<ProductoResponseDTO> listarTodos();

    /**
     * Lista productos filtrando opcionalmente por su estado activo o inactivo
     *
     * @param soloActivos si es {@code true} retorna solo los activos, si es {@code false} retorna todos
     * @return lista de productos según el filtro indicado
     */
    List<ProductoResponseDTO> listarTodos(boolean soloActivos);

    /**
     * Retorna los productos que pertenecen a una categoría específica
     *
     * @param categoriaId ID de la categoría
     * @return lista de productos de esa categoría
     */
    List<ProductoResponseDTO> listarPorCategoria(Long categoriaId);

    /**
     * Obtiene el detalle completo de un producto por su ID
     *
     * @param id ID del producto
     * @return datos completos del producto
     */
    ProductoResponseDTO obtenerPorId(Long id);

    /**
     * Obtiene la entidad {@link Producto} directamente desde la base de datos —
     * pensado para uso interno entre servicios, no para exponer en el controlador
     *
     * @param id ID del producto
     * @return entidad {@link Producto}
     */
    Producto obtenerEntidadPorId(Long id);

    /**
     * Descuenta del stock de un producto la cantidad indicada tras una compra
     *
     * @param productoId ID del producto
     * @param cantidad   unidades a descontar del stock
     */
    void actualizarStock(Long productoId, int cantidad);

    /**
     * Lista todos los productos en formato resumido con menos datos que
     * {@link #listarTodos()} — útil para listados rápidos en la tienda
     *
     * @return lista liviana de productos con datos básicos
     */
    List<ProductoListadoDTO> listarTodosLiviano();

    /**
     * Activa múltiples productos a la vez a partir de una lista de IDs —
     * útil para habilitar productos en bloque desde el panel administrativo
     *
     * @param ids lista de IDs de los productos a activar
     */
    void activarLote(List<Long> ids);
}