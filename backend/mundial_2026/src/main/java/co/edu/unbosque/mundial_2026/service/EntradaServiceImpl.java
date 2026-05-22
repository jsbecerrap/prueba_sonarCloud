package co.edu.unbosque.mundial_2026.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.model.Refund;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.RefundCreateParams;

import co.edu.unbosque.mundial_2026.dto.CuposZonaDTO;
import co.edu.unbosque.mundial_2026.dto.CuposFilaDTO;
import co.edu.unbosque.mundial_2026.dto.PartidoCapacidadDTO;
import co.edu.unbosque.mundial_2026.dto.request.EntradaRequestDTO;
import co.edu.unbosque.mundial_2026.dto.request.TransferenciaRequestDTO;
import co.edu.unbosque.mundial_2026.dto.response.EntradaResponseDTO;
import co.edu.unbosque.mundial_2026.entity.Entrada;
import co.edu.unbosque.mundial_2026.entity.Partido;
import co.edu.unbosque.mundial_2026.entity.Usuario;
import co.edu.unbosque.mundial_2026.exception.CupoNoDisponibleException;
import co.edu.unbosque.mundial_2026.exception.EntradaNotFoundException;
import co.edu.unbosque.mundial_2026.exception.EstadoInvalidoException;
import co.edu.unbosque.mundial_2026.exception.LimiteSuperadoException;
import co.edu.unbosque.mundial_2026.exception.PagoStripeException;
import co.edu.unbosque.mundial_2026.repository.EntradaRepository;



@Service
public class EntradaServiceImpl implements EntradaService {

    private final EntradaRepository entradaRepository;
    private final UsuarioService usuarioService;
    private final PartidoService partidoService;
    private final EventoAuditoriaService auditoriaService;

    private static final String ESTADO_RESERVADA = "RESERVADA";
    private static final String ESTADO_PAGADA = "PAGADA";
    private static final String ESTADO_TRANSFERIDA = "TRANSFERIDA";
    private static final String ENTRADA_NO_ENCONTRADA = "Entrada no encontrada";
    private static final String TIPO_ENTRADA = "ENTRADA";
    private static final String PREFIJO_USUARIO = "Usuario ";
    private static final String ENTRADA_NO_PERTENECE = "Esta entrada no pertenece al usuario";
    private static final String ZONA_BARRA = "BARRA";
    private static final String ZONA_GENERAL = "GENERAL";
    private static final String ZONA_PALCO = "PALCO";
    private static final String ZONA_ESQUINA = "ESQUINA";

    private final NotificacionService notificacionService;

    private static final Map<String, Long> PRECIO_POR_RONDA = new HashMap<>();
    static {
        PRECIO_POR_RONDA.put("Group Stage - 1", 50000L);
        PRECIO_POR_RONDA.put("Group Stage - 2", 50000L);
        PRECIO_POR_RONDA.put("Group Stage - 3", 50000L);
        PRECIO_POR_RONDA.put("Round of 32", 80000L);
        PRECIO_POR_RONDA.put("Round of 16", 100000L);
        PRECIO_POR_RONDA.put("Quarter-finals", 150000L);
        PRECIO_POR_RONDA.put("Semi-finals", 200000L);
        PRECIO_POR_RONDA.put("3rd Place Final", 180000L);
        PRECIO_POR_RONDA.put("Final", 300000L);
    }
    private static final Set<String> SELECCIONES_TOP = new HashSet<>();
    static {
        SELECCIONES_TOP.add("France");
        SELECCIONES_TOP.add("Spain");
        SELECCIONES_TOP.add("Argentina");
        SELECCIONES_TOP.add("England");
        SELECCIONES_TOP.add("Portugal");
        SELECCIONES_TOP.add("Brazil");
        SELECCIONES_TOP.add("Netherlands");
        SELECCIONES_TOP.add("Morocco");
        SELECCIONES_TOP.add("Belgium");
        SELECCIONES_TOP.add("Germany");
        SELECCIONES_TOP.add("Colombia");
    }
    private static final Map<String, Double> MULTIPLICADOR_CATEGORIA = new HashMap<>();
    static {
        MULTIPLICADOR_CATEGORIA.put(ZONA_BARRA, 1.0);
        MULTIPLICADOR_CATEGORIA.put(ZONA_GENERAL, 2.0);
        MULTIPLICADOR_CATEGORIA.put(ZONA_PALCO, 3.5);
        MULTIPLICADOR_CATEGORIA.put(ZONA_ESQUINA, 0.7);
    }
    private static final Map<String, Double> PORCENTAJE_ZONA = new HashMap<>();
    static {
        PORCENTAJE_ZONA.put(ZONA_BARRA, 0.33);
        PORCENTAJE_ZONA.put(ZONA_GENERAL, 0.37);
        PORCENTAJE_ZONA.put(ZONA_PALCO, 0.15);
        PORCENTAJE_ZONA.put(ZONA_ESQUINA, 0.15);
    }

