package co.edu.unbosque.mundial_2026.controller;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import co.edu.unbosque.mundial_2026.dto.response.*;
import co.edu.unbosque.mundial_2026.entity.Partido;
import co.edu.unbosque.mundial_2026.exception.PartidoNotFoundException;
import co.edu.unbosque.mundial_2026.service.PartidoService;

/**
 * Pruebas unitarias para {@link PartidoController}
 * Verifica el comportamiento del controlador de partidos usando mocks de {@link PartidoService},
 * con contexto de seguridad simulado para los endpoints que requieren usuario autenticado
 */
@ExtendWith(MockitoExtension.class)
class PartidoControllerTest {

    @Mock private PartidoService partidoService;
    @InjectMocks private PartidoController controller;

    /** Correo del usuario autenticado simulado en el SecurityContext para los tests de favoritos */
    private static final String USER_CORREO = "user@test.com";

    /** Fecha de partido de prueba usada en los tests de busqueda y sincronizacion por fecha */
    private static final String FECHA_PARTIDO = "2026-06-11";

    @Mock private SecurityContext securityContext;
    @Mock private Authentication authentication;

    @BeforeEach
    void setUpSecurityContext() {
        lenient().when(securityContext.getAuthentication()).thenReturn(authentication);
        lenient().when(authentication.getName()).thenReturn(USER_CORREO);
        SecurityContextHolder.setContext(securityContext);
    }

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    /** 
     * @param id
     * @return PartidoDTO
     */
    private PartidoDTO partidoDTO(Long id) {
        InfoPartidoDTO info = new InfoPartidoDTO();
        info.setId(id);
        EstadioDTO estadio = new EstadioDTO();
        estadio.setNombre("MetLife Stadium");
        info.setEstadio(estadio);
        EstadoDTO estado = new EstadoDTO();
        estado.setCodigo("NS");
        info.setEstado(estado);
        info.setFecha("2026-06-11T18:00:00+00:00");
        LigaDTO liga = new LigaDTO();
        liga.setRonda("Group Stage");
        EquipoDTO local = new EquipoDTO();
        local.setNombre("Colombia");
        EquipoDTO visitante = new EquipoDTO();
        visitante.setNombre("Brazil");
        EquipoConEstadioDTO equipos = new EquipoConEstadioDTO();
        equipos.setLocal(local);
        equipos.setVisitante(visitante);
        MarcadorDTO goles = new MarcadorDTO();
        PartidoDTO dto = new PartidoDTO();
        dto.setInformacion(info);
        dto.setLiga(liga);
        dto.setEquipos(equipos);
        dto.setGoles(goles);
        return dto;
    }

    /**
     * Verifica que listar todos los partidos retorna HTTP 200 y la lista contiene exactamente un elemento
     */
    @Test
    void listarPartidos_retornaOkConLista() {
        when(partidoService.obtenerPartidos()).thenReturn(List.of(partidoDTO(1L)));

        ResponseEntity<List<PartidoDTO>> res = controller.listarPartidos();

        assertEquals(200, res.getStatusCode().value());
        assertEquals(1, res.getBody().size());
        verify(partidoService).obtenerPartidos();
    }

    /**
     * Verifica que listar partidos cuando no hay ninguno retorna HTTP 200 con lista vacia
     */
    @Test
    void listarPartidos_listaVacia_retornaOkVacio() {
        when(partidoService.obtenerPartidos()).thenReturn(List.of());

        ResponseEntity<List<PartidoDTO>> res = controller.listarPartidos();

        assertEquals(200, res.getStatusCode().value());
        assertTrue(res.getBody().isEmpty());
    }

    /**
     * Verifica que obtener partidos por equipo retorna HTTP 200 y la lista contiene exactamente un elemento
     */
    @Test
    void obtenerPartidosPorEquipo_retornaOkConLista() {
        when(partidoService.obtenerPartidosPorEquipo(1L)).thenReturn(List.of(partidoDTO(1L)));

        ResponseEntity<List<PartidoDTO>> res = controller.obtenerPartidosPorEquipo(1L);

        assertEquals(200, res.getStatusCode().value());
        assertEquals(1, res.getBody().size());
        verify(partidoService).obtenerPartidosPorEquipo(1L);
    }

