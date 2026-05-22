package co.edu.unbosque.mundial_2026.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import co.edu.unbosque.mundial_2026.dto.NotificacionDTO;
import co.edu.unbosque.mundial_2026.dto.request.NotificacionMasivaRequestDTO;
import co.edu.unbosque.mundial_2026.dto.request.NotificacionRequestDTO;
import co.edu.unbosque.mundial_2026.entity.*;
import co.edu.unbosque.mundial_2026.exception.PartidoNotFoundException;
import co.edu.unbosque.mundial_2026.exception.UsuarioNotFoundException;
import co.edu.unbosque.mundial_2026.repository.*;


@ExtendWith(MockitoExtension.class)
class NotificacionServiceImplTest {

    @Mock private NotificacionRepository notificacionRepository;
    @Mock private UsuarioRepository usuarioRepository;
    @Mock private PartidoRepository partidoRepository;
    @Mock private EventoAuditoriaService eventoAuditoriaService;
private static final String COLOMBIA_VS_BRAZIL = "Colombia vs Brazil";
private static final String TITULO_TEST = "Titulo test";
private static final String INFO = "INFO";
private static final String MENSAJE_TEST = "Mensaje test";
private static final String SISTEMA = "SISTEMA";
private static final String COLOMBIA = "Colombia";
private static final String BRAZIL = "Brazil";
    @InjectMocks private NotificacionServiceImpl service;

    private Usuario crearUsuario(Long id, boolean activo) {
        Rol rol = new Rol();
        rol.setNombre("ROLE_USUARIO");
        Usuario u = new Usuario();
        u.setId(id);
        u.setCorreoUsuario("user" + id + "@test.com");
        u.setActivo(activo);
        u.setRol(rol);
        u.setSeleccionesU(new ArrayList<>());
        u.setPreferenciasu(new ArrayList<>());
        u.setCiudadFavoritas(new ArrayList<>());
        return u;
    }

    private Notificacion crearNotificacion(Long id, Usuario usuario, boolean leida) {
        Notificacion n = new Notificacion(INFO, TITULO_TEST, MENSAJE_TEST, SISTEMA, "ENVIADA", usuario);
        n.setId(id);
        n.setLeida(leida);
        n.setFecha(LocalDateTime.now());
        return n;
    }

    private NotificacionRequestDTO crearRequestDTO(Long usuarioId) {
        NotificacionRequestDTO dto = new NotificacionRequestDTO();
        dto.setUsuarioId(usuarioId);
        dto.setTipo(INFO);
        dto.setTitulo(TITULO_TEST);
        dto.setMensaje(MENSAJE_TEST);
        dto.setCanal(SISTEMA);
        return dto;
    }

    private NotificacionMasivaRequestDTO crearMasivaDTO(List<Long> ids) {
        NotificacionMasivaRequestDTO dto = new NotificacionMasivaRequestDTO();
        dto.setTipo(INFO);
        dto.setTitulo("Titulo masivo");
        dto.setMensaje("Mensaje masivo");
        dto.setCanal(SISTEMA);
        dto.setUsuarioIds(ids);
        return dto;
    }

    @Test
    void enviarNotificacion_usuarioExistente_sinToken_guardaNotificacion() {
        Usuario usuario = crearUsuario(1L, true);
        usuario.setFcmtoken(null);
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(notificacionRepository.save(any(Notificacion.class))).thenReturn(new Notificacion());

        service.enviarNotificacion(crearRequestDTO(1L));

        verify(notificacionRepository).save(any(Notificacion.class));
    }

    @Test
    void enviarNotificacion_usuarioExistente_conTokenBlanco_guardaNotificacion() {
        Usuario usuario = crearUsuario(1L, true);
        usuario.setFcmtoken("   ");
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(notificacionRepository.save(any(Notificacion.class))).thenReturn(new Notificacion());

        service.enviarNotificacion(crearRequestDTO(1L));

        verify(notificacionRepository).save(any(Notificacion.class));
    }

