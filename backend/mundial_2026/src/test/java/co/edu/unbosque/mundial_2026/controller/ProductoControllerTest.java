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

import co.edu.unbosque.mundial_2026.controller.ProductoController;
import co.edu.unbosque.mundial_2026.dto.request.ActivarLoteRequestDTO;
import co.edu.unbosque.mundial_2026.dto.request.ProductoActualizarRequestDTO;
import co.edu.unbosque.mundial_2026.dto.request.ProductoRequestDTO;
import co.edu.unbosque.mundial_2026.dto.response.ProductoListadoDTO;
import co.edu.unbosque.mundial_2026.dto.response.ProductoResponseDTO;
import co.edu.unbosque.mundial_2026.exception.ProductoNotFoundException;
import co.edu.unbosque.mundial_2026.service.ProductoService;

@ExtendWith(MockitoExtension.class)
class ProductoControllerTest {

    @Mock private ProductoService productoService;
    @InjectMocks private ProductoController controller;

    private ProductoResponseDTO responseDTO(Long id, String nombre) {
        ProductoResponseDTO dto = new ProductoResponseDTO();
        dto.setId(id);
        dto.setNombre(nombre);
        return dto;
    }

    private ProductoRequestDTO requestDTO() {
        ProductoRequestDTO dto = new ProductoRequestDTO();
        dto.setNombre("Camiseta Colombia");
        dto.setPrecio(80000.0);
        dto.setCategoriaId(1L);
        return dto;
    }

    private ProductoActualizarRequestDTO actualizarDTO() {
        ProductoActualizarRequestDTO dto = new ProductoActualizarRequestDTO();
        dto.setPrecio(90000.0);
        return dto;
    }

    @Test
    void crear_exitoso_retorna201() {
        ProductoRequestDTO dto = requestDTO();
        when(productoService.crear(dto)).thenReturn(responseDTO(1L, "Camiseta Colombia"));

        ResponseEntity<ProductoResponseDTO> res = controller.crear(dto);

        assertEquals(201, res.getStatusCode().value());
        assertNotNull(res.getBody());
        assertEquals(1L, res.getBody().getId());
        assertEquals("Camiseta Colombia", res.getBody().getNombre());
        verify(productoService).crear(dto);
    }

    @Test
    void crear_categoriaInexistente_propagaExcepcion() {
        when(productoService.crear(any())).thenThrow(new ProductoNotFoundException("categoría no encontrada"));

        assertThrows(ProductoNotFoundException.class, () -> controller.crear(requestDTO()));
    }

    @Test
    void actualizar_exitoso_retornaOk() {
        ProductoActualizarRequestDTO dto = actualizarDTO();
        when(productoService.actualizar(1L, dto)).thenReturn(responseDTO(1L, "Camiseta Colombia"));

        ResponseEntity<ProductoResponseDTO> res = controller.actualizar(1L, dto);

        assertEquals(200, res.getStatusCode().value());
        assertNotNull(res.getBody());
        assertEquals(1L, res.getBody().getId());
        verify(productoService).actualizar(1L, dto);
    }

    @Test
    void actualizar_productoNoExistente_propagaExcepcion() {
        when(productoService.actualizar(eq(99L), any())).thenThrow(new ProductoNotFoundException("no encontrado"));

        assertThrows(ProductoNotFoundException.class, () -> controller.actualizar(99L, actualizarDTO()));
    }

    @Test
    void eliminar_exitoso_retorna204() {
        doNothing().when(productoService).eliminar(1L);

        ResponseEntity<Void> res = controller.eliminar(1L);

        assertEquals(204, res.getStatusCode().value());
        assertNull(res.getBody());
        verify(productoService).eliminar(1L);
    }

    @Test
    void eliminar_productoNoExistente_propagaExcepcion() {
        doThrow(new ProductoNotFoundException("no encontrado")).when(productoService).eliminar(99L);

        assertThrows(ProductoNotFoundException.class, () -> controller.eliminar(99L));
    }

    @Test
    void reactivar_exitoso_retorna204() {
        doNothing().when(productoService).reactivar(1L);

        ResponseEntity<Void> res = controller.reactivar(1L);

        assertEquals(204, res.getStatusCode().value());
        assertNull(res.getBody());
        verify(productoService).reactivar(1L);
    }

    @Test
    void reactivar_productoNoExistente_propagaExcepcion() {
        doThrow(new ProductoNotFoundException("no encontrado")).when(productoService).reactivar(99L);

        assertThrows(ProductoNotFoundException.class, () -> controller.reactivar(99L));
    }

    @Test
    void listarTodosAdmin_retornaOkConLista() {
        when(productoService.listarTodos(false))
                .thenReturn(List.of(responseDTO(1L, "Camiseta"), responseDTO(2L, "Gorra")));

        ResponseEntity<List<ProductoResponseDTO>> res = controller.listarTodosAdmin();

        assertEquals(200, res.getStatusCode().value());
        assertEquals(2, res.getBody().size());
        verify(productoService).listarTodos(false);
    }