    /**
     * Verifica que obtener partidos de un equipo sin partidos retorna HTTP 200 con lista vacia
     */
    @Test
    void obtenerPartidosPorEquipo_listaVacia_retornaOkVacio() {
        when(partidoService.obtenerPartidosPorEquipo(99L)).thenReturn(List.of());

        ResponseEntity<List<PartidoDTO>> res = controller.obtenerPartidosPorEquipo(99L);

        assertEquals(200, res.getStatusCode().value());
        assertTrue(res.getBody().isEmpty());
    }

    /**
     * Verifica que obtener standings retorna HTTP 200 y la lista contiene exactamente un grupo
     */
    @Test
    void obtenerStandings_retornaOkConGrupos() {
        List<List<PosicionDTO>> standings = List.of(List.of(new PosicionDTO()));
        when(partidoService.obtenerStandings()).thenReturn(standings);

        ResponseEntity<List<List<PosicionDTO>>> res = controller.obtenerStandings();

        assertEquals(200, res.getStatusCode().value());
        assertEquals(1, res.getBody().size());
        verify(partidoService).obtenerStandings();
    }

    /**
     * Verifica que obtener standings cuando no hay grupos retorna HTTP 200 con lista vacia
     */
    @Test
    void obtenerStandings_vacio_retornaOkVacio() {
        when(partidoService.obtenerStandings()).thenReturn(List.of());

        ResponseEntity<List<List<PosicionDTO>>> res = controller.obtenerStandings();

        assertEquals(200, res.getStatusCode().value());
        assertTrue(res.getBody().isEmpty());
    }

    /**
     * Verifica que obtener selecciones del mundial retorna HTTP 200 y la lista contiene exactamente un elemento
     */
    @Test
    void obtenerSelecciones_retornaOkConLista() {
        when(partidoService.obtenerSelecciones()).thenReturn(List.of(new EquipoMundialDTO()));

        ResponseEntity<List<EquipoMundialDTO>> res = controller.obtenerSelecciones();

        assertEquals(200, res.getStatusCode().value());
        assertEquals(1, res.getBody().size());
        verify(partidoService).obtenerSelecciones();
    }

    /**
     * Verifica que obtener selecciones cuando no hay ninguna retorna HTTP 200 con lista vacia
     */
    @Test
    void obtenerSelecciones_listaVacia_retornaOkVacio() {
        when(partidoService.obtenerSelecciones()).thenReturn(List.of());

        ResponseEntity<List<EquipoMundialDTO>> res = controller.obtenerSelecciones();

        assertEquals(200, res.getStatusCode().value());
        assertTrue(res.getBody().isEmpty());
    }

    /**
     * Verifica que obtener jugadores por equipo retorna HTTP 200 y la lista contiene exactamente un elemento
     */
    @Test
    void obtenerJugadoresPorEquipo_retornaOkConLista() {
        when(partidoService.obtenerJugadoresPorEquipo(1L)).thenReturn(List.of(new JugadorDTO()));

        ResponseEntity<List<JugadorDTO>> res = controller.obtenerJugadoresPorEquipo(1L);

        assertEquals(200, res.getStatusCode().value());
        assertEquals(1, res.getBody().size());
        verify(partidoService).obtenerJugadoresPorEquipo(1L);
    }

    /**
     * Verifica que obtener jugadores de un equipo sin jugadores retorna HTTP 200 con lista vacia
     */
    @Test
    void obtenerJugadoresPorEquipo_listaVacia_retornaOkVacio() {
        when(partidoService.obtenerJugadoresPorEquipo(99L)).thenReturn(List.of());

        ResponseEntity<List<JugadorDTO>> res = controller.obtenerJugadoresPorEquipo(99L);

        assertEquals(200, res.getStatusCode().value());
        assertTrue(res.getBody().isEmpty());
    }

