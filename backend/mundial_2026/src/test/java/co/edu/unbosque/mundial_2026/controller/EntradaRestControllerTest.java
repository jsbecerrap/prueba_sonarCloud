package co.edu.unbosque.mundial_2026.controller;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import co.edu.unbosque.mundial_2026.dto.CuposZonaDTO;
import co.edu.unbosque.mundial_2026.dto.PartidoCapacidadDTO;
import co.edu.unbosque.mundial_2026.dto.request.EntradaRequestDTO;
import co.edu.unbosque.mundial_2026.dto.request.TransferenciaRequestDTO;
import co.edu.unbosque.mundial_2026.dto.response.EntradaResponseDTO;
import co.edu.unbosque.mundial_2026.exception.*;
import co.edu.unbosque.mundial_2026.service.EntradaService;

@ExtendWith(MockitoExtension.class)
class EntradaRestControllerTest {

    @Mock private EntradaService entradaService;
    @InjectMocks private EntradaController controller;
    private static final String USER_CORREO = "user@test.com";
private static final String PAGADA = "PAGADA";
private static final String PM_TEST = "pm_test";

    /** 
     * @param estado
     * @return EntradaResponseDTO
     */
    private EntradaResponseDTO responseDTO(String estado) {
        EntradaResponseDTO dto = new EntradaResponseDTO();
        dto.setId(1L);
        dto.setEstado(estado);
        dto.setCantidad(2);
        dto.setPrecio(100000.0);
        return dto;
    }

    @Test
    void cuposPorZona_retornaOkConLista() {
        CuposZonaDTO zona = new CuposZonaDTO("BARRA", 1000, 200);
        when(entradaService.obtenerCuposPorZona(1L)).thenReturn(List.of(zona));

        ResponseEntity<List<CuposZonaDTO>> res = controller.cuposPorZona(1L);

        assertEquals(200, res.getStatusCode().value());
        assertNotNull(res.getBody());
        assertEquals(1, res.getBody().size());
        assertEquals("BARRA", res.getBody().get(0).getZona());
    }

    @Test
    void cuposPorZona_listaVacia_retornaOkVacio() {
        when(entradaService.obtenerCuposPorZona(1L)).thenReturn(List.of());

        ResponseEntity<List<CuposZonaDTO>> res = controller.cuposPorZona(1L);

        assertEquals(200, res.getStatusCode().value());
        assertTrue(res.getBody().isEmpty());
    }

    @Test
    void obtener_entradaExistente_retornaOk() {
        when(entradaService.obtenerEntrada(1L)).thenReturn(responseDTO(PAGADA));

        ResponseEntity<EntradaResponseDTO> res = controller.obtener(1L);

        assertEquals(200, res.getStatusCode().value());
        assertNotNull(res.getBody());
        assertEquals(PAGADA, res.getBody().getEstado());
    }

    @Test
    void obtener_entradaNoExistente_propagaExcepcion() {
        when(entradaService.obtenerEntrada(99L)).thenThrow(new EntradaNotFoundException("no encontrada"));

        assertThrows(EntradaNotFoundException.class, () -> controller.obtener(99L));
    }

    @Test
    void reservar_exitosa_retornaOk() {
        EntradaRequestDTO dto = new EntradaRequestDTO();
        dto.setPartidoId(1L);
        dto.setCantidad(2);
        dto.setCategoria("BARRA");
        when(entradaService.reservarEntrada(USER_CORREO, dto)).thenReturn(responseDTO("RESERVADA"));

        ResponseEntity<EntradaResponseDTO> res = controller.reservar(USER_CORREO, dto);

        assertEquals(200, res.getStatusCode().value());
        assertEquals("RESERVADA", res.getBody().getEstado());
        verify(entradaService).reservarEntrada(USER_CORREO, dto);
    }

