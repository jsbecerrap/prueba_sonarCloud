package co.edu.unbosque.mundial_2026.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.edu.unbosque.mundial_2026.dto.request.UsuarioActualizarRequestDTO;
import co.edu.unbosque.mundial_2026.dto.request.UsuarioRequestDTO;
import co.edu.unbosque.mundial_2026.dto.response.PreferenciaDTO;
import co.edu.unbosque.mundial_2026.dto.response.UsuarioResponseDTO;
import co.edu.unbosque.mundial_2026.security.TokenBlacklist;
import co.edu.unbosque.mundial_2026.service.UsuarioService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import static co.edu.unbosque.mundial_2026.security.TokenJwt.HEADER_AUTHORIZATION;
import static co.edu.unbosque.mundial_2026.security.TokenJwt.PREFIX_TOKEN;

/**
 * Controlador REST que expone los endpoints de gestión de usuarios, autenticación
 * y preferencias personales — combina operaciones públicas como el registro,
 * operaciones del usuario autenticado como actualizar perfil y preferencias,
 * y operaciones administrativas restringidas a ADMIN
 *
 * <p>Base URL: {@code /api}</p>
 */
@RestController
@RequestMapping("/api")
public class UsuarioController {

    private final UsuarioService service;
    private final TokenBlacklist tokenBlacklist;
    private static final String KEY_USUARIO = "usuario";
    private static final String ROL_ADMIN = "hasRole('ADMIN')";

    public UsuarioController(final UsuarioService service,
            final TokenBlacklist tokenBlacklist) {
        this.service = service;
        this.tokenBlacklist = tokenBlacklist;
    }

    /**
     * {@code GET /api/usuarios/listar} — Lista todos los usuarios registrados
     * en el sistema — solo ADMIN
     *
     * @return lista de todos los usuarios
     */
    @PreAuthorize(ROL_ADMIN)
    @GetMapping("/usuarios/listar")
    public ResponseEntity<List<UsuarioResponseDTO>> listarTodos() {
        return ResponseEntity.ok(service.listarTodos());
    }

    /**
     * {@code GET /api/usuarios/{idUsuario}} — Obtiene el detalle de un usuario
     * por su ID — solo ADMIN
     *
     * @param idUsuario ID del usuario a consultar
     * @return datos del usuario
     */
    @PreAuthorize(ROL_ADMIN)
    @GetMapping("/usuarios/{idUsuario}")
    public ResponseEntity<UsuarioResponseDTO> obtenerUsuario(@PathVariable final Long idUsuario) {
        return ResponseEntity.ok(service.obtenerUsuario(idUsuario));
    }

    /**
     * {@code GET /api/usuarios/perfil} — Retorna el perfil del usuario autenticado
     * extrayendo su correo directamente del token JWT
     *
     * @return datos del usuario autenticado
     */
    @GetMapping("/usuarios/perfil")
    public ResponseEntity<UsuarioResponseDTO> obtenerPerfil() {
        final String correo = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(service.obtenerPorCorreo(correo));
    }

