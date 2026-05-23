package co.edu.unbosque.mundial_2026;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.io.InputStream;

/**
 * Configuración de Firebase que inicializa la conexión con los servicios de Google
 * al arrancar la aplicación usando las credenciales del archivo de cuenta de servicio
 */
@Configuration
public class FireBaseConfig {

    /**
     * Inicializa {@link FirebaseApp} una sola vez al levantar el contexto de Spring
     *
     * <p>Lee el archivo {@code firebase-service-account.json} desde el classpath y
     * construye las opciones de conexión con las credenciales de Google — si el archivo
     * no existe, el método termina silenciosamente sin lanzar excepción, permitiendo
     * que la aplicación arranque aunque Firebase no esté disponible</p>
     *
     * @throws IOException si el archivo existe pero ocurre un error al leerlo
     */
    @PostConstruct
    public void inicializar() throws IOException {
        try (InputStream serviceAccount = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("firebase-service-account.json")) {

            if (serviceAccount == null) {
                return;
            }

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
            }
        }
    }
}