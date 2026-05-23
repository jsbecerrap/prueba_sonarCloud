package co.edu.unbosque.mundial_2026.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO de respuesta para los marcadores de partidos
 * Contiene los goles del equipo local y visitante
 */
public class MarcadorDTO {

    /**
     * Goles del equipo local
     */
    @JsonProperty("home")
    private Integer local;

    /**
     * Goles del equipo visitante
     */
    @JsonProperty("away")
    private Integer visitante;

    /**
     * Obtiene los goles del equipo local
     *
     * @return goles del equipo local
     */
    public Integer getLocal() {
        return local;
    }

    /**
     * Establece los goles del equipo local
     *
     * @param local goles del equipo local
     */
    public void setLocal(Integer local) {
        this.local = local;
    }

    /**
     * Obtiene los goles del equipo visitante
     *
     * @return goles del equipo visitante
     */
    public Integer getVisitante() {
        return visitante;
    }

    /**
     * Establece los goles del equipo visitante
     *
     * @param visitante goles del equipo visitante
     */
    public void setVisitante(Integer visitante) {
        this.visitante = visitante;
    }
}