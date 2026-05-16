package co.edu.unbosque.mundial_2026.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import co.edu.unbosque.mundial_2026.entity.Orden;

public interface OrdenRepository extends JpaRepository<Orden, Long> {

    Optional<Orden> findByUsuarioIdAndEstado(Long usuarioId, String estado);

    @Query("SELECT DISTINCT o FROM Orden o " +
           "LEFT JOIN FETCH o.items i " +
           "LEFT JOIN FETCH i.producto p " +
           "LEFT JOIN FETCH p.categoria " +
           "WHERE o.usuario.id = :usuarioId AND o.estado <> :estado")
    List<Orden> findByUsuarioIdAndEstadoNot(@Param("usuarioId") Long usuarioId,
                                            @Param("estado") String estado);
}