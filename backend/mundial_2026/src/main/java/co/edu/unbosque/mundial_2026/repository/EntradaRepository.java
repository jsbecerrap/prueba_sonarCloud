package co.edu.unbosque.mundial_2026.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import co.edu.unbosque.mundial_2026.entity.Entrada;

public interface EntradaRepository extends JpaRepository<Entrada, Long> {

    @Query("SELECT e FROM Entrada e JOIN FETCH e.partido WHERE e.usuario.id = :usuarioId")
    List<Entrada> findByUsuarioId(@Param("usuarioId") Long usuarioId);

    List<Entrada> findByEstadoAndTtlReservaLessThan(String estado, LocalDateTime fecha);

    List<Entrada> findByUsuarioIdAndFechaCompraBetween(Long usuarioId, LocalDateTime inicio, LocalDateTime fin);
   @Query("SELECT COALESCE(SUM(e.cantidad), 0) FROM Entrada e WHERE e.partido.id = :partidoId AND e.estado IN :estados")
int sumCantidadByPartidoAndEstados(@Param("partidoId") Long partidoId, @Param("estados") List<String> estados);

@Query("SELECT COALESCE(SUM(e.cantidad), 0) FROM Entrada e WHERE e.partido.id = :partidoId AND UPPER(e.categoria) = :categoria AND e.estado IN :estados")
int sumCantidadByPartidoAndCategoriaAndEstados(@Param("partidoId") Long partidoId, @Param("categoria") String categoria, @Param("estados") List<String> estados);
}