    /**
     * Verifica que obtener partidos por fecha retorna HTTP 200 y la lista contiene exactamente un elemento
     */
    @Test
    void obtenerPartidosPorFecha_retornaOkConLista() {
        when(partidoService.obtenerPartidosPorFecha(FECHA_PARTIDO)).thenReturn(List.of(partidoDTO(1L)));

        ResponseEntity<List<PartidoDTO>> res = controller.obtenerPartidosPorFecha(FECHA_PARTIDO);

        assertEquals(200, res.getStatusCode().value());
        assertEquals(1, res.getBody().size());
        verify(partidoService).obtenerPartidosPorFecha(FECHA_PARTIDO);
    }

    /**
     * Verifica que obtener partidos en una fecha sin partidos retorna HTTP 200 con lista vacia
     */
    @Test
    void obtenerPartidosPorFecha_listaVacia_retornaOkVacio() {
        when(partidoService.obtenerPartidosPorFecha("2099-01-01")).thenReturn(List.of());

        ResponseEntity<List<PartidoDTO>> res = controller.obtenerPartidosPorFecha("2099-01-01");

        assertEquals(200, res.getStatusCode().value());
        assertTrue(res.getBody().isEmpty());
    }

    /**
     * Verifica que obtener partidos en vivo retorna HTTP 200 y la lista contiene exactamente un elemento
     */
    @Test
    void obtenerPartidosEnVivo_retornaOkConLista() {
        when(partidoService.obtenerPartidosEnVivo()).thenReturn(List.of(partidoDTO(1L)));

        ResponseEntity<List<PartidoDTO>> res = controller.obtenerPartidosEnVivo();

        assertEquals(200, res.getStatusCode().value());
        assertEquals(1, res.getBody().size());
        verify(partidoService).obtenerPartidosEnVivo();
    }

    /**
     * Verifica que obtener partidos en vivo cuando no hay ninguno retorna HTTP 200 con lista vacia
     */
    @Test
    void obtenerPartidosEnVivo_sinPartidos_retornaOkVacio() {
        when(partidoService.obtenerPartidosEnVivo()).thenReturn(List.of());

        ResponseEntity<List<PartidoDTO>> res = controller.obtenerPartidosEnVivo();

        assertEquals(200, res.getStatusCode().value());
        assertTrue(res.getBody().isEmpty());
    }

    /**
     * Verifica que obtener partidos de las selecciones favoritas del usuario autenticado
     * retorna HTTP 200 y la lista contiene exactamente un elemento
     */
    @Test
    void obtenerPartidosPorSeleccionesFav_retornaOkConLista() {
        when(partidoService.obtenerPartidosPorSeleccionesFav(USER_CORREO))
                .thenReturn(List.of(partidoDTO(1L)));

        ResponseEntity<List<PartidoDTO>> res = controller.obtenerPartidosPorSeleccionesFav();

        assertEquals(200, res.getStatusCode().value());
        assertEquals(1, res.getBody().size());
        verify(partidoService).obtenerPartidosPorSeleccionesFav(USER_CORREO);
    }

    /**
     * Verifica que obtener partidos por selecciones favoritas sin resultados retorna HTTP 200 con lista vacia
     */
    @Test
    void obtenerPartidosPorSeleccionesFav_listaVacia_retornaOkVacio() {
        when(partidoService.obtenerPartidosPorSeleccionesFav(USER_CORREO)).thenReturn(List.of());

        ResponseEntity<List<PartidoDTO>> res = controller.obtenerPartidosPorSeleccionesFav();

        assertEquals(200, res.getStatusCode().value());
        assertTrue(res.getBody().isEmpty());
    }

