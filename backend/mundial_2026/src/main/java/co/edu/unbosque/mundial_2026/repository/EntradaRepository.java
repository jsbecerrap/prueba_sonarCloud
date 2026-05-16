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
}