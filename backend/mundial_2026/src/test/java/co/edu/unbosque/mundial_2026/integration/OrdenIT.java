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

class OrdenIT extends BaseIntegrationTest {

    private static final String URL_AGREGAR = "/api/ordenes/carrito/agregar";
    private static final String URL_CARRITO = "/api/ordenes/carrito";
    private static final String URL_ITEM = "/api/ordenes/carrito/item/1";
    private static final String URL_CONFIRMAR = "/api/ordenes/carrito/confirmar";
    private static final String URL_HISTORIAL = "/api/ordenes/historial";
    private static final String URL_CANCELAR = "/api/ordenes/carrito";
    private static final String URL_HISTORIAL_LIVIANO = "/api/ordenes/historial/liviano";

    @MockitoBean
    private OrdenService ordenService;

    private AgregarItemDTO itemValido() {
        AgregarItemDTO dto = new AgregarItemDTO();
        dto.setProductoId(1L);
        dto.setCantidad(2);
        dto.setVarianteId(1L);
        return dto;
    }

    private ConfirmarOrdenDTO confirmarValido() {
        ConfirmarOrdenDTO dto = new ConfirmarOrdenDTO();
        dto.setMetodoPagoId(1L);
        return dto;
    }

    @Test
    void agregar_conDatosValidos_retorna200() throws Exception {
        when(ordenService.agregarItem(eq(USER_EMAIL), any())).thenReturn(new OrdenResponseDTO());

        mockMvc.perform(post(URL_AGREGAR)
                        .header("Authorization", "Bearer " + tokenUsuario())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemValido())))
                .andExpect(status().isOk());
    }

    @Test
    void agregar_sinToken_retorna401() throws Exception {
        mockMvc.perform(post(URL_AGREGAR)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemValido())))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void agregar_productoIdNulo_retorna400() throws Exception {
        AgregarItemDTO dto = new AgregarItemDTO();
        dto.setCantidad(1);
        dto.setVarianteId(1L);

        mockMvc.perform(post(URL_AGREGAR)
                        .header("Authorization", "Bearer " + tokenUsuario())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void agregar_cantidadCero_retorna400() throws Exception {
        AgregarItemDTO dto = new AgregarItemDTO();
        dto.setProductoId(1L);
        dto.setCantidad(0);
        dto.setVarianteId(1L);

        mockMvc.perform(post(URL_AGREGAR)
                        .header("Authorization", "Bearer " + tokenUsuario())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void agregar_varianteIdNula_retorna400() throws Exception {
        AgregarItemDTO dto = new AgregarItemDTO();
        dto.setProductoId(1L);
        dto.setCantidad(1);

        mockMvc.perform(post(URL_AGREGAR)
                        .header("Authorization", "Bearer " + tokenUsuario())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void carrito_conToken_retorna200() throws Exception {
        when(ordenService.obtenerCarrito(USER_EMAIL)).thenReturn(new OrdenResponseDTO());

        mockMvc.perform(get(URL_CARRITO)
                        .header("Authorization", "Bearer " + tokenUsuario()))
                .andExpect(status().isOk());
    }

    @Test
    void carrito_sinToken_retorna401() throws Exception {
        mockMvc.perform(get(URL_CARRITO))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void eliminarItem_conToken_retorna200() throws Exception {
        when(ordenService.eliminarItem(eq(USER_EMAIL), eq(1L))).thenReturn(new OrdenResponseDTO());

        mockMvc.perform(delete(URL_ITEM)
                        .header("Authorization", "Bearer " + tokenUsuario()))
                .andExpect(status().isOk());
    }

    @Test
    void eliminarItem_sinToken_retorna401() throws Exception {
        mockMvc.perform(delete(URL_ITEM))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void confirmar_conDatosValidos_retorna200() throws Exception {
        when(ordenService.confirmarOrden(eq(USER_EMAIL), any())).thenReturn(new OrdenResponseDTO());

        mockMvc.perform(post(URL_CONFIRMAR)
                        .header("Authorization", "Bearer " + tokenUsuario())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(confirmarValido())))
                .andExpect(status().isOk());
    }

    @Test
    void confirmar_metodoPagoNulo_retorna400() throws Exception {
        ConfirmarOrdenDTO dto = new ConfirmarOrdenDTO();

        mockMvc.perform(post(URL_CONFIRMAR)
                        .header("Authorization", "Bearer " + tokenUsuario())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void confirmar_sinToken_retorna401() throws Exception {
        mockMvc.perform(post(URL_CONFIRMAR)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(confirmarValido())))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void historial_conToken_retorna200() throws Exception {
        when(ordenService.historial(USER_EMAIL)).thenReturn(List.of(new OrdenResponseDTO()));

        mockMvc.perform(get(URL_HISTORIAL)
                        .header("Authorization", "Bearer " + tokenUsuario()))
                .andExpect(status().isOk());
    }

    @Test
    void historial_sinToken_retorna401() throws Exception {
        mockMvc.perform(get(URL_HISTORIAL))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void cancelar_conToken_retorna200() throws Exception {
        when(ordenService.cancelarOrden(USER_EMAIL)).thenReturn(new OrdenResponseDTO());

        mockMvc.perform(delete(URL_CANCELAR)
                        .header("Authorization", "Bearer " + tokenUsuario()))
                .andExpect(status().isOk());
    }

    @Test
    void cancelar_sinToken_retorna401() throws Exception {
        mockMvc.perform(delete(URL_CANCELAR))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void historialLiviano_conToken_retorna200() throws Exception {
        when(ordenService.historialLiviano(USER_EMAIL)).thenReturn(List.of(new OrdenHistorialDTO()));

        mockMvc.perform(get(URL_HISTORIAL_LIVIANO)
                        .header("Authorization", "Bearer " + tokenUsuario()))
                .andExpect(status().isOk());
    }

    @Test
    void historialLiviano_sinToken_retorna401() throws Exception {
        mockMvc.perform(get(URL_HISTORIAL_LIVIANO))
                .andExpect(status().isUnauthorized());
    }
}