package co.edu.unbosque.mundial_2026.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;

import co.edu.unbosque.mundial_2026.dto.request.AgregarItemDTO;
import co.edu.unbosque.mundial_2026.dto.request.ConfirmarOrdenDTO;
import co.edu.unbosque.mundial_2026.dto.response.ItemOrdenResponseDTO;
import co.edu.unbosque.mundial_2026.dto.response.OrdenHistorialDTO;
import co.edu.unbosque.mundial_2026.dto.response.OrdenResponseDTO;
import co.edu.unbosque.mundial_2026.entity.ItemOrden;
import co.edu.unbosque.mundial_2026.entity.MetodoPago;
import co.edu.unbosque.mundial_2026.entity.Orden;
import co.edu.unbosque.mundial_2026.entity.Producto;
import co.edu.unbosque.mundial_2026.entity.Usuario;
import co.edu.unbosque.mundial_2026.entity.VarianteProducto;
import co.edu.unbosque.mundial_2026.exception.CarritoVacioException;
import co.edu.unbosque.mundial_2026.exception.ItemNotFoundException;
import co.edu.unbosque.mundial_2026.exception.MetodoPagoInvalidoException;
import co.edu.unbosque.mundial_2026.exception.OrdenNotFoundException;
import co.edu.unbosque.mundial_2026.exception.PagoStripeException;
import co.edu.unbosque.mundial_2026.exception.ProductoNotFoundException;
import co.edu.unbosque.mundial_2026.exception.StockInsuficienteException;
import co.edu.unbosque.mundial_2026.repository.ItemOrdenRepository;
import co.edu.unbosque.mundial_2026.repository.OrdenRepository;
import co.edu.unbosque.mundial_2026.repository.VarianteProductoRepository;

/**
 * Implementación del servicio encargado de gestionar el carrito de compras y las órdenes
 * de la tienda del Mundial 2026.
 * Cubre el ciclo completo: agregar productos al carrito, consultar el estado actual,
 * eliminar ítems, confirmar el pago con Stripe, cancelar el carrito y consultar el historial.
 * Si un carrito lleva más de una hora inactivo sin ser pagado, se envía una notificación
 * automática al usuario recordándole que tiene productos pendientes.
 * Cada operación relevante queda registrada en auditoría
 */
@Service
public class OrdenServiceImpl implements OrdenService {

    private final OrdenRepository ordenRepository;
    private final ItemOrdenRepository itemOrdenRepository;
    private final VarianteProductoRepository varianteRepository;
    private final UsuarioService usuarioService;
    private final ProductoService productoService;
    private final MetodoPagoService metodoPagoService;
    private final EventoAuditoriaService auditoriaService;

    private static final String ESTADO_PENDIENTE = "PENDIENTE";
    private static final String ESTADO_PAGADA = "PAGADA";
    private static final String TIPO_ORDEN = "Orden";
    private static final String PREFIJO_ORDEN = "ORDEN-";
    private static final String CARRITO_NO_ACTIVO = "No tienes un carrito activo";

    private final NotificacionService notificacionService;

    public OrdenServiceImpl(OrdenRepository ordenRepository,
            ItemOrdenRepository itemOrdenRepository,
            VarianteProductoRepository varianteRepository,
            UsuarioService usuarioService,
            ProductoService productoService,
            MetodoPagoService metodoPagoService,
            EventoAuditoriaService auditoriaService,
            NotificacionService notificacionService,
            @Value("${stripe.api.key}") String stripeApiKey) {
        this.ordenRepository = ordenRepository;
        this.itemOrdenRepository = itemOrdenRepository;
        this.varianteRepository = varianteRepository;
        this.usuarioService = usuarioService;
        this.productoService = productoService;
        this.metodoPagoService = metodoPagoService;
        this.auditoriaService = auditoriaService;
        this.notificacionService = notificacionService;
        Stripe.apiKey = stripeApiKey;
    }

