package co.edu.unbosque.mundial_2026.integration;

import co.edu.unbosque.mundial_2026.dto.request.ActivarLoteRequestDTO;
import co.edu.unbosque.mundial_2026.dto.request.ProductoActualizarRequestDTO;
import co.edu.unbosque.mundial_2026.dto.request.ProductoRequestDTO;
import co.edu.unbosque.mundial_2026.dto.response.ProductoListadoDTO;
import co.edu.unbosque.mundial_2026.dto.response.ProductoResponseDTO;
import co.edu.unbosque.mundial_2026.service.ProductoService;
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

class ProductoIT extends BaseIntegrationTest {

    private static final String BASE_URL = "/api/productos";
    private static final String URL_ID = "/api/productos/1";
    private static final String URL_REACTIVAR = "/api/productos/1/reactivar";
    private static final String URL_ADMIN_TODOS = "/api/productos/admin/todos";
    private static final String URL_LISTADO = "/api/productos/listado";
    private static final String URL_ACTIVAR_LOTE = "/api/productos/activar-lote";
    private static final String NOMBRE_VALIDO = "Camiseta Argentina";

    @MockitoBean
    private ProductoService productoService;

    private ProductoRequestDTO requestValido() {
    ProductoRequestDTO.VarianteRequestDTO variante = new ProductoRequestDTO.VarianteRequestDTO();
    variante.setEspecificacion("Talla M");
    variante.setStock(10);

    ProductoRequestDTO dto = new ProductoRequestDTO();
    dto.setNombre(NOMBRE_VALIDO);
    dto.setPrecio(50.0);
    dto.setCategoriaId(1L);
    dto.setVariantes(List.of(variante));
    return dto;
}

    @Test
    void crear_conRolAdmin_retorna201() throws Exception {
        when(productoService.crear(any())).thenReturn(new ProductoResponseDTO());

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
        ProductoRequestDTO dto = new ProductoRequestDTO();
        dto.setNombre("");
        dto.setPrecio(50.0);
        dto.setCategoriaId(1L);

        mockMvc.perform(post(BASE_URL)
                        .header("Authorization", "Bearer " + tokenAdmin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void crear_precioNulo_retorna400() throws Exception {
        ProductoRequestDTO dto = new ProductoRequestDTO();
        dto.setNombre(NOMBRE_VALIDO);
        dto.setCategoriaId(1L);

        mockMvc.perform(post(BASE_URL)
                        .header("Authorization", "Bearer " + tokenAdmin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void crear_categoriaIdNula_retorna400() throws Exception {
        ProductoRequestDTO dto = new ProductoRequestDTO();
        dto.setNombre(NOMBRE_VALIDO);
        dto.setPrecio(50.0);

        mockMvc.perform(post(BASE_URL)
                        .header("Authorization", "Bearer " + tokenAdmin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void actualizar_conRolAdmin_retorna200() throws Exception {
        when(productoService.actualizar(eq(1L), any())).thenReturn(new ProductoResponseDTO());

        mockMvc.perform(put(URL_ID)
                        .header("Authorization", "Bearer " + tokenAdmin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ProductoActualizarRequestDTO())))
                .andExpect(status().isOk());
    }

    @Test
    void actualizar_conRolUser_retorna403() throws Exception {
        mockMvc.perform(put(URL_ID)
                        .header("Authorization", "Bearer " + tokenUsuario())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ProductoActualizarRequestDTO())))
                .andExpect(status().isForbidden());
    }

    @Test
    void actualizar_sinToken_retorna401() throws Exception {
        mockMvc.perform(put(URL_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ProductoActualizarRequestDTO())))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void eliminar_conRolAdmin_retorna204() throws Exception {
        doNothing().when(productoService).eliminar(1L);

        mockMvc.perform(delete(URL_ID)
                        .header("Authorization", "Bearer " + tokenAdmin()))
                .andExpect(status().isNoContent());
    }

    @Test
    void eliminar_conRolUser_retorna403() throws Exception {
        mockMvc.perform(delete(URL_ID)
                        .header("Authorization", "Bearer " + tokenUsuario()))
                .andExpect(status().isForbidden());
    }

    @Test
    void eliminar_sinToken_retorna401() throws Exception {
        mockMvc.perform(delete(URL_ID))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void reactivar_conRolAdmin_retorna204() throws Exception {
        doNothing().when(productoService).reactivar(1L);

        mockMvc.perform(patch(URL_REACTIVAR)
                        .header("Authorization", "Bearer " + tokenAdmin()))
                .andExpect(status().isNoContent());
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
    void listarTodosAdmin_conRolAdmin_retorna200() throws Exception {
        when(productoService.listarTodos(false)).thenReturn(List.of());

        mockMvc.perform(get(URL_ADMIN_TODOS)
                        .header("Authorization", "Bearer " + tokenAdmin()))
                .andExpect(status().isOk());
    }

    @Test
    void listarTodosAdmin_conRolUser_retorna403() throws Exception {
        mockMvc.perform(get(URL_ADMIN_TODOS)
                        .header("Authorization", "Bearer " + tokenUsuario()))
                .andExpect(status().isForbidden());
    }

    @Test
    void listarTodosAdmin_sinToken_retorna401() throws Exception {
        mockMvc.perform(get(URL_ADMIN_TODOS))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void listar_sinFiltro_retorna200() throws Exception {
        when(productoService.listarTodos()).thenReturn(List.of(new ProductoResponseDTO()));

        mockMvc.perform(get(BASE_URL))
                .andExpect(status().isOk());
    }

    @Test
    void listar_conCategoria_retorna200() throws Exception {
        when(productoService.listarPorCategoria(1L)).thenReturn(List.of());

        mockMvc.perform(get(BASE_URL + "?categoriaId=1"))
                .andExpect(status().isOk());
    }

    @Test
    void obtenerPorId_sinToken_retorna200() throws Exception {
        when(productoService.obtenerPorId(1L)).thenReturn(new ProductoResponseDTO());

        mockMvc.perform(get(URL_ID))
                .andExpect(status().isOk());
    }

    @Test
    void listarLiviano_retorna200() throws Exception {
        when(productoService.listarTodosLiviano()).thenReturn(List.of(new ProductoListadoDTO()));

        mockMvc.perform(get(URL_LISTADO))
                .andExpect(status().isOk());
    }

    @Test
    void activarLote_conRolAdmin_retorna204() throws Exception {
        ActivarLoteRequestDTO dto = new ActivarLoteRequestDTO();
        dto.setIds(List.of(1L, 2L));
        doNothing().when(productoService).activarLote(any());

        mockMvc.perform(patch(URL_ACTIVAR_LOTE)
                        .header("Authorization", "Bearer " + tokenAdmin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNoContent());
    }

    @Test
    void activarLote_conRolUser_retorna403() throws Exception {
        mockMvc.perform(patch(URL_ACTIVAR_LOTE)
                        .header("Authorization", "Bearer " + tokenUsuario())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void activarLote_sinToken_retorna401() throws Exception {
        mockMvc.perform(patch(URL_ACTIVAR_LOTE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isUnauthorized());
    }
}