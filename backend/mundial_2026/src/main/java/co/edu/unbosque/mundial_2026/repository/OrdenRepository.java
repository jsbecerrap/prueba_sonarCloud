package co.edu.unbosque.mundial_2026.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import co.edu.unbosque.mundial_2026.entity.Orden;

/**
 * Repositorio encargado de la gestión y consulta de las órdenes registradas en el sistema
 */
public interface OrdenRepository extends JpaRepository<Orden, Long> {

    /**
     * Busca una orden de un usuario que coincida con un estado específico
     *
     * @param usuarioId identificador del usuario consultado
     * @param estado estado de la orden a buscar
     * @return orden encontrada si existe
     */
    Optional<Orden> findByUsuarioIdAndEstado(Long usuarioId, String estado);

    /**
     * Ejecuta una consulta personalizada para obtener las órdenes de un usuario excluyendo un estado específico
     * La consulta carga de forma anticipada los ítems los productos sus categorías y el método de pago para recuperar toda la información relacionada
     *
     * @param usuarioId identificador del usuario consultado
     * @param estado estado que debe excluirse de la búsqueda
     * @return lista de órdenes con sus detalles completos asociados
     */
    @Query("SELECT DISTINCT o FROM Orden o " +
           "LEFT JOIN FETCH o.items i " +
           "LEFT JOIN FETCH i.producto p " +
           "LEFT JOIN FETCH p.categoria " +
           "LEFT JOIN FETCH o.metodoPago " +
           "WHERE o.usuario.id = :usuarioId AND o.estado <> :estado")
    List<Orden> findByUsuarioIdAndEstadoNot(@Param("usuarioId") Long usuarioId,
                                            @Param("estado") String estado);

    /**
     * Obtiene el historial de órdenes de un usuario filtrando por un conjunto de estados específicos
     * La consulta incluye el método de pago y organiza los resultados desde la orden más reciente
     *
     * @param usuarioId identificador del usuario consultado
     * @param estados lista de estados permitidos en la búsqueda
     * @return historial de órdenes que cumplen con los estados indicados
     */
    @Query("SELECT o FROM Orden o " +
           "LEFT JOIN FETCH o.metodoPago " +
           "WHERE o.usuario.id = :usuarioId AND o.estado IN :estados " +
           "ORDER BY o.fechaCreacion DESC")
    List<Orden> findHistorialByUsuarioIdAndEstadoIn(
        @Param("usuarioId") Long usuarioId,
        @Param("estados") List<String> estados);

    /**
     * Obtiene las órdenes que tienen un estado específico y fueron creadas antes de una fecha determinada
     *
     * @param estado estado de las órdenes a consultar
     * @param fecha fecha límite de creación
     * @return lista de órdenes que cumplen la condición indicada
     */
    List<Orden> findByEstadoAndFechaCreacionBefore(String estado, LocalDateTime fecha);

    /**
     * Obtiene las órdenes que tienen un estado específico creadas antes de una fecha y que aún no han sido notificadas como abandonadas
     *
     * @param estado estado de las órdenes a consultar
     * @param fecha fecha límite de creación
     * @return lista de órdenes pendientes de notificación de abandono
     */
    List<Orden> findByEstadoAndFechaCreacionBeforeAndNotificadoAbandonadoFalse(
        String estado, LocalDateTime fecha);

    /**
     * Cuenta el total de órdenes que ya tienen un pago registrado
     *
     * @return cantidad total de órdenes pagadas
     */
    long countByFechaPagoIsNotNull();

    /**
     * Calcula la suma total de ingresos generados por las órdenes que ya fueron pagadas
     *
     * @return valor acumulado de ingresos registrados
     */
    @Query("SELECT COALESCE(SUM(o.total), 0.0) FROM Orden o WHERE o.fechaPago IS NOT NULL")
    Double sumIngresoTotal();

    /**
     * Genera un reporte agrupado por método de pago calculando la cantidad de órdenes y el ingreso total asociado a cada uno
     * La consulta incluye órdenes sin método registrado y organiza los resultados desde el mayor ingreso generado
     *
     * @return lista con el resumen de ingresos agrupado por método de pago
     */
    @Query("SELECT COALESCE(mp.tipo, 'Sin método'), COUNT(o), COALESCE(SUM(o.total), 0.0) " +
           "FROM Orden o LEFT JOIN o.metodoPago mp " +
           "WHERE o.fechaPago IS NOT NULL " +
           "GROUP BY mp.tipo " +
           "ORDER BY COALESCE(SUM(o.total), 0.0) DESC")
    List<Object[]> findIngresosPorMetodoPago();

    /**
     * Genera un ranking de los usuarios con mayor gasto en compras calculando la cantidad de órdenes pagadas y el monto total invertido
     * La consulta organiza los resultados desde el usuario con mayor ingreso generado y permite limitar resultados con paginación
     *
     * @param pageable configuración de paginación y límite de resultados
     * @return lista con la información resumida de los usuarios con mayor consumo
     */
    @Query("SELECT o.usuario.id, o.usuario.nombre, o.usuario.apellido, o.usuario.correoUsuario, " +
           "COUNT(o), COALESCE(SUM(o.total), 0.0) " +
           "FROM Orden o " +
           "WHERE o.fechaPago IS NOT NULL " +
           "GROUP BY o.usuario.id, o.usuario.nombre, o.usuario.apellido, o.usuario.correoUsuario " +
           "ORDER BY COALESCE(SUM(o.total), 0.0) DESC")
    List<Object[]> findTopUsuariosSouvenir(Pageable pageable);
}