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

/**
 * Pruebas de integración para los endpoints relacionados con métodos de pago
 * 
 * Se validan operaciones de listado, creación, actualización, eliminación
 * y configuración de método por defecto verificando autenticación, validaciones
 * y respuestas HTTP esperadas
 */
class MetodoPagoIT extends BaseIntegrationTest {

    /**
     * Servicio mockeado utilizado para simular operaciones de métodos de pago
     */
    @MockitoBean
    private MetodoPagoService metodoPagoService;

    /**
     * Nombre del header utilizado para enviar el token JWT
     */
    private static final String AUTH_HEADER = "Authorization";

    /**
     * Prefijo utilizado en el token Bearer
     */
    private static final String BEARER_PREFIX = "Bearer ";

    /**
     * URL base de los endpoints de métodos de pago
     */
    private static final String PAYMENTS_URL = "/payments";

    /**
     * Tipo de método de pago tarjeta
     */
    private static final String CARD = "CARD";

    /**
     * Nombre utilizado para pruebas de tarjeta débito
     */
    private static final String VISA_DEBITO = "Visa Débito";

    /**
     * Tipo de método de pago transferencia
     */
    private static final String TRANSFER = "TRANSFER";

    /**
     * Nombre utilizado para pruebas de transferencia bancaria
     */
    private static final String BANCOLOMBIA = "Bancolombia";

    /**
     * URL utilizada para operaciones sobre un método de pago específico
     */
    private static final String PAYMENTS_ID_URL = "/payments/1";

