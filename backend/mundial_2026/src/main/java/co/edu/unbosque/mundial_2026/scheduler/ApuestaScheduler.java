package co.edu.unbosque.mundial_2026.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import co.edu.unbosque.mundial_2026.repository.ApuestaRepository;
import co.edu.unbosque.mundial_2026.service.ApuestaService;

/**
 * Componente encargado de ejecutar tareas programadas relacionadas con apuestas
 * automatiza cierre de apuestas y cálculo de puntajes parciales y finales
 */
@Component
public class ApuestaScheduler {

    private final ApuestaService apuestaService;
    private final ApuestaRepository apuestaRepository;

    public ApuestaScheduler(ApuestaService apuestaService, ApuestaRepository apuestaRepository) {
        this.apuestaService = apuestaService;
        this.apuestaRepository = apuestaRepository;
    }

    /**
     * Ejecuta el cierre automático de apuestas vencidas
     * se ejecuta cada 5 minutos para actualizar el estado del sistema
     */
    @Scheduled(fixedDelay = 300_000)
    public void cerrarPollasvencidas() {
        apuestaService.cerrarApuestasVencidas();
    }

    /**
     * Calcula los puntos parciales de apuestas que aún están abiertas
     * permite actualización progresiva del ranking de usuarios
     */
    @Scheduled(fixedDelay = 600_000)
    public void calcularParciales() {
        apuestaRepository.findByEstado("ABIERTA")
                .forEach(a -> apuestaService.calcularPuntosParciales(a.getId()));
    }

    /**
     * Calcula los puntos finales de todas las apuestas cerradas
     * asegurando actualización completa de resultados del sistema
     */
    @Scheduled(fixedDelay = 600_000)
    public void calcularFinales() {
        apuestaService.calcularPuntosAutomatico();
    }
}