    private static final List<String> FILAS = List.of("A", "B", "C", "D", "E", "F");
    private static final List<String> ZONAS = List.of(ZONA_BARRA, ZONA_GENERAL, ZONA_PALCO, ZONA_ESQUINA);
    private static final List<String> ESTADOS_ACTIVOS = List.of(ESTADO_RESERVADA, ESTADO_PAGADA);

    public EntradaServiceImpl(EntradaRepository entradaRepository,
        UsuarioService usuarioService,
        PartidoService partidoService,
        EventoAuditoriaService auditoriaService,
        NotificacionService notificacionService,
        @Value("${stripe.api.key}") String stripeApiKey) {
        this.entradaRepository = entradaRepository;
        this.usuarioService = usuarioService;
        this.partidoService = partidoService;
        this.auditoriaService = auditoriaService;
        this.notificacionService = notificacionService;
        Stripe.apiKey = stripeApiKey;
    }

@Transactional
@Override
public EntradaResponseDTO reservarEntrada(String correo, EntradaRequestDTO dto) {
    Usuario u = usuarioService.obtenerEntidadPorCorreo(correo);
    Long usuarioId = u.getId();

    Partido partido = partidoService.obtenerPartidoEntidadPorId(dto.getPartidoId());

    if (partido.getCapacidadDisponible() < dto.getCantidad()) {
        throw new CupoNoDisponibleException("No hay cupo disponible para este partido");
    }

    if (dto.getCantidad() > 4) {
        throw new LimiteSuperadoException("Máximo 4 entradas por transacción");
    }

    LocalDateTime inicioDia = LocalDateTime.now().toLocalDate().atStartOfDay();
    LocalDateTime finDia = inicioDia.plusDays(1);

    List<Entrada> entradasHoy = entradaRepository.findByUsuarioIdAndFechaCompraBetween(usuarioId, inicioDia, finDia);

    int compradasHoy = 0;
    for (Entrada entrada : entradasHoy) {
        if (ESTADO_RESERVADA.equals(entrada.getEstado()) || ESTADO_PAGADA.equals(entrada.getEstado())) {
            compradasHoy += entrada.getCantidad();
        }
    }

    if (compradasHoy + dto.getCantidad() > 12) {
        throw new LimiteSuperadoException("Límite diario de 12 entradas alcanzado");
    }

    String categoria = (dto.getCategoria() != null && !dto.getCategoria().isBlank())
            ? dto.getCategoria().toUpperCase(Locale.ROOT)
            : ZONA_BARRA;
    String sector = (dto.getSector() != null && !dto.getSector().isBlank())
            ? dto.getSector()
            : "Norte";

    int totalVendidoPartido = entradaRepository.sumCantidadByPartidoAndEstados(
        partido.getId(), ESTADOS_ACTIVOS
    );
    int capacidadTotal = partido.getCapacidadDisponible() + totalVendidoPartido;

    Map<String, Map<String, Integer>> distribucion = calcularDistribucion(capacidadTotal);
    Map<String, Integer> cuposPorFila = distribucion.get(categoria);
    if (cuposPorFila == null) {
        throw new CupoNoDisponibleException("Zona inválida: " + categoria);
    }

    String fila = asignarFilaDisponible(partido.getId(), categoria, dto.getCantidad(), cuposPorFila);
    if (fila == null) {
        throw new CupoNoDisponibleException(
            "No hay cupos disponibles en la zona " + categoria +
            " para " + dto.getCantidad() + " entrada(s)."
        );
    }

    int ultimoAsiento = entradaRepository.maxAsientoFinByPartidoCategoriaYFila(
        partido.getId(), categoria, fila
    );
    int asientoInicio = ultimoAsiento + 1;

    Entrada entrada = new Entrada();
    entrada.setUsuario(u);
    entrada.setCantidad(dto.getCantidad());
    entrada.setEstado(ESTADO_RESERVADA);
    entrada.setPartido(partido);
    entrada.setCategoria(categoria);
    entrada.setSector(sector);
    entrada.setFila(fila);
    entrada.setAsientoInicio(asientoInicio);
    entrada.setPrecio(calcularPrecio(partido, categoria) * dto.getCantidad());
    entrada.setFechaCompra(LocalDateTime.now());
    entrada.setTtlReserva(LocalDateTime.now().plusMinutes(15));

    entradaRepository.save(entrada);
    partidoService.actualizarCapacidad(partido.getId(), -dto.getCantidad());

   auditoriaService.registrar(
    "ENTRADA_RESERVADA",
    u.getNombre() + " " + u.getApellido() + " (ID " + usuarioId + ") reservó "
        + dto.getCantidad() + " entrada(s) — "
        + partido.getSeleccionLocal() + " vs " + partido.getSeleccionVisitante()
        + " | Zona: " + categoria + " | Sector: " + sector + " | Fila: " + fila
        + " | Valor: $" + entrada.getPrecio(),
    usuarioId,
    String.valueOf(entrada.getId()),
    TIPO_ENTRADA
);

    String nombrePartido = partido.getSeleccionLocal() + " vs " + partido.getSeleccionVisitante();
    notificacionService.notificarReservaCreada(u, nombrePartido);

    return toDTO(entrada);
}
@Transactional
@Override
public EntradaResponseDTO confirmarPago(Long entradaId, String paymentRef) {
    Entrada entrada = entradaRepository.findById(entradaId)
            .orElseThrow(() -> new EntradaNotFoundException(ENTRADA_NO_ENCONTRADA));

    if (!ESTADO_RESERVADA.equals(entrada.getEstado())) {
        throw new EstadoInvalidoException("La entrada no está en estado RESERVADA");
    }

    if (entrada.getTtlReserva().isBefore(LocalDateTime.now())) {
        throw new EstadoInvalidoException("La reserva ha expirado");
    }

    try {
        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
            .setAmount((long)(entrada.getPrecio() * 100))
            .setCurrency("usd")
            .setConfirm(true)
            .setPaymentMethod(paymentRef)
            .setAutomaticPaymentMethods(
                PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                    .setEnabled(true)
                    .setAllowRedirects(PaymentIntentCreateParams.AutomaticPaymentMethods.AllowRedirects.NEVER)
                    .build()
            )
            .build();

        PaymentIntent intent = PaymentIntent.create(params);

        entrada.setEstado(ESTADO_PAGADA);
        entrada.setPaymentRef(intent.getId());
        entrada.setFechaPago(LocalDateTime.now());
        entrada.setTtlReserva(null);
        entradaRepository.save(entrada);

      auditoriaService.registrar(
    "ENTRADA_PAGADA",
    entrada.getUsuario().getNombre() + " " + entrada.getUsuario().getApellido()
        + " (ID " + entrada.getUsuario().getId() + ") confirmó pago de "
        + entrada.getCantidad() + " entrada(s) — "
        + entrada.getPartido().getSeleccionLocal() + " vs " + entrada.getPartido().getSeleccionVisitante()
        + " | Zona: " + entrada.getCategoria()
        + " | Valor total: $" + entrada.getPrecio()
        + " | Ref. pago: " + intent.getId(),
    entrada.getUsuario().getId(),
    String.valueOf(entradaId),
    TIPO_ENTRADA
);
        String nombrePartido = entrada.getPartido().getSeleccionLocal() + " vs " + entrada.getPartido().getSeleccionVisitante();
        notificacionService.notificarEntradaPagada(
            entrada.getUsuario(),
            nombrePartido,
            entrada.getCategoria(),
            entrada.getSector(),
            entrada.getFila()
        );

    } catch (StripeException e) {
        auditoriaService.registrar(
    "ENTRADA_PAGO_FALLIDO",
    "Pago fallido — " + entrada.getUsuario().getNombre() + " " + entrada.getUsuario().getApellido()
        + " (ID " + entrada.getUsuario().getId() + ") | "
        + entrada.getPartido().getSeleccionLocal() + " vs " + entrada.getPartido().getSeleccionVisitante()
        + " | Zona: " + entrada.getCategoria()
        + " | Valor: $" + entrada.getPrecio()
        + " | Error: " + e.getMessage(),
    entrada.getUsuario().getId(),
    String.valueOf(entradaId),
    TIPO_ENTRADA
);
        notificacionService.notificarEntradaPagoFallido(entrada.getUsuario());
        throw new PagoStripeException("Error al procesar el pago. Revisa los datos de tu tarjeta e intenta de nuevo.", e);
    }

    return toDTO(entrada);
}

@Transactional
@Override
public EntradaResponseDTO cancelarReserva(String correo, Long entradaId) {
    Usuario u = usuarioService.obtenerEntidadPorCorreo(correo);
    Long usuarioId = u.getId();

    Entrada entrada = entradaRepository.findById(entradaId)
            .orElseThrow(() -> new EntradaNotFoundException(ENTRADA_NO_ENCONTRADA));

    if (!entrada.getUsuario().getId().equals(usuarioId)) {
        throw new EstadoInvalidoException(ENTRADA_NO_PERTENECE);
    }

    if (!ESTADO_RESERVADA.equals(entrada.getEstado())) {
        throw new EstadoInvalidoException("Solo se pueden cancelar entradas en estado RESERVADA");
    }

    entrada.setEstado("CANCELADA");
    entradaRepository.save(entrada);

    partidoService.actualizarCapacidad(entrada.getPartido().getId(), entrada.getCantidad());

    auditoriaService.registrar(
    "ENTRADA_CANCELADA",
    u.getNombre() + " " + u.getApellido() + " (ID " + usuarioId + ") canceló "
        + entrada.getCantidad() + " entrada(s) — "
        + entrada.getPartido().getSeleccionLocal() + " vs " + entrada.getPartido().getSeleccionVisitante()
        + " | Zona: " + entrada.getCategoria() + " | Sector: " + entrada.getSector(),
    usuarioId,
    String.valueOf(entradaId),
    TIPO_ENTRADA
);

    return toDTO(entrada);
}
@Transactional
@Override
public EntradaResponseDTO transferirEntrada(Long entradaId, TransferenciaRequestDTO dto, String correo) {
    Usuario u = usuarioService.obtenerEntidadPorCorreo(correo);
    Long usuarioId = u.getId();

    Entrada entrada = entradaRepository.findById(entradaId)
            .orElseThrow(() -> new EntradaNotFoundException(ENTRADA_NO_ENCONTRADA));

    if (!entrada.getUsuario().getId().equals(usuarioId)) {
        throw new EstadoInvalidoException(ENTRADA_NO_PERTENECE);
    }

    if (!ESTADO_PAGADA.equals(entrada.getEstado())) {
        throw new EstadoInvalidoException("Solo se pueden transferir entradas pagadas");
    }

    LocalDateTime inicioDia = LocalDateTime.now().toLocalDate().atStartOfDay();
    LocalDateTime finDia = inicioDia.plusDays(1);

    List<Entrada> transferenciasHoy = entradaRepository.findByUsuarioIdAndFechaCompraBetween(usuarioId, inicioDia, finDia);
    int totalTransferidas = 0;
    for (Entrada entradaActual : transferenciasHoy) {
        if (ESTADO_TRANSFERIDA.equals(entradaActual.getEstado())) {
            totalTransferidas += entradaActual.getCantidad();
        }
    }

    if (totalTransferidas + entrada.getCantidad() > 12) {
        throw new LimiteSuperadoException("Límite diario de transferencias alcanzado");
    }

    Usuario usuarioRecibe = usuarioService.obtenerEntidadPorCorreo(dto.getCorreoDestino());
    entrada.setEstado(ESTADO_TRANSFERIDA);
    entradaRepository.save(entrada);

    Entrada nuevaEntrada = new Entrada();
    nuevaEntrada.setCantidad(entrada.getCantidad());
    nuevaEntrada.setUsuario(usuarioRecibe);
    nuevaEntrada.setPartido(entrada.getPartido());
    nuevaEntrada.setPrecio(entrada.getPrecio());
    nuevaEntrada.setEstado(ESTADO_PAGADA);
    nuevaEntrada.setFechaCompra(LocalDateTime.now());
    nuevaEntrada.setCategoria(entrada.getCategoria());
    nuevaEntrada.setSector(entrada.getSector());
    nuevaEntrada.setFila(entrada.getFila());
    nuevaEntrada.setAsientoInicio(entrada.getAsientoInicio());
    entradaRepository.save(nuevaEntrada);

    auditoriaService.registrar(
    "ENTRADA_TRANSFERIDA",
    u.getNombre() + " " + u.getApellido() + " (ID " + usuarioId + ") transfirió "
        + entrada.getCantidad() + " entrada(s) — "
        + entrada.getPartido().getSeleccionLocal() + " vs " + entrada.getPartido().getSeleccionVisitante()
        + " | Zona: " + entrada.getCategoria()
        + " | Destinatario: " + dto.getCorreoDestino(),
    usuarioId,
    String.valueOf(entradaId),
    TIPO_ENTRADA
);

    String nombrePartido = entrada.getPartido().getSeleccionLocal() + " vs " + entrada.getPartido().getSeleccionVisitante();
    notificacionService.notificarEntradaTransferida(u, dto.getCorreoDestino(), nombrePartido);
    notificacionService.notificarEntradaRecibida(usuarioRecibe, u.getCorreoUsuario(), nombrePartido);

    return toDTO(nuevaEntrada);
}
@Transactional
@Override
public EntradaResponseDTO reembolsarEntrada(String correo, Long entradaId) {
    Usuario u = usuarioService.obtenerEntidadPorCorreo(correo);
    Long usuarioId = u.getId();

    Entrada entrada = entradaRepository.findById(entradaId)
            .orElseThrow(() -> new EntradaNotFoundException(ENTRADA_NO_ENCONTRADA));

    if (!entrada.getUsuario().getId().equals(usuarioId)) {
        throw new EstadoInvalidoException(ENTRADA_NO_PERTENECE);
    }

    if (!ESTADO_PAGADA.equals(entrada.getEstado())) {
        throw new EstadoInvalidoException("Solo se pueden reembolsar entradas pagadas");
    }

    try {
        RefundCreateParams params = RefundCreateParams.builder()
            .setPaymentIntent(entrada.getPaymentRef())
            .build();

        Refund.create(params);

        entrada.setEstado("REEMBOLSADA");
        entrada.setFechaReembolso(LocalDateTime.now());
        entradaRepository.save(entrada);

        partidoService.actualizarCapacidad(entrada.getPartido().getId(), entrada.getCantidad());

       auditoriaService.registrar(
    "ENTRADA_REEMBOLSADA",
    u.getNombre() + " " + u.getApellido() + " (ID " + usuarioId + ") reembolsó "
        + entrada.getCantidad() + " entrada(s) — "
        + entrada.getPartido().getSeleccionLocal() + " vs " + entrada.getPartido().getSeleccionVisitante()
        + " | Zona: " + entrada.getCategoria()
        + " | Valor reembolsado: $" + entrada.getPrecio(),
    usuarioId,
    String.valueOf(entradaId),
    TIPO_ENTRADA
);
        notificacionService.notificarEntradaReembolsada(u, entradaId);

    } catch (StripeException e) {
      auditoriaService.registrar(
    "ENTRADA_REEMBOLSO_FALLIDO",
    "Reembolso fallido — " + u.getNombre() + " " + u.getApellido()
        + " (ID " + usuarioId + ") | "
        + entrada.getPartido().getSeleccionLocal() + " vs " + entrada.getPartido().getSeleccionVisitante()
        + " | Zona: " + entrada.getCategoria()
        + " | Valor: $" + entrada.getPrecio()
        + " | Error: " + e.getMessage(),
    usuarioId,
    String.valueOf(entradaId),
    TIPO_ENTRADA
);
        notificacionService.notificarEntradaReembolsoFallido(u, entradaId);
        throw new PagoStripeException("Error al procesar el reembolso. Intenta de nuevo más tarde.", e);
    }

    return toDTO(entrada);
}

@Transactional(readOnly = true)
@Override
public List<EntradaResponseDTO> listarEntradasUsuario(String correo) {
    Usuario u = usuarioService.obtenerEntidadPorCorreo(correo);
  return entradaRepository.findByUsuarioId(u.getId())
    .stream()
    .sorted((a, b) -> b.getFechaCompra().compareTo(a.getFechaCompra()))
    .map(this::toDTO)
    .toList();
}
    @Transactional(readOnly = true)
@Override
    public EntradaResponseDTO obtenerEntrada(Long entradaId) {
        Entrada entrada = entradaRepository.findById(entradaId).orElseThrow(() -> new EntradaNotFoundException(ENTRADA_NO_ENCONTRADA));
        return toDTO(entrada);
    }

