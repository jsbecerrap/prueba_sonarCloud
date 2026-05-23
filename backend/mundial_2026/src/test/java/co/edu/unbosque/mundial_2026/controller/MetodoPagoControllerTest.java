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
    private static final String USER_CORREO = "user@test.com";

    /** 
     * @return MetodoPagoRequestDTO
     */
    private MetodoPagoRequestDTO requestValido() {
        MetodoPagoRequestDTO dto = new MetodoPagoRequestDTO();
        dto.setType("CARD");
        dto.setLabel("Visa Principal");
        dto.setDetails("4242-4242");
        return dto;
    }

    /** 
     * @return MetodoPagoResponseDTO
     */
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
        when(metodoPagoService.listarPorCorreo(USER_CORREO))
                .thenReturn(List.of(responseDTO()));

        ResponseEntity<List<MetodoPagoResponseDTO>> res = controller.listar(USER_CORREO);

        assertEquals(200, res.getStatusCode().value());
        assertEquals(1, res.getBody().size());
        verify(metodoPagoService).listarPorCorreo(USER_CORREO);
    }

    @Test
    void listar_listaVacia_retorna200() {
        when(metodoPagoService.listarPorCorreo(USER_CORREO)).thenReturn(List.of());

        ResponseEntity<List<MetodoPagoResponseDTO>> res = controller.listar(USER_CORREO);

        assertEquals(200, res.getStatusCode().value());
        assertTrue(res.getBody().isEmpty());
    }

    @Test
    void listar_serviceLanzaExcepcion_propaga() {
        when(metodoPagoService.listarPorCorreo(any())).thenThrow(new RuntimeException("error"));

        assertThrows(RuntimeException.class, () -> controller.listar(USER_CORREO));
    }

   

    @Test
    void agregar_valido_retorna200() {
      when(metodoPagoService.agregar(any(), any())).thenReturn(responseDTO());

        ResponseEntity<MetodoPagoResponseDTO> res = controller.agregar(USER_CORREO, requestValido());

        assertEquals(200, res.getStatusCode().value());
        assertNotNull(res.getBody());
        assertEquals("CARD", res.getBody().getType());
        verify(metodoPagoService).agregar(eq(USER_CORREO), any());
    }

    @Test
    void agregar_serviceRetornaNull_retorna400() {
        when(metodoPagoService.agregar(any(), any())).thenReturn(null);

        ResponseEntity<MetodoPagoResponseDTO> res = controller.agregar(USER_CORREO, requestValido());

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

        ResponseEntity<MetodoPagoResponseDTO> res = controller.agregar(USER_CORREO, dto);

        assertEquals(200, res.getStatusCode().value());
        assertEquals("PSE", res.getBody().getType());
    }

    @Test
    void agregar_tipoCASH_retorna200() {
        MetodoPagoRequestDTO dto = requestValido();
        dto.setType("CASH");
        when(metodoPagoService.agregar(any(), any())).thenReturn(responseDTO());

        ResponseEntity<MetodoPagoResponseDTO> res = controller.agregar(USER_CORREO, dto);

        assertEquals(200, res.getStatusCode().value());
    }

    @Test
    void agregar_tipoTRANSFER_retorna200() {
        MetodoPagoRequestDTO dto = requestValido();
        dto.setType("TRANSFER");
        when(metodoPagoService.agregar(any(), any())).thenReturn(responseDTO());

        ResponseEntity<MetodoPagoResponseDTO> res = controller.agregar(USER_CORREO, dto);

        assertEquals(200, res.getStatusCode().value());
    }

    @Test
    void agregar_serviceLanzaExcepcion_propaga() {
        when(metodoPagoService.agregar(any(), any())).thenThrow(new RuntimeException("error"));

        assertThrows(RuntimeException.class,
                () -> controller.agregar(USER_CORREO, requestValido()));
    }

   

    @Test
    void setDefault_retorna204() {
        doNothing().when(metodoPagoService).setDefaultPorCorreo(USER_CORREO, 1L);

        ResponseEntity<Void> res = controller.setDefault(USER_CORREO, 1L);

        assertEquals(204, res.getStatusCode().value());
        assertNull(res.getBody());
        verify(metodoPagoService).setDefaultPorCorreo(USER_CORREO, 1L);
    }

    @Test
    void setDefault_serviceLanzaExcepcion_propaga() {
        doThrow(new RuntimeException("no encontrado"))
                .when(metodoPagoService).setDefaultPorCorreo(any(), eq(99L));

        assertThrows(RuntimeException.class,
                () -> controller.setDefault(USER_CORREO, 99L));
    }



    @Test
    void eliminar_retorna204() {
        doNothing().when(metodoPagoService).eliminar(USER_CORREO, 1L);

        ResponseEntity<Void> res = controller.eliminar(USER_CORREO, 1L);

        assertEquals(204, res.getStatusCode().value());
        assertNull(res.getBody());
        verify(metodoPagoService).eliminar(USER_CORREO, 1L);
    }

    @Test
    void eliminar_serviceLanzaExcepcion_propaga() {
        doThrow(new RuntimeException("no encontrado"))
                .when(metodoPagoService).eliminar(any(), eq(99L));

        assertThrows(RuntimeException.class,
                () -> controller.eliminar(USER_CORREO, 99L));
    }

   

    @Test
    void actualizar_valido_retorna200() {
       when(metodoPagoService.actualizar(any(), eq(1L), any())).thenReturn(responseDTO());

        ResponseEntity<MetodoPagoResponseDTO> res =
                controller.actualizar(USER_CORREO, 1L, requestValido());

        assertEquals(200, res.getStatusCode().value());
        assertNotNull(res.getBody());
        verify(metodoPagoService).actualizar(eq(USER_CORREO), eq(1L), any());
    }

    @Test
    void actualizar_retornaElDTODelServicio() {
        MetodoPagoResponseDTO esperado = responseDTO();
        esperado.setLabel("Visa Actualizada");
        when(metodoPagoService.actualizar(any(), eq(1L), any())).thenReturn(esperado);

        ResponseEntity<MetodoPagoResponseDTO> res =
                controller.actualizar(USER_CORREO, 1L, requestValido());

        assertEquals("Visa Actualizada", res.getBody().getLabel());
    }

    @Test
    void actualizar_serviceLanzaExcepcion_propaga() {
        when(metodoPagoService.actualizar(any(), eq(99L), any()))
                .thenThrow(new RuntimeException("no encontrado"));

        assertThrows(RuntimeException.class,
                () -> controller.actualizar(USER_CORREO, 99L, requestValido()));
    }
}