package co.edu.unbosque.mundial_2026.dto;

/**
 * DTO que representa la participación de un usuario en una apuesta (polla).
 * <p>
 * Incluye la posición en el ranking y los puntos acumulados por el usuario
 * dentro de una apuesta específica.
 * </p>
 */
public class ParticipacionDTO {

    /** Identificador único de la participación. */
    private Long id;

    /** Identificador del usuario que participa en la apuesta. */
    private Long usuarioId;

    /** Identificador de la apuesta a la que pertenece esta participación. */
    private Long apuestaId;

    /** Puntos acumulados por el usuario en esta apuesta según sus pronósticos acertados. */
    private Integer puntos;

    /** Posición actual del usuario en el ranking de la apuesta. */
    private Integer posicionRanking;

    /**
     * Constructor vacío requerido por frameworks de serialización.
     */
    public ParticipacionDTO() {
    }

    /**
     * Constructor completo para inicializar todos los campos de la participación.
     *
     * @param id              identificador único de la participación
     * @param usuarioId       ID del usuario participante
     * @param apuestaId       ID de la apuesta
     * @param puntos          puntos acumulados por el usuario
     * @param posicionRanking posición en el ranking de la apuesta
     */
    public ParticipacionDTO(Long id, Long usuarioId, Long apuestaId, Integer puntos, Integer posicionRanking) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.apuestaId = apuestaId;
        this.puntos = puntos;
        this.posicionRanking = posicionRanking;
    }

    /**
     * Obtiene el identificador único de la participación.
     *
     * @return ID de la participación
     */
    public Long getId() {
        return id;
    }

    /**
     * Establece el identificador único de la participación.
     *
     * @param id ID de la participación
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Obtiene el ID del usuario participante.
     *
     * @return ID del usuario
     */
    public Long getUsuarioId() {
        return usuarioId;
    }

    /**
     * Establece el ID del usuario participante.
     *
     * @param usuarioId ID del usuario
     */
    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    /**
     * Obtiene el ID de la apuesta asociada.
     *
     * @return ID de la apuesta
     */
    public Long getApuestaId() {
        return apuestaId;
    }

    /**
     * Establece el ID de la apuesta asociada.
     *
     * @param apuestaId ID de la apuesta
     */
    public void setApuestaId(Long apuestaId) {
        this.apuestaId = apuestaId;
    }

    /**
     * Obtiene los puntos acumulados por el usuario en la apuesta.
     *
     * @return puntos del usuario
     */
    public Integer getPuntos() {
        return puntos;
    }

    /**
     * Establece los puntos acumulados por el usuario en la apuesta.
     *
     * @param puntos puntos del usuario
     */
    public void setPuntos(Integer puntos) {
        this.puntos = puntos;
    }

    /**
     * Obtiene la posición actual del usuario en el ranking de la apuesta.
     *
     * @return posición en el ranking
     */
    public Integer getPosicionRanking() {
        return posicionRanking;
    }

    /**
     * Establece la posición actual del usuario en el ranking de la apuesta.
     *
     * @param posicionRanking posición en el ranking
     */
    public void setPosicionRanking(Integer posicionRanking) {
        this.posicionRanking = posicionRanking;
    }
}