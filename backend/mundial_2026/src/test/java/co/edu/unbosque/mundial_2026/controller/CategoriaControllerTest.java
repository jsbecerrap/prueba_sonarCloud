package co.edu.unbosque.mundial_2026.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import co.edu.unbosque.mundial_2026.dto.request.CategoriaRequestDTO;
import co.edu.unbosque.mundial_2026.dto.response.CategoriaResponseDTO;
import co.edu.unbosque.mundial_2026.dto.response.DesactivarCategoriaResponseDTO;
import co.edu.unbosque.mundial_2026.dto.response.ProductoResponseDTO;
import co.edu.unbosque.mundial_2026.dto.response.ReactivarCategoriaResponseDTO;
import co.edu.unbosque.mundial_2026.service.CategoriaService;

@ExtendWith(MockitoExtension.class)
class CategoriaControllerTest {

    @Mock
    private CategoriaService categoriaService;

    @InjectMocks
    private CategoriaController controller;

    private CategoriaRequestDTO requestValido() {
        CategoriaRequestDTO dto = new CategoriaRequestDTO();
        dto.setNombre("Camisetas");
        dto.setDescripcion("Ropa oficial");
        return dto;
    }

    @Test
    void crear_valido_retorna201() {
        when(categoriaService.crear(any())).thenReturn(new CategoriaResponseDTO());

        ResponseEntity<CategoriaResponseDTO> res = controller.crear(requestValido());

        assertEquals(201, res.getStatusCode().value());
        assertNotNull(res.getBody());
        verify(categoriaService).crear(any());
    }

    @Test
    void crear_retornaElDTODelServicio() {
        CategoriaResponseDTO esperado = new CategoriaResponseDTO();
        esperado.setId(5L);
        when(categoriaService.crear(any())).thenReturn(esperado);

        ResponseEntity<CategoriaResponseDTO> res = controller.crear(requestValido());

        assertEquals(5L, res.getBody().getId());
    }

    @Test
    void crear_serviceLanzaExcepcion_propaga() {
        when(categoriaService.crear(any())).thenThrow(new RuntimeException("ya existe"));

        assertThrows(RuntimeException.class, () -> controller.crear(requestValido()));
    }

    @Test
    void crear_descripcionNula_retorna201() {
        CategoriaRequestDTO dto = new CategoriaRequestDTO();
        dto.setNombre("Gorras");
        when(categoriaService.crear(any())).thenReturn(new CategoriaResponseDTO());

        ResponseEntity<CategoriaResponseDTO> res = controller.crear(dto);

        assertEquals(201, res.getStatusCode().value());
    }



    @Test
    void listar_retorna200ConLista() {
        when(categoriaService.listar()).thenReturn(List.of(new CategoriaResponseDTO()));

        ResponseEntity<List<CategoriaResponseDTO>> res = controller.listar();

        assertEquals(200, res.getStatusCode().value());
        assertEquals(1, res.getBody().size());
        verify(categoriaService).listar();
    }

    @Test
    void listar_listaVacia_retorna200() {
        when(categoriaService.listar()).thenReturn(List.of());

        ResponseEntity<List<CategoriaResponseDTO>> res = controller.listar();

        assertEquals(200, res.getStatusCode().value());
        assertTrue(res.getBody().isEmpty());
    }


    @Test
    void actualizar_valido_retorna200() {
        when(categoriaService.actualizar(eq(1L), any())).thenReturn(new CategoriaResponseDTO());

        ResponseEntity<CategoriaResponseDTO> res = controller.actualizar(1L, requestValido());

        assertEquals(200, res.getStatusCode().value());
        assertNotNull(res.getBody());
        verify(categoriaService).actualizar(eq(1L), any());
    }

    @Test
    void actualizar_retornaElDTODelServicio() {
        CategoriaResponseDTO esperado = new CategoriaResponseDTO();
        esperado.setId(1L);
        when(categoriaService.actualizar(eq(1L), any())).thenReturn(esperado);

        ResponseEntity<CategoriaResponseDTO> res = controller.actualizar(1L, requestValido());

        assertEquals(1L, res.getBody().getId());
    }

