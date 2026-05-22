package co.edu.unbosque.mundial_2026.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import co.edu.unbosque.mundial_2026.dto.request.CategoriaRequestDTO;
import co.edu.unbosque.mundial_2026.dto.response.CategoriaResponseDTO;
import co.edu.unbosque.mundial_2026.dto.response.DesactivarCategoriaResponseDTO;
import co.edu.unbosque.mundial_2026.dto.response.ProductoResponseDTO;
import co.edu.unbosque.mundial_2026.dto.response.ReactivarCategoriaResponseDTO;
import co.edu.unbosque.mundial_2026.service.CategoriaService;

@WebMvcTest(CategoriaController.class)
@ActiveProfiles("test")
class CategoriaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CategoriaService categoriaService;

    private final ObjectMapper mapper = new ObjectMapper();

    private CategoriaRequestDTO requestValido() {
        CategoriaRequestDTO dto = new CategoriaRequestDTO();
        dto.setNombre("Camisetas");
        dto.setDescripcion("Ropa oficial");
        return dto;
    }

    @Test
    void crear_conRolAdmin_retorna201() throws Exception {
        when(categoriaService.crear(any())).thenReturn(new CategoriaResponseDTO());

        mockMvc.perform(post("/api/categorias")
                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(requestValido())))
                .andExpect(status().isCreated());
    }

    @Test
    void crear_sinRolAdmin_retorna403() throws Exception {
        mockMvc.perform(post("/api/categorias")
                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_USER")))
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(requestValido())))
                .andExpect(status().isForbidden());
    }

    @Test
    void crear_sinAutenticacion_retorna401() throws Exception {
        mockMvc.perform(post("/api/categorias")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(requestValido())))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void crear_nombreVacio_retorna400() throws Exception {
        CategoriaRequestDTO dto = new CategoriaRequestDTO();
        dto.setNombre("");

        mockMvc.perform(post("/api/categorias")
                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void crear_nombreNull_retorna400() throws Exception {
        CategoriaRequestDTO dto = new CategoriaRequestDTO();

        mockMvc.perform(post("/api/categorias")
                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void crear_nombreMuyCorto_retorna400() throws Exception {
        CategoriaRequestDTO dto = new CategoriaRequestDTO();
        dto.setNombre("A");

        mockMvc.perform(post("/api/categorias")
                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void crear_nombreConCaracteresInvalidos_retorna400() throws Exception {
        CategoriaRequestDTO dto = new CategoriaRequestDTO();
        dto.setNombre("Camisetas!!@#");

        mockMvc.perform(post("/api/categorias")
                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void crear_descripcionDemasiadoLarga_retorna400() throws Exception {
        CategoriaRequestDTO dto = requestValido();
        dto.setDescripcion("a".repeat(251));

        mockMvc.perform(post("/api/categorias")
                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void listar_sinAutenticacion_retorna200() throws Exception {
        when(categoriaService.listar()).thenReturn(List.of(new CategoriaResponseDTO()));

        mockMvc.perform(get("/api/categorias"))
                .andExpect(status().isOk());
    }

    @Test
    void listar_listaVacia_retorna200() throws Exception {
        when(categoriaService.listar()).thenReturn(List.of());

        mockMvc.perform(get("/api/categorias"))
                .andExpect(status().isOk());
    }

    @Test
    void listar_conJwt_retorna200() throws Exception {
        when(categoriaService.listar()).thenReturn(List.of(new CategoriaResponseDTO()));

        mockMvc.perform(get("/api/categorias")
                .with(jwt()))
                .andExpect(status().isOk());
    }

    @Test
    void actualizar_conRolAdmin_retorna200() throws Exception {
        when(categoriaService.actualizar(eq(1L), any())).thenReturn(new CategoriaResponseDTO());

        mockMvc.perform(put("/api/categorias/1")
                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(requestValido())))
                .andExpect(status().isOk());
    }

    @Test
    void actualizar_sinRolAdmin_retorna403() throws Exception {
        mockMvc.perform(put("/api/categorias/1")
                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_USER")))
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(requestValido())))
                .andExpect(status().isForbidden());
    }

    @Test
    void actualizar_sinAutenticacion_retorna401() throws Exception {
        mockMvc.perform(put("/api/categorias/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(requestValido())))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void actualizar_bodyInvalido_retorna400() throws Exception {
        CategoriaRequestDTO dto = new CategoriaRequestDTO();

        mockMvc.perform(put("/api/categorias/1")
                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void desactivar_conRolAdmin_retorna200() throws Exception {
        when(categoriaService.desactivar(1L)).thenReturn(new DesactivarCategoriaResponseDTO());

        mockMvc.perform(patch("/api/categorias/1/desactivar")
                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
                .andExpect(status().isOk());
    }

    @Test
    void desactivar_sinRolAdmin_retorna403() throws Exception {
        mockMvc.perform(patch("/api/categorias/1/desactivar")
                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_USER"))))
                .andExpect(status().isForbidden());
    }

    @Test
    void desactivar_sinAutenticacion_retorna401() throws Exception {
        mockMvc.perform(patch("/api/categorias/1/desactivar"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void reactivar_conRolAdmin_retorna200() throws Exception {
        when(categoriaService.reactivar(1L)).thenReturn(new ReactivarCategoriaResponseDTO());

        mockMvc.perform(patch("/api/categorias/1/reactivar")
                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
                .andExpect(status().isOk());
    }

    @Test
    void reactivar_sinRolAdmin_retorna403() throws Exception {
        mockMvc.perform(patch("/api/categorias/1/reactivar")
                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_USER"))))
                .andExpect(status().isForbidden());
    }

    @Test
    void reactivar_sinAutenticacion_retorna401() throws Exception {
        mockMvc.perform(patch("/api/categorias/1/reactivar"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void listarTodas_conRolAdmin_retorna200() throws Exception {
        when(categoriaService.listarTodas()).thenReturn(List.of(new CategoriaResponseDTO()));

        mockMvc.perform(get("/api/categorias/todas")
                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
                .andExpect(status().isOk());
    }

    @Test
    void listarTodas_listaVacia_retorna200() throws Exception {
        when(categoriaService.listarTodas()).thenReturn(List.of());

        mockMvc.perform(get("/api/categorias/todas")
                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
                .andExpect(status().isOk());
    }

    @Test
    void listarTodas_sinRolAdmin_retorna403() throws Exception {
        mockMvc.perform(get("/api/categorias/todas")
                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_USER"))))
                .andExpect(status().isForbidden());
    }

    @Test
    void listarTodas_sinAutenticacion_retorna401() throws Exception {
        mockMvc.perform(get("/api/categorias/todas"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void obtenerProductos_conRolAdmin_retorna200() throws Exception {
        when(categoriaService.obtenerProductosPorCategoria(1L)).thenReturn(List.of(new ProductoResponseDTO()));

        mockMvc.perform(get("/api/categorias/1/productos")
                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
                .andExpect(status().isOk());
    }

    @Test
    void obtenerProductos_listaVacia_retorna200() throws Exception {
        when(categoriaService.obtenerProductosPorCategoria(eq(99L))).thenReturn(List.of());

        mockMvc.perform(get("/api/categorias/99/productos")
                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
                .andExpect(status().isOk());
    }

    @Test
    void obtenerProductos_sinRolAdmin_retorna403() throws Exception {
        mockMvc.perform(get("/api/categorias/1/productos")
                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_USER"))))
                .andExpect(status().isForbidden());
    }

    @Test
    void obtenerProductos_sinAutenticacion_retorna401() throws Exception {
        mockMvc.perform(get("/api/categorias/1/productos"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void crear_descripcionNula_retorna201() throws Exception {
        CategoriaRequestDTO dto = new CategoriaRequestDTO();
        dto.setNombre("Gorras");
        when(categoriaService.crear(any())).thenReturn(new CategoriaResponseDTO());

        mockMvc.perform(post("/api/categorias")
                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());
    }

    @Test
    void actualizar_nombreConCaracteresInvalidos_retorna400() throws Exception {
        CategoriaRequestDTO dto = new CategoriaRequestDTO();
        dto.setNombre("Cat@!!!");

        mockMvc.perform(put("/api/categorias/1")
                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }
}