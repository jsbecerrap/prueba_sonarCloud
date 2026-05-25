package co.edu.unbosque.mundial_2026.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import co.edu.unbosque.mundial_2026.dto.CuposZonaDTO;
import co.edu.unbosque.mundial_2026.dto.request.EntradaRequestDTO;
import co.edu.unbosque.mundial_2026.dto.request.TransferenciaRequestDTO;
import co.edu.unbosque.mundial_2026.dto.response.EntradaResponseDTO;
import co.edu.unbosque.mundial_2026.entity.*;
import co.edu.unbosque.mundial_2026.exception.*;
import co.edu.unbosque.mundial_2026.repository.EntradaRepository;

/**
 * Pruebas unitarias para {@link EntradaServiceImpl}
 * Verifica la logica de negocio del servicio de entradas incluyendo reserva, pago, transferencia,
 * reembolso, expiracion de reservas y gestion de cupos por zona
 */
@ExtendWith(MockitoExtension.class)
class EntradaServiceImplTest {

    @Mock private EntradaRepository entradaRepository;
    @Mock private UsuarioService usuarioService;
    @Mock private PartidoService partidoService;
    @Mock private EventoAuditoriaService auditoriaService;
    @Mock private NotificacionService notificacionService;

    /** Correo del usuario principal de prueba usado en los tests de reserva y gestion de entradas */
    private static final String CORREO_USER1 = "user1@test.com";

    /** Categoria de zona barra usada como categoria por defecto en los tests de reserva */
    private static final String BARRA = "BARRA";

    /** Estado de entrada reservada usado en los tests de flujo de pago y cancelacion */
    private static final String RESERVADA = "RESERVADA";

    /** Estado de entrada pagada usado en los tests de transferencia y reembolso */
    private static final String PAGADA = "PAGADA";

    /** Correo de destino generico usado en los tests de transferencia con correo invalido */
    private static final String DESTINO = "destino@test.com";

    /** Correo del segundo usuario de prueba usado en los tests de transferencia exitosa */
    private static final String CORREO_USER2 = "user2@test.com";

