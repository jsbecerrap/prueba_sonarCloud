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

public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private static final int SEGUNDOS_DIA = 86400;

    // ===== Parámetros de rate limiting / bloqueo por intentos fallidos =====
    private static final int MAX_INTENTOS_FALLIDOS = 5;
    private static final int MINUTOS_BLOQUEO = 15;

    // Límite real de bcrypt; contraseñas más largas son cortadas internamente
    // y, sobre todo, permiten ataques de DoS por costo de hash.
    private static final int MAX_LONGITUD_PASSWORD = 72;

    // Clave que usamos para guardar el correo en el request entre
    // attemptAuthentication y unsuccessfulAuthentication
    private static final String ATTR_CORREO_INTENTO = "correoIntentoLogin";

    private final AuthenticationManager authManager;
    private final UsuarioRepository usuarioRepo;

    public JwtAuthenticationFilter(final AuthenticationManager authManager,
            final UsuarioRepository usuarioRepo) {
        super(authManager);
        this.authManager = authManager;
        this.usuarioRepo = usuarioRepo;
    }

    /*
     * Lee las credenciales del request UNA sola vez. Antes de delegar al
     * AuthenticationManager, verifica si el usuario está bloqueado por
     * intentos fallidos previos y rechaza el login en ese caso.
     */
    @Override
    public Authentication attemptAuthentication(final HttpServletRequest request,
            final HttpServletResponse response) throws AuthenticationException {
        try {
            final Usuario credenciales = new ObjectMapper()
                    .readValue(request.getInputStream(), Usuario.class);

            final String correo = credenciales.getCorreoUsuario();
            final String contrasena = credenciales.getContrasena();

            // Guardamos el correo en el request para poder identificarlo en
            // unsuccessfulAuthentication. Si es null se queda en null y
            // simplemente no se incrementa contador (no podemos saber a quién).
            request.setAttribute(ATTR_CORREO_INTENTO, correo);

            // Anti bcrypt DoS: rechaza contraseñas absurdamente largas
            // sin siquiera intentar compararlas.
            if (contrasena != null && contrasena.length() > MAX_LONGITUD_PASSWORD) {
                throw new AuthenticationServiceException(
                        "La contraseña excede la longitud permitida");
            }

            // Verifica si la cuenta está actualmente bloqueada por intentos
            // fallidos. Solo aplica para usuarios existentes.
            if (correo != null && !correo.isBlank()) {
                final Optional<Usuario> opt = usuarioRepo.findByCorreoUsuario(correo);
                if (opt.isPresent()) {
                    final Usuario usuario = opt.get();
                    if (usuario.getBloqueadoHasta() != null
                            && usuario.getBloqueadoHasta().isAfter(LocalDateTime.now())) {
                        throw new LockedException("Cuenta temporalmente bloqueada");
                    }
                }
            }

            return authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(correo, contrasena));

        } catch (IOException e) {
            throw new AuthenticationServiceException("Error al leer las credenciales", e);
        }
    }

    /*
     * Autenticación exitosa: emite el token JWT y resetea el contador
     * de intentos fallidos del usuario.
     */
    @Override
    protected void successfulAuthentication(final HttpServletRequest request,
            final HttpServletResponse response,
            final FilterChain chain,
            final Authentication authResult) throws IOException, ServletException {

        final String username = authResult.getName();
        final Usuario usuario = usuarioRepo.findByCorreoUsuario(username)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        // Reset del contador de intentos fallidos tras login exitoso
        if ((usuario.getIntentosFallidos() != null && usuario.getIntentosFallidos() > 0)
                || usuario.getBloqueadoHasta() != null) {
            usuario.setIntentosFallidos(0);
            usuario.setBloqueadoHasta(null);
            usuarioRepo.save(usuario);
        }

        final String nombreCompleto = usuario.getNombre() + " " + usuario.getApellido();
        List<GrantedAuthority> roles = new ArrayList<>(authResult.getAuthorities());

        final String token = Jwts.builder()
                .subject(username)
                .claim("authorities", roles.stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toList()))
                .expiration(Date.from(Instant.now().plusSeconds(SEGUNDOS_DIA)))
                .signWith(TokenJwt.getSecretKey())
                .compact();

        response.addHeader(HEADER_AUTHORIZATION, PREFIX_TOKEN + token);

        final Map<String, String> json = new HashMap<>();
        json.put("token", token);
        json.put("username", username);
        json.put("mensaje", "Hola " + nombreCompleto + " sesión iniciada correctamente");

        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(new ObjectMapper().writeValueAsString(json));
    }

    @Override
    protected void unsuccessfulAuthentication(final HttpServletRequest request,
            final HttpServletResponse response,
            final AuthenticationException failed) throws IOException, ServletException {

        final Object attrCorreo = request.getAttribute(ATTR_CORREO_INTENTO);
        final String correo = (attrCorreo != null) ? attrCorreo.toString() : null;
       final boolean estabaBloqueado = failed instanceof LockedException;

        if (!estabaBloqueado && correo != null && !correo.isBlank()) {
            usuarioRepo.findByCorreoUsuario(correo).ifPresent(this::registrarIntentoFallido);
        }

        final Map<String, String> json = new HashMap<>();
        if (estabaBloqueado) {
            json.put("mensaje", "Demasiados intentos. Espera unos minutos.");
            response.setStatus(429); // Too Many Requests
        } else {
            json.put("mensaje", "Error en la autenticacion correo/contraseña incorrecto");
            response.setStatus(401);
        }
        json.put("error", failed.getMessage());

        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(new ObjectMapper().writeValueAsString(json));
    }

    private void registrarIntentoFallido(final Usuario usuario) {
        final int intentos = (usuario.getIntentosFallidos() != null
                ? usuario.getIntentosFallidos() : 0) + 1;
        usuario.setIntentosFallidos(intentos);

        if (intentos >= MAX_INTENTOS_FALLIDOS) {
            usuario.setBloqueadoHasta(LocalDateTime.now().plusMinutes(MINUTOS_BLOQUEO));
            
            usuario.setIntentosFallidos(0);
        }
        usuarioRepo.save(usuario);
    }
}