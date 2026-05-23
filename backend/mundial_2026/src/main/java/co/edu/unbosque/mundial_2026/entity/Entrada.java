package co.edu.unbosque.mundial_2026.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * Representa una entrada
 */
@Entity
@Table(name = "entradas")
public class Entrada {

    /**
     * Identificador de la entrada
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Usuario asociado a la entrada
     */
    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    /**
     * Partido asociado a la entrada
     */
    @ManyToOne
    @JoinColumn(name = "partido_id", nullable = false)
    private Partido partido;

    /**
     * Estado de la entrada
     */
    @Column(nullable = false)
    private String estado;

    /**
     * Fecha de compra
     */
    @Column(name = "fecha_compra")
    private LocalDateTime fechaCompra;

    /**
     * Precio de la entrada
     */
    @Column(nullable = false)
    private Double precio;

    /**
     * Tiempo límite de reserva
     */
    @Column(name = "ttl_reserva")
    private LocalDateTime ttlReserva;

    /**
     * Cantidad de entradas
     */
    @Column(nullable = false)
    private Integer cantidad;

    /**
     * Referencia de pago
     */
    @Column(name = "payment_ref")
    private String paymentRef;

    /**
     * Fecha de pago
     */
    @Column(name = "fecha_pago")
    private LocalDateTime fechaPago;

    /**
     * Fecha de reembolso
     */
    @Column(name = "fecha_reembolso")
    private LocalDateTime fechaReembolso;

    /**
     * Categoría de la entrada
     */
    @Column(nullable = false)
    private String categoria;

    /**
     * Sector de la entrada
     */
    @Column
    private String sector;

    /**
     * Fila de la entrada
     */
    @Column
    private String fila;

    /**
     * Asiento inicial
     */
    @Column(name = "asiento_inicio")
    private Integer asientoInicio;

    /**
     * Crea una entrada
     */
    public Entrada() {
        //Constructor vacio
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
     * Asigna el identificador de la entrada
     *
     * @param id identificador de la entrada
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Obtiene el usuario asociado
     *
     * @return usuario asociado
     */
    public Usuario getUsuario() {
        return usuario;
    }

    /**
     * Asigna el usuario asociado
     *
     * @param usuario usuario asociado
     */
    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    /**
     * Obtiene el partido asociado
     *
     * @return partido asociado
     */
    public Partido getPartido() {
        return partido;
    }

    /**
     * Asigna el partido asociado
     *
     * @param partido partido asociado
     */
    public void setPartido(Partido partido) {
        this.partido = partido;
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
     * Asigna el estado de la entrada
     *
     * @param estado estado de la entrada
     */
    public void setEstado(String estado) {
        this.estado = estado;
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
     * Asigna la fecha de compra
     *
     * @param fechaCompra fecha de compra
     */
    public void setFechaCompra(LocalDateTime fechaCompra) {
        this.fechaCompra = fechaCompra;
    }

    /**
     * Obtiene el precio de la entrada
     *
     * @return precio de la entrada
     */
    public Double getPrecio() {
        return precio;
    }

    /**
     * Asigna el precio de la entrada
     *
     * @param precio precio de la entrada
     */
    public void setPrecio(Double precio) {
        this.precio = precio;
    }

    /**
     * Obtiene el tiempo límite de reserva
     *
     * @return tiempo límite de reserva
     */
    public LocalDateTime getTtlReserva() {
        return ttlReserva;
    }

    /**
     * Asigna el tiempo límite de reserva
     *
     * @param ttlReserva tiempo límite de reserva
     */
    public void setTtlReserva(LocalDateTime ttlReserva) {
        this.ttlReserva = ttlReserva;
    }

    /**
     * Obtiene la cantidad de entradas
     *
     * @return cantidad de entradas
     */
    public Integer getCantidad() {
        return cantidad;
    }

    /**
     * Asigna la cantidad de entradas
     *
     * @param cantidad cantidad de entradas
     */
    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    /**
     * Obtiene la referencia de pago
     *
     * @return referencia de pago
     */
    public String getPaymentRef() {
        return paymentRef;
    }

    /**
     * Asigna la referencia de pago
     *
     * @param paymentRef referencia de pago
     */
    public void setPaymentRef(String paymentRef) {
        this.paymentRef = paymentRef;
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
     * Asigna la fecha de pago
     *
     * @param fechaPago fecha de pago
     */
    public void setFechaPago(LocalDateTime fechaPago) {
        this.fechaPago = fechaPago;
    }

    /**
     * Obtiene la fecha de reembolso
     *
     * @return fecha de reembolso
     */
    public LocalDateTime getFechaReembolso() {
        return fechaReembolso;
    }

    /**
     * Asigna la fecha de reembolso
     *
     * @param fechaReembolso fecha de reembolso
     */
    public void setFechaReembolso(LocalDateTime fechaReembolso) {
        this.fechaReembolso = fechaReembolso;
    }

    /**
     * Obtiene la categoría de la entrada
     *
     * @return categoría de la entrada
     */
    public String getCategoria() {
        return categoria;
    }

    /**
     * Asigna la categoría de la entrada
     *
     * @param categoria categoría de la entrada
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
     * Asigna el sector de la entrada
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
     * Asigna la fila de la entrada
     *
     * @param fila fila de la entrada
     */
    public void setFila(String fila) {
        this.fila = fila;
    }

    /**
     * Obtiene el asiento inicial
     *
     * @return asiento inicial
     */
    public Integer getAsientoInicio() {
        return asientoInicio;
    }

    /**
     * Asigna el asiento inicial
     *
     * @param asientoInicio asiento inicial
     */
    public void setAsientoInicio(Integer asientoInicio) {
        this.asientoInicio = asientoInicio;
    }
}