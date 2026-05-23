package co.edu.unbosque.mundial_2026.integration;

import co.edu.unbosque.mundial_2026.dto.CuposZonaDTO;
import co.edu.unbosque.mundial_2026.dto.PartidoCapacidadDTO;
import co.edu.unbosque.mundial_2026.dto.request.EntradaRequestDTO;
import co.edu.unbosque.mundial_2026.dto.request.TransferenciaRequestDTO;
import co.edu.unbosque.mundial_2026.dto.response.EntradaResponseDTO;
import co.edu.unbosque.mundial_2026.service.EntradaService;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class EntradaIT extends BaseIntegrationTest {

    private static final String URL_CUPOS = "/api/entradas/cupos-zona/1";
    private static final String URL_DETALLE = "/api/entradas/1";
    private static final String URL_RESERVAR = "/api/entradas/reservar";
    private static final String URL_PAGAR = "/api/entradas/pagar/1";
    private static final String URL_CANCELAR = "/api/entradas/cancelar/1";
    private static final String URL_TRANSFERIR = "/api/entradas/transferir/1";
    private static final String URL_REEMBOLSAR = "/api/entradas/reembolsar/1";
    private static final String URL_USUARIO = "/api/entradas/usuario";
    private static final String URL_PARTIDOS = "/api/entradas/partidos";
    private static final String PAYMENT_REF = "REF-001";

    @MockitoBean
    private EntradaService entradaService;

    private EntradaRequestDTO requestValido() {
        EntradaRequestDTO dto = new EntradaRequestDTO();
        dto.setPartidoId(1L);
        dto.setCantidad(2);
        dto.setCategoria("VIP");
        dto.setSector("NORTE");
        return dto;
    }

    @Test
    void cuposPorZona_conToken_retorna200() throws Exception {
        when(entradaService.obtenerCuposPorZona(1L)).thenReturn(List.of(new CuposZonaDTO()));

        mockMvc.perform(get(URL_CUPOS)
                        .header("Authorization", "Bearer " + tokenUsuario()))
                .andExpect(status().isOk());
    }

    @Test
    void cuposPorZona_sinToken_retorna401() throws Exception {
        mockMvc.perform(get(URL_CUPOS))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void obtener_conToken_retorna200() throws Exception {
        when(entradaService.obtenerEntrada(1L)).thenReturn(new EntradaResponseDTO());

        mockMvc.perform(get(URL_DETALLE)
                        .header("Authorization", "Bearer " + tokenUsuario()))
                .andExpect(status().isOk());
    }

    @Test
    void obtener_sinToken_retorna401() throws Exception {
        mockMvc.perform(get(URL_DETALLE))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void reservar_conToken_retorna200() throws Exception {
        when(entradaService.reservarEntrada(eq(USER_EMAIL), any())).thenReturn(new EntradaResponseDTO());

        mockMvc.perform(post(URL_RESERVAR)
                        .header("Authorization", "Bearer " + tokenUsuario())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestValido())))
                .andExpect(status().isOk());
    }

    @Test
    void reservar_sinToken_retorna401() throws Exception {
        mockMvc.perform(post(URL_RESERVAR)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestValido())))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void pagar_conToken_retorna200() throws Exception {
        when(entradaService.confirmarPago(1L, PAYMENT_REF)).thenReturn(new EntradaResponseDTO());

        mockMvc.perform(patch(URL_PAGAR)
                        .header("Authorization", "Bearer " + tokenUsuario())
                        .param("paymentRef", PAYMENT_REF))
                .andExpect(status().isOk());
    }

    @Test
    void pagar_sinToken_retorna401() throws Exception {
        mockMvc.perform(patch(URL_PAGAR)
                        .param("paymentRef", PAYMENT_REF))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void pagar_sinPaymentRef_retorna400() throws Exception {
        mockMvc.perform(patch(URL_PAGAR)
                        .header("Authorization", "Bearer " + tokenUsuario()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void cancelar_conToken_retorna200() throws Exception {
        when(entradaService.cancelarReserva(USER_EMAIL, 1L)).thenReturn(new EntradaResponseDTO());

        mockMvc.perform(patch(URL_CANCELAR)
                        .header("Authorization", "Bearer " + tokenUsuario()))
                .andExpect(status().isOk());
    }

    @Test
    void cancelar_sinToken_retorna401() throws Exception {
        mockMvc.perform(patch(URL_CANCELAR))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void transferir_conToken_retorna200() throws Exception {
        TransferenciaRequestDTO dto = new TransferenciaRequestDTO();
        when(entradaService.transferirEntrada(eq(1L), any(), eq(USER_EMAIL)))
                .thenReturn(new EntradaResponseDTO());

        mockMvc.perform(patch(URL_TRANSFERIR)
                        .header("Authorization", "Bearer " + tokenUsuario())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    void transferir_sinToken_retorna401() throws Exception {
        mockMvc.perform(patch(URL_TRANSFERIR)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void reembolsar_conToken_retorna200() throws Exception {
        when(entradaService.reembolsarEntrada(USER_EMAIL, 1L)).thenReturn(new EntradaResponseDTO());

        mockMvc.perform(patch(URL_REEMBOLSAR)
                        .header("Authorization", "Bearer " + tokenUsuario()))
                .andExpect(status().isOk());
    }

    @Test
    void reembolsar_sinToken_retorna401() throws Exception {
        mockMvc.perform(patch(URL_REEMBOLSAR))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void listarUsuario_conToken_retorna200() throws Exception {
        when(entradaService.listarEntradasUsuario(USER_EMAIL)).thenReturn(List.of(new EntradaResponseDTO()));

        mockMvc.perform(get(URL_USUARIO)
                        .header("Authorization", "Bearer " + tokenUsuario()))
                .andExpect(status().isOk());
    }

    @Test
    void listarUsuario_sinToken_retorna401() throws Exception {
        mockMvc.perform(get(URL_USUARIO))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void listarPartidos_conToken_retorna200() throws Exception {
        when(entradaService.listarPartidosConCapacidad()).thenReturn(List.of(new PartidoCapacidadDTO()));

        mockMvc.perform(get(URL_PARTIDOS)
                        .header("Authorization", "Bearer " + tokenUsuario()))
                .andExpect(status().isOk());
    }

    @Test
    void listarPartidos_sinToken_retorna401() throws Exception {
        mockMvc.perform(get(URL_PARTIDOS))
                .andExpect(status().isUnauthorized());
    }
}