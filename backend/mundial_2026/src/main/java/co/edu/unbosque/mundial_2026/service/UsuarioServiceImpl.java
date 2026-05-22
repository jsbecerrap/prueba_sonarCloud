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
import co.edu.unbosque.mundial_2026.repository.SeleccionRepository;
import co.edu.unbosque.mundial_2026.repository.UsuarioRepository;
import java.util.UUID;
@Service
public class UsuarioServiceImpl implements UsuarioService {

  private static final String USUARIO_NO_ENCONTRADO = "Usuario no encontrado";
    private static final String ENTIDAD_USUARIO = "Usuario";
    private final UsuarioRepository repository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;
    private final SeleccionRepository seleccionRepository;
    private final EstadioRepository estadioRepository;
    private final CiudadRepository ciudadRepository;
private final EventoAuditoriaService auditoriaService;



   private final NotificacionService notificacionService;

public UsuarioServiceImpl(UsuarioRepository repository, RolRepository rolRepository,
        PasswordEncoder passwordEncoder, SeleccionRepository seleccionRepository,
        EstadioRepository estadioRepository, CiudadRepository ciudadRepository,
        NotificacionService notificacionService,EventoAuditoriaService auditoriaService) {
    this.repository = repository;
    this.rolRepository = rolRepository;
    this.passwordEncoder = passwordEncoder;
    this.seleccionRepository = seleccionRepository;
    this.estadioRepository = estadioRepository;
    this.ciudadRepository = ciudadRepository;
    this.notificacionService = notificacionService;
    this.auditoriaService = auditoriaService;
}
    //retorna el dto de los usuarios que estan registrados en el aplicativo
    @Override
    @Transactional(readOnly = true)
    public List<UsuarioResponseDTO> listarTodos() {
        return repository.findAll().stream()
        .map(this::toResponseDTO)
        .toList();
    }
//Registra al usuario guardandolo en la base de datos verificando los datos,aplicando las excepciones,hasheando la contraseña 
//En caso de que no ponga rol por defecto se le asginara ROL_USUARIO
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
    @Override
    @Transactional(readOnly = true)
    public UsuarioResponseDTO obtenerUsuario(final Long usuarioId) {
        return toResponseDTO(repository.findById(usuarioId)
                .orElseThrow(() -> new UsuarioNotFoundException(USUARIO_NO_ENCONTRADO)));
    }

    @Override
    @Transactional(readOnly = true)
    public UsuarioResponseDTO obtenerPorCorreo(final String correo) {
        return toResponseDTO(repository.findByCorreoUsuario(correo)
                .orElseThrow(() -> new UsuarioNotFoundException(USUARIO_NO_ENCONTRADO)));
    }

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
//Actualiza los valores del usuario, es importante mandar si cambio el correo ya que se debe generar un nuevo token y hacer log out con el anterior
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


    @Override
    @Transactional(readOnly = true)
    public List<PreferenciaDTO> seleccionesUsuario(final String correo) {
        final Usuario usuario = repository.findByCorreoUsuario(correo)
                .orElseThrow(() -> new UsuarioNotFoundException(USUARIO_NO_ENCONTRADO));
        return usuario.getSeleccionesU().stream()
                .map(s -> new PreferenciaDTO(s.getId(), s.getNombre()))
                .toList();//se devuelve preferenciaDTO para asi devolver el nombre y el id segun corresponda(seleccion,estadio,estado)
    }
//Agrega las selecciones mediante el id se verifica que no esten para no sobreescribirlas y se guardan en la bd
   @Override
@Transactional
public void agregarSeleccion(final String correo, final List<Long> idSelecciones) {
    Long usuarioId = repository.findIdByCorreo(correo);
    for (Long seleccionId : idSelecciones) {
        repository.insertarSeleccion(usuarioId, seleccionId);
    }
  
}

@Override
@Transactional
public void eliminarSeleccion(final String correo, final Long seleccionId) {
    Long usuarioId = repository.findIdByCorreo(correo);
    repository.eliminarSeleccion(usuarioId, seleccionId);
}



