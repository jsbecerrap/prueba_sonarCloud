package co.edu.unbosque.mundial_2026.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.unbosque.mundial_2026.dto.request.UsuarioActualizarRequestDTO;
import co.edu.unbosque.mundial_2026.dto.request.UsuarioRequestDTO;
import co.edu.unbosque.mundial_2026.dto.response.PreferenciaDTO;
import co.edu.unbosque.mundial_2026.dto.response.UsuarioResponseDTO;
import co.edu.unbosque.mundial_2026.entity.Rol;
import co.edu.unbosque.mundial_2026.entity.Usuario;
import co.edu.unbosque.mundial_2026.exception.ContrasenaIncorrectaException;
import co.edu.unbosque.mundial_2026.exception.CorreoEnUsoException;
import co.edu.unbosque.mundial_2026.exception.RolNotFoundException;
import co.edu.unbosque.mundial_2026.exception.UsuarioNotFoundException;
import co.edu.unbosque.mundial_2026.repository.CiudadRepository;
import co.edu.unbosque.mundial_2026.repository.EstadioRepository;
import co.edu.unbosque.mundial_2026.repository.RolRepository;
import co.edu.unbosque.mundial_2026.repository.UsuarioRepository;
import java.util.UUID;

/**
 * Implementación del servicio encargado de gestionar los usuarios de la plataforma
 * del Mundial 2026.
 * Cubre el ciclo completo del usuario: registro, consulta, actualización de perfil,
 * desactivación y gestión de preferencias (selecciones, estadios y ciudades favoritas).
 * La contraseña siempre se almacena hasheada. Si el usuario cambia su correo o contraseña,
 * se exige verificar la contraseña actual antes de aplicar el cambio.
 * Cuando se registra el token FCM por primera vez, se envía la notificación de bienvenida.
 * Cada operación relevante queda registrada en auditoría
 */
@Service
public class UsuarioServiceImpl implements UsuarioService {

    private static final String USUARIO_NO_ENCONTRADO = "Usuario no encontrado";
    private static final String ENTIDAD_USUARIO = "Usuario";
    private final UsuarioRepository repository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;
    private final EstadioRepository estadioRepository;
    private final CiudadRepository ciudadRepository;
    private final EventoAuditoriaService auditoriaService;
    private final NotificacionService notificacionService;

    public UsuarioServiceImpl(UsuarioRepository repository, RolRepository rolRepository,
            PasswordEncoder passwordEncoder,
            EstadioRepository estadioRepository, CiudadRepository ciudadRepository,
            NotificacionService notificacionService, EventoAuditoriaService auditoriaService) {
        this.repository = repository;
        this.rolRepository = rolRepository;
        this.passwordEncoder = passwordEncoder;
        this.estadioRepository = estadioRepository;
        this.ciudadRepository = ciudadRepository;
        this.notificacionService = notificacionService;
        this.auditoriaService = auditoriaService;
    }

    /**
     * Retorna todos los usuarios registrados en el sistema sin ningún filtro
     *
     * @return lista de {@link UsuarioResponseDTO} con todos los usuarios
     */
    @Override
    @Transactional(readOnly = true)
    public List<UsuarioResponseDTO> listarTodos() {
        return repository.findAll().stream()
                .map(this::toResponseDTO)
                .toList();
    }

    /**
     * Registra un nuevo usuario con el rol ROLE_USUARIO por defecto.
     * Verifica que el correo no esté en uso y hashea la contraseña antes de persistir.
     * La operación queda registrada en auditoría
     *
     * @param dto datos del usuario a registrar: correo, contraseña, nombre y apellido
     * @return {@link UsuarioResponseDTO} con la información del usuario creado
     * @throws CorreoEnUsoException  si el correo ya está registrado
     * @throws RolNotFoundException  si el rol ROLE_USUARIO no existe en la base de datos
     */
    @Override
    @Transactional
    public UsuarioResponseDTO registrarUsuario(final UsuarioRequestDTO dto) {
        if (repository.findByCorreoUsuario(dto.getCorreoUsuario()).isPresent()) {
            throw new CorreoEnUsoException("El correo ya está en uso: " + dto.getCorreoUsuario());
        }

        final String nombreRol = "ROLE_USUARIO";
        final Rol rol = rolRepository.findByNombre(nombreRol)
                .orElseThrow(() -> new RolNotFoundException("Rol no encontrado: " + nombreRol));

        final Usuario usuario = new Usuario();
        usuario.setCorreoUsuario(dto.getCorreoUsuario());
        usuario.setContrasena(passwordEncoder.encode(dto.getContrasena()));
        usuario.setRol(rol);
        usuario.setNombre(dto.getNombre());
        usuario.setApellido(dto.getApellido());
        Usuario guardado = repository.save(usuario);

        auditoriaService.registrar(
                "USUARIO_REGISTRADO",
                "Nuevo usuario registrado: " + guardado.getCorreoUsuario() + " con rol " + nombreRol,
                guardado.getId(),
                UUID.randomUUID().toString(),
                ENTIDAD_USUARIO);

        return toResponseDTO(guardado);
    }

