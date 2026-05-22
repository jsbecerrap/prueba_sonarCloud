package co.edu.unbosque.mundial_2026.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestClient;

import co.edu.unbosque.mundial_2026.dto.PartidoCapacidadDTO;
import co.edu.unbosque.mundial_2026.dto.response.EquipoConEstadioDTO;
import co.edu.unbosque.mundial_2026.dto.response.EquipoDTO;
import co.edu.unbosque.mundial_2026.dto.response.EquipoJugadorDTO;
import co.edu.unbosque.mundial_2026.dto.response.EquipoMundialDTO;
import co.edu.unbosque.mundial_2026.dto.response.EquipoMundialResponseDTO;
import co.edu.unbosque.mundial_2026.dto.response.EstadioDTO;
import co.edu.unbosque.mundial_2026.dto.response.EstadisticaGrupoDTO;
import co.edu.unbosque.mundial_2026.dto.response.EstadoDTO;
import co.edu.unbosque.mundial_2026.dto.response.InfoPartidoDTO;
import co.edu.unbosque.mundial_2026.dto.response.JugadorDTO;
import co.edu.unbosque.mundial_2026.dto.response.JugadorResponseDTO;
import co.edu.unbosque.mundial_2026.dto.response.LigaDTO;
import co.edu.unbosque.mundial_2026.dto.response.LigaStandingDTO;
import co.edu.unbosque.mundial_2026.dto.response.MarcadorDTO;
import co.edu.unbosque.mundial_2026.dto.response.PartidoDTO;
import co.edu.unbosque.mundial_2026.dto.response.PartidoResponseDTO;
import co.edu.unbosque.mundial_2026.dto.response.PosicionDTO;
import co.edu.unbosque.mundial_2026.dto.response.PreferenciaDTO;
import co.edu.unbosque.mundial_2026.dto.response.StandingResponseDTO;
import co.edu.unbosque.mundial_2026.entity.CiudadFavorita;
import co.edu.unbosque.mundial_2026.entity.EstadioFavorito;
import co.edu.unbosque.mundial_2026.entity.Partido;
import co.edu.unbosque.mundial_2026.entity.Seleccion;
import co.edu.unbosque.mundial_2026.entity.Usuario;
import co.edu.unbosque.mundial_2026.exception.PartidoNotFoundException;
import co.edu.unbosque.mundial_2026.repository.PartidoRepository;
import co.edu.unbosque.mundial_2026.repository.SeleccionRepository;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings({ "rawtypes", "unchecked" })
class PartidoServiceImplTest {

    @Mock
    private RestClient footballClient;

    @Mock
    private RestClient.RequestHeadersUriSpec uriSpec;

    @Mock
    private RestClient.RequestHeadersSpec headersSpec;

    @Mock
    private RestClient.ResponseSpec responseSpec;

    @Mock
    private PartidoRepository partidoRepository;

    @Mock
    private UsuarioService usuarioService;

    @Mock
    private SeleccionRepository seleccionRepository;

    @Mock
    private EventoAuditoriaService auditoriaService;

    @InjectMocks
    private PartidoServiceImpl partidoService;

    private Usuario usuario;
    private PartidoDTO partidoDTO;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setCorreoUsuario("test@test.com");
        usuario.setSeleccionesU(new ArrayList<>());
        usuario.setPreferenciasu(new ArrayList<>());
        usuario.setCiudadFavoritas(new ArrayList<>());

