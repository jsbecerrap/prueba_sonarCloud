package co.edu.unbosque.mundial_2026.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import co.edu.unbosque.mundial_2026.entity.Entrada;

public interface EntradaRepository extends JpaRepository<Entrada, Long> {

    @Query("SELECT e FROM Entrada e JOIN FETCH e.partido WHERE e.usuario.id = :usuarioId")
    List<Entrada> findByUsuarioId(@Param("usuarioId") Long usuarioId);

    List<Entrada> findByEstadoAndTtlReservaLessThan(String estado, LocalDateTime fecha);

    List<Entrada> findByUsuarioIdAndFechaCompraBetween(Long usuarioId, LocalDateTime inicio, LocalDateTime fin);

    @Query("SELECT COALESCE(SUM(e.cantidad), 0) FROM Entrada e " +
           "WHERE e.partido.id = :partidoId AND e.estado IN :estados")
    int sumCantidadByPartidoAndEstados(@Param("partidoId") Long partidoId,
                                       @Param("estados") List<String> estados);

    @Query("SELECT COALESCE(SUM(e.cantidad), 0) FROM Entrada e " +
           "WHERE e.partido.id = :partidoId " +
           "AND UPPER(e.categoria) = :categoria " +
           "AND e.estado IN :estados")
    int sumCantidadByPartidoAndCategoriaAndEstados(@Param("partidoId") Long partidoId,
                                                   @Param("categoria") String categoria,
                                                   @Param("estados") List<String> estados);

    List<Entrada> findByEstadoAndTtlReservaBetween(String estado, LocalDateTime inicio, LocalDateTime fin);

    @Query("SELECT COALESCE(SUM(e.cantidad), 0) FROM Entrada e " +
           "WHERE e.partido.id = :partidoId " +
           "AND UPPER(e.categoria) = :categoria " +
           "AND UPPER(e.fila) = :fila " +
           "AND e.estado IN :estados")
    int sumCantidadByPartidoCategoriaYFila(@Param("partidoId") Long partidoId,
                                           @Param("categoria") String categoria,
                                           @Param("fila") String fila,
                                           @Param("estados") List<String> estados);

    @Query("SELECT COALESCE(MAX(e.asientoInicio + e.cantidad - 1), 0) FROM Entrada e " +
           "WHERE e.partido.id = :partidoId " +
           "AND UPPER(e.categoria) = :categoria " +
           "AND UPPER(e.fila) = :fila " +
           "AND e.asientoInicio IS NOT NULL")
    int maxAsientoFinByPartidoCategoriaYFila(@Param("partidoId") Long partidoId,
                                             @Param("categoria") String categoria,
                                             @Param("fila") String fila);

    @Query("SELECT COALESCE(SUM(e.cantidad), 0) FROM Entrada e WHERE e.fechaPago IS NOT NULL")
    Long sumEntradasVendidas();

    @Query("SELECT e.partido.id, e.partido.seleccionLocal, e.partido.seleccionVisitante, " +
           "e.partido.ronda, e.partido.estadio, " +
           "SUM(e.cantidad), SUM(e.precio) " +
           "FROM Entrada e " +
           "WHERE e.fechaPago IS NOT NULL " +
           "GROUP BY e.partido.id, e.partido.seleccionLocal, e.partido.seleccionVisitante, " +
           "e.partido.ronda, e.partido.estadio " +
           "ORDER BY SUM(e.cantidad) DESC")
    List<Object[]> findEntradasPorPartido();

    @Query("SELECT e.usuario.id, e.usuario.nombre, e.usuario.apellido, e.usuario.correoUsuario, " +
           "SUM(e.cantidad), COALESCE(SUM(e.precio), 0.0) " +
           "FROM Entrada e " +
           "WHERE e.fechaPago IS NOT NULL " +
           "GROUP BY e.usuario.id, e.usuario.nombre, e.usuario.apellido, e.usuario.correoUsuario " +
           "ORDER BY COALESCE(SUM(e.precio), 0.0) DESC")
    List<Object[]> findTopUsuariosEntrada(Pageable pageable);
}