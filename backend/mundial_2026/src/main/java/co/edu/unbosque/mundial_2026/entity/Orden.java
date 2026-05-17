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

@Entity
@Table(name = "ordenes")
public class Orden {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

   @ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "usuario_id", nullable = false)
private Usuario usuario;

    @Column(nullable = false)
    private String estado;

    @Column(nullable = false)
    private Double total;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_pago")
    private LocalDateTime fechaPago;

    @Column(name = "payment_ref")
    private String paymentRef;

    @ManyToOne
    @JoinColumn(name = "metodo_pago_id")
    private MetodoPago metodoPago;

    @OneToMany(mappedBy = "orden", fetch = FetchType.LAZY)
    @BatchSize(size = 30)
    private List<ItemOrden> items = new ArrayList<>();
    @Column(name = "notificado_abandonado", nullable = false)
    private boolean notificadoAbandonado = false;

    public boolean isNotificadoAbandonado() {
        return notificadoAbandonado;
    }

    public void setNotificadoAbandonado(boolean notificadoAbandonado) {
        this.notificadoAbandonado = notificadoAbandonado;
    }

    public Orden() {
        // Constructor vacio
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public LocalDateTime getFechaPago() {
        return fechaPago;
    }

    public void setFechaPago(LocalDateTime fechaPago) {
        this.fechaPago = fechaPago;
    }

    public String getPaymentRef() {
        return paymentRef;
    }

    public void setPaymentRef(String paymentRef) {
        this.paymentRef = paymentRef;
    }

    public MetodoPago getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(MetodoPago metodoPago) {
        this.metodoPago = metodoPago;
    }

    public List<ItemOrden> getItems() {
        return items;
    }

    public void setItems(List<ItemOrden> items) {
        this.items = items;
    }
}