    @Test
    void actualizar_serviceLanzaExcepcion_propaga() {
        when(categoriaService.actualizar(eq(99L), any())).thenThrow(new RuntimeException("no encontrada"));

        assertThrows(RuntimeException.class, () -> controller.actualizar(99L, requestValido()));
    }

   

    @Test
    void desactivar_retorna200() {
        when(categoriaService.desactivar(1L)).thenReturn(new DesactivarCategoriaResponseDTO());

        ResponseEntity<DesactivarCategoriaResponseDTO> res = controller.desactivar(1L);

        assertEquals(200, res.getStatusCode().value());
        assertNotNull(res.getBody());
        verify(categoriaService).desactivar(1L);
    }

    @Test
    void desactivar_serviceLanzaExcepcion_propaga() {
        when(categoriaService.desactivar(99L)).thenThrow(new RuntimeException("no encontrada"));

        assertThrows(RuntimeException.class, () -> controller.desactivar(99L));
    }

   

    @Test
    void reactivar_retorna200() {
        when(categoriaService.reactivar(1L)).thenReturn(new ReactivarCategoriaResponseDTO());

        ResponseEntity<ReactivarCategoriaResponseDTO> res = controller.reactivar(1L);

        assertEquals(200, res.getStatusCode().value());
        assertNotNull(res.getBody());
        verify(categoriaService).reactivar(1L);
    }

    @Test
    void reactivar_serviceLanzaExcepcion_propaga() {
        when(categoriaService.reactivar(99L)).thenThrow(new RuntimeException("no encontrada"));

        assertThrows(RuntimeException.class, () -> controller.reactivar(99L));
    }


    @Test
    void listarTodas_retorna200ConLista() {
        when(categoriaService.listarTodas()).thenReturn(List.of(new CategoriaResponseDTO()));

        ResponseEntity<List<CategoriaResponseDTO>> res = controller.listarTodas();

        assertEquals(200, res.getStatusCode().value());
        assertEquals(1, res.getBody().size());
        verify(categoriaService).listarTodas();
    }

    @Test
    void listarTodas_listaVacia_retorna200() {
        when(categoriaService.listarTodas()).thenReturn(List.of());

        ResponseEntity<List<CategoriaResponseDTO>> res = controller.listarTodas();

        assertEquals(200, res.getStatusCode().value());
        assertTrue(res.getBody().isEmpty());
    }

    @Test
    void listarTodas_serviceLanzaExcepcion_propaga() {
        when(categoriaService.listarTodas()).thenThrow(new RuntimeException("error"));

        assertThrows(RuntimeException.class, () -> controller.listarTodas());
    }

 
    @Test
    void obtenerProductos_retorna200ConLista() {
        when(categoriaService.obtenerProductosPorCategoria(1L))
                .thenReturn(List.of(new ProductoResponseDTO()));

        ResponseEntity<List<ProductoResponseDTO>> res = controller.obtenerProductos(1L);

        assertEquals(200, res.getStatusCode().value());
        assertEquals(1, res.getBody().size());
        verify(categoriaService).obtenerProductosPorCategoria(1L);
    }

    @Test
    void obtenerProductos_listaVacia_retorna200() {
        when(categoriaService.obtenerProductosPorCategoria(99L)).thenReturn(List.of());

        ResponseEntity<List<ProductoResponseDTO>> res = controller.obtenerProductos(99L);

        assertEquals(200, res.getStatusCode().value());
        assertTrue(res.getBody().isEmpty());
    }

    @Test
    void obtenerProductos_categoriaNoExistente_propaga() {
        when(categoriaService.obtenerProductosPorCategoria(99L))
                .thenThrow(new RuntimeException("categoria no encontrada"));

        assertThrows(RuntimeException.class, () -> controller.obtenerProductos(99L));
    }
}