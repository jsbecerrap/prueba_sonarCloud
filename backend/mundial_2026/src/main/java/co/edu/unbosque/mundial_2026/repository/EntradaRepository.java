package co.edu.unbosque.mundial_2026.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import co.edu.unbosque.mundial_2026.entity.Entrada;

/**
 * Repositorio para la gestión y persistencia de las entradas del mundial incluyendo consultas complejas de agregación y reportes de ventas
 */
public interface EntradaRepository extends JpaRepository<Entrada, Long> {

    /**
     * Ejecuta una consulta personalizada para obtener las entradas de un usuario cargando de forma anticipada la información de los partidos relacionados para optimizar el rendimiento
     * 
     * @param usuarioId identificador único del usuario del cual se requieren las entradas
     * @return lista de entradas asociadas al usuario con sus respectivos partidos cargados
     */
    @Query("SELECT e FROM Entrada e JOIN FETCH e.partido WHERE e.usuario.id = :usuarioId")
    List<Entrada> findByUsuarioId(@Param("usuarioId") Long usuarioId);

    /**
     * Busca las entradas que coinciden con un estado específico y cuyo tiempo de vida de la reserva sea menor a la fecha límite proporcionada
     * 
     * @param estado situación de la reserva de la entrada
     * @param fecha límite temporal para evaluar la expiración del tiempo de vida
     * @return listado de entradas que han superado el tiempo de reserva establecido
     */
    List<Entrada> findByEstadoAndTtlReservaLessThan(String estado, LocalDateTime fecha);

    /**
     * Obtiene el historial de entradas adquiridas por un usuario específico dentro de un rango de fechas determinado
     * 
     * @param usuarioId identificador único del comprador
     * @param inicio fecha y hora inicial del periodo de búsqueda
     * @param fin fecha y hora final del periodo de búsqueda
     * @return lista de entradas compradas en ese intervalo de tiempo
     */
    List<Entrada> findByUsuarioIdAndFechaCompraBetween(Long usuarioId, LocalDateTime inicio, LocalDateTime fin);

    /**
     * Calcula la suma total de entradas asociadas a un partido que se encuentren dentro de un listado de estados específicos devolviendo cero si no hay registros
     * 
     * @param partidoId identificador único del partido a consultar
     * @param estados lista de condiciones válidas para contabilizar las entradas
     * @return cantidad total de entradas acumuladas para ese partido
     */
    @Query("SELECT COALESCE(SUM(e.cantidad), 0) FROM Entrada e " +
           "WHERE e.partido.id = :partidoId AND e.estado IN :estados")
    int sumCantidadByPartidoAndEstados(@Param("partidoId") Long partidoId,
                                       @Param("estados") List<String> estados);

    /**
     * Suma la cantidad de entradas para un partido según una categoría específica convertida a mayúsculas y que pertenezcan a los estados indicados
     * 
     * @param partidoId identificador del partido en cuestión
     * @param categoria sector o tipo de ubicación de la entrada
     * @param estados condiciones en las que debe estar la entrada para ser sumada
     * @return total de entradas que coinciden con el partido la categoría y los estados provistos
     */
    @Query("SELECT COALESCE(SUM(e.cantidad), 0) FROM Entrada e " +
           "WHERE e.partido.id = :partidoId " +
           "AND UPPER(e.categoria) = :categoria " +
           "AND e.estado IN :estados")
    int sumCantidadByPartidoAndCategoriaAndEstados(@Param("partidoId") Long partidoId,
                                                   @Param("categoria") String categoria,
                                                   @Param("estados") List<String> estados);

    /**
     * Recupera las entradas que poseen un estado específico y cuyo tiempo de vida de reserva se encuentra dentro del rango de fechas estipulado
     * 
     * @param estado situación operativa de la entrada
     * @param inicio punto de partida temporal del rango de reserva
     * @param fin punto final temporal del rango de reserva
     * @return listado de entradas con reservas programadas en ese espacio de tiempo
     */
    List<Entrada> findByEstadoAndTtlReservaBetween(String estado, LocalDateTime inicio, LocalDateTime fin);

