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
import org.springframework.web.client.RestClientException;
import java.time.LocalDateTime;
@ExtendWith(MockitoExtension.class)
@SuppressWarnings({ "rawtypes", "unchecked" })
class PartidoServiceImplTest {
private static final String CORREO_TEST = "test@test.com";
private static final String BRASIL = "Brasil";
private static final String ARGENTINA = "Argentina";
private static final String ESTADIO_AZTECA = "Estadio Azteca";
private static final String FECHA = "2026-06-11T20:00:00+00:00";
private static final String CIUDAD_MEXICO = "Ciudad de Mexico";
private static final String FECHA_PARTIDO = "2026-06-11";
private static final String GRUPO_A = "Group A";  
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
        usuario.setCorreoUsuario(CORREO_TEST);
        usuario.setSeleccionesU(new ArrayList<>());
        usuario.setPreferenciasu(new ArrayList<>());
        usuario.setCiudadFavoritas(new ArrayList<>());

        partidoDTO = construirPartidoDTO(1001L, BRASIL, ARGENTINA, ESTADIO_AZTECA,
                FECHA, "FT", GRUPO_A, 2, 1);
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
        @Test
void cuandoAPIFalla_retornaDesdeBD() {
    when(footballClient.get()).thenThrow(new RestClientException("API no disponible"));

    Partido p = new Partido();
    p.setId(1001L);
    p.setSeleccionLocal(BRASIL);
    p.setSeleccionVisitante(ARGENTINA);
    p.setEstadio(ESTADIO_AZTECA);
    p.setEstado("FT");
    p.setRonda(GRUPO_A);
    p.setGolesLocal(2);
    p.setGolesVisitante(1);
    p.setFecha(LocalDateTime.of(2026, 6, 11, 20, 0));
    when(partidoRepository.findAll()).thenReturn(List.of(p));

    List<PartidoDTO> resultado = partidoService.obtenerPartidos();

    assertEquals(1, resultado.size());
    assertEquals(BRASIL, resultado.get(0).getEquipos().getLocal().getNombre());
    assertEquals(ARGENTINA, resultado.get(0).getEquipos().getVisitante().getNombre());
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

            List<PartidoDTO> resultado = partidoService.obtenerPartidosPorFecha(FECHA_PARTIDO);

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
        @Test
void obtenerPartidosPorFecha_cuandoAPIFalla_retornaDesdeBD() {
    when(footballClient.get()).thenThrow(new RestClientException("API no disponible"));

    Partido coincide = new Partido();
    coincide.setId(1002L);
    coincide.setSeleccionLocal(BRASIL);
    coincide.setSeleccionVisitante(ARGENTINA);
    coincide.setEstadio(ESTADIO_AZTECA);
    coincide.setEstado("NS");
    coincide.setFecha(LocalDateTime.of(2026, 6, 11, 20, 0));

    Partido noCoincide = new Partido();
    noCoincide.setId(1005L);
    noCoincide.setEstado("NS");
    noCoincide.setFecha(LocalDateTime.of(2026, 6, 15, 18, 0));

    when(partidoRepository.findAll()).thenReturn(List.of(coincide, noCoincide));

    List<PartidoDTO> resultado = partidoService.obtenerPartidosPorFecha(FECHA_PARTIDO);

    assertEquals(1, resultado.size());
    assertEquals(BRASIL, resultado.get(0).getEquipos().getLocal().getNombre());
}

@Test
void obtenerPartidosEnVivo_cuandoAPIFalla_retornaEnVivoDesdeBD() {
    when(footballClient.get()).thenThrow(new RestClientException("API no disponible"));

    Partido vivo = new Partido();
    vivo.setId(1003L);
    vivo.setSeleccionLocal(BRASIL);
    vivo.setSeleccionVisitante(ARGENTINA);
    vivo.setEstadio(ESTADIO_AZTECA);
    vivo.setEstado("1H");
    vivo.setFecha(LocalDateTime.of(2026, 6, 11, 20, 0));

    Partido terminado = new Partido();
    terminado.setId(1004L);
    terminado.setEstado("FT");
    terminado.setFecha(LocalDateTime.of(2026, 6, 10, 18, 0));

    when(partidoRepository.findAll()).thenReturn(List.of(vivo, terminado));

    List<PartidoDTO> resultado = partidoService.obtenerPartidosEnVivo();

    assertEquals(1, resultado.size());
    assertEquals(BRASIL, resultado.get(0).getEquipos().getLocal().getNombre());
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

            int resultado = partidoService.sincronizarPorFechaYLiga(FECHA_PARTIDO, 1, 2026);

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

            int resultado = partidoService.sincronizarPorFechaYLiga(FECHA_PARTIDO, 1, 2026);

            assertEquals(1, resultado);
        }

        @Test
        void cuandoPartidoSinGoles_noEstableceMarcadores() {
            PartidoDTO sinGoles = construirPartidoDTO(2001L, BRASIL, ARGENTINA,
                    ESTADIO_AZTECA, FECHA, "NS", GRUPO_A, null, null);
            sinGoles.setGoles(null);

            PartidoResponseDTO response = new PartidoResponseDTO();
            response.setPartidos(List.of(sinGoles));

            prepararRestClient(response);
            when(partidoRepository.findById(2001L)).thenReturn(Optional.empty());

            int resultado = partidoService.sincronizarPorFechaYLiga(FECHA_PARTIDO, 1, 2026);

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
            sel.setNombre(BRASIL);
            usuario.setSeleccionesU(List.of(sel));

            PartidoResponseDTO response = new PartidoResponseDTO();
            response.setPartidos(List.of(partidoDTO));

            when(usuarioService.obtenerEntidadPorCorreo(CORREO_TEST)).thenReturn(usuario);
            prepararRestClient(response);

            List<PartidoDTO> resultado = partidoService.obtenerPartidosPorSeleccionesFav(CORREO_TEST);

            assertEquals(1, resultado.size());
        }

        @Test
        void cuandoNoTieneSelecciones_retornaListaVacia() {
            when(usuarioService.obtenerEntidadPorCorreo(CORREO_TEST)).thenReturn(usuario);

            List<PartidoDTO> resultado = partidoService.obtenerPartidosPorSeleccionesFav(CORREO_TEST);

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
            est.setNombre(ESTADIO_AZTECA);
            usuario.setPreferenciasu(List.of(est));

            PartidoResponseDTO response = new PartidoResponseDTO();
            response.setPartidos(List.of(partidoDTO));

            when(usuarioService.obtenerEntidadPorCorreo(CORREO_TEST)).thenReturn(usuario);
            prepararRestClient(response);

            List<PartidoDTO> resultado = partidoService.obtenerPartidosPorEstadiosFav(CORREO_TEST);

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

            when(usuarioService.obtenerEntidadPorCorreo(CORREO_TEST)).thenReturn(usuario);
            prepararRestClient(response);

            List<PartidoDTO> resultado = partidoService.obtenerPartidosPorEstadiosFav(CORREO_TEST);

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
            ciudad.setNombre(CIUDAD_MEXICO);
            usuario.setCiudadFavoritas(List.of(ciudad));

            PartidoResponseDTO response = new PartidoResponseDTO();
            response.setPartidos(List.of(partidoDTO));

            when(usuarioService.obtenerEntidadPorCorreo(CORREO_TEST)).thenReturn(usuario);
            prepararRestClient(response);

            List<PartidoDTO> resultado = partidoService.obtenerPartidosPorCiudadesFav(CORREO_TEST);

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

            when(usuarioService.obtenerEntidadPorCorreo(CORREO_TEST)).thenReturn(usuario);
            prepararRestClient(response);

            List<PartidoDTO> resultado = partidoService.obtenerPartidosPorCiudadesFav(CORREO_TEST);

            assertTrue(resultado.isEmpty());
        }
    }

