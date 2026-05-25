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

/**
 * Pruebas unitarias para {@link EventoAuditoriaController}
 * Verifica el comportamiento del controlador de auditoria usando mocks de {@link EventoAuditoriaService}
 */
@ExtendWith(MockitoExtension.class)
class EventoAuditoriaControllerTest {

    @Mock private EventoAuditoriaService eventoService;
    @InjectMocks private EventoAuditoriaController controller;

    /** Tipo de entidad usado como constante en los tests */
    private static final String USUARIO = "USUARIO";

    /** Tipo de evento de inicio de sesion usado en los tests */
    private static final String LOGIN = "LOGIN";

    /** Identificador de correlacion de prueba usado en los tests */
    private static final String CORR_1 = "corr-1";

    /** Tipo de evento de compra usado en los tests */
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

    /**
     * Verifica que obtener todos los eventos retorna HTTP 200
     * y la pagina contiene exactamente un elemento
     */
    @Test
    void obtenerTodos_retornaOkConPagina() {
        when(eventoService.obtenerTodos(any(Pageable.class))).thenReturn(paginaConUno(crearDTO(1L, LOGIN)));

        ResponseEntity<Page<EventoAuditoriaDTO>> res = controller.obtenerTodos(0, 50);

        assertEquals(200, res.getStatusCode().value());
        assertNotNull(res.getBody());
        assertEquals(1, res.getBody().getTotalElements());
    }

    /**
     * Verifica que obtener todos los eventos cuando no hay ninguno retorna HTTP 200 con pagina vacia
     */
    @Test
    void obtenerTodos_paginaVacia_retornaOkVacio() {
        when(eventoService.obtenerTodos(any(Pageable.class))).thenReturn(paginaVacia());

        ResponseEntity<Page<EventoAuditoriaDTO>> res = controller.obtenerTodos(0, 50);

        assertEquals(200, res.getStatusCode().value());
        assertTrue(res.getBody().isEmpty());
    }

    /**
     * Verifica que pasar un numero de pagina negativo lo corrige a cero automaticamente
     */
    @Test
    void obtenerTodos_pageNegativa_usaCero() {
        when(eventoService.obtenerTodos(any(Pageable.class))).thenReturn(paginaVacia());

        ResponseEntity<Page<EventoAuditoriaDTO>> res = controller.obtenerTodos(-5, 50);

        assertEquals(200, res.getStatusCode().value());
        verify(eventoService).obtenerTodos(argThat(p -> p.getPageNumber() == 0));
    }

    /**
     * Verifica que pasar un size superior a 100 lo limita a 100 para evitar sobrecargar la consulta
     */
    @Test
    void obtenerTodos_sizeSuperiorACien_usaCien() {
        when(eventoService.obtenerTodos(any(Pageable.class))).thenReturn(paginaVacia());

        ResponseEntity<Page<EventoAuditoriaDTO>> res = controller.obtenerTodos(0, 200);

        assertEquals(200, res.getStatusCode().value());
        verify(eventoService).obtenerTodos(argThat(p -> p.getPageSize() == 100));
    }

    /**
     * Verifica que pasar un size menor a 1 lo corrige a 1 como minimo permitido
     */
    @Test
    void obtenerTodos_sizeMenorAUno_usaUno() {
        when(eventoService.obtenerTodos(any(Pageable.class))).thenReturn(paginaVacia());

        ResponseEntity<Page<EventoAuditoriaDTO>> res = controller.obtenerTodos(0, 0);

        assertEquals(200, res.getStatusCode().value());
        verify(eventoService).obtenerTodos(argThat(p -> p.getPageSize() == 1));
    }

    /**
     * Verifica que buscar eventos con todos los filtros disponibles retorna HTTP 200
     * y el servicio recibe exactamente los parametros indicados
     */
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

    /**
     * Verifica que buscar eventos sin ningun filtro retorna HTTP 200
     * y el servicio recibe todos los parametros como nulos
     */
    @Test
    void buscar_sinFiltros_retornaOk() {
        when(eventoService.buscarConFiltros(isNull(), isNull(), isNull(), isNull(), any(Pageable.class)))
                .thenReturn(paginaVacia());

        ResponseEntity<Page<EventoAuditoriaDTO>> res = controller.buscar(null, null, null, null, 0, 50);

        assertEquals(200, res.getStatusCode().value());
        verify(eventoService).buscarConFiltros(isNull(), isNull(), isNull(), isNull(), any(Pageable.class));
    }

    /**
     * Verifica que buscar eventos filtrando solo por usuario retorna HTTP 200
     */
    @Test
    void buscar_soloFiltroUsuario_retornaOk() {
        when(eventoService.buscarConFiltros(eq(1L), isNull(), isNull(), isNull(), any(Pageable.class)))
                .thenReturn(paginaConUno(crearDTO(1L, COMPRA)));

        ResponseEntity<Page<EventoAuditoriaDTO>> res = controller.buscar(1L, null, null, null, 0, 50);

        assertEquals(200, res.getStatusCode().value());
    }