    /**
     * Busca y retorna un usuario por su id
     *
     * @param usuarioId id del usuario a consultar
     * @return {@link UsuarioResponseDTO} con los datos del usuario
     * @throws UsuarioNotFoundException si el usuario no existe
     */
    @Override
    @Transactional(readOnly = true)
    public UsuarioResponseDTO obtenerUsuario(final Long usuarioId) {
        return toResponseDTO(repository.findById(usuarioId)
                .orElseThrow(() -> new UsuarioNotFoundException(USUARIO_NO_ENCONTRADO)));
    }

    /**
     * Busca y retorna un usuario por su correo electrónico
     *
     * @param correo correo del usuario a consultar
     * @return {@link UsuarioResponseDTO} con los datos del usuario
     * @throws UsuarioNotFoundException si no existe un usuario con ese correo
     */
    @Override
    @Transactional(readOnly = true)
    public UsuarioResponseDTO obtenerPorCorreo(final String correo) {
        return toResponseDTO(repository.findByCorreoUsuario(correo)
                .orElseThrow(() -> new UsuarioNotFoundException(USUARIO_NO_ENCONTRADO)));
    }

    /**
     * Registra un nuevo usuario desde el panel de administración, permitiendo asignar
     * un rol específico. Si no se indica rol, se asigna ROLE_USUARIO por defecto.
     * Verifica que el correo no esté en uso y hashea la contraseña antes de persistir.
     * La operación queda registrada en auditoría indicando que fue creado por un admin
     *
     * @param dto datos del usuario a registrar, incluyendo el rol deseado
     * @return {@link UsuarioResponseDTO} con la información del usuario creado
     * @throws CorreoEnUsoException si el correo ya está registrado
     * @throws RolNotFoundException si el rol indicado no existe en la base de datos
     */
    @Override
    @Transactional
    public UsuarioResponseDTO registrarUsuarioComoAdmin(final UsuarioRequestDTO dto) {
        if (repository.findByCorreoUsuario(dto.getCorreoUsuario()).isPresent()) {
            throw new CorreoEnUsoException("El correo ya está en uso: " + dto.getCorreoUsuario());
        }

        final String nombreRol = determinarRol(dto.getRol());
        final Rol rol = rolRepository.findByNombre(nombreRol)
                .orElseThrow(() -> new RolNotFoundException("Rol no encontrado: " + nombreRol));

        final Usuario usuario = new Usuario();
        usuario.setCorreoUsuario(dto.getCorreoUsuario());
        usuario.setContrasena(passwordEncoder.encode(dto.getContrasena()));
        usuario.setRol(rol);
        usuario.setNombre(dto.getNombre());
        usuario.setApellido(dto.getApellido());
        Usuario guardado = repository.save(usuario);

        auditoriaService.registrar(
                "USUARIO_REGISTRADO_POR_ADMIN",
                "Usuario creado por admin: " + guardado.getCorreoUsuario() + " con rol " + nombreRol,
                guardado.getId(),
                UUID.randomUUID().toString(),
                ENTIDAD_USUARIO);

        return toResponseDTO(guardado);
    }

