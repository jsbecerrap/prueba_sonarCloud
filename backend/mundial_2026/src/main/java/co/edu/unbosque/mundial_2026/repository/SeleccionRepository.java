package co.edu.unbosque.mundial_2026.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import co.edu.unbosque.mundial_2026.entity.Seleccion;

/**
 * Repositorio para gestionar las operaciones de acceso a datos
 * de la entidad Seleccion.
 */
@Repository
public interface SeleccionRepository extends JpaRepository<Seleccion, Long> {

    /**
     * Busca una selección por su identificador único.
     *
     * @param id identificador de la selección
     * @return selección encontrada si existe
     */
    Optional<Seleccion> findById(Long id);

    /**
     * Obtiene una lista de selecciones a partir de un conjunto de identificadores.
     *
     * @param ids colección de identificadores de selecciones
     * @return lista de selecciones encontradas
     */
    List<Seleccion> findAllById(Iterable<Long> ids);
}