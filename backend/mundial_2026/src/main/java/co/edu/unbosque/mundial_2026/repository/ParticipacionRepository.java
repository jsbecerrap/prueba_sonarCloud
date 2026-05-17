package co.edu.unbosque.mundial_2026.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import co.edu.unbosque.mundial_2026.entity.Participacion;

@Repository
public interface ParticipacionRepository extends JpaRepository<Participacion, Long> {
    Optional<Participacion> findByUsuarioIdAndApuestaId(Long usuarioId, Long apuestaId);
    List<Participacion> findByApuestaId(Long apuestaId);
    List<Participacion> findByApuestaIdOrderByPuntosDesc(Long apuestaId);
    List<Participacion> findByUsuarioId(Long usuarioId);
    void deleteByApuestaId(Long apuestaId);
    @Query("SELECT p FROM Participacion p JOIN FETCH p.apuesta a JOIN FETCH a.creadaPor WHERE p.usuario.id = :usuarioId")
List<Participacion> findByUsuarioIdConApuesta(@Param("usuarioId") Long usuarioId);

@Query("SELECT p FROM Participacion p JOIN FETCH p.usuario WHERE p.apuesta.id IN :apuestaIds")
List<Participacion> findByApuestaIdInConUsuario(@Param("apuestaIds") List<Long> apuestaIds);
}