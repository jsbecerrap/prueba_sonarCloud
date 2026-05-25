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

    private static final String AUTH_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    @MockitoBean
    private CategoriaService categoriaService;

    /**
     * Construye una solicitud válida para crear o actualizar categorías.
     *
     * @return DTO con datos válidos de categoría.
     */
    private CategoriaRequestDTO requestValido() {
        CategoriaRequestDTO dto = new CategoriaRequestDTO();
        dto.setNombre(NOMBRE_VALIDO);
        return dto;
    }

    /**
     * Verifica que un administrador pueda crear categorías correctamente.
     *
     * @throws Exception si ocurre un error en la petición mock.
     */
    @Test
    void crear_conRolAdmin_retorna201() throws Exception {
        when(categoriaService.crear(any())).thenReturn(new CategoriaResponseDTO());

        mockMvc.perform(post(BASE_URL)
                        .header(AUTH_HEADER, BEARER_PREFIX + tokenAdmin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestValido())))
                .andExpect(status().isCreated());
    }

    /**
     * Verifica que un usuario sin permisos no pueda crear categorías.
     *
     * @throws Exception si ocurre un error en la ejecución de la prueba.
     */
    @Test
    void crear_conRolUser_retorna403() throws Exception {
        mockMvc.perform(post(BASE_URL)
                        .header(AUTH_HEADER, BEARER_PREFIX + tokenUsuario())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestValido())))
                .andExpect(status().isForbidden());
    }

    /**
     * Verifica que el endpoint retorne 401 cuando no se envía token.
     *
     * @throws Exception si ocurre un error en la petición mock.
     */
    @Test
    void crear_sinToken_retorna401() throws Exception {
        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestValido())))
                .andExpect(status().isUnauthorized());
    }

    /**
     * Verifica que el endpoint retorne 400 cuando el nombre está vacío.
     *
     * @throws Exception si ocurre un error en la ejecución de la prueba.
     */
    @Test
    void crear_nombreVacio_retorna400() throws Exception {
        CategoriaRequestDTO dto = new CategoriaRequestDTO();
        dto.setNombre("");

        mockMvc.perform(post(BASE_URL)
                        .header(AUTH_HEADER, BEARER_PREFIX + tokenAdmin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    /**
     * Verifica que el endpoint retorne 400 cuando el nombre es demasiado corto.
     *
     * @throws Exception si ocurre un error en la ejecución de la prueba.
     */
    @Test
    void crear_nombreDemasiadoCorto_retorna400() throws Exception {
        CategoriaRequestDTO dto = new CategoriaRequestDTO();
        dto.setNombre("A");

        mockMvc.perform(post(BASE_URL)
                        .header(AUTH_HEADER, BEARER_PREFIX + tokenAdmin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    /**
     * Verifica que el endpoint retorne 400 cuando el nombre contiene
     * caracteres inválidos.
     *
     * @throws Exception si ocurre un error en la petición mock.
     */
    @Test
    void crear_nombreConCaracteresInvalidos_retorna400() throws Exception {
        CategoriaRequestDTO dto = new CategoriaRequestDTO();
        dto.setNombre("Categoria@#$");

        mockMvc.perform(post(BASE_URL)
                        .header(AUTH_HEADER, BEARER_PREFIX + tokenAdmin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    /**
     * Verifica que cualquier usuario pueda listar categorías sin autenticación.
     *
     * @throws Exception si ocurre un error en la ejecución de la prueba.
     */
    @Test
    void listar_sinToken_retorna200() throws Exception {
        when(categoriaService.listar()).thenReturn(List.of(new CategoriaResponseDTO()));

        mockMvc.perform(get(BASE_URL))
                .andExpect(status().isOk());
    }

    /**
     * Verifica que un usuario autenticado pueda listar categorías.
     *
     * @throws Exception si ocurre un error en la petición mock.
     */
    @Test
    void listar_conToken_retorna200() throws Exception {
        when(categoriaService.listar()).thenReturn(List.of());

        mockMvc.perform(get(BASE_URL)
                        .header(AUTH_HEADER, BEARER_PREFIX + tokenUsuario()))
                .andExpect(status().isOk());
    }

    /**
     * Verifica que un administrador pueda actualizar categorías correctamente.
     *
     * @throws Exception si ocurre un error en la ejecución de la prueba.
     */
    @Test
    void actualizar_conRolAdmin_retorna200() throws Exception {
        when(categoriaService.actualizar(eq(1L), any())).thenReturn(new CategoriaResponseDTO());

        mockMvc.perform(put(URL_ID)
                        .header(AUTH_HEADER, BEARER_PREFIX + tokenAdmin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestValido())))
                .andExpect(status().isOk());
    }

    /**
     * Verifica que un usuario sin permisos no pueda actualizar categorías.
     *
     * @throws Exception si ocurre un error en la petición mock.
     */
    @Test
    void actualizar_conRolUser_retorna403() throws Exception {
        mockMvc.perform(put(URL_ID)
                        .header(AUTH_HEADER, BEARER_PREFIX + tokenUsuario())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestValido())))
                .andExpect(status().isForbidden());
    }

    /**
     * Verifica que el endpoint retorne 401 cuando se intenta actualizar
     * una categoría sin autenticación.
     *
     * @throws Exception si ocurre un error en la ejecución de la prueba.
     */
    @Test
    void actualizar_sinToken_retorna401() throws Exception {
        mockMvc.perform(put(URL_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestValido())))
                .andExpect(status().isUnauthorized());
    }

    /**
     * Verifica que el endpoint retorne 400 cuando el nombre enviado
     * para actualización está vacío.
     *
     * @throws Exception si ocurre un error en la petición mock.
     */
    @Test
    void actualizar_nombreVacio_retorna400() throws Exception {
        CategoriaRequestDTO dto = new CategoriaRequestDTO();
        dto.setNombre("");

        mockMvc.perform(put(URL_ID)
                        .header(AUTH_HEADER, BEARER_PREFIX + tokenAdmin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    /**
     * Verifica que un administrador pueda desactivar categorías.
     *
     * @throws Exception si ocurre un error en la ejecución de la prueba.
     */
    @Test
    void desactivar_conRolAdmin_retorna200() throws Exception {
        when(categoriaService.desactivar(1L)).thenReturn(new DesactivarCategoriaResponseDTO());

        mockMvc.perform(patch(URL_DESACTIVAR)
                        .header(AUTH_HEADER, BEARER_PREFIX + tokenAdmin()))
                .andExpect(status().isOk());
    }

    /**
     * Verifica que un usuario sin permisos no pueda desactivar categorías.
     *
     * @throws Exception si ocurre un error en la petición mock.
     */
    @Test
    void desactivar_conRolUser_retorna403() throws Exception {
        mockMvc.perform(patch(URL_DESACTIVAR)
                        .header(AUTH_HEADER, BEARER_PREFIX + tokenUsuario()))
                .andExpect(status().isForbidden());
    }

    /**
     * Verifica que el endpoint retorne 401 cuando se intenta desactivar
     * una categoría sin autenticación.
     *
     * @throws Exception si ocurre un error en la ejecución de la prueba.
     */
    @Test
    void desactivar_sinToken_retorna401() throws Exception {
        mockMvc.perform(patch(URL_DESACTIVAR))
                .andExpect(status().isUnauthorized());
    }

    /**
     * Verifica que un administrador pueda reactivar categorías.
     *
     * @throws Exception si ocurre un error en la petición mock.
     */
    @Test
    void reactivar_conRolAdmin_retorna200() throws Exception {
        when(categoriaService.reactivar(1L)).thenReturn(new ReactivarCategoriaResponseDTO());

        mockMvc.perform(patch(URL_REACTIVAR)
                        .header(AUTH_HEADER, BEARER_PREFIX + tokenAdmin()))
                .andExpect(status().isOk());
    }

    /**
     * Verifica que un usuario sin permisos no pueda reactivar categorías.
     *
     * @throws Exception si ocurre un error en la ejecución de la prueba.
     */
    @Test
    void reactivar_conRolUser_retorna403() throws Exception {
        mockMvc.perform(patch(URL_REACTIVAR)
                        .header(AUTH_HEADER, BEARER_PREFIX + tokenUsuario()))
                .andExpect(status().isForbidden());
    }

    /**
     * Verifica que el endpoint retorne 401 cuando se intenta reactivar
     * una categoría sin autenticación.
     *
     * @throws Exception si ocurre un error en la petición mock.
     */
    @Test
    void reactivar_sinToken_retorna401() throws Exception {
        mockMvc.perform(patch(URL_REACTIVAR))
                .andExpect(status().isUnauthorized());
    }

    /**
     * Verifica que un administrador pueda listar todas las categorías.
     *
     * @throws Exception si ocurre un error en la ejecución de la prueba.
     */
    @Test
    void listarTodas_conRolAdmin_retorna200() throws Exception {
        when(categoriaService.listarTodas()).thenReturn(List.of());

        mockMvc.perform(get(URL_TODAS)
                        .header(AUTH_HEADER, BEARER_PREFIX + tokenAdmin()))
                .andExpect(status().isOk());
    }

    /**
     * Verifica que un usuario sin permisos no pueda listar todas las categorías.
     *
     * @throws Exception si ocurre un error en la petición mock.
     */
    @Test
    void listarTodas_conRolUser_retorna403() throws Exception {
        mockMvc.perform(get(URL_TODAS)
                        .header(AUTH_HEADER, BEARER_PREFIX + tokenUsuario()))
                .andExpect(status().isForbidden());
    }

    /**
     * Verifica que el endpoint retorne 401 cuando se intenta listar
     * todas las categorías sin autenticación.
     *
     * @throws Exception si ocurre un error en la ejecución de la prueba.
     */
    @Test
    void listarTodas_sinToken_retorna401() throws Exception {
        mockMvc.perform(get(URL_TODAS))
                .andExpect(status().isUnauthorized());
    }

    /**
     * Verifica que un administrador pueda consultar los productos
     * asociados a una categoría.
     *
     * @throws Exception si ocurre un error en la petición mock.
     */
    @Test
    void obtenerProductos_conRolAdmin_retorna200() throws Exception {
        when(categoriaService.obtenerProductosPorCategoria(1L))
                .thenReturn(List.of(new ProductoResponseDTO()));

        mockMvc.perform(get(URL_PRODUCTOS)
                        .header(AUTH_HEADER, BEARER_PREFIX + tokenAdmin()))
                .andExpect(status().isOk());
    }

    /**
     * Verifica que un usuario sin permisos no pueda consultar
     * productos por categoría.
     *
     * @throws Exception si ocurre un error en la ejecución de la prueba.
     */
    @Test
    void obtenerProductos_conRolUser_retorna403() throws Exception {
        mockMvc.perform(get(URL_PRODUCTOS)
                        .header(AUTH_HEADER, BEARER_PREFIX + tokenUsuario()))
                .andExpect(status().isForbidden());
    }

    /**
     * Verifica que el endpoint retorne 401 cuando se intenta consultar
     * productos de una categoría sin autenticación.
     *
     * @throws Exception si ocurre un error en la petición mock.
     */
    @Test
    void obtenerProductos_sinToken_retorna401() throws Exception {
        mockMvc.perform(get(URL_PRODUCTOS))
                .andExpect(status().isUnauthorized());
    }
}