package co.edu.unbosque.mundial_2026.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO de respuesta para las posiciones de grupos
 * Contiene informacion del equipo y su posicion
 */
public class PosicionDTO {

    /**
     * Posicion del equipo en la tabla
     */
    @JsonProperty("rank")
    private Integer posicion;

    /**
     * Informacion del equipo
     */
    @JsonProperty("team")
    private EquipoDTO equipo;

    /**
     * Puntos obtenidos por el equipo
     */
    @JsonProperty("points")
    private Integer puntos;

    /**
     * Diferencia de goles del equipo
     */
    @JsonProperty("goalsDiff")
    private Integer diferenciaGoles;

    /**
     * Grupo del equipo
     */
    @JsonProperty("group")
    private String grupo;

    /**
     * Obtiene la posicion del equipo
     *
     * @return posicion del equipo
     */
    public Integer getPosicion() {
        return posicion;
    }

    /**
     * Establece la posicion del equipo
     *
     * @param posicion posicion del equipo
     */
    public void setPosicion(Integer posicion) {
        this.posicion = posicion;
    }

    /**
     * Obtiene la informacion del equipo
     *
     * @return informacion del equipo
     */
    public EquipoDTO getEquipo() {
        return equipo;
    }

    /**
     * Establece la informacion del equipo
     *
     * @param equipo informacion del equipo
     */
    public void setEquipo(EquipoDTO equipo) {
        this.equipo = equipo;
    }

    /**
     * Obtiene los puntos del equipo
     *
     * @return puntos del equipo
     */
    public Integer getPuntos() {
        return puntos;
    }

    /**
     * Establece los puntos del equipo
     *
     * @param puntos puntos del equipo
     */
    public void setPuntos(Integer puntos) {
        this.puntos = puntos;
    }

    /**
     * Obtiene la diferencia de goles
     *
     * @return diferencia de goles
     */
    public Integer getDiferenciaGoles() {
        return diferenciaGoles;
    }

    /**
     * Establece la diferencia de goles
     *
     * @param diferenciaGoles diferencia de goles
     */
    public void setDiferenciaGoles(Integer diferenciaGoles) {
        this.diferenciaGoles = diferenciaGoles;
    }

    /**
     * Obtiene el grupo del equipo
     *
     * @return grupo del equipo
     */
    public String getGrupo() {
        return grupo;
    }

    /**
     * Establece el grupo del equipo
     *
     * @param grupo grupo del equipo
     */
    public void setGrupo(String grupo) {
        this.grupo = grupo;
    }
}