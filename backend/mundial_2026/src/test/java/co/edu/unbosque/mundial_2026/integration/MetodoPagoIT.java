package co.edu.unbosque.mundial_2026.integration;

import co.edu.unbosque.mundial_2026.dto.request.MetodoPagoRequestDTO;
import co.edu.unbosque.mundial_2026.dto.response.MetodoPagoResponseDTO;
import co.edu.unbosque.mundial_2026.service.MetodoPagoService;
import org.junit.jupiter.api.Test;

import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class MetodoPagoIT extends BaseIntegrationTest {

    @MockitoBean
private MetodoPagoService metodoPagoService;


    @Test
    void listar_conToken_retorna200() throws Exception {
        MetodoPagoResponseDTO dto = new MetodoPagoResponseDTO();
        dto.setId("1");
        dto.setType("CARD");
        dto.setLabel("Visa Débito");

        when(metodoPagoService.listarPorCorreo(USER_EMAIL)).thenReturn(List.of(dto));

        mockMvc.perform(get("/payments")
                        .header("Authorization", "Bearer " + tokenUsuario()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].type").value("CARD"))
                .andExpect(jsonPath("$[0].label").value("Visa Débito"));
    }

    @Test
    void listar_sinToken_retorna401() throws Exception {
        mockMvc.perform(get("/payments"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void listar_conTokenInvalido_retorna401() throws Exception {
        mockMvc.perform(get("/payments")
                        .header("Authorization", "Bearer token.invalido.xyz"))
                .andExpect(status().isUnauthorized());
    }

    

    @Test
    void agregar_conDatosValidos_retorna200() throws Exception {
        MetodoPagoRequestDTO request = new MetodoPagoRequestDTO();
        request.setType("CARD");
        request.setLabel("Visa Débito");

        MetodoPagoResponseDTO response = new MetodoPagoResponseDTO();
        response.setId("1");
        response.setType("CARD");
        response.setLabel("Visa Débito");

        when(metodoPagoService.agregar(eq(USER_EMAIL), any())).thenReturn(response);

        mockMvc.perform(post("/payments")
                        .header("Authorization", "Bearer " + tokenUsuario())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.type").value("CARD"))
                .andExpect(jsonPath("$.label").value("Visa Débito"));
    }

    @Test
    void agregar_sinToken_retorna401() throws Exception {
        MetodoPagoRequestDTO request = new MetodoPagoRequestDTO();
        request.setType("CARD");
        request.setLabel("Visa Débito");

        mockMvc.perform(post("/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void agregar_tipoInvalido_retorna400() throws Exception {
        MetodoPagoRequestDTO request = new MetodoPagoRequestDTO();
        request.setType("BITCOIN");
        request.setLabel("Visa Débito");

        mockMvc.perform(post("/payments")
                        .header("Authorization", "Bearer " + tokenUsuario())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void agregar_labelVacio_retorna400() throws Exception {
        MetodoPagoRequestDTO request = new MetodoPagoRequestDTO();
        request.setType("CARD");
        request.setLabel("");

        mockMvc.perform(post("/payments")
                        .header("Authorization", "Bearer " + tokenUsuario())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void agregar_servicioRetornaNull_retorna400() throws Exception {
        MetodoPagoRequestDTO request = new MetodoPagoRequestDTO();
        request.setType("PSE");
        request.setLabel("Mi PSE");

        when(metodoPagoService.agregar(eq(USER_EMAIL), any())).thenReturn(null);

        mockMvc.perform(post("/payments")
                        .header("Authorization", "Bearer " + tokenUsuario())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    

    @Test
    void setDefault_conToken_retorna204() throws Exception {
        doNothing().when(metodoPagoService).setDefaultPorCorreo(USER_EMAIL, 1L);

        mockMvc.perform(patch("/payments/1/default")
                        .header("Authorization", "Bearer " + tokenUsuario()))
                .andExpect(status().isNoContent());
    }

    @Test
    void setDefault_sinToken_retorna401() throws Exception {
        mockMvc.perform(patch("/payments/1/default"))
                .andExpect(status().isUnauthorized());
    }

    

    @Test
    void eliminar_conToken_retorna204() throws Exception {
        doNothing().when(metodoPagoService).eliminar(USER_EMAIL, 1L);

        mockMvc.perform(delete("/payments/1")
                        .header("Authorization", "Bearer " + tokenUsuario()))
                .andExpect(status().isNoContent());
    }

    @Test
    void eliminar_sinToken_retorna401() throws Exception {
        mockMvc.perform(delete("/payments/1"))
                .andExpect(status().isUnauthorized());
    }

    

    @Test
    void actualizar_conDatosValidos_retorna200() throws Exception {
        MetodoPagoRequestDTO request = new MetodoPagoRequestDTO();
        request.setType("TRANSFER");
        request.setLabel("Bancolombia");

        MetodoPagoResponseDTO response = new MetodoPagoResponseDTO();
        response.setId("1");
        response.setType("TRANSFER");
        response.setLabel("Bancolombia");

        when(metodoPagoService.actualizar(eq(USER_EMAIL), eq(1L), any())).thenReturn(response);

        mockMvc.perform(patch("/payments/1")
                        .header("Authorization", "Bearer " + tokenUsuario())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.type").value("TRANSFER"))
                .andExpect(jsonPath("$.label").value("Bancolombia"));
    }

    @Test
    void actualizar_sinToken_retorna401() throws Exception {
        MetodoPagoRequestDTO request = new MetodoPagoRequestDTO();
        request.setType("TRANSFER");
        request.setLabel("Bancolombia");

        mockMvc.perform(patch("/payments/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void actualizar_labelDemasiadoCorto_retorna400() throws Exception {
        MetodoPagoRequestDTO request = new MetodoPagoRequestDTO();
        request.setType("CARD");
        request.setLabel("AB");

        mockMvc.perform(patch("/payments/1")
                        .header("Authorization", "Bearer " + tokenUsuario())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}