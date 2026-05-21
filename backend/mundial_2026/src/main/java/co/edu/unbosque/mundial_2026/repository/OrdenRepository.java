package co.edu.unbosque.mundial_2026.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
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
           "LEFT JOIN FETCH o.metodoPago " +
           "WHERE o.usuario.id = :usuarioId AND o.estado <> :estado")
    List<Orden> findByUsuarioIdAndEstadoNot(@Param("usuarioId") Long usuarioId,
                                            @Param("estado") String estado);

    @Query("SELECT o FROM Orden o " +
           "LEFT JOIN FETCH o.metodoPago " +
           "WHERE o.usuario.id = :usuarioId AND o.estado IN :estados " +
           "ORDER BY o.fechaCreacion DESC")
    List<Orden> findHistorialByUsuarioIdAndEstadoIn(
        @Param("usuarioId") Long usuarioId,
        @Param("estados") List<String> estados);

    List<Orden> findByEstadoAndFechaCreacionBefore(String estado, LocalDateTime fecha);

    List<Orden> findByEstadoAndFechaCreacionBeforeAndNotificadoAbandonadoFalse(
        String estado, LocalDateTime fecha);

    long countByFechaPagoIsNotNull();

    @Query("SELECT COALESCE(SUM(o.total), 0.0) FROM Orden o WHERE o.fechaPago IS NOT NULL")
    Double sumIngresoTotal();

    @Query("SELECT COALESCE(mp.tipo, 'Sin método'), COUNT(o), COALESCE(SUM(o.total), 0.0) " +
           "FROM Orden o LEFT JOIN o.metodoPago mp " +
           "WHERE o.fechaPago IS NOT NULL " +
           "GROUP BY mp.tipo " +
           "ORDER BY COALESCE(SUM(o.total), 0.0) DESC")
    List<Object[]> findIngresosPorMetodoPago();

    @Query("SELECT o.usuario.id, o.usuario.nombre, o.usuario.apellido, o.usuario.correoUsuario, " +
           "COUNT(o), COALESCE(SUM(o.total), 0.0) " +
           "FROM Orden o " +
           "WHERE o.fechaPago IS NOT NULL " +
           "GROUP BY o.usuario.id, o.usuario.nombre, o.usuario.apellido, o.usuario.correoUsuario " +
           "ORDER BY COALESCE(SUM(o.total), 0.0) DESC")
    List<Object[]> findTopUsuariosSouvenir(Pageable pageable);
}