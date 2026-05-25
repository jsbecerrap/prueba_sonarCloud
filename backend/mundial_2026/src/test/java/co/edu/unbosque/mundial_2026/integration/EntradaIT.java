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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Pruebas de integración para los endpoints de entradas.
 * <p>
 * Valida reservas, pagos, cancelaciones, transferencias,
 * reembolsos y consultas relacionadas con entradas.
 */
class EntradaIT extends BaseIntegrationTest {

    private static final String AUTH_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

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

    /**
     * Construye un request válido para reservar entradas.
     *
     * @return request válido de entrada
     */
    private EntradaRequestDTO requestValido() {
        EntradaRequestDTO dto = new EntradaRequestDTO();
        dto.setPartidoId(1L);
        dto.setCantidad(2);
        dto.setCategoria("VIP");
        dto.setSector("NORTE");
        return dto;
    }

    /**
     * Verifica la consulta de cupos por zona con autenticación.
     *
     * @throws Exception si ocurre un error en la petición
     */
    @Test
    void cuposPorZona_conToken_retorna200() throws Exception {
        when(entradaService.obtenerCuposPorZona(1L))
                .thenReturn(List.of(new CuposZonaDTO()));

        mockMvc.perform(get(URL_CUPOS)
                        .header(AUTH_HEADER, BEARER_PREFIX + tokenUsuario()))
                .andExpect(status().isOk());
    }

    /**
     * Verifica que consultar cupos sin token retorne 401.
     *
     * @throws Exception si ocurre un error en la petición
     */
    @Test
    void cuposPorZona_sinToken_retorna401() throws Exception {
        mockMvc.perform(get(URL_CUPOS))
                .andExpect(status().isUnauthorized());
    }

    /**
     * Verifica la consulta de una entrada por id.
     *
     * @throws Exception si ocurre un error en la petición
     */
    @Test
    void obtener_conToken_retorna200() throws Exception {
        when(entradaService.obtenerEntrada(1L))
                .thenReturn(new EntradaResponseDTO());

        mockMvc.perform(get(URL_DETALLE)
                        .header(AUTH_HEADER, BEARER_PREFIX + tokenUsuario()))
                .andExpect(status().isOk());
    }

    /**
     * Verifica que consultar una entrada sin token retorne 401.
     *
     * @throws Exception si ocurre un error en la petición
     */
    @Test
    void obtener_sinToken_retorna401() throws Exception {
        mockMvc.perform(get(URL_DETALLE))
                .andExpect(status().isUnauthorized());
    }