    /**
     * Verifica que obtener partidos de los estadios favoritos del usuario autenticado
     * retorna HTTP 200 y la lista contiene exactamente un elemento
     */
    @Test
    void obtenerPartidosPorEstadiosFav_retornaOkConLista() {
        when(partidoService.obtenerPartidosPorEstadiosFav(USER_CORREO))
                .thenReturn(List.of(partidoDTO(1L)));

        ResponseEntity<List<PartidoDTO>> res = controller.obtenerPartidosPorEstadiosFav();

        assertEquals(200, res.getStatusCode().value());
        assertEquals(1, res.getBody().size());
        verify(partidoService).obtenerPartidosPorEstadiosFav(USER_CORREO);
    }

    /**
     * Verifica que obtener partidos por estadios favoritos sin resultados retorna HTTP 200 con lista vacia
     */
    @Test
    void obtenerPartidosPorEstadiosFav_listaVacia_retornaOkVacio() {
        when(partidoService.obtenerPartidosPorEstadiosFav(USER_CORREO)).thenReturn(List.of());

        ResponseEntity<List<PartidoDTO>> res = controller.obtenerPartidosPorEstadiosFav();

        assertEquals(200, res.getStatusCode().value());
        assertTrue(res.getBody().isEmpty());
    }

    /**
     * Verifica que obtener partidos de las ciudades favoritas del usuario autenticado
     * retorna HTTP 200 y la lista contiene exactamente un elemento
     */
    @Test
    void obtenerPartidosPorCiudadesFav_retornaOkConLista() {
        when(partidoService.obtenerPartidosPorCiudadesFav(USER_CORREO))
                .thenReturn(List.of(partidoDTO(1L)));

        ResponseEntity<List<PartidoDTO>> res = controller.obtenerPartidosPorCiudadesFav();

        assertEquals(200, res.getStatusCode().value());
        assertEquals(1, res.getBody().size());
        verify(partidoService).obtenerPartidosPorCiudadesFav(USER_CORREO);
    }

    /**
     * Verifica que obtener partidos por ciudades favoritas sin resultados retorna HTTP 200 con lista vacia
     */
    @Test
    void obtenerPartidosPorCiudadesFav_listaVacia_retornaOkVacio() {
        when(partidoService.obtenerPartidosPorCiudadesFav(USER_CORREO)).thenReturn(List.of());

        ResponseEntity<List<PartidoDTO>> res = controller.obtenerPartidosPorCiudadesFav();

        assertEquals(200, res.getStatusCode().value());
        assertTrue(res.getBody().isEmpty());
    }

    /**
     * Verifica que sincronizar partidos por fecha y liga retorna HTTP 200
     * y el conteo de partidos sincronizados es el esperado
     */
    @Test
    void sincronizarPorFechaYLiga_retornaOkConConteo() {
        when(partidoService.sincronizarPorFechaYLiga(FECHA_PARTIDO, 1, 2026)).thenReturn(10);

        ResponseEntity<Integer> res = controller.sincronizarPorFechaYLiga(1, 2026, FECHA_PARTIDO);

        assertEquals(200, res.getStatusCode().value());
        assertEquals(10, res.getBody());
        verify(partidoService).sincronizarPorFechaYLiga(FECHA_PARTIDO, 1, 2026);
    }

    /**
     * Verifica que sincronizar partidos cuando no hay ninguno que sincronizar retorna HTTP 200 con cero
     */
    @Test
    void sincronizarPorFechaYLiga_sinPartidos_retornaCero() {
        when(partidoService.sincronizarPorFechaYLiga(any(), anyInt(), anyInt())).thenReturn(0);

        ResponseEntity<Integer> res = controller.sincronizarPorFechaYLiga(1, 2026, FECHA_PARTIDO);

        assertEquals(200, res.getStatusCode().value());
        assertEquals(0, res.getBody());
    }

    /**
     * Verifica que actualizar el resultado de un partido existente retorna HTTP 200 con valor uno
     */
    @Test
    void actualizarResultado_retornaOkConUno() {
        when(partidoService.actualizarResultado(1L, 2, 1, 90)).thenReturn(1);

        ResponseEntity<Integer> res = controller.actualizarResultado(1L, 2, 1, 90);

        assertEquals(200, res.getStatusCode().value());
        assertEquals(1, res.getBody());
        verify(partidoService).actualizarResultado(1L, 2, 1, 90);
    }

