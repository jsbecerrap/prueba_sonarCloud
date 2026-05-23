package co.edu.unbosque.mundial_2026.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import co.edu.unbosque.mundial_2026.entity.Categoria;

/**
 * Repositorio encargado de gestionar el acceso a datos y la persistencia de las diferentes categorías en el sistema
 */
public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

    /**
     * Realiza una búsqueda en la base de datos para localizar una categoría utilizando su nombre exacto
     * 
     * @param nombre cadena de texto que identifica de forma única a la categoría buscada
     * @return un objeto Optional que contiene la categoría si fue encontrada en el sistema
     */
    Optional<Categoria> findByNombre(String nombre);

    /**
     * Consulta y devuelve un listado con todas las categorías que se encuentran actualmente en estado habilitado o vigente
     * 
     * @return lista de objetos de tipo Categoria cuyo indicador de actividad está marcado como verdadero
     */
    List<Categoria> findByActivoTrue();

    /**
     * Busca una categoría específica mediante su identificador único asegurando que se encuentre en estado vigente al mismo tiempo
     * 
     * @param id código numérico y único que identifica a la categoría dentro de la base de datos
     * @return un objeto Optional con la categoría que cumple con el identificador y el estado activo
     */
    Optional<Categoria> findByIdAndActivoTrue(Long id);
}