    /**
     * Verifica la reserva de entradas con datos válidos.
     *
     * @throws Exception si ocurre un error en la petición
     */
    @Test
    void reservar_conToken_retorna200() throws Exception {
        when(entradaService.reservarEntrada(eq(USER_EMAIL), any()))
                .thenReturn(new EntradaResponseDTO());

        mockMvc.perform(post(URL_RESERVAR)
                        .header(AUTH_HEADER, BEARER_PREFIX + tokenUsuario())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestValido())))
                .andExpect(status().isOk());
    }

    /**
     * Verifica que reservar entradas sin token retorne 401.
     *
     * @throws Exception si ocurre un error en la petición
     */
    @Test
    void reservar_sinToken_retorna401() throws Exception {
        mockMvc.perform(post(URL_RESERVAR)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestValido())))
                .andExpect(status().isUnauthorized());
    }

    /**
     * Verifica la confirmación de pago de una entrada.
     *
     * @throws Exception si ocurre un error en la petición
     */
    @Test
    void pagar_conToken_retorna200() throws Exception {
        when(entradaService.confirmarPago(1L, PAYMENT_REF))
                .thenReturn(new EntradaResponseDTO());

        mockMvc.perform(patch(URL_PAGAR)
                        .header(AUTH_HEADER, BEARER_PREFIX + tokenUsuario())
                        .param("paymentRef", PAYMENT_REF))
                .andExpect(status().isOk());
    }

    /**
     * Verifica que pagar sin token retorne 401.
     *
     * @throws Exception si ocurre un error en la petición
     */
    @Test
    void pagar_sinToken_retorna401() throws Exception {
        mockMvc.perform(patch(URL_PAGAR)
                        .param("paymentRef", PAYMENT_REF))
                .andExpect(status().isUnauthorized());
    }

    /**
     * Verifica que pagar sin referencia de pago retorne 400.
     *
     * @throws Exception si ocurre un error en la petición
     */
    @Test
    void pagar_sinPaymentRef_retorna400() throws Exception {
        mockMvc.perform(patch(URL_PAGAR)
                        .header(AUTH_HEADER, BEARER_PREFIX + tokenUsuario()))
                .andExpect(status().isBadRequest());
    }

    /**
     * Verifica la cancelación de una reserva.
     *
     * @throws Exception si ocurre un error en la petición
     */
    @Test
    void cancelar_conToken_retorna200() throws Exception {
        when(entradaService.cancelarReserva(USER_EMAIL, 1L))
                .thenReturn(new EntradaResponseDTO());

        mockMvc.perform(patch(URL_CANCELAR)
                        .header(AUTH_HEADER, BEARER_PREFIX + tokenUsuario()))
                .andExpect(status().isOk());
    }

    /**
     * Verifica que cancelar una reserva sin token retorne 401.
     *
     * @throws Exception si ocurre un error en la petición
     */
    @Test
    void cancelar_sinToken_retorna401() throws Exception {
        mockMvc.perform(patch(URL_CANCELAR))
                .andExpect(status().isUnauthorized());
    }

    /**
     * Verifica la transferencia de una entrada.
     *
     * @throws Exception si ocurre un error en la petición
     */
    @Test
    void transferir_conToken_retorna200() throws Exception {
        TransferenciaRequestDTO dto = new TransferenciaRequestDTO();

        when(entradaService.transferirEntrada(eq(1L), any(), eq(USER_EMAIL)))
                .thenReturn(new EntradaResponseDTO());

        mockMvc.perform(patch(URL_TRANSFERIR)
                        .header(AUTH_HEADER, BEARER_PREFIX + tokenUsuario())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    /**
     * Verifica que transferir una entrada sin token retorne 401.
     *
     * @throws Exception si ocurre un error en la petición
     */
    @Test
    void transferir_sinToken_retorna401() throws Exception {
        mockMvc.perform(patch(URL_TRANSFERIR)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isUnauthorized());
    }

    /**
     * Verifica el reembolso de una entrada.
     *
     * @throws Exception si ocurre un error en la petición
     */
    @Test
    void reembolsar_conToken_retorna200() throws Exception {
        when(entradaService.reembolsarEntrada(USER_EMAIL, 1L))
                .thenReturn(new EntradaResponseDTO());

        mockMvc.perform(patch(URL_REEMBOLSAR)
                        .header(AUTH_HEADER, BEARER_PREFIX + tokenUsuario()))
                .andExpect(status().isOk());
    }

    /**
     * Verifica que reembolsar una entrada sin token retorne 401.
     *
     * @throws Exception si ocurre un error en la petición
     */
    @Test
    void reembolsar_sinToken_retorna401() throws Exception {
        mockMvc.perform(patch(URL_REEMBOLSAR))
                .andExpect(status().isUnauthorized());
    }

    /**
     * Verifica la consulta de entradas del usuario autenticado.
     *
     * @throws Exception si ocurre un error en la petición
     */
    @Test
    void listarUsuario_conToken_retorna200() throws Exception {
        when(entradaService.listarEntradasUsuario(USER_EMAIL))
                .thenReturn(List.of(new EntradaResponseDTO()));

        mockMvc.perform(get(URL_USUARIO)
                        .header(AUTH_HEADER, BEARER_PREFIX + tokenUsuario()))
                .andExpect(status().isOk());
    }

    /**
     * Verifica que listar entradas del usuario sin token retorne 401.
     *
     * @throws Exception si ocurre un error en la petición
     */
    @Test
    void listarUsuario_sinToken_retorna401() throws Exception {
        mockMvc.perform(get(URL_USUARIO))
                .andExpect(status().isUnauthorized());
    }

    /**
     * Verifica la consulta de partidos con capacidad disponible.
     *
     * @throws Exception si ocurre un error en la petición
     */
    @Test
    void listarPartidos_conToken_retorna200() throws Exception {
        when(entradaService.listarPartidosConCapacidad())
                .thenReturn(List.of(new PartidoCapacidadDTO()));

        mockMvc.perform(get(URL_PARTIDOS)
                        .header(AUTH_HEADER, BEARER_PREFIX + tokenUsuario()))
                .andExpect(status().isOk());
    }

    /**
     * Verifica que listar partidos sin token retorne 401.
     *
     * @throws Exception si ocurre un error en la petición
     */
    @Test
    void listarPartidos_sinToken_retorna401() throws Exception {
        mockMvc.perform(get(URL_PARTIDOS))
                .andExpect(status().isUnauthorized());
    }
}