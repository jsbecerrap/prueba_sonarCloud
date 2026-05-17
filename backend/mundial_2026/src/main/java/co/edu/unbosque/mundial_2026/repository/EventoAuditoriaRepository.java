package co.edu.unbosque.mundial_2026.repository;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import co.edu.unbosque.mundial_2026.entity.EventoAuditoria;

public interface EventoAuditoriaRepository extends JpaRepository<EventoAuditoria, Long> {

    Page<EventoAuditoria> findByUsuarioId(Long usuarioId, Pageable pageable);
    Page<EventoAuditoria> findByTipo(String tipo, Pageable pageable);
    Page<EventoAuditoria> findByIdCorrelacion(String idCorrelacion, Pageable pageable);
    Page<EventoAuditoria> findByEntidadCorrelacion(String entidadCorrelacion, Pageable pageable);
    Page<EventoAuditoria> findByFechaBetween(LocalDateTime inicio, LocalDateTime fin, Pageable pageable);

   @Query(value = """
    SELECT * FROM eventos_auditoria e
    WHERE (:usuarioId IS NULL OR e.usuario_id = :usuarioId)
    AND (:tipos IS NULL OR e.tipo = ANY(STRING_TO_ARRAY(:tipos, ',')))
    AND (:fechaInicio IS NULL OR e.fecha >= CAST(:fechaInicio AS TIMESTAMP))
    AND (:fechaFin IS NULL OR e.fecha <= CAST(:fechaFin AS TIMESTAMP))
    ORDER BY e.fecha DESC
    """,
    countQuery = """
    SELECT COUNT(*) FROM eventos_auditoria e
    WHERE (:usuarioId IS NULL OR e.usuario_id = :usuarioId)
    AND (:tipos IS NULL OR e.tipo = ANY(STRING_TO_ARRAY(:tipos, ',')))
    AND (:fechaInicio IS NULL OR e.fecha >= CAST(:fechaInicio AS TIMESTAMP))
    AND (:fechaFin IS NULL OR e.fecha <= CAST(:fechaFin AS TIMESTAMP))
    """,
    nativeQuery = true)
Page<EventoAuditoria> buscarConFiltros(
    @Param("usuarioId") Long usuarioId,
    @Param("tipos") String tipos,
    @Param("fechaInicio") String fechaInicio,
    @Param("fechaFin") String fechaFin,
    Pageable pageable
);
}