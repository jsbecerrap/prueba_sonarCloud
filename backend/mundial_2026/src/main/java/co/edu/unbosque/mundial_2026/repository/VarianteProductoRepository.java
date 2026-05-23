package co.edu.unbosque.mundial_2026.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import co.edu.unbosque.mundial_2026.entity.VarianteProducto;

/**
 * Repositorio para gestionar las operaciones de acceso a datos
 * de la entidad VarianteProducto.
 */
public interface VarianteProductoRepository extends JpaRepository<VarianteProducto, Long> {

    /**
     * Obtiene una variante de producto junto con su producto asociado
     * y la categoría del mismo cargando la información relacionada en una sola consulta
     *
     * @param id identificador de la variante
     * @return variante encontrada con su producto y categoría
     */
    @Query("SELECT v FROM VarianteProducto v " +
           "JOIN FETCH v.producto p " +
           "JOIN FETCH p.categoria " +
           "WHERE v.id = :id")
    Optional<VarianteProducto> findByIdWithProductoYCategoria(@Param("id") Long id);

    /**
     * Obtiene todas las variantes asociadas a un producto específico
     *
     * @param productoId identificador del producto
     * @return lista de variantes del producto
     */
    List<VarianteProducto> findByProductoId(Long productoId);
}