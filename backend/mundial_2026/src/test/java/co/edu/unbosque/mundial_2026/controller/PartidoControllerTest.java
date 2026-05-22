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

import co.edu.unbosque.mundial_2026.controller.PartidoController;
import co.edu.unbosque.mundial_2026.dto.response.*;
import co.edu.unbosque.mundial_2026.entity.Partido;
import co.edu.unbosque.mundial_2026.exception.PartidoNotFoundException;
import co.edu.unbosque.mundial_2026.service.PartidoService;

@ExtendWith(MockitoExtension.class)
class PartidoControllerTest {

    @Mock private PartidoService partidoService;
    @InjectMocks private PartidoController controller;

    @Mock private SecurityContext securityContext;
    @Mock private Authentication authentication;

    @BeforeEach
    void setUpSecurityContext() {
       when(securityContext.getAuthentication()).thenReturn(authentication);
when(authentication.getName()).thenReturn("user@test.com");
        SecurityContextHolder.setContext(securityContext);
    }

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

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

    @Test
    void listarPartidos_retornaOkConLista() {
        when(partidoService.obtenerPartidos()).thenReturn(List.of(partidoDTO(1L)));

        ResponseEntity<List<PartidoDTO>> res = controller.listarPartidos();

        assertEquals(200, res.getStatusCode().value());
        assertEquals(1, res.getBody().size());
        verify(partidoService).obtenerPartidos();
    }

    @Test
    void listarPartidos_listaVacia_retornaOkVacio() {
        when(partidoService.obtenerPartidos()).thenReturn(List.of());

        ResponseEntity<List<PartidoDTO>> res = controller.listarPartidos();

        assertEquals(200, res.getStatusCode().value());
        assertTrue(res.getBody().isEmpty());
    }

    @Test
    void obtenerPartidosPorEquipo_retornaOkConLista() {
        when(partidoService.obtenerPartidosPorEquipo(1L)).thenReturn(List.of(partidoDTO(1L)));

        ResponseEntity<List<PartidoDTO>> res = controller.obtenerPartidosPorEquipo(1L);

        assertEquals(200, res.getStatusCode().value());
        assertEquals(1, res.getBody().size());
        verify(partidoService).obtenerPartidosPorEquipo(1L);
    }

    @Test
    void obtenerPartidosPorEquipo_listaVacia_retornaOkVacio() {
        when(partidoService.obtenerPartidosPorEquipo(99L)).thenReturn(List.of());

        ResponseEntity<List<PartidoDTO>> res = controller.obtenerPartidosPorEquipo(99L);

        assertEquals(200, res.getStatusCode().value());
        assertTrue(res.getBody().isEmpty());
    }

    @Test
    void obtenerStandings_retornaOkConGrupos() {
        List<List<PosicionDTO>> standings = List.of(List.of(new PosicionDTO()));
        when(partidoService.obtenerStandings()).thenReturn(standings);

        ResponseEntity<List<List<PosicionDTO>>> res = controller.obtenerStandings();

        assertEquals(200, res.getStatusCode().value());
        assertEquals(1, res.getBody().size());
        verify(partidoService).obtenerStandings();
    }

    @Test
    void obtenerStandings_vacio_retornaOkVacio() {
        when(partidoService.obtenerStandings()).thenReturn(List.of());

        ResponseEntity<List<List<PosicionDTO>>> res = controller.obtenerStandings();

        assertEquals(200, res.getStatusCode().value());
        assertTrue(res.getBody().isEmpty());
    }

    @Test
    void obtenerSelecciones_retornaOkConLista() {
        when(partidoService.obtenerSelecciones()).thenReturn(List.of(new EquipoMundialDTO()));

        ResponseEntity<List<EquipoMundialDTO>> res = controller.obtenerSelecciones();

        assertEquals(200, res.getStatusCode().value());
        assertEquals(1, res.getBody().size());
        verify(partidoService).obtenerSelecciones();
    }

    @Test
    void obtenerSelecciones_listaVacia_retornaOkVacio() {
        when(partidoService.obtenerSelecciones()).thenReturn(List.of());

        ResponseEntity<List<EquipoMundialDTO>> res = controller.obtenerSelecciones();

        assertEquals(200, res.getStatusCode().value());
        assertTrue(res.getBody().isEmpty());
    }

