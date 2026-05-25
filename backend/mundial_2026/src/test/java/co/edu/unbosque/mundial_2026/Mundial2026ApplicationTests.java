package co.edu.unbosque.mundial_2026;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Clase de prueba encargada de verificar que el contexto de Spring Boot
 * se cargue correctamente durante la ejecución de tests.
 */
@SpringBootTest
@ActiveProfiles("test")
class Mundial2026ApplicationTests {

    /**
     * Verifica que el contexto de la aplicación se cargue correctamente.
     */
    @Test
    void contextLoads() {
        //verifica que spring se carge correctamente(contexto)
    }
}