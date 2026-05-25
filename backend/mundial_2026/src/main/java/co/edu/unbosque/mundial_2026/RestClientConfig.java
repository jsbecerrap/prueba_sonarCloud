package co.edu.unbosque.mundial_2026;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

/**
 * Configuración del cliente HTTP para consumir la API externa de fútbol (API-Sports)
 *
 * <p>Las credenciales se inyectan desde variables de entorno para evitar
 * exponer datos sensibles en el código fuente</p>
 */
@Configuration
public class RestClientConfig {

    /** URL base de la API de fútbol, inyectada desde {@code api.football.url} */
    @Value("${api.football.url}")
    private String apiFootballUrl;

    /** Clave de autenticación de la API, inyectada desde {@code api.football.key} */
    @Value("${api.football.key}")
    private String apiFootballKey;

    /**
     * Construye y registra un {@link RestClient} preconfigurado con la URL base
     * y el header de autenticación requerido por API-Sports ({@code x-apisports-key})
     *
     * @return instancia lista para hacer peticiones a la API de fútbol
     */
    @Bean
    public RestClient footballClient() {
        return RestClient.builder()
                .baseUrl(apiFootballUrl)//base url
                .defaultHeader("x-apisports-key", apiFootballKey)//header y la key
                .build();//construye el objeto 
    }
}