    @Test
    void obtenerJugadoresPorEquipo_retornaOkConLista() {
        when(partidoService.obtenerJugadoresPorEquipo(1L)).thenReturn(List.of(new JugadorDTO()));

        ResponseEntity<List<JugadorDTO>> res = controller.obtenerJugadoresPorEquipo(1L);

        assertEquals(200, res.getStatusCode().value());
        assertEquals(1, res.getBody().size());
        verify(partidoService).obtenerJugadoresPorEquipo(1L);
    }

    @Test
    void obtenerJugadoresPorEquipo_listaVacia_retornaOkVacio() {
        when(partidoService.obtenerJugadoresPorEquipo(99L)).thenReturn(List.of());

        ResponseEntity<List<JugadorDTO>> res = controller.obtenerJugadoresPorEquipo(99L);

        assertEquals(200, res.getStatusCode().value());
        assertTrue(res.getBody().isEmpty());
    }

    @Test
    void obtenerPartidosPorFecha_retornaOkConLista() {
        when(partidoService.obtenerPartidosPorFecha("2026-06-11")).thenReturn(List.of(partidoDTO(1L)));

        ResponseEntity<List<PartidoDTO>> res = controller.obtenerPartidosPorFecha("2026-06-11");

        assertEquals(200, res.getStatusCode().value());
        assertEquals(1, res.getBody().size());
        verify(partidoService).obtenerPartidosPorFecha("2026-06-11");
    }

    @Test
    void obtenerPartidosPorFecha_listaVacia_retornaOkVacio() {
        when(partidoService.obtenerPartidosPorFecha("2099-01-01")).thenReturn(List.of());

        ResponseEntity<List<PartidoDTO>> res = controller.obtenerPartidosPorFecha("2099-01-01");

        assertEquals(200, res.getStatusCode().value());
        assertTrue(res.getBody().isEmpty());
    }

    @Test
    void obtenerPartidosEnVivo_retornaOkConLista() {
        when(partidoService.obtenerPartidosEnVivo()).thenReturn(List.of(partidoDTO(1L)));

        ResponseEntity<List<PartidoDTO>> res = controller.obtenerPartidosEnVivo();

        assertEquals(200, res.getStatusCode().value());
        assertEquals(1, res.getBody().size());
        verify(partidoService).obtenerPartidosEnVivo();
    }

    @Test
    void obtenerPartidosEnVivo_sinPartidos_retornaOkVacio() {
        when(partidoService.obtenerPartidosEnVivo()).thenReturn(List.of());

        ResponseEntity<List<PartidoDTO>> res = controller.obtenerPartidosEnVivo();

        assertEquals(200, res.getStatusCode().value());
        assertTrue(res.getBody().isEmpty());
    }

    @Test
    void obtenerPartidosPorSeleccionesFav_retornaOkConLista() {
        when(partidoService.obtenerPartidosPorSeleccionesFav("user@test.com"))
                .thenReturn(List.of(partidoDTO(1L)));

        ResponseEntity<List<PartidoDTO>> res = controller.obtenerPartidosPorSeleccionesFav();

        assertEquals(200, res.getStatusCode().value());
        assertEquals(1, res.getBody().size());
        verify(partidoService).obtenerPartidosPorSeleccionesFav("user@test.com");
    }

    @Test
    void obtenerPartidosPorSeleccionesFav_listaVacia_retornaOkVacio() {
        when(partidoService.obtenerPartidosPorSeleccionesFav("user@test.com")).thenReturn(List.of());

        ResponseEntity<List<PartidoDTO>> res = controller.obtenerPartidosPorSeleccionesFav();

        assertEquals(200, res.getStatusCode().value());
        assertTrue(res.getBody().isEmpty());
    }

    @Test
    void obtenerPartidosPorEstadiosFav_retornaOkConLista() {
        when(partidoService.obtenerPartidosPorEstadiosFav("user@test.com"))
                .thenReturn(List.of(partidoDTO(1L)));

        ResponseEntity<List<PartidoDTO>> res = controller.obtenerPartidosPorEstadiosFav();

        assertEquals(200, res.getStatusCode().value());
        assertEquals(1, res.getBody().size());
        verify(partidoService).obtenerPartidosPorEstadiosFav("user@test.com");
    }

    @Test
    void obtenerPartidosPorEstadiosFav_listaVacia_retornaOkVacio() {
        when(partidoService.obtenerPartidosPorEstadiosFav("user@test.com")).thenReturn(List.of());

        ResponseEntity<List<PartidoDTO>> res = controller.obtenerPartidosPorEstadiosFav();

        assertEquals(200, res.getStatusCode().value());
        assertTrue(res.getBody().isEmpty());
    }

