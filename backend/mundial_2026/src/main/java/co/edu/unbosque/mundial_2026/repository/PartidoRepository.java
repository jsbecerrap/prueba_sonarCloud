package co.edu.unbosque.mundial_2026.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import co.edu.unbosque.mundial_2026.entity.Partido;

/**
 * Repositorio para gestionar las operaciones de acceso a datos
 * de la entidad Partido.
 */
@Repository
public interface PartidoRepository extends JpaRepository<Partido, Long> {

    /**
     * Busca partidos en los que el nombre proporcionado coincida
     * parcial o totalmente con la selección local o visitante.
     *
     * @param nombre nombre de la selección a buscar
     * @return lista de partidos encontrados
     */
    @Query("SELECT p FROM Partido p WHERE LOWER(p.seleccionLocal) LIKE LOWER(CONCAT('%', :nombre, '%')) " +
           "OR LOWER(p.seleccionVisitante) LIKE LOWER(CONCAT('%', :nombre, '%'))")
    List<Partido> findBySeleccion(@Param("nombre") String nombre);

    /**
     * Busca partidos cuyo estadio coincida parcial
     * o totalmente con el nombre proporcionado.
     *
     * @param nombre nombre del estadio a buscar
     * @return lista de partidos encontrados
     */
    @Query("SELECT p FROM Partido p WHERE LOWER(p.estadio) LIKE LOWER(CONCAT('%', :nombre, '%'))")
    List<Partido> findByEstadio(@Param("nombre") String nombre);
}