    /**
     * Desactiva un usuario de forma lógica marcándolo como inactivo sin eliminarlo.
     * La operación queda registrada en auditoría
     *
     * @param usuarioId id del usuario a desactivar
     * @throws UsuarioNotFoundException si el usuario no existe
     */
    @Override
    @Transactional
    public void eliminarUsuario(final Long usuarioId) {
        final Usuario usuario = repository.findById(usuarioId)
                .orElseThrow(() -> new UsuarioNotFoundException(USUARIO_NO_ENCONTRADO));
        usuario.setActivo(false);
        repository.save(usuario);

        auditoriaService.registrar(
                "USUARIO_ELIMINADO",
                "Usuario desactivado: " + usuario.getCorreoUsuario(),
                usuario.getId(),
                UUID.randomUUID().toString(),
                ENTIDAD_USUARIO);
    }

    /**
     * Actualiza el perfil del usuario. Solo modifica los campos que vengan con valor en el request.
     * Si se cambia el correo o la contraseña, se exige verificar la contraseña actual primero.
     * Si el correo cambió, el token JWT anterior queda inválido y el usuario debe iniciar sesión nuevamente.
     * Se notifica al usuario del cambio y la operación queda registrada en auditoría.
     * Retorna un mapa con el DTO actualizado y un flag que indica si el correo cambió
     *
     * @param correoUsuario correo actual del usuario que hace la actualización
     * @param dto           campos a actualizar: nombre, apellido, correo nuevo, contraseña nueva y contraseña actual
     * @return mapa con "usuario" ({@link UsuarioResponseDTO}) y "correocambio" (boolean)
     * @throws UsuarioNotFoundException      si el usuario no existe
     * @throws ContrasenaIncorrectaException si la contraseña actual no coincide al intentar cambiar correo o contraseña
     * @throws CorreoEnUsoException          si el nuevo correo ya está registrado por otro usuario
     */
    @Override
    @Transactional
    public Map<String, Object> actualizarPerfil(final String correoUsuario,
            final UsuarioActualizarRequestDTO dto) {
        final Usuario usuario = repository.findByCorreoUsuario(correoUsuario)
                .orElseThrow(() -> new UsuarioNotFoundException(USUARIO_NO_ENCONTRADO));
        final String contrasenaOrig = usuario.getContrasena();
        actualizarNombre(usuario, dto);
        actualizarApellido(usuario, dto);
        actualizarContrasena(usuario, dto, contrasenaOrig);
        final boolean correoCambio = actualizarCorreo(usuario, dto, contrasenaOrig);
        final Usuario usuarioGuardado = repository.save(usuario);
        notificacionService.notificarActualizacionPerfil(usuarioGuardado);

        final StringBuilder descripcion = new StringBuilder(128);
        descripcion.append("Perfil actualizado: ")
                .append(usuarioGuardado.getNombre()).append(' ').append(usuarioGuardado.getApellido())
                .append(" (").append(usuarioGuardado.getCorreoUsuario()).append(')');
        if (dto.getNombre() != null && !dto.getNombre().isBlank()) {
            descripcion.append(" | nombre cambiado");
        }
        if (dto.getApellido() != null && !dto.getApellido().isBlank()) {
            descripcion.append(" | apellido cambiado");
        }
        if (dto.getContrasenaNueva() != null && !dto.getContrasenaNueva().isBlank()) {
            descripcion.append(" | contrasena cambiada");
        }
        if (correoCambio) {
            descripcion.append(" | correo cambiado");
        }

        auditoriaService.registrar(
                "USUARIO_ACTUALIZADO",
                descripcion.toString(),
                usuarioGuardado.getId(),
                UUID.randomUUID().toString(),
                ENTIDAD_USUARIO);

        final Map<String, Object> resultado = new HashMap<>();
        resultado.put("usuario", toResponseDTO(usuarioGuardado));
        resultado.put("correocambio", correoCambio);
        return resultado;
    }

    /**
     * Retorna las selecciones favoritas del usuario como lista de id y nombre
     *
     * @param correo correo del usuario
     * @return lista de {@link PreferenciaDTO} con las selecciones favoritas del usuario
     * @throws UsuarioNotFoundException si el usuario no existe
     */
    @Override
    @Transactional(readOnly = true)
    public List<PreferenciaDTO> seleccionesUsuario(final String correo) {
        final Usuario usuario = repository.findByCorreoUsuario(correo)
                .orElseThrow(() -> new UsuarioNotFoundException(USUARIO_NO_ENCONTRADO));
        return usuario.getSeleccionesU().stream()
                .map(s -> new PreferenciaDTO(s.getId(), s.getNombre()))
                .toList();
    }

