package co.edu.unbosque.mundial_2026.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import co.edu.unbosque.mundial_2026.entity.ItemOrden;

public interface ItemOrdenRepository extends JpaRepository<ItemOrden, Long> {

    Optional<ItemOrden> findByOrdenIdAndProductoIdAndVarianteId(Long ordenId, Long productoId, Long varianteId);
   @Query("SELECT i FROM ItemOrden i " +
       "LEFT JOIN FETCH i.producto p " +
       "LEFT JOIN FETCH p.categoria " +
       "LEFT JOIN FETCH i.variante " +
       "WHERE i.orden.id = :ordenId")
List<ItemOrden> findByOrdenId(@Param("ordenId") Long ordenId);

}