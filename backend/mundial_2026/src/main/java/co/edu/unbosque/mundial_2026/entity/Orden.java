package co.edu.unbosque.mundial_2026.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.BatchSize;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
/**
 * Entidad que representa una orden de compra realizada por un usuario.
 * DynamicUpdate permite que Hibernate actualice únicamente
 * los campos modificados en la base de datos.
 */
@Entity
@Table(name = "ordenes")
@org.hibernate.annotations.DynamicUpdate
public class Orden {

    /**
     * Identificador único de la orden.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Usuario asociado a la orden.
     * Se carga de forma lazy, por lo que Hibernate
     * solo recupera el usuario cuando realmente se accede a él.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    /**
     * Estado actual de la orden.
     */
    @Column(nullable = false)
    private String estado;

    /**
     * Valor total de la orden.
     */
    @Column(nullable = false)
    private Double total;

    /**
     * Fecha y hora en que fue creada la orden.
     */
    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;

    /**
     * Fecha y hora en que se realizó el pago.
     */
    @Column(name = "fecha_pago")
    private LocalDateTime fechaPago;

    /**
     * Referencia externa asociada al pago.
     */
    @Column(name = "payment_ref")
    private String paymentRef;

    /**
     * Método de pago usado en la orden.
     */
    @ManyToOne
    @JoinColumn(name = "metodo_pago_id")
    private MetodoPago metodoPago;

    /**
     * Lista de items pertenecientes a la orden.
     * Se carga de forma lazy para evitar traer todos
     * los items inmediatamente.
     * BatchSize optimiza la carga agrupándolos en bloques de 30.
     */
    @OneToMany(mappedBy = "orden", fetch = FetchType.LAZY)
    @BatchSize(size = 30)
    private List<ItemOrden> items = new ArrayList<>();

    /**
     * Indica si ya se notificó el abandono de la orden.
     */
    @Column(name = "notificado_abandonado", nullable = false)
    private boolean notificadoAbandonado;

    /**
     * Constructor vacío.
     */
    public Orden() {
        // Constructor vacio
    }

    /**
     * Retorna si la orden abandonada ya fue notificada.
     *
     * @return true si ya fue notificada, false en caso contrario
     */
    public boolean isNotificadoAbandonado() {
        return notificadoAbandonado;
    }

    /**
     * Define el estado de notificación de abandono.
     *
     * @param notificadoAbandonado nuevo estado de notificación
     */
    public void setNotificadoAbandonado(boolean notificadoAbandonado) {
        this.notificadoAbandonado = notificadoAbandonado;
    }

    /**
     * Retorna el identificador de la orden.
     *
     * @return id de la orden
     */
    public Long getId() {
        return id;
    }

    /**
     * Define el identificador de la orden.
     *
     * @param id nuevo identificador
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Retorna el usuario asociado.
     *
     * @return usuario de la orden
     */
    public Usuario getUsuario() {
        return usuario;
    }

    /**
     * Define el usuario asociado.
     *
     * @param usuario nuevo usuario
     */
    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    /**
     * Retorna el estado de la orden.
     *
     * @return estado actual
     */
    public String getEstado() {
        return estado;
    }

    /**
     * Define el estado de la orden.
     *
     * @param estado nuevo estado
     */
    public void setEstado(String estado) {
        this.estado = estado;
    }

    /**
     * Retorna el total de la orden.
     *
     * @return valor total
     */
    public Double getTotal() {
        return total;
    }

    /**
     * Define el total de la orden.
     *
     * @param total nuevo valor total
     */
    public void setTotal(Double total) {
        this.total = total;
    }

    /**
     * Retorna la fecha de creación.
     *
     * @return fecha de creación
     */
    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    /**
     * Define la fecha de creación.
     *
     * @param fechaCreacion nueva fecha de creación
     */
    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    /**
     * Retorna la fecha de pago.
     *
     * @return fecha de pago
     */
    public LocalDateTime getFechaPago() {
        return fechaPago;
    }

    /**
     * Define la fecha de pago.
     *
     * @param fechaPago nueva fecha de pago
     */
    public void setFechaPago(LocalDateTime fechaPago) {
        this.fechaPago = fechaPago;
    }

    /**
     * Retorna la referencia de pago.
     *
     * @return referencia externa del pago
     */
    public String getPaymentRef() {
        return paymentRef;
    }

    /**
     * Define la referencia de pago.
     *
     * @param paymentRef nueva referencia
     */
    public void setPaymentRef(String paymentRef) {
        this.paymentRef = paymentRef;
    }

    /**
     * Retorna el método de pago.
     *
     * @return método de pago asociado
     */
    public MetodoPago getMetodoPago() {
        return metodoPago;
    }

    /**
     * Define el método de pago.
     *
     * @param metodoPago nuevo método de pago
     */
    public void setMetodoPago(MetodoPago metodoPago) {
        this.metodoPago = metodoPago;
    }

    /**
     * Retorna la lista de items de la orden.
     *
     * @return lista de items
     */
    public List<ItemOrden> getItems() {
        return items;
    }

    /**
     * Define la lista de items de la orden.
     *
     * @param items nueva lista de items
     */
    public void setItems(List<ItemOrden> items) {
        this.items = items;
    }
}