    /**
     * Agrega una lista de selecciones favoritas al usuario usando consultas nativas
     * para evitar sobreescribir las que ya tiene registradas
     *
     * @param correo        correo del usuario
     * @param idSelecciones lista de ids de las selecciones a agregar
     */
    @Override
    @Transactional
    public void agregarSeleccion(final String correo, final List<Long> idSelecciones) {
        Long usuarioId = repository.findIdByCorreo(correo);
        for (Long seleccionId : idSelecciones) {
            repository.insertarSeleccion(usuarioId, seleccionId);
        }
    }

    /**
     * Elimina una selección favorita del usuario
     *
     * @param correo      correo del usuario
     * @param seleccionId id de la selección a eliminar de sus favoritos
     */
    @Override
    @Transactional
    public void eliminarSeleccion(final String correo, final Long seleccionId) {
        Long usuarioId = repository.findIdByCorreo(correo);
        repository.eliminarSeleccion(usuarioId, seleccionId);
    }

    /**
     * Retorna los estadios favoritos del usuario como lista de id y nombre
     *
     * @param correo correo del usuario
     * @return lista de {@link PreferenciaDTO} con los estadios favoritos del usuario
     * @throws UsuarioNotFoundException si el usuario no existe
     */
    @Override
    @Transactional(readOnly = true)
    public List<PreferenciaDTO> estadiosUsuario(final String correo) {
        final Usuario usuario = repository.findByCorreoUsuario(correo)
                .orElseThrow(() -> new UsuarioNotFoundException(USUARIO_NO_ENCONTRADO));
        return usuario.getPreferenciasu().stream()
                .map(e -> new PreferenciaDTO(e.getId(), e.getNombre()))
                .toList();
    }

    /**
     * Agrega una lista de estadios favoritos al usuario usando consultas nativas
     * para evitar sobreescribir los que ya tiene registrados
     *
     * @param correo     correo del usuario
     * @param idEstadios lista de ids de los estadios a agregar
     */
    @Override
    @Transactional
    public void agregarEstadio(final String correo, final List<Long> idEstadios) {
        Long usuarioId = repository.findIdByCorreo(correo);
        for (Long estadioId : idEstadios) {
            repository.insertarEstadio(usuarioId, estadioId);
        }
    }

    /**
     * Elimina un estadio favorito del usuario
     *
     * @param correo    correo del usuario
     * @param estadioId id del estadio a eliminar de sus favoritos
     */
    @Override
    @Transactional
    public void eliminarEstadio(final String correo, final Long estadioId) {
        Long usuarioId = repository.findIdByCorreo(correo);
        repository.eliminarEstadio(usuarioId, estadioId);
    }

    /**
     * Agrega una lista de ciudades favoritas al usuario usando consultas nativas
     * para evitar sobreescribir las que ya tiene registradas
     *
     * @param correo     correo del usuario
     * @param idCiudades lista de ids de las ciudades a agregar
     */
    @Override
    @Transactional
    public void agregarCiudad(final String correo, final List<Long> idCiudades) {
        Long usuarioId = repository.findIdByCorreo(correo);
        for (Long ciudadId : idCiudades) {
            repository.insertarCiudad(usuarioId, ciudadId);
        }
    }

    /**
     * Elimina una ciudad favorita del usuario
     *
     * @param correo    correo del usuario
     * @param ciudadId  id de la ciudad a eliminar de sus favoritos
     */
    @Override
    @Transactional
    public void eliminarCiudad(final String correo, final Long ciudadId) {
        Long usuarioId = repository.findIdByCorreo(correo);
        repository.eliminarCiudad(usuarioId, ciudadId);
    }

    /**
     * Retorna las ciudades favoritas del usuario como lista de id y nombre
     *
     * @param correo correo del usuario
     * @return lista de {@link PreferenciaDTO} con las ciudades favoritas del usuario
     * @throws UsuarioNotFoundException si el usuario no existe
     */
    @Override
    @Transactional(readOnly = true)
    public List<PreferenciaDTO> ciudadesUsuario(final String correo) {
        final Usuario usuario = repository.findByCorreoUsuario(correo)
                .orElseThrow(() -> new UsuarioNotFoundException(USUARIO_NO_ENCONTRADO));
        return usuario.getCiudadFavoritas().stream()
                .map(c -> new PreferenciaDTO(c.getId(), c.getNombre()))
                .toList();
    }

