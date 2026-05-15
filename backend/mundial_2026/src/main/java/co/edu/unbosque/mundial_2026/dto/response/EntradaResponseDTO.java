package co.edu.unbosque.mundial_2026.dto.response;

import java.time.LocalDateTime;

public class EntradaResponseDTO {
    private Long id;
    private Long usuarioId;
    private Long partidoId;
    private String estado;
    private Integer cantidad;
    private Double precio;
    private LocalDateTime fechaCompra;
    private LocalDateTime ttlReserva;
    private LocalDateTime fechaPago;
    private LocalDateTime fechaReembolso;
    private String paymentRef;
    private String seleccionLocal;
    private String seleccionVisitante;
    private String fecha;
    private String estadio;
    private String ronda;
    private String categoria;
    private String sector;
    private String fila;
    private Integer asientoInicio;

    public EntradaResponseDTO() {
        // Constructor vacio
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public Long getPartidoId() {
        return partidoId;
    }

    public void setPartidoId(Long partidoId) {
        this.partidoId = partidoId;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public Double getPrecio() {
        return precio;
    }

    public void setPrecio(Double precio) {
        this.precio = precio;
    }

    public LocalDateTime getFechaCompra() {
        return fechaCompra;
    }

    public void setFechaCompra(LocalDateTime fechaCompra) {
        this.fechaCompra = fechaCompra;
    }

    public LocalDateTime getTtlReserva() {
        return ttlReserva;
    }

    public void setTtlReserva(LocalDateTime ttlReserva) {
        this.ttlReserva = ttlReserva;
    }

    public LocalDateTime getFechaPago() {
        return fechaPago;
    }

    public void setFechaPago(LocalDateTime fechaPago) {
        this.fechaPago = fechaPago;
    }

    public LocalDateTime getFechaReembolso() {
        return fechaReembolso;
    }

    public void setFechaReembolso(LocalDateTime fechaReembolso) {
        this.fechaReembolso = fechaReembolso;
    }

    public String getPaymentRef() {
        return paymentRef;
    }

    public void setPaymentRef(String paymentRef) {
        this.paymentRef = paymentRef;
    }

    public String getSeleccionLocal() {
        return seleccionLocal;
    }

    public void setSeleccionLocal(String seleccionLocal) {
        this.seleccionLocal = seleccionLocal;
    }

    public String getSeleccionVisitante() {
        return seleccionVisitante;
    }

    public void setSeleccionVisitante(String seleccionVisitante) {
        this.seleccionVisitante = seleccionVisitante;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getEstadio() {
        return estadio;
    }

    public void setEstadio(String estadio) {
        this.estadio = estadio;
    }

    public String getRonda() {
        return ronda;
    }

    public void setRonda(String ronda) {
        this.ronda = ronda;
    }
    public String getCategoria() {
    return categoria;
}

public void setCategoria(String categoria) {
    this.categoria = categoria;
}
public String getSector() {
        return sector;
    }

    public void setSector(String sector) {
        this.sector = sector;
    }

    public String getFila() {
        return fila;
    }

    public void setFila(String fila) {
        this.fila = fila;
    }

    public Integer getAsientoInicio() {
        return asientoInicio;
    }

    public void setAsientoInicio(Integer asientoInicio) {
        this.asientoInicio = asientoInicio;
    }
}