    @Test
    void listarTodosAdmin_listaVacia_retornaOkVacio() {
        when(productoService.listarTodos(false)).thenReturn(List.of());

        ResponseEntity<List<ProductoResponseDTO>> res = controller.listarTodosAdmin();

        assertEquals(200, res.getStatusCode().value());
        assertTrue(res.getBody().isEmpty());
    }

    @Test
    void listar_sinCategoria_retornaTodosLosProductos() {
        when(productoService.listarTodos())
                .thenReturn(List.of(responseDTO(1L, "Camiseta"), responseDTO(2L, "Gorra")));

        ResponseEntity<List<ProductoResponseDTO>> res = controller.listar(null);

        assertEquals(200, res.getStatusCode().value());
        assertEquals(2, res.getBody().size());
        verify(productoService).listarTodos();
        verify(productoService, never()).listarPorCategoria(any());
    }

    @Test
    void listar_conCategoria_retornaProductosFiltrados() {
        when(productoService.listarPorCategoria(1L)).thenReturn(List.of(responseDTO(1L, "Camiseta")));

        ResponseEntity<List<ProductoResponseDTO>> res = controller.listar(1L);

        assertEquals(200, res.getStatusCode().value());
        assertEquals(1, res.getBody().size());
        verify(productoService).listarPorCategoria(1L);
        verify(productoService, never()).listarTodos();
    }

    @Test
    void listar_conCategoria_listaVacia_retornaOkVacio() {
        when(productoService.listarPorCategoria(99L)).thenReturn(List.of());

        ResponseEntity<List<ProductoResponseDTO>> res = controller.listar(99L);

        assertEquals(200, res.getStatusCode().value());
        assertTrue(res.getBody().isEmpty());
    }

    @Test
    void listar_sinCategoria_listaVacia_retornaOkVacio() {
        when(productoService.listarTodos()).thenReturn(List.of());

        ResponseEntity<List<ProductoResponseDTO>> res = controller.listar(null);

        assertEquals(200, res.getStatusCode().value());
        assertTrue(res.getBody().isEmpty());
    }

    @Test
    void obtenerPorId_existente_retornaOk() {
        when(productoService.obtenerPorId(1L)).thenReturn(responseDTO(1L, "Camiseta Colombia"));

        ResponseEntity<ProductoResponseDTO> res = controller.obtenerPorId(1L);

        assertEquals(200, res.getStatusCode().value());
        assertEquals(1L, res.getBody().getId());
        assertEquals("Camiseta Colombia", res.getBody().getNombre());
        verify(productoService).obtenerPorId(1L);
    }

    @Test
    void obtenerPorId_noExistente_propagaExcepcion() {
        when(productoService.obtenerPorId(99L)).thenThrow(new ProductoNotFoundException("no encontrado"));

        assertThrows(ProductoNotFoundException.class, () -> controller.obtenerPorId(99L));
    }

    @Test
    void listarLiviano_retornaOkConLista() {
        ProductoListadoDTO dto = new ProductoListadoDTO();
        when(productoService.listarTodosLiviano()).thenReturn(List.of(dto));

        ResponseEntity<List<ProductoListadoDTO>> res = controller.listarLiviano();

        assertEquals(200, res.getStatusCode().value());
        assertEquals(1, res.getBody().size());
        verify(productoService).listarTodosLiviano();
    }

    @Test
    void listarLiviano_listaVacia_retornaOkVacio() {
        when(productoService.listarTodosLiviano()).thenReturn(List.of());

        ResponseEntity<List<ProductoListadoDTO>> res = controller.listarLiviano();

        assertEquals(200, res.getStatusCode().value());
        assertTrue(res.getBody().isEmpty());
    }

    @Test
    void activarLote_exitoso_retorna204() {
        ActivarLoteRequestDTO dto = new ActivarLoteRequestDTO();
        dto.setIds(List.of(1L, 2L, 3L));
        doNothing().when(productoService).activarLote(List.of(1L, 2L, 3L));

        ResponseEntity<Void> res = controller.activarLote(dto);

        assertEquals(204, res.getStatusCode().value());
        assertNull(res.getBody());
        verify(productoService).activarLote(List.of(1L, 2L, 3L));
    }

    @Test
    void activarLote_listaVacia_retorna204() {
        ActivarLoteRequestDTO dto = new ActivarLoteRequestDTO();
        dto.setIds(List.of());
        doNothing().when(productoService).activarLote(List.of());

        ResponseEntity<Void> res = controller.activarLote(dto);

        assertEquals(204, res.getStatusCode().value());
        verify(productoService).activarLote(List.of());
    }

    @Test
    void activarLote_productoNoExistente_propagaExcepcion() {
        ActivarLoteRequestDTO dto = new ActivarLoteRequestDTO();
        dto.setIds(List.of(99L));
        doThrow(new ProductoNotFoundException("no encontrado")).when(productoService).activarLote(List.of(99L));

        assertThrows(ProductoNotFoundException.class, () -> controller.activarLote(dto));
    }
}