   @Transactional
@Override
public void expirarReservasVencidas() {
    List<Entrada> vencidas = entradaRepository.findByEstadoAndTtlReservaLessThan(ESTADO_RESERVADA, LocalDateTime.now());

    for (Entrada entrada : vencidas) {
        entrada.setEstado("EXPIRADA");
        entradaRepository.save(entrada);

        partidoService.actualizarCapacidad(entrada.getPartido().getId(), entrada.getCantidad());

       auditoriaService.registrar(
    "ENTRADA_EXPIRADA",
    "Reserva expirada — " + entrada.getUsuario().getNombre() + " " + entrada.getUsuario().getApellido()
        + " (ID " + entrada.getUsuario().getId() + ") | "
        + entrada.getCantidad() + " entrada(s) — "
        + entrada.getPartido().getSeleccionLocal() + " vs " + entrada.getPartido().getSeleccionVisitante()
        + " | Zona: " + entrada.getCategoria(),
    entrada.getUsuario().getId(),
    String.valueOf(entrada.getId()),
    TIPO_ENTRADA
);
        String nombrePartido = entrada.getPartido().getSeleccionLocal() + " vs " + entrada.getPartido().getSeleccionVisitante();
        notificacionService.notificarReservaExpirada(entrada.getUsuario(), nombrePartido);
    }
}
@Transactional
@Override
public void avisarReservasPorExpirar() {
    LocalDateTime ahora = LocalDateTime.now();
    LocalDateTime en6Minutos = ahora.plusMinutes(6);
    LocalDateTime en4Minutos = ahora.plusMinutes(4);
    List<Entrada> porExpirar = entradaRepository.findByEstadoAndTtlReservaBetween(ESTADO_RESERVADA, en4Minutos, en6Minutos);

    for (Entrada entrada : porExpirar) {
        String nombrePartido = entrada.getPartido().getSeleccionLocal() + " vs " + entrada.getPartido().getSeleccionVisitante();
        notificacionService.notificarReservaPorExpirar(entrada.getUsuario(), nombrePartido);
    }
}