    /**
     * {@code POST /api/usuarios/registrar} — Registra un nuevo usuario con rol de cliente
     *
     * @param dto datos del usuario a registrar
     * @return usuario creado con HTTP 201
     */
    @PostMapping("/usuarios/registrar")
    public ResponseEntity<UsuarioResponseDTO> registrarUsuario(
            @Valid @RequestBody final UsuarioRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.registrarUsuario(dto));
    }

    /**
     * {@code DELETE /api/usuarios/{idUsuario}} — Elimina lógicamente un usuario
     * del sistema — solo ADMIN
     *
     * @param idUsuario ID del usuario a eliminar
     * @return respuesta vacía con HTTP 204
     */
    @PreAuthorize(ROL_ADMIN)
    @DeleteMapping("/usuarios/{idUsuario}")
    public ResponseEntity<Void> eliminarUsuario(@PathVariable final Long idUsuario) {
        service.eliminarUsuario(idUsuario);
        return ResponseEntity.noContent().build();
    }

    /**
     * {@code PUT /api/usuarios/perfil} — Actualiza el perfil del usuario autenticado —
     * si el correo electrónico cambia, el token JWT actual se invalida añadiéndolo
     * a la lista negra, obligando al usuario a iniciar sesión nuevamente con
     * las nuevas credenciales para obtener un token válido
     *
     * @param dto     nuevos datos del perfil
     * @param request petición HTTP de donde se extrae el token actual para invalidarlo
     * @return perfil actualizado, o mensaje indicando que debe iniciar sesión si el correo cambió
     */
    @PutMapping("/usuarios/perfil")
    public ResponseEntity<Object> actualizarPerfil(
            @Valid @RequestBody final UsuarioActualizarRequestDTO dto,
            final HttpServletRequest request) {
        final String correoUsuario = SecurityContextHolder.getContext().getAuthentication().getName();
        final Map<String, Object> resultado = service.actualizarPerfil(correoUsuario, dto);
        final boolean correoCambio = (boolean) resultado.get("correocambio");
        if (correoCambio) {
            final String header = request.getHeader(HEADER_AUTHORIZATION);
            if (header != null && header.startsWith(PREFIX_TOKEN)) {
                tokenBlacklist.agregar(header.replace(PREFIX_TOKEN, ""));
            }
            final Map<String, Object> response = new HashMap<>();
            response.put(KEY_USUARIO, resultado.get(KEY_USUARIO));
            response.put("mensaje", "Correo actualizado, inicia sesión nuevamente");
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.ok(resultado.get(KEY_USUARIO));
    }

    /**
     * {@code POST /api/auth/logout} — Cierra la sesión del usuario añadiendo su token JWT
     * a la lista negra para que no pueda volver a usarse aunque aún no haya expirado
     *
     * @param request petición HTTP de donde se extrae el token a invalidar
     * @return mensaje confirmando el cierre de sesión
     */
    @PostMapping("/auth/logout")
    public ResponseEntity<Object> logout(final HttpServletRequest request) {
        final String header = request.getHeader(HEADER_AUTHORIZATION);
        if (header != null && header.startsWith(PREFIX_TOKEN)) {
            tokenBlacklist.agregar(header.replace(PREFIX_TOKEN, ""));
        }
        final Map<String, String> response = new HashMap<>();
        response.put("mensaje", "Sesión cerrada correctamente");
        return ResponseEntity.ok(response);
    }

    /**
     * {@code GET /api/usuarios/seleccionesFavoritas} — Retorna las selecciones
     * marcadas como favoritas por el usuario autenticado
     *
     * @return lista de selecciones favoritas del usuario
     */
    @GetMapping("/usuarios/seleccionesFavoritas")
    public ResponseEntity<List<PreferenciaDTO>> obtenerSelecciones() {
        final String correo = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(service.seleccionesUsuario(correo));
    }

    /**
     * {@code DELETE /api/usuarios/seleccionesFavoritas/{seleccionId}} — Elimina una
     * selección de la lista de favoritas del usuario autenticado
     *
     * @param seleccionId ID de la selección a eliminar
     * @return respuesta vacía con HTTP 204
     */
    @DeleteMapping("/usuarios/seleccionesFavoritas/{seleccionId}")
    public ResponseEntity<Void> eliminarSeleccion(@PathVariable final Long seleccionId) {
        final String correo = SecurityContextHolder.getContext().getAuthentication().getName();
        service.eliminarSeleccion(correo, seleccionId);
        return ResponseEntity.noContent().build();
    }

    /**
     * {@code GET /api/usuarios/estadiosFav} — Retorna los estadios marcados
     * como favoritos por el usuario autenticado
     *
     * @return lista de estadios favoritos del usuario
     */
    @GetMapping("/usuarios/estadiosFav")
    public ResponseEntity<List<PreferenciaDTO>> obtenerEstadios() {
        final String correo = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(service.estadiosUsuario(correo));
    }

    /**
     * {@code DELETE /api/usuarios/estadiosFav/{estadioId}} — Elimina un estadio
     * de la lista de favoritos del usuario autenticado
     *
     * @param estadioId ID del estadio a eliminar
     * @return respuesta vacía con HTTP 204
     */
    @DeleteMapping("/usuarios/estadiosFav/{estadioId}")
    public ResponseEntity<Void> eliminarEstadio(@PathVariable final Long estadioId) {
        final String correo = SecurityContextHolder.getContext().getAuthentication().getName();
        service.eliminarEstadio(correo, estadioId);
        return ResponseEntity.noContent().build();
    }

    /**
     * {@code GET /api/usuarios/ciudadesFav} — Retorna las ciudades marcadas
     * como favoritas por el usuario autenticado
     *
     * @return lista de ciudades favoritas del usuario
     */
    @GetMapping("/usuarios/ciudadesFav")
    public ResponseEntity<List<PreferenciaDTO>> obtenerCiudades() {
        final String correo = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(service.ciudadesUsuario(correo));
    }

    /**
     * {@code DELETE /api/usuarios/ciudadesFav/{ciudadId}} — Elimina una ciudad
     * de la lista de favoritas del usuario autenticado
     *
     * @param ciudadId ID de la ciudad a eliminar
     * @return respuesta vacía con HTTP 204
     */
    @DeleteMapping("/usuarios/ciudadesFav/{ciudadId}")
    public ResponseEntity<Void> eliminarCiudad(@PathVariable final Long ciudadId) {
        final String correo = SecurityContextHolder.getContext().getAuthentication().getName();
        service.eliminarCiudad(correo, ciudadId);
        return ResponseEntity.noContent().build();
    }

    /**
     * {@code GET /api/estadios} — Retorna el catálogo completo de estadios disponibles
     * para que el usuario los marque como favoritos desde el frontend
     *
     * @return lista de estadios disponibles
     */
    @GetMapping("/estadios")
    public ResponseEntity<List<PreferenciaDTO>> listarEstadios() {
        return ResponseEntity.ok(service.listarEstadios());
    }

    /**
     * {@code GET /api/ciudades} — Retorna el catálogo completo de ciudades disponibles
     * para que el usuario las marque como favoritas desde el frontend
     *
     * @return lista de ciudades disponibles
     */
    @GetMapping("/ciudades")
    public ResponseEntity<List<PreferenciaDTO>> listarCiudades() {
        return ResponseEntity.ok(service.listarCiudades());
    }

    /**
     * {@code PUT /api/usuarios/fcm-token} — Actualiza el token FCM del dispositivo
     * del usuario autenticado para que pueda recibir notificaciones push
     *
     * @param body mapa con la clave {@code fcmToken} y el nuevo token del dispositivo
     * @return respuesta vacía con HTTP 204
     */
    @PutMapping("/usuarios/fcm-token")
    public ResponseEntity<Void> actualizarFcmToken(@RequestBody Map<String, String> body) {
        String correo = SecurityContextHolder.getContext().getAuthentication().getName();
        service.actualizarFcmToken(correo, body.get("fcmToken"));
        return ResponseEntity.noContent().build();
    }

    /**
     * {@code PUT /api/usuarios/seleccionesFavoritas} — Agrega una o varias selecciones
     * a la lista de favoritas del usuario autenticado
     *
     * @param ids lista de IDs de selecciones a agregar
     * @return respuesta vacía con HTTP 204
     */
    @PutMapping("/usuarios/seleccionesFavoritas")
    public ResponseEntity<Void> agregarSeleccion(@RequestBody final List<Long> ids) {
        final String correo = SecurityContextHolder.getContext().getAuthentication().getName();
        service.agregarSeleccion(correo, ids);
        return ResponseEntity.noContent().build();
    }

    /**
     * {@code PUT /api/usuarios/estadiosFav} — Agrega uno o varios estadios
     * a la lista de favoritos del usuario autenticado
     *
     * @param ids lista de IDs de estadios a agregar
     * @return respuesta vacía con HTTP 204
     */
    @PutMapping("/usuarios/estadiosFav")
    public ResponseEntity<Void> agregarEstadio(@RequestBody final List<Long> ids) {
        final String correo = SecurityContextHolder.getContext().getAuthentication().getName();
        service.agregarEstadio(correo, ids);
        return ResponseEntity.noContent().build();
    }

    /**
     * {@code PUT /api/usuarios/ciudadesFav} — Agrega una o varias ciudades
     * a la lista de favoritas del usuario autenticado
     *
     * @param ids lista de IDs de ciudades a agregar
     * @return respuesta vacía con HTTP 204
     */
    @PutMapping("/usuarios/ciudadesFav")
    public ResponseEntity<Void> agregarCiudad(@RequestBody final List<Long> ids) {
        final String correo = SecurityContextHolder.getContext().getAuthentication().getName();
        service.agregarCiudad(correo, ids);
        return ResponseEntity.noContent().build();
    }

    /**
     * {@code GET /api/usuarios/{idUsuario}/nombre} — Retorna únicamente el nombre
     * y apellido de un usuario — útil para mostrar datos básicos sin exponer
     * toda la información del perfil
     *
     * @param idUsuario ID del usuario
     * @return mapa con las claves {@code nombre} y {@code apellido}
     */
    @GetMapping("/usuarios/{idUsuario}/nombre")
    public ResponseEntity<Map<String, String>> obtenerNombreUsuario(@PathVariable final Long idUsuario) {
        UsuarioResponseDTO u = service.obtenerUsuario(idUsuario);
        Map<String, String> response = new HashMap<>();
        response.put("nombre", u.getNombre());
        response.put("apellido", u.getApellido());
        return ResponseEntity.ok(response);
    }

    /**
     * {@code POST /api/usuarios/admin/registrar} — Registra un nuevo usuario
     * asignándole directamente el rol de administrador — solo ADMIN
     *
     * @param dto datos del usuario administrador a registrar
     * @return usuario administrador creado con HTTP 201
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/usuarios/admin/registrar")
    public ResponseEntity<UsuarioResponseDTO> registrarUsuarioPorAdmin(
            @Valid @RequestBody final UsuarioRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.registrarUsuarioComoAdmin(dto));
    }
}