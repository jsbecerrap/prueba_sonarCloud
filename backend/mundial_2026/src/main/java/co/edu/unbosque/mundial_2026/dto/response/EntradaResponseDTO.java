package co.edu.unbosque.mundial_2026.dto.response;

import java.time.LocalDateTime;

/**
 * DTO de respuesta para las entradas del sistema
 * Contiene informacion de la compra, el partido y la ubicacion de la entrada
 */
public class EntradaResponseDTO {

    /**
     * Identificador de la entrada
     */
    private Long id;

    /**
     * Identificador del usuario
     */
    private Long usuarioId;

    /**
     * Identificador del partido
     */
    private Long partidoId;

    /**
     * Estado de la entrada
     */
    private String estado;

    /**
     * Cantidad de entradas compradas
     */
    private Integer cantidad;

    /**
     * Precio total de la compra
     */
    private Double precio;

    /**
     * Fecha de compra de la entrada
     */
    private LocalDateTime fechaCompra;

    /**
     * Tiempo limite de la reserva
     */
    private LocalDateTime ttlReserva;

    /**
     * Fecha de pago de la entrada
     */
    private LocalDateTime fechaPago;

    /**
     * Fecha del reembolso
     */
    private LocalDateTime fechaReembolso;

    /**
     * Referencia del pago
     */
    private String paymentRef;

    /**
     * Nombre de la seleccion local
     */
    private String seleccionLocal;

    /**
     * Nombre de la seleccion visitante
     */
    private String seleccionVisitante;

    /**
     * Fecha del partido
     */
    private String fecha;

    /**
     * Nombre del estadio
     */
    private String estadio;

    /**
     * Ronda del partido
     */
    private String ronda;

    /**
     * Categoria de la entrada
     */
    private String categoria;

    /**
     * Sector de la entrada
     */
    private String sector;

    /**
     * Fila de la entrada
     */
    private String fila;

    /**
     * Numero inicial del asiento
     */
    private Integer asientoInicio;

    /**
     * Constructor vacio de la clase
     */
    public EntradaResponseDTO() {
        // Constructor vacio
    }

    /**
     * Obtiene el identificador de la entrada
     *
     * @return identificador de la entrada
     */
    public Long getId() {
        return id;
    }

    /**
     * Establece el identificador de la entrada
     *
     * @param id identificador de la entrada
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Obtiene el identificador del usuario
     *
     * @return identificador del usuario
     */
    public Long getUsuarioId() {
        return usuarioId;
    }

    /**
     * Establece el identificador del usuario
     *
     * @param usuarioId identificador del usuario
     */
    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    /**
     * Obtiene el identificador del partido
     *
     * @return identificador del partido
     */
    public Long getPartidoId() {
        return partidoId;
    }

    /**
     * Establece el identificador del partido
     *
     * @param partidoId identificador del partido
     */
    public void setPartidoId(Long partidoId) {
        this.partidoId = partidoId;
    }

    /**
     * Obtiene el estado de la entrada
     *
     * @return estado de la entrada
     */
    public String getEstado() {
        return estado;
    }

    /**
     * Establece el estado de la entrada
     *
     * @param estado estado de la entrada
     */
    public void setEstado(String estado) {
        this.estado = estado;
    }

    /**
     * Obtiene la cantidad de entradas compradas
     *
     * @return cantidad de entradas compradas
     */
    public Integer getCantidad() {
        return cantidad;
    }

    /**
     * Establece la cantidad de entradas compradas
     *
     * @param cantidad cantidad de entradas compradas
     */
    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    /**
     * Obtiene el precio total de la compra
     *
     * @return precio total de la compra
     */
    public Double getPrecio() {
        return precio;
    }

    /**
     * Establece el precio total de la compra
     *
     * @param precio precio total de la compra
     */
    public void setPrecio(Double precio) {
        this.precio = precio;
    }

    /**
     * Obtiene la fecha de compra
     *
     * @return fecha de compra
     */
    public LocalDateTime getFechaCompra() {
        return fechaCompra;
    }

