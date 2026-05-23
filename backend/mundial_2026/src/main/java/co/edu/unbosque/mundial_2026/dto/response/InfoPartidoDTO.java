package co.edu.unbosque.mundial_2026.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO de respuesta para la informacion de partidos
 * Contiene datos generales del partido
 */
public class InfoPartidoDTO {

    /**
     * Identificador del partido
     */
    @JsonProperty("id")
    private Long id;

    /**
     * Fecha del partido
     */
    @JsonProperty("date")
    private String fecha;

    /**
     * Estado del partido
     */
    @JsonProperty("status")
    private EstadoDTO estado;

    /**
     * Informacion del estadio
     */
    @JsonProperty("venue")
    private EstadioDTO estadio;

    /**
     * Obtiene el identificador del partido
     *
     * @return identificador del partido
     */
    public Long getId() {
        return id;
    }

    /**
     * Establece el identificador del partido
     *
     * @param id identificador del partido
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Obtiene la fecha del partido
     *
     * @return fecha del partido
     */
    public String getFecha() {
        return fecha;
    }

    /**
     * Establece la fecha del partido
     *
     * @param fecha fecha del partido
     */
    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    /**
     * Obtiene el estado del partido
     *
     * @return estado del partido
     */
    public EstadoDTO getEstado() {
        return estado;
    }

    /**
     * Establece el estado del partido
     *
     * @param estado estado del partido
     */
    public void setEstado(EstadoDTO estado) {
        this.estado = estado;
    }

    /**
     * Obtiene la informacion del estadio
     *
     * @return informacion del estadio
     */
    public EstadioDTO getEstadio() {
        return estadio;
    }

    /**
     * Establece la informacion del estadio
     *
     * @param estadio informacion del estadio
     */
    public void setEstadio(EstadioDTO estadio) {
        this.estadio = estadio;
    }
}