    /**
     * Retorna el catálogo completo de estadios disponibles para agregar como favoritos
     *
     * @return lista de {@link PreferenciaDTO} con todos los estadios registrados
     */
    @Override
    @Transactional(readOnly = true)
    public List<PreferenciaDTO> listarEstadios() {
        return estadioRepository.findAll().stream()
                .map(e -> new PreferenciaDTO(e.getId(), e.getNombre()))
                .toList();
    }

    /**
     * Retorna el catálogo completo de ciudades disponibles para agregar como favoritas
     *
     * @return lista de {@link PreferenciaDTO} con todas las ciudades registradas
     */
    @Override
    @Transactional(readOnly = true)
    public List<PreferenciaDTO> listarCiudades() {
        return ciudadRepository.findAll().stream()
                .map(c -> new PreferenciaDTO(c.getId(), c.getNombre()))
                .toList();
    }

    /**
     * Actualiza el nombre del usuario solo si el valor en el request no es nulo ni vacío
     *
     * @param usuario entidad del usuario a modificar
     * @param dto     request con el nuevo nombre
     */
    private void actualizarNombre(final Usuario usuario, final UsuarioActualizarRequestDTO dto) {
        if (dto.getNombre() != null && !dto.getNombre().isBlank()) {
            usuario.setNombre(dto.getNombre());
        }
    }

    /**
     * Actualiza el apellido del usuario solo si el valor en el request no es nulo ni vacío
     *
     * @param usuario entidad del usuario a modificar
     * @param dto     request con el nuevo apellido
     */
    private void actualizarApellido(final Usuario usuario, final UsuarioActualizarRequestDTO dto) {
        if (dto.getApellido() != null && !dto.getApellido().isBlank()) {
            usuario.setApellido(dto.getApellido());
        }
    }

    /**
     * Actualiza la contraseña del usuario si se proporciona una nueva.
     * Exige verificar la contraseña actual antes de aplicar el cambio.
     * La nueva contraseña se almacena hasheada
     *
     * @param usuario        entidad del usuario a modificar
     * @param dto            request con la contraseña actual y la nueva
     * @param contrasenaOrig contraseña actual hasheada del usuario
     * @throws ContrasenaIncorrectaException si la contraseña actual no coincide
     */
    private void actualizarContrasena(final Usuario usuario, final UsuarioActualizarRequestDTO dto,
            final String contrasenaOrig) {
        if (dto.getContrasenaNueva() != null && !dto.getContrasenaNueva().isBlank()) {
            validarContrasenaActual(dto.getContrasenaActual(), contrasenaOrig);
            usuario.setContrasena(passwordEncoder.encode(dto.getContrasenaNueva()));
        }
    }

    /**
     * Actualiza el correo del usuario si se proporciona uno nuevo.
     * Exige verificar la contraseña actual y que el nuevo correo no esté en uso.
     * Retorna true si el correo cambió, lo que indica que el token JWT anterior queda inválido
     *
     * @param usuario        entidad del usuario a modificar
     * @param dto            request con el nuevo correo y la contraseña actual
     * @param contrasenaOrig contraseña actual hasheada del usuario
     * @return true si el correo fue actualizado, false si no se proporcionó correo nuevo
     * @throws ContrasenaIncorrectaException si la contraseña actual no coincide
     * @throws CorreoEnUsoException          si el nuevo correo ya está registrado
     */
    private boolean actualizarCorreo(final Usuario usuario, final UsuarioActualizarRequestDTO dto,
            final String contrasenaOrig) {
        if (dto.getCorreoNuevo() != null && !dto.getCorreoNuevo().isBlank()) {
            validarContrasenaActual(dto.getContrasenaActual(), contrasenaOrig);
            if (repository.findByCorreoUsuario(dto.getCorreoNuevo()).isPresent()) {
                throw new CorreoEnUsoException("El correo ya está en uso");
            }
            usuario.setCorreoUsuario(dto.getCorreoNuevo());
            return true;
        }
        return false;
    }