    /**
     * Verifica que un usuario autenticado pueda listar sus métodos de pago
     * 
     * @throws Exception si ocurre un error durante la petición
     */
    @Test
    void listar_conToken_retorna200() throws Exception {
        MetodoPagoResponseDTO dto = new MetodoPagoResponseDTO();
        dto.setId("1");
        dto.setType(CARD);
        dto.setLabel(VISA_DEBITO);

        when(metodoPagoService.listarPorCorreo(USER_EMAIL)).thenReturn(List.of(dto));

        mockMvc.perform(get(PAYMENTS_URL)
                        .header(AUTH_HEADER, BEARER_PREFIX + tokenUsuario()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].type").value(CARD))
                .andExpect(jsonPath("$[0].label").value(VISA_DEBITO));
    }

    /**
     * Verifica que listar métodos de pago sin autenticación retorne 401
     * 
     * @throws Exception si ocurre un error durante la petición
     */
    @Test
    void listar_sinToken_retorna401() throws Exception {
        mockMvc.perform(get(PAYMENTS_URL))
                .andExpect(status().isUnauthorized());
    }

    /**
     * Verifica que un token inválido retorne respuesta 401
     * 
     * @throws Exception si ocurre un error durante la petición
     */
    @Test
    void listar_conTokenInvalido_retorna401() throws Exception {
        mockMvc.perform(get(PAYMENTS_URL)
                        .header(AUTH_HEADER, "Bearer token.invalido.xyz"))
                .andExpect(status().isUnauthorized());
    }

    /**
     * Verifica la creación correcta de un método de pago válido
     * 
     * @throws Exception si ocurre un error durante la petición
     */
    @Test
    void agregar_conDatosValidos_retorna200() throws Exception {
        MetodoPagoRequestDTO request = new MetodoPagoRequestDTO();
        request.setType(CARD);
        request.setLabel(VISA_DEBITO);

        MetodoPagoResponseDTO response = new MetodoPagoResponseDTO();
        response.setId("1");
        response.setType(CARD);
        response.setLabel(VISA_DEBITO);

        when(metodoPagoService.agregar(eq(USER_EMAIL), any())).thenReturn(response);

        mockMvc.perform(post(PAYMENTS_URL)
                        .header(AUTH_HEADER, BEARER_PREFIX + tokenUsuario())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.type").value(CARD))
                .andExpect(jsonPath("$.label").value(VISA_DEBITO));
    }

    /**
     * Verifica que agregar un método de pago sin token retorne 401
     * 
     * @throws Exception si ocurre un error durante la petición
     */
    @Test
    void agregar_sinToken_retorna401() throws Exception {
        MetodoPagoRequestDTO request = new MetodoPagoRequestDTO();
        request.setType(CARD);
        request.setLabel(VISA_DEBITO);

        mockMvc.perform(post(PAYMENTS_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    /**
     * Verifica que un tipo de método de pago inválido retorne 400
     * 
     * @throws Exception si ocurre un error durante la petición
     */
    @Test
    void agregar_tipoInvalido_retorna400() throws Exception {
        MetodoPagoRequestDTO request = new MetodoPagoRequestDTO();
        request.setType("BITCOIN");
        request.setLabel(VISA_DEBITO);

        mockMvc.perform(post(PAYMENTS_URL)
                        .header(AUTH_HEADER, BEARER_PREFIX + tokenUsuario())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    /**
     * Verifica que un label vacío retorne error de validación
     * 
     * @throws Exception si ocurre un error durante la petición
     */
    @Test
    void agregar_labelVacio_retorna400() throws Exception {
        MetodoPagoRequestDTO request = new MetodoPagoRequestDTO();
        request.setType(CARD);
        request.setLabel("");

        mockMvc.perform(post(PAYMENTS_URL)
                        .header(AUTH_HEADER, BEARER_PREFIX + tokenUsuario())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    /**
     * Verifica el comportamiento cuando el servicio retorna null
     * 
     * @throws Exception si ocurre un error durante la petición
     */
    @Test
    void agregar_servicioRetornaNull_retorna400() throws Exception {
        MetodoPagoRequestDTO request = new MetodoPagoRequestDTO();
        request.setType("PSE");
        request.setLabel("Mi PSE");

        when(metodoPagoService.agregar(eq(USER_EMAIL), any())).thenReturn(null);

        mockMvc.perform(post(PAYMENTS_URL)
                        .header(AUTH_HEADER, BEARER_PREFIX + tokenUsuario())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    /**
     * Verifica que un usuario autenticado pueda definir un método por defecto
     * 
     * @throws Exception si ocurre un error durante la petición
     */
    @Test
    void setDefault_conToken_retorna204() throws Exception {
        doNothing().when(metodoPagoService).setDefaultPorCorreo(USER_EMAIL, 1L);

        mockMvc.perform(patch("/payments/1/default")
                        .header(AUTH_HEADER, BEARER_PREFIX + tokenUsuario()))
                .andExpect(status().isNoContent());
    }

    /**
     * Verifica que configurar método por defecto sin token retorne 401
     * 
     * @throws Exception si ocurre un error durante la petición
     */
    @Test
    void setDefault_sinToken_retorna401() throws Exception {
        mockMvc.perform(patch("/payments/1/default"))
                .andExpect(status().isUnauthorized());
    }

    /**
     * Verifica la eliminación correcta de un método de pago
     * 
     * @throws Exception si ocurre un error durante la petición
     */
    @Test
    void eliminar_conToken_retorna204() throws Exception {
        doNothing().when(metodoPagoService).eliminar(USER_EMAIL, 1L);

        mockMvc.perform(delete(PAYMENTS_ID_URL)
                        .header(AUTH_HEADER, BEARER_PREFIX + tokenUsuario()))
                .andExpect(status().isNoContent());
    }

    /**
     * Verifica que eliminar sin autenticación retorne 401
     * 
     * @throws Exception si ocurre un error durante la petición
     */
    @Test
    void eliminar_sinToken_retorna401() throws Exception {
        mockMvc.perform(delete(PAYMENTS_ID_URL))
                .andExpect(status().isUnauthorized());
    }

    /**
     * Verifica la actualización correcta de un método de pago
     * 
     * @throws Exception si ocurre un error durante la petición
     */
    @Test
    void actualizar_conDatosValidos_retorna200() throws Exception {
        MetodoPagoRequestDTO request = new MetodoPagoRequestDTO();
        request.setType(TRANSFER);
        request.setLabel(BANCOLOMBIA);

        MetodoPagoResponseDTO response = new MetodoPagoResponseDTO();
        response.setId("1");
        response.setType(TRANSFER);
        response.setLabel(BANCOLOMBIA);

        when(metodoPagoService.actualizar(eq(USER_EMAIL), eq(1L), any())).thenReturn(response);

        mockMvc.perform(patch(PAYMENTS_ID_URL)
                        .header(AUTH_HEADER, BEARER_PREFIX + tokenUsuario())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.type").value(TRANSFER))
                .andExpect(jsonPath("$.label").value(BANCOLOMBIA));
    }

    /**
     * Verifica que actualizar un método sin token retorne 401
     * 
     * @throws Exception si ocurre un error durante la petición
     */
    @Test
    void actualizar_sinToken_retorna401() throws Exception {
        MetodoPagoRequestDTO request = new MetodoPagoRequestDTO();
        request.setType(TRANSFER);
        request.setLabel(BANCOLOMBIA);

        mockMvc.perform(patch(PAYMENTS_ID_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    /**
     * Verifica que un label demasiado corto retorne error de validación
     * 
     * @throws Exception si ocurre un error durante la petición
     */
    @Test
    void actualizar_labelDemasiadoCorto_retorna400() throws Exception {
        MetodoPagoRequestDTO request = new MetodoPagoRequestDTO();
        request.setType(CARD);
        request.setLabel("AB");

        mockMvc.perform(patch(PAYMENTS_ID_URL)
                        .header(AUTH_HEADER, BEARER_PREFIX + tokenUsuario())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}