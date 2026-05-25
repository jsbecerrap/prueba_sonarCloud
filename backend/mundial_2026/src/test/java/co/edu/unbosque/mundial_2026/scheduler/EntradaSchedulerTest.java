package co.edu.unbosque.mundial_2026.scheduler;

import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import co.edu.unbosque.mundial_2026.service.EntradaService;

/**
 * Pruebas unitarias para la clase EntradaScheduler.
 * Verifica la ejecución de tareas programadas
 * relacionadas con reservas de entradas.
 */
@ExtendWith(MockitoExtension.class)
class EntradaSchedulerTest {

    @Mock
    private EntradaService entradaService;

    @InjectMocks
    private EntradaScheduler scheduler;

    /**
     * Verifica que la expiración de reservas
     * invoque el servicio correspondiente.
     */
    @Test
    void expirarReservas_invocaExpirarReservasVencidas() {
        scheduler.expirarReservas();
        verify(entradaService).expirarReservasVencidas();
    }

    /**
     * Verifica que el aviso de reservas próximas a expirar
     * invoque el servicio correspondiente.
     */
    @Test
    void avisarPorExpirar_invocaAvisarReservasPorExpirar() {
        scheduler.avisarPorExpirar();
        verify(entradaService).avisarReservasPorExpirar();
    }

    /**
     * Verifica que una excepción en el servicio
     * durante la expiración de reservas se propague.
     */
    @Test
    void expirarReservas_serviceLanzaExcepcion_propaga() {
        doThrow(new RuntimeException("error")).when(entradaService).expirarReservasVencidas();
        org.junit.jupiter.api.Assertions.assertThrows(RuntimeException.class,
                () -> scheduler.expirarReservas());
    }

    /**
     * Verifica que una excepción en el servicio
     * durante el aviso de reservas se propague.
     */
    @Test
    void avisarPorExpirar_serviceLanzaExcepcion_propaga() {
        doThrow(new RuntimeException("error")).when(entradaService).avisarReservasPorExpirar();
        org.junit.jupiter.api.Assertions.assertThrows(RuntimeException.class,
                () -> scheduler.avisarPorExpirar());
    }
}