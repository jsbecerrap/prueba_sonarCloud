package co.edu.unbosque.mundial_2026.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import co.edu.unbosque.mundial_2026.entity.VarianteProducto;

public interface VarianteProductoRepository extends JpaRepository<VarianteProducto, Long> {

    List<VarianteProducto> findByProductoId(Long productoId);
}