    @Nested
    @DisplayName("filtrarPorSeleccion, filtrarPorEstadio, filtrarPorCiudad")
    class FiltrarBD {

        @Test
        void filtrarPorSeleccion_retornaPartidos() {
            Partido p = new Partido();
            when(partidoRepository.findBySeleccion(BRASIL)).thenReturn(List.of(p));

            List<Partido> resultado = partidoService.filtrarPorSeleccion(BRASIL);

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
            p.setEstadio(ESTADIO_AZTECA);
            when(partidoRepository.findAll()).thenReturn(List.of(p));

            List<Partido> resultado = partidoService.filtrarPorCiudad(CIUDAD_MEXICO);

            assertEquals(1, resultado.size());
        }

        @Test
        void filtrarPorCiudad_cuandoEstadioNoMapea_retornaListaVacia() {
            Partido p = new Partido();
            p.setEstadio("EstadioInexistente");
            when(partidoRepository.findAll()).thenReturn(List.of(p));

            List<Partido> resultado = partidoService.filtrarPorCiudad(CIUDAD_MEXICO);

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
            s.setNombre(BRASIL);
            when(seleccionRepository.findAll()).thenReturn(List.of(s));

            List<PreferenciaDTO> resultado = partidoService.obtenerCatalogoSelecciones();

            assertEquals(1, resultado.size());
            assertEquals(BRASIL, resultado.get(0).getNombre());
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
            p.setSeleccionLocal(BRASIL);
            p.setSeleccionVisitante(ARGENTINA);
            p.setEstadio(ESTADIO_AZTECA);
            p.setCapacidadDisponible(50000);
            p.setRonda(GRUPO_A);

            when(partidoRepository.findAll()).thenReturn(List.of(p));

            List<PartidoCapacidadDTO> resultado = partidoService.listarPartidosConCapacidad();

            assertEquals(1, resultado.size());
            assertEquals(CIUDAD_MEXICO, resultado.get(0).getCiudad());
            assertEquals(50000, resultado.get(0).getCapacidadDisponible());
        }

        @Test
        void cuandoCapacidadNull_usaValorPorDefecto60000() {
            Partido p = new Partido();
            p.setId(1L);
            p.setEstadio(ESTADIO_AZTECA);
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
            p.setSeleccionLocal(BRASIL);
            p.setSeleccionVisitante(ARGENTINA);
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