package co.edu.unbosque.mundial_2026.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import co.edu.unbosque.mundial_2026.entity.Pronostico;

/**
 * Repositorio para gestionar las operaciones de acceso a datos
 * de la entidad Pronostico.
 */
@Repository
public interface PronosticoRepository extends JpaRepository<Pronostico, Long> {

    /**
     * Obtiene todos los pronósticos asociados a una apuesta.
     *
     * @param apuestaId identificador de la apuesta
     * @return lista de pronósticos encontrados
     */
    List<Pronostico> findByApuestaId(Long apuestaId);

    /**
     * Obtiene los pronósticos de una apuesta realizados
     * por un usuario específico.
     *
     * @param apuestaId identificador de la apuesta
     * @param usuarioId identificador del usuario
     * @return lista de pronósticos encontrados
     */
    List<Pronostico> findByApuestaIdAndUsuarioId(Long apuestaId, Long usuarioId);

    /**
     * Elimina todos los pronósticos asociados a una apuesta.
     *
     * @param apuestaId identificador de la apuesta
     */
    void deleteByApuestaId(Long apuestaId);

    /**
     * Obtiene los partidos con mayor cantidad de pronósticos
     * registrados.
     *
     * @param pageable configuración de paginación o límite
     * @return lista de resultados con id, selecciones, ronda y cantidad de pronósticos
     */
    @Query("SELECT p.partido.id, p.partido.seleccionLocal, p.partido.seleccionVisitante, " +
           "p.partido.ronda, COUNT(p) " +
           "FROM Pronostico p " +
           "GROUP BY p.partido.id, p.partido.seleccionLocal, p.partido.seleccionVisitante, p.partido.ronda " +
           "ORDER BY COUNT(p) DESC")
    List<Object[]> findPartidosMasApostados(Pageable pageable);
}