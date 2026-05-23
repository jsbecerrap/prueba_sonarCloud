package co.edu.unbosque.mundial_2026.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import co.edu.unbosque.mundial_2026.service.EntradaService;

/**
 * Scheduler encargado de automatizar procesos de control de reservas de entradas
 * ejecuta validaciones periódicas para expiración y notificación de reservas próximas a vencer
 */
@Component
public class EntradaScheduler {

    private final EntradaService entradaService;

    public EntradaScheduler(EntradaService entradaService) {
        this.entradaService = entradaService;
    }

    /**
     * Ejecuta la expiración automática de reservas vencidas
     * se ejecuta cada 2 minutos para liberar entradas no confirmadas a tiempo
     */
    @Scheduled(fixedDelay = 120_000)
    public void expirarReservas() {
        entradaService.expirarReservasVencidas();
    }

    /**
     * Envía alertas sobre reservas próximas a expirar
     * permite al usuario reaccionar antes de perder su entrada
     */
    @Scheduled(fixedDelay = 120_000)
    public void avisarPorExpirar() {
        entradaService.avisarReservasPorExpirar();
    }
}