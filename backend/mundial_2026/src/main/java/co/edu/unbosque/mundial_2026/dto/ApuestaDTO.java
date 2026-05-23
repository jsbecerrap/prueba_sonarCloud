package co.edu.unbosque.mundial_2026.dto;

import java.time.LocalDateTime;

/**
 * DTO que representa los datos básicos de una apuesta (polla).
 * <p>
 * Se utiliza para transferir la información esencial de una apuesta
 * sin incluir la lista detallada de sus participantes.
 * </p>
 */
public class ApuestaDTO {

    /** Identificador único de la apuesta. */
    private Long id;

    /** Nombre descriptivo de la apuesta. */
    private String nombre;

    /** Estado actual de la apuesta (por ejemplo: ABIERTA, CERRADA, FINALIZADA). */
    private String estado;

    /** Código de invitación que permite a otros usuarios unirse a la apuesta. */
    private String codigoInvitacion;

    /** Fecha y hora límite hasta la cual se aceptan pronósticos. */
    private LocalDateTime fechaCierre;

    /** Identificador del usuario que creó la apuesta. */
    private Long creadoPor;

    /**
     * Constructor vacío requerido por frameworks de serialización.
     */
    public ApuestaDTO() {
    }

    /**
     * Constructor completo para inicializar todos los campos de la apuesta.
     *
     * @param id               identificador único de la apuesta
     * @param nombre           nombre de la apuesta
     * @param estado           estado actual de la apuesta
     * @param codigoInvitacion código para unirse a la apuesta
     * @param fechaCierre      fecha y hora de cierre de la apuesta
     * @param creadoPor        ID del usuario creador
     */
    public ApuestaDTO(Long id, String nombre, String estado, String codigoInvitacion,
            LocalDateTime fechaCierre, Long creadoPor) {
        this.id = id;
        this.nombre = nombre;
        this.estado = estado;
        this.codigoInvitacion = codigoInvitacion;
        this.fechaCierre = fechaCierre;
        this.creadoPor = creadoPor;
    }

    /**
     * Obtiene el identificador único de la apuesta.
     *
     * @return ID de la apuesta
     */
    public Long getId() {
        return id;
    }

    /**
     * Establece el identificador único de la apuesta.
     *
     * @param id ID de la apuesta
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Obtiene el nombre de la apuesta.
     *
     * @return nombre de la apuesta
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Establece el nombre de la apuesta.
     *
     * @param nombre nombre de la apuesta
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Obtiene el estado actual de la apuesta.
     *
     * @return estado de la apuesta
     */
    public String getEstado() {
        return estado;
    }

    /**
     * Establece el estado actual de la apuesta.
     *
     * @param estado estado de la apuesta
     */
    public void setEstado(String estado) {
        this.estado = estado;
    }

    /**
     * Obtiene el código de invitación de la apuesta.
     *
     * @return código de invitación
     */
    public String getCodigoInvitacion() {
        return codigoInvitacion;
    }

    /**
     * Establece el código de invitación de la apuesta.
     *
     * @param codigoInvitacion código de invitación
     */
    public void setCodigoInvitacion(String codigoInvitacion) {
        this.codigoInvitacion = codigoInvitacion;
    }

    /**
     * Obtiene la fecha y hora de cierre de la apuesta.
     *
     * @return fecha y hora de cierre
     */
    public LocalDateTime getFechaCierre() {
        return fechaCierre;
    }

    /**
     * Establece la fecha y hora de cierre de la apuesta.
     *
     * @param fechaCierre fecha y hora de cierre
     */
    public void setFechaCierre(LocalDateTime fechaCierre) {
        this.fechaCierre = fechaCierre;
    }

    /**
     * Obtiene el ID del usuario que creó la apuesta.
     *
     * @return ID del usuario creador
     */
    public Long getCreadoPor() {
        return creadoPor;
    }

    /**
     * Establece el ID del usuario que creó la apuesta.
     *
     * @param creadoPor ID del usuario creador
     */
    public void setCreadoPor(Long creadoPor) {
        this.creadoPor = creadoPor;
    }
}