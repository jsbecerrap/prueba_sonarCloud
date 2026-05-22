package co.edu.unbosque.mundial_2026.scheduler;

import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import co.edu.unbosque.mundial_2026.entity.Apuesta;
import co.edu.unbosque.mundial_2026.repository.ApuestaRepository;
import co.edu.unbosque.mundial_2026.service.ApuestaService;

@ExtendWith(MockitoExtension.class)
class ApuestaSchedulerTest {

    @Mock
    private ApuestaService apuestaService;

    @Mock
    private ApuestaRepository apuestaRepository;

    @InjectMocks
    private ApuestaScheduler scheduler;

    @Test
    void cerrarPollasvencidas_invocaCerrarApuestasVencidas() {
        scheduler.cerrarPollasvencidas();
        verify(apuestaService).cerrarApuestasVencidas();
    }

    @Test
    void calcularParciales_conApuestasAbiertas_calculaPorCadaUna() {
        Apuesta a1 = new Apuesta();
        a1.setId(1L);
        Apuesta a2 = new Apuesta();
        a2.setId(2L);
        when(apuestaRepository.findByEstado("ABIERTA")).thenReturn(List.of(a1, a2));

        scheduler.calcularParciales();

        verify(apuestaService).calcularPuntosParciales(1L);
        verify(apuestaService).calcularPuntosParciales(2L);
    }

    @Test
    void calcularParciales_sinApuestasAbiertas_noCalculaNada() {
        when(apuestaRepository.findByEstado("ABIERTA")).thenReturn(List.of());

        scheduler.calcularParciales();

        verify(apuestaService, never()).calcularPuntosParciales(any());
    }

    @Test
    void calcularFinales_invocaCalculaPuntosAutomatico() {
        scheduler.calcularFinales();
        verify(apuestaService).calcularPuntosAutomatico();
    }
}