    @Test
    void obtenerPartidosPorCiudadesFav_retornaOkConLista() {
        when(partidoService.obtenerPartidosPorCiudadesFav("user@test.com"))
                .thenReturn(List.of(partidoDTO(1L)));

        ResponseEntity<List<PartidoDTO>> res = controller.obtenerPartidosPorCiudadesFav();

        assertEquals(200, res.getStatusCode().value());
        assertEquals(1, res.getBody().size());
        verify(partidoService).obtenerPartidosPorCiudadesFav("user@test.com");
    }

    @Test
    void obtenerPartidosPorCiudadesFav_listaVacia_retornaOkVacio() {
        when(partidoService.obtenerPartidosPorCiudadesFav("user@test.com")).thenReturn(List.of());

        ResponseEntity<List<PartidoDTO>> res = controller.obtenerPartidosPorCiudadesFav();

        assertEquals(200, res.getStatusCode().value());
        assertTrue(res.getBody().isEmpty());
    }

    @Test
    void sincronizarPorFechaYLiga_retornaOkConConteo() {
        when(partidoService.sincronizarPorFechaYLiga("2026-06-11", 1, 2026)).thenReturn(10);

        ResponseEntity<Integer> res = controller.sincronizarPorFechaYLiga(1, 2026, "2026-06-11");

        assertEquals(200, res.getStatusCode().value());
        assertEquals(10, res.getBody());
        verify(partidoService).sincronizarPorFechaYLiga("2026-06-11", 1, 2026);
    }

    @Test
    void sincronizarPorFechaYLiga_sinPartidos_retornaCero() {
        when(partidoService.sincronizarPorFechaYLiga(any(), anyInt(), anyInt())).thenReturn(0);

        ResponseEntity<Integer> res = controller.sincronizarPorFechaYLiga(1, 2026, "2026-06-11");

        assertEquals(200, res.getStatusCode().value());
        assertEquals(0, res.getBody());
    }

    @Test
    void actualizarResultado_retornaOkConUno() {
        when(partidoService.actualizarResultado(1L, 2, 1, 90)).thenReturn(1);

        ResponseEntity<Integer> res = controller.actualizarResultado(1L, 2, 1, 90);

        assertEquals(200, res.getStatusCode().value());
        assertEquals(1, res.getBody());
        verify(partidoService).actualizarResultado(1L, 2, 1, 90);
    }

    @Test
    void actualizarResultado_partidoNoExistente_propagaExcepcion() {
        when(partidoService.actualizarResultado(eq(99L), anyInt(), anyInt(), anyInt()))
                .thenThrow(new PartidoNotFoundException("no encontrado"));

        assertThrows(PartidoNotFoundException.class,
                () -> controller.actualizarResultado(99L, 1, 0, 90));
    }

    @Test
    void obtenerPorId_existente_retornaOk() {
        when(partidoService.obtenerPartidoPorId(1L)).thenReturn(partidoDTO(1L));

        ResponseEntity<PartidoDTO> res = controller.obtenerPorId(1L);

        assertEquals(200, res.getStatusCode().value());
        assertNotNull(res.getBody());
        assertEquals(1L, res.getBody().getInformacion().getId());
        verify(partidoService).obtenerPartidoPorId(1L);
    }

    @Test
    void obtenerPorId_noExistente_propagaExcepcion() {
        when(partidoService.obtenerPartidoPorId(99L)).thenThrow(new PartidoNotFoundException("no encontrado"));

        assertThrows(PartidoNotFoundException.class, () -> controller.obtenerPorId(99L));
    }

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

    @Test
    void obtenerCatalogoSelecciones_listaVacia_retornaOkVacio() {
        when(partidoService.obtenerCatalogoSelecciones()).thenReturn(List.of());

        ResponseEntity<List<PreferenciaDTO>> res = controller.obtenerCatalogoSelecciones();

        assertEquals(200, res.getStatusCode().value());
        assertTrue(res.getBody().isEmpty());
    }

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

    @Test
    void listarDesdeBD_listaVacia_retornaOkVacio() {
        when(partidoService.listarDesdeBD()).thenReturn(List.of());

        ResponseEntity<List<Partido>> res = controller.listarDesdeBD();

        assertEquals(200, res.getStatusCode().value());
        assertTrue(res.getBody().isEmpty());
    }
}