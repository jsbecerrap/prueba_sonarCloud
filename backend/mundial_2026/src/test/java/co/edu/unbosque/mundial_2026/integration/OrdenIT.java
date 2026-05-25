package co.edu.unbosque.mundial_2026.integration;

import co.edu.unbosque.mundial_2026.dto.request.AgregarItemDTO;
import co.edu.unbosque.mundial_2026.dto.request.ConfirmarOrdenDTO;
import co.edu.unbosque.mundial_2026.dto.response.OrdenHistorialDTO;
import co.edu.unbosque.mundial_2026.dto.response.OrdenResponseDTO;
import co.edu.unbosque.mundial_2026.service.OrdenService;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
/**
 * Pruebas de integración para los endpoints relacionados con órdenes y carrito
 *
 * Se valida el comportamiento de creación de órdenes, carrito,
 * confirmación de compra e historial según autenticación y datos enviados
 */
class OrdenIT extends BaseIntegrationTest {

    /**
     * URL para agregar productos al carrito
     */
    private static final String URL_AGREGAR = "/api/ordenes/carrito/agregar";

    /**
     * URL para consultar el carrito actual
     */
    private static final String URL_CARRITO = "/api/ordenes/carrito";

    /**
     * URL para eliminar un item del carrito
     */
    private static final String URL_ITEM = "/api/ordenes/carrito/item/1";

    /**
     * URL para confirmar la orden
     */
    private static final String URL_CONFIRMAR = "/api/ordenes/carrito/confirmar";

    /**
     * URL para consultar historial de órdenes
     */
    private static final String URL_HISTORIAL = "/api/ordenes/historial";

    /**
     * URL para cancelar la orden actual
     */
    private static final String URL_CANCELAR = "/api/ordenes/carrito";

    /**
     * URL para consultar historial liviano
     */
    private static final String URL_HISTORIAL_LIVIANO = "/api/ordenes/historial/liviano";

    /**
     * Nombre del header de autenticación
     */
    private static final String AUTH_HEADER = "Authorization";

    /**
     * Prefijo usado para tokens JWT
     */
    private static final String BEARER_PREFIX = "Bearer ";

    /**
     * Servicio de órdenes simulado para pruebas
     */
    @MockitoBean
    private OrdenService ordenService;

    /**
     * Construye un DTO válido para agregar productos al carrito
     *
     * @return DTO con información válida
     */
    private AgregarItemDTO itemValido() {
        AgregarItemDTO dto = new AgregarItemDTO();
        dto.setProductoId(1L);
        dto.setCantidad(2);
        dto.setVarianteId(1L);
        return dto;
    }

    /**
     * Construye un DTO válido para confirmar una orden
     *
     * @return DTO con método de pago válido
     */
    private ConfirmarOrdenDTO confirmarValido() {
        ConfirmarOrdenDTO dto = new ConfirmarOrdenDTO();
        dto.setMetodoPagoId(1L);
        return dto;
    }