    /**
     * Verifica que buscar eventos filtrando solo por tipo retorna HTTP 200
     */
    @Test
    void buscar_soloFiltroTipos_retornaOk() {
        when(eventoService.buscarConFiltros(isNull(), eq("ORDEN_PAGADA,LOGIN"), isNull(), isNull(), any(Pageable.class)))
                .thenReturn(paginaVacia());

        ResponseEntity<Page<EventoAuditoriaDTO>> res = controller.buscar(null, "ORDEN_PAGADA,LOGIN", null, null, 0, 50);

        assertEquals(200, res.getStatusCode().value());
    }

    /**
     * Verifica que buscar eventos por usuario retorna HTTP 200
     * y la pagina contiene exactamente un elemento
     */
    @Test
    void buscarPorUsuario_retornaOkConPagina() {
        when(eventoService.buscarPorUsuario(eq(1L), any(Pageable.class)))
                .thenReturn(paginaConUno(crearDTO(1L, LOGIN)));

        ResponseEntity<Page<EventoAuditoriaDTO>> res = controller.buscarPorUsuario(1L, 0, 50);

        assertEquals(200, res.getStatusCode().value());
        assertEquals(1, res.getBody().getTotalElements());
        verify(eventoService).buscarPorUsuario(eq(1L), any(Pageable.class));
    }

    /**
     * Verifica que buscar eventos de un usuario sin resultados retorna HTTP 200 con pagina vacia
     */
    @Test
    void buscarPorUsuario_sinResultados_retornaOkVacio() {
        when(eventoService.buscarPorUsuario(eq(99L), any(Pageable.class))).thenReturn(paginaVacia());

        ResponseEntity<Page<EventoAuditoriaDTO>> res = controller.buscarPorUsuario(99L, 0, 50);

        assertEquals(200, res.getStatusCode().value());
        assertTrue(res.getBody().isEmpty());
    }

    /**
     * Verifica que los parametros de paginacion personalizados se pasan correctamente al servicio
     */
    @Test
    void buscarPorUsuario_paginacionPersonalizada_respetaParametros() {
        when(eventoService.buscarPorUsuario(eq(1L), any(Pageable.class))).thenReturn(paginaVacia());

        controller.buscarPorUsuario(1L, 2, 10);

        verify(eventoService).buscarPorUsuario(eq(1L), argThat(p -> p.getPageNumber() == 2 && p.getPageSize() == 10));
    }

    /**
     * Verifica que buscar eventos por tipo retorna HTTP 200
     * y la pagina contiene exactamente un elemento
     */
    @Test
    void buscarPorTipo_retornaOkConPagina() {
        when(eventoService.buscarPorTipo(eq(LOGIN), any(Pageable.class)))
                .thenReturn(paginaConUno(crearDTO(1L, LOGIN)));

        ResponseEntity<Page<EventoAuditoriaDTO>> res = controller.buscarPorTipo(LOGIN, 0, 50);

        assertEquals(200, res.getStatusCode().value());
        assertEquals(1, res.getBody().getTotalElements());
        verify(eventoService).buscarPorTipo(eq(LOGIN), any(Pageable.class));
    }

    /**
     * Verifica que buscar eventos por un tipo que no existe retorna HTTP 200 con pagina vacia
     */
    @Test
    void buscarPorTipo_sinResultados_retornaOkVacio() {
        when(eventoService.buscarPorTipo(eq("TIPO_RARO"), any(Pageable.class))).thenReturn(paginaVacia());

        ResponseEntity<Page<EventoAuditoriaDTO>> res = controller.buscarPorTipo("TIPO_RARO", 0, 50);

        assertEquals(200, res.getStatusCode().value());
        assertTrue(res.getBody().isEmpty());
    }

    /**
     * Verifica que los parametros de paginacion personalizados al buscar por tipo se pasan correctamente al servicio
     */
    @Test
    void buscarPorTipo_paginacionPersonalizada_respetaParametros() {
        when(eventoService.buscarPorTipo(eq(COMPRA), any(Pageable.class))).thenReturn(paginaVacia());

        controller.buscarPorTipo(COMPRA, 1, 20);

        verify(eventoService).buscarPorTipo(eq(COMPRA), argThat(p -> p.getPageNumber() == 1 && p.getPageSize() == 20));
    }

    /**
     * Verifica que buscar eventos por correlacion retorna HTTP 200
     * y la pagina contiene exactamente un elemento
     */
    @Test
    void buscarPorCorrelacion_retornaOkConPagina() {
        when(eventoService.buscarPorCorrelacion(eq(CORR_1), any(Pageable.class)))
                .thenReturn(paginaConUno(crearDTO(1L, COMPRA)));

        ResponseEntity<Page<EventoAuditoriaDTO>> res = controller.buscarPorCorrelacion(CORR_1, 0, 50);

        assertEquals(200, res.getStatusCode().value());
        assertEquals(1, res.getBody().getTotalElements());
        verify(eventoService).buscarPorCorrelacion(eq(CORR_1), any(Pageable.class));
    }