    /**
     * Establece la fecha de compra
     *
     * @param fechaCompra fecha de compra
     */
    public void setFechaCompra(LocalDateTime fechaCompra) {
        this.fechaCompra = fechaCompra;
    }

    /**
     * Obtiene el tiempo limite de la reserva
     *
     * @return tiempo limite de la reserva
     */
    public LocalDateTime getTtlReserva() {
        return ttlReserva;
    }

    /**
     * Establece el tiempo limite de la reserva
     *
     * @param ttlReserva tiempo limite de la reserva
     */
    public void setTtlReserva(LocalDateTime ttlReserva) {
        this.ttlReserva = ttlReserva;
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
     * Obtiene la fecha del reembolso
     *
     * @return fecha del reembolso
     */
    public LocalDateTime getFechaReembolso() {
        return fechaReembolso;
    }

    /**
     * Establece la fecha del reembolso
     *
     * @param fechaReembolso fecha del reembolso
     */
    public void setFechaReembolso(LocalDateTime fechaReembolso) {
        this.fechaReembolso = fechaReembolso;
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
     * Obtiene el nombre de la seleccion local
     *
     * @return nombre de la seleccion local
     */
    public String getSeleccionLocal() {
        return seleccionLocal;
    }

    /**
     * Establece el nombre de la seleccion local
     *
     * @param seleccionLocal nombre de la seleccion local
     */
    public void setSeleccionLocal(String seleccionLocal) {
        this.seleccionLocal = seleccionLocal;
    }

    /**
     * Obtiene el nombre de la seleccion visitante
     *
     * @return nombre de la seleccion visitante
     */
    public String getSeleccionVisitante() {
        return seleccionVisitante;
    }

    /**
     * Establece el nombre de la seleccion visitante
     *
     * @param seleccionVisitante nombre de la seleccion visitante
     */
    public void setSeleccionVisitante(String seleccionVisitante) {
        this.seleccionVisitante = seleccionVisitante;
    }

    /**
     * Obtiene la fecha del partido
     *
     * @return fecha del partido
     */
    public String getFecha() {
        return fecha;
    }

    /**
     * Establece la fecha del partido
     *
     * @param fecha fecha del partido
     */
    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    /**
     * Obtiene el nombre del estadio
     *
     * @return nombre del estadio
     */
    public String getEstadio() {
        return estadio;
    }

    /**
     * Establece el nombre del estadio
     *
     * @param estadio nombre del estadio
     */
    public void setEstadio(String estadio) {
        this.estadio = estadio;
    }

    /**
     * Obtiene la ronda del partido
     *
     * @return ronda del partido
     */
    public String getRonda() {
        return ronda;
    }

    /**
     * Establece la ronda del partido
     *
     * @param ronda ronda del partido
     */
    public void setRonda(String ronda) {
        this.ronda = ronda;
    }

    /**
     * Obtiene la categoria de la entrada
     *
     * @return categoria de la entrada
     */
    public String getCategoria() {
        return categoria;
    }

    /**
     * Establece la categoria de la entrada
     *
     * @param categoria categoria de la entrada
     */
    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    /**
     * Obtiene el sector de la entrada
     *
     * @return sector de la entrada
     */
    public String getSector() {
        return sector;
    }

    /**
     * Establece el sector de la entrada
     *
     * @param sector sector de la entrada
     */
    public void setSector(String sector) {
        this.sector = sector;
    }

    /**
     * Obtiene la fila de la entrada
     *
     * @return fila de la entrada
     */
    public String getFila() {
        return fila;
    }

    /**
     * Establece la fila de la entrada
     *
     * @param fila fila de la entrada
     */
    public void setFila(String fila) {
        this.fila = fila;
    }

    /**
     * Obtiene el numero inicial del asiento
     *
     * @return numero inicial del asiento
     */
    public Integer getAsientoInicio() {
        return asientoInicio;
    }

    /**
     * Establece el numero inicial del asiento
     *
     * @param asientoInicio numero inicial del asiento
     */
    public void setAsientoInicio(Integer asientoInicio) {
        this.asientoInicio = asientoInicio;
    }
}