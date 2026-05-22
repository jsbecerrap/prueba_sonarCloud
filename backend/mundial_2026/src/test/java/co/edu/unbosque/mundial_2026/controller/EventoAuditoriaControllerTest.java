package co.edu.unbosque.mundial_2026.controller;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import co.edu.unbosque.mundial_2026.dto.EventoAuditoriaDTO;
import co.edu.unbosque.mundial_2026.service.EventoAuditoriaService;

@ExtendWith(MockitoExtension.class)
class EventoAuditoriaControllerTest {

    @Mock private EventoAuditoriaService eventoService;
    @InjectMocks private EventoAuditoriaController controller;

private static final String USUARIO = "USUARIO";
private static final String LOGIN = "LOGIN";
private static final String CORR_1 = "corr-1";
private static final String COMPRA = "COMPRA";
    private EventoAuditoriaDTO crearDTO(Long id, String tipo) {
        return new EventoAuditoriaDTO(id, tipo, "desc", LocalDateTime.now(), CORR_1, USUARIO, 1L);
    }

    private Page<EventoAuditoriaDTO> paginaConUno(EventoAuditoriaDTO dto) {
        return new PageImpl<>(List.of(dto));
    }

    private Page<EventoAuditoriaDTO> paginaVacia() {
        return new PageImpl<>(List.of());
    }

    @Test
    void obtenerTodos_retornaOkConPagina() {
        when(eventoService.obtenerTodos(any(Pageable.class))).thenReturn(paginaConUno(crearDTO(1L, LOGIN)));

        ResponseEntity<Page<EventoAuditoriaDTO>> res = controller.obtenerTodos(0, 50);

        assertEquals(200, res.getStatusCode().value());
        assertNotNull(res.getBody());
        assertEquals(1, res.getBody().getTotalElements());
    }

    @Test
    void obtenerTodos_paginaVacia_retornaOkVacio() {
        when(eventoService.obtenerTodos(any(Pageable.class))).thenReturn(paginaVacia());

        ResponseEntity<Page<EventoAuditoriaDTO>> res = controller.obtenerTodos(0, 50);

        assertEquals(200, res.getStatusCode().value());
        assertTrue(res.getBody().isEmpty());
    }

    @Test
    void obtenerTodos_pageNegativa_usaCero() {
        when(eventoService.obtenerTodos(any(Pageable.class))).thenReturn(paginaVacia());

        ResponseEntity<Page<EventoAuditoriaDTO>> res = controller.obtenerTodos(-5, 50);

        assertEquals(200, res.getStatusCode().value());
        verify(eventoService).obtenerTodos(argThat(p -> p.getPageNumber() == 0));
    }

    @Test
    void obtenerTodos_sizeSuperiorACien_usaCien() {
        when(eventoService.obtenerTodos(any(Pageable.class))).thenReturn(paginaVacia());

        ResponseEntity<Page<EventoAuditoriaDTO>> res = controller.obtenerTodos(0, 200);

        assertEquals(200, res.getStatusCode().value());
        verify(eventoService).obtenerTodos(argThat(p -> p.getPageSize() == 100));
    }

    @Test
    void obtenerTodos_sizeMenorAUno_usaUno() {
        when(eventoService.obtenerTodos(any(Pageable.class))).thenReturn(paginaVacia());

        ResponseEntity<Page<EventoAuditoriaDTO>> res = controller.obtenerTodos(0, 0);

        assertEquals(200, res.getStatusCode().value());
        verify(eventoService).obtenerTodos(argThat(p -> p.getPageSize() == 1));
    }

    @Test
    void buscar_conTodosLosFiltros_retornaOk() {
        LocalDateTime inicio = LocalDateTime.now().minusDays(7);
        LocalDateTime fin = LocalDateTime.now();
        when(eventoService.buscarConFiltros(eq(1L), eq(LOGIN), eq(inicio), eq(fin), any(Pageable.class)))
                .thenReturn(paginaConUno(crearDTO(1L, LOGIN)));

        ResponseEntity<Page<EventoAuditoriaDTO>> res = controller.buscar(1L, LOGIN, inicio, fin, 0, 50);

        assertEquals(200, res.getStatusCode().value());
        assertEquals(1, res.getBody().getTotalElements());
        verify(eventoService).buscarConFiltros(eq(1L), eq(LOGIN), eq(inicio), eq(fin), any(Pageable.class));
    }

