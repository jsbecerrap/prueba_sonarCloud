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

/**
 * Pruebas unitarias para {@link ApuestaServiceImpl}
 * Verifica la logica de negocio del servicio de apuestas usando mocks de repositorios y servicios dependientes
 */
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

    /** Usuario de prueba usado como creador y participante en los tests */
    private Usuario usuario;

    /** Apuesta de prueba en estado abierto usada como base en los tests */
    private Apuesta apuesta;

    /** Partido de prueba con fecha futura usado en los tests de pronosticos */
    private Partido partido;

    /** Participacion de prueba que vincula el usuario con la apuesta en los tests */
    private Participacion participacion;

    /** Correo del usuario de prueba usado en validaciones de propietario */
    private static final String CORREO_TEST = "test@test.com";

    /** Resultado de partido local usado como constante en pronosticos de prueba */
    private static final String LOCAL = "LOCAL";

    /** Estado cerrado de apuesta usado en los tests que validan transiciones de estado */
    private static final String CERRADA = "CERRADA";

    /** Estado abierto de apuesta usado en los tests que validan transiciones de estado */
    private static final String ABIERTA = "ABIERTA";

    /** Nombre de la apuesta de prueba usado en verificaciones de datos */
    private static final String POLLA_TEST = "Polla Test";

    /** Codigo de invitacion de prueba usado en los tests de union a apuesta */
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

    /**
     * Tests del metodo crearApuesta — cubre creacion exitosa, fecha pasada y fecha nula
     */
    @Nested
    @DisplayName("crearApuesta")
    class CrearApuesta {

        /**
         * Verifica que con datos validos se crea la apuesta y se registra la participacion del creador
         */
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

        /**
         * Verifica que una fecha de cierre en el pasado lanza {@link ApuestaCerradaException}
         * y no persiste la apuesta
         */
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

        /**
         * Verifica que una fecha de cierre nula se acepta sin validacion de fecha
         */
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

    /**
     * Tests del metodo registrarPronostico — cubre registro exitoso y multiples casos de error
     */
    @Nested
    @DisplayName("registrarPronostico")
    class RegistrarPronostico {

        /**
         * Verifica que con datos validos se registra el pronostico y se audita el evento
         */
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

        /**
         * Verifica que una apuesta inexistente lanza {@link ApuestaNotFoundException}
         */
        @Test
        void cuandoApuestaNoExiste_lanzaApuestaNotFoundException() {
            PronosticoRequestDTO dto = new PronosticoRequestDTO();
            dto.setUsuarioId(1L);
            dto.setApuestaId(99L);

            when(usuarioService.obtenerEntidadPorId(1L)).thenReturn(usuario);
            when(apuestaRepository.findById(99L)).thenReturn(Optional.empty());

            assertThrows(ApuestaNotFoundException.class, () -> apuestaService.registrarPronostico(dto));
        }

        /**
         * Verifica que una apuesta cerrada lanza {@link ApuestaCerradaException}
         */
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

        /**
         * Verifica que un usuario que no participa en la apuesta lanza {@link ParticipacionNotFoundException}
         */
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

        /**
         * Verifica que un partido a menos de 5 minutos de iniciar lanza {@link PartidoYaIniciadoException}
         */
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

    /**
     * Tests del metodo unirseApuesta — cubre union exitosa, codigo invalido y usuario duplicado
     */
    @Nested
    @DisplayName("unirseApuesta")
    class UnirseApuesta {

        /**
         * Verifica que con codigo valido y usuario nuevo se registra la participacion y se notifica
         */
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

        /**
         * Verifica que un codigo de invitacion inexistente lanza {@link CodigoInvalidoException}
         */
        @Test
        void cuandoCodigoNoExiste_lanzaCodigoInvalidoException() {
            when(usuarioService.obtenerEntidadPorId(1L)).thenReturn(usuario);
            when(apuestaRepository.findByCodigoInvitacion("MAL")).thenReturn(Optional.empty());

            assertThrows(CodigoInvalidoException.class,
                    () -> apuestaService.unirseApuesta("MAL", 1L));
        }

        /**
         * Verifica que un usuario que ya pertenece a la apuesta lanza {@link UsuarioYaEnApuestaException}
         */
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

    /**
     * Tests del metodo editarPronostico — cubre edicion exitosa, resultado nulo, no existente y permisos
     */
    @Nested
    @DisplayName("editarPronostico")
    class EditarPronostico {

        /**
         * Verifica que con pronostico propio y apuesta abierta se actualizan correctamente los datos
         */
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

        /**
         * Verifica que si el resultado en el DTO es nulo se conserva el resultado anterior del pronostico
         */
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

        /**
         * Verifica que un pronostico inexistente lanza {@link PronosticoNotFoundException}
         */
        @Test
        void cuandoNoExiste_lanzaPronosticoNotFoundException() {
            PronosticoRequestDTO dto = new PronosticoRequestDTO();
            when(pronosticoRepository.findById(999L)).thenReturn(Optional.empty());

            assertThrows(PronosticoNotFoundException.class,
                    () -> apuestaService.editarPronostico(999L, dto, CORREO_TEST));
        }

        /**
         * Verifica que intentar editar un pronostico de otro usuario lanza {@link ApuestaCerradaException}
         */
        @Test
        void cuandoNoEsDelUsuario_lanzaApuestaCerradaException() {
            Pronostico pronostico = construirPronostico();

            PronosticoRequestDTO dto = new PronosticoRequestDTO();

            when(pronosticoRepository.findById(500L)).thenReturn(Optional.of(pronostico));

            assertThrows(ApuestaCerradaException.class,
                    () -> apuestaService.editarPronostico(500L, dto, "otro@test.com"));
        }

        /**
         * Verifica que intentar editar un pronostico en una apuesta cerrada lanza {@link ApuestaCerradaException}
         */
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

    /**
     * Tests del metodo eliminarPronostico — cubre eliminacion exitosa, no existente y permisos
     */
    @Nested
    @DisplayName("eliminarPronostico")
    class EliminarPronostico {

        /**
         * Verifica que con pronostico propio y apuesta abierta se elimina y se audita el evento
         */
        @Test
        void cuandoEsDelUsuarioYApuestaAbierta_eliminaPronostico() {
            Pronostico pronostico = construirPronostico();
            when(pronosticoRepository.findById(500L)).thenReturn(Optional.of(pronostico));

            apuestaService.eliminarPronostico(500L, CORREO_TEST);

            verify(pronosticoRepository).delete(pronostico);
            verify(auditoriaService).registrar(eq("PRONOSTICO_ELIMINADO"), anyString(), eq(1L), anyString(),
                    eq("Pronostico"));
        }

        /**
         * Verifica que un pronostico inexistente lanza {@link PronosticoNotFoundException}
         */
        @Test
        void cuandoNoExiste_lanzaPronosticoNotFoundException() {
            when(pronosticoRepository.findById(999L)).thenReturn(Optional.empty());

            assertThrows(PronosticoNotFoundException.class,
                    () -> apuestaService.eliminarPronostico(999L, CORREO_TEST));
        }

        /**
         * Verifica que intentar eliminar un pronostico de otro usuario lanza {@link ApuestaCerradaException}
         */
        @Test
        void cuandoNoEsDelUsuario_lanzaApuestaCerradaException() {
            Pronostico pronostico = construirPronostico();
            when(pronosticoRepository.findById(500L)).thenReturn(Optional.of(pronostico));

            assertThrows(ApuestaCerradaException.class,
                    () -> apuestaService.eliminarPronostico(500L, "otro@test.com"));
        }

        /**
         * Verifica que intentar eliminar un pronostico en una apuesta cerrada lanza {@link ApuestaCerradaException}
         */
        @Test
        void cuandoApuestaCerrada_lanzaApuestaCerradaException() {
            Pronostico pronostico = construirPronostico();
            apuesta.setEstado(CERRADA);
            when(pronosticoRepository.findById(500L)).thenReturn(Optional.of(pronostico));

            assertThrows(ApuestaCerradaException.class,
                    () -> apuestaService.eliminarPronostico(500L, CORREO_TEST));
        }
    }

    /**
     * Tests del metodo obtenerRanking — verifica ordenamiento por puntos
     */
    @Nested
    @DisplayName("obtenerRanking")
    class ObtenerRanking {

        /**
         * Verifica que las participaciones se retornan ordenadas por puntos descendente
         */
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

    /**
     * Tests del metodo calcularPuntos — cubre multiples escenarios de puntaje y casos de error
     */
    @Nested
    @DisplayName("calcularPuntos")
    class CalcularPuntos {

        /**
         * Verifica que una apuesta inexistente lanza {@link ApuestaNotFoundException}
         */
        @Test
        void cuandoApuestaNoExiste_lanzaApuestaNotFoundException() {
            when(apuestaRepository.findById(99L)).thenReturn(Optional.empty());

            assertThrows(ApuestaNotFoundException.class, () -> apuestaService.calcularPuntos(99L));
        }

        /**
         * Verifica que calcular puntos en una apuesta no cerrada lanza {@link EstadoInvalidoException}
         */
        @Test
        void cuandoApuestaNoCerrada_lanzaEstadoInvalidoException() {
            when(apuestaRepository.findById(10L)).thenReturn(Optional.of(apuesta));

            assertThrows(EstadoInvalidoException.class, () -> apuestaService.calcularPuntos(10L));
        }

        /**
         * Verifica que acertar resultado y marcador exacto otorga 6 puntos al pronostico
         */
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

        /**
         * Verifica que acertar solo el resultado sin el marcador exacto otorga 2 puntos
         */
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

        /**
         * Verifica que acertar solo el total de goles sin el resultado otorga 1 punto
         */
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

        /**
         * Verifica que acertar victoria visitante con marcador exacto otorga 6 puntos
         */
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

        /**
         * Verifica que acertar empate con marcador exacto otorga 6 puntos
         */
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

        /**
         * Verifica que un partido sin goles registrados omite el pronostico y retorna lista vacia
         */
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

        /**
         * Verifica que una participacion inexistente al calcular puntos lanza {@link ParticipacionNotFoundException}
         */
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

    /**
     * Tests del metodo cerrarApuesta — cubre cierre exitoso, no existente y ya cerrada
     */
    @Nested
    @DisplayName("cerrarApuesta")
    class CerrarApuesta {

        /**
         * Verifica que una apuesta abierta cambia su estado a cerrada correctamente
         */
        @Test
        void cuandoEstaAbierta_laCierra() {
            when(apuestaRepository.findById(10L)).thenReturn(Optional.of(apuesta));
            when(apuestaRepository.save(any(Apuesta.class))).thenReturn(apuesta);

            ApuestaDTO resultado = apuestaService.cerrarApuesta(10L);

            assertEquals(CERRADA, resultado.getEstado());
            assertEquals(CERRADA, apuesta.getEstado());
        }

        /**
         * Verifica que una apuesta inexistente lanza {@link ApuestaNotFoundException}
         */
        @Test
        void cuandoNoExiste_lanzaApuestaNotFoundException() {
            when(apuestaRepository.findById(99L)).thenReturn(Optional.empty());

            assertThrows(ApuestaNotFoundException.class, () -> apuestaService.cerrarApuesta(99L));
        }

        /**
         * Verifica que intentar cerrar una apuesta ya cerrada lanza {@link ApuestaCerradaException}
         */
        @Test
        void cuandoYaCerrada_lanzaApuestaCerradaException() {
            apuesta.setEstado(CERRADA);
            when(apuestaRepository.findById(10L)).thenReturn(Optional.of(apuesta));

            assertThrows(ApuestaCerradaException.class, () -> apuestaService.cerrarApuesta(10L));
        }
    }

    /**
     * Tests del metodo obtenerApuesta — cubre obtencion exitosa y no existente
     */
    @Nested
    @DisplayName("obtenerApuesta")
    class ObtenerApuesta {

        /**
         * Verifica que una apuesta existente retorna el DTO con los datos correctos
         */
        @Test
        void cuandoExiste_retornaDTO() {
            when(apuestaRepository.findById(10L)).thenReturn(Optional.of(apuesta));

            ApuestaDTO resultado = apuestaService.obtenerApuesta(10L);

            assertEquals(10L, resultado.getId());
            assertEquals(POLLA_TEST, resultado.getNombre());
        }

        /**
         * Verifica que una apuesta inexistente lanza {@link ApuestaNotFoundException}
         */
        @Test
        void cuandoNoExiste_lanzaApuestaNotFoundException() {
            when(apuestaRepository.findById(99L)).thenReturn(Optional.empty());

            assertThrows(ApuestaNotFoundException.class, () -> apuestaService.obtenerApuesta(99L));
        }
    }

    /**
     * Tests del metodo listarParticipantes — verifica listado de participantes de una apuesta
     */
    @Nested
    @DisplayName("listarParticipantes")
    class ListarParticipantes {

        /**
         * Verifica que se retornan los participantes asociados a la apuesta indicada
         */
        @Test
        void retornaParticipantesDeLaApuesta() {
            when(participacionRepository.findByApuestaId(10L)).thenReturn(List.of(participacion));

            List<ParticipacionDTO> resultado = apuestaService.listarParticipantes(10L);

            assertEquals(1, resultado.size());
        }
    }

    /**
     * Tests del metodo listarApuestasPorUsuario — cubre listado con y sin apuestas
     */
    @Nested
    @DisplayName("listarApuestasPorUsuario")
    class ListarApuestasPorUsuario {

        /**
         * Verifica que se retornan las apuestas en las que el usuario participa
         */
        @Test
        void retornaApuestasDelUsuario() {
            when(participacionRepository.findByUsuarioId(1L)).thenReturn(List.of(participacion));

            List<ApuestaDTO> resultado = apuestaService.listarApuestasPorUsuario(1L);

            assertEquals(1, resultado.size());
            assertEquals(POLLA_TEST, resultado.get(0).getNombre());
        }

        /**
         * Verifica que un usuario sin apuestas retorna lista vacia
         */
        @Test
        void cuandoNoTiene_retornaListaVacia() {
            when(participacionRepository.findByUsuarioId(1L)).thenReturn(Collections.emptyList());

            List<ApuestaDTO> resultado = apuestaService.listarApuestasPorUsuario(1L);

            assertTrue(resultado.isEmpty());
        }
    }

    /**
     * Tests del metodo verificarPronostico — cubre obtencion exitosa y no existente
     */
    @Nested
    @DisplayName("verificarPronostico")
    class VerificarPronostico {

        /**
         * Verifica que un pronostico existente retorna el DTO con el ID correcto
         */
        @Test
        void cuandoExiste_retornaDTO() {
            Pronostico pronostico = construirPronostico();
            when(pronosticoRepository.findById(500L)).thenReturn(Optional.of(pronostico));

            PronosticoDTO resultado = apuestaService.verificarPronostico(500L);

            assertNotNull(resultado);
            assertEquals(500L, resultado.getId());
        }

        /**
         * Verifica que un pronostico inexistente lanza {@link PronosticoNotFoundException}
         */
        @Test
        void cuandoNoExiste_lanzaPronosticoNotFoundException() {
            when(pronosticoRepository.findById(999L)).thenReturn(Optional.empty());

            assertThrows(PronosticoNotFoundException.class,
                    () -> apuestaService.verificarPronostico(999L));
        }
    }

    /**
     * Tests del metodo calcularPuntosAutomatico — verifica calculo automatico de apuestas pendientes
     */
    @Nested
    @DisplayName("calcularPuntosAutomatico")
    class CalcularPuntosAutomatico {

        /**
         * Verifica que las apuestas cerradas sin puntos calculados son procesadas y guardadas
         */
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

        /**
         * Verifica que si no hay apuestas pendientes no se realiza ninguna operacion de guardado
         */
        @Test
        void cuandoNoHayApuestas_noHaceNada() {
            when(apuestaRepository.findByEstadoAndPuntosCalculadosFalse(CERRADA))
                    .thenReturn(Collections.emptyList());

            apuestaService.calcularPuntosAutomatico();

            verify(apuestaRepository, never()).save(any());
        }
    }

    /**
     * Tests del metodo cerrarApuestasVencidas — verifica cierre automatico por fecha de vencimiento
     */
    @Nested
    @DisplayName("cerrarApuestasVencidas")
    class CerrarApuestasVencidas {

        /**
         * Verifica que las apuestas cuya fecha de cierre ya paso cambian su estado a cerrada
         */
        @Test
        void cuandoHayVencidas_lasCierra() {
            when(apuestaRepository.findByEstadoAndFechaCierreBefore(eq(ABIERTA), any(LocalDateTime.class)))
                    .thenReturn(List.of(apuesta));
            when(apuestaRepository.findById(10L)).thenReturn(Optional.of(apuesta));
            when(apuestaRepository.save(any(Apuesta.class))).thenReturn(apuesta);

            apuestaService.cerrarApuestasVencidas();

            assertEquals(CERRADA, apuesta.getEstado());
        }

        /**
         * Verifica que si no hay apuestas vencidas no se realiza ninguna operacion de guardado
         */
        @Test
        void cuandoNoHayVencidas_noHaceNada() {
            when(apuestaRepository.findByEstadoAndFechaCierreBefore(eq(ABIERTA), any(LocalDateTime.class)))
                    .thenReturn(Collections.emptyList());

            apuestaService.cerrarApuestasVencidas();

            verify(apuestaRepository, never()).save(any());
        }
    }

    /**
     * Tests del metodo misPronosticos — verifica listado de pronosticos por usuario y apuesta
     */
    @Nested
    @DisplayName("misPronosticos")
    class MisPronosticos {

        /**
         * Verifica que se retornan los pronosticos del usuario en la apuesta indicada
         */
        @Test
        void retornaPronosticosDelUsuario() {
            Pronostico p = construirPronostico();
            when(pronosticoRepository.findByApuestaIdAndUsuarioId(10L, 1L)).thenReturn(List.of(p));

            List<PronosticoDTO> resultado = apuestaService.misPronosticos(10L, 1L);

            assertEquals(1, resultado.size());
        }
    }

    /**
     * Tests del metodo calcularPuntosParciales — cubre calculo parcial, omision y errores
     */
    @Nested
    @DisplayName("calcularPuntosParciales")
    class CalcularPuntosParciales {

        /**
         * Verifica que una apuesta inexistente lanza {@link ApuestaNotFoundException}
         */
        @Test
        void cuandoApuestaNoExiste_lanzaApuestaNotFoundException() {
            when(apuestaRepository.findById(99L)).thenReturn(Optional.empty());

            assertThrows(ApuestaNotFoundException.class,
                    () -> apuestaService.calcularPuntosParciales(99L));
        }

        /**
         * Verifica que un partido con goles registrados calcula y actualiza los puntos de la participacion
         */
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

        /**
         * Verifica que un partido sin goles registrados omite el pronostico y retorna lista vacia
         */
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

        /**
         * Verifica que un pronostico con puntos ya calculados es omitido sin modificar su valor
         */
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

        /**
         * Verifica que una participacion inexistente al calcular parciales lanza {@link ParticipacionNotFoundException}
         */
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

    /**
     * Tests del metodo listarTodas — cubre listado completo y vacio
     */
    @Nested
    @DisplayName("listarTodas")
    class ListarTodas {

        /**
         * Verifica que se retornan todas las apuestas existentes en el sistema
         */
        @Test
        void retornaTodasLasApuestas() {
            when(apuestaRepository.findAll()).thenReturn(List.of(apuesta));

            List<ApuestaDTO> resultado = apuestaService.listarTodas();

            assertEquals(1, resultado.size());
            assertEquals(POLLA_TEST, resultado.get(0).getNombre());
        }

        /**
         * Verifica que si no hay apuestas en el sistema se retorna lista vacia
         */
        @Test
        void cuandoNoHay_retornaListaVacia() {
            when(apuestaRepository.findAll()).thenReturn(Collections.emptyList());

            List<ApuestaDTO> resultado = apuestaService.listarTodas();

            assertTrue(resultado.isEmpty());
        }
    }

    /**
     * Tests del metodo eliminarApuesta — cubre eliminacion exitosa y no existente
     */
    @Nested
    @DisplayName("eliminarApuesta")
    class EliminarApuesta {

        /**
         * Verifica que eliminar una apuesta existente borra sus pronosticos, participaciones y la apuesta
         */
        @Test
        void cuandoExiste_eliminaPronosticosParticipacionesYApuesta() {
            when(apuestaRepository.findById(10L)).thenReturn(Optional.of(apuesta));

            apuestaService.eliminarApuesta(10L);

            verify(pronosticoRepository).deleteByApuestaId(10L);
            verify(participacionRepository).deleteByApuestaId(10L);
            verify(apuestaRepository).delete(apuesta);
        }

        /**
         * Verifica que una apuesta inexistente lanza {@link ApuestaNotFoundException}
         * y no se intenta ninguna eliminacion
         */
        @Test
        void cuandoNoExiste_lanzaApuestaNotFoundException() {
            when(apuestaRepository.findById(99L)).thenReturn(Optional.empty());

            assertThrows(ApuestaNotFoundException.class, () -> apuestaService.eliminarApuesta(99L));
            verify(apuestaRepository, never()).delete(any());
        }
    }

    /**
     * Tests del metodo listarApuestasPorUsuarioCompleto — cubre listado con participantes y vacio
     */
    @Nested
    @DisplayName("listarApuestasPorUsuarioCompleto")
    class ListarApuestasPorUsuarioCompleto {

        /**
         * Verifica que se retornan las apuestas del usuario con la informacion de todos sus participantes
         */
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

        /**
         * Verifica que un usuario sin apuestas retorna lista vacia
         */
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