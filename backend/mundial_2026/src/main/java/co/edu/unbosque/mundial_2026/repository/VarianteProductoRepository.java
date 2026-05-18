package co.edu.unbosque.mundial_2026.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import co.edu.unbosque.mundial_2026.entity.VarianteProducto;

public interface VarianteProductoRepository extends JpaRepository<VarianteProducto, Long> {
 @Query("SELECT v FROM VarianteProducto v " +
           "JOIN FETCH v.producto p " +
           "JOIN FETCH p.categoria " +
           "WHERE v.id = :id")
    Optional<VarianteProducto> findByIdWithProductoYCategoria(@Param("id") Long id);
    List<VarianteProducto> findByProductoId(Long productoId);
}