package co.edu.unbosque.mundial_2026.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import co.edu.unbosque.mundial_2026.entity.Usuario;

/**
 * Repositorio encargado de la gestión y consulta de los usuarios registrados en el sistema
 */
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    /**
     * Busca un usuario según su correo electrónico registrado
     *
     * @param correoUsuario correo electrónico del usuario consultado
     * @return usuario encontrado si existe
     */
    Optional<Usuario> findByCorreoUsuario(String correoUsuario);

    /**
     * Obtiene todos los usuarios que se encuentran activos
     *
     * @return lista de usuarios activos
     */
    List<Usuario> findByActivoTrue();

    /**
     * Ejecuta una consulta personalizada para obtener el identificador de un usuario a partir de su correo electrónico
     *
     * @param correo correo electrónico del usuario consultado
     * @return identificador del usuario encontrado
     */
    @Query("SELECT u.id FROM Usuario u WHERE u.correoUsuario = :correo")
    Long findIdByCorreo(@Param("correo") String correo);

    /**
     * Inserta la relación entre un usuario y una selección en la tabla intermedia
     * Si la relación ya existe no realiza ninguna acción
     *
     * @param usuarioId identificador del usuario
     * @param seleccionId identificador de la selección
     */
    @Modifying
    @Query(value = "INSERT INTO usuarios_selecciones (usuario_id, seleccion_id) VALUES (:usuarioId, :seleccionId) ON CONFLICT DO NOTHING", nativeQuery = true)
    void insertarSeleccion(@Param("usuarioId") Long usuarioId, @Param("seleccionId") Long seleccionId);

    /**
     * Elimina la relación entre un usuario y una selección de la tabla intermedia
     *
     * @param usuarioId identificador del usuario
     * @param seleccionId identificador de la selección
     */
    @Modifying
    @Query(value = "DELETE FROM usuarios_selecciones WHERE usuario_id = :usuarioId AND seleccion_id = :seleccionId", nativeQuery = true)
    void eliminarSeleccion(@Param("usuarioId") Long usuarioId, @Param("seleccionId") Long seleccionId);

    /**
     * Inserta la relación entre un usuario y un estadio en la tabla de preferencias de ubicación
     * Si la relación ya existe no realiza ninguna acción
     *
     * @param usuarioId identificador del usuario
     * @param estadioId identificador del estadio
     */
    @Modifying
    @Query(value = "INSERT INTO usuarios_preferencias_ubi (usuario_id, estadio_id) VALUES (:usuarioId, :estadioId) ON CONFLICT DO NOTHING", nativeQuery = true)
    void insertarEstadio(@Param("usuarioId") Long usuarioId, @Param("estadioId") Long estadioId);

    /**
     * Elimina la relación entre un usuario y un estadio de la tabla de preferencias de ubicación
     *
     * @param usuarioId identificador del usuario
     * @param estadioId identificador del estadio
     */
    @Modifying
    @Query(value = "DELETE FROM usuarios_preferencias_ubi WHERE usuario_id = :usuarioId AND estadio_id = :estadioId", nativeQuery = true)
    void eliminarEstadio(@Param("usuarioId") Long usuarioId, @Param("estadioId") Long estadioId);

    /**
     * Inserta la relación entre un usuario y una ciudad en la tabla de ciudades favoritas
     * Si la relación ya existe no realiza ninguna acción
     *
     * @param usuarioId identificador del usuario
     * @param ciudadId identificador de la ciudad
     */
    @Modifying
    @Query(value = "INSERT INTO usuarios_ciudad_favoritas (usuario_id, ciudad_id) VALUES (:usuarioId, :ciudadId) ON CONFLICT DO NOTHING", nativeQuery = true)
    void insertarCiudad(@Param("usuarioId") Long usuarioId, @Param("ciudadId") Long ciudadId);

    /**
     * Elimina la relación entre un usuario y una ciudad de la tabla de ciudades favoritas
     *
     * @param usuarioId identificador del usuario
     * @param ciudadId identificador de la ciudad
     */
    @Modifying
    @Query(value = "DELETE FROM usuarios_ciudad_favoritas WHERE usuario_id = :usuarioId AND ciudad_id = :ciudadId", nativeQuery = true)
    void eliminarCiudad(@Param("usuarioId") Long usuarioId, @Param("ciudadId") Long ciudadId);

    /**
     * Ejecuta una consulta personalizada para obtener un usuario cargando de forma anticipada la información de su rol
     * Esto permite recuperar el usuario junto con su rol en una sola consulta
     *
     * @param correo correo electrónico del usuario consultado
     * @return usuario encontrado con su rol asociado si existe
     */
    @Query("SELECT u FROM Usuario u JOIN FETCH u.rol WHERE u.correoUsuario = :correo")
    Optional<Usuario> findByCorreoUsuarioConRol(@Param("correo") String correo);

}