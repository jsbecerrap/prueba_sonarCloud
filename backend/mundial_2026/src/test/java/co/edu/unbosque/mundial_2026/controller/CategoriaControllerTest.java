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

/**
 * Pruebas unitarias para {@link CategoriaController}.
 * Verifica el comportamiento de los endpoints relacionados con la gestión
 * de categorías utilizando mocks del servicio.
 */
@ExtendWith(MockitoExtension.class)
class CategoriaControllerTest {

    /**
     * Mock del servicio de categorías.
     */
    @Mock
    private CategoriaService categoriaService;

    /**
     * Instancia del controlador con mocks inyectados.
     */
    @InjectMocks
    private CategoriaController controller;

    /** 
     * Crea un DTO válido de prueba para usar en los tests.
     * 
     * @return CategoriaRequestDTO con datos de ejemplo
     */
    private CategoriaRequestDTO requestValido() {
        CategoriaRequestDTO dto = new CategoriaRequestDTO();
        dto.setNombre("Camisetas");
        dto.setDescripcion("Ropa oficial");
        return dto;
    }

    /**
     * Verifica que crear una categoría válida retorna estado 201.
     */
    @Test
    void crear_valido_retorna201() {
        when(categoriaService.crear(any())).thenReturn(new CategoriaResponseDTO());

        ResponseEntity<CategoriaResponseDTO> res = controller.crear(requestValido());

        assertEquals(201, res.getStatusCode().value());
        assertNotNull(res.getBody());
        verify(categoriaService).crear(any());
    }

    /**
     * Verifica que el controlador retorna el DTO entregado por el servicio.
     */
    @Test
    void crear_retornaElDTODelServicio() {
        CategoriaResponseDTO esperado = new CategoriaResponseDTO();
        esperado.setId(5L);
        when(categoriaService.crear(any())).thenReturn(esperado);

        ResponseEntity<CategoriaResponseDTO> res = controller.crear(requestValido());

        assertEquals(5L, res.getBody().getId());
    }

    /**
     * Verifica que una excepción del servicio al crear se propaga.
     */
    @Test
    void crear_serviceLanzaExcepcion_propaga() {
        when(categoriaService.crear(any())).thenThrow(new RuntimeException("ya existe"));

        assertThrows(RuntimeException.class, () -> controller.crear(requestValido()));
    }

    /**
     * Verifica que una descripción nula no impide crear y retorna 201.
     */
    @Test
    void crear_descripcionNula_retorna201() {
        CategoriaRequestDTO dto = new CategoriaRequestDTO();
        dto.setNombre("Gorras");
        when(categoriaService.crear(any())).thenReturn(new CategoriaResponseDTO());

        ResponseEntity<CategoriaResponseDTO> res = controller.crear(dto);

        assertEquals(201, res.getStatusCode().value());
    }

    /**
     * Verifica que listar categorías retorna 200 con elementos.
     */
    @Test
    void listar_retorna200ConLista() {
        when(categoriaService.listar()).thenReturn(List.of(new CategoriaResponseDTO()));

        ResponseEntity<List<CategoriaResponseDTO>> res = controller.listar();

        assertEquals(200, res.getStatusCode().value());
        assertEquals(1, res.getBody().size());
        verify(categoriaService).listar();
    }

    /**
     * Verifica que listar categorías vacías retorna 200.
     */
    @Test
    void listar_listaVacia_retorna200() {
        when(categoriaService.listar()).thenReturn(List.of());

        ResponseEntity<List<CategoriaResponseDTO>> res = controller.listar();

        assertEquals(200, res.getStatusCode().value());
        assertTrue(res.getBody().isEmpty());
    }

    /**
     * Verifica que actualizar una categoría válida retorna 200.
     */
    @Test
    void actualizar_valido_retorna200() {
        when(categoriaService.actualizar(eq(1L), any())).thenReturn(new CategoriaResponseDTO());

        ResponseEntity<CategoriaResponseDTO> res = controller.actualizar(1L, requestValido());

        assertEquals(200, res.getStatusCode().value());
        assertNotNull(res.getBody());
        verify(categoriaService).actualizar(eq(1L), any());
    }

    /**
     * Verifica que el DTO actualizado retornado es el del servicio.
     */
    @Test
    void actualizar_retornaElDTODelServicio() {
        CategoriaResponseDTO esperado = new CategoriaResponseDTO();
        esperado.setId(1L);
        when(categoriaService.actualizar(eq(1L), any())).thenReturn(esperado);

        ResponseEntity<CategoriaResponseDTO> res = controller.actualizar(1L, requestValido());

        assertEquals(1L, res.getBody().getId());
    }