    /**
     * Verifica que un usuario autenticado pueda agregar productos al carrito
     */
    @Test
    void agregar_conDatosValidos_retorna200() throws Exception {
        when(ordenService.agregarItem(eq(USER_EMAIL), any())).thenReturn(new OrdenResponseDTO());

        mockMvc.perform(post(URL_AGREGAR)
                        .header(AUTH_HEADER, BEARER_PREFIX + tokenUsuario())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemValido())))
                .andExpect(status().isOk());
    }

    /**
     * Verifica que agregar productos sin autenticación retorne 401
     */
    @Test
    void agregar_sinToken_retorna401() throws Exception {
        mockMvc.perform(post(URL_AGREGAR)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemValido())))
                .andExpect(status().isUnauthorized());
    }

    /**
     * Verifica validación cuando el producto es nulo
     */
    @Test
    void agregar_productoIdNulo_retorna400() throws Exception {
        AgregarItemDTO dto = new AgregarItemDTO();
        dto.setCantidad(1);
        dto.setVarianteId(1L);

        mockMvc.perform(post(URL_AGREGAR)
                        .header(AUTH_HEADER, BEARER_PREFIX + tokenUsuario())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    /**
     * Verifica validación cuando la cantidad es cero
     */
    @Test
    void agregar_cantidadCero_retorna400() throws Exception {
        AgregarItemDTO dto = new AgregarItemDTO();
        dto.setProductoId(1L);
        dto.setCantidad(0);
        dto.setVarianteId(1L);

        mockMvc.perform(post(URL_AGREGAR)
                        .header(AUTH_HEADER, BEARER_PREFIX + tokenUsuario())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    /**
     * Verifica validación cuando la variante es nula
     */
    @Test
    void agregar_varianteIdNula_retorna400() throws Exception {
        AgregarItemDTO dto = new AgregarItemDTO();
        dto.setProductoId(1L);
        dto.setCantidad(1);

        mockMvc.perform(post(URL_AGREGAR)
                        .header(AUTH_HEADER, BEARER_PREFIX + tokenUsuario())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    /**
     * Verifica consulta del carrito con autenticación válida
     */
    @Test
    void carrito_conToken_retorna200() throws Exception {
        when(ordenService.obtenerCarrito(USER_EMAIL)).thenReturn(new OrdenResponseDTO());

        mockMvc.perform(get(URL_CARRITO)
                        .header(AUTH_HEADER, BEARER_PREFIX + tokenUsuario()))
                .andExpect(status().isOk());
    }

    /**
     * Verifica que consultar carrito sin token retorne 401
     */
    @Test
    void carrito_sinToken_retorna401() throws Exception {
        mockMvc.perform(get(URL_CARRITO))
                .andExpect(status().isUnauthorized());
    }

    /**
     * Verifica eliminación de item del carrito
     */
    @Test
    void eliminarItem_conToken_retorna200() throws Exception {
        when(ordenService.eliminarItem(eq(USER_EMAIL), eq(1L))).thenReturn(new OrdenResponseDTO());

        mockMvc.perform(delete(URL_ITEM)
                        .header(AUTH_HEADER, BEARER_PREFIX + tokenUsuario()))
                .andExpect(status().isOk());
    }

    /**
     * Verifica que eliminar items sin token retorne 401
     */
    @Test
    void eliminarItem_sinToken_retorna401() throws Exception {
        mockMvc.perform(delete(URL_ITEM))
                .andExpect(status().isUnauthorized());
    }

    /**
     * Verifica confirmación correcta de una orden
     */
    @Test
    void confirmar_conDatosValidos_retorna200() throws Exception {
        when(ordenService.confirmarOrden(eq(USER_EMAIL), any())).thenReturn(new OrdenResponseDTO());

        mockMvc.perform(post(URL_CONFIRMAR)
                        .header(AUTH_HEADER, BEARER_PREFIX + tokenUsuario())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(confirmarValido())))
                .andExpect(status().isOk());
    }

    /**
     * Verifica validación cuando el método de pago es nulo
     */
    @Test
    void confirmar_metodoPagoNulo_retorna400() throws Exception {
        ConfirmarOrdenDTO dto = new ConfirmarOrdenDTO();

        mockMvc.perform(post(URL_CONFIRMAR)
                        .header(AUTH_HEADER, BEARER_PREFIX + tokenUsuario())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    /**
     * Verifica que confirmar órdenes sin token retorne 401
     */
    @Test
    void confirmar_sinToken_retorna401() throws Exception {
        mockMvc.perform(post(URL_CONFIRMAR)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(confirmarValido())))
                .andExpect(status().isUnauthorized());
    }

    /**
     * Verifica consulta del historial de órdenes
     */
    @Test
    void historial_conToken_retorna200() throws Exception {
        when(ordenService.historial(USER_EMAIL)).thenReturn(List.of(new OrdenResponseDTO()));

        mockMvc.perform(get(URL_HISTORIAL)
                        .header(AUTH_HEADER, BEARER_PREFIX + tokenUsuario()))
                .andExpect(status().isOk());
    }

    /**
     * Verifica que consultar historial sin token retorne 401
     */
    @Test
    void historial_sinToken_retorna401() throws Exception {
        mockMvc.perform(get(URL_HISTORIAL))
                .andExpect(status().isUnauthorized());
    }

    /**
     * Verifica cancelación de la orden actual
     */
    @Test
    void cancelar_conToken_retorna200() throws Exception {
        when(ordenService.cancelarOrden(USER_EMAIL)).thenReturn(new OrdenResponseDTO());

        mockMvc.perform(delete(URL_CANCELAR)
                        .header(AUTH_HEADER, BEARER_PREFIX + tokenUsuario()))
                .andExpect(status().isOk());
    }

    /**
     * Verifica que cancelar órdenes sin token retorne 401
     */
    @Test
    void cancelar_sinToken_retorna401() throws Exception {
        mockMvc.perform(delete(URL_CANCELAR))
                .andExpect(status().isUnauthorized());
    }

    /**
     * Verifica consulta del historial liviano
     */
    @Test
    void historialLiviano_conToken_retorna200() throws Exception {
        when(ordenService.historialLiviano(USER_EMAIL)).thenReturn(List.of(new OrdenHistorialDTO()));

        mockMvc.perform(get(URL_HISTORIAL_LIVIANO)
                        .header(AUTH_HEADER, BEARER_PREFIX + tokenUsuario()))
                .andExpect(status().isOk());
    }

    /**
     * Verifica que consultar historial liviano sin token retorne 401
     */
    @Test
    void historialLiviano_sinToken_retorna401() throws Exception {
        mockMvc.perform(get(URL_HISTORIAL_LIVIANO))
                .andExpect(status().isUnauthorized());
    }
}