    @Test
    void enviarNotificacion_usuarioNoExistente_lanzaExcepcion() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(UsuarioNotFoundException.class, () -> service.enviarNotificacion(crearRequestDTO(99L)));
    }

    @Test
    void listarPorUsuario_usuarioExistente_retornaLista() {
        Usuario usuario = crearUsuario(1L, true);
        Notificacion notificacion = crearNotificacion(1L, usuario, false);

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(notificacionRepository.findByUsuario(usuario)).thenReturn(List.of(notificacion));

        List<NotificacionDTO> resultado = service.listarPorUsuario(1L);

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertFalse(resultado.get(0).isLeida());
    }

    @Test
    void listarPorUsuario_sinNotificaciones_retornaListaVacia() {
        Usuario usuario = crearUsuario(1L, true);

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(notificacionRepository.findByUsuario(usuario)).thenReturn(List.of());

        List<NotificacionDTO> resultado = service.listarPorUsuario(1L);

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    @Test
    void listarPorUsuario_usuarioNoExistente_lanzaExcepcion() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(UsuarioNotFoundException.class, () -> service.listarPorUsuario(99L));
    }

    @Test
    void marcarLeida_notificacionExistente_marcaComoLeida() {
        Usuario usuario = crearUsuario(1L, true);
        Notificacion notificacion = crearNotificacion(1L, usuario, false);

        when(notificacionRepository.findById(1L)).thenReturn(Optional.of(notificacion));
        when(notificacionRepository.save(any(Notificacion.class))).thenReturn(notificacion);

        service.marcarLeida(1L);

        assertTrue(notificacion.isLeida());
        verify(notificacionRepository).save(notificacion);
    }

    @Test
    void marcarLeida_notificacionNoExistente_lanzaExcepcion() {
        when(notificacionRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> service.marcarLeida(99L));
    }

    @Test
    void marcarTodasLeidas_usuarioExistente_invocaRepositorio() {
        doNothing().when(notificacionRepository).marcarTodasLeidasPorUsuario(1L);

        service.marcarTodasLeidas(1L);

        verify(notificacionRepository).marcarTodasLeidasPorUsuario(1L);
    }

    @Test
    void enviarMasiva_sinUsuarioIds_enviaATodosActivos() {
        Usuario activo = crearUsuario(1L, true);
        Usuario inactivo = crearUsuario(2L, false);

        when(usuarioRepository.findAll()).thenReturn(List.of(activo, inactivo));
        when(notificacionRepository.saveAll(any())).thenReturn(List.of());
        doNothing().when(eventoAuditoriaService).registrar(any(), any(), any(), any(), any());

        service.enviarMasiva(crearMasivaDTO(null));

        verify(notificacionRepository).saveAll(argThat(list -> ((List<?>) list).size() == 1));
    }

    @Test
    void enviarMasiva_conListaVacia_enviaATodosActivos() {
        Usuario activo = crearUsuario(1L, true);

        when(usuarioRepository.findAll()).thenReturn(List.of(activo));
        when(notificacionRepository.saveAll(any())).thenReturn(List.of());
        doNothing().when(eventoAuditoriaService).registrar(any(), any(), any(), any(), any());

        service.enviarMasiva(crearMasivaDTO(List.of()));

        verify(notificacionRepository).saveAll(any());
    }

    @Test
    void enviarMasiva_conUsuarioIds_enviaASoloActivos() {
        Usuario activo = crearUsuario(1L, true);
        Usuario inactivo = crearUsuario(2L, false);

        when(usuarioRepository.findAllById(List.of(1L, 2L))).thenReturn(List.of(activo, inactivo));
        when(notificacionRepository.saveAll(any())).thenReturn(List.of());
        doNothing().when(eventoAuditoriaService).registrar(any(), any(), any(), any(), any());

        service.enviarMasiva(crearMasivaDTO(List.of(1L, 2L)));

        verify(notificacionRepository).saveAll(argThat(list -> ((List<?>) list).size() == 1));
    }

    @Test
    void notificarPorPartido_partidoExistente_usuarioConSeleccionFav_notifica() {
        Partido partido = new Partido();
        partido.setId(1L);
        partido.setSeleccionLocal(COLOMBIA);
        partido.setSeleccionVisitante(BRAZIL);

        Seleccion seleccion = new Seleccion();
        seleccion.setNombre(COLOMBIA);

        Usuario usuario = crearUsuario(1L, true);
        usuario.setSeleccionesU(List.of(seleccion));

        when(partidoRepository.findById(1L)).thenReturn(Optional.of(partido));
        when(usuarioRepository.findAll()).thenReturn(List.of(usuario));
        when(notificacionRepository.saveAll(any())).thenReturn(List.of());
        doNothing().when(eventoAuditoriaService).registrar(any(), any(), any(), any(), any());

        service.notificarPorPartido(1L, INFO, "Titulo", "Mensaje");

        verify(notificacionRepository).saveAll(argThat(list -> ((List<?>) list).size() == 1));
    }

    @Test
    void notificarPorPartido_usuarioSinSeleccionesFav_noEnviaNotificacion() {
        Partido partido = new Partido();
        partido.setId(1L);
        partido.setSeleccionLocal(COLOMBIA);
        partido.setSeleccionVisitante(BRAZIL);

        Usuario usuario = crearUsuario(1L, true);
        usuario.setSeleccionesU(List.of());

        when(partidoRepository.findById(1L)).thenReturn(Optional.of(partido));
        when(usuarioRepository.findAll()).thenReturn(List.of(usuario));
        when(notificacionRepository.saveAll(any())).thenReturn(List.of());
        doNothing().when(eventoAuditoriaService).registrar(any(), any(), any(), any(), any());

        service.notificarPorPartido(1L, INFO, "Titulo", "Mensaje");

        verify(notificacionRepository).saveAll(argThat(list -> ((List<?>) list).isEmpty()));
    }

    @Test
    void notificarPorPartido_usuarioInactivo_noEnviaNotificacion() {
        Partido partido = new Partido();
        partido.setId(1L);
        partido.setSeleccionLocal(COLOMBIA);
        partido.setSeleccionVisitante(BRAZIL);

        Seleccion seleccion = new Seleccion();
        seleccion.setNombre(COLOMBIA);

        Usuario inactivo = crearUsuario(2L, false);
        inactivo.setSeleccionesU(List.of(seleccion));

        when(partidoRepository.findById(1L)).thenReturn(Optional.of(partido));
        when(usuarioRepository.findAll()).thenReturn(List.of(inactivo));
        when(notificacionRepository.saveAll(any())).thenReturn(List.of());
        doNothing().when(eventoAuditoriaService).registrar(any(), any(), any(), any(), any());

        service.notificarPorPartido(1L, INFO, "Titulo", "Mensaje");

        verify(notificacionRepository).saveAll(argThat(list -> ((List<?>) list).isEmpty()));
    }

    @Test
    void notificarPorPartido_partidoNoExistente_lanzaExcepcion() {
        when(partidoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(PartidoNotFoundException.class,
                () -> service.notificarPorPartido(99L, INFO, "Titulo", "Mensaje"));
    }

    @Test
    void notificarRegistro_usuarioSinToken_guardaNotificacion() {
        Usuario usuario = crearUsuario(1L, true);
        usuario.setFcmtoken(null);
        when(notificacionRepository.save(any(Notificacion.class))).thenReturn(new Notificacion());

        service.notificarRegistro(usuario);

        verify(notificacionRepository).save(any(Notificacion.class));
    }

    @Test
    void notificarActualizacionPerfil_usuarioSinToken_guardaNotificacion() {
        Usuario usuario = crearUsuario(1L, true);
        usuario.setFcmtoken(null);
        when(notificacionRepository.save(any(Notificacion.class))).thenReturn(new Notificacion());

        service.notificarActualizacionPerfil(usuario);

        verify(notificacionRepository).save(any(Notificacion.class));
    }

    @Test
    void notificarEntradaPagada_guardaNotificacion() {
        Usuario usuario = crearUsuario(1L, true);
        usuario.setFcmtoken(null);
        when(notificacionRepository.save(any(Notificacion.class))).thenReturn(new Notificacion());

        service.notificarEntradaPagada(usuario, COLOMBIA_VS_BRAZIL, "VIP", "A", "12");

        verify(notificacionRepository).save(any(Notificacion.class));
    }

    @Test
    void notificarEntradaPagoFallido_guardaNotificacion() {
        Usuario usuario = crearUsuario(1L, true);
        usuario.setFcmtoken(null);
        when(notificacionRepository.save(any(Notificacion.class))).thenReturn(new Notificacion());

        service.notificarEntradaPagoFallido(usuario);

        verify(notificacionRepository).save(any(Notificacion.class));
    }

    @Test
    void notificarOrdenConfirmada_guardaNotificacion() {
        Usuario usuario = crearUsuario(1L, true);
        usuario.setFcmtoken(null);
        when(notificacionRepository.save(any(Notificacion.class))).thenReturn(new Notificacion());

        service.notificarOrdenConfirmada(usuario, 150000.0);

        verify(notificacionRepository).save(any(Notificacion.class));
    }

    @Test
    void notificarEntradaReembolsada_guardaNotificacion() {
        Usuario usuario = crearUsuario(1L, true);
        usuario.setFcmtoken(null);
        when(notificacionRepository.save(any(Notificacion.class))).thenReturn(new Notificacion());

        service.notificarEntradaReembolsada(usuario, 10L);

        verify(notificacionRepository).save(any(Notificacion.class));
    }

    @Test
    void notificarEntradaReembolsoFallido_guardaNotificacion() {
        Usuario usuario = crearUsuario(1L, true);
        usuario.setFcmtoken(null);
        when(notificacionRepository.save(any(Notificacion.class))).thenReturn(new Notificacion());

        service.notificarEntradaReembolsoFallido(usuario, 10L);

        verify(notificacionRepository).save(any(Notificacion.class));
    }

    @Test
    void notificarEntradaTransferida_guardaNotificacion() {
        Usuario usuario = crearUsuario(1L, true);
        usuario.setFcmtoken(null);
        when(notificacionRepository.save(any(Notificacion.class))).thenReturn(new Notificacion());

        service.notificarEntradaTransferida(usuario, "destino@test.com", COLOMBIA_VS_BRAZIL);

        verify(notificacionRepository).save(any(Notificacion.class));
    }

    @Test
    void notificarEntradaRecibida_guardaNotificacion() {
        Usuario usuario = crearUsuario(1L, true);
        usuario.setFcmtoken(null);
        when(notificacionRepository.save(any(Notificacion.class))).thenReturn(new Notificacion());

        service.notificarEntradaRecibida(usuario, "origen@test.com", COLOMBIA_VS_BRAZIL);

        verify(notificacionRepository).save(any(Notificacion.class));
    }

    @Test
    void notificarReservaExpirada_guardaNotificacion() {
        Usuario usuario = crearUsuario(1L, true);
        usuario.setFcmtoken(null);
        when(notificacionRepository.save(any(Notificacion.class))).thenReturn(new Notificacion());

        service.notificarReservaExpirada(usuario, COLOMBIA_VS_BRAZIL);

        verify(notificacionRepository).save(any(Notificacion.class));
    }

    @Test
    void notificarReservaPorExpirar_guardaNotificacion() {
        Usuario usuario = crearUsuario(1L, true);
        usuario.setFcmtoken(null);
        when(notificacionRepository.save(any(Notificacion.class))).thenReturn(new Notificacion());

        service.notificarReservaPorExpirar(usuario, COLOMBIA_VS_BRAZIL);

        verify(notificacionRepository).save(any(Notificacion.class));
    }

    @Test
    void notificarReservaCreada_guardaNotificacion() {
        Usuario usuario = crearUsuario(1L, true);
        usuario.setFcmtoken(null);
        when(notificacionRepository.save(any(Notificacion.class))).thenReturn(new Notificacion());

        service.notificarReservaCreada(usuario, COLOMBIA_VS_BRAZIL);

        verify(notificacionRepository).save(any(Notificacion.class));
    }

    @Test
    void notificarApuestaUnirse_guardaNotificacionParaAmbosUsuarios() {
        Usuario nuevo = crearUsuario(1L, true);
        nuevo.setFcmtoken(null);
        Usuario creador = crearUsuario(2L, true);
        creador.setFcmtoken(null);
        when(notificacionRepository.save(any(Notificacion.class))).thenReturn(new Notificacion());

        service.notificarApuestaUnirse(nuevo, creador, "Polla Mundial");

        verify(notificacionRepository, times(2)).save(any(Notificacion.class));
    }

    @Test
    void notificarApuestaCerrada_variosParticipantes_guardaTodasLasNotificaciones() {
        Usuario u1 = crearUsuario(1L, true);
        u1.setFcmtoken(null);
        Usuario u2 = crearUsuario(2L, true);
        u2.setFcmtoken(null);
        when(notificacionRepository.saveAll(any())).thenReturn(List.of());

        service.notificarApuestaCerrada(List.of(u1, u2), "Polla Mundial");

        verify(notificacionRepository).saveAll(argThat(list -> ((List<?>) list).size() == 2));
    }

    @Test
    void notificarApuestaCerrada_sinParticipantes_guardaListaVacia() {
        when(notificacionRepository.saveAll(any())).thenReturn(List.of());

        service.notificarApuestaCerrada(List.of(), "Polla Sin Gente");

        verify(notificacionRepository).saveAll(argThat(list -> ((List<?>) list).isEmpty()));
    }

    @Test
    void notificarPuntosCalculados_guardaNotificacion() {
        Usuario usuario = crearUsuario(1L, true);
        usuario.setFcmtoken(null);
        when(notificacionRepository.save(any(Notificacion.class))).thenReturn(new Notificacion());

        service.notificarPuntosCalculados(usuario, "Polla Mundial", 3, 120);

        verify(notificacionRepository).save(any(Notificacion.class));
    }

    @Test
    void notificarCarritoAbandonado_guardaNotificacion() {
        Usuario usuario = crearUsuario(1L, true);
        usuario.setFcmtoken(null);
        when(notificacionRepository.save(any(Notificacion.class))).thenReturn(new Notificacion());

        service.notificarCarritoAbandonado(usuario);

        verify(notificacionRepository).save(any(Notificacion.class));
    }

    @Test
    void notificarOrdenFallida_guardaNotificacion() {
        Usuario usuario = crearUsuario(1L, true);
        usuario.setFcmtoken(null);
        when(notificacionRepository.save(any(Notificacion.class))).thenReturn(new Notificacion());

        service.notificarOrdenFallida(usuario);

        verify(notificacionRepository).save(any(Notificacion.class));
    }

    @Test
    void listarPorUsuarioPaginado_retornaPaginaDTO() {
        Usuario usuario = crearUsuario(1L, true);
        Notificacion notificacion = crearNotificacion(1L, usuario, false);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Notificacion> page = new PageImpl<>(List.of(notificacion), pageable, 1);

        when(notificacionRepository.findByUsuarioIdOrderByFechaDesc(1L, pageable)).thenReturn(page);

        Page<NotificacionDTO> resultado = service.listarPorUsuarioPaginado(1L, pageable);

        assertNotNull(resultado);
        assertEquals(1, resultado.getTotalElements());
        assertEquals(1L, resultado.getContent().get(0).getId());
    }

    @Test
    void listarPorUsuarioPaginado_sinNotificaciones_retornaPaginaVacia() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Notificacion> page = new PageImpl<>(List.of(), pageable, 0);

        when(notificacionRepository.findByUsuarioIdOrderByFechaDesc(1L, pageable)).thenReturn(page);

        Page<NotificacionDTO> resultado = service.listarPorUsuarioPaginado(1L, pageable);

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    @Test
    void listarPorFecha_retornaPaginaDTO() {
        Usuario usuario = crearUsuario(1L, true);
        Notificacion notificacion = crearNotificacion(1L, usuario, false);
        Pageable pageable = PageRequest.of(0, 10);
        LocalDateTime desde = LocalDateTime.now().minusDays(7);
        LocalDateTime hasta = LocalDateTime.now();
        Page<Notificacion> page = new PageImpl<>(List.of(notificacion), pageable, 1);

        when(notificacionRepository.findByUsuarioIdAndFechaBetweenOrderByFechaDesc(1L, desde, hasta, pageable))
                .thenReturn(page);

        Page<NotificacionDTO> resultado = service.listarPorFecha(1L, desde, hasta, pageable);

        assertNotNull(resultado);
        assertEquals(1, resultado.getTotalElements());
    }

    @Test
    void listarPorFecha_sinResultados_retornaPaginaVacia() {
        Pageable pageable = PageRequest.of(0, 10);
        LocalDateTime desde = LocalDateTime.now().minusDays(1);
        LocalDateTime hasta = LocalDateTime.now();
        Page<Notificacion> page = new PageImpl<>(List.of(), pageable, 0);

        when(notificacionRepository.findByUsuarioIdAndFechaBetweenOrderByFechaDesc(1L, desde, hasta, pageable))
                .thenReturn(page);

        Page<NotificacionDTO> resultado = service.listarPorFecha(1L, desde, hasta, pageable);

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }
}