    /**
     * Verifica que una excepción al actualizar se propaga.
     */
    @Test
    void actualizar_serviceLanzaExcepcion_propaga() {
        when(categoriaService.actualizar(eq(99L), any())).thenThrow(new RuntimeException("no encontrada"));

        assertThrows(RuntimeException.class, () -> controller.actualizar(99L, requestValido()));
    }

    /**
     * Verifica que desactivar una categoría retorna 200.
     */
    @Test
    void desactivar_retorna200() {
        when(categoriaService.desactivar(1L)).thenReturn(new DesactivarCategoriaResponseDTO());

        ResponseEntity<DesactivarCategoriaResponseDTO> res = controller.desactivar(1L);

        assertEquals(200, res.getStatusCode().value());
        assertNotNull(res.getBody());
        verify(categoriaService).desactivar(1L);
    }

    /**
     * Verifica que una excepción al desactivar se propaga.
     */
    @Test
    void desactivar_serviceLanzaExcepcion_propaga() {
        when(categoriaService.desactivar(99L)).thenThrow(new RuntimeException("no encontrada"));

        assertThrows(RuntimeException.class, () -> controller.desactivar(99L));
    }

    /**
     * Verifica que reactivar una categoría retorna 200.
     */
    @Test
    void reactivar_retorna200() {
        when(categoriaService.reactivar(1L)).thenReturn(new ReactivarCategoriaResponseDTO());

        ResponseEntity<ReactivarCategoriaResponseDTO> res = controller.reactivar(1L);

        assertEquals(200, res.getStatusCode().value());
        assertNotNull(res.getBody());
        verify(categoriaService).reactivar(1L);
    }

    /**
     * Verifica que una excepción al reactivar se propaga.
     */
    @Test
    void reactivar_serviceLanzaExcepcion_propaga() {
        when(categoriaService.reactivar(99L)).thenThrow(new RuntimeException("no encontrada"));

        assertThrows(RuntimeException.class, () -> controller.reactivar(99L));
    }

    /**
     * Verifica que listar todas las categorías retorna 200 con lista.
     */
    @Test
    void listarTodas_retorna200ConLista() {
        when(categoriaService.listarTodas()).thenReturn(List.of(new CategoriaResponseDTO()));

        ResponseEntity<List<CategoriaResponseDTO>> res = controller.listarTodas();

        assertEquals(200, res.getStatusCode().value());
        assertEquals(1, res.getBody().size());
        verify(categoriaService).listarTodas();
    }

    /**
     * Verifica que listar todas con lista vacía retorna 200.
     */
    @Test
    void listarTodas_listaVacia_retorna200() {
        when(categoriaService.listarTodas()).thenReturn(List.of());

        ResponseEntity<List<CategoriaResponseDTO>> res = controller.listarTodas();

        assertEquals(200, res.getStatusCode().value());
        assertTrue(res.getBody().isEmpty());
    }

    /**
     * Verifica que una excepción al listar todas se propaga.
     */
    @Test
    void listarTodas_serviceLanzaExcepcion_propaga() {
        when(categoriaService.listarTodas()).thenThrow(new RuntimeException("error"));

        assertThrows(RuntimeException.class, () -> controller.listarTodas());
    }

    /**
     * Verifica que obtener productos de una categoría retorna 200 con lista.
     */
    @Test
    void obtenerProductos_retorna200ConLista() {
        when(categoriaService.obtenerProductosPorCategoria(1L))
                .thenReturn(List.of(new ProductoResponseDTO()));

        ResponseEntity<List<ProductoResponseDTO>> res = controller.obtenerProductos(1L);

        assertEquals(200, res.getStatusCode().value());
        assertEquals(1, res.getBody().size());
        verify(categoriaService).obtenerProductosPorCategoria(1L);
    }

    /**
     * Verifica que obtener productos con lista vacía retorna 200.
     */
    @Test
    void obtenerProductos_listaVacia_retorna200() {
        when(categoriaService.obtenerProductosPorCategoria(99L)).thenReturn(List.of());

        ResponseEntity<List<ProductoResponseDTO>> res = controller.obtenerProductos(99L);

        assertEquals(200, res.getStatusCode().value());
        assertTrue(res.getBody().isEmpty());
    }

    /**
     * Verifica que una excepción por categoría inexistente se propaga.
     */
    @Test
    void obtenerProductos_categoriaNoExistente_propaga() {
        when(categoriaService.obtenerProductosPorCategoria(99L))
                .thenThrow(new RuntimeException("categoria no encontrada"));

        assertThrows(RuntimeException.class, () -> controller.obtenerProductos(99L));
    }
}