    @Test
    void buscar_sinFiltros_retornaOk() {
        when(eventoService.buscarConFiltros(isNull(), isNull(), isNull(), isNull(), any(Pageable.class)))
                .thenReturn(paginaVacia());

        ResponseEntity<Page<EventoAuditoriaDTO>> res = controller.buscar(null, null, null, null, 0, 50);

        assertEquals(200, res.getStatusCode().value());
        verify(eventoService).buscarConFiltros(isNull(), isNull(), isNull(), isNull(), any(Pageable.class));
    }

    @Test
    void buscar_soloFiltroUsuario_retornaOk() {
        when(eventoService.buscarConFiltros(eq(1L), isNull(), isNull(), isNull(), any(Pageable.class)))
                .thenReturn(paginaConUno(crearDTO(1L, COMPRA)));

        ResponseEntity<Page<EventoAuditoriaDTO>> res = controller.buscar(1L, null, null, null, 0, 50);

        assertEquals(200, res.getStatusCode().value());
    }

    @Test
    void buscar_soloFiltroTipos_retornaOk() {
        when(eventoService.buscarConFiltros(isNull(), eq("ORDEN_PAGADA,LOGIN"), isNull(), isNull(), any(Pageable.class)))
                .thenReturn(paginaVacia());

        ResponseEntity<Page<EventoAuditoriaDTO>> res = controller.buscar(null, "ORDEN_PAGADA,LOGIN", null, null, 0, 50);

        assertEquals(200, res.getStatusCode().value());
    }

    @Test
    void buscarPorUsuario_retornaOkConPagina() {
        when(eventoService.buscarPorUsuario(eq(1L), any(Pageable.class)))
                .thenReturn(paginaConUno(crearDTO(1L, LOGIN)));

        ResponseEntity<Page<EventoAuditoriaDTO>> res = controller.buscarPorUsuario(1L, 0, 50);

        assertEquals(200, res.getStatusCode().value());
        assertEquals(1, res.getBody().getTotalElements());
        verify(eventoService).buscarPorUsuario(eq(1L), any(Pageable.class));
    }

    @Test
    void buscarPorUsuario_sinResultados_retornaOkVacio() {
        when(eventoService.buscarPorUsuario(eq(99L), any(Pageable.class))).thenReturn(paginaVacia());

        ResponseEntity<Page<EventoAuditoriaDTO>> res = controller.buscarPorUsuario(99L, 0, 50);

        assertEquals(200, res.getStatusCode().value());
        assertTrue(res.getBody().isEmpty());
    }

    @Test
    void buscarPorUsuario_paginacionPersonalizada_respetaParametros() {
        when(eventoService.buscarPorUsuario(eq(1L), any(Pageable.class))).thenReturn(paginaVacia());

        controller.buscarPorUsuario(1L, 2, 10);

        verify(eventoService).buscarPorUsuario(eq(1L), argThat(p -> p.getPageNumber() == 2 && p.getPageSize() == 10));
    }

    @Test
    void buscarPorTipo_retornaOkConPagina() {
        when(eventoService.buscarPorTipo(eq(LOGIN), any(Pageable.class)))
                .thenReturn(paginaConUno(crearDTO(1L, LOGIN)));

        ResponseEntity<Page<EventoAuditoriaDTO>> res = controller.buscarPorTipo(LOGIN, 0, 50);

        assertEquals(200, res.getStatusCode().value());
        assertEquals(1, res.getBody().getTotalElements());
        verify(eventoService).buscarPorTipo(eq(LOGIN), any(Pageable.class));
    }

    @Test
    void buscarPorTipo_sinResultados_retornaOkVacio() {
        when(eventoService.buscarPorTipo(eq("TIPO_RARO"), any(Pageable.class))).thenReturn(paginaVacia());

        ResponseEntity<Page<EventoAuditoriaDTO>> res = controller.buscarPorTipo("TIPO_RARO", 0, 50);

        assertEquals(200, res.getStatusCode().value());
        assertTrue(res.getBody().isEmpty());
    }

    @Test
    void buscarPorTipo_paginacionPersonalizada_respetaParametros() {
        when(eventoService.buscarPorTipo(eq(COMPRA), any(Pageable.class))).thenReturn(paginaVacia());

        controller.buscarPorTipo(COMPRA, 1, 20);

        verify(eventoService).buscarPorTipo(eq(COMPRA), argThat(p -> p.getPageNumber() == 1 && p.getPageSize() == 20));
    }

    @Test
    void buscarPorCorrelacion_retornaOkConPagina() {
        when(eventoService.buscarPorCorrelacion(eq(CORR_1), any(Pageable.class)))
                .thenReturn(paginaConUno(crearDTO(1L, COMPRA)));

        ResponseEntity<Page<EventoAuditoriaDTO>> res = controller.buscarPorCorrelacion(CORR_1, 0, 50);

        assertEquals(200, res.getStatusCode().value());
        assertEquals(1, res.getBody().getTotalElements());
        verify(eventoService).buscarPorCorrelacion(eq(CORR_1), any(Pageable.class));
    }

