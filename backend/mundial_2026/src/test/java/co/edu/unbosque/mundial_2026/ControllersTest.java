package co.edu.unbosque.mundial_2026;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import co.edu.unbosque.mundial_2026.controller.ApuestaRestController;
import co.edu.unbosque.mundial_2026.controller.EntradaRestController;
import co.edu.unbosque.mundial_2026.controller.OrdenController;
import co.edu.unbosque.mundial_2026.controller.PartidoController;
import co.edu.unbosque.mundial_2026.dto.ApuestaDTO;
import co.edu.unbosque.mundial_2026.dto.ParticipacionDTO;
import co.edu.unbosque.mundial_2026.dto.PronosticoDTO;
import co.edu.unbosque.mundial_2026.dto.response.EntradaResponseDTO;
import co.edu.unbosque.mundial_2026.dto.response.OrdenResponseDTO;
import co.edu.unbosque.mundial_2026.dto.response.PartidoDTO;
import co.edu.unbosque.mundial_2026.dto.response.PreferenciaDTO;
import co.edu.unbosque.mundial_2026.service.ApuestaService;
import co.edu.unbosque.mundial_2026.service.EntradaService;
import co.edu.unbosque.mundial_2026.service.OrdenService;
import co.edu.unbosque.mundial_2026.service.PartidoService;

@ExtendWith(MockitoExtension.class)
class ControllersTest {

    // ── ApuestaRestController ─────────────────────────────────────────────
    @Mock private ApuestaService apuestaService;
    @InjectMocks private ApuestaRestController apuestaController;

    // ── EntradaRestController ─────────────────────────────────────────────
    @Mock private EntradaService entradaService;
    @InjectMocks private EntradaRestController entradaController;

    // ── OrdenController ───────────────────────────────────────────────────
    @Mock private OrdenService ordenService;
    @InjectMocks private OrdenController ordenController;

    // ── PartidoController ─────────────────────────────────────────────────
    @Mock private PartidoService partidoService;
    @InjectMocks private PartidoController partidoController;

    // ── ApuestaRestController tests ───────────────────────────────────────

    @Test
    void apuesta_listarPorUsuario_retornaOk() {
        when(apuestaService.listarApuestasPorUsuario(1L)).thenReturn(List.of());
        ResponseEntity<?> res = apuestaController.listarApuestasPorUsuario(1L);
        assertEquals(200, res.getStatusCode().value());
    }

    @Test
    void apuesta_obtenerApuesta_retornaOk() {
        when(apuestaService.obtenerApuesta(1L)).thenReturn(new ApuestaDTO());
        ResponseEntity<?> res = apuestaController.obtenerApuesta(1L);
        assertEquals(200, res.getStatusCode().value());
    }

    @Test
    void apuesta_obtenerRanking_retornaOk() {
        when(apuestaService.obtenerRanking(1L)).thenReturn(List.of());
        ResponseEntity<?> res = apuestaController.obtenerRanking(1L);
        assertEquals(200, res.getStatusCode().value());
    }

    @Test
    void apuesta_listarParticipantes_retornaOk() {
        when(apuestaService.listarParticipantes(1L)).thenReturn(List.of());
        ResponseEntity<?> res = apuestaController.listarParticipantes(1L);
        assertEquals(200, res.getStatusCode().value());
    }

    @Test
    void apuesta_verificarPronostico_retornaOk() {
        when(apuestaService.verificarPronostico(1L)).thenReturn(new PronosticoDTO());
        ResponseEntity<?> res = apuestaController.verificarPronostico(1L);
        assertEquals(200, res.getStatusCode().value());
    }

    // ── EntradaRestController tests ───────────────────────────────────────

    @Test
    void entrada_listarPartidos_retornaOk() {
        when(entradaService.listarPartidosConCapacidad()).thenReturn(List.of());
        ResponseEntity<?> res = entradaController.listarPartidos();
        assertEquals(200, res.getStatusCode().value());
    }

    @Test
    void entrada_obtener_retornaOk() {
        when(entradaService.obtenerEntrada(1L)).thenReturn(new EntradaResponseDTO());
        ResponseEntity<?> res = entradaController.obtener(1L);
        assertEquals(200, res.getStatusCode().value());
    }

    // ── OrdenController tests ─────────────────────────────────────────────

    @Test
    void orden_historial_retornaOk() {
        org.springframework.security.core.userdetails.UserDetails user =
            org.springframework.security.core.userdetails.User
                .withUsername("test@test.com")
                .password("pass")
                .roles("USUARIO")
                .build();
        when(ordenService.historial("test@test.com")).thenReturn(List.of());
        ResponseEntity<?> res = ordenController.historial(user);
        assertEquals(200, res.getStatusCode().value());
    }

    @Test
    void orden_carrito_retornaOk() {
        org.springframework.security.core.userdetails.UserDetails user =
            org.springframework.security.core.userdetails.User
                .withUsername("test@test.com")
                .password("pass")
                .roles("USUARIO")
                .build();
        when(ordenService.obtenerCarrito("test@test.com")).thenReturn(new OrdenResponseDTO());
        ResponseEntity<?> res = ordenController.carrito(user);
        assertEquals(200, res.getStatusCode().value());
    }

    // ── PartidoController tests ───────────────────────────────────────────

    @Test
    void partido_catalogo_selecciones_retornaOk() {
        when(partidoService.obtenerCatalogoSelecciones()).thenReturn(List.of());
        ResponseEntity<?> res = partidoController.obtenerCatalogoSelecciones();
        assertEquals(200, res.getStatusCode().value());
    }
}