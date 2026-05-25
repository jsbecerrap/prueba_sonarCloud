package co.edu.unbosque.mundial_2026.security;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import co.edu.unbosque.mundial_2026.repository.UsuarioRepository;
import co.edu.unbosque.mundial_2026.security.filter.JwtAuthenticationFilter;
import co.edu.unbosque.mundial_2026.security.filter.JwtValidationFilter;

/**
 * Configuración principal de seguridad del sistema
 * define autenticación autorización filtros JWT CORS y políticas de sesión
 */
@Configuration
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Value("${jwt.secret}")
    private String jwtSecret;

    private final AuthenticationConfiguration authConfig;
    private final UsuarioRepository usuarioRepository;
    private final TokenBlacklist tokenBlacklist;

    private static final String API_ENTRADAS = "/api/entradas/**";
    private static final String API_PAYMENTS = "/payments/**";
    private static final String API_AUDITORIA = "/api/auditoria/**";

    public SecurityConfig(AuthenticationConfiguration authConfig,
                          UsuarioRepository usuarioRepository,
                          TokenBlacklist tokenBlacklist) {
        this.authConfig = authConfig;
        this.usuarioRepository = usuarioRepository;
        this.tokenBlacklist = tokenBlacklist;
    }

    /**
     * Expone el AuthenticationManager usado por Spring Security
     * encargado de procesar autenticación de credenciales
     */
    @Bean
    public AuthenticationManager authenticationManager() throws Exception {
        return authConfig.getAuthenticationManager();
    }

    /**
     * Define el encoder de contraseñas usando BCrypt
     * asegurando almacenamiento seguro de credenciales
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Inicializa la clave secreta del JWT al iniciar la aplicación
     */
    @Bean
    public CommandLineRunner initJwtKey() {
        return args -> TokenJwt.init(jwtSecret);
    }

    /**
     * Configura la cadena de filtros de seguridad HTTP
     * define rutas públicas protegidas roles filtros JWT CORS y sesión stateless
     */
  @Bean
public SecurityFilterChain filterChain(final HttpSecurity http,
                                       final AuthenticationManager authManager) throws Exception {

    return http.authorizeHttpRequests(authz -> authz
            .requestMatchers(HttpMethod.GET, "/api/usuarios/listar").permitAll()
            .requestMatchers(HttpMethod.POST, "/api/usuarios/registrar").permitAll()
            .requestMatchers(HttpMethod.GET, "/api/productos/**").permitAll()
            .requestMatchers(HttpMethod.GET, "/api/categorias").permitAll()
            .requestMatchers(HttpMethod.POST, "/api/auth/logout").authenticated()
            .requestMatchers(HttpMethod.POST, API_ENTRADAS).authenticated()
            .requestMatchers(HttpMethod.GET, API_ENTRADAS).authenticated()
            .requestMatchers(HttpMethod.PATCH, API_ENTRADAS).authenticated()
            .requestMatchers(HttpMethod.DELETE, API_ENTRADAS).authenticated()
            .requestMatchers(HttpMethod.PUT, API_ENTRADAS).authenticated()
            .requestMatchers(HttpMethod.GET, API_PAYMENTS).authenticated()
            .requestMatchers(HttpMethod.POST, API_PAYMENTS).authenticated()
            .requestMatchers(HttpMethod.PATCH, API_PAYMENTS).authenticated()
            .requestMatchers(HttpMethod.DELETE, API_PAYMENTS).authenticated()
            .requestMatchers(HttpMethod.PUT, API_PAYMENTS).authenticated()
            .requestMatchers(HttpMethod.GET, API_AUDITORIA).hasRole("ADMIN")
            .requestMatchers(HttpMethod.DELETE, "/api/**").authenticated()
            .requestMatchers(HttpMethod.PUT, "/api/**").authenticated()
            .anyRequest().authenticated())
            .addFilter(new JwtAuthenticationFilter(authManager, usuarioRepository))
            .addFilter(new JwtValidationFilter(authManager, tokenBlacklist))
            .csrf(config -> config.disable())//no usamos cookies
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))//use cors de abajo 
            .sessionManagement(management -> management
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS))//statless osea no guarda peticiones
            .exceptionHandling(ex -> ex
                    .authenticationEntryPoint((request, response, authException) -> {
                        response.setStatus(401);
                        response.setContentType("application/json");
                        response.getWriter().write("{\"error\":\"No autenticado\"}");//que pasa si trata de ingresar a algo que no 
                    })
            )
            .build();
}
    /**
     * Configuración CORS para permitir peticiones desde frontend locales y producción
     * define métodos permitidos headers y dominios autorizados
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        final CorsConfiguration config = new CorsConfiguration();

        config.setAllowedOriginPatterns(Arrays.asList(
                "http://localhost:3000",
                "http://localhost:4200",
                "http://localhost:8080",
                "http://localhost:5173",
                "https://mundial-2026-hub.vercel.app",
                "https://*.vercel.app"));//links que puede trate el front 

        config.setAllowedMethods(Arrays.asList("GET", "POST", "DELETE", "PUT", "PATCH"));
        config.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
        config.setAllowCredentials(true);

        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);//todas las rutas y simplemente lo añade de arriba 

        return source;
    }

    /**
     * Registra el filtro CORS con máxima prioridad para evitar bloqueos en solicitudes cross-origin
     *  los preflight requests del navegador ANTES que Spring Security,
 * evitando que los bloquee por falta de token JWT
     */
    @Bean
    public FilterRegistrationBean<CorsFilter> corsFilter() {
        final FilterRegistrationBean<CorsFilter> corsBean =
                new FilterRegistrationBean<>(new CorsFilter(corsConfigurationSource()));

        corsBean.setOrder(Ordered.HIGHEST_PRECEDENCE);// corre antes que todos los filtros
        return corsBean;
    }
}