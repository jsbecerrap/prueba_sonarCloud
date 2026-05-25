package co.edu.unbosque.mundial_2026;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Clase principal de la aplicación Spring Boot del sistema Mundial 2026
 *
 * <p>Habilita la ejecución de tareas programadas con {@code @EnableScheduling}
 * y el procesamiento asíncrono de métodos con {@code @EnableAsync}</p>// 
 */
@EnableScheduling
@EnableAsync//permite trabjar de forma asincrona por ejemplo las notificaciones
@SpringBootApplication
public class Mundial2026Application {

	/**
	 * Punto de entrada de la aplicación — arranca el contexto de Spring Boot
	 *
	 * @param args argumentos de línea de comandos
	 */
	public static void main(String[] args) {
		SpringApplication.run(Mundial2026Application.class, args);
	}

}