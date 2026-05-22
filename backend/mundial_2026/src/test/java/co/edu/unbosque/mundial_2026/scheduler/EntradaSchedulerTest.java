package co.edu.unbosque.mundial_2026.scheduler;

import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import co.edu.unbosque.mundial_2026.service.EntradaService;

@ExtendWith(MockitoExtension.class)
class EntradaSchedulerTest {

    @Mock
    private EntradaService entradaService;

    @InjectMocks
    private EntradaScheduler scheduler;

    @Test
    void expirarReservas_invocaExpirarReservasVencidas() {
        scheduler.expirarReservas();
        verify(entradaService).expirarReservasVencidas();
    }

    @Test
    void avisarPorExpirar_invocaAvisarReservasPorExpirar() {
        scheduler.avisarPorExpirar();
        verify(entradaService).avisarReservasPorExpirar();
    }

    @Test
    void expirarReservas_serviceLanzaExcepcion_propaga() {
        doThrow(new RuntimeException("error")).when(entradaService).expirarReservasVencidas();
        org.junit.jupiter.api.Assertions.assertThrows(RuntimeException.class,
                () -> scheduler.expirarReservas());
    }

    @Test
    void avisarPorExpirar_serviceLanzaExcepcion_propaga() {
        doThrow(new RuntimeException("error")).when(entradaService).avisarReservasPorExpirar();
        org.junit.jupiter.api.Assertions.assertThrows(RuntimeException.class,
                () -> scheduler.avisarPorExpirar());
    }
}