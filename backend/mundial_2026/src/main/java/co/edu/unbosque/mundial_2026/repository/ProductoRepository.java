package co.edu.unbosque.mundial_2026.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import co.edu.unbosque.mundial_2026.dto.response.ProductoListadoDTO;
import co.edu.unbosque.mundial_2026.entity.Producto;
/**
 * Repositorio encargado de la gestión y consulta de los productos registrados en el sistema
 */
public interface ProductoRepository extends JpaRepository<Producto, Long> {

    /**
     * Obtiene todos los productos que se encuentran activos
     *
     * @return lista de productos activos
     */
    List<Producto> findByActivoTrue();

    /**
     * Obtiene los productos activos que pertenecen a una categoría específica
     *
     * @param categoriaId identificador de la categoría consultada
     * @return lista de productos activos asociados a la categoría
     */
    List<Producto> findByCategoriaIdAndActivoTrue(Long categoriaId);
  
    /**
     * Ejecuta una consulta personalizada para obtener todos los productos activos cargando de forma anticipada sus variantes
     * Esto permite recuperar la información relacionada en una sola consulta y evitar cargas adicionales
     *
     * @return lista de productos activos con sus variantes asociadas
     */
    @Query("SELECT DISTINCT p FROM Producto p LEFT JOIN FETCH p.variantes WHERE p.activo = true")
    List<Producto> findByActivoTrueWithVariantes();

    /**
     * Ejecuta una consulta personalizada para obtener productos activos con sus variantes asociadas permitiendo limitar resultados con paginación
     * La consulta evita duplicados y carga las variantes en la misma operación
     *
     * @param pageable configuración de paginación y límite de resultados
     * @return lista de productos activos con sus variantes cargadas
     */
    @Query("SELECT DISTINCT p FROM Producto p LEFT JOIN FETCH p.variantes WHERE p.activo = true")
    List<Producto> findByActivoTrueWithVariantes(Pageable pageable);

    /**
     * Cuenta la cantidad total de productos activos registrados en el sistema
     *
     * @return número total de productos activos
     */
    @Query("SELECT COUNT(DISTINCT p) FROM Producto p WHERE p.activo = true")
    long countByActivoTrue();

    /**
     * Genera un listado liviano de productos activos proyectando únicamente la información necesaria en un DTO
     * La consulta incluye datos generales del producto calcula el stock total disponible y cuenta sus variantes asociadas
     *
     * @return lista de productos resumidos con información consolidada
     */
    @Query("SELECT new co.edu.unbosque.mundial_2026.dto.response.ProductoListadoDTO(" +
           "p.id, p.nombre, p.descripcion, p.precio, p.imagenUrl, " +
           "p.categoria.nombre, p.equipo, p.bandera, p.destacado, " +
           "COALESCE(SUM(v.stock), 0), COUNT(v)) " +
           "FROM Producto p LEFT JOIN p.variantes v " +
           "WHERE p.activo = true " +
           "GROUP BY p.id, p.nombre, p.descripcion, p.precio, p.imagenUrl, " +
           "p.categoria.nombre, p.equipo, p.bandera, p.destacado")
    List<ProductoListadoDTO> findAllLiviano();

    /**
     * Cuenta la cantidad de productos activos que pertenecen a una categoría específica
     *
     * @param categoriaId identificador de la categoría consultada
     * @return número total de productos activos en la categoría
     */
    long countByCategoriaIdAndActivoTrue(Long categoriaId);

    /**
     * Ejecuta una consulta personalizada para obtener todos los productos cargando de forma anticipada sus variantes
     * La consulta incluye productos activos e inactivos y evita duplicados en el resultado
     *
     * @return lista completa de productos con sus variantes asociadas
     */
    @Query("SELECT DISTINCT p FROM Producto p LEFT JOIN FETCH p.variantes")
    List<Producto> findAllWithVariantes();

    /**
     * Obtiene todos los productos que pertenecen a una categoría específica sin filtrar por estado
     *
     * @param categoriaId identificador de la categoría consultada
     * @return lista de productos asociados a la categoría
     */
    List<Producto> findByCategoriaId(Long categoriaId);

    /**
     * Cuenta la cantidad total de productos que pertenecen a una categoría específica
     *
     * @param categoriaId identificador de la categoría consultada
     * @return número total de productos registrados en la categoría
     */
    long countByCategoriaId(Long categoriaId);
}