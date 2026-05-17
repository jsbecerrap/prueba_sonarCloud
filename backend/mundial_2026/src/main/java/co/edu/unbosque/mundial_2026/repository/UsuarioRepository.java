package co.edu.unbosque.mundial_2026.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import co.edu.unbosque.mundial_2026.entity.Usuario;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByCorreoUsuario(String correoUsuario);

    List<Usuario> findByActivoTrue();
    @Query("SELECT u.id FROM Usuario u WHERE u.correoUsuario = :correo")
Long findIdByCorreo(@Param("correo") String correo);

@Modifying
@Query(value = "INSERT INTO usuarios_selecciones (usuario_id, seleccion_id) VALUES (:usuarioId, :seleccionId) ON CONFLICT DO NOTHING", nativeQuery = true)
void insertarSeleccion(@Param("usuarioId") Long usuarioId, @Param("seleccionId") Long seleccionId);

@Modifying
@Query(value = "DELETE FROM usuarios_selecciones WHERE usuario_id = :usuarioId AND seleccion_id = :seleccionId", nativeQuery = true)
void eliminarSeleccion(@Param("usuarioId") Long usuarioId, @Param("seleccionId") Long seleccionId);

@Modifying
@Query(value = "INSERT INTO usuarios_preferencias_ubi (usuario_id, estadio_id) VALUES (:usuarioId, :estadioId) ON CONFLICT DO NOTHING", nativeQuery = true)
void insertarEstadio(@Param("usuarioId") Long usuarioId, @Param("estadioId") Long estadioId);

@Modifying
@Query(value = "DELETE FROM usuarios_preferencias_ubi WHERE usuario_id = :usuarioId AND estadio_id = :estadioId", nativeQuery = true)
void eliminarEstadio(@Param("usuarioId") Long usuarioId, @Param("estadioId") Long estadioId);
@Modifying
@Query(value = "INSERT INTO usuarios_ciudad_favoritas (usuario_id, ciudad_id) VALUES (:usuarioId, :ciudadId) ON CONFLICT DO NOTHING", nativeQuery = true)
void insertarCiudad(@Param("usuarioId") Long usuarioId, @Param("ciudadId") Long ciudadId);

@Modifying
@Query(value = "DELETE FROM usuarios_ciudad_favoritas WHERE usuario_id = :usuarioId AND ciudad_id = :ciudadId", nativeQuery = true)
void eliminarCiudad(@Param("usuarioId") Long usuarioId, @Param("ciudadId") Long ciudadId);
}