        partidoDTO = construirPartidoDTO(1001L, "Brasil", "Argentina", "Estadio Azteca",
                "2026-06-11T20:00:00+00:00", "FT", "Group A", 2, 1);
    }

    private PartidoDTO construirPartidoDTO(Long id, String local, String visitante,
            String estadio, String fecha, String estado, String ronda,
            Integer golesLocal, Integer golesVisitante) {
        PartidoDTO dto = new PartidoDTO();

        InfoPartidoDTO info = new InfoPartidoDTO();
        info.setId(id);
        info.setFecha(fecha);
        EstadoDTO est = new EstadoDTO();
        est.setCodigo(estado);
        info.setEstado(est);
        EstadioDTO estad = new EstadioDTO();
        estad.setNombre(estadio);
        info.setEstadio(estad);
        dto.setInformacion(info);

        LigaDTO liga = new LigaDTO();
        liga.setRonda(ronda);
        dto.setLiga(liga);

        EquipoDTO eqLocal = new EquipoDTO();
        eqLocal.setNombre(local);
        EquipoDTO eqVis = new EquipoDTO();
        eqVis.setNombre(visitante);
        EquipoConEstadioDTO equipos = new EquipoConEstadioDTO();
        equipos.setLocal(eqLocal);
        equipos.setVisitante(eqVis);
        dto.setEquipos(equipos);

        MarcadorDTO marcador = new MarcadorDTO();
        marcador.setLocal(golesLocal);
        marcador.setVisitante(golesVisitante);
        dto.setGoles(marcador);

        return dto;
    }

    private void prepararRestClient(Object responseBody) {
        when(footballClient.get()).thenReturn(uriSpec);
        when(uriSpec.uri(anyString())).thenReturn(headersSpec);
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(any(Class.class))).thenReturn(responseBody);
    }

    @Nested
    @DisplayName("obtenerPartidos")
    class ObtenerPartidos {

        @Test
        void retornaListaDeAPI() {
            PartidoResponseDTO response = new PartidoResponseDTO();
            response.setPartidos(List.of(partidoDTO));

            prepararRestClient(response);

            List<PartidoDTO> resultado = partidoService.obtenerPartidos();

            assertEquals(1, resultado.size());
        }
    }

    @Nested
    @DisplayName("obtenerPartidosPorEquipo")
    class ObtenerPartidosPorEquipo {

        @Test
        void retornaPartidosDeUnEquipo() {
            PartidoResponseDTO response = new PartidoResponseDTO();
            response.setPartidos(List.of(partidoDTO));

            prepararRestClient(response);

            List<PartidoDTO> resultado = partidoService.obtenerPartidosPorEquipo(10L);

            assertEquals(1, resultado.size());
        }
    }

    @Nested
    @DisplayName("obtenerPartidoPorId")
    class ObtenerPartidoPorId {

        @Test
        void cuandoExiste_retornaPartido() {
            PartidoResponseDTO response = new PartidoResponseDTO();
            response.setPartidos(List.of(partidoDTO));

            prepararRestClient(response);

            PartidoDTO resultado = partidoService.obtenerPartidoPorId(1001L);

            assertNotNull(resultado);
            assertEquals(1001L, resultado.getInformacion().getId());
        }

        @Test
        void cuandoListaVacia_lanzaPartidoNotFoundException() {
            PartidoResponseDTO response = new PartidoResponseDTO();
            response.setPartidos(Collections.emptyList());

            prepararRestClient(response);

            assertThrows(PartidoNotFoundException.class,
                    () -> partidoService.obtenerPartidoPorId(999L));
        }

        @Test
        void cuandoListaNula_lanzaPartidoNotFoundException() {
            PartidoResponseDTO response = new PartidoResponseDTO();
            response.setPartidos(null);

            prepararRestClient(response);

            assertThrows(PartidoNotFoundException.class,
                    () -> partidoService.obtenerPartidoPorId(999L));
        }
    }

    @Nested
    @DisplayName("obtenerStandings")
    class ObtenerStandings {

        @Test
        void retornaTablasDePosicion() {
            PosicionDTO posicion = new PosicionDTO();
            EstadisticaGrupoDTO grupo = new EstadisticaGrupoDTO();
            grupo.setTablas(List.of(List.of(posicion)));
            LigaStandingDTO liga = new LigaStandingDTO();
            liga.setTablas(grupo);
            StandingResponseDTO response = new StandingResponseDTO();
            response.setRespuesta(List.of(liga));

            prepararRestClient(response);

            List<List<PosicionDTO>> resultado = partidoService.obtenerStandings();

            assertEquals(1, resultado.size());
        }
    }

    @Nested
    @DisplayName("obtenerSelecciones")
    class ObtenerSelecciones {

        @Test
        void retornaSelecciones() {
            EquipoMundialDTO eq = new EquipoMundialDTO();
            EquipoMundialResponseDTO response = new EquipoMundialResponseDTO();
            response.setEquipos(List.of(eq));

            prepararRestClient(response);

            List<EquipoMundialDTO> resultado = partidoService.obtenerSelecciones();

            assertEquals(1, resultado.size());
        }
    }

    @Nested
    @DisplayName("obtenerJugadoresPorEquipo")
    class ObtenerJugadoresPorEquipo {

        @Test
        void retornaJugadoresDelEquipo() {
            JugadorDTO j = new JugadorDTO();
            EquipoJugadorDTO eqJug = new EquipoJugadorDTO();
            eqJug.setJugadores(List.of(j));
            JugadorResponseDTO response = new JugadorResponseDTO();
            response.setRespuesta(List.of(eqJug));

            prepararRestClient(response);

            List<JugadorDTO> resultado = partidoService.obtenerJugadoresPorEquipo(10L);

            assertEquals(1, resultado.size());
        }
    }

    @Nested
    @DisplayName("obtenerPartidosPorFecha y obtenerPartidosEnVivo")
    class ObtenerPartidosFiltros {

        @Test
        void obtenerPartidosPorFecha_retornaPartidos() {
            PartidoResponseDTO response = new PartidoResponseDTO();
            response.setPartidos(List.of(partidoDTO));

            prepararRestClient(response);

            List<PartidoDTO> resultado = partidoService.obtenerPartidosPorFecha("2026-06-11");

            assertEquals(1, resultado.size());
        }

        @Test
        void obtenerPartidosEnVivo_retornaPartidosLive() {
            PartidoResponseDTO response = new PartidoResponseDTO();
            response.setPartidos(List.of(partidoDTO));

            prepararRestClient(response);

            List<PartidoDTO> resultado = partidoService.obtenerPartidosEnVivo();

            assertEquals(1, resultado.size());
        }
    }

    @Nested
    @DisplayName("sincronizarPorFechaYLiga")
    class SincronizarPorFechaYLiga {

        @Test
        void cuandoTienePartidos_losGuardaYRetornaCantidad() {
            PartidoResponseDTO response = new PartidoResponseDTO();
            response.setPartidos(List.of(partidoDTO));

            prepararRestClient(response);
            when(partidoRepository.findById(1001L)).thenReturn(Optional.empty());

            int resultado = partidoService.sincronizarPorFechaYLiga("2026-06-11", 1, 2026);

            assertEquals(1, resultado);
            verify(partidoRepository).saveAll(any());
        }

        @Test
        void cuandoPartidoYaExiste_actualizaElExistente() {
            Partido existente = new Partido();
            existente.setId(1001L);

            PartidoResponseDTO response = new PartidoResponseDTO();
            response.setPartidos(List.of(partidoDTO));

            prepararRestClient(response);
            when(partidoRepository.findById(1001L)).thenReturn(Optional.of(existente));

            int resultado = partidoService.sincronizarPorFechaYLiga("2026-06-11", 1, 2026);

            assertEquals(1, resultado);
        }

        @Test
        void cuandoPartidoSinGoles_noEstableceMarcadores() {
            PartidoDTO sinGoles = construirPartidoDTO(2001L, "Brasil", "Argentina",
                    "Estadio Azteca", "2026-06-11T20:00:00+00:00", "NS", "Group A", null, null);
            sinGoles.setGoles(null);

            PartidoResponseDTO response = new PartidoResponseDTO();
            response.setPartidos(List.of(sinGoles));

            prepararRestClient(response);
            when(partidoRepository.findById(2001L)).thenReturn(Optional.empty());

            int resultado = partidoService.sincronizarPorFechaYLiga("2026-06-11", 1, 2026);

            assertEquals(1, resultado);
        }
    }

    @Nested
    @DisplayName("obtenerPartidosPorSeleccionesFav")
    class ObtenerPartidosPorSeleccionesFav {

        @Test
        void cuandoTieneSelecciones_retornaPartidos() {
            Seleccion sel = new Seleccion();
            sel.setId(10L);
            sel.setNombre("Brasil");
            usuario.setSeleccionesU(List.of(sel));

            PartidoResponseDTO response = new PartidoResponseDTO();
            response.setPartidos(List.of(partidoDTO));

            when(usuarioService.obtenerEntidadPorCorreo("test@test.com")).thenReturn(usuario);
            prepararRestClient(response);

            List<PartidoDTO> resultado = partidoService.obtenerPartidosPorSeleccionesFav("test@test.com");

            assertEquals(1, resultado.size());
        }

        @Test
        void cuandoNoTieneSelecciones_retornaListaVacia() {
            when(usuarioService.obtenerEntidadPorCorreo("test@test.com")).thenReturn(usuario);

            List<PartidoDTO> resultado = partidoService.obtenerPartidosPorSeleccionesFav("test@test.com");

            assertTrue(resultado.isEmpty());
        }
    }

    @Nested
    @DisplayName("obtenerPartidosPorEstadiosFav")
    class ObtenerPartidosPorEstadiosFav {

        @Test
        void filtraPartidosPorEstadioFavorito() {
            EstadioFavorito est = new EstadioFavorito();
            est.setId(20L);
            est.setNombre("Estadio Azteca");
            usuario.setPreferenciasu(List.of(est));

            PartidoResponseDTO response = new PartidoResponseDTO();
            response.setPartidos(List.of(partidoDTO));

            when(usuarioService.obtenerEntidadPorCorreo("test@test.com")).thenReturn(usuario);
            prepararRestClient(response);

            List<PartidoDTO> resultado = partidoService.obtenerPartidosPorEstadiosFav("test@test.com");

            assertEquals(1, resultado.size());
        }

        @Test
        void cuandoEstadioNoCoincide_retornaListaVacia() {
            EstadioFavorito est = new EstadioFavorito();
            est.setId(20L);
            est.setNombre("Otro Estadio");
            usuario.setPreferenciasu(List.of(est));

            PartidoResponseDTO response = new PartidoResponseDTO();
            response.setPartidos(List.of(partidoDTO));

            when(usuarioService.obtenerEntidadPorCorreo("test@test.com")).thenReturn(usuario);
            prepararRestClient(response);

            List<PartidoDTO> resultado = partidoService.obtenerPartidosPorEstadiosFav("test@test.com");

            assertTrue(resultado.isEmpty());
        }
    }

    @Nested
    @DisplayName("obtenerPartidosPorCiudadesFav")
    class ObtenerPartidosPorCiudadesFav {

        @Test
        void filtraPartidosPorCiudadFavorita() {
            CiudadFavorita ciudad = new CiudadFavorita();
            ciudad.setId(30L);
            ciudad.setNombre("Ciudad de Mexico");
            usuario.setCiudadFavoritas(List.of(ciudad));

            PartidoResponseDTO response = new PartidoResponseDTO();
            response.setPartidos(List.of(partidoDTO));

            when(usuarioService.obtenerEntidadPorCorreo("test@test.com")).thenReturn(usuario);
            prepararRestClient(response);

            List<PartidoDTO> resultado = partidoService.obtenerPartidosPorCiudadesFav("test@test.com");

            assertEquals(1, resultado.size());
        }

        @Test
        void cuandoCiudadNoMapea_retornaListaVacia() {
            CiudadFavorita ciudad = new CiudadFavorita();
            ciudad.setId(30L);
            ciudad.setNombre("CiudadInexistente");
            usuario.setCiudadFavoritas(List.of(ciudad));

            PartidoResponseDTO response = new PartidoResponseDTO();
            response.setPartidos(List.of(partidoDTO));

            when(usuarioService.obtenerEntidadPorCorreo("test@test.com")).thenReturn(usuario);
            prepararRestClient(response);

            List<PartidoDTO> resultado = partidoService.obtenerPartidosPorCiudadesFav("test@test.com");

            assertTrue(resultado.isEmpty());
        }
    }

    @Nested
    @DisplayName("filtrarPorSeleccion, filtrarPorEstadio, filtrarPorCiudad")
    class FiltrarBD {

        @Test
        void filtrarPorSeleccion_retornaPartidos() {
            Partido p = new Partido();
            when(partidoRepository.findBySeleccion("Brasil")).thenReturn(List.of(p));

            List<Partido> resultado = partidoService.filtrarPorSeleccion("Brasil");

            assertEquals(1, resultado.size());
        }

        @Test
        void filtrarPorEstadio_retornaPartidos() {
            Partido p = new Partido();
            when(partidoRepository.findByEstadio("Azteca")).thenReturn(List.of(p));

            List<Partido> resultado = partidoService.filtrarPorEstadio("Azteca");

            assertEquals(1, resultado.size());
        }

        @Test
        void filtrarPorCiudad_cuandoEstadioMapeaACiudad_retornaPartido() {
            Partido p = new Partido();
            p.setEstadio("Estadio Azteca");
            when(partidoRepository.findAll()).thenReturn(List.of(p));

            List<Partido> resultado = partidoService.filtrarPorCiudad("Ciudad de Mexico");

            assertEquals(1, resultado.size());
        }

        @Test
        void filtrarPorCiudad_cuandoEstadioNoMapea_retornaListaVacia() {
            Partido p = new Partido();
            p.setEstadio("EstadioInexistente");
            when(partidoRepository.findAll()).thenReturn(List.of(p));

            List<Partido> resultado = partidoService.filtrarPorCiudad("Ciudad de Mexico");

            assertTrue(resultado.isEmpty());
        }
    }

    @Nested
    @DisplayName("obtenerCatalogoSelecciones")
    class ObtenerCatalogoSelecciones {

        @Test
        void retornaTodasLasSelecciones() {
            Seleccion s = new Seleccion();
            s.setId(1L);
            s.setNombre("Brasil");
            when(seleccionRepository.findAll()).thenReturn(List.of(s));

            List<PreferenciaDTO> resultado = partidoService.obtenerCatalogoSelecciones();

            assertEquals(1, resultado.size());
            assertEquals("Brasil", resultado.get(0).getNombre());
        }
    }

    @Nested
    @DisplayName("obtenerPartidoEntidadPorId")
    class ObtenerPartidoEntidadPorId {

        @Test
        void cuandoExiste_retornaPartido() {
            Partido p = new Partido();
            p.setId(1L);
            when(partidoRepository.findById(1L)).thenReturn(Optional.of(p));

            Partido resultado = partidoService.obtenerPartidoEntidadPorId(1L);

            assertEquals(1L, resultado.getId());
        }

        @Test
        void cuandoNoExiste_lanzaPartidoNotFoundException() {
            when(partidoRepository.findById(99L)).thenReturn(Optional.empty());

            assertThrows(PartidoNotFoundException.class,
                    () -> partidoService.obtenerPartidoEntidadPorId(99L));
        }
    }

    @Nested
    @DisplayName("actualizarCapacidad")
    class ActualizarCapacidad {

        @Test
        void cuandoExiste_sumaLaCantidad() {
            Partido p = new Partido();
            p.setId(1L);
            p.setCapacidadDisponible(100);

            when(partidoRepository.findById(1L)).thenReturn(Optional.of(p));

            partidoService.actualizarCapacidad(1L, 50);

            assertEquals(150, p.getCapacidadDisponible());
            verify(partidoRepository).save(p);
        }

        @Test
        void cuandoExisteYRestaCantidad_resta() {
            Partido p = new Partido();
            p.setId(1L);
            p.setCapacidadDisponible(100);

            when(partidoRepository.findById(1L)).thenReturn(Optional.of(p));

            partidoService.actualizarCapacidad(1L, -30);

            assertEquals(70, p.getCapacidadDisponible());
        }

        @Test
        void cuandoNoExiste_lanzaPartidoNotFoundException() {
            when(partidoRepository.findById(99L)).thenReturn(Optional.empty());

            assertThrows(PartidoNotFoundException.class,
                    () -> partidoService.actualizarCapacidad(99L, 10));
        }
    }

    @Nested
    @DisplayName("listarPartidosConCapacidad")
    class ListarPartidosConCapacidad {

        @Test
        void retornaTodosConSusCapacidades() {
            Partido p = new Partido();
            p.setId(1L);
            p.setSeleccionLocal("Brasil");
            p.setSeleccionVisitante("Argentina");
            p.setEstadio("Estadio Azteca");
            p.setCapacidadDisponible(50000);
            p.setRonda("Group A");

            when(partidoRepository.findAll()).thenReturn(List.of(p));

            List<PartidoCapacidadDTO> resultado = partidoService.listarPartidosConCapacidad();

            assertEquals(1, resultado.size());
            assertEquals("Ciudad de Mexico", resultado.get(0).getCiudad());
            assertEquals(50000, resultado.get(0).getCapacidadDisponible());
        }

        @Test
        void cuandoCapacidadNull_usaValorPorDefecto60000() {
            Partido p = new Partido();
            p.setId(1L);
            p.setEstadio("Estadio Azteca");
            p.setCapacidadDisponible(null);

            when(partidoRepository.findAll()).thenReturn(List.of(p));

            List<PartidoCapacidadDTO> resultado = partidoService.listarPartidosConCapacidad();

            assertEquals(60000, resultado.get(0).getCapacidadDisponible());
        }

        @Test
        void cuandoEstadioNoMapeaACiudad_usaPorConfirmar() {
            Partido p = new Partido();
            p.setId(1L);
            p.setEstadio("Otro Estadio");
            p.setCapacidadDisponible(40000);

            when(partidoRepository.findAll()).thenReturn(List.of(p));

            List<PartidoCapacidadDTO> resultado = partidoService.listarPartidosConCapacidad();

            assertEquals("Por confirmar", resultado.get(0).getCiudad());
        }
    }

    @Nested
    @DisplayName("listarDesdeBD")
    class ListarDesdeBD {

        @Test
        void retornaTodosLosDeLaBD() {
            Partido p = new Partido();
            when(partidoRepository.findAll()).thenReturn(List.of(p));

            List<Partido> resultado = partidoService.listarDesdeBD();

            assertEquals(1, resultado.size());
        }
    }

    @Nested
    @DisplayName("actualizarResultado")
    class ActualizarResultado {

        @Test
        void cuandoExiste_actualizaGolesYRegistraAuditoria() {
            Partido p = new Partido();
            p.setId(1L);
            p.setSeleccionLocal("Brasil");
            p.setSeleccionVisitante("Argentina");
            p.setGolesLocal(0);
            p.setGolesVisitante(0);

            when(partidoRepository.findById(1L)).thenReturn(Optional.of(p));

            int resultado = partidoService.actualizarResultado(1L, 3, 1, 2);

            assertEquals(1, resultado);
            assertEquals(3, p.getGolesLocal());
            assertEquals(1, p.getGolesVisitante());
            assertEquals("2", p.getEstado());
            verify(auditoriaService).registrar(eq("PARTIDO_RESULTADO_ACTUALIZADO"), anyString(),
                    eq(null), anyString(), eq("Partido"));
        }

        @Test
        void cuandoNoExiste_lanzaPartidoNotFoundException() {
            when(partidoRepository.findById(99L)).thenReturn(Optional.empty());

            assertThrows(PartidoNotFoundException.class,
                    () -> partidoService.actualizarResultado(99L, 0, 0, 1));
        }
    }

    @Nested
    @DisplayName("sincronizarDesdeAPI")
    class SincronizarDesdeAPI {

        @Test
        void cuandoHayPartidos_losGuardaYRegistraAuditoria() {
            PartidoResponseDTO response = new PartidoResponseDTO();
            response.setPartidos(List.of(partidoDTO));

            prepararRestClient(response);
            when(partidoRepository.findById(1001L)).thenReturn(Optional.empty());

            int resultado = partidoService.sincronizarDesdeAPI();

            assertEquals(1, resultado);
            verify(partidoRepository).saveAll(any());
            verify(auditoriaService).registrar(eq("PARTIDOS_SINCRONIZADOS"), anyString(),
                    eq(null), anyString(), eq("Partido"));
        }

        @Test
        void cuandoResponseNula_retornaCero() {
            prepararRestClient(null);

            int resultado = partidoService.sincronizarDesdeAPI();

            assertEquals(0, resultado);
            verify(partidoRepository, never()).saveAll(any());
        }

        @Test
        void cuandoListaNula_retornaCero() {
            PartidoResponseDTO response = new PartidoResponseDTO();
            response.setPartidos(null);

            prepararRestClient(response);

            int resultado = partidoService.sincronizarDesdeAPI();

            assertEquals(0, resultado);
            verify(partidoRepository, never()).saveAll(any());
        }

        @Test
        void cuandoListaVacia_retornaCero() {
            PartidoResponseDTO response = new PartidoResponseDTO();
            response.setPartidos(Collections.emptyList());

            prepararRestClient(response);

            int resultado = partidoService.sincronizarDesdeAPI();

            assertEquals(0, resultado);
            verify(partidoRepository, never()).saveAll(any());
        }
    }
}