    @Test
    void reservar_sinCapacidad_propagaExcepcion() {
        EntradaRequestDTO dto = new EntradaRequestDTO();
        dto.setPartidoId(1L);
        dto.setCantidad(2);
        when(entradaService.reservarEntrada(any(), any())).thenThrow(new CupoNoDisponibleException("sin cupo"));

        assertThrows(CupoNoDisponibleException.class, () -> controller.reservar(USER_CORREO, dto));
    }

    @Test
    void reservar_limiteSuperado_propagaExcepcion() {
        EntradaRequestDTO dto = new EntradaRequestDTO();
        dto.setPartidoId(1L);
        dto.setCantidad(5);
        when(entradaService.reservarEntrada(any(), any())).thenThrow(new LimiteSuperadoException("limite"));

        assertThrows(LimiteSuperadoException.class, () -> controller.reservar(USER_CORREO, dto));
    }

    @Test
    void pagar_exitoso_retornaOk() {
        when(entradaService.confirmarPago(1L, PM_TEST)).thenReturn(responseDTO(PAGADA));

        ResponseEntity<EntradaResponseDTO> res = controller.pagar(1L, PM_TEST);

        assertEquals(200, res.getStatusCode().value());
        assertEquals(PAGADA, res.getBody().getEstado());
        verify(entradaService).confirmarPago(1L, PM_TEST);
    }

    @Test
    void pagar_estadoInvalido_propagaExcepcion() {
        when(entradaService.confirmarPago(any(), any())).thenThrow(new EstadoInvalidoException("no reservada"));

        assertThrows(EstadoInvalidoException.class, () -> controller.pagar(1L, PM_TEST));
    }

    @Test
    void pagar_errorStripe_propagaExcepcion() {
        when(entradaService.confirmarPago(any(), any())).thenThrow(new PagoStripeException("stripe error"));

        assertThrows(PagoStripeException.class, () -> controller.pagar(1L, PM_TEST));
    }

    @Test
    void cancelar_exitosa_retornaOk() {
        when(entradaService.cancelarReserva(USER_CORREO, 1L)).thenReturn(responseDTO("CANCELADA"));

        ResponseEntity<EntradaResponseDTO> res = controller.cancelar(USER_CORREO, 1L);

        assertEquals(200, res.getStatusCode().value());
        assertEquals("CANCELADA", res.getBody().getEstado());
        verify(entradaService).cancelarReserva(USER_CORREO, 1L);
    }

    @Test
    void cancelar_entradaNoPertenece_propagaExcepcion() {
        when(entradaService.cancelarReserva(any(), any())).thenThrow(new EstadoInvalidoException("no pertenece"));

        assertThrows(EstadoInvalidoException.class, () -> controller.cancelar(USER_CORREO, 1L));
    }

    @Test
    void cancelar_entradaNoEncontrada_propagaExcepcion() {
        when(entradaService.cancelarReserva(any(), any())).thenThrow(new EntradaNotFoundException("no encontrada"));

        assertThrows(EntradaNotFoundException.class, () -> controller.cancelar(USER_CORREO, 1L));
    }

    @Test
    void transferir_exitosa_retornaOk() {
        TransferenciaRequestDTO dto = new TransferenciaRequestDTO();
        dto.setCorreoDestino("destino@test.com");
        when(entradaService.transferirEntrada(1L, dto, USER_CORREO)).thenReturn(responseDTO(PAGADA));

        ResponseEntity<EntradaResponseDTO> res = controller.transferir(USER_CORREO, 1L, dto);

        assertEquals(200, res.getStatusCode().value());
        verify(entradaService).transferirEntrada(1L, dto, USER_CORREO);
    }

    @Test
    void transferir_estadoNoPagada_propagaExcepcion() {
        TransferenciaRequestDTO dto = new TransferenciaRequestDTO();
        dto.setCorreoDestino("destino@test.com");
        when(entradaService.transferirEntrada(any(), any(), any()))
                .thenThrow(new EstadoInvalidoException("no pagada"));

        assertThrows(EstadoInvalidoException.class, () -> controller.transferir(USER_CORREO, 1L, dto));
    }

