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

import java.time.LocalDateTime;

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

import co.edu.unbosque.mundial_2026.dto.ApuestaConParticipantesDTO;
import co.edu.unbosque.mundial_2026.dto.ApuestaDTO;
import co.edu.unbosque.mundial_2026.dto.ParticipacionDTO;
import co.edu.unbosque.mundial_2026.dto.PronosticoDTO;
import co.edu.unbosque.mundial_2026.dto.request.ApuestaRequestDTO;
import co.edu.unbosque.mundial_2026.dto.request.PronosticoRequestDTO;
import co.edu.unbosque.mundial_2026.entity.Apuesta;
import co.edu.unbosque.mundial_2026.entity.Participacion;
import co.edu.unbosque.mundial_2026.entity.Partido;
import co.edu.unbosque.mundial_2026.entity.Pronostico;
import co.edu.unbosque.mundial_2026.entity.Usuario;
import co.edu.unbosque.mundial_2026.exception.ApuestaCerradaException;
import co.edu.unbosque.mundial_2026.exception.ApuestaNotFoundException;
import co.edu.unbosque.mundial_2026.exception.CodigoInvalidoException;
import co.edu.unbosque.mundial_2026.exception.EstadoInvalidoException;
import co.edu.unbosque.mundial_2026.exception.ParticipacionNotFoundException;
import co.edu.unbosque.mundial_2026.exception.PartidoYaIniciadoException;
import co.edu.unbosque.mundial_2026.exception.PronosticoNotFoundException;
import co.edu.unbosque.mundial_2026.exception.UsuarioYaEnApuestaException;
import co.edu.unbosque.mundial_2026.repository.ApuestaRepository;
import co.edu.unbosque.mundial_2026.repository.ParticipacionRepository;
import co.edu.unbosque.mundial_2026.repository.PronosticoRepository;

@ExtendWith(MockitoExtension.class)
class ApuestaServiceImplTest {

    @Mock
    private ApuestaRepository apuestaRepository;

    @Mock
    private PronosticoRepository pronosticoRepository;

    @Mock
    private ParticipacionRepository participacionRepository;

    @Mock
    private UsuarioService usuarioService;

    @Mock
    private PartidoService partidoService;

    @Mock
    private NotificacionService notificacionService;

    @Mock
    private EventoAuditoriaService auditoriaService;

    @InjectMocks
    private ApuestaServiceImpl apuestaService;

    private Usuario usuario;
    private Apuesta apuesta;
    private Partido partido;
    private Participacion participacion;
    private static final String CORREO_TEST = "test@test.com";
private static final String LOCAL = "LOCAL";
private static final String CERRADA = "CERRADA";
private static final String ABIERTA = "ABIERTA";
private static final String POLLA_TEST = "Polla Test";
private static final String CODIGO = "CODE-001";
    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setCorreoUsuario(CORREO_TEST);
        usuario.setNombre("Juan");
        usuario.setApellido("Perez");

        apuesta = new Apuesta();
        apuesta.setId(10L);
        apuesta.setNombre(POLLA_TEST);
        apuesta.setEstado(ABIERTA);
        apuesta.setCodigoInvitacion(CODIGO);
        apuesta.setFechaCierre(LocalDateTime.now().plusDays(10));
        apuesta.setCreadaPor(usuario);

        partido = new Partido();
        partido.setId(100L);
        partido.setSeleccionLocal("Brasil");
        partido.setSeleccionVisitante("Argentina");
        partido.setFecha(LocalDateTime.now().plusDays(5));