    /**
     * Verifica que buscar eventos por una correlacion inexistente retorna HTTP 200 con pagina vacia
     */
    @Test
    void buscarPorCorrelacion_sinResultados_retornaOkVacio() {
        when(eventoService.buscarPorCorrelacion(eq("no-existe"), any(Pageable.class))).thenReturn(paginaVacia());

        ResponseEntity<Page<EventoAuditoriaDTO>> res = controller.buscarPorCorrelacion("no-existe", 0, 50);

        assertEquals(200, res.getStatusCode().value());
        assertTrue(res.getBody().isEmpty());
    }

    /**
     * Verifica que buscar eventos por rango de fechas retorna HTTP 200
     * y la pagina contiene exactamente un elemento
     */
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

    /**
     * Verifica que buscar eventos en un rango de fechas sin resultados retorna HTTP 200 con pagina vacia
     */
    @Test
    void buscarPorFecha_sinResultados_retornaOkVacio() {
        LocalDateTime inicio = LocalDateTime.now().minusDays(30);
        LocalDateTime fin = LocalDateTime.now().minusDays(29);
        when(eventoService.buscarPorFecha(eq(inicio), eq(fin), any(Pageable.class))).thenReturn(paginaVacia());

        ResponseEntity<Page<EventoAuditoriaDTO>> res = controller.buscarPorFecha(inicio, fin, 0, 50);

        assertEquals(200, res.getStatusCode().value());
        assertTrue(res.getBody().isEmpty());
    }

    /**
     * Verifica que los parametros de paginacion personalizados al buscar por fecha se pasan correctamente al servicio
     */
    @Test
    void buscarPorFecha_paginacionPersonalizada_respetaParametros() {
        LocalDateTime inicio = LocalDateTime.now().minusDays(1);
        LocalDateTime fin = LocalDateTime.now();
        when(eventoService.buscarPorFecha(any(), any(), any(Pageable.class))).thenReturn(paginaVacia());

        controller.buscarPorFecha(inicio, fin, 3, 25);

        verify(eventoService).buscarPorFecha(eq(inicio), eq(fin), argThat(p -> p.getPageNumber() == 3 && p.getPageSize() == 25));
    }

    /**
     * Verifica que buscar eventos por entidad retorna HTTP 200
     * y la pagina contiene exactamente un elemento
     */
    @Test
    void buscarPorEntidad_retornaOkConPagina() {
        when(eventoService.buscarPorEntidad(eq(USUARIO), any(Pageable.class)))
                .thenReturn(paginaConUno(crearDTO(1L, "REGISTRO")));

        ResponseEntity<Page<EventoAuditoriaDTO>> res = controller.buscarPorEntidad(USUARIO, 0, 50);

        assertEquals(200, res.getStatusCode().value());
        assertEquals(1, res.getBody().getTotalElements());
        verify(eventoService).buscarPorEntidad(eq(USUARIO), any(Pageable.class));
    }

    /**
     * Verifica que buscar eventos por una entidad sin resultados retorna HTTP 200 con pagina vacia
     */
    @Test
    void buscarPorEntidad_sinResultados_retornaOkVacio() {
        when(eventoService.buscarPorEntidad(eq("ENTIDAD_RARA"), any(Pageable.class))).thenReturn(paginaVacia());

        ResponseEntity<Page<EventoAuditoriaDTO>> res = controller.buscarPorEntidad("ENTIDAD_RARA", 0, 50);

        assertEquals(200, res.getStatusCode().value());
        assertTrue(res.getBody().isEmpty());
    }

    /**
     * Verifica que los parametros de paginacion personalizados al buscar por entidad se pasan correctamente al servicio
     */
    @Test
    void buscarPorEntidad_paginacionPersonalizada_respetaParametros() {
        when(eventoService.buscarPorEntidad(eq("ORDEN"), any(Pageable.class))).thenReturn(paginaVacia());

        controller.buscarPorEntidad("ORDEN", 0, 10);

        verify(eventoService).buscarPorEntidad(eq("ORDEN"), argThat(p -> p.getPageSize() == 10));
    }

    /**
     * Verifica que el Pageable construido internamente ordena los resultados por fecha de forma descendente
     */
    @Test
    void buildPageable_ordenDescendentePorFecha() {
        when(eventoService.obtenerTodos(any(Pageable.class))).thenReturn(paginaVacia());

        controller.obtenerTodos(0, 10);

        verify(eventoService).obtenerTodos(argThat(p ->
                p.getSort().getOrderFor("fecha") != null &&
                p.getSort().getOrderFor("fecha").isDescending()));
    }
}