    /**
     * Agrega un producto al carrito del usuario. Si el usuario no tiene un carrito activo,
     * se crea uno nuevo en estado PENDIENTE. Si el producto ya está en el carrito con la
     * misma variante, se suma la cantidad. Valida que el producto esté activo,
     * que la variante pertenezca al producto y que haya stock suficiente.
     * La operación queda registrada en auditoría
     *
     * @param correo correo del usuario
     * @param dto    datos del ítem a agregar: id de producto, id de variante y cantidad
     * @return {@link OrdenResponseDTO} con el estado actualizado del carrito
     * @throws ProductoNotFoundException    si la variante o el producto no existen o el producto está inactivo
     * @throws StockInsuficienteException   si no hay stock suficiente para la cantidad solicitada
     */
    @Override
    @Transactional
    public OrdenResponseDTO agregarItem(String correo, AgregarItemDTO dto) {
        Usuario usuario = usuarioService.obtenerEntidadPorCorreo(correo);
        Long usuarioId = usuario.getId();

        VarianteProducto variante = varianteRepository.findByIdWithProductoYCategoria(dto.getVarianteId())
                .orElseThrow(() -> new ProductoNotFoundException("Variante no encontrada"));

        Producto producto = variante.getProducto();

        if (!producto.getId().equals(dto.getProductoId())) {
            throw new ProductoNotFoundException("La variante no pertenece al producto");
        }
        if (variante.getStock() < dto.getCantidad()) {
            throw new StockInsuficienteException("Stock insuficiente para esta variante");
        }
        if (Boolean.FALSE.equals(producto.getActivo())) {
            throw new ProductoNotFoundException("Este producto no está disponible");
        }

        Orden ordenActual = ordenRepository.findByUsuarioIdAndEstado(usuarioId, ESTADO_PENDIENTE)
                .orElseGet(() -> {
                    Orden nueva = new Orden();
                    Usuario refUsuario = new Usuario();
                    refUsuario.setId(usuarioId);
                    nueva.setUsuario(refUsuario);
                    nueva.setEstado(ESTADO_PENDIENTE);
                    nueva.setFechaCreacion(LocalDateTime.now());
                    nueva.setTotal(0.0);
                    return ordenRepository.save(nueva);
                });

        Optional<ItemOrden> item = itemOrdenRepository
                .findByOrdenIdAndProductoIdAndVarianteId(ordenActual.getId(), producto.getId(), variante.getId());

        ItemOrden itemOrdenActual;
        if (item.isPresent()) {
            itemOrdenActual = item.get();
            itemOrdenActual.setCantidad(itemOrdenActual.getCantidad() + dto.getCantidad());
        } else {
            itemOrdenActual = new ItemOrden();
            itemOrdenActual.setOrden(ordenActual);
            itemOrdenActual.setProducto(producto);
            itemOrdenActual.setVariante(variante);
            itemOrdenActual.setCantidad(dto.getCantidad());
            itemOrdenActual.setPrecioUnitario(producto.getPrecio());
        }
        itemOrdenRepository.save(itemOrdenActual);

        double delta = dto.getCantidad() * itemOrdenActual.getPrecioUnitario();
        ordenActual.setTotal(ordenActual.getTotal() + delta);

        String descripcionAuditoria = usuario.getNombre() + " " + usuario.getApellido()
                + " (ID " + usuarioId + ") agregó "
                + dto.getCantidad() + " x " + producto.getNombre()
                + (variante.getEspecificacion() != null ? " (" + variante.getEspecificacion() + ")" : "")
                + " | Subtotal: $" + (dto.getCantidad() * producto.getPrecio());
        auditoriaService.registrar(
                "ITEM_AGREGADO_CARRITO",
                descripcionAuditoria,
                usuarioId,
                PREFIJO_ORDEN + ordenActual.getId(),
                TIPO_ORDEN);

        List<ItemOrden> itemsRespuesta = itemOrdenRepository.findByOrdenIdConDetalles(ordenActual.getId());
        return toOrdenDTO(ordenActual, itemsRespuesta);
    }

    /**
     * Retorna el carrito activo (estado PENDIENTE) del usuario con todos sus ítems
     *
     * @param correo correo del usuario
     * @return {@link OrdenResponseDTO} con el contenido actual del carrito
     * @throws OrdenNotFoundException si el usuario no tiene un carrito activo
     */
    @Override
    @Transactional(readOnly = true)
    public OrdenResponseDTO obtenerCarrito(String correo) {
        Usuario usuario = usuarioService.obtenerEntidadPorCorreo(correo);
        Orden orden = ordenRepository.findByUsuarioIdAndEstado(usuario.getId(), ESTADO_PENDIENTE)
                .orElseThrow(() -> new OrdenNotFoundException(CARRITO_NO_ACTIVO));
        List<ItemOrden> items = itemOrdenRepository.findByOrdenIdConDetalles(orden.getId());
        return toOrdenDTO(orden, items);
    }