    @Test
    void transferir_limiteSuperado_propagaExcepcion() {
        TransferenciaRequestDTO dto = new TransferenciaRequestDTO();
        dto.setCorreoDestino("destino@test.com");
        when(entradaService.transferirEntrada(any(), any(), any()))
                .thenThrow(new LimiteSuperadoException("limite diario"));

        assertThrows(LimiteSuperadoException.class, () -> controller.transferir(USER_CORREO, 1L, dto));
    }

    @Test
    void reembolsar_exitoso_retornaOk() {
        when(entradaService.reembolsarEntrada(USER_CORREO, 1L)).thenReturn(responseDTO("REEMBOLSADA"));

        ResponseEntity<EntradaResponseDTO> res = controller.reembolsar(USER_CORREO, 1L);

        assertEquals(200, res.getStatusCode().value());
        assertEquals("REEMBOLSADA", res.getBody().getEstado());
        verify(entradaService).reembolsarEntrada(USER_CORREO, 1L);
    }

    @Test
    void reembolsar_estadoNoPagada_propagaExcepcion() {
        when(entradaService.reembolsarEntrada(any(), any()))
                .thenThrow(new EstadoInvalidoException("no pagada"));

        assertThrows(EstadoInvalidoException.class, () -> controller.reembolsar(USER_CORREO, 1L));
    }

    @Test
    void reembolsar_errorStripe_propagaExcepcion() {
        when(entradaService.reembolsarEntrada(any(), any()))
                .thenThrow(new PagoStripeException("stripe error"));

        assertThrows(PagoStripeException.class, () -> controller.reembolsar(USER_CORREO, 1L));
    }

    @Test
    void reembolsar_entradaNoPertenece_propagaExcepcion() {
        when(entradaService.reembolsarEntrada(any(), any()))
                .thenThrow(new EstadoInvalidoException("no pertenece"));

        assertThrows(EstadoInvalidoException.class, () -> controller.reembolsar(USER_CORREO, 1L));
    }

    @Test
    void listar_retornaOkConEntradas() {
        when(entradaService.listarEntradasUsuario(USER_CORREO))
                .thenReturn(List.of(responseDTO(PAGADA), responseDTO("RESERVADA")));

        ResponseEntity<List<EntradaResponseDTO>> res = controller.listar(USER_CORREO);

        assertEquals(200, res.getStatusCode().value());
        assertEquals(2, res.getBody().size());
    }

    @Test
    void listar_sinEntradas_retornaOkVacio() {
        when(entradaService.listarEntradasUsuario(USER_CORREO)).thenReturn(List.of());

        ResponseEntity<List<EntradaResponseDTO>> res = controller.listar(USER_CORREO);

        assertEquals(200, res.getStatusCode().value());
        assertTrue(res.getBody().isEmpty());
    }

    @Test
    void listarPartidos_retornaOkConLista() {
        PartidoCapacidadDTO p = new PartidoCapacidadDTO();
        p.setId(1L);
        p.setLocal("Colombia");
        p.setVisitante("Brazil");
        when(entradaService.listarPartidosConCapacidad()).thenReturn(List.of(p));

        ResponseEntity<List<PartidoCapacidadDTO>> res = controller.listarPartidos();

        assertEquals(200, res.getStatusCode().value());
        assertEquals(1, res.getBody().size());
    }

    @Test
    void listarPartidos_listaVacia_retornaOkVacio() {
        when(entradaService.listarPartidosConCapacidad()).thenReturn(List.of());

        ResponseEntity<List<PartidoCapacidadDTO>> res = controller.listarPartidos();

        assertEquals(200, res.getStatusCode().value());
        assertTrue(res.getBody().isEmpty());
    }
}