    private EntradaResponseDTO toDTO(Entrada entrada) {
    EntradaResponseDTO dto = new EntradaResponseDTO();
    dto.setId(entrada.getId());
    dto.setUsuarioId(entrada.getUsuario().getId());
    dto.setPartidoId(entrada.getPartido().getId());
dto.setSeleccionLocal(entrada.getPartido().getSeleccionLocal());
dto.setSeleccionVisitante(entrada.getPartido().getSeleccionVisitante());
dto.setFecha(entrada.getPartido().getFecha() != null ? entrada.getPartido().getFecha().toString() : null);
dto.setEstadio(entrada.getPartido().getEstadio());
dto.setRonda(entrada.getPartido().getRonda());
   dto.setEstado(entrada.getEstado());
    dto.setCantidad(entrada.getCantidad());
    dto.setPrecio(entrada.getPrecio());
    dto.setFechaCompra(entrada.getFechaCompra());
    dto.setTtlReserva(entrada.getTtlReserva());
    dto.setFechaPago(entrada.getFechaPago());
    dto.setFechaReembolso(entrada.getFechaReembolso());
    dto.setPaymentRef(entrada.getPaymentRef());
    dto.setCategoria(entrada.getCategoria());
    dto.setSector(entrada.getSector());
    dto.setFila(entrada.getFila());
    dto.setAsientoInicio(entrada.getAsientoInicio());
    return dto;
}
@Transactional(readOnly = true)
@Override
public List<PartidoCapacidadDTO> listarPartidosConCapacidad() {
    return partidoService.listarPartidosConCapacidad();
}
private Double calcularPrecio(Partido partido, String categoria) {
    Long precioBase = PRECIO_POR_RONDA.getOrDefault(partido.getRonda(), 50000L);
    boolean hayTop = SELECCIONES_TOP.contains(partido.getSeleccionLocal())
                  || SELECCIONES_TOP.contains(partido.getSeleccionVisitante());
    if (hayTop) {
        precioBase = (long)(precioBase * 1.5);
    }
    Double multiplicador = MULTIPLICADOR_CATEGORIA.getOrDefault(
        categoria != null ? categoria.toUpperCase(Locale.ROOT) : ZONA_BARRA, 1.0
    );
    return precioBase * multiplicador;
}
@Transactional(readOnly = true)
@Override
public List<CuposZonaDTO> obtenerCuposPorZona(Long partidoId) {
    Partido partido = partidoService.obtenerPartidoEntidadPorId(partidoId);

    int totalVendido = entradaRepository.sumCantidadByPartidoAndEstados(
        partidoId, ESTADOS_ACTIVOS
    );
    int capacidadDisponible = partido.getCapacidadDisponible() != null
            ? partido.getCapacidadDisponible()
            : 0;
    int capacidadTotal = capacidadDisponible + totalVendido;

    Map<String, Map<String, Integer>> distribucion = calcularDistribucion(capacidadTotal);
    List<CuposZonaDTO> resultado = new ArrayList<>();

    for (String zona : ZONAS) {
        Map<String, Integer> cuposPorFila = distribucion.get(zona);
        int limiteZona = cuposPorFila.values().stream().mapToInt(Integer::intValue).sum();
        int vendidosZona = entradaRepository.sumCantidadByPartidoAndCategoriaAndEstados(
            partidoId, zona, ESTADOS_ACTIVOS
        );

        List<CuposFilaDTO> filasDTO = new ArrayList<>();
        for (String nombreFila : FILAS) {
            int limiteFila = cuposPorFila.get(nombreFila);
            int vendidosFila = entradaRepository.sumCantidadByPartidoCategoriaYFila(
                partidoId, zona, nombreFila, ESTADOS_ACTIVOS
            );
            filasDTO.add(new CuposFilaDTO(nombreFila, limiteFila, vendidosFila));
        }

        resultado.add(new CuposZonaDTO(zona, limiteZona, vendidosZona, filasDTO));
    }

    return resultado;
}

