package co.edu.unbosque.mundial_2026.scheduler;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import co.edu.unbosque.mundial_2026.service.PartidoService;

/**
 * Pruebas unitarias para la clase PartidoScheduler.
 * Verifica la sincronización automática
 * de resultados de partidos.
 */
@ExtendWith(MockitoExtension.class)
class PartidoSchedulerTest {

    @Mock
    private PartidoService partidoService;

    @InjectMocks
    private PartidoScheduler scheduler;

    /**
     * Verifica que la actualización de resultados
     * invoque la sincronización con la liga y temporada configuradas.
     */
    @Test
    void actualizarResultadosHoy_invocaSincronizarConLigaYTemporada()  {
        when(partidoService.sincronizarPorFechaYLiga(anyString(), eq(1), eq(2026))).thenReturn(5);

        scheduler.actualizarResultadosHoy();

        verify(partidoService).sincronizarPorFechaYLiga(anyString(), eq(1), eq(2026));
    }

    /**
     * Verifica que una excepción en el servicio
     * no se propague durante la actualización automática.
     */
    @Test
    void actualizarResultadosHoy_serviceLanzaExcepcion_noPropagas() {
        when(partidoService.sincronizarPorFechaYLiga(anyString(), anyInt(), anyInt()))
                .thenThrow(new RuntimeException("API caída"));

        org.junit.jupiter.api.Assertions.assertDoesNotThrow(() -> scheduler.actualizarResultadosHoy());
    }

    /**
     * Verifica que una actualización sin resultados
     * no genere excepciones.
     */
    @Test
    void actualizarResultadosHoy_retornaCeroActualizados_noLanzaExcepcion() {
        when(partidoService.sincronizarPorFechaYLiga(anyString(), eq(1), eq(2026))).thenReturn(0);

        org.junit.jupiter.api.Assertions.assertDoesNotThrow(() -> scheduler.actualizarResultadosHoy());
        verify(partidoService).sincronizarPorFechaYLiga(anyString(), eq(1), eq(2026));
    }
}