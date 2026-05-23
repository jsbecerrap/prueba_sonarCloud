package co.edu.unbosque.mundial_2026.dto;

import java.time.LocalDateTime;

/**
 * DTO que representa un evento de auditoría registrado en el sistema.
 * <p>
 * Contiene la información necesaria para rastrear acciones realizadas
 * por los usuarios o el sistema, facilitando el monitoreo y la trazabilidad.
 * </p>
 */
public class EventoAuditoriaDTO {

    /** Identificador único del evento de auditoría. */
    private Long id;

    /** Tipo de evento registrado (por ejemplo: USUARIO_CREADO, ORDEN_CONFIRMADA). */
    private String tipo;

    /** Descripción detallada del evento ocurrido. */
    private String descripcion;

    /** Fecha y hora exacta en que ocurrió el evento. */
    private LocalDateTime fecha;

    /** Identificador de correlación que vincula el evento con una entidad específica. */
    private String idCorrelacion;

    /** Nombre de la entidad de dominio relacionada con el evento (por ejemplo: "Usuario", "Orden"). */
    private String entidadCorrelacion;

    /** Identificador del usuario que desencadenó el evento. Puede ser {@code null} si fue un proceso automático. */
    private Long usuarioId;

    /**
     * Constructor vacío requerido por frameworks de serialización.
     */
    public EventoAuditoriaDTO() {
    }

    /**
     * Constructor completo para inicializar todos los campos del evento de auditoría.
     *
     * @param id                   identificador único del evento
     * @param tipo                 tipo de evento registrado
     * @param descripcion          descripción detallada del evento
     * @param fecha                fecha y hora del evento
     * @param idCorrelacion        identificador de la entidad relacionada
     * @param entidadCorrelacion   nombre de la entidad relacionada
     * @param usuarioId            ID del usuario que generó el evento
     */
    public EventoAuditoriaDTO(Long id, String tipo, String descripcion, LocalDateTime fecha,
            String idCorrelacion, String entidadCorrelacion, Long usuarioId) {
        this.id = id;
        this.tipo = tipo;
        this.descripcion = descripcion;
        this.fecha = fecha;
        this.idCorrelacion = idCorrelacion;
        this.entidadCorrelacion = entidadCorrelacion;
        this.usuarioId = usuarioId;
    }

    /**
     * Obtiene el identificador único del evento.
     *
     * @return ID del evento de auditoría
     */
    public Long getId() {
        return id;
    }

    /**
     * Establece el identificador único del evento.
     *
     * @param id ID del evento de auditoría
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Obtiene el tipo de evento registrado.
     *
     * @return tipo del evento
     */
    public String getTipo() {
        return tipo;
    }

    /**
     * Establece el tipo de evento registrado.
     *
     * @param tipo tipo del evento
     */
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    /**
     * Obtiene la descripción detallada del evento.
     *
     * @return descripción del evento
     */
    public String getDescripcion() {
        return descripcion;
    }

    /**
     * Establece la descripción detallada del evento.
     *
     * @param descripcion descripción del evento
     */
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    /**
     * Obtiene la fecha y hora en que ocurrió el evento.
     *
     * @return fecha y hora del evento
     */
    public LocalDateTime getFecha() {
        return fecha;
    }

    /**
     * Establece la fecha y hora en que ocurrió el evento.
     *
     * @param fecha fecha y hora del evento
     */
    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    /**
     * Obtiene el identificador de correlación del evento.
     *
     * @return ID de correlación con la entidad relacionada
     */
    public String getIdCorrelacion() {
        return idCorrelacion;
    }

    /**
     * Establece el identificador de correlación del evento.
     *
     * @param idCorrelacion ID de correlación con la entidad relacionada
     */
    public void setIdCorrelacion(String idCorrelacion) {
        this.idCorrelacion = idCorrelacion;
    }

    /**
     * Obtiene el nombre de la entidad de dominio relacionada con el evento.
     *
     * @return nombre de la entidad correlacionada
     */
    public String getEntidadCorrelacion() {
        return entidadCorrelacion;
    }

    /**
     * Establece el nombre de la entidad de dominio relacionada con el evento.
     *
     * @param entidadCorrelacion nombre de la entidad correlacionada
     */
    public void setEntidadCorrelacion(String entidadCorrelacion) {
        this.entidadCorrelacion = entidadCorrelacion;
    }

    /**
     * Obtiene el ID del usuario que desencadenó el evento.
     *
     * @return ID del usuario, o {@code null} si fue un proceso automático
     */
    public Long getUsuarioId() {
        return usuarioId;
    }

    /**
     * Establece el ID del usuario que desencadenó el evento.
     *
     * @param usuarioId ID del usuario
     */
    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }
}