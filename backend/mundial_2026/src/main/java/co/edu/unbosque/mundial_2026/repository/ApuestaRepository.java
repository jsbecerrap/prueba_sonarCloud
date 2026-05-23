package co.edu.unbosque.mundial_2026.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import co.edu.unbosque.mundial_2026.entity.Apuesta;

/**
 * Repositorio encargado de gestionar las operaciones de persistencia en la base de datos para la entidad Apuesta
 */
@Repository
public interface ApuestaRepository extends JpaRepository<Apuesta, Long> {

    /**
     * Realiza una consulta para buscar una apuesta específica mediante su código de invitación único
     * 
     * @param codigoInvitacion cadena de texto que representa el código de acceso a la apuesta
     * @return un contenedor Optional que puede albergar la apuesta encontrada si coincide con el código proporcionado
     */
    Optional<Apuesta> findByCodigoInvitacion(String codigoInvitacion);

    /**
     * Recupera un listado completo de apuestas que fueron desarrolladas o registradas por un usuario específico
     * 
     * @param usuarioId identificador numérico único del usuario creador
     * @return lista con todas las apuestas asociadas al identificador del usuario
     */
    List<Apuesta> findByCreadaPorId(Long usuarioId);

    /**
     * Filtra y obtiene todas las apuestas del sistema que coincidan con un estado determinado
     * 
     * @param estado cadena de caracteres que define la situación actual de la apuesta
     * @return colección de apuestas que se encuentran en el estado solicitado
     */
    List<Apuesta> findByEstado(String estado);

    /**
     * Ejecuta una búsqueda combinada para encontrar apuestas con un estado específico cuya fecha de cierre sea anterior al tiempo de referencia indicado
     * 
     * @param estado condición actual en la que debe encontrarse la apuesta
     * @param fecha límite temporal para contrastar y validar que la fecha de cierre ya haya expirado
     * @return listado de apuestas que cumplen simultáneamente con el estado y la expiración del tiempo
     */
    List<Apuesta> findByEstadoAndFechaCierreBefore(String estado, LocalDateTime fecha);

    /**
     * Consulta las apuestas que pertenecen a un estado específico y que todavía no han pasado por el proceso de asignación o cálculo de puntajes
     * 
     * @param estado filtro para determinar la situación operativa de la apuesta
     * @return lista de apuestas pendientes por procesar en su puntuación dentro del sistema
     */
    List<Apuesta> findByEstadoAndPuntosCalculadosFalse(String estado);
}