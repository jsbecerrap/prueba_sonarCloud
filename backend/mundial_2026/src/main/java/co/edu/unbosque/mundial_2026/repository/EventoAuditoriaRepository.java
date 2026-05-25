package co.edu.unbosque.mundial_2026.repository;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import co.edu.unbosque.mundial_2026.entity.EventoAuditoria;

/**
 * Repositorio encargado de la gestión y consulta de los eventos de auditoría registrados en el sistema
 */

public interface EventoAuditoriaRepository extends JpaRepository<EventoAuditoria, Long> {

    /**
     * Obtiene los eventos de auditoría asociados a un usuario específico de forma paginada
     *
     * @param usuarioId identificador del usuario consultado
     * @param pageable configuración de paginación de la consulta
     * @return página con los eventos encontrados para el usuario
     */
    Page<EventoAuditoria> findByUsuarioId(Long usuarioId, Pageable pageable);

    /**
     * Obtiene los eventos de auditoría que coinciden con un tipo específico de forma paginada
     *
     * @param tipo categoría o tipo de evento a buscar
     * @param pageable configuración de paginación de la consulta
     * @return página con los eventos que coinciden con el tipo indicado
     */
    Page<EventoAuditoria> findByTipo(String tipo, Pageable pageable);

    /**
     * Obtiene los eventos de auditoría relacionados con un identificador de correlación específico
     *
     * @param idCorrelacion identificador que agrupa eventos relacionados
     * @param pageable configuración de paginación de la consulta
     * @return página con los eventos asociados al identificador de correlación
     */
    Page<EventoAuditoria> findByIdCorrelacion(String idCorrelacion, Pageable pageable);

    /**
     * Obtiene los eventos de auditoría asociados a una entidad de correlación específica
     *
     * @param entidadCorrelacion nombre o referencia de la entidad relacionada
     * @param pageable configuración de paginación de la consulta
     * @return página con los eventos asociados a la entidad indicada
     */
    Page<EventoAuditoria> findByEntidadCorrelacion(String entidadCorrelacion, Pageable pageable);

    /**
     * Obtiene los eventos de auditoría registrados dentro de un rango de fechas específico
     *
     * @param inicio fecha inicial del rango de búsqueda
     * @param fin fecha final del rango de búsqueda
     * @param pageable configuración de paginación de la consulta
     * @return página con los eventos encontrados en el intervalo indicado
     */
    Page<EventoAuditoria> findByFechaBetween(LocalDateTime inicio, LocalDateTime fin, Pageable pageable);

   /**
    * Ejecuta una consulta nativa con filtros opcionales para buscar eventos de auditoría según usuario tipos de evento y rango de fechas
    * La consulta solo aplica cada filtro cuando se recibe un valor y ordena los resultados desde el evento más reciente
    * Incluye una consulta adicional de conteo para soportar la paginación correctamente
    *
    * @param usuarioId identificador del usuario a filtrar o null para incluir todos
    * @param tipos listado de tipos separados por comas para filtrar múltiples categorías o null para no aplicar filtro
    * @param fechaInicio fecha mínima desde la cual se buscan eventos o null para no limitar el inicio
    * @param fechaFin fecha máxima hasta la cual se buscan eventos o null para no limitar el final
    * @param pageable configuración de paginación de la consulta
    * @return página con los eventos que cumplen los filtros aplicados
    */
   //valida con or si llega se lo asigna si no no y la segunda es para contar cuantos hay y asi paginar
   //igual simplemente usa any para validar que este en ellos 
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