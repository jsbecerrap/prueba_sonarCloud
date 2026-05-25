package co.edu.unbosque.mundial_2026.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import co.edu.unbosque.mundial_2026.entity.Participacion;

/**
 * Repositorio para gestionar las operaciones de acceso a datos
 * de la entidad Participacion.
 */
@Repository
public interface ParticipacionRepository extends JpaRepository<Participacion, Long> {

    /**
     * Busca una participación asociada a un usuario y una apuesta.
     *
     * @param usuarioId identificador del usuario
     * @param apuestaId identificador de la apuesta
     * @return participación encontrada, si existe
     */
    Optional<Participacion> findByUsuarioIdAndApuestaId(Long usuarioId, Long apuestaId);

    /**
     * Obtiene todas las participaciones de una apuesta.
     *
     * @param apuestaId identificador de la apuesta
     * @return lista de participaciones asociadas
     */
    List<Participacion> findByApuestaId(Long apuestaId);

    /**
     * Obtiene las participaciones de una apuesta ordenadas
     * por puntos de forma descendente.
     *
     * @param apuestaId identificador de la apuesta
     * @return lista de participaciones ordenadas por puntaje
     */
    List<Participacion> findByApuestaIdOrderByPuntosDesc(Long apuestaId);

    /**
     * Obtiene todas las participaciones de un usuario.
     *
     * @param usuarioId identificador del usuario
     * @return lista de participaciones asociadas
     */
    List<Participacion> findByUsuarioId(Long usuarioId);

    /**
     * Elimina todas las participaciones asociadas a una apuesta.
     *
     * @param apuestaId identificador de la apuesta
     */
    void deleteByApuestaId(Long apuestaId);

    /**
     * Obtiene las participaciones de un usuario junto con
     * la información de la apuesta y su creador.
     *
     * @param usuarioId identificador del usuario
     * @return lista de participaciones con datos relacionados
     */
    @Query("SELECT p FROM Participacion p JOIN FETCH p.apuesta a JOIN FETCH a.creadaPor WHERE p.usuario.id = :usuarioId")
    List<Participacion> findByUsuarioIdConApuesta(@Param("usuarioId") Long usuarioId);

    /**
     * Obtiene las participaciones de varias apuestas junto con
     * la información del usuario asociado.
     *
     * @param apuestaIds lista de identificadores de apuestas
     * @return lista de participaciones con usuario cargado
     */
    @Query("SELECT p FROM Participacion p JOIN FETCH p.usuario WHERE p.apuesta.id IN :apuestaIds")
    List<Participacion> findByApuestaIdInConUsuario(@Param("apuestaIds") List<Long> apuestaIds);

    /**
     * Obtiene el ranking de apuestas según la cantidad
     * de participantes registrados.
     *
     * @param pageable configuración de paginación o límite
     * @return lista de resultados con id, nombre, estado y cantidad de participantes
     */
    @Query("SELECT p.apuesta.id, p.apuesta.nombre, p.apuesta.estado, COUNT(p) " +
           "FROM Participacion p " +
           "GROUP BY p.apuesta.id, p.apuesta.nombre, p.apuesta.estado " +
           "ORDER BY COUNT(p) DESC")
    List<Object[]> findPollaRanking(Pageable pageable);
}