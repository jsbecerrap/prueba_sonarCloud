package co.edu.unbosque.mundial_2026.service;

import java.util.List;
import java.util.Map;

import co.edu.unbosque.mundial_2026.dto.request.UsuarioActualizarRequestDTO;
import co.edu.unbosque.mundial_2026.dto.request.UsuarioRequestDTO;
import co.edu.unbosque.mundial_2026.dto.response.PreferenciaDTO;
import co.edu.unbosque.mundial_2026.dto.response.UsuarioResponseDTO;
import co.edu.unbosque.mundial_2026.entity.Usuario;

/**
 * Contrato del servicio de usuarios — gestiona el registro, consulta, actualización
 * y preferencias personales de cada usuario en el sistema
 */
public interface UsuarioService {

    /**
     * Retorna la lista completa de usuarios registrados en el sistema
     *
     * @return lista de todos los usuarios
     */
    List<UsuarioResponseDTO> listarTodos();

    /**
     * Registra un nuevo usuario con rol de cliente en el sistema
     *
     * @param dto datos del usuario a registrar
     * @return usuario creado con su información básica
     */
    UsuarioResponseDTO registrarUsuario(UsuarioRequestDTO dto);

    /**
     * Obtiene el detalle de un usuario por su ID
     *
     * @param usuarioId ID del usuario
     * @return datos del usuario
     */
    UsuarioResponseDTO obtenerUsuario(Long usuarioId);

    /**
     * Obtiene el detalle de un usuario por su correo electrónico
     *
     * @param correo correo del usuario
     * @return datos del usuario
     */
    UsuarioResponseDTO obtenerPorCorreo(String correo);

    /**
     * Elimina lógicamente un usuario del sistema
     *
     * @param usuarioId ID del usuario a eliminar
     */
    void eliminarUsuario(Long usuarioId);

    /**
     * Actualiza los datos del perfil del usuario — retorna un mapa con los campos
     * actualizados y sus nuevos valores para confirmar los cambios realizados
     *
     * @param correoUsuario correo del usuario que actualiza su perfil
     * @param dto           nuevos datos del perfil
     * @return mapa con los campos modificados y sus valores actualizados
     */
    Map<String, Object> actualizarPerfil(String correoUsuario, UsuarioActualizarRequestDTO dto);

    /**
     * Agrega una o varias selecciones a la lista de favoritas del usuario
     *
     * @param correo correo del usuario
     * @param ids    lista de IDs de selecciones a agregar
     */
    void agregarSeleccion(String correo, List<Long> ids);

    /**
     * Agrega uno o varios estadios a la lista de favoritos del usuario
     *
     * @param correo correo del usuario
     * @param ids    lista de IDs de estadios a agregar
     */
    void agregarEstadio(String correo, List<Long> ids);

    /**
     * Agrega una o varias ciudades a la lista de favoritas del usuario
     *
     * @param correo correo del usuario
     * @param ids    lista de IDs de ciudades a agregar
     */
    void agregarCiudad(String correo, List<Long> ids);

    /**
     * Retorna las selecciones marcadas como favoritas por el usuario
     *
     * @param correo correo del usuario
     * @return lista de selecciones favoritas
     */
    List<PreferenciaDTO> seleccionesUsuario(String correo);

    /**
     * Elimina una selección de la lista de favoritas del usuario
     *
     * @param correo      correo del usuario
     * @param seleccionId ID de la selección a eliminar
     */
    void eliminarSeleccion(String correo, Long seleccionId);

    /**
     * Retorna los estadios marcados como favoritos por el usuario
     *
     * @param correo correo del usuario
     * @return lista de estadios favoritos
     */
    List<PreferenciaDTO> estadiosUsuario(String correo);

    /**
     * Elimina un estadio de la lista de favoritos del usuario
     *
     * @param correo    correo del usuario
     * @param estadioId ID del estadio a eliminar
     */
    void eliminarEstadio(String correo, Long estadioId);

    /**
     * Retorna las ciudades marcadas como favoritas por el usuario
     *
     * @param correo correo del usuario
     * @return lista de ciudades favoritas
     */
    List<PreferenciaDTO> ciudadesUsuario(String correo);

    /**
     * Elimina una ciudad de la lista de favoritas del usuario
     *
     * @param correo   correo del usuario
     * @param ciudadId ID de la ciudad a eliminar
     */
    void eliminarCiudad(String correo, Long ciudadId);

    /**
     * Retorna el catálogo completo de estadios disponibles para marcar como favoritos
     *
     * @return lista de estadios disponibles
     */
    List<PreferenciaDTO> listarEstadios();

    /**
     * Retorna el catálogo completo de ciudades disponibles para marcar como favoritas
     *
     * @return lista de ciudades disponibles
     */
    List<PreferenciaDTO> listarCiudades();

    /**
     * Obtiene la entidad {@link Usuario} por su ID — pensado para uso interno
     * entre servicios, no para exponer en el controlador
     *
     * @param usuarioId ID del usuario
     * @return entidad {@link Usuario}
     */
    Usuario obtenerEntidadPorId(Long usuarioId);

    /**
     * Obtiene la entidad {@link Usuario} por su correo — pensado para uso interno
     * entre servicios, no para exponer en el controlador
     *
     * @param correo correo del usuario
     * @return entidad {@link Usuario}
     */
    Usuario obtenerEntidadPorCorreo(String correo);

    /**
     * Actualiza el token FCM del usuario para el envío de notificaciones push
     * a su dispositivo móvil
     *
     * @param correo   correo del usuario
     * @param fcmToken nuevo token FCM del dispositivo
     */
    void actualizarFcmToken(String correo, String fcmToken);

    /**
     * Registra un nuevo usuario asignándole directamente el rol de administrador —
     * operación exclusiva para uso desde el panel de administración
     *
     * @param dto datos del usuario a registrar
     * @return usuario administrador creado
     */
    UsuarioResponseDTO registrarUsuarioComoAdmin(UsuarioRequestDTO dto);
}