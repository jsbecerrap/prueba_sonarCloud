package co.edu.unbosque.mundial_2026.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import co.edu.unbosque.mundial_2026.service.EntradaService;

@Component
public class EntradaScheduler {

    private final EntradaService entradaService;

    public EntradaScheduler(EntradaService entradaService) {
        this.entradaService = entradaService;
    }

    @Scheduled(fixedDelay = 120_000)
    public void expirarReservas() {
        entradaService.expirarReservasVencidas();
    }

    @Scheduled(fixedDelay = 120_000)
    public void avisarPorExpirar() {
        entradaService.avisarReservasPorExpirar();
    }
}