    /**
     * Obtiene la sumatoria de las cantidades de entradas vendidas o reservadas filtrando por partido categoría y una fila específica del estadio
     * 
     * @param partidoId identificador único del partido
     * @param categoria nivel o sección del estadio
     * @param fila hilera específica de asientos dentro de la sección
     * @param estados conjunto de estados requeridos para la consulta
     * @return número total de entradas ocupadas en esa fila y categoría para el partido
     */
    @Query("SELECT COALESCE(SUM(e.cantidad), 0) FROM Entrada e " +
           "WHERE e.partido.id = :partidoId " +
           "AND UPPER(e.categoria) = :categoria " +
           "AND UPPER(e.fila) = :fila " +
           "AND e.estado IN :estados")
    int sumCantidadByPartidoCategoriaYFila(@Param("partidoId") Long partidoId,
                                           @Param("categoria") String categoria,
                                           @Param("fila") String fila,
                                           @Param("estados") List<String> estados);

    /**
     * Determina el número máximo de asiento ocupado en una fila y categoría específicas para un partido calculando la posición final a partir del asiento inicial y la cantidad adquirida
     * 
     * @param partidoId identificador del partido evaluado
     * @param categoria sección de ubicación dentro del escenario deportivo
     * @param fila hilera de asientos analizada
     * @return el índice del último asiento asignado o cero si no hay registros válidos
     */
    @Query("SELECT COALESCE(MAX(e.asientoInicio + e.cantidad - 1), 0) FROM Entrada e " +
           "WHERE e.partido.id = :partidoId " +
           "AND UPPER(e.categoria) = :categoria " +
           "AND UPPER(e.fila) = :fila " +
           "AND e.asientoInicio IS NOT NULL")
    int maxAsientoFinByPartidoCategoriaYFila(@Param("partidoId") Long partidoId,
                                             @Param("categoria") String categoria,
                                             @Param("fila") String fila);

    /**
     * Ejecuta una métrica global para obtener la cantidad total de todas las entradas vendidas en el sistema que ya cuentan con un registro de pago efectivo
     * 
     * @return total acumulado de boletas pagadas en la plataforma
     */
    @Query("SELECT COALESCE(SUM(e.cantidad), 0) FROM Entrada e WHERE e.fechaPago IS NOT NULL")
    Long sumEntradasVendidas();

    /**
     * Genera un reporte agrupado por partido estadístico detallando el identificador las selecciones participantes la ronda el estadio el volumen de entradas vendidas y los ingresos totales ordenados de mayor a menor demanda
     * 
     * @return lista de arreglos de objetos con la información resumida de ventas por cada partido
     */
    @Query("SELECT e.partido.id, e.partido.seleccionLocal, e.partido.seleccionVisitante, " +
           "e.partido.ronda, e.partido.estadio, " +
           "SUM(e.cantidad), SUM(e.precio) " +
           "FROM Entrada e " +
           "WHERE e.fechaPago IS NOT NULL " +
           "GROUP BY e.partido.id, e.partido.seleccionLocal, e.partido.seleccionVisitante, " +
           "e.partido.ronda, e.partido.estadio " +
           "ORDER BY SUM(e.cantidad) DESC")
    List<Object[]> findEntradasPorPartido();

    /**
     * Obtiene un ranking paginado de los usuarios que más dinero han invertido en entradas detallando sus datos personales la cantidad comprada y el monto total ordenado de forma descendente
     * 
     * @param pageable configuración de paginación y límites para el reporte
     * @return lista de arreglos de objetos con los datos de consumo de los clientes top del sistema
     */
    @Query("SELECT e.usuario.id, e.usuario.nombre, e.usuario.apellido, e.usuario.correoUsuario, " +
           "SUM(e.cantidad), COALESCE(SUM(e.precio), 0.0) " +
           "FROM Entrada e " +
           "WHERE e.fechaPago IS NOT NULL " +
           "GROUP BY e.usuario.id, e.usuario.nombre, e.usuario.apellido, e.usuario.correoUsuario " +
           "ORDER BY COALESCE(SUM(e.precio), 0.0) DESC")
    List<Object[]> findTopUsuariosEntrada(Pageable pageable);
}