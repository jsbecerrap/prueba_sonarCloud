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

/**
 * Pruebas de integracion para los endpoints de productos
 * 
 * Valida:
 * autenticacion
 * autorizacion por roles
 * validaciones de entrada
 * y respuestas correctas de los endpoints
 */
class ProductoIT extends BaseIntegrationTest {

    /**
     * URL base de productos
     */
    private static final String BASE_URL = "/api/productos";

    /**
     * URL de consulta por identificador
     */
    private static final String URL_ID = "/api/productos/1";

    /**
     * URL para reactivar productos
     */
    private static final String URL_REACTIVAR = "/api/productos/1/reactivar";

    /**
     * URL administrativa para listar productos
     */
    private static final String URL_ADMIN_TODOS = "/api/productos/admin/todos";

    /**
     * URL de listado liviano de productos
     */
    private static final String URL_LISTADO = "/api/productos/listado";

    /**
     * URL para activar productos por lote
     */
    private static final String URL_ACTIVAR_LOTE = "/api/productos/activar-lote";

    /**
     * Nombre valido usado en pruebas
     */
    private static final String NOMBRE_VALIDO = "Camiseta Argentina";

    /**
     * Header de autorizacion
     */
    private static final String AUTH_HEADER = "Authorization";

    /**
     * Prefijo Bearer para tokens JWT
     */
    private static final String BEARER_PREFIX = "Bearer ";

    @MockitoBean
    private ProductoService productoService;

    /**
     * Construye un DTO valido para creacion de productos
     * 
     * @return DTO configurado para pruebas
     */
    private ProductoRequestDTO requestValido() {

        ProductoRequestDTO.VarianteRequestDTO variante =
                new ProductoRequestDTO.VarianteRequestDTO();

        variante.setEspecificacion("Talla M");
        variante.setStock(10);

        ProductoRequestDTO dto = new ProductoRequestDTO();
        dto.setNombre(NOMBRE_VALIDO);
        dto.setPrecio(50.0);
        dto.setCategoriaId(1L);
        dto.setVariantes(List.of(variante));

        return dto;
    }

