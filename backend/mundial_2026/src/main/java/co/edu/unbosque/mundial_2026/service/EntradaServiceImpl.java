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

/**
 * Implementación del servicio encargado de gestionar el ciclo de vida completo de las entradas
 * para los partidos del Mundial 2026.
 * Cubre reserva, confirmación de pago con Stripe, cancelación, transferencia entre usuarios,
 * reembolso y expiración automática de reservas vencidas.
 * El precio de cada entrada se calcula dinámicamente según la ronda del partido,
 * la zona seleccionada y si alguna de las selecciones es considerada top.
 * La capacidad del estadio se distribuye automáticamente en zonas y filas,
 * y cada operación queda registrada en auditoría con notificación al usuario
 */
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
    private static final String ENTRADA_NO_PERTENECE = "Esta entrada no pertenece al usuario";
    private static final String ZONA_BARRA = "BARRA";
    private static final String ZONA_GENERAL = "GENERAL";
    private static final String ZONA_PALCO = "PALCO";
    private static final String ZONA_ESQUINA = "ESQUINA";
    private static final String LOG_ID = " (ID ";
    private static final String LOG_ENTRADAS = " entrada(s) — ";
    private static final String LOG_ZONA = " | Zona: ";
    private static final String LOG_VALOR = " | Valor: $";

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

    /**
     * Reserva una o varias entradas para un partido. Valida que haya cupo disponible,
     * que la cantidad no supere el límite de 4 por transacción y el límite diario de 12 entradas.
     * Asigna automáticamente la zona, fila y asiento según disponibilidad.
     * El precio se calcula según la ronda, zona y si hay selecciones top en el partido.
     * La reserva tiene un tiempo de vida de 15 minutos para completar el pago.
     * Se notifica al usuario y se registra en auditoría
     *
     * @param correo correo del usuario que realiza la reserva
     * @param dto    datos de la reserva: id del partido, cantidad, zona y sector deseados
     * @return {@link EntradaResponseDTO} con la información de la entrada reservada
     * @throws CupoNoDisponibleException si no hay cupo en el partido o en la zona solicitada
     * @throws LimiteSuperadoException   si se supera el límite de 4 por transacción o 12 diarias
     */
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
            u.getNombre() + " " + u.getApellido() + LOG_ID + usuarioId + ") reservó "
            + dto.getCantidad() + LOG_ENTRADAS
            + partido.getSeleccionLocal() + " vs " + partido.getSeleccionVisitante()
            + LOG_ZONA + categoria + " | Sector: " + sector + " | Fila: " + fila
            + LOG_VALOR + entrada.getPrecio(),
            usuarioId,
            String.valueOf(entrada.getId()),
            TIPO_ENTRADA
        );

        String nombrePartido = partido.getSeleccionLocal() + " vs " + partido.getSeleccionVisitante();
        notificacionService.notificarReservaCreada(u, nombrePartido);

        return toDTO(entrada);
    }

    /**
     * Confirma el pago de una entrada reservada procesando el cobro a través de Stripe.
     * Valida que la entrada esté en estado RESERVADA y que la reserva no haya expirado.
     * Si el pago es exitoso, cambia el estado a PAGADA y guarda la referencia del pago.
     * Si Stripe falla, registra el error en auditoría, notifica al usuario y lanza excepción
     *
     * @param entradaId  id de la entrada a pagar
     * @param paymentRef referencia del método de pago de Stripe (paymentMethod ID)
     * @return {@link EntradaResponseDTO} con el estado actualizado a PAGADA
     * @throws EntradaNotFoundException si la entrada no existe
     * @throws EstadoInvalidoException  si la entrada no está en RESERVADA o la reserva expiró
     * @throws PagoStripeException      si Stripe rechaza o falla al procesar el pago
     */
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
                + LOG_ID + entrada.getUsuario().getId() + ") confirmó pago de "
                + entrada.getCantidad() + LOG_ENTRADAS
                + entrada.getPartido().getSeleccionLocal() + " vs " + entrada.getPartido().getSeleccionVisitante()
                + LOG_ZONA + entrada.getCategoria()
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
                + LOG_ID + entrada.getUsuario().getId() + ") | "
                + entrada.getPartido().getSeleccionLocal() + " vs " + entrada.getPartido().getSeleccionVisitante()
                + LOG_ZONA + entrada.getCategoria()
                + LOG_VALOR + entrada.getPrecio()
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

    /**
     * Cancela una reserva que aún no ha sido pagada. Verifica que la entrada
     * pertenezca al usuario y esté en estado RESERVADA. Al cancelar, devuelve
     * los cupos al partido para que estén disponibles nuevamente
     *
     * @param correo    correo del usuario que solicita la cancelación
     * @param entradaId id de la entrada a cancelar
     * @return {@link EntradaResponseDTO} con el estado actualizado a CANCELADA
     * @throws EntradaNotFoundException si la entrada no existe
     * @throws EstadoInvalidoException  si la entrada no pertenece al usuario o no está en RESERVADA
     */
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
            u.getNombre() + " " + u.getApellido() + LOG_ID + usuarioId + ") canceló "
            + entrada.getCantidad() + LOG_ENTRADAS
            + entrada.getPartido().getSeleccionLocal() + " vs " + entrada.getPartido().getSeleccionVisitante()
            + LOG_ZONA + entrada.getCategoria() + " | Sector: " + entrada.getSector(),
            usuarioId,
            String.valueOf(entradaId),
            TIPO_ENTRADA
        );

        return toDTO(entrada);
    }

    /**
     * Transfiere una entrada pagada a otro usuario identificado por su correo.
     * La entrada original queda marcada como TRANSFERIDA y se crea una nueva entrada
     * idéntica a nombre del destinatario con estado PAGADA y los mismos asientos.
     * Valida que el límite diario de transferencias no supere 12 entradas.
     * Ambos usuarios reciben notificación de la operación
     *
     * @param entradaId id de la entrada a transferir
     * @param dto       datos de la transferencia, incluyendo el correo del destinatario
     * @param correo    correo del usuario que transfiere
     * @return {@link EntradaResponseDTO} con la nueva entrada creada a nombre del destinatario
     * @throws EntradaNotFoundException si la entrada no existe
     * @throws EstadoInvalidoException  si la entrada no pertenece al usuario o no está en PAGADA
     * @throws LimiteSuperadoException  si se supera el límite diario de transferencias
     */
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
            u.getNombre() + " " + u.getApellido() + LOG_ID + usuarioId + ") transfirió "
            + entrada.getCantidad() + LOG_ENTRADAS
            + entrada.getPartido().getSeleccionLocal() + " vs " + entrada.getPartido().getSeleccionVisitante()
            + LOG_ZONA + entrada.getCategoria()
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

    /**
     * Procesa el reembolso de una entrada pagada a través de Stripe y devuelve los cupos al partido.
     * Si Stripe falla, registra el error en auditoría, notifica al usuario y lanza excepción.
     * Solo se pueden reembolsar entradas en estado PAGADA que pertenezcan al usuario
     *
     * @param correo    correo del usuario que solicita el reembolso
     * @param entradaId id de la entrada a reembolsar
     * @return {@link EntradaResponseDTO} con el estado actualizado a REEMBOLSADA
     * @throws EntradaNotFoundException si la entrada no existe
     * @throws EstadoInvalidoException  si la entrada no pertenece al usuario o no está en PAGADA
     * @throws PagoStripeException      si Stripe falla al procesar el reembolso
     */
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
                u.getNombre() + " " + u.getApellido() + LOG_ID + usuarioId + ") reembolsó "
                + entrada.getCantidad() + LOG_ENTRADAS
                + entrada.getPartido().getSeleccionLocal() + " vs " + entrada.getPartido().getSeleccionVisitante()
                + LOG_ZONA + entrada.getCategoria()
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
                + LOG_ID + usuarioId + ") | "
                + entrada.getPartido().getSeleccionLocal() + " vs " + entrada.getPartido().getSeleccionVisitante()
                + LOG_ZONA + entrada.getCategoria()
                + LOG_VALOR + entrada.getPrecio()
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

    /**
     * Retorna todas las entradas de un usuario ordenadas de la más reciente a la más antigua
     *
     * @param correo correo del usuario
     * @return lista de {@link EntradaResponseDTO} ordenada por fecha de compra descendente
     */
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

    /**
     * Busca y retorna una entrada específica por su id
     *
     * @param entradaId id de la entrada a consultar
     * @return {@link EntradaResponseDTO} con los datos de la entrada
     * @throws EntradaNotFoundException si la entrada no existe
     */
    @Transactional(readOnly = true)
    @Override
    public EntradaResponseDTO obtenerEntrada(Long entradaId) {
        Entrada entrada = entradaRepository.findById(entradaId).orElseThrow(() -> new EntradaNotFoundException(ENTRADA_NO_ENCONTRADA));
        return toDTO(entrada);
    }

    /**
     * Método ejecutado automáticamente (scheduled) que busca todas las reservas cuyo tiempo
     * de vida venció y las marca como EXPIRADAS, devolviendo los cupos al partido.
     * Notifica a cada usuario afectado
     */
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
                + LOG_ID + entrada.getUsuario().getId() + ") | "
                + entrada.getCantidad() + LOG_ENTRADAS
                + entrada.getPartido().getSeleccionLocal() + " vs " + entrada.getPartido().getSeleccionVisitante()
                + LOG_ZONA + entrada.getCategoria(),
                entrada.getUsuario().getId(),
                String.valueOf(entrada.getId()),
                TIPO_ENTRADA
            );
            String nombrePartido = entrada.getPartido().getSeleccionLocal() + " vs " + entrada.getPartido().getSeleccionVisitante();
            notificacionService.notificarReservaExpirada(entrada.getUsuario(), nombrePartido);
        }
    }

    /**
     * Método ejecutado automáticamente (scheduled) que detecta reservas que están
     * entre 4 y 6 minutos antes de vencer y envía una notificación de advertencia
     * al usuario para que complete el pago a tiempo
     */
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

    /**
     * Convierte una entidad {@link Entrada} a su representación DTO de respuesta,
     * incluyendo los datos del partido asociado
     *
     * @param entrada entidad a convertir
     * @return {@link EntradaResponseDTO} con todos los datos de la entrada
     */
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

    /**
     * Retorna la lista de partidos con su capacidad disponible actual
     *
     * @return lista de {@link PartidoCapacidadDTO} con cada partido y sus cupos restantes
     */
    @Transactional(readOnly = true)
    @Override
    public List<PartidoCapacidadDTO> listarPartidosConCapacidad() {
        return partidoService.listarPartidosConCapacidad();
    }

    /**
     * Calcula el precio de una entrada según la ronda del partido, la zona seleccionada
     * y si alguna de las selecciones es considerada top. Si hay selección top, el precio
     * base se incrementa un 50%. Luego se aplica el multiplicador de zona
     *
     * @param partido   partido para el que se calcula el precio
     * @param categoria zona seleccionada (BARRA, GENERAL, PALCO, ESQUINA)
     * @return precio final calculado para una entrada
     */
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

    /**
     * Retorna la disponibilidad de cupos por zona y fila para un partido específico.
     * Calcula la distribución teórica de la capacidad total del estadio y la compara
     * con las entradas ya vendidas para mostrar cuántos cupos quedan en cada fila
     *
     * @param partidoId id del partido a consultar
     * @return lista de {@link CuposZonaDTO} con el detalle de cupos por zona y fila
     */
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

    /**
     * Distribuye la capacidad total del estadio entre las zonas definidas según
     * los porcentajes configurados. La última zona absorbe los cupos restantes
     * para evitar pérdidas por redondeo. Lanza excepción si la suma no cuadra
     *
     * @param capacidadTotal capacidad total del estadio para el partido
     * @return mapa con cada zona y su distribución de cupos por fila
     * @throws IllegalStateException si la suma de cupos distribuidos no coincide con la capacidad total
     */
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

    /**
     * Divide los cupos de una zona entre las filas disponibles de forma equitativa.
     * La última fila absorbe los cupos sobrantes por redondeo
     *
     * @param cupoZona total de cupos disponibles para la zona
     * @return mapa con cada fila y su cantidad de cupos asignados
     */
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

    /**
     * Recorre las filas disponibles en una zona y retorna la primera fila
     * que tenga suficiente espacio para la cantidad de entradas solicitadas.
     * Retorna null si ninguna fila puede acomodar la cantidad pedida
     *
     * @param partidoId    id del partido
     * @param categoria    zona donde se busca disponibilidad
     * @param cantidad     número de entradas a ubicar juntas
     * @param cuposPorFila mapa con el límite de cupos por fila en esa zona
     * @return nombre de la fila disponible, o null si no hay ninguna con espacio suficiente
     */
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