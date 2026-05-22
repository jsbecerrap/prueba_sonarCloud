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

import co.edu.unbosque.mundial_2026.dto.request.MetodoPagoRequestDTO;
import co.edu.unbosque.mundial_2026.dto.response.MetodoPagoResponseDTO;
import co.edu.unbosque.mundial_2026.service.MetodoPagoService;

@ExtendWith(MockitoExtension.class)
class MetodoPagoControllerTest {

    @Mock
    private MetodoPagoService metodoPagoService;

    @InjectMocks
    private MetodoPagoController controller;

    private MetodoPagoRequestDTO requestValido() {
        MetodoPagoRequestDTO dto = new MetodoPagoRequestDTO();
        dto.setType("CARD");
        dto.setLabel("Visa Principal");
        dto.setDetails("4242-4242");
        return dto;
    }

    private MetodoPagoResponseDTO responseDTO() {
        MetodoPagoResponseDTO dto = new MetodoPagoResponseDTO();
        dto.setId("mp-1");
        dto.setType("CARD");
        dto.setLabel("Visa Principal");
        dto.setDefault(false);
        return dto;
    }

   

    @Test
    void listar_retorna200ConLista() {
        when(metodoPagoService.listarPorCorreo("user@test.com"))
                .thenReturn(List.of(responseDTO()));

        ResponseEntity<List<MetodoPagoResponseDTO>> res = controller.listar("user@test.com");

        assertEquals(200, res.getStatusCode().value());
        assertEquals(1, res.getBody().size());
        verify(metodoPagoService).listarPorCorreo("user@test.com");
    }

    @Test
    void listar_listaVacia_retorna200() {
        when(metodoPagoService.listarPorCorreo("user@test.com")).thenReturn(List.of());

        ResponseEntity<List<MetodoPagoResponseDTO>> res = controller.listar("user@test.com");

        assertEquals(200, res.getStatusCode().value());
        assertTrue(res.getBody().isEmpty());
    }

    @Test
    void listar_serviceLanzaExcepcion_propaga() {
        when(metodoPagoService.listarPorCorreo(any())).thenThrow(new RuntimeException("error"));

        assertThrows(RuntimeException.class, () -> controller.listar("user@test.com"));
    }

   

    @Test
    void agregar_valido_retorna200() {
        when(metodoPagoService.agregar("user@test.com", requestValido())).thenReturn(responseDTO());

        ResponseEntity<MetodoPagoResponseDTO> res = controller.agregar("user@test.com", requestValido());

        assertEquals(200, res.getStatusCode().value());
        assertNotNull(res.getBody());
        assertEquals("CARD", res.getBody().getType());
        verify(metodoPagoService).agregar(eq("user@test.com"), any());
    }

    @Test
    void agregar_serviceRetornaNull_retorna400() {
        when(metodoPagoService.agregar(any(), any())).thenReturn(null);

        ResponseEntity<MetodoPagoResponseDTO> res = controller.agregar("user@test.com", requestValido());

        assertEquals(400, res.getStatusCode().value());
        assertNull(res.getBody());
    }

    @Test
    void agregar_tipoPSE_retorna200() {
        MetodoPagoRequestDTO dto = requestValido();
        dto.setType("PSE");
        MetodoPagoResponseDTO resp = responseDTO();
        resp.setType("PSE");
        when(metodoPagoService.agregar(any(), any())).thenReturn(resp);

        ResponseEntity<MetodoPagoResponseDTO> res = controller.agregar("user@test.com", dto);

        assertEquals(200, res.getStatusCode().value());
        assertEquals("PSE", res.getBody().getType());
    }

    @Test
    void agregar_tipoCASH_retorna200() {
        MetodoPagoRequestDTO dto = requestValido();
        dto.setType("CASH");
        when(metodoPagoService.agregar(any(), any())).thenReturn(responseDTO());

        ResponseEntity<MetodoPagoResponseDTO> res = controller.agregar("user@test.com", dto);

        assertEquals(200, res.getStatusCode().value());
    }

    @Test
    void agregar_tipoTRANSFER_retorna200() {
        MetodoPagoRequestDTO dto = requestValido();
        dto.setType("TRANSFER");
        when(metodoPagoService.agregar(any(), any())).thenReturn(responseDTO());

        ResponseEntity<MetodoPagoResponseDTO> res = controller.agregar("user@test.com", dto);

        assertEquals(200, res.getStatusCode().value());
    }

    @Test
    void agregar_serviceLanzaExcepcion_propaga() {
        when(metodoPagoService.agregar(any(), any())).thenThrow(new RuntimeException("error"));

        assertThrows(RuntimeException.class,
                () -> controller.agregar("user@test.com", requestValido()));
    }

   

    @Test
    void setDefault_retorna204() {
        doNothing().when(metodoPagoService).setDefaultPorCorreo("user@test.com", 1L);

        ResponseEntity<Void> res = controller.setDefault("user@test.com", 1L);

        assertEquals(204, res.getStatusCode().value());
        assertNull(res.getBody());
        verify(metodoPagoService).setDefaultPorCorreo("user@test.com", 1L);
    }

    @Test
    void setDefault_serviceLanzaExcepcion_propaga() {
        doThrow(new RuntimeException("no encontrado"))
                .when(metodoPagoService).setDefaultPorCorreo(any(), eq(99L));

        assertThrows(RuntimeException.class,
                () -> controller.setDefault("user@test.com", 99L));
    }



    @Test
    void eliminar_retorna204() {
        doNothing().when(metodoPagoService).eliminar("user@test.com", 1L);

        ResponseEntity<Void> res = controller.eliminar("user@test.com", 1L);

        assertEquals(204, res.getStatusCode().value());
        assertNull(res.getBody());
        verify(metodoPagoService).eliminar("user@test.com", 1L);
    }

    @Test
    void eliminar_serviceLanzaExcepcion_propaga() {
        doThrow(new RuntimeException("no encontrado"))
                .when(metodoPagoService).eliminar(any(), eq(99L));

        assertThrows(RuntimeException.class,
                () -> controller.eliminar("user@test.com", 99L));
    }

   

    @Test
    void actualizar_valido_retorna200() {
        when(metodoPagoService.actualizar("user@test.com", 1L, requestValido()))
                .thenReturn(responseDTO());

        ResponseEntity<MetodoPagoResponseDTO> res =
                controller.actualizar("user@test.com", 1L, requestValido());

        assertEquals(200, res.getStatusCode().value());
        assertNotNull(res.getBody());
        verify(metodoPagoService).actualizar(eq("user@test.com"), eq(1L), any());
    }

    @Test
    void actualizar_retornaElDTODelServicio() {
        MetodoPagoResponseDTO esperado = responseDTO();
        esperado.setLabel("Visa Actualizada");
        when(metodoPagoService.actualizar(any(), eq(1L), any())).thenReturn(esperado);

        ResponseEntity<MetodoPagoResponseDTO> res =
                controller.actualizar("user@test.com", 1L, requestValido());

        assertEquals("Visa Actualizada", res.getBody().getLabel());
    }

    @Test
    void actualizar_serviceLanzaExcepcion_propaga() {
        when(metodoPagoService.actualizar(any(), eq(99L), any()))
                .thenThrow(new RuntimeException("no encontrado"));

        assertThrows(RuntimeException.class,
                () -> controller.actualizar("user@test.com", 99L, requestValido()));
    }
}