    /**
     * Verifica que actualizar el resultado de un partido inexistente lanza {@link PartidoNotFoundException}
     */
    @Test
    void actualizarResultado_partidoNoExistente_propagaExcepcion() {
        when(partidoService.actualizarResultado(eq(99L), anyInt(), anyInt(), anyInt()))
                .thenThrow(new PartidoNotFoundException("no encontrado"));

        assertThrows(PartidoNotFoundException.class,
                () -> controller.actualizarResultado(99L, 1, 0, 90));
    }

    /**
     * Verifica que obtener un partido por ID existente retorna HTTP 200
     * y el ID del DTO coincide con el solicitado
     */
    @Test
    void obtenerPorId_existente_retornaOk() {
        when(partidoService.obtenerPartidoPorId(1L)).thenReturn(partidoDTO(1L));

        ResponseEntity<PartidoDTO> res = controller.obtenerPorId(1L);

        assertEquals(200, res.getStatusCode().value());
        assertNotNull(res.getBody());
        assertEquals(1L, res.getBody().getInformacion().getId());
        verify(partidoService).obtenerPartidoPorId(1L);
    }

    /**
     * Verifica que obtener un partido por ID inexistente lanza {@link PartidoNotFoundException}
     */
    @Test
    void obtenerPorId_noExistente_propagaExcepcion() {
        when(partidoService.obtenerPartidoPorId(99L)).thenThrow(new PartidoNotFoundException("no encontrado"));

        assertThrows(PartidoNotFoundException.class, () -> controller.obtenerPorId(99L));
    }

    /**
     * Verifica que obtener el catalogo de selecciones retorna HTTP 200
     * y el nombre del primer elemento coincide con el esperado
     */
    @Test
    void obtenerCatalogoSelecciones_retornaOkConLista() {
        when(partidoService.obtenerCatalogoSelecciones())
                .thenReturn(List.of(new PreferenciaDTO(1L, "Colombia")));

        ResponseEntity<List<PreferenciaDTO>> res = controller.obtenerCatalogoSelecciones();

        assertEquals(200, res.getStatusCode().value());
        assertEquals(1, res.getBody().size());
        assertEquals("Colombia", res.getBody().get(0).getNombre());
        verify(partidoService).obtenerCatalogoSelecciones();
    }

    /**
     * Verifica que obtener el catalogo de selecciones cuando no hay ninguna retorna HTTP 200 con lista vacia
     */
    @Test
    void obtenerCatalogoSelecciones_listaVacia_retornaOkVacio() {
        when(partidoService.obtenerCatalogoSelecciones()).thenReturn(List.of());

        ResponseEntity<List<PreferenciaDTO>> res = controller.obtenerCatalogoSelecciones();

        assertEquals(200, res.getStatusCode().value());
        assertTrue(res.getBody().isEmpty());
    }

    /**
     * Verifica que listar partidos desde la base de datos retorna HTTP 200
     * y la lista contiene exactamente un elemento
     */
    @Test
    void listarDesdeBD_retornaOkConLista() {
        Partido partido = new Partido();
        partido.setId(1L);
        when(partidoService.listarDesdeBD()).thenReturn(List.of(partido));

        ResponseEntity<List<Partido>> res = controller.listarDesdeBD();

        assertEquals(200, res.getStatusCode().value());
        assertEquals(1, res.getBody().size());
        verify(partidoService).listarDesdeBD();
    }

    /**
     * Verifica que listar partidos desde la base de datos cuando no hay ninguno retorna HTTP 200 con lista vacia
     */
    @Test
    void listarDesdeBD_listaVacia_retornaOkVacio() {
        when(partidoService.listarDesdeBD()).thenReturn(List.of());

        ResponseEntity<List<Partido>> res = controller.listarDesdeBD();

        assertEquals(200, res.getStatusCode().value());
        assertTrue(res.getBody().isEmpty());
    }
}