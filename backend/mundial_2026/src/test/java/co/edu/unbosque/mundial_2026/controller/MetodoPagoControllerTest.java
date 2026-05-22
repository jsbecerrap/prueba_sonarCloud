package co.edu.unbosque.mundial_2026.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import co.edu.unbosque.mundial_2026.dto.request.MetodoPagoRequestDTO;
import co.edu.unbosque.mundial_2026.dto.response.MetodoPagoResponseDTO;
import co.edu.unbosque.mundial_2026.service.MetodoPagoService;

@WebMvcTest(MetodoPagoController.class)
@ActiveProfiles("test")
class MetodoPagoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MetodoPagoService metodoPagoService;

    private final ObjectMapper mapper = new ObjectMapper();

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
    void listar_conJwt_retorna200() throws Exception {
        when(metodoPagoService.listarPorCorreo(anyString()))
                .thenReturn(List.of(responseDTO()));

        mockMvc.perform(get("/payments")
                .with(jwt().jwt(j -> j.subject("user@test.com"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void listar_listaVacia_retorna200() throws Exception {
        when(metodoPagoService.listarPorCorreo(anyString())).thenReturn(List.of());

        mockMvc.perform(get("/payments")
                .with(jwt().jwt(j -> j.subject("user@test.com"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void listar_sinAutenticacion_retorna401() throws Exception {
        mockMvc.perform(get("/payments"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void agregar_valido_retorna200() throws Exception {
        when(metodoPagoService.agregar(anyString(), any())).thenReturn(responseDTO());

        mockMvc.perform(post("/payments")
                .with(jwt().jwt(j -> j.subject("user@test.com")))
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(requestValido())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.type").value("CARD"));
    }

    @Test
    void agregar_serviceRetornaNull_retorna400() throws Exception {
        when(metodoPagoService.agregar(anyString(), any())).thenReturn(null);

        mockMvc.perform(post("/payments")
                .with(jwt().jwt(j -> j.subject("user@test.com")))
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(requestValido())))
                .andExpect(status().isBadRequest());
    }

    @Test
    void agregar_tipoInvalido_retorna400() throws Exception {
        MetodoPagoRequestDTO dto = requestValido();
        dto.setType("BITCOIN");

        mockMvc.perform(post("/payments")
                .with(jwt().jwt(j -> j.subject("user@test.com")))
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void agregar_tipoVacio_retorna400() throws Exception {
        MetodoPagoRequestDTO dto = requestValido();
        dto.setType("");

        mockMvc.perform(post("/payments")
                .with(jwt().jwt(j -> j.subject("user@test.com")))
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void agregar_tipoNull_retorna400() throws Exception {
        MetodoPagoRequestDTO dto = requestValido();
        dto.setType(null);

        mockMvc.perform(post("/payments")
                .with(jwt().jwt(j -> j.subject("user@test.com")))
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void agregar_labelVacio_retorna400() throws Exception {
        MetodoPagoRequestDTO dto = requestValido();
        dto.setLabel("");

        mockMvc.perform(post("/payments")
                .with(jwt().jwt(j -> j.subject("user@test.com")))
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void agregar_labelMuyCorto_retorna400() throws Exception {
        MetodoPagoRequestDTO dto = requestValido();
        dto.setLabel("Ab");

        mockMvc.perform(post("/payments")
                .with(jwt().jwt(j -> j.subject("user@test.com")))
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void agregar_labelConCaracteresInvalidos_retorna400() throws Exception {
        MetodoPagoRequestDTO dto = requestValido();
        dto.setLabel("Visa@#$%");

        mockMvc.perform(post("/payments")
                .with(jwt().jwt(j -> j.subject("user@test.com")))
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void agregar_tipoPSE_retorna200() throws Exception {
        MetodoPagoRequestDTO dto = requestValido();
        dto.setType("PSE");
        MetodoPagoResponseDTO resp = responseDTO();
        resp.setType("PSE");
        when(metodoPagoService.agregar(anyString(), any())).thenReturn(resp);

        mockMvc.perform(post("/payments")
                .with(jwt().jwt(j -> j.subject("user@test.com")))
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    void agregar_tipoCASH_retorna200() throws Exception {
        MetodoPagoRequestDTO dto = requestValido();
        dto.setType("CASH");
        when(metodoPagoService.agregar(anyString(), any())).thenReturn(responseDTO());

        mockMvc.perform(post("/payments")
                .with(jwt().jwt(j -> j.subject("user@test.com")))
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    void agregar_tipoTRANSFER_retorna200() throws Exception {
        MetodoPagoRequestDTO dto = requestValido();
        dto.setType("TRANSFER");
        when(metodoPagoService.agregar(anyString(), any())).thenReturn(responseDTO());

        mockMvc.perform(post("/payments")
                .with(jwt().jwt(j -> j.subject("user@test.com")))
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    void agregar_sinAutenticacion_retorna401() throws Exception {
        mockMvc.perform(post("/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(requestValido())))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void setDefault_retorna204() throws Exception {
        doNothing().when(metodoPagoService).setDefaultPorCorreo(anyString(), eq(1L));

        mockMvc.perform(patch("/payments/1/default")
                .with(jwt().jwt(j -> j.subject("user@test.com"))))
                .andExpect(status().isNoContent());
    }

    @Test
    void setDefault_sinAutenticacion_retorna401() throws Exception {
        mockMvc.perform(patch("/payments/1/default"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void setDefault_serviceLanzaExcepcion_retorna500() throws Exception {
        doThrow(new RuntimeException("No encontrado"))
                .when(metodoPagoService).setDefaultPorCorreo(anyString(), eq(99L));

        mockMvc.perform(patch("/payments/99/default")
                .with(jwt().jwt(j -> j.subject("user@test.com"))))
                .andExpect(status().is5xxServerError());
    }

    @Test
    void eliminar_retorna204() throws Exception {
        doNothing().when(metodoPagoService).eliminar(anyString(), eq(1L));

        mockMvc.perform(delete("/payments/1")
                .with(jwt().jwt(j -> j.subject("user@test.com"))))
                .andExpect(status().isNoContent());
    }

    @Test
    void eliminar_sinAutenticacion_retorna401() throws Exception {
        mockMvc.perform(delete("/payments/1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void eliminar_serviceLanzaExcepcion_retorna500() throws Exception {
        doThrow(new RuntimeException("No encontrado"))
                .when(metodoPagoService).eliminar(anyString(), eq(99L));

        mockMvc.perform(delete("/payments/99")
                .with(jwt().jwt(j -> j.subject("user@test.com"))))
                .andExpect(status().is5xxServerError());
    }

    @Test
    void actualizar_valido_retorna200() throws Exception {
        when(metodoPagoService.actualizar(anyString(), eq(1L), any())).thenReturn(responseDTO());

        mockMvc.perform(patch("/payments/1")
                .with(jwt().jwt(j -> j.subject("user@test.com")))
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(requestValido())))
                .andExpect(status().isOk());
    }

    @Test
    void actualizar_bodyInvalido_retorna400() throws Exception {
        MetodoPagoRequestDTO dto = new MetodoPagoRequestDTO();

        mockMvc.perform(patch("/payments/1")
                .with(jwt().jwt(j -> j.subject("user@test.com")))
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void actualizar_tipoInvalido_retorna400() throws Exception {
        MetodoPagoRequestDTO dto = requestValido();
        dto.setType("CRIPTO");

        mockMvc.perform(patch("/payments/1")
                .with(jwt().jwt(j -> j.subject("user@test.com")))
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void actualizar_sinAutenticacion_retorna401() throws Exception {
        mockMvc.perform(patch("/payments/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(requestValido())))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void actualizar_detallesInvalidos_retorna400() throws Exception {
        MetodoPagoRequestDTO dto = requestValido();
        dto.setDetails("ref@#$%!");

        mockMvc.perform(patch("/payments/1")
                .with(jwt().jwt(j -> j.subject("user@test.com")))
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }
}