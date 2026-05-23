package co.edu.unbosque.mundial_2026.integration;

import co.edu.unbosque.mundial_2026.dto.request.CategoriaRequestDTO;
import co.edu.unbosque.mundial_2026.dto.response.CategoriaResponseDTO;
import co.edu.unbosque.mundial_2026.dto.response.DesactivarCategoriaResponseDTO;
import co.edu.unbosque.mundial_2026.dto.response.ProductoResponseDTO;
import co.edu.unbosque.mundial_2026.dto.response.ReactivarCategoriaResponseDTO;
import co.edu.unbosque.mundial_2026.service.CategoriaService;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class CategoriaIT extends BaseIntegrationTest {

    private static final String BASE_URL = "/api/categorias";
    private static final String URL_TODAS = "/api/categorias/todas";
    private static final String URL_ID = "/api/categorias/1";
    private static final String URL_DESACTIVAR = "/api/categorias/1/desactivar";
    private static final String URL_REACTIVAR = "/api/categorias/1/reactivar";
    private static final String URL_PRODUCTOS = "/api/categorias/1/productos";
    private static final String NOMBRE_VALIDO = "Souvenirs";

    @MockitoBean
    private CategoriaService categoriaService;

    private CategoriaRequestDTO requestValido() {
        CategoriaRequestDTO dto = new CategoriaRequestDTO();
        dto.setNombre(NOMBRE_VALIDO);
        return dto;
    }

    @Test
    void crear_conRolAdmin_retorna201() throws Exception {
        when(categoriaService.crear(any())).thenReturn(new CategoriaResponseDTO());

        mockMvc.perform(post(BASE_URL)
                        .header("Authorization", "Bearer " + tokenAdmin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestValido())))
                .andExpect(status().isCreated());
    }

    @Test
    void crear_conRolUser_retorna403() throws Exception {
        mockMvc.perform(post(BASE_URL)
                        .header("Authorization", "Bearer " + tokenUsuario())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestValido())))
                .andExpect(status().isForbidden());
    }

    @Test
    void crear_sinToken_retorna401() throws Exception {
        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestValido())))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void crear_nombreVacio_retorna400() throws Exception {
        CategoriaRequestDTO dto = new CategoriaRequestDTO();
        dto.setNombre("");

        mockMvc.perform(post(BASE_URL)
                        .header("Authorization", "Bearer " + tokenAdmin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void crear_nombreDemasiadoCorto_retorna400() throws Exception {
        CategoriaRequestDTO dto = new CategoriaRequestDTO();
        dto.setNombre("A");

        mockMvc.perform(post(BASE_URL)
                        .header("Authorization", "Bearer " + tokenAdmin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void crear_nombreConCaracteresInvalidos_retorna400() throws Exception {
        CategoriaRequestDTO dto = new CategoriaRequestDTO();
        dto.setNombre("Categoria@#$");

        mockMvc.perform(post(BASE_URL)
                        .header("Authorization", "Bearer " + tokenAdmin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void listar_sinToken_retorna200() throws Exception {
        when(categoriaService.listar()).thenReturn(List.of(new CategoriaResponseDTO()));

        mockMvc.perform(get(BASE_URL))
                .andExpect(status().isOk());
    }

    @Test
    void listar_conToken_retorna200() throws Exception {
        when(categoriaService.listar()).thenReturn(List.of());

        mockMvc.perform(get(BASE_URL)
                        .header("Authorization", "Bearer " + tokenUsuario()))
                .andExpect(status().isOk());
    }

    @Test
    void actualizar_conRolAdmin_retorna200() throws Exception {
        when(categoriaService.actualizar(eq(1L), any())).thenReturn(new CategoriaResponseDTO());

        mockMvc.perform(put(URL_ID)
                        .header("Authorization", "Bearer " + tokenAdmin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestValido())))
                .andExpect(status().isOk());
    }

    @Test
    void actualizar_conRolUser_retorna403() throws Exception {
        mockMvc.perform(put(URL_ID)
                        .header("Authorization", "Bearer " + tokenUsuario())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestValido())))
                .andExpect(status().isForbidden());
    }

    @Test
    void actualizar_sinToken_retorna401() throws Exception {
        mockMvc.perform(put(URL_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestValido())))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void actualizar_nombreVacio_retorna400() throws Exception {
        CategoriaRequestDTO dto = new CategoriaRequestDTO();
        dto.setNombre("");

        mockMvc.perform(put(URL_ID)
                        .header("Authorization", "Bearer " + tokenAdmin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void desactivar_conRolAdmin_retorna200() throws Exception {
        when(categoriaService.desactivar(1L)).thenReturn(new DesactivarCategoriaResponseDTO());

        mockMvc.perform(patch(URL_DESACTIVAR)
                        .header("Authorization", "Bearer " + tokenAdmin()))
                .andExpect(status().isOk());
    }

    @Test
    void desactivar_conRolUser_retorna403() throws Exception {
        mockMvc.perform(patch(URL_DESACTIVAR)
                        .header("Authorization", "Bearer " + tokenUsuario()))
                .andExpect(status().isForbidden());
    }

    @Test
    void desactivar_sinToken_retorna401() throws Exception {
        mockMvc.perform(patch(URL_DESACTIVAR))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void reactivar_conRolAdmin_retorna200() throws Exception {
        when(categoriaService.reactivar(1L)).thenReturn(new ReactivarCategoriaResponseDTO());

        mockMvc.perform(patch(URL_REACTIVAR)
                        .header("Authorization", "Bearer " + tokenAdmin()))
                .andExpect(status().isOk());
    }

    @Test
    void reactivar_conRolUser_retorna403() throws Exception {
        mockMvc.perform(patch(URL_REACTIVAR)
                        .header("Authorization", "Bearer " + tokenUsuario()))
                .andExpect(status().isForbidden());
    }

    @Test
    void reactivar_sinToken_retorna401() throws Exception {
        mockMvc.perform(patch(URL_REACTIVAR))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void listarTodas_conRolAdmin_retorna200() throws Exception {
        when(categoriaService.listarTodas()).thenReturn(List.of());

        mockMvc.perform(get(URL_TODAS)
                        .header("Authorization", "Bearer " + tokenAdmin()))
                .andExpect(status().isOk());
    }

    @Test
    void listarTodas_conRolUser_retorna403() throws Exception {
        mockMvc.perform(get(URL_TODAS)
                        .header("Authorization", "Bearer " + tokenUsuario()))
                .andExpect(status().isForbidden());
    }

    @Test
    void listarTodas_sinToken_retorna401() throws Exception {
        mockMvc.perform(get(URL_TODAS))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void obtenerProductos_conRolAdmin_retorna200() throws Exception {
        when(categoriaService.obtenerProductosPorCategoria(1L)).thenReturn(List.of(new ProductoResponseDTO()));

        mockMvc.perform(get(URL_PRODUCTOS)
                        .header("Authorization", "Bearer " + tokenAdmin()))
                .andExpect(status().isOk());
    }

    @Test
    void obtenerProductos_conRolUser_retorna403() throws Exception {
        mockMvc.perform(get(URL_PRODUCTOS)
                        .header("Authorization", "Bearer " + tokenUsuario()))
                .andExpect(status().isForbidden());
    }

    @Test
    void obtenerProductos_sinToken_retorna401() throws Exception {
        mockMvc.perform(get(URL_PRODUCTOS))
                .andExpect(status().isUnauthorized());
    }
}