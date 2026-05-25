package co.edu.unbosque.mundial_2026.security.filter;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import co.edu.unbosque.mundial_2026.entity.Usuario;
import co.edu.unbosque.mundial_2026.repository.UsuarioRepository;
import co.edu.unbosque.mundial_2026.security.TokenJwt;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

import static co.edu.unbosque.mundial_2026.security.TokenJwt.HEADER_AUTHORIZATION;
import static co.edu.unbosque.mundial_2026.security.TokenJwt.PREFIX_TOKEN;
@SuppressWarnings({"PMD.CyclomaticComplexity", "PMD.ExceptionAsFlowControl", "PMD.AvoidDeeplyNestedIfStmts"})
/**
 * Filtro de autenticación JWT encargado de interceptar el login del sistema
 * valida credenciales controla intentos fallidos genera token y gestiona bloqueo de usuarios
 */
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private static final int SEGUNDOS_DIA = 86400;
    private static final String KEY_MENSAJE = "mensaje";
    private static final int MAX_INTENTOS_FALLIDOS = 5;
    private static final int MINUTOS_BLOQUEO = 15;
    private static final int MAX_LONGITUD_PASSWORD = 72;
    private static final String ATTR_CORREO_INTENTO = "correoIntentoLogin";

    private final AuthenticationManager authManager;
    private final UsuarioRepository usuarioRepo;

    public JwtAuthenticationFilter(final AuthenticationManager authManager,
                                   final UsuarioRepository usuarioRepo) {
        super(authManager);
        this.authManager = authManager;
        this.usuarioRepo = usuarioRepo;
    }

    /**
     * Intercepta la petición de login y extrae credenciales
     * valida bloqueo de cuenta por intentos fallidos y limita ataques por contraseñas excesivas
     * delega la autenticación al AuthenticationManager
     */
    @Override
    public Authentication attemptAuthentication(final HttpServletRequest request,
                                               final HttpServletResponse response)
            throws AuthenticationException {

        try {
            final Usuario credenciales = new ObjectMapper()
                    .readValue(request.getInputStream(), Usuario.class);

            final String correo = credenciales.getCorreoUsuario();
            final String contrasena = credenciales.getContrasena();

            request.setAttribute(ATTR_CORREO_INTENTO, correo);

            if (contrasena != null && contrasena.length() > MAX_LONGITUD_PASSWORD) {
                throw new AuthenticationServiceException("La contraseña excede la longitud permitida");//la contraseña no exceda la longitud 
            }

            if (correo != null && !correo.isBlank()) {
                final Optional<Usuario> opt = usuarioRepo.findByCorreoUsuario(correo);
                if (opt.isPresent()) {
                    final Usuario usuario = opt.get();
                    if (usuario.getBloqueadoHasta() != null
                            && usuario.getBloqueadoHasta().isAfter(LocalDateTime.now())) {
                        throw new LockedException("Cuenta temporalmente bloqueada");//Mira que no este bloqueado
                    }
                }
            }

            return authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(correo, contrasena));//manda la autenticacion( se observa de manera baja en el manager)

        } catch (IOException e) {
            throw new AuthenticationServiceException("Error al leer las credenciales", e);
        }
    }

    /**
     * Ejecuta la lógica cuando la autenticación es exitosa
     * genera el token JWT incluye roles del usuario y reinicia contador de intentos fallidos
     */
    @Override
    protected void successfulAuthentication(final HttpServletRequest request,
                                           final HttpServletResponse response,
                                           final FilterChain chain,
                                           final Authentication authResult)
            throws IOException, ServletException {

        final String username = authResult.getName();//se obtiene el correo y se busca
        final Usuario usuario = usuarioRepo.findByCorreoUsuario(username)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        if ((usuario.getIntentosFallidos() != null && usuario.getIntentosFallidos() > 0)
                || usuario.getBloqueadoHasta() != null) {
            usuario.setIntentosFallidos(0);
            usuario.setBloqueadoHasta(null);
            usuarioRepo.save(usuario);//se le reinician los valores de bloqueo 
        }

        final String nombreCompleto = usuario.getNombre() + " " + usuario.getApellido();

        List<GrantedAuthority> roles = new ArrayList<>(authResult.getAuthorities());//obtiene los roles 

        final String token = Jwts.builder()//construye el token con el nombre, roles, cuando expira y que debe entrar con ello 
                .subject(username)
                .claim("authorities", roles.stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toList()))
                .expiration(Date.from(Instant.now().plusSeconds(SEGUNDOS_DIA)))
                .signWith(TokenJwt.getSecretKey())
                .compact();

        response.addHeader(HEADER_AUTHORIZATION, PREFIX_TOKEN + token);//se añade en el header por el front y en formato del body mas abajo 

        final Map<String, String> json = new HashMap<>();
        json.put("token", token);
        json.put("username", username);
        json.put(KEY_MENSAJE, "Hola " + nombreCompleto + " sesión iniciada correctamente");

        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(new ObjectMapper().writeValueAsString(json));
    }

    /**
     * Maneja los intentos de autenticación fallidos
     * incrementa contador de intentos bloquea usuario si excede el límite y responde error adecuado
     */
    @Override
    protected void unsuccessfulAuthentication(final HttpServletRequest request,
                                             final HttpServletResponse response,
                                             final AuthenticationException failed)
            throws IOException, ServletException {

        final Object attrCorreo = request.getAttribute(ATTR_CORREO_INTENTO);//se obtiene el correo para mirar y intentos y eso 
        String correo = null;
if (attrCorreo != null) {
    correo = attrCorreo.toString();//se debe convertir astring
}
        final boolean estabaBloqueado = failed instanceof LockedException;

        if (!estabaBloqueado && correo != null && !correo.isBlank()) {
            usuarioRepo.findByCorreoUsuario(correo)
                    .ifPresent(this::registrarIntentoFallido);
        }

        final Map<String, String> json = new HashMap<>();

        if (estabaBloqueado) {//manda segun si esta bloqueado o no 
            json.put(KEY_MENSAJE, "Demasiados intentos espera unos minutos");
            response.setStatus(429);
        } else {
            json.put(KEY_MENSAJE, "Error en la autenticacion correo contraseña incorrecto");//si esta mal la contraseña 
            response.setStatus(401);
        }

        json.put("error", failed.getMessage());

        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(new ObjectMapper().writeValueAsString(json));
    }

    /**
     * Registra intentos fallidos de login y aplica bloqueo temporal si supera el límite permitido
     */
    private void registrarIntentoFallido(final Usuario usuario) {
        int intentos = 0;
if (usuario.getIntentosFallidos() != null) {//mira cuantos intentos para añadir o si tiene 0
    intentos = usuario.getIntentosFallidos();
}
intentos = intentos + 1;

        usuario.setIntentosFallidos(intentos);

        if (intentos >= MAX_INTENTOS_FALLIDOS) {
            usuario.setBloqueadoHasta(LocalDateTime.now().plusMinutes(MINUTOS_BLOQUEO));
            usuario.setIntentosFallidos(0);//se reinicia 
        }

        usuarioRepo.save(usuario);
    }
}