    private Map<String, Map<String, Integer>> calcularDistribucion(int capacidadTotal) {
        Map<String, Map<String, Integer>> resultado = new LinkedHashMap<>();

        int asignadoZonas = 0;
        for (int i = 0; i < ZONAS.size(); i++) {
            String zona = ZONAS.get(i);
            int cupoZona;
            if (i < ZONAS.size() - 1) {
                cupoZona = (int)(capacidadTotal * PORCENTAJE_ZONA.get(zona));
                asignadoZonas += cupoZona;
            } else {
                cupoZona = capacidadTotal - asignadoZonas;
            }
            resultado.put(zona, dividirEnFilas(cupoZona));
        }

        int sumaZonas = resultado.values().stream()
                .mapToInt(m -> m.values().stream().mapToInt(Integer::intValue).sum())
                .sum();
        if (sumaZonas != capacidadTotal) {
            throw new IllegalStateException(
                "Distribución incoherente: suma de zonas=" + sumaZonas +
                " ≠ capacidad=" + capacidadTotal
            );
        }

        return resultado;
    }

    private Map<String, Integer> dividirEnFilas(int cupoZona) {
        Map<String, Integer> resultado = new LinkedHashMap<>();
        int cupoBase = cupoZona / FILAS.size();
        int asignadoFilas = 0;

        for (int i = 0; i < FILAS.size(); i++) {
            String fila = FILAS.get(i);
            int cupoFila;
            if (i < FILAS.size() - 1) {
                cupoFila = cupoBase;
                asignadoFilas += cupoFila;
            } else {
                cupoFila = cupoZona - asignadoFilas;
            }
            resultado.put(fila, cupoFila);
        }

        return resultado;
    }

    private String asignarFilaDisponible(Long partidoId, String categoria, int cantidad,
                                         Map<String, Integer> cuposPorFila) {
        for (String fila : FILAS) {
            int limiteFila = cuposPorFila.get(fila);
            int vendidosFila = entradaRepository.sumCantidadByPartidoCategoriaYFila(
                partidoId, categoria, fila, ESTADOS_ACTIVOS
            );
            if (vendidosFila + cantidad <= limiteFila) {
                return fila;
            }
        }
        return null;
    }
}