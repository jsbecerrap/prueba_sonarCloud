package co.edu.unbosque.mundial_2026.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

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
@Query("SELECT new co.edu.unbosque.mundial_2026.dto.response.ProductoListadoDTO(" +
       "p.id, p.nombre, p.descripcion, p.precio, p.imagenUrl, " +
       "p.categoria.nombre, p.equipo, p.bandera, p.destacado, " +
       "COALESCE(SUM(v.stock), 0), COUNT(v)) " +
       "FROM Producto p LEFT JOIN p.variantes v " +
       "WHERE p.activo = true " +
       "GROUP BY p.id, p.nombre, p.descripcion, p.precio, p.imagenUrl, " +
       "p.categoria.nombre, p.equipo, p.bandera, p.destacado")
List<ProductoListadoDTO> findAllLiviano();

long countByCategoriaIdAndActivoTrue(Long categoriaId);
@Query("SELECT DISTINCT p FROM Producto p LEFT JOIN FETCH p.variantes")
List<Producto> findAllWithVariantes();
List<Producto> findByCategoriaId(Long categoriaId);
long countByCategoriaId(Long categoriaId);
}