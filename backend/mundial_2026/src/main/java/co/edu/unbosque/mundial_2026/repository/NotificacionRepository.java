package co.edu.unbosque.mundial_2026.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import co.edu.unbosque.mundial_2026.entity.Notificacion;
import co.edu.unbosque.mundial_2026.entity.Usuario;

/**
 * Repositorio para gestionar las operaciones de acceso a datos
 * de la entidad Notificacion.
 */
@Repository
public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {

    /**
     * Obtiene todas las notificaciones de un usuario.
     *
     * @param usuario usuario asociado
     * @return lista de notificaciones del usuario
     */
    List<Notificacion> findByUsuario(Usuario usuario);

    /**
     * Obtiene las notificaciones de un usuario ordenadas
     * por fecha descendente.
     *
     * @param usuarioId identificador del usuario
     * @return lista de notificaciones ordenadas por fecha
     */
    List<Notificacion> findByUsuarioIdOrderByFechaDesc(Long usuarioId);

    /**
     * Obtiene las notificaciones no leídas de un usuario.
     *
     * @param usuarioId identificador del usuario
     * @return lista de notificaciones no leídas
     */
    List<Notificacion> findByUsuarioIdAndLeidaFalse(Long usuarioId);

    /**
     * Obtiene las notificaciones de un usuario filtradas
     * por estado.
     *
     * @param usuarioId identificador del usuario
     * @param estado estado de la notificación
     * @return lista de notificaciones filtradas
     */
    List<Notificacion> findByUsuarioIdAndEstado(Long usuarioId, String estado);

    /**
     * Obtiene una página de notificaciones de un usuario
     * ordenadas por fecha descendente.
     *
     * @param usuarioId identificador del usuario
     * @param pageable configuración de paginación
     * @return página de notificaciones
     */
    Page<Notificacion> findByUsuarioIdOrderByFechaDesc(Long usuarioId, Pageable pageable);

    /**
     * Obtiene una página de notificaciones de un usuario
     * dentro de un rango de fechas, ordenadas por fecha descendente.
     *
     * @param usuarioId identificador del usuario
     * @param desde fecha inicial del rango
     * @param hasta fecha final del rango
     * @param pageable configuración de paginación
     * @return página de notificaciones filtradas
     */
    Page<Notificacion> findByUsuarioIdAndFechaBetweenOrderByFechaDesc(
            Long usuarioId, LocalDateTime desde, LocalDateTime hasta, Pageable pageable);

    /**
     * Marca como leídas todas las notificaciones no leídas
     * de un usuario.
     *
     * @param usuarioId identificador del usuario
     */
    @Modifying(clearAutomatically = true)
    @Query("UPDATE Notificacion n SET n.leida = true WHERE n.usuario.id = :usuarioId AND n.leida = false")
    void marcarTodasLeidasPorUsuario(@Param("usuarioId") Long usuarioId);
}