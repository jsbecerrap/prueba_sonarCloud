package co.edu.unbosque.mundial_2026.dto.response;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO de respuesta para las ordenes
 * Contiene informacion general de la orden y sus items
 */
public class OrdenResponseDTO {

    /**
     * Identificador de la orden
     */
    private Long id;

    /**
     * Estado de la orden
     */
    private String estado;

    /**
     * Total de la orden
     */
    private Double total;

    /**
     * Fecha de creacion de la orden
     */
    private LocalDateTime fechaCreacion;

    /**
     * Fecha de pago de la orden
     */
    private LocalDateTime fechaPago;

    /**
     * Referencia del pago
     */
    private String paymentRef;

    /**
     * Nombre del metodo de pago
     */
    private String metodoPagoLabel;

    /**
     * Lista de items de la orden
     */
    private List<ItemOrdenResponseDTO> items;

    /**
     * Obtiene el identificador de la orden
     *
     * @return identificador de la orden
     */
    public Long getId() {
        return id;
    }

    /**
     * Establece el identificador de la orden
     *
     * @param id identificador de la orden
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Obtiene el estado de la orden
     *
     * @return estado de la orden
     */
    public String getEstado() {
        return estado;
    }

    /**
     * Establece el estado de la orden
     *
     * @param estado estado de la orden
     */
    public void setEstado(String estado) {
        this.estado = estado;
    }

    /**
     * Obtiene el total de la orden
     *
     * @return total de la orden
     */
    public Double getTotal() {
        return total;
    }

    /**
     * Establece el total de la orden
     *
     * @param total total de la orden
     */
    public void setTotal(Double total) {
        this.total = total;
    }

    /**
     * Obtiene la fecha de creacion
     *
     * @return fecha de creacion
     */
    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    /**
     * Establece la fecha de creacion
     *
     * @param fechaCreacion fecha de creacion
     */
    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    /**
     * Obtiene la fecha de pago
     *
     * @return fecha de pago
     */
    public LocalDateTime getFechaPago() {
        return fechaPago;
    }

    /**
     * Establece la fecha de pago
     *
     * @param fechaPago fecha de pago
     */
    public void setFechaPago(LocalDateTime fechaPago) {
        this.fechaPago = fechaPago;
    }

    /**
     * Obtiene la referencia del pago
     *
     * @return referencia del pago
     */
    public String getPaymentRef() {
        return paymentRef;
    }

    /**
     * Establece la referencia del pago
     *
     * @param paymentRef referencia del pago
     */
    public void setPaymentRef(String paymentRef) {
        this.paymentRef = paymentRef;
    }

    /**
     * Obtiene el nombre del metodo de pago
     *
     * @return nombre del metodo de pago
     */
    public String getMetodoPagoLabel() {
        return metodoPagoLabel;
    }

    /**
     * Establece el nombre del metodo de pago
     *
     * @param metodoPagoLabel nombre del metodo de pago
     */
    public void setMetodoPagoLabel(String metodoPagoLabel) {
        this.metodoPagoLabel = metodoPagoLabel;
    }

    /**
     * Obtiene la lista de items
     *
     * @return lista de items
     */
    public List<ItemOrdenResponseDTO> getItems() {
        return items;
    }

    /**
     * Establece la lista de items
     *
     * @param items lista de items
     */
    public void setItems(List<ItemOrdenResponseDTO> items) {
        this.items = items;
    }
}