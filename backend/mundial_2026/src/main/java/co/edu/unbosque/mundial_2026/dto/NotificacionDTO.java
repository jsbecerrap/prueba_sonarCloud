package co.edu.unbosque.mundial_2026.dto;

import java.time.LocalDateTime;

/**
 * DTO que representa una notificación enviada a un usuario del sistema.
 * <p>
 * Contiene toda la información necesaria para mostrar y gestionar
 * notificaciones, incluyendo su tipo, canal de envío y estado de lectura.
 * </p>
 */
public class NotificacionDTO {

    /** Identificador único de la notificación. */
    private Long id;

    /** Tipo de notificación (por ejemplo: SISTEMA, APUESTA, ORDEN). */
    private String tipo;

    /** Título corto de la notificación. */
    private String titulo;

    /** Mensaje completo de la notificación. */
    private String mensaje;

    /** Canal por el que se envió la notificación (por ejemplo: EMAIL, PUSH, IN_APP). */
    private String canal;

    /** Estado de envío de la notificación (por ejemplo: ENVIADA, PENDIENTE, FALLIDA). */
    private String estado;

    /** Indica si el usuario ya leyó la notificación. */
    private boolean leida;

    /** Fecha y hora en que se generó la notificación. */
    private LocalDateTime fecha;

    /** Identificador del usuario destinatario de la notificación. */
    private Long usuarioId;

    /**
     * Constructor vacío requerido por frameworks de serialización.
     */
    public NotificacionDTO() {
    }

    /**
     * Constructor completo para inicializar todos los campos de la notificación.
     *
     * @param id        identificador único de la notificación
     * @param tipo      tipo de notificación
     * @param titulo    título de la notificación
     * @param mensaje   mensaje de la notificación
     * @param canal     canal de envío
     * @param estado    estado de envío
     * @param leida     {@code true} si el usuario ya la leyó
     * @param fecha     fecha y hora de la notificación
     * @param usuarioId ID del usuario destinatario
     */
    public NotificacionDTO(Long id, String tipo, String titulo, String mensaje,
            String canal, String estado, boolean leida, LocalDateTime fecha, Long usuarioId) {
        this.id = id;
        this.tipo = tipo;
        this.titulo = titulo;
        this.mensaje = mensaje;
        this.canal = canal;
        this.estado = estado;
        this.leida = leida;
        this.fecha = fecha;
        this.usuarioId = usuarioId;
    }

    /**
     * Obtiene el identificador único de la notificación.
     *
     * @return ID de la notificación
     */
    public Long getId() {
        return id;
    }

    /**
     * Establece el identificador único de la notificación.
     *
     * @param id ID de la notificación
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Obtiene el tipo de la notificación.
     *
     * @return tipo de la notificación
     */
    public String getTipo() {
        return tipo;
    }

    /**
     * Establece el tipo de la notificación.
     *
     * @param tipo tipo de la notificación
     */
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    /**
     * Obtiene el título de la notificación.
     *
     * @return título de la notificación
     */
    public String getTitulo() {
        return titulo;
    }

    /**
     * Establece el título de la notificación.
     *
     * @param titulo título de la notificación
     */
    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    /**
     * Obtiene el mensaje de la notificación.
     *
     * @return mensaje completo de la notificación
     */
    public String getMensaje() {
        return mensaje;
    }

    /**
     * Establece el mensaje de la notificación.
     *
     * @param mensaje mensaje completo de la notificación
     */
    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    /**
     * Obtiene el canal de envío de la notificación.
     *
     * @return canal de envío
     */
    public String getCanal() {
        return canal;
    }

    /**
     * Establece el canal de envío de la notificación.
     *
     * @param canal canal de envío
     */
    public void setCanal(String canal) {
        this.canal = canal;
    }

    /**
     * Obtiene el estado de envío de la notificación.
     *
     * @return estado de la notificación
     */
    public String getEstado() {
        return estado;
    }

    /**
     * Establece el estado de envío de la notificación.
     *
     * @param estado estado de la notificación
     */
    public void setEstado(String estado) {
        this.estado = estado;
    }

    /**
     * Indica si el usuario ha leído la notificación.
     *
     * @return {@code true} si fue leída, {@code false} en caso contrario
     */
    public boolean isLeida() {
        return leida;
    }

    /**
     * Establece si el usuario ha leído la notificación.
     *
     * @param leida {@code true} si fue leída
     */
    public void setLeida(boolean leida) {
        this.leida = leida;
    }

    /**
     * Obtiene la fecha y hora de la notificación.
     *
     * @return fecha y hora de generación
     */
    public LocalDateTime getFecha() {
        return fecha;
    }

    /**
     * Establece la fecha y hora de la notificación.
     *
     * @param fecha fecha y hora de generación
     */
    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    /**
     * Obtiene el ID del usuario destinatario.
     *
     * @return ID del usuario destinatario
     */
    public Long getUsuarioId() {
        return usuarioId;
    }

    /**
     * Establece el ID del usuario destinatario.
     *
     * @param usuarioId ID del usuario destinatario
     */
    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }
}