    /**
     * Elimina un ítem del carrito activo del usuario y ajusta el total de la orden.
     * Si al eliminar el ítem el carrito queda vacío, la orden también se elimina
     *
     * @param correo correo del usuario
     * @param itemId id del ítem a eliminar
     * @return {@link OrdenResponseDTO} con el estado del carrito tras la eliminación
     * @throws OrdenNotFoundException si el usuario no tiene un carrito activo
     * @throws ItemNotFoundException  si el ítem no existe
     */
    @Override
    @Transactional
    public OrdenResponseDTO eliminarItem(String correo, Long itemId) {
        Usuario usuario = usuarioService.obtenerEntidadPorCorreo(correo);
        Orden ordenActual = ordenRepository.findByUsuarioIdAndEstado(usuario.getId(), ESTADO_PENDIENTE)
                .orElseThrow(() -> new OrdenNotFoundException("Usuario no tiene orden activa"));
        ItemOrden itemAEliminar = itemOrdenRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException("No existe ese item"));
        itemOrdenRepository.delete(itemAEliminar);
        ordenActual.setTotal(ordenActual.getTotal() - (itemAEliminar.getCantidad() * itemAEliminar.getPrecioUnitario()));
        List<ItemOrden> itemsRestantes = itemOrdenRepository.findByOrdenIdConDetalles(ordenActual.getId());
        if (itemsRestantes.isEmpty()) {
            ordenRepository.delete(ordenActual);
            return toOrdenDTO(ordenActual, itemsRestantes);
        }
        ordenRepository.save(ordenActual);
        return toOrdenDTO(ordenActual, itemsRestantes);
    }

    /**
     * Confirma y paga el carrito activo del usuario procesando el cobro a través de Stripe.
     * Valida que el carrito no esté vacío, que el método de pago pertenezca al usuario
     * y que haya stock suficiente para todos los ítems. Si el pago es exitoso, descuenta
     * el stock de cada variante, marca la orden como PAGADA y notifica al usuario.
     * Si Stripe falla, notifica al usuario del error y lanza excepción
     *
     * @param correo correo del usuario
     * @param dto    datos de confirmación: id del método de pago a usar
     * @return {@link OrdenResponseDTO} con la orden en estado PAGADA
     * @throws OrdenNotFoundException       si no hay carrito activo
     * @throws CarritoVacioException        si el carrito no tiene ítems
     * @throws MetodoPagoInvalidoException  si el método de pago no pertenece al usuario
     * @throws StockInsuficienteException   si algún producto se quedó sin stock antes del pago
     * @throws PagoStripeException          si Stripe rechaza o falla al procesar el pago
     */
    @Override
    @Transactional
    public OrdenResponseDTO confirmarOrden(String correo, ConfirmarOrdenDTO dto) {
        Usuario usuario = usuarioService.obtenerEntidadPorCorreo(correo);
        Long usuarioId = usuario.getId();
        Orden ordenAPagar = ordenRepository.findByUsuarioIdAndEstado(usuarioId, ESTADO_PENDIENTE)
                .orElseThrow(() -> new OrdenNotFoundException(CARRITO_NO_ACTIVO));
        List<ItemOrden> items = itemOrdenRepository.findByOrdenId(ordenAPagar.getId());
        if (items.isEmpty()) {
            throw new CarritoVacioException("El carrito está vacío");
        }
        MetodoPago metodoPago = metodoPagoService.obtenerEntidadPorId(dto.getMetodoPagoId());
        if (!metodoPago.getUsuario().getId().equals(usuarioId)) {
            throw new MetodoPagoInvalidoException("El método de pago no pertenece al usuario");
        }
        for (ItemOrden item : items) {
            if (item.getVariante().getStock() < item.getCantidad()) {
                throw new StockInsuficienteException("Stock insuficiente para " + item.getProducto().getNombre()
                        + (item.getVariante().getEspecificacion() != null
                                ? " (" + item.getVariante().getEspecificacion() + ")"
                                : ""));
            }
        }
        try {
           long totalCentavos = Math.round(ordenAPagar.getTotal() * 100);
            PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                    .setAmount(totalCentavos)
                    .setCurrency("usd")
                    .setPaymentMethod(metodoPago.getDetails())
                    .setConfirm(true)
                    .setAutomaticPaymentMethods(
                            PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                                    .setEnabled(true)
                                    .setAllowRedirects(
                                            PaymentIntentCreateParams.AutomaticPaymentMethods.AllowRedirects.NEVER)
                                    .build())
                    .build();
            PaymentIntent paymentIntent = PaymentIntent.create(params);
            ordenAPagar.setEstado(ESTADO_PAGADA);
            ordenAPagar.setFechaPago(LocalDateTime.now());
            ordenAPagar.setPaymentRef(paymentIntent.getId());
            ordenAPagar.setMetodoPago(metodoPago);
            for (ItemOrden item : items) {
                productoService.actualizarStock(item.getVariante().getId(), item.getCantidad());
            }
            ordenRepository.save(ordenAPagar);
            auditoriaService.registrar(
                    "ORDEN_PAGADA",
                    usuario.getNombre() + " " + usuario.getApellido()
                            + " pagó orden por $" + ordenAPagar.getTotal()
                            + " con " + metodoPago.getLabel(),
                    usuarioId,
                    PREFIJO_ORDEN + ordenAPagar.getId(),
                    TIPO_ORDEN);
            notificacionService.notificarOrdenConfirmada(usuario, ordenAPagar.getTotal());
        } catch (StripeException e) {
            notificacionService.notificarOrdenFallida(usuario);
            throw new PagoStripeException(
                    "Error al procesar el pago. Revisa los datos de tu tarjeta e intenta de nuevo.", e);
        }
        return toOrdenDTO(ordenAPagar, items);
    }

    /**
     * Retorna el historial completo de órdenes del usuario excluyendo las que están en estado PENDIENTE
     *
     * @param correo correo del usuario
     * @return lista de {@link OrdenResponseDTO} con todas las órdenes procesadas del usuario
     */
    @Override
    @Transactional(readOnly = true)
    public List<OrdenResponseDTO> historial(String correo) {
        Usuario usuario = usuarioService.obtenerEntidadPorCorreo(correo);
        List<Orden> ordenes = ordenRepository.findByUsuarioIdAndEstadoNot(usuario.getId(), ESTADO_PENDIENTE);
        List<OrdenResponseDTO> responseDTOs = new ArrayList<>();
       for (Orden orden : ordenes) {
    responseDTOs.add(toOrdenDTO(orden, orden.getItems()));
}
        return responseDTOs;
    }

    /**
     * Cancela el carrito activo del usuario eliminando todos sus ítems y la orden.
     * La operación queda registrada en auditoría
     *
     * @param correo correo del usuario
     * @return {@link OrdenResponseDTO} con los datos de la orden cancelada
     * @throws OrdenNotFoundException si el usuario no tiene un carrito activo
     */
    @Override
    @Transactional
    public OrdenResponseDTO cancelarOrden(String correo) {
        Usuario usuario = usuarioService.obtenerEntidadPorCorreo(correo);
        Orden orden = ordenRepository.findByUsuarioIdAndEstado(usuario.getId(), ESTADO_PENDIENTE)
                .orElseThrow(() -> new OrdenNotFoundException(CARRITO_NO_ACTIVO));
        List<ItemOrden> items = itemOrdenRepository.findByOrdenIdConDetalles(orden.getId());
        itemOrdenRepository.deleteAll(items);
        ordenRepository.delete(orden);
        auditoriaService.registrar(
                "ORDEN_CANCELADA",
                usuario.getNombre() + " " + usuario.getApellido() + " vació el carrito",
                usuario.getId(),
                PREFIJO_ORDEN + orden.getId(),
                TIPO_ORDEN);
        return toOrdenDTO(orden, items);
    }

    /**
     * Convierte una entidad {@link ItemOrden} a su representación DTO de respuesta,
     * incluyendo el subtotal calculado y el nombre de la categoría del producto
     *
     * @param item entidad del ítem a convertir
     * @return {@link ItemOrdenResponseDTO} con los datos del ítem
     */
    private ItemOrdenResponseDTO toItemDTO(ItemOrden item) {
        ItemOrdenResponseDTO response = new ItemOrdenResponseDTO();
        response.setId(item.getId());
        response.setProductoId(item.getProducto().getId());
        response.setVarianteId(item.getVariante().getId());
        response.setProductoNombre(item.getProducto().getNombre());
        response.setProductoImagenUrl(item.getProducto().getImagenUrl());
        response.setEspecificacion(item.getVariante().getEspecificacion());
        response.setCantidad(item.getCantidad());
        response.setPrecioUnitario(item.getPrecioUnitario());
        response.setSubtotal(item.getCantidad() * item.getPrecioUnitario());
        response.setCategoriaNombre(item.getProducto().getCategoria().getNombre());
        return response;
    }

    /**
     * Convierte una entidad {@link Orden} y su lista de ítems a su representación DTO de respuesta
     *
     * @param orden orden a convertir
     * @param items lista de ítems asociados a la orden
     * @return {@link OrdenResponseDTO} con los datos completos de la orden
     */
    private OrdenResponseDTO toOrdenDTO(Orden orden, List<ItemOrden> items) {
        OrdenResponseDTO response = new OrdenResponseDTO();
        response.setId(orden.getId());
        response.setEstado(orden.getEstado());
        response.setTotal(orden.getTotal());
        response.setFechaCreacion(orden.getFechaCreacion());
        response.setFechaPago(orden.getFechaPago());
        response.setPaymentRef(orden.getPaymentRef());
        if (orden.getMetodoPago() != null) {
            response.setMetodoPagoLabel(orden.getMetodoPago().getLabel());
        }
        List<ItemOrdenResponseDTO> itemDTOs = new ArrayList<>();
        for (ItemOrden item : items) {
            itemDTOs.add(toItemDTO(item));
        }
        response.setItems(itemDTOs);
        return response;
    }

    /**
     * Retorna el historial liviano de órdenes pagadas o reembolsadas del usuario,
     * con un formato resumido que incluye los ítems pero sin datos de auditoría ni estado completo
     *
     * @param correo correo del usuario
     * @return lista de {@link OrdenHistorialDTO} con las órdenes pagadas y reembolsadas
     */
    @Override
    @Transactional(readOnly = true)
    public List<OrdenHistorialDTO> historialLiviano(String correo) {
        Usuario usuario = usuarioService.obtenerEntidadPorCorreo(correo);
        List<Orden> ordenes = ordenRepository.findHistorialByUsuarioIdAndEstadoIn(
                usuario.getId(),
                List.of(ESTADO_PAGADA, "REEMBOLSADA"));
        List<OrdenHistorialDTO> resultado = new ArrayList<>();
        for (Orden o : ordenes) {
            OrdenHistorialDTO dto = new OrdenHistorialDTO();
            dto.setId(o.getId());
            dto.setEstado(o.getEstado());
            dto.setTotal(o.getTotal());
            dto.setFechaCreacion(o.getFechaCreacion());
            dto.setFechaPago(o.getFechaPago());
            dto.setPaymentRef(o.getPaymentRef());
            dto.setMetodoPagoLabel(o.getMetodoPago() != null ? o.getMetodoPago().getLabel() : null);
            List<OrdenHistorialDTO.ItemHistorialDTO> items = new ArrayList<>();
            for (ItemOrden item : o.getItems()) {
                OrdenHistorialDTO.ItemHistorialDTO itemDto = new OrdenHistorialDTO.ItemHistorialDTO();
                itemDto.setProductoNombre(item.getProducto().getNombre());
                itemDto.setCategoriaNombre(item.getProducto().getCategoria().getNombre());
                itemDto.setCantidad(item.getCantidad());
                itemDto.setPrecioUnitario(item.getPrecioUnitario());
                itemDto.setSubtotal(item.getCantidad() * item.getPrecioUnitario());
                items.add(itemDto);
            }
            dto.setItems(items);
            resultado.add(dto);
        }
        return resultado;
    }

    /**
     * Método ejecutado automáticamente cada 5 minutos (scheduled) que detecta carritos
     * en estado PENDIENTE con más de una hora de inactividad que aún no han sido notificados.
     * Por cada carrito abandonado con ítems, envía una notificación al usuario
     * y marca la orden para no volver a notificarla
     */
    @Transactional
    @org.springframework.scheduling.annotation.Scheduled(fixedRate = 300000)
    public void notificarCarritosAbandonados() {
        LocalDateTime haceUnaHora = LocalDateTime.now().minusHours(1);
        List<Orden> abandonadas = ordenRepository
                .findByEstadoAndFechaCreacionBeforeAndNotificadoAbandonadoFalse(ESTADO_PENDIENTE, haceUnaHora);
        for (Orden orden : abandonadas) {
            List<ItemOrden> items = itemOrdenRepository.findByOrdenId(orden.getId());
            if (!items.isEmpty()) {
                notificacionService.notificarCarritoAbandonado(orden.getUsuario());
                orden.setNotificadoAbandonado(true);
                ordenRepository.save(orden);
            }
        }
    }
}