    @Test
    void buscarPorCorrelacion_sinResultados_retornaOkVacio() {
        when(eventoService.buscarPorCorrelacion(eq("no-existe"), any(Pageable.class))).thenReturn(paginaVacia());

        ResponseEntity<Page<EventoAuditoriaDTO>> res = controller.buscarPorCorrelacion("no-existe", 0, 50);

        assertEquals(200, res.getStatusCode().value());
        assertTrue(res.getBody().isEmpty());
    }

    @Test
    void buscarPorFecha_retornaOkConPagina() {
        LocalDateTime inicio = LocalDateTime.now().minusDays(1);
        LocalDateTime fin = LocalDateTime.now();
        when(eventoService.buscarPorFecha(eq(inicio), eq(fin), any(Pageable.class)))
                .thenReturn(paginaConUno(crearDTO(1L, LOGIN)));

        ResponseEntity<Page<EventoAuditoriaDTO>> res = controller.buscarPorFecha(inicio, fin, 0, 50);

        assertEquals(200, res.getStatusCode().value());
        assertEquals(1, res.getBody().getTotalElements());
        verify(eventoService).buscarPorFecha(eq(inicio), eq(fin), any(Pageable.class));
    }

    @Test
    void buscarPorFecha_sinResultados_retornaOkVacio() {
        LocalDateTime inicio = LocalDateTime.now().minusDays(30);
        LocalDateTime fin = LocalDateTime.now().minusDays(29);
        when(eventoService.buscarPorFecha(eq(inicio), eq(fin), any(Pageable.class))).thenReturn(paginaVacia());

        ResponseEntity<Page<EventoAuditoriaDTO>> res = controller.buscarPorFecha(inicio, fin, 0, 50);

        assertEquals(200, res.getStatusCode().value());
        assertTrue(res.getBody().isEmpty());
    }

    @Test
    void buscarPorFecha_paginacionPersonalizada_respetaParametros() {
        LocalDateTime inicio = LocalDateTime.now().minusDays(1);
        LocalDateTime fin = LocalDateTime.now();
        when(eventoService.buscarPorFecha(any(), any(), any(Pageable.class))).thenReturn(paginaVacia());

        controller.buscarPorFecha(inicio, fin, 3, 25);

        verify(eventoService).buscarPorFecha(eq(inicio), eq(fin), argThat(p -> p.getPageNumber() == 3 && p.getPageSize() == 25));
    }

    @Test
    void buscarPorEntidad_retornaOkConPagina() {
        when(eventoService.buscarPorEntidad(eq(USUARIO), any(Pageable.class)))
                .thenReturn(paginaConUno(crearDTO(1L, "REGISTRO")));

        ResponseEntity<Page<EventoAuditoriaDTO>> res = controller.buscarPorEntidad(USUARIO, 0, 50);

        assertEquals(200, res.getStatusCode().value());
        assertEquals(1, res.getBody().getTotalElements());
        verify(eventoService).buscarPorEntidad(eq(USUARIO), any(Pageable.class));
    }

    @Test
    void buscarPorEntidad_sinResultados_retornaOkVacio() {
        when(eventoService.buscarPorEntidad(eq("ENTIDAD_RARA"), any(Pageable.class))).thenReturn(paginaVacia());

        ResponseEntity<Page<EventoAuditoriaDTO>> res = controller.buscarPorEntidad("ENTIDAD_RARA", 0, 50);

        assertEquals(200, res.getStatusCode().value());
        assertTrue(res.getBody().isEmpty());
    }

    @Test
    void buscarPorEntidad_paginacionPersonalizada_respetaParametros() {
        when(eventoService.buscarPorEntidad(eq("ORDEN"), any(Pageable.class))).thenReturn(paginaVacia());

        controller.buscarPorEntidad("ORDEN", 0, 10);

        verify(eventoService).buscarPorEntidad(eq("ORDEN"), argThat(p -> p.getPageSize() == 10));
    }

    @Test
    void buildPageable_ordenDescendentePorFecha() {
        when(eventoService.obtenerTodos(any(Pageable.class))).thenReturn(paginaVacia());

        controller.obtenerTodos(0, 10);

        verify(eventoService).obtenerTodos(argThat(p ->
                p.getSort().getOrderFor("fecha") != null &&
                p.getSort().getOrderFor("fecha").isDescending()));
    }
}