    /** Instancia del servicio bajo prueba construida manualmente con dependencias mockeadas */
    private EntradaServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new EntradaServiceImpl(
                entradaRepository, usuarioService, partidoService,
                auditoriaService, notificacionService, "sk_test_dummy");
    }

    private Usuario crearUsuario(Long id) {
        Usuario u = new Usuario();
        u.setId(id);
        u.setNombre("Juan");
        u.setApellido("Perez");
        u.setCorreoUsuario("user" + id + "@test.com");
        return u;
    }

    private Partido crearPartido(Long id, int capacidad) {
        Partido p = new Partido();
        p.setId(id);
        p.setSeleccionLocal("Colombia");
        p.setSeleccionVisitante("Brazil");
        p.setRonda("Group Stage - 1");
        p.setEstadio("MetLife Stadium");
        p.setCapacidadDisponible(capacidad);
        return p;
    }

    private Entrada crearEntrada(Long id, Usuario usuario, Partido partido, String estado) {
        Entrada e = new Entrada();
        e.setId(id);
        e.setUsuario(usuario);
        e.setPartido(partido);
        e.setEstado(estado);
        e.setCantidad(2);
        e.setPrecio(100000.0);
        e.setCategoria(BARRA);
        e.setSector("Norte");
        e.setFila("A");
        e.setAsientoInicio(1);
        e.setFechaCompra(LocalDateTime.now());
        e.setTtlReserva(LocalDateTime.now().plusMinutes(15));
        return e;
    }

    private EntradaRequestDTO crearRequestDTO(Long partidoId, int cantidad, String categoria) {
        EntradaRequestDTO dto = new EntradaRequestDTO();
        dto.setPartidoId(partidoId);
        dto.setCantidad(cantidad);
        dto.setCategoria(categoria);
        dto.setSector("Norte");
        return dto;
    }

    /**
     * Verifica que reservar una entrada con datos validos retorna el DTO con estado RESERVADA
     * y descuenta la capacidad del partido
     */
    @Test
    void reservarEntrada_exitosa_retornaDTO() {
        Usuario usuario = crearUsuario(1L);
        Partido partido = crearPartido(1L, 60000);

        when(usuarioService.obtenerEntidadPorCorreo(CORREO_USER1)).thenReturn(usuario);
        when(partidoService.obtenerPartidoEntidadPorId(1L)).thenReturn(partido);
        when(entradaRepository.findByUsuarioIdAndFechaCompraBetween(eq(1L), any(), any())).thenReturn(List.of());
        when(entradaRepository.sumCantidadByPartidoAndEstados(eq(1L), anyList())).thenReturn(0);
        when(entradaRepository.sumCantidadByPartidoCategoriaYFila(eq(1L), eq(BARRA), eq("A"), anyList())).thenReturn(0);
        when(entradaRepository.maxAsientoFinByPartidoCategoriaYFila(eq(1L), eq(BARRA), eq("A"))).thenReturn(0);
        when(entradaRepository.save(any(Entrada.class))).thenAnswer(inv -> inv.getArgument(0));
        doNothing().when(partidoService).actualizarCapacidad(eq(1L), eq(-2));
        doNothing().when(auditoriaService).registrar(any(), any(), any(), any(), any());
        doNothing().when(notificacionService).notificarReservaCreada(any(), any());

        EntradaResponseDTO resultado = service.reservarEntrada(CORREO_USER1, crearRequestDTO(1L, 2, BARRA));

        assertNotNull(resultado);
        assertEquals(RESERVADA, resultado.getEstado());
        verify(entradaRepository).save(any(Entrada.class));
        verify(partidoService).actualizarCapacidad(1L, -2);
    }

    /**
     * Verifica que cuando la categoria en el DTO es nula se asigna BARRA como categoria por defecto
     */
    @Test
    void reservarEntrada_categoriaNull_usaBarra() {
        Usuario usuario = crearUsuario(1L);
        Partido partido = crearPartido(1L, 60000);

        when(usuarioService.obtenerEntidadPorCorreo(CORREO_USER1)).thenReturn(usuario);
        when(partidoService.obtenerPartidoEntidadPorId(1L)).thenReturn(partido);
        when(entradaRepository.findByUsuarioIdAndFechaCompraBetween(eq(1L), any(), any())).thenReturn(List.of());
        when(entradaRepository.sumCantidadByPartidoAndEstados(eq(1L), anyList())).thenReturn(0);
        when(entradaRepository.sumCantidadByPartidoCategoriaYFila(eq(1L), eq(BARRA), eq("A"), anyList())).thenReturn(0);
        when(entradaRepository.maxAsientoFinByPartidoCategoriaYFila(eq(1L), eq(BARRA), eq("A"))).thenReturn(0);
        when(entradaRepository.save(any(Entrada.class))).thenAnswer(inv -> inv.getArgument(0));
        doNothing().when(partidoService).actualizarCapacidad(eq(1L), eq(-1));
        doNothing().when(auditoriaService).registrar(any(), any(), any(), any(), any());
        doNothing().when(notificacionService).notificarReservaCreada(any(), any());

        EntradaRequestDTO dto = crearRequestDTO(1L, 1, null);
        EntradaResponseDTO resultado = service.reservarEntrada(CORREO_USER1, dto);

        assertNotNull(resultado);
        assertEquals(BARRA, resultado.getCategoria());
    }

    /**
     * Verifica que intentar reservar mas entradas de las disponibles lanza {@link CupoNoDisponibleException}
     */
    @Test
    void reservarEntrada_sinCapacidad_lanzaCupoNoDisponible() {
        Usuario usuario = crearUsuario(1L);
        Partido partido = crearPartido(1L, 1);

        when(usuarioService.obtenerEntidadPorCorreo(CORREO_USER1)).thenReturn(usuario);
        when(partidoService.obtenerPartidoEntidadPorId(1L)).thenReturn(partido);

        assertThrows(CupoNoDisponibleException.class,
                () -> service.reservarEntrada(CORREO_USER1, crearRequestDTO(1L, 2, BARRA)));
    }

    /**
     * Verifica que solicitar mas de cuatro entradas en una sola reserva lanza {@link LimiteSuperadoException}
     */
    @Test
    void reservarEntrada_cantidadMayorACuatro_lanzaLimiteSuperado() {
        Usuario usuario = crearUsuario(1L);
        Partido partido = crearPartido(1L, 60000);

        when(usuarioService.obtenerEntidadPorCorreo(CORREO_USER1)).thenReturn(usuario);
        when(partidoService.obtenerPartidoEntidadPorId(1L)).thenReturn(partido);

        assertThrows(LimiteSuperadoException.class,
                () -> service.reservarEntrada(CORREO_USER1, crearRequestDTO(1L, 5, BARRA)));
    }

    /**
     * Verifica que superar el limite diario con entradas pagadas lanza {@link LimiteSuperadoException}
     */
    @Test
    void reservarEntrada_limiteDiarioSuperado_lanzaLimiteSuperado() {
        Usuario usuario = crearUsuario(1L);
        Partido partido = crearPartido(1L, 60000);
        Entrada entradaHoy = crearEntrada(1L, usuario, partido, PAGADA);
        entradaHoy.setCantidad(11);

        when(usuarioService.obtenerEntidadPorCorreo(CORREO_USER1)).thenReturn(usuario);
        when(partidoService.obtenerPartidoEntidadPorId(1L)).thenReturn(partido);
        when(entradaRepository.findByUsuarioIdAndFechaCompraBetween(eq(1L), any(), any()))
                .thenReturn(List.of(entradaHoy));

        assertThrows(LimiteSuperadoException.class,
                () -> service.reservarEntrada(CORREO_USER1, crearRequestDTO(1L, 2, BARRA)));
    }

    /**
     * Verifica que las entradas reservadas del dia tambien cuentan para el limite diario
     */
    @Test
    void reservarEntrada_entradasHoyReservadas_contanEnLimite() {
        Usuario usuario = crearUsuario(1L);
        Partido partido = crearPartido(1L, 60000);
        Entrada entradaHoy = crearEntrada(1L, usuario, partido, RESERVADA);
        entradaHoy.setCantidad(11);

        when(usuarioService.obtenerEntidadPorCorreo(CORREO_USER1)).thenReturn(usuario);
        when(partidoService.obtenerPartidoEntidadPorId(1L)).thenReturn(partido);
        when(entradaRepository.findByUsuarioIdAndFechaCompraBetween(eq(1L), any(), any()))
                .thenReturn(List.of(entradaHoy));

        assertThrows(LimiteSuperadoException.class,
                () -> service.reservarEntrada(CORREO_USER1, crearRequestDTO(1L, 2, BARRA)));
    }

    /**
     * Verifica que las entradas canceladas del dia no cuentan para el limite diario
     */
    @Test
    void reservarEntrada_entradasHoyOtroEstado_noContanEnLimite() {
        Usuario usuario = crearUsuario(1L);
        Partido partido = crearPartido(1L, 60000);
        Entrada entradaCancelada = crearEntrada(1L, usuario, partido, "CANCELADA");
        entradaCancelada.setCantidad(11);

        when(usuarioService.obtenerEntidadPorCorreo(CORREO_USER1)).thenReturn(usuario);
        when(partidoService.obtenerPartidoEntidadPorId(1L)).thenReturn(partido);
        when(entradaRepository.findByUsuarioIdAndFechaCompraBetween(eq(1L), any(), any()))
                .thenReturn(List.of(entradaCancelada));
        when(entradaRepository.sumCantidadByPartidoAndEstados(eq(1L), anyList())).thenReturn(0);
        when(entradaRepository.sumCantidadByPartidoCategoriaYFila(eq(1L), eq(BARRA), eq("A"), anyList())).thenReturn(0);
        when(entradaRepository.maxAsientoFinByPartidoCategoriaYFila(eq(1L), eq(BARRA), eq("A"))).thenReturn(0);
        when(entradaRepository.save(any(Entrada.class))).thenAnswer(inv -> inv.getArgument(0));
        doNothing().when(partidoService).actualizarCapacidad(any(), anyInt());
        doNothing().when(auditoriaService).registrar(any(), any(), any(), any(), any());
        doNothing().when(notificacionService).notificarReservaCreada(any(), any());

        EntradaResponseDTO resultado = service.reservarEntrada(CORREO_USER1, crearRequestDTO(1L, 1, BARRA));

        assertNotNull(resultado);
    }

    /**
     * Verifica que solicitar una categoria de zona inexistente lanza {@link CupoNoDisponibleException}
     */
    @Test
    void reservarEntrada_zonaInvalida_lanzaCupoNoDisponible() {
        Usuario usuario = crearUsuario(1L);
        Partido partido = crearPartido(1L, 60000);

        when(usuarioService.obtenerEntidadPorCorreo(CORREO_USER1)).thenReturn(usuario);
        when(partidoService.obtenerPartidoEntidadPorId(1L)).thenReturn(partido);
        when(entradaRepository.findByUsuarioIdAndFechaCompraBetween(eq(1L), any(), any())).thenReturn(List.of());
        when(entradaRepository.sumCantidadByPartidoAndEstados(eq(1L), anyList())).thenReturn(0);

        assertThrows(CupoNoDisponibleException.class,
                () -> service.reservarEntrada(CORREO_USER1, crearRequestDTO(1L, 1, "ZONA_INEXISTENTE")));
    }

    /**
     * Verifica que cuando todas las filas de la zona estan llenas lanza {@link CupoNoDisponibleException}
     */
    @Test
    void reservarEntrada_todasFilasLlenas_lanzaCupoNoDisponible() {
        Usuario usuario = crearUsuario(1L);
        Partido partido = crearPartido(1L, 60000);

        when(usuarioService.obtenerEntidadPorCorreo(CORREO_USER1)).thenReturn(usuario);
        when(partidoService.obtenerPartidoEntidadPorId(1L)).thenReturn(partido);
        when(entradaRepository.findByUsuarioIdAndFechaCompraBetween(eq(1L), any(), any())).thenReturn(List.of());
        when(entradaRepository.sumCantidadByPartidoAndEstados(eq(1L), anyList())).thenReturn(0);
        when(entradaRepository.sumCantidadByPartidoCategoriaYFila(eq(1L), eq(BARRA), anyString(), anyList()))
                .thenReturn(999999);

        assertThrows(CupoNoDisponibleException.class,
                () -> service.reservarEntrada(CORREO_USER1, crearRequestDTO(1L, 1, BARRA)));
    }

    /**
     * Verifica que partidos con selecciones top incrementan el precio base de la entrada
     */
    @Test
    void reservarEntrada_seleccionTop_incrementaPrecio() {
        Usuario usuario = crearUsuario(1L);
        Partido partido = crearPartido(1L, 60000);
        partido.setSeleccionLocal("Colombia");
        partido.setSeleccionVisitante("Brazil");
        partido.setRonda("Group Stage - 1");

        when(usuarioService.obtenerEntidadPorCorreo(CORREO_USER1)).thenReturn(usuario);
        when(partidoService.obtenerPartidoEntidadPorId(1L)).thenReturn(partido);
        when(entradaRepository.findByUsuarioIdAndFechaCompraBetween(eq(1L), any(), any())).thenReturn(List.of());
        when(entradaRepository.sumCantidadByPartidoAndEstados(eq(1L), anyList())).thenReturn(0);
        when(entradaRepository.sumCantidadByPartidoCategoriaYFila(eq(1L), eq(BARRA), eq("A"), anyList())).thenReturn(0);
        when(entradaRepository.maxAsientoFinByPartidoCategoriaYFila(eq(1L), eq(BARRA), eq("A"))).thenReturn(5);
        when(entradaRepository.save(any(Entrada.class))).thenAnswer(inv -> inv.getArgument(0));
        doNothing().when(partidoService).actualizarCapacidad(any(), anyInt());
        doNothing().when(auditoriaService).registrar(any(), any(), any(), any(), any());
        doNothing().when(notificacionService).notificarReservaCreada(any(), any());

        EntradaResponseDTO resultado = service.reservarEntrada(CORREO_USER1, crearRequestDTO(1L, 1, BARRA));

        assertNotNull(resultado);
        assertTrue(resultado.getPrecio() > 50000.0);
    }

    /**
     * Verifica que cancelar una reserva propia retorna el DTO con estado CANCELADA
     * y devuelve la capacidad al partido
     */
    @Test
    void cancelarReserva_exitosa_retornaDTO() {
        Usuario usuario = crearUsuario(1L);
        Partido partido = crearPartido(1L, 1000);
        Entrada entrada = crearEntrada(1L, usuario, partido, RESERVADA);

        when(usuarioService.obtenerEntidadPorCorreo(CORREO_USER1)).thenReturn(usuario);
        when(entradaRepository.findById(1L)).thenReturn(Optional.of(entrada));
        when(entradaRepository.save(any(Entrada.class))).thenAnswer(inv -> inv.getArgument(0));
        doNothing().when(partidoService).actualizarCapacidad(1L, 2);
        doNothing().when(auditoriaService).registrar(any(), any(), any(), any(), any());

        EntradaResponseDTO resultado = service.cancelarReserva(CORREO_USER1, 1L);

        assertNotNull(resultado);
        assertEquals("CANCELADA", resultado.getEstado());
        verify(partidoService).actualizarCapacidad(1L, 2);
    }

    /**
     * Verifica que cancelar una entrada inexistente lanza {@link EntradaNotFoundException}
     */
    @Test
    void cancelarReserva_entradaNoEncontrada_lanzaExcepcion() {
        Usuario usuario = crearUsuario(1L);

        when(usuarioService.obtenerEntidadPorCorreo(CORREO_USER1)).thenReturn(usuario);
        when(entradaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(EntradaNotFoundException.class,
                () -> service.cancelarReserva(CORREO_USER1, 99L));
    }

    /**
     * Verifica que cancelar una entrada que pertenece a otro usuario lanza {@link EstadoInvalidoException}
     */
    @Test
    void cancelarReserva_entradaNoPertenece_lanzaExcepcion() {
        Usuario usuario = crearUsuario(1L);
        Usuario otroUsuario = crearUsuario(2L);
        Partido partido = crearPartido(1L, 1000);
        Entrada entrada = crearEntrada(1L, otroUsuario, partido, RESERVADA);

        when(usuarioService.obtenerEntidadPorCorreo(CORREO_USER1)).thenReturn(usuario);
        when(entradaRepository.findById(1L)).thenReturn(Optional.of(entrada));

        assertThrows(EstadoInvalidoException.class,
                () -> service.cancelarReserva(CORREO_USER1, 1L));
    }

    /**
     * Verifica que cancelar una entrada con estado no cancelable lanza {@link EstadoInvalidoException}
     */
    @Test
    void cancelarReserva_estadoNoCancelable_lanzaExcepcion() {
        Usuario usuario = crearUsuario(1L);
        Partido partido = crearPartido(1L, 1000);
        Entrada entrada = crearEntrada(1L, usuario, partido, PAGADA);

        when(usuarioService.obtenerEntidadPorCorreo(CORREO_USER1)).thenReturn(usuario);
        when(entradaRepository.findById(1L)).thenReturn(Optional.of(entrada));

        assertThrows(EstadoInvalidoException.class,
                () -> service.cancelarReserva(CORREO_USER1, 1L));
    }

    /**
     * Verifica que confirmar el pago de una entrada inexistente lanza {@link EntradaNotFoundException}
     */
    @Test
    void confirmarPago_entradaNoEncontrada_lanzaExcepcion() {
        when(entradaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(EntradaNotFoundException.class,
                () -> service.confirmarPago(99L, "pm_test"));
    }

    /**
     * Verifica que confirmar el pago de una entrada que no esta en estado RESERVADA lanza {@link EstadoInvalidoException}
     */
    @Test
    void confirmarPago_estadoNoReservada_lanzaExcepcion() {
        Usuario usuario = crearUsuario(1L);
        Partido partido = crearPartido(1L, 1000);
        Entrada entrada = crearEntrada(1L, usuario, partido, PAGADA);

        when(entradaRepository.findById(1L)).thenReturn(Optional.of(entrada));

        assertThrows(EstadoInvalidoException.class,
                () -> service.confirmarPago(1L, "pm_test"));
    }

    /**
     * Verifica que confirmar el pago de una reserva expirada lanza {@link EstadoInvalidoException}
     */
    @Test
    void confirmarPago_reservaExpirada_lanzaExcepcion() {
        Usuario usuario = crearUsuario(1L);
        Partido partido = crearPartido(1L, 1000);
        Entrada entrada = crearEntrada(1L, usuario, partido, RESERVADA);
        entrada.setTtlReserva(LocalDateTime.now().minusMinutes(1));

        when(entradaRepository.findById(1L)).thenReturn(Optional.of(entrada));

        assertThrows(EstadoInvalidoException.class,
                () -> service.confirmarPago(1L, "pm_test"));
    }

    /**
     * Verifica que transferir una entrada inexistente lanza {@link EntradaNotFoundException}
     */
    @Test
    void transferirEntrada_entradaNoEncontrada_lanzaExcepcion() {
        Usuario usuario = crearUsuario(1L);
        TransferenciaRequestDTO dto = new TransferenciaRequestDTO();
        dto.setCorreoDestino(DESTINO);

        when(usuarioService.obtenerEntidadPorCorreo(CORREO_USER1)).thenReturn(usuario);
        when(entradaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(EntradaNotFoundException.class,
                () -> service.transferirEntrada(99L, dto, CORREO_USER1));
    }

    /**
     * Verifica que transferir una entrada que pertenece a otro usuario lanza {@link EstadoInvalidoException}
     */
    @Test
    void transferirEntrada_entradaNoPertenece_lanzaExcepcion() {
        Usuario usuario = crearUsuario(1L);
        Usuario otroUsuario = crearUsuario(2L);
        Partido partido = crearPartido(1L, 1000);
        Entrada entrada = crearEntrada(1L, otroUsuario, partido, PAGADA);
        TransferenciaRequestDTO dto = new TransferenciaRequestDTO();
        dto.setCorreoDestino(DESTINO);

        when(usuarioService.obtenerEntidadPorCorreo(CORREO_USER1)).thenReturn(usuario);
        when(entradaRepository.findById(1L)).thenReturn(Optional.of(entrada));

        assertThrows(EstadoInvalidoException.class,
                () -> service.transferirEntrada(1L, dto, CORREO_USER1));
    }

    /**
     * Verifica que transferir una entrada que no esta en estado PAGADA lanza {@link EstadoInvalidoException}
     */
    @Test
    void transferirEntrada_estadoNoPagada_lanzaExcepcion() {
        Usuario usuario = crearUsuario(1L);
        Partido partido = crearPartido(1L, 1000);
        Entrada entrada = crearEntrada(1L, usuario, partido, RESERVADA);
        TransferenciaRequestDTO dto = new TransferenciaRequestDTO();
        dto.setCorreoDestino(DESTINO);

        when(usuarioService.obtenerEntidadPorCorreo(CORREO_USER1)).thenReturn(usuario);
        when(entradaRepository.findById(1L)).thenReturn(Optional.of(entrada));

        assertThrows(EstadoInvalidoException.class,
                () -> service.transferirEntrada(1L, dto, CORREO_USER1));
    }

    /**
     * Verifica que superar el limite diario de transferencias lanza {@link LimiteSuperadoException}
     */
    @Test
    void transferirEntrada_limiteDiarioSuperado_lanzaLimiteSuperado() {
        Usuario usuario = crearUsuario(1L);
        Partido partido = crearPartido(1L, 1000);
        Entrada entrada = crearEntrada(1L, usuario, partido, PAGADA);
        entrada.setCantidad(2);
        Entrada yaTransferida = crearEntrada(2L, usuario, partido, "TRANSFERIDA");
        yaTransferida.setCantidad(11);
        TransferenciaRequestDTO dto = new TransferenciaRequestDTO();
        dto.setCorreoDestino(DESTINO);

        when(usuarioService.obtenerEntidadPorCorreo(CORREO_USER1)).thenReturn(usuario);
        when(entradaRepository.findById(1L)).thenReturn(Optional.of(entrada));
        when(entradaRepository.findByUsuarioIdAndFechaCompraBetween(eq(1L), any(), any()))
                .thenReturn(List.of(yaTransferida));

        assertThrows(LimiteSuperadoException.class,
                () -> service.transferirEntrada(1L, dto, CORREO_USER1));
    }

    /**
     * Verifica que transferir una entrada valida al usuario destino retorna el DTO con estado PAGADA
     * y persiste dos entradas: la original como transferida y la nueva del destinatario
     */
    @Test
    void transferirEntrada_exitosa_retornaDTO() {
        Usuario usuario = crearUsuario(1L);
        Usuario destino = crearUsuario(2L);
        Partido partido = crearPartido(1L, 1000);
        Entrada entrada = crearEntrada(1L, usuario, partido, PAGADA);
        TransferenciaRequestDTO dto = new TransferenciaRequestDTO();
        dto.setCorreoDestino(CORREO_USER2);

        when(usuarioService.obtenerEntidadPorCorreo(CORREO_USER1)).thenReturn(usuario);
        when(usuarioService.obtenerEntidadPorCorreo(CORREO_USER2)).thenReturn(destino);
        when(entradaRepository.findById(1L)).thenReturn(Optional.of(entrada));
        when(entradaRepository.findByUsuarioIdAndFechaCompraBetween(eq(1L), any(), any())).thenReturn(List.of());
        when(entradaRepository.save(any(Entrada.class))).thenAnswer(inv -> inv.getArgument(0));
        doNothing().when(auditoriaService).registrar(any(), any(), any(), any(), any());
        doNothing().when(notificacionService).notificarEntradaTransferida(any(), any(), any());
        doNothing().when(notificacionService).notificarEntradaRecibida(any(), any(), any());

        EntradaResponseDTO resultado = service.transferirEntrada(1L, dto, CORREO_USER1);

        assertNotNull(resultado);
        assertEquals(PAGADA, resultado.getEstado());
        verify(entradaRepository, times(2)).save(any(Entrada.class));
    }

    /**
     * Verifica que las entradas pagadas del dia no cuentan para el limite diario de transferencias
     */
    @Test
    void transferirEntrada_entradasHoyNoTransferidas_noContanEnLimite() {
        Usuario usuario = crearUsuario(1L);
        Usuario destino = crearUsuario(2L);
        Partido partido = crearPartido(1L, 1000);
        Entrada entrada = crearEntrada(1L, usuario, partido, PAGADA);
        Entrada entradaPagadaHoy = crearEntrada(3L, usuario, partido, PAGADA);
        entradaPagadaHoy.setCantidad(10);
        TransferenciaRequestDTO dto = new TransferenciaRequestDTO();
        dto.setCorreoDestino(CORREO_USER2);

        when(usuarioService.obtenerEntidadPorCorreo(CORREO_USER1)).thenReturn(usuario);
        when(usuarioService.obtenerEntidadPorCorreo(CORREO_USER2)).thenReturn(destino);
        when(entradaRepository.findById(1L)).thenReturn(Optional.of(entrada));
        when(entradaRepository.findByUsuarioIdAndFechaCompraBetween(eq(1L), any(), any()))
                .thenReturn(List.of(entradaPagadaHoy));
        when(entradaRepository.save(any(Entrada.class))).thenAnswer(inv -> inv.getArgument(0));
        doNothing().when(auditoriaService).registrar(any(), any(), any(), any(), any());
        doNothing().when(notificacionService).notificarEntradaTransferida(any(), any(), any());
        doNothing().when(notificacionService).notificarEntradaRecibida(any(), any(), any());

        EntradaResponseDTO resultado = service.transferirEntrada(1L, dto, CORREO_USER1);

        assertNotNull(resultado);
    }

    /**
     * Verifica que reembolsar una entrada inexistente lanza {@link EntradaNotFoundException}
     */
    @Test
    void reembolsarEntrada_entradaNoEncontrada_lanzaExcepcion() {
        Usuario usuario = crearUsuario(1L);

        when(usuarioService.obtenerEntidadPorCorreo(CORREO_USER1)).thenReturn(usuario);
        when(entradaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(EntradaNotFoundException.class,
                () -> service.reembolsarEntrada(CORREO_USER1, 99L));
    }

    /**
     * Verifica que reembolsar una entrada que pertenece a otro usuario lanza {@link EstadoInvalidoException}
     */
    @Test
    void reembolsarEntrada_entradaNoPertenece_lanzaExcepcion() {
        Usuario usuario = crearUsuario(1L);
        Usuario otroUsuario = crearUsuario(2L);
        Partido partido = crearPartido(1L, 1000);
        Entrada entrada = crearEntrada(1L, otroUsuario, partido, PAGADA);

        when(usuarioService.obtenerEntidadPorCorreo(CORREO_USER1)).thenReturn(usuario);
        when(entradaRepository.findById(1L)).thenReturn(Optional.of(entrada));

        assertThrows(EstadoInvalidoException.class,
                () -> service.reembolsarEntrada(CORREO_USER1, 1L));
    }

    /**
     * Verifica que reembolsar una entrada que no esta en estado PAGADA lanza {@link EstadoInvalidoException}
     */
    @Test
    void reembolsarEntrada_estadoNoPagada_lanzaExcepcion() {
        Usuario usuario = crearUsuario(1L);
        Partido partido = crearPartido(1L, 1000);
        Entrada entrada = crearEntrada(1L, usuario, partido, RESERVADA);

        when(usuarioService.obtenerEntidadPorCorreo(CORREO_USER1)).thenReturn(usuario);
        when(entradaRepository.findById(1L)).thenReturn(Optional.of(entrada));

        assertThrows(EstadoInvalidoException.class,
                () -> service.reembolsarEntrada(CORREO_USER1, 1L));
    }

    /**
     * Verifica que listar entradas de un usuario retorna la lista ordenada por fecha de compra descendente
     */
    @Test
    void listarEntradasUsuario_retornaListaOrdenada() {
        Usuario usuario = crearUsuario(1L);
        Partido partido = crearPartido(1L, 1000);
        Entrada e1 = crearEntrada(1L, usuario, partido, PAGADA);
        e1.setFechaCompra(LocalDateTime.now().minusDays(1));
        Entrada e2 = crearEntrada(2L, usuario, partido, RESERVADA);
        e2.setFechaCompra(LocalDateTime.now());

        when(usuarioService.obtenerEntidadPorCorreo(CORREO_USER1)).thenReturn(usuario);
        when(entradaRepository.findByUsuarioId(1L)).thenReturn(List.of(e1, e2));

        List<EntradaResponseDTO> resultado = service.listarEntradasUsuario(CORREO_USER1);

        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals(2L, resultado.get(0).getId());
    }

    /**
     * Verifica que listar entradas de un usuario sin entradas retorna lista vacia
     */
    @Test
    void listarEntradasUsuario_sinEntradas_retornaVacio() {
        Usuario usuario = crearUsuario(1L);

        when(usuarioService.obtenerEntidadPorCorreo(CORREO_USER1)).thenReturn(usuario);
        when(entradaRepository.findByUsuarioId(1L)).thenReturn(List.of());

        List<EntradaResponseDTO> resultado = service.listarEntradasUsuario(CORREO_USER1);

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    /**
     * Verifica que obtener una entrada existente retorna el DTO con el ID correcto
     */
    @Test
    void obtenerEntrada_existente_retornaDTO() {
        Usuario usuario = crearUsuario(1L);
        Partido partido = crearPartido(1L, 1000);
        Entrada entrada = crearEntrada(1L, usuario, partido, PAGADA);

        when(entradaRepository.findById(1L)).thenReturn(Optional.of(entrada));

        EntradaResponseDTO resultado = service.obtenerEntrada(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
    }

    /**
     * Verifica que obtener una entrada inexistente lanza {@link EntradaNotFoundException}
     */
    @Test
    void obtenerEntrada_noExistente_lanzaExcepcion() {
        when(entradaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(EntradaNotFoundException.class, () -> service.obtenerEntrada(99L));
    }

    /**
     * Verifica que expirar reservas vencidas cambia su estado a EXPIRADA
     * y devuelve la capacidad al partido correspondiente
     */
    @Test
    void expirarReservasVencidas_conReservas_expiraYActualizaCapacidad() {
        Usuario usuario = crearUsuario(1L);
        Partido partido = crearPartido(1L, 1000);
        Entrada entrada = crearEntrada(1L, usuario, partido, RESERVADA);
        entrada.setTtlReserva(LocalDateTime.now().minusMinutes(1));

        when(entradaRepository.findByEstadoAndTtlReservaLessThan(eq(RESERVADA), any())).thenReturn(List.of(entrada));
        when(entradaRepository.save(any(Entrada.class))).thenAnswer(inv -> inv.getArgument(0));
        doNothing().when(partidoService).actualizarCapacidad(eq(1L), eq(2));
        doNothing().when(auditoriaService).registrar(any(), any(), any(), any(), any());
        doNothing().when(notificacionService).notificarReservaExpirada(any(), any());

        service.expirarReservasVencidas();

        assertEquals("EXPIRADA", entrada.getEstado());
        verify(partidoService).actualizarCapacidad(1L, 2);
        verify(notificacionService).notificarReservaExpirada(any(), any());
    }

    /**
     * Verifica que cuando no hay reservas vencidas no se persiste nada ni se actualiza capacidad
     */
    @Test
    void expirarReservasVencidas_sinReservas_noHaceNada() {
        when(entradaRepository.findByEstadoAndTtlReservaLessThan(eq(RESERVADA), any())).thenReturn(List.of());

        service.expirarReservasVencidas();

        verify(entradaRepository, never()).save(any());
        verify(partidoService, never()).actualizarCapacidad(any(), anyInt());
    }

    /**
     * Verifica que cuando hay reservas proximas a expirar se notifica a cada usuario correspondiente
     */
    @Test
    void avisarReservasPorExpirar_conReservas_notificaUsuarios() {
        Usuario usuario = crearUsuario(1L);
        Partido partido = crearPartido(1L, 1000);
        Entrada entrada = crearEntrada(1L, usuario, partido, RESERVADA);

        when(entradaRepository.findByEstadoAndTtlReservaBetween(eq(RESERVADA), any(), any()))
                .thenReturn(List.of(entrada));
        doNothing().when(notificacionService).notificarReservaPorExpirar(any(), any());

        service.avisarReservasPorExpirar();

        verify(notificacionService).notificarReservaPorExpirar(any(), any());
    }

    /**
     * Verifica que cuando no hay reservas proximas a expirar no se envia ninguna notificacion
     */
    @Test
    void avisarReservasPorExpirar_sinReservas_noNotifica() {
        when(entradaRepository.findByEstadoAndTtlReservaBetween(eq(RESERVADA), any(), any()))
                .thenReturn(List.of());

        service.avisarReservasPorExpirar();

        verify(notificacionService, never()).notificarReservaPorExpirar(any(), any());
    }

    /**
     * Verifica que listarPartidosConCapacidad delega correctamente al servicio de partidos
     */
    @Test
    void listarPartidosConCapacidad_delegaAPartidoService() {
        when(partidoService.listarPartidosConCapacidad()).thenReturn(List.of());

        service.listarPartidosConCapacidad();

        verify(partidoService).listarPartidosConCapacidad();
    }

    /**
     * Verifica que obtenerCuposPorZona retorna exactamente cuatro zonas para el partido indicado
     */
    @Test
    void obtenerCuposPorZona_retornaCuatroZonas() {
        Partido partido = crearPartido(1L, 60000);

        when(partidoService.obtenerPartidoEntidadPorId(1L)).thenReturn(partido);
        when(entradaRepository.sumCantidadByPartidoAndEstados(eq(1L), anyList())).thenReturn(0);
        when(entradaRepository.sumCantidadByPartidoAndCategoriaAndEstados(eq(1L), anyString(), anyList())).thenReturn(0);
        when(entradaRepository.sumCantidadByPartidoCategoriaYFila(eq(1L), anyString(), anyString(), anyList())).thenReturn(0);

        List<CuposZonaDTO> resultado = service.obtenerCuposPorZona(1L);

        assertNotNull(resultado);
        assertEquals(4, resultado.size());
    }

    /**
     * Verifica que cuando la capacidad del partido es nula se usa cero como valor base para los cupos
     */
    @Test
    void obtenerCuposPorZona_capacidadNull_usaCero() {
        Partido partido = crearPartido(1L, 0);
        partido.setCapacidadDisponible(null);

        when(partidoService.obtenerPartidoEntidadPorId(1L)).thenReturn(partido);
        when(entradaRepository.sumCantidadByPartidoAndEstados(eq(1L), anyList())).thenReturn(0);
        when(entradaRepository.sumCantidadByPartidoAndCategoriaAndEstados(eq(1L), anyString(), anyList())).thenReturn(0);
        when(entradaRepository.sumCantidadByPartidoCategoriaYFila(eq(1L), anyString(), anyString(), anyList())).thenReturn(0);

        List<CuposZonaDTO> resultado = service.obtenerCuposPorZona(1L);

        assertNotNull(resultado);
        assertEquals(4, resultado.size());
    }

    /**
     * Verifica que cuando hay entradas vendidas los cupos por zona se reportan con seis filas por zona
     */
    @Test
    void obtenerCuposPorZona_conVendidos_reportaCorrectamente() {
        Partido partido = crearPartido(1L, 58000);

        when(partidoService.obtenerPartidoEntidadPorId(1L)).thenReturn(partido);
        when(entradaRepository.sumCantidadByPartidoAndEstados(eq(1L), anyList())).thenReturn(2000);
        when(entradaRepository.sumCantidadByPartidoAndCategoriaAndEstados(eq(1L), anyString(), anyList())).thenReturn(100);
        when(entradaRepository.sumCantidadByPartidoCategoriaYFila(eq(1L), anyString(), anyString(), anyList())).thenReturn(10);

        List<CuposZonaDTO> resultado = service.obtenerCuposPorZona(1L);

        assertNotNull(resultado);
        assertEquals(4, resultado.size());
        resultado.forEach(z -> assertEquals(6, z.getFilas().size()));
    }
}