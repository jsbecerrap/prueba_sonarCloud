package co.edu.unbosque.mundial_2026.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

/**
 * Entidad que representa una notificación enviada a un usuario.
 * Contiene la información del mensaje, su canal de envío,
 * el estado de la notificación y la fecha en que fue registrada.
 */
@Entity
@Table(name = "notificaciones")
public class Notificacion {

    /**
     * Identificador único de la notificación.
     * Se genera automáticamente en la base de datos.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Tipo de notificación.
     * Es un campo obligatorio.
     */
    @Column(nullable = false)
    private String tipo;

    /**
     * Título de la notificación.
     * Es un campo obligatorio.
     */
    @Column(nullable = false)
    private String titulo;

    /**
     * Contenido del mensaje de la notificación.
     * Es un campo obligatorio con un máximo de 500 caracteres.
     */
    @Column(nullable = false, length = 500)
    private String mensaje;

    /**
     * Canal por el cual se envía la notificación.
     * Es un campo obligatorio.
     */
    @Column(nullable = false)
    private String canal;

    /**
     * Estado actual de la notificación.
     * Es un campo obligatorio.
     */
    @Column(nullable = false)
    private String estado;

    /**
     * Indica si la notificación ha sido leída.
     * Es un campo obligatorio.
     */
    @Column(nullable = false)
    private boolean leida;

    /**
     * Fecha de envío de la notificación.
     * Se almacena automáticamente antes de persistir la entidad.
     */
    @Column(name = "fecha_envio", nullable = false)
    private LocalDateTime fecha;

    /**
     * Usuario asociado a la notificación.
     * Se relaciona mediante la clave foránea usuario_id.
     */
    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    /**
     * Constructor vacío requerido por JPA.
     */
    public Notificacion() {
        // Constructor vacío
    }

    /**
     * Crea una notificación con sus datos principales.
     *
     * @param tipo tipo de notificación.
     * @param titulo título de la notificación.
     * @param mensaje contenido del mensaje.
     * @param canal canal de envío.
     * @param estado estado inicial de la notificación.
     * @param usuario usuario asociado.
     */
    public Notificacion(String tipo, String titulo, String mensaje,
            String canal, String estado, Usuario usuario) {
        this.tipo = tipo;
        this.titulo = titulo;
        this.mensaje = mensaje;
        this.canal = canal;
        this.estado = estado;
        this.usuario = usuario;
        this.leida = false;
    }

    /**
     * Inicializa automáticamente la fecha de envío
     * y marca la notificación como no leída antes de guardarla.
     */
    @PrePersist
    public void prePersist() {
        this.fecha = LocalDateTime.now();
        this.leida = false;
    }

    /**
     * Obtiene el identificador de la notificación.
     *
     * @return identificador de la notificación.
     */
    public Long getId() {
        return id;
    }

    /**
     * Asigna el identificador de la notificación.
     *
     * @param id nuevo identificador.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Obtiene el tipo de notificación.
     *
     * @return tipo de la notificación.
     */
    public String getTipo() {
        return tipo;
    }

    /**
     * Asigna el tipo de notificación.
     *
     * @param tipo nuevo tipo.
     */
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    /**
     * Obtiene el título de la notificación.
     *
     * @return título de la notificación.
     */
    public String getTitulo() {
        return titulo;
    }

    /**
     * Asigna el título de la notificación.
     *
     * @param titulo nuevo título.
     */
    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    /**
     * Obtiene el mensaje de la notificación.
     *
     * @return contenido del mensaje.
     */
    public String getMensaje() {
        return mensaje;
    }

    /**
     * Asigna el mensaje de la notificación.
     *
     * @param mensaje nuevo contenido.
     */
    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    /**
     * Obtiene el canal de envío.
     *
     * @return canal de la notificación.
     */
    public String getCanal() {
        return canal;
    }

    /**
     * Asigna el canal de envío.
     *
     * @param canal nuevo canal.
     */
    public void setCanal(String canal) {
        this.canal = canal;
    }

    /**
     * Obtiene el estado actual de la notificación.
     *
     * @return estado de la notificación.
     */
    public String getEstado() {
        return estado;
    }

    /**
     * Asigna el estado de la notificación.
     *
     * @param estado nuevo estado.
     */
    public void setEstado(String estado) {
        this.estado = estado;
    }

    /**
     * Indica si la notificación ha sido leída.
     *
     * @return true si fue leída, false en caso contrario.
     */
    public boolean isLeida() {
        return leida;
    }

    /**
     * Define si la notificación ha sido leída.
     *
     * @param leida nuevo estado de lectura.
     */
    public void setLeida(boolean leida) {
        this.leida = leida;
    }

    /**
     * Obtiene la fecha de envío de la notificación.
     *
     * @return fecha de envío.
     */
    public LocalDateTime getFecha() {
        return fecha;
    }

    /**
     * Asigna la fecha de envío de la notificación.
     *
     * @param fecha nueva fecha de envío.
     */
    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    /**
     * Obtiene el usuario asociado.
     *
     * @return usuario correspondiente.
     */
    public Usuario getUsuario() {
        return usuario;
    }

    /**
     * Asigna el usuario asociado.
     *
     * @param usuario nuevo usuario.
     */
    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
}