    /**
     * Valida que la contraseña actual proporcionada coincida con la almacenada en la base de datos
     *
     * @param contrasenaActual contraseña en texto plano enviada por el usuario
     * @param contrasenaOrig   contraseña hasheada almacenada en la base de datos
     * @throws ContrasenaIncorrectaException si no coinciden o si la contraseña actual es nula
     */
    private void validarContrasenaActual(final String contrasenaActual, final String contrasenaOrig) {
        if (contrasenaActual == null || !passwordEncoder.matches(contrasenaActual, contrasenaOrig)) {
            throw new ContrasenaIncorrectaException("La contraseña actual es incorrecta");
        }
    }

    /**
     * Determina el rol a asignar al usuario. Si no se proporciona rol o viene vacío,
     * se asigna ROLE_USUARIO por defecto
     *
     * @param rol nombre del rol recibido en el request, puede ser nulo o vacío
     * @return nombre del rol a asignar
     */
    private String determinarRol(final String rol) {
        if (rol == null || rol.isBlank()) {
            return "ROLE_USUARIO";
        }
        return rol;
    }

    /**
     * Convierte una entidad {@link Usuario} a su representación DTO de respuesta
     *
     * @param usuario entidad a convertir
     * @return {@link UsuarioResponseDTO} con los datos del usuario
     */
    private UsuarioResponseDTO toResponseDTO(final Usuario usuario) {
        final UsuarioResponseDTO dto = new UsuarioResponseDTO();
        dto.setId(usuario.getId());
        dto.setCorreoUsuario(usuario.getCorreoUsuario());
        dto.setRol(usuario.getRol().getNombre());
        dto.setNombre(usuario.getNombre());
        dto.setApellido(usuario.getApellido());
        dto.setActivo(usuario.isActivo());
        dto.setFechaRegistro(usuario.getFechaRegistro());
        return dto;
    }

    /**
     * Retorna la entidad {@link Usuario} directamente desde la base de datos por su id.
     * Usado internamente por otros servicios que necesitan la entidad completa
     *
     * @param usuarioId id del usuario a buscar
     * @return entidad {@link Usuario} encontrada
     * @throws UsuarioNotFoundException si el usuario no existe
     */
    @Override
    @Transactional(readOnly = true)
    public Usuario obtenerEntidadPorId(final Long usuarioId) {
        return repository.findById(usuarioId)
                .orElseThrow(() -> new UsuarioNotFoundException("Usuario no encontrado con id: " + usuarioId));
    }

    /**
     * Retorna la entidad {@link Usuario} directamente desde la base de datos por su correo.
     * Usado internamente por otros servicios que necesitan la entidad completa
     *
     * @param correo correo del usuario a buscar
     * @return entidad {@link Usuario} encontrada
     * @throws UsuarioNotFoundException si no existe un usuario con ese correo
     */
    @Override
    @Transactional(readOnly = true)
    public Usuario obtenerEntidadPorCorreo(final String correo) {
        return repository.findByCorreoUsuario(correo)
                .orElseThrow(() -> new UsuarioNotFoundException(
                        "Usuario no encontrado con correo: " + correo));
    }

    /**
     * Registra o actualiza el token FCM del usuario para el envío de notificaciones push.
     * Si es la primera vez que se registra un token (el usuario no tenía uno antes),
     * se envía la notificación de bienvenida a través de Firebase
     *
     * @param correo   correo del usuario
     * @param fcmToken token FCM del dispositivo del usuario
     * @throws UsuarioNotFoundException si el usuario no existe
     */
    @Override
    @Transactional
    public void actualizarFcmToken(String correo, String fcmToken) {
        Usuario usuario = repository.findByCorreoUsuario(correo)
                .orElseThrow(() -> new UsuarioNotFoundException(USUARIO_NO_ENCONTRADO));

        boolean esPrimerToken = usuario.getFcmtoken() == null || usuario.getFcmtoken().isBlank();

        usuario.setFcmtoken(fcmToken);
        repository.save(usuario);

        if (esPrimerToken) {
            notificacionService.notificarRegistro(usuario);
        }
    }
}