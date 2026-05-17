package co.edu.unbosque.mundial_2026.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.query.Param;
import java.util.Optional;
import co.edu.unbosque.mundial_2026.dto.response.ProductoListadoDTO;
import co.edu.unbosque.mundial_2026.entity.Producto;

public interface ProductoRepository extends JpaRepository<Producto, Long> {
    List<Producto> findByActivoTrue();
    List<Producto> findByCategoriaIdAndActivoTrue(Long categoriaId);
  
    @Query("SELECT DISTINCT p FROM Producto p LEFT JOIN FETCH p.variantes WHERE p.activo = true")
List<Producto> findByActivoTrueWithVariantes();
@Query("SELECT DISTINCT p FROM Producto p LEFT JOIN FETCH p.variantes WHERE p.activo = true")
List<Producto> findByActivoTrueWithVariantes(Pageable pageable);

@Query(value = "SELECT COUNT(DISTINCT p) FROM Producto p WHERE p.activo = true")
long countByActivoTrue();

@Query("SELECT DISTINCT p FROM Producto p LEFT JOIN FETCH p.variantes LEFT JOIN FETCH p.categoria WHERE p.activo = true")
List<Producto> findAllLiviano();
long countByCategoriaIdAndActivoTrue(Long categoriaId);
@Query("SELECT DISTINCT p FROM Producto p LEFT JOIN FETCH p.variantes")
List<Producto> findAllWithVariantes();
List<Producto> findByCategoriaId(Long categoriaId);
long countByCategoriaId(Long categoriaId);
@Query("SELECT p FROM Producto p LEFT JOIN FETCH p.variantes LEFT JOIN FETCH p.categoria WHERE p.id = :id")
Optional<Producto> findByIdWithVariantes(@Param("id") Long id);
}