        participacion = new Participacion();
        participacion.setId(1000L);
        participacion.setUsuario(usuario);
        participacion.setApuesta(apuesta);
        participacion.setPuntos(0);
    }

    @Nested
    @DisplayName("crearApuesta")
    class CrearApuesta {

        @Test
        void cuandoDatosValidos_creaApuestaYParticipacion() {
            ApuestaRequestDTO dto = new ApuestaRequestDTO();
            dto.setNombre("Mi Polla");
            dto.setFechaCierre(LocalDateTime.now().plusDays(5));
            dto.setUsuarioId(1L);

            when(usuarioService.obtenerEntidadPorId(1L)).thenReturn(usuario);
            when(apuestaRepository.save(any(Apuesta.class))).thenAnswer(inv -> {
                Apuesta a = inv.getArgument(0);
                a.setId(11L);
                return a;
            });

            ApuestaDTO resultado = apuestaService.crearApuesta(dto);

            assertNotNull(resultado);
            assertEquals("Mi Polla", resultado.getNombre());
            assertEquals(ABIERTA, resultado.getEstado());
            verify(participacionRepository).save(any(Participacion.class));
            verify(auditoriaService).registrar(eq("APUESTA_CREADA"), anyString(), eq(1L), anyString(), eq("Apuesta"));
        }

        @Test
        void cuandoFechaEsPasada_lanzaApuestaCerradaException() {
            ApuestaRequestDTO dto = new ApuestaRequestDTO();
            dto.setNombre("Polla Pasada");
            dto.setFechaCierre(LocalDateTime.now().minusDays(1));
            dto.setUsuarioId(1L);

            when(usuarioService.obtenerEntidadPorId(1L)).thenReturn(usuario);

            assertThrows(ApuestaCerradaException.class, () -> apuestaService.crearApuesta(dto));
            verify(apuestaRepository, never()).save(any());
        }

        @Test
        void cuandoFechaEsNull_creaSinValidarFecha() {
            ApuestaRequestDTO dto = new ApuestaRequestDTO();
            dto.setNombre("Polla Sin Fecha");
            dto.setFechaCierre(null);
            dto.setUsuarioId(1L);

            when(usuarioService.obtenerEntidadPorId(1L)).thenReturn(usuario);
            when(apuestaRepository.save(any(Apuesta.class))).thenAnswer(inv -> inv.getArgument(0));

            ApuestaDTO resultado = apuestaService.crearApuesta(dto);

            assertNotNull(resultado);
        }
    }

    @Nested
    @DisplayName("registrarPronostico")
    class RegistrarPronostico {

        @Test
        void cuandoDatosValidos_registraPronostico() {
            PronosticoRequestDTO dto = new PronosticoRequestDTO();
            dto.setUsuarioId(1L);
            dto.setApuestaId(10L);
            dto.setPartidoId(100L);
            dto.setResultadoPronosticado(LOCAL);
            dto.setGolesLocalPronosticados(2);
            dto.setGolesVisitantePronosticados(1);

            when(usuarioService.obtenerEntidadPorId(1L)).thenReturn(usuario);
            when(apuestaRepository.findById(10L)).thenReturn(Optional.of(apuesta));
            when(participacionRepository.findByUsuarioIdAndApuestaId(1L, 10L))
                    .thenReturn(Optional.of(participacion));
            when(partidoService.obtenerPartidoEntidadPorId(100L)).thenReturn(partido);
            when(pronosticoRepository.save(any(Pronostico.class))).thenAnswer(inv -> {
                Pronostico p = inv.getArgument(0);
                p.setId(500L);
                return p;
            });

            PronosticoDTO resultado = apuestaService.registrarPronostico(dto);

            assertNotNull(resultado);
            assertEquals(LOCAL, resultado.getResultadoPronosticado());
            verify(auditoriaService).registrar(eq("PRONOSTICO_REGISTRADO"), anyString(), eq(1L), anyString(),
                    eq("Pronostico"));
        }

        @Test
        void cuandoApuestaNoExiste_lanzaApuestaNotFoundException() {
            PronosticoRequestDTO dto = new PronosticoRequestDTO();
            dto.setUsuarioId(1L);
            dto.setApuestaId(99L);

            when(usuarioService.obtenerEntidadPorId(1L)).thenReturn(usuario);
            when(apuestaRepository.findById(99L)).thenReturn(Optional.empty());

            assertThrows(ApuestaNotFoundException.class, () -> apuestaService.registrarPronostico(dto));
        }

        @Test
        void cuandoApuestaCerrada_lanzaApuestaCerradaException() {
            apuesta.setEstado(CERRADA);

            PronosticoRequestDTO dto = new PronosticoRequestDTO();
            dto.setUsuarioId(1L);
            dto.setApuestaId(10L);

            when(usuarioService.obtenerEntidadPorId(1L)).thenReturn(usuario);
            when(apuestaRepository.findById(10L)).thenReturn(Optional.of(apuesta));

            assertThrows(ApuestaCerradaException.class, () -> apuestaService.registrarPronostico(dto));
        }

        @Test
        void cuandoUsuarioNoEsParticipante_lanzaParticipacionNotFoundException() {
            PronosticoRequestDTO dto = new PronosticoRequestDTO();
            dto.setUsuarioId(1L);
            dto.setApuestaId(10L);

            when(usuarioService.obtenerEntidadPorId(1L)).thenReturn(usuario);
            when(apuestaRepository.findById(10L)).thenReturn(Optional.of(apuesta));
            when(participacionRepository.findByUsuarioIdAndApuestaId(1L, 10L))
                    .thenReturn(Optional.empty());

            assertThrows(ParticipacionNotFoundException.class,
                    () -> apuestaService.registrarPronostico(dto));
        }

        @Test
        void cuandoPartidoYaIniciado_lanzaPartidoYaIniciadoException() {
            partido.setFecha(LocalDateTime.now().plusMinutes(2));

            PronosticoRequestDTO dto = new PronosticoRequestDTO();
            dto.setUsuarioId(1L);
            dto.setApuestaId(10L);
            dto.setPartidoId(100L);

            when(usuarioService.obtenerEntidadPorId(1L)).thenReturn(usuario);
            when(apuestaRepository.findById(10L)).thenReturn(Optional.of(apuesta));
            when(participacionRepository.findByUsuarioIdAndApuestaId(1L, 10L))
                    .thenReturn(Optional.of(participacion));
            when(partidoService.obtenerPartidoEntidadPorId(100L)).thenReturn(partido);

            assertThrows(PartidoYaIniciadoException.class,
                    () -> apuestaService.registrarPronostico(dto));
        }
    }

    @Nested
    @DisplayName("unirseApuesta")
    class UnirseApuesta {

        @Test
        void cuandoCodigoValidoYUsuarioNoEsta_seUne() {
            when(usuarioService.obtenerEntidadPorId(1L)).thenReturn(usuario);
            when(apuestaRepository.findByCodigoInvitacion(CODIGO)).thenReturn(Optional.of(apuesta));
            when(participacionRepository.findByUsuarioIdAndApuestaId(1L, 10L))
                    .thenReturn(Optional.empty());

            ApuestaDTO resultado = apuestaService.unirseApuesta(CODIGO, 1L);

            assertNotNull(resultado);
            assertEquals(POLLA_TEST, resultado.getNombre());
            verify(participacionRepository).save(any(Participacion.class));
           verify(notificacionService).notificarApuestaUnirse(usuario, usuario, POLLA_TEST);
            verify(auditoriaService).registrar(eq("APUESTA_UNIRSE"), anyString(), eq(1L), anyString(), eq("Apuesta"));
        }

        @Test
        void cuandoCodigoNoExiste_lanzaCodigoInvalidoException() {
            when(usuarioService.obtenerEntidadPorId(1L)).thenReturn(usuario);
            when(apuestaRepository.findByCodigoInvitacion("MAL")).thenReturn(Optional.empty());

            assertThrows(CodigoInvalidoException.class,
                    () -> apuestaService.unirseApuesta("MAL", 1L));
        }

        @Test
        void cuandoUsuarioYaEstaEnLaApuesta_lanzaUsuarioYaEnApuestaException() {
            when(usuarioService.obtenerEntidadPorId(1L)).thenReturn(usuario);
            when(apuestaRepository.findByCodigoInvitacion(CODIGO)).thenReturn(Optional.of(apuesta));
            when(participacionRepository.findByUsuarioIdAndApuestaId(1L, 10L))
                    .thenReturn(Optional.of(participacion));

            assertThrows(UsuarioYaEnApuestaException.class,
                    () -> apuestaService.unirseApuesta(CODIGO, 1L));
        }
    }

    @Nested
    @DisplayName("editarPronostico")
    class EditarPronostico {

        @Test
        void cuandoEsDelUsuarioYApuestaAbierta_editaPronostico() {
            Pronostico pronostico = construirPronostico();

            PronosticoRequestDTO dto = new PronosticoRequestDTO();
            dto.setResultadoPronosticado("VISITANTE");
            dto.setGolesLocalPronosticados(0);
            dto.setGolesVisitantePronosticados(2);

            when(pronosticoRepository.findById(500L)).thenReturn(Optional.of(pronostico));
            when(pronosticoRepository.save(any(Pronostico.class))).thenReturn(pronostico);

            PronosticoDTO resultado = apuestaService.editarPronostico(500L, dto, CORREO_TEST);

            assertNotNull(resultado);
            assertEquals(0, pronostico.getGolesLocalPronosticados());
            assertEquals(2, pronostico.getGolesVisitantePronosticados());
            assertEquals("VISITANTE", pronostico.getResultadoPronosticado());
        }

        @Test
        void cuandoResultadoEnDTOEsNull_mantieneElAnterior() {
            Pronostico pronostico = construirPronostico();

            PronosticoRequestDTO dto = new PronosticoRequestDTO();
            dto.setResultadoPronosticado(null);
            dto.setGolesLocalPronosticados(3);
            dto.setGolesVisitantePronosticados(1);

            when(pronosticoRepository.findById(500L)).thenReturn(Optional.of(pronostico));
            when(pronosticoRepository.save(any(Pronostico.class))).thenReturn(pronostico);

            apuestaService.editarPronostico(500L, dto, CORREO_TEST);

            assertEquals(LOCAL, pronostico.getResultadoPronosticado());
        }

        @Test
        void cuandoNoExiste_lanzaPronosticoNotFoundException() {
            PronosticoRequestDTO dto = new PronosticoRequestDTO();
            when(pronosticoRepository.findById(999L)).thenReturn(Optional.empty());

            assertThrows(PronosticoNotFoundException.class,
                    () -> apuestaService.editarPronostico(999L, dto, CORREO_TEST));
        }

        @Test
        void cuandoNoEsDelUsuario_lanzaApuestaCerradaException() {
            Pronostico pronostico = construirPronostico();

            PronosticoRequestDTO dto = new PronosticoRequestDTO();

            when(pronosticoRepository.findById(500L)).thenReturn(Optional.of(pronostico));

            assertThrows(ApuestaCerradaException.class,
                    () -> apuestaService.editarPronostico(500L, dto, "otro@test.com"));
        }

        @Test
        void cuandoApuestaCerrada_lanzaApuestaCerradaException() {
            Pronostico pronostico = construirPronostico();
            apuesta.setEstado(CERRADA);

            PronosticoRequestDTO dto = new PronosticoRequestDTO();

            when(pronosticoRepository.findById(500L)).thenReturn(Optional.of(pronostico));

            assertThrows(ApuestaCerradaException.class,
                    () -> apuestaService.editarPronostico(500L, dto, CORREO_TEST));
        }
    }

    @Nested
    @DisplayName("eliminarPronostico")
    class EliminarPronostico {

        @Test
        void cuandoEsDelUsuarioYApuestaAbierta_eliminaPronostico() {
            Pronostico pronostico = construirPronostico();
            when(pronosticoRepository.findById(500L)).thenReturn(Optional.of(pronostico));

            apuestaService.eliminarPronostico(500L, CORREO_TEST);

            verify(pronosticoRepository).delete(pronostico);
            verify(auditoriaService).registrar(eq("PRONOSTICO_ELIMINADO"), anyString(), eq(1L), anyString(),
                    eq("Pronostico"));
        }

        @Test
        void cuandoNoExiste_lanzaPronosticoNotFoundException() {
            when(pronosticoRepository.findById(999L)).thenReturn(Optional.empty());

            assertThrows(PronosticoNotFoundException.class,
                    () -> apuestaService.eliminarPronostico(999L, CORREO_TEST));
        }

        @Test
        void cuandoNoEsDelUsuario_lanzaApuestaCerradaException() {
            Pronostico pronostico = construirPronostico();
            when(pronosticoRepository.findById(500L)).thenReturn(Optional.of(pronostico));

            assertThrows(ApuestaCerradaException.class,
                    () -> apuestaService.eliminarPronostico(500L, "otro@test.com"));
        }

        @Test
        void cuandoApuestaCerrada_lanzaApuestaCerradaException() {
            Pronostico pronostico = construirPronostico();
            apuesta.setEstado(CERRADA);
            when(pronosticoRepository.findById(500L)).thenReturn(Optional.of(pronostico));

            assertThrows(ApuestaCerradaException.class,
                    () -> apuestaService.eliminarPronostico(500L, CORREO_TEST));
        }
    }

    @Nested
    @DisplayName("obtenerRanking")
    class ObtenerRanking {

        @Test
        void retornaParticipacionesOrdenadasPorPuntos() {
            participacion.setPuntos(20);
            participacion.setPosicionRanking(1);

            when(participacionRepository.findByApuestaIdOrderByPuntosDesc(10L))
                    .thenReturn(List.of(participacion));

            List<ParticipacionDTO> resultado = apuestaService.obtenerRanking(10L);

            assertEquals(1, resultado.size());
            assertEquals(20, resultado.get(0).getPuntos());
        }
    }

    @Nested
    @DisplayName("calcularPuntos")
    class CalcularPuntos {

        @Test
        void cuandoApuestaNoExiste_lanzaApuestaNotFoundException() {
            when(apuestaRepository.findById(99L)).thenReturn(Optional.empty());

            assertThrows(ApuestaNotFoundException.class, () -> apuestaService.calcularPuntos(99L));
        }

        @Test
        void cuandoApuestaNoCerrada_lanzaEstadoInvalidoException() {
            when(apuestaRepository.findById(10L)).thenReturn(Optional.of(apuesta));

            assertThrows(EstadoInvalidoException.class, () -> apuestaService.calcularPuntos(10L));
        }

        @Test
        void cuandoAciertaResultadoExacto_otorga6Puntos() {
            apuesta.setEstado(CERRADA);
            partido.setGolesLocal(2);
            partido.setGolesVisitante(1);
            partido.setFecha(LocalDateTime.now().minusDays(1));

            Pronostico pronostico = new Pronostico();
            pronostico.setId(500L);
            pronostico.setApuesta(apuesta);
            pronostico.setUsuario(usuario);
            pronostico.setPartido(partido);
            pronostico.setResultadoPronosticado(LOCAL);
            pronostico.setGolesLocalPronosticados(2);
            pronostico.setGolesVisitantePronosticados(1);
            pronostico.setPuntosObtenidos(0);

            when(apuestaRepository.findById(10L)).thenReturn(Optional.of(apuesta));
            when(participacionRepository.findByApuestaId(10L)).thenReturn(List.of(participacion));
            when(pronosticoRepository.findByApuestaId(10L)).thenReturn(List.of(pronostico));
            when(participacionRepository.findByUsuarioIdAndApuestaId(1L, 10L))
                    .thenReturn(Optional.of(participacion));
            when(participacionRepository.findByApuestaIdOrderByPuntosDesc(10L))
                    .thenReturn(List.of(participacion));

            List<PronosticoDTO> resultado = apuestaService.calcularPuntos(10L);

            assertEquals(1, resultado.size());
            assertEquals(6, pronostico.getPuntosObtenidos());
            verify(apuestaRepository).save(apuesta);
            assertTrue(apuesta.isPuntosCalculados());
            verify(auditoriaService).registrar(eq("APUESTA_FINALIZADA"), anyString(), eq(null), anyString(),
                    eq("Apuesta"));
        }

        @Test
        void cuandoSoloAciertaResultado_otorga2Puntos() {
            apuesta.setEstado(CERRADA);
            partido.setGolesLocal(3);
            partido.setGolesVisitante(0);
            partido.setFecha(LocalDateTime.now().minusDays(1));

            Pronostico pronostico = new Pronostico();
            pronostico.setId(501L);
            pronostico.setApuesta(apuesta);
            pronostico.setUsuario(usuario);
            pronostico.setPartido(partido);
            pronostico.setResultadoPronosticado(LOCAL);
            pronostico.setGolesLocalPronosticados(1);
            pronostico.setGolesVisitantePronosticados(0);
            pronostico.setPuntosObtenidos(0);

            when(apuestaRepository.findById(10L)).thenReturn(Optional.of(apuesta));
            when(participacionRepository.findByApuestaId(10L)).thenReturn(List.of(participacion));
            when(pronosticoRepository.findByApuestaId(10L)).thenReturn(List.of(pronostico));
            when(participacionRepository.findByUsuarioIdAndApuestaId(1L, 10L))
                    .thenReturn(Optional.of(participacion));
            when(participacionRepository.findByApuestaIdOrderByPuntosDesc(10L))
                    .thenReturn(List.of(participacion));

            apuestaService.calcularPuntos(10L);

            assertEquals(2, pronostico.getPuntosObtenidos());
        }

        @Test
        void cuandoSoloAciertaTotalGoles_otorga1Punto() {
            apuesta.setEstado(CERRADA);
            partido.setGolesLocal(2);
            partido.setGolesVisitante(2);
            partido.setFecha(LocalDateTime.now().minusDays(1));

            Pronostico pronostico = new Pronostico();
            pronostico.setId(502L);
            pronostico.setApuesta(apuesta);
            pronostico.setUsuario(usuario);
            pronostico.setPartido(partido);
            pronostico.setResultadoPronosticado(LOCAL);
            pronostico.setGolesLocalPronosticados(3);
            pronostico.setGolesVisitantePronosticados(1);
            pronostico.setPuntosObtenidos(0);

            when(apuestaRepository.findById(10L)).thenReturn(Optional.of(apuesta));
            when(participacionRepository.findByApuestaId(10L)).thenReturn(List.of(participacion));
            when(pronosticoRepository.findByApuestaId(10L)).thenReturn(List.of(pronostico));
            when(participacionRepository.findByUsuarioIdAndApuestaId(1L, 10L))
                    .thenReturn(Optional.of(participacion));
            when(participacionRepository.findByApuestaIdOrderByPuntosDesc(10L))
                    .thenReturn(List.of(participacion));

            apuestaService.calcularPuntos(10L);

            assertEquals(1, pronostico.getPuntosObtenidos());
        }

        @Test
        void cuandoPartidoVisitanteGana_resultadoEsVisitante() {
            apuesta.setEstado(CERRADA);
            partido.setGolesLocal(0);
            partido.setGolesVisitante(2);
            partido.setFecha(LocalDateTime.now().minusDays(1));

            Pronostico pronostico = new Pronostico();
            pronostico.setId(503L);
            pronostico.setApuesta(apuesta);
            pronostico.setUsuario(usuario);
            pronostico.setPartido(partido);
            pronostico.setResultadoPronosticado("VISITANTE");
            pronostico.setGolesLocalPronosticados(0);
            pronostico.setGolesVisitantePronosticados(2);
            pronostico.setPuntosObtenidos(0);

            when(apuestaRepository.findById(10L)).thenReturn(Optional.of(apuesta));
            when(participacionRepository.findByApuestaId(10L)).thenReturn(List.of(participacion));
            when(pronosticoRepository.findByApuestaId(10L)).thenReturn(List.of(pronostico));
            when(participacionRepository.findByUsuarioIdAndApuestaId(1L, 10L))
                    .thenReturn(Optional.of(participacion));
            when(participacionRepository.findByApuestaIdOrderByPuntosDesc(10L))
                    .thenReturn(List.of(participacion));

            apuestaService.calcularPuntos(10L);

            assertEquals(6, pronostico.getPuntosObtenidos());
        }

        @Test
        void cuandoPartidoEmpate_resultadoEsEmpate() {
            apuesta.setEstado(CERRADA);
            partido.setGolesLocal(1);
            partido.setGolesVisitante(1);
            partido.setFecha(LocalDateTime.now().minusDays(1));

            Pronostico pronostico = new Pronostico();
            pronostico.setId(504L);
            pronostico.setApuesta(apuesta);
            pronostico.setUsuario(usuario);
            pronostico.setPartido(partido);
            pronostico.setResultadoPronosticado("EMPATE");
            pronostico.setGolesLocalPronosticados(1);
            pronostico.setGolesVisitantePronosticados(1);
            pronostico.setPuntosObtenidos(0);

            when(apuestaRepository.findById(10L)).thenReturn(Optional.of(apuesta));
            when(participacionRepository.findByApuestaId(10L)).thenReturn(List.of(participacion));
            when(pronosticoRepository.findByApuestaId(10L)).thenReturn(List.of(pronostico));
            when(participacionRepository.findByUsuarioIdAndApuestaId(1L, 10L))
                    .thenReturn(Optional.of(participacion));
            when(participacionRepository.findByApuestaIdOrderByPuntosDesc(10L))
                    .thenReturn(List.of(participacion));

            apuestaService.calcularPuntos(10L);

            assertEquals(6, pronostico.getPuntosObtenidos());
        }

        @Test
        void cuandoPartidoSinGoles_omitePronostico() {
            apuesta.setEstado(CERRADA);
            partido.setGolesLocal(null);
            partido.setGolesVisitante(null);
            partido.setFecha(LocalDateTime.now().minusDays(1));

            Pronostico pronostico = new Pronostico();
            pronostico.setId(505L);
            pronostico.setApuesta(apuesta);
            pronostico.setUsuario(usuario);
            pronostico.setPartido(partido);
            pronostico.setResultadoPronosticado(LOCAL);
            pronostico.setGolesLocalPronosticados(2);
            pronostico.setGolesVisitantePronosticados(1);
            pronostico.setPuntosObtenidos(0);

            when(apuestaRepository.findById(10L)).thenReturn(Optional.of(apuesta));
            when(participacionRepository.findByApuestaId(10L)).thenReturn(List.of(participacion));
            when(pronosticoRepository.findByApuestaId(10L)).thenReturn(List.of(pronostico));
            when(participacionRepository.findByApuestaIdOrderByPuntosDesc(10L))
                    .thenReturn(List.of(participacion));

            List<PronosticoDTO> resultado = apuestaService.calcularPuntos(10L);

            assertTrue(resultado.isEmpty());
            assertEquals(0, pronostico.getPuntosObtenidos());
        }

        @Test
        void cuandoParticipacionNoExiste_lanzaParticipacionNotFoundException() {
            apuesta.setEstado(CERRADA);
            partido.setGolesLocal(2);
            partido.setGolesVisitante(1);
            partido.setFecha(LocalDateTime.now().minusDays(1));

            Pronostico pronostico = new Pronostico();
            pronostico.setId(506L);
            pronostico.setApuesta(apuesta);
            pronostico.setUsuario(usuario);
            pronostico.setPartido(partido);
            pronostico.setResultadoPronosticado(LOCAL);
            pronostico.setGolesLocalPronosticados(2);
            pronostico.setGolesVisitantePronosticados(1);
            pronostico.setPuntosObtenidos(0);

            when(apuestaRepository.findById(10L)).thenReturn(Optional.of(apuesta));
            when(participacionRepository.findByApuestaId(10L)).thenReturn(List.of(participacion));
            when(pronosticoRepository.findByApuestaId(10L)).thenReturn(List.of(pronostico));
            when(participacionRepository.findByUsuarioIdAndApuestaId(1L, 10L))
                    .thenReturn(Optional.empty());

            assertThrows(ParticipacionNotFoundException.class,
                    () -> apuestaService.calcularPuntos(10L));
        }
    }

    @Nested
    @DisplayName("cerrarApuesta")
    class CerrarApuesta {

        @Test
        void cuandoEstaAbierta_laCierra() {
            when(apuestaRepository.findById(10L)).thenReturn(Optional.of(apuesta));
            when(apuestaRepository.save(any(Apuesta.class))).thenReturn(apuesta);

            ApuestaDTO resultado = apuestaService.cerrarApuesta(10L);

            assertEquals(CERRADA, resultado.getEstado());
            assertEquals(CERRADA, apuesta.getEstado());
        }

        @Test
        void cuandoNoExiste_lanzaApuestaNotFoundException() {
            when(apuestaRepository.findById(99L)).thenReturn(Optional.empty());

            assertThrows(ApuestaNotFoundException.class, () -> apuestaService.cerrarApuesta(99L));
        }

        @Test
        void cuandoYaCerrada_lanzaApuestaCerradaException() {
            apuesta.setEstado(CERRADA);
            when(apuestaRepository.findById(10L)).thenReturn(Optional.of(apuesta));

            assertThrows(ApuestaCerradaException.class, () -> apuestaService.cerrarApuesta(10L));
        }
    }

    @Nested
    @DisplayName("obtenerApuesta")
    class ObtenerApuesta {

        @Test
        void cuandoExiste_retornaDTO() {
            when(apuestaRepository.findById(10L)).thenReturn(Optional.of(apuesta));

            ApuestaDTO resultado = apuestaService.obtenerApuesta(10L);

            assertEquals(10L, resultado.getId());
            assertEquals(POLLA_TEST, resultado.getNombre());
        }

        @Test
        void cuandoNoExiste_lanzaApuestaNotFoundException() {
            when(apuestaRepository.findById(99L)).thenReturn(Optional.empty());

            assertThrows(ApuestaNotFoundException.class, () -> apuestaService.obtenerApuesta(99L));
        }
    }

    @Nested
    @DisplayName("listarParticipantes")
    class ListarParticipantes {

        @Test
        void retornaParticipantesDeLaApuesta() {
            when(participacionRepository.findByApuestaId(10L)).thenReturn(List.of(participacion));

            List<ParticipacionDTO> resultado = apuestaService.listarParticipantes(10L);

            assertEquals(1, resultado.size());
        }
    }

    @Nested
    @DisplayName("listarApuestasPorUsuario")
    class ListarApuestasPorUsuario {

        @Test
        void retornaApuestasDelUsuario() {
            when(participacionRepository.findByUsuarioId(1L)).thenReturn(List.of(participacion));

            List<ApuestaDTO> resultado = apuestaService.listarApuestasPorUsuario(1L);

            assertEquals(1, resultado.size());
            assertEquals(POLLA_TEST, resultado.get(0).getNombre());
        }

        @Test
        void cuandoNoTiene_retornaListaVacia() {
            when(participacionRepository.findByUsuarioId(1L)).thenReturn(Collections.emptyList());

            List<ApuestaDTO> resultado = apuestaService.listarApuestasPorUsuario(1L);

            assertTrue(resultado.isEmpty());
        }
    }

    @Nested
    @DisplayName("verificarPronostico")
    class VerificarPronostico {

        @Test
        void cuandoExiste_retornaDTO() {
            Pronostico pronostico = construirPronostico();
            when(pronosticoRepository.findById(500L)).thenReturn(Optional.of(pronostico));

            PronosticoDTO resultado = apuestaService.verificarPronostico(500L);

            assertNotNull(resultado);
            assertEquals(500L, resultado.getId());
        }

        @Test
        void cuandoNoExiste_lanzaPronosticoNotFoundException() {
            when(pronosticoRepository.findById(999L)).thenReturn(Optional.empty());

            assertThrows(PronosticoNotFoundException.class,
                    () -> apuestaService.verificarPronostico(999L));
        }
    }

    @Nested
    @DisplayName("calcularPuntosAutomatico")
    class CalcularPuntosAutomatico {

        @Test
        void cuandoHayApuestasCerradasSinCalcular_lasCalcula() {
            apuesta.setEstado(CERRADA);

            when(apuestaRepository.findByEstadoAndPuntosCalculadosFalse(CERRADA))
                    .thenReturn(List.of(apuesta));
            when(apuestaRepository.findById(10L)).thenReturn(Optional.of(apuesta));
            when(participacionRepository.findByApuestaId(10L)).thenReturn(Collections.emptyList());
            when(pronosticoRepository.findByApuestaId(10L)).thenReturn(Collections.emptyList());
            when(participacionRepository.findByApuestaIdOrderByPuntosDesc(10L))
                    .thenReturn(Collections.emptyList());

            apuestaService.calcularPuntosAutomatico();

            verify(apuestaRepository).save(apuesta);
        }

        @Test
        void cuandoNoHayApuestas_noHaceNada() {
            when(apuestaRepository.findByEstadoAndPuntosCalculadosFalse(CERRADA))
                    .thenReturn(Collections.emptyList());

            apuestaService.calcularPuntosAutomatico();

            verify(apuestaRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("cerrarApuestasVencidas")
    class CerrarApuestasVencidas {

        @Test
        void cuandoHayVencidas_lasCierra() {
            when(apuestaRepository.findByEstadoAndFechaCierreBefore(eq(ABIERTA), any(LocalDateTime.class)))
                    .thenReturn(List.of(apuesta));
            when(apuestaRepository.findById(10L)).thenReturn(Optional.of(apuesta));
            when(apuestaRepository.save(any(Apuesta.class))).thenReturn(apuesta);

            apuestaService.cerrarApuestasVencidas();

            assertEquals(CERRADA, apuesta.getEstado());
        }

        @Test
        void cuandoNoHayVencidas_noHaceNada() {
            when(apuestaRepository.findByEstadoAndFechaCierreBefore(eq(ABIERTA), any(LocalDateTime.class)))
                    .thenReturn(Collections.emptyList());

            apuestaService.cerrarApuestasVencidas();

            verify(apuestaRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("misPronosticos")
    class MisPronosticos {

        @Test
        void retornaPronosticosDelUsuario() {
            Pronostico p = construirPronostico();
            when(pronosticoRepository.findByApuestaIdAndUsuarioId(10L, 1L)).thenReturn(List.of(p));

            List<PronosticoDTO> resultado = apuestaService.misPronosticos(10L, 1L);

            assertEquals(1, resultado.size());
        }
    }

    @Nested
    @DisplayName("calcularPuntosParciales")
    class CalcularPuntosParciales {

        @Test
        void cuandoApuestaNoExiste_lanzaApuestaNotFoundException() {
            when(apuestaRepository.findById(99L)).thenReturn(Optional.empty());

            assertThrows(ApuestaNotFoundException.class,
                    () -> apuestaService.calcularPuntosParciales(99L));
        }

        @Test
        void cuandoPartidoConGoles_calculaYActualizaParticipacion() {
            partido.setGolesLocal(2);
            partido.setGolesVisitante(1);

            Pronostico pronostico = new Pronostico();
            pronostico.setId(500L);
            pronostico.setApuesta(apuesta);
            pronostico.setUsuario(usuario);
            pronostico.setPartido(partido);
            pronostico.setResultadoPronosticado(LOCAL);
            pronostico.setGolesLocalPronosticados(2);
            pronostico.setGolesVisitantePronosticados(1);
            pronostico.setPuntosObtenidos(0);

            when(apuestaRepository.findById(10L)).thenReturn(Optional.of(apuesta));
            when(pronosticoRepository.findByApuestaId(10L)).thenReturn(List.of(pronostico));
            when(participacionRepository.findByUsuarioIdAndApuestaId(1L, 10L))
                    .thenReturn(Optional.of(participacion));
            when(participacionRepository.findByApuestaIdOrderByPuntosDesc(10L))
                    .thenReturn(List.of(participacion));

            List<PronosticoDTO> resultado = apuestaService.calcularPuntosParciales(10L);

            assertEquals(1, resultado.size());
            assertEquals(6, pronostico.getPuntosObtenidos());
        }

        @Test
        void cuandoPartidoSinGoles_omiteProtnostico() {
            partido.setGolesLocal(null);
            partido.setGolesVisitante(null);

            Pronostico pronostico = new Pronostico();
            pronostico.setId(500L);
            pronostico.setApuesta(apuesta);
            pronostico.setUsuario(usuario);
            pronostico.setPartido(partido);
            pronostico.setPuntosObtenidos(0);

            when(apuestaRepository.findById(10L)).thenReturn(Optional.of(apuesta));
            when(pronosticoRepository.findByApuestaId(10L)).thenReturn(List.of(pronostico));
            when(participacionRepository.findByApuestaIdOrderByPuntosDesc(10L))
                    .thenReturn(List.of(participacion));

            List<PronosticoDTO> resultado = apuestaService.calcularPuntosParciales(10L);

            assertTrue(resultado.isEmpty());
        }

        @Test
        void cuandoPronosticoYaCalculado_loOmite() {
            partido.setGolesLocal(2);
            partido.setGolesVisitante(1);

            Pronostico pronostico = new Pronostico();
            pronostico.setId(500L);
            pronostico.setApuesta(apuesta);
            pronostico.setUsuario(usuario);
            pronostico.setPartido(partido);
            pronostico.setPuntosObtenidos(5);

            when(apuestaRepository.findById(10L)).thenReturn(Optional.of(apuesta));
            when(pronosticoRepository.findByApuestaId(10L)).thenReturn(List.of(pronostico));
            when(participacionRepository.findByApuestaIdOrderByPuntosDesc(10L))
                    .thenReturn(List.of(participacion));

            List<PronosticoDTO> resultado = apuestaService.calcularPuntosParciales(10L);

            assertTrue(resultado.isEmpty());
            assertEquals(5, pronostico.getPuntosObtenidos());
        }

        @Test
        void cuandoParticipacionNoExiste_lanzaParticipacionNotFoundException() {
            partido.setGolesLocal(2);
            partido.setGolesVisitante(1);

            Pronostico pronostico = new Pronostico();
            pronostico.setId(500L);
            pronostico.setApuesta(apuesta);
            pronostico.setUsuario(usuario);
            pronostico.setPartido(partido);
            pronostico.setResultadoPronosticado(LOCAL);
            pronostico.setGolesLocalPronosticados(2);
            pronostico.setGolesVisitantePronosticados(1);
            pronostico.setPuntosObtenidos(0);

            when(apuestaRepository.findById(10L)).thenReturn(Optional.of(apuesta));
            when(pronosticoRepository.findByApuestaId(10L)).thenReturn(List.of(pronostico));
            when(participacionRepository.findByUsuarioIdAndApuestaId(1L, 10L))
                    .thenReturn(Optional.empty());

            assertThrows(ParticipacionNotFoundException.class,
                    () -> apuestaService.calcularPuntosParciales(10L));
        }
    }

    @Nested
    @DisplayName("listarTodas")
    class ListarTodas {

        @Test
        void retornaTodasLasApuestas() {
            when(apuestaRepository.findAll()).thenReturn(List.of(apuesta));

            List<ApuestaDTO> resultado = apuestaService.listarTodas();

            assertEquals(1, resultado.size());
            assertEquals(POLLA_TEST, resultado.get(0).getNombre());
        }

        @Test
        void cuandoNoHay_retornaListaVacia() {
            when(apuestaRepository.findAll()).thenReturn(Collections.emptyList());

            List<ApuestaDTO> resultado = apuestaService.listarTodas();

            assertTrue(resultado.isEmpty());
        }
    }

    @Nested
    @DisplayName("eliminarApuesta")
    class EliminarApuesta {

        @Test
        void cuandoExiste_eliminaPronosticosParticipacionesYApuesta() {
            when(apuestaRepository.findById(10L)).thenReturn(Optional.of(apuesta));

            apuestaService.eliminarApuesta(10L);

            verify(pronosticoRepository).deleteByApuestaId(10L);
            verify(participacionRepository).deleteByApuestaId(10L);
            verify(apuestaRepository).delete(apuesta);
        }

        @Test
        void cuandoNoExiste_lanzaApuestaNotFoundException() {
            when(apuestaRepository.findById(99L)).thenReturn(Optional.empty());

            assertThrows(ApuestaNotFoundException.class, () -> apuestaService.eliminarApuesta(99L));
            verify(apuestaRepository, never()).delete(any());
        }
    }

    @Nested
    @DisplayName("listarApuestasPorUsuarioCompleto")
    class ListarApuestasPorUsuarioCompleto {

        @Test
        void retornaApuestasConParticipantes() {
            when(participacionRepository.findByUsuarioIdConApuesta(1L))
                    .thenReturn(List.of(participacion));
            when(participacionRepository.findByApuestaIdInConUsuario(List.of(10L)))
                    .thenReturn(List.of(participacion));

            List<ApuestaConParticipantesDTO> resultado =
                    apuestaService.listarApuestasPorUsuarioCompleto(1L);

            assertEquals(1, resultado.size());
            assertEquals(POLLA_TEST, resultado.get(0).getNombre());
        }

        @Test
        void cuandoNoTieneApuestas_retornaListaVacia() {
            when(participacionRepository.findByUsuarioIdConApuesta(1L))
                    .thenReturn(Collections.emptyList());
            when(participacionRepository.findByApuestaIdInConUsuario(Collections.emptyList()))
                    .thenReturn(Collections.emptyList());

            List<ApuestaConParticipantesDTO> resultado =
                    apuestaService.listarApuestasPorUsuarioCompleto(1L);

            assertTrue(resultado.isEmpty());
        }
    }

    private Pronostico construirPronostico() {
        Pronostico p = new Pronostico();
        p.setId(500L);
        p.setApuesta(apuesta);
        p.setUsuario(usuario);
        p.setPartido(partido);
        p.setResultadoPronosticado(LOCAL);
        p.setGolesLocalPronosticados(2);
        p.setGolesVisitantePronosticados(1);
        p.setPuntosObtenidos(0);
        return p;
    }
}