    @Override
    @Transactional(readOnly = true)
    public List<PreferenciaDTO> estadiosUsuario(final String correo) {
        final Usuario usuario = repository.findByCorreoUsuario(correo)
                .orElseThrow(() -> new UsuarioNotFoundException(USUARIO_NO_ENCONTRADO));
        return usuario.getPreferenciasu().stream()
                .map(e -> new PreferenciaDTO(e.getId(), e.getNombre()))
                .toList();
    }

  @Override
@Transactional
public void agregarEstadio(final String correo, final List<Long> idEstadios) {
    Long usuarioId = repository.findIdByCorreo(correo);
    for (Long estadioId : idEstadios) {
        repository.insertarEstadio(usuarioId, estadioId);
    }
  
}

@Override
@Transactional
public void eliminarEstadio(final String correo, final Long estadioId) {
    Long usuarioId = repository.findIdByCorreo(correo);
    repository.eliminarEstadio(usuarioId, estadioId);
}

@Override
@Transactional
public void agregarCiudad(final String correo, final List<Long> idCiudades) {
    Long usuarioId = repository.findIdByCorreo(correo);
    for (Long ciudadId : idCiudades) {
        repository.insertarCiudad(usuarioId, ciudadId);
    }
 
}

@Override
@Transactional
public void eliminarCiudad(final String correo, final Long ciudadId) {
    Long usuarioId = repository.findIdByCorreo(correo);
    repository.eliminarCiudad(usuarioId, ciudadId);
}

    @Override
    @Transactional(readOnly = true)
    public List<PreferenciaDTO> ciudadesUsuario(final String correo) {
        final Usuario usuario = repository.findByCorreoUsuario(correo)
                .orElseThrow(() -> new UsuarioNotFoundException(USUARIO_NO_ENCONTRADO));
        return usuario.getCiudadFavoritas().stream()
                .map(c -> new PreferenciaDTO(c.getId(), c.getNombre()))
                .toList();
    }

    

    @Override
    @Transactional(readOnly = true)
    public List<PreferenciaDTO> listarEstadios() {
        return estadioRepository.findAll().stream()
                .map(e -> new PreferenciaDTO(e.getId(), e.getNombre()))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PreferenciaDTO> listarCiudades() {
        return ciudadRepository.findAll().stream()
                .map(c -> new PreferenciaDTO(c.getId(), c.getNombre()))
                .toList();
    }

//Verificaciones al momento de actualizar

    private void actualizarNombre(final Usuario usuario, final UsuarioActualizarRequestDTO dto) {
        if (dto.getNombre() != null && !dto.getNombre().isBlank()) {
            usuario.setNombre(dto.getNombre());
        }
    }

    private void actualizarApellido(final Usuario usuario, final UsuarioActualizarRequestDTO dto) {
        if (dto.getApellido() != null && !dto.getApellido().isBlank()) {
            usuario.setApellido(dto.getApellido());
        }
    }

   private void actualizarContrasena(final Usuario usuario, final UsuarioActualizarRequestDTO dto,
        final String contrasenaOrig) {
    if (dto.getContrasenaNueva() != null && !dto.getContrasenaNueva().isBlank()) {
        validarContrasenaActual(dto.getContrasenaActual(), contrasenaOrig);
        usuario.setContrasena(passwordEncoder.encode(dto.getContrasenaNueva()));
    }
}
//Verificacion del correo
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

    private void validarContrasenaActual(final String contrasenaActual, final String contrasenaOrig) {
        if (contrasenaActual == null || !passwordEncoder.matches(contrasenaActual, contrasenaOrig)) {
            throw new ContrasenaIncorrectaException("La contraseña actual es incorrecta");
        }
    }
//Determina el rol del usuario en caso de que no ponga nada
    private String determinarRol(final String rol) {
        if (rol == null || rol.isBlank()) {
            return "ROLE_USUARIO";
        }
        return rol;
    }

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
    @Override
@Transactional(readOnly = true)
public Usuario obtenerEntidadPorId(final Long usuarioId) {
    return repository.findById(usuarioId)
            .orElseThrow(() -> new UsuarioNotFoundException("Usuario no encontrado con id: " + usuarioId));
}
@Override
@Transactional(readOnly = true)
public Usuario obtenerEntidadPorCorreo(final String correo) {
    return repository.findByCorreoUsuario(correo)
            .orElseThrow(() -> new UsuarioNotFoundException(
                    "Usuario no encontrado con correo: " + correo));
}
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