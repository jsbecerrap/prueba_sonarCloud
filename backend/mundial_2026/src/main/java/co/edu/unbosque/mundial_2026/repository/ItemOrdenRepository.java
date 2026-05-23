package co.edu.unbosque.mundial_2026.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import co.edu.unbosque.mundial_2026.entity.ItemOrden;

/**
 * Repositorio encargado de la gestión y consulta de los ítems asociados a las órdenes del sistema
 */
public interface ItemOrdenRepository extends JpaRepository<ItemOrden, Long> {

    /**
     * Obtiene todos los ítems que pertenecen a una orden específica
     *
     * @param ordenId identificador de la orden consultada
     * @return lista de ítems asociados a la orden
     */
    List<ItemOrden> findByOrdenId(Long ordenId);

    /**
     * Busca un ítem específico dentro de una orden según el producto y la variante asociados
     *
     * @param ordenId identificador de la orden
     * @param productoId identificador del producto
     * @param varianteId identificador de la variante del producto
     * @return ítem encontrado si existe
     */
    Optional<ItemOrden> findByOrdenIdAndProductoIdAndVarianteId(Long ordenId, Long productoId, Long varianteId);

    /**
     * Ejecuta una consulta personalizada para obtener los ítems de una orden cargando de forma anticipada el producto su categoría y su variante
     * Esto permite recuperar toda la información relacionada en una sola consulta y evitar cargas adicionales
     *
     * @param ordenId identificador de la orden consultada
     * @return lista de ítems con sus detalles completos asociados
     */
    @Query("SELECT i FROM ItemOrden i JOIN FETCH i.producto p JOIN FETCH p.categoria JOIN FETCH i.variante WHERE i.orden.id = :ordenId")
    List<ItemOrden> findByOrdenIdConDetalles(@Param("ordenId") Long ordenId);

    /**
     * Genera un ranking de los productos más vendidos agrupando por producto y calculando la cantidad total vendida y el ingreso generado
     * La consulta ordena los resultados desde el producto con mayor volumen de ventas y permite limitar resultados con paginación
     *
     * @param pageable configuración de paginación y límite de resultados
     * @return lista con la información resumida de los productos más vendidos
     */
    @Query("SELECT i.producto.id, i.producto.nombre, i.producto.categoria.nombre, " +
           "SUM(i.cantidad), SUM(i.cantidad * i.precioUnitario) " +
           "FROM ItemOrden i " +
           "GROUP BY i.producto.id, i.producto.nombre, i.producto.categoria.nombre " +
           "ORDER BY SUM(i.cantidad) DESC")
    List<Object[]> findTopProductosMasVendidos(Pageable pageable);

    /**
     * Genera un reporte agrupado por categoría calculando la cantidad total vendida y el ingreso acumulado de cada una
     * La consulta organiza los resultados desde la categoría con mayor facturación
     *
     * @return lista con el resumen de ventas agrupado por categoría
     */
    @Query("SELECT i.producto.categoria.nombre, SUM(i.cantidad), SUM(i.cantidad * i.precioUnitario) " +
           "FROM ItemOrden i " +
           "GROUP BY i.producto.categoria.nombre " +
           "ORDER BY SUM(i.cantidad * i.precioUnitario) DESC")
    List<Object[]> findVentasPorCategoria();
}