    /**
     * Verifica creacion exitosa de productos
     * con rol administrador
     * 
     * @throws Exception error durante la prueba
     */
    @Test
    void crear_conRolAdmin_retorna201() throws Exception {

        when(productoService.crear(any()))
                .thenReturn(new ProductoResponseDTO());

        mockMvc.perform(post(BASE_URL)
                        .header(AUTH_HEADER, BEARER_PREFIX + tokenAdmin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestValido())))
                .andExpect(status().isCreated());
    }

    /**
     * Verifica acceso denegado a creacion
     * para usuarios sin permisos administrativos
     * 
     * @throws Exception error durante la prueba
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
     * Verifica acceso no autorizado
     * cuando no se envia token
     * 
     * @throws Exception error durante la prueba
     */
    @Test
    void crear_sinToken_retorna401() throws Exception {

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestValido())))
                .andExpect(status().isUnauthorized());
    }

    /**
     * Verifica validacion de nombre vacio
     * durante la creacion de productos
     * 
     * @throws Exception error durante la prueba
     */
    @Test
    void crear_nombreVacio_retorna400() throws Exception {

        ProductoRequestDTO dto = new ProductoRequestDTO();
        dto.setNombre("");
        dto.setPrecio(50.0);
        dto.setCategoriaId(1L);

        mockMvc.perform(post(BASE_URL)
                        .header(AUTH_HEADER, BEARER_PREFIX + tokenAdmin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    /**
     * Verifica validacion de precio nulo
     * durante la creacion de productos
     * 
     * @throws Exception error durante la prueba
     */
    @Test
    void crear_precioNulo_retorna400() throws Exception {

        ProductoRequestDTO dto = new ProductoRequestDTO();
        dto.setNombre(NOMBRE_VALIDO);
        dto.setCategoriaId(1L);

        mockMvc.perform(post(BASE_URL)
                        .header(AUTH_HEADER, BEARER_PREFIX + tokenAdmin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    /**
     * Verifica validacion de categoria nula
     * durante la creacion de productos
     * 
     * @throws Exception error durante la prueba
     */
    @Test
    void crear_categoriaIdNula_retorna400() throws Exception {

        ProductoRequestDTO dto = new ProductoRequestDTO();
        dto.setNombre(NOMBRE_VALIDO);
        dto.setPrecio(50.0);

        mockMvc.perform(post(BASE_URL)
                        .header(AUTH_HEADER, BEARER_PREFIX + tokenAdmin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    /**
     * Verifica actualizacion exitosa de productos
     * con rol administrador
     * 
     * @throws Exception error durante la prueba
     */
    @Test
    void actualizar_conRolAdmin_retorna200() throws Exception {

        when(productoService.actualizar(eq(1L), any()))
                .thenReturn(new ProductoResponseDTO());

        mockMvc.perform(put(URL_ID)
                        .header(AUTH_HEADER, BEARER_PREFIX + tokenAdmin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new ProductoActualizarRequestDTO())))
                .andExpect(status().isOk());
    }

    /**
     * Verifica acceso denegado a actualizacion
     * para usuarios sin permisos administrativos
     * 
     * @throws Exception error durante la prueba
     */
    @Test
    void actualizar_conRolUser_retorna403() throws Exception {

        mockMvc.perform(put(URL_ID)
                        .header(AUTH_HEADER, BEARER_PREFIX + tokenUsuario())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new ProductoActualizarRequestDTO())))
                .andExpect(status().isForbidden());
    }

    /**
     * Verifica acceso no autorizado a actualizacion
     * cuando no se envia token
     * 
     * @throws Exception error durante la prueba
     */
    @Test
    void actualizar_sinToken_retorna401() throws Exception {

        mockMvc.perform(put(URL_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new ProductoActualizarRequestDTO())))
                .andExpect(status().isUnauthorized());
    }

    /**
     * Verifica eliminacion exitosa de productos
     * con rol administrador
     * 
     * @throws Exception error durante la prueba
     */
    @Test
    void eliminar_conRolAdmin_retorna204() throws Exception {

        doNothing().when(productoService).eliminar(1L);

        mockMvc.perform(delete(URL_ID)
                        .header(AUTH_HEADER, BEARER_PREFIX + tokenAdmin()))
                .andExpect(status().isNoContent());
    }

    /**
     * Verifica acceso denegado a eliminacion
     * para usuarios sin permisos administrativos
     * 
     * @throws Exception error durante la prueba
     */
    @Test
    void eliminar_conRolUser_retorna403() throws Exception {

        mockMvc.perform(delete(URL_ID)
                        .header(AUTH_HEADER, BEARER_PREFIX + tokenUsuario()))
                .andExpect(status().isForbidden());
    }

    /**
     * Verifica acceso no autorizado a eliminacion
     * cuando no se envia token
     * 
     * @throws Exception error durante la prueba
     */
    @Test
    void eliminar_sinToken_retorna401() throws Exception {

        mockMvc.perform(delete(URL_ID))
                .andExpect(status().isUnauthorized());
    }

    /**
     * Verifica reactivacion exitosa de productos
     * con rol administrador
     * 
     * @throws Exception error durante la prueba
     */
    @Test
    void reactivar_conRolAdmin_retorna204() throws Exception {

        doNothing().when(productoService).reactivar(1L);

        mockMvc.perform(patch(URL_REACTIVAR)
                        .header(AUTH_HEADER, BEARER_PREFIX + tokenAdmin()))
                .andExpect(status().isNoContent());
    }

    /**
     * Verifica acceso denegado a reactivacion
     * para usuarios sin permisos administrativos
     * 
     * @throws Exception error durante la prueba
     */
    @Test
    void reactivar_conRolUser_retorna403() throws Exception {

        mockMvc.perform(patch(URL_REACTIVAR)
                        .header(AUTH_HEADER, BEARER_PREFIX + tokenUsuario()))
                .andExpect(status().isForbidden());
    }

    /**
     * Verifica acceso no autorizado a reactivacion
     * cuando no se envia token
     * 
     * @throws Exception error durante la prueba
     */
    @Test
    void reactivar_sinToken_retorna401() throws Exception {

        mockMvc.perform(patch(URL_REACTIVAR))
                .andExpect(status().isUnauthorized());
    }

    /**
     * Verifica listado administrativo de productos
     * con rol administrador
     * 
     * @throws Exception error durante la prueba
     */
    @Test
    void listarTodosAdmin_conRolAdmin_retorna200() throws Exception {

        when(productoService.listarTodos(false))
                .thenReturn(List.of());

        mockMvc.perform(get(URL_ADMIN_TODOS)
                        .header(AUTH_HEADER, BEARER_PREFIX + tokenAdmin()))
                .andExpect(status().isOk());
    }

    /**
     * Verifica acceso denegado al listado administrativo
     * para usuarios sin permisos
     * 
     * @throws Exception error durante la prueba
     */
    @Test
    void listarTodosAdmin_conRolUser_retorna403() throws Exception {

        mockMvc.perform(get(URL_ADMIN_TODOS)
                        .header(AUTH_HEADER, BEARER_PREFIX + tokenUsuario()))
                .andExpect(status().isForbidden());
    }

    /**
     * Verifica acceso no autorizado al listado administrativo
     * cuando no se envia token
     * 
     * @throws Exception error durante la prueba
     */
    @Test
    void listarTodosAdmin_sinToken_retorna401() throws Exception {

        mockMvc.perform(get(URL_ADMIN_TODOS))
                .andExpect(status().isUnauthorized());
    }

    /**
     * Verifica listado general de productos
     * sin filtros
     * 
     * @throws Exception error durante la prueba
     */
    @Test
    void listar_sinFiltro_retorna200() throws Exception {

        when(productoService.listarTodos())
                .thenReturn(List.of(new ProductoResponseDTO()));

        mockMvc.perform(get(BASE_URL))
                .andExpect(status().isOk());
    }

    /**
     * Verifica listado de productos
     * filtrado por categoria
     * 
     * @throws Exception error durante la prueba
     */
    @Test
    void listar_conCategoria_retorna200() throws Exception {

        when(productoService.listarPorCategoria(1L))
                .thenReturn(List.of());

        mockMvc.perform(get(BASE_URL + "?categoriaId=1"))
                .andExpect(status().isOk());
    }

    /**
     * Verifica consulta de producto por identificador
     * sin necesidad de autenticacion
     * 
     * @throws Exception error durante la prueba
     */
    @Test
    void obtenerPorId_sinToken_retorna200() throws Exception {

        when(productoService.obtenerPorId(1L))
                .thenReturn(new ProductoResponseDTO());

        mockMvc.perform(get(URL_ID))
                .andExpect(status().isOk());
    }

    /**
     * Verifica listado liviano de productos
     * 
     * @throws Exception error durante la prueba
     */
    @Test
    void listarLiviano_retorna200() throws Exception {

        when(productoService.listarTodosLiviano())
                .thenReturn(List.of(new ProductoListadoDTO()));

        mockMvc.perform(get(URL_LISTADO))
                .andExpect(status().isOk());
    }

    /**
     * Verifica activacion masiva de productos
     * con rol administrador
     * 
     * @throws Exception error durante la prueba
     */
    @Test
    void activarLote_conRolAdmin_retorna204() throws Exception {

        ActivarLoteRequestDTO dto = new ActivarLoteRequestDTO();
        dto.setIds(List.of(1L, 2L));

        doNothing().when(productoService).activarLote(any());

        mockMvc.perform(patch(URL_ACTIVAR_LOTE)
                        .header(AUTH_HEADER, BEARER_PREFIX + tokenAdmin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNoContent());
    }

    /**
     * Verifica acceso denegado a activacion masiva
     * para usuarios sin permisos administrativos
     * 
     * @throws Exception error durante la prueba
     */
    @Test
    void activarLote_conRolUser_retorna403() throws Exception {

        mockMvc.perform(patch(URL_ACTIVAR_LOTE)
                        .header(AUTH_HEADER, BEARER_PREFIX + tokenUsuario())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isForbidden());
    }

    /**
     * Verifica acceso no autorizado a activacion masiva
     * cuando no se envia token
     * 
     * @throws Exception error durante la prueba
     */
    @Test
    void activarLote_sinToken_retorna401() throws Exception {

        mockMvc.perform(patch(URL_ACTIVAR_LOTE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isUnauthorized());
    }
}