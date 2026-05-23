package co.edu.unbosque.mundial_2026.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO de respuesta para los equipos de un partido
 * Contiene el equipo local y el equipo visitante
 */
public class EquipoConEstadioDTO {

    /**
     * Equipo local del partido
     */
    @JsonProperty("home")
    private EquipoDTO local;

    /**
     * Equipo visitante del partido
     */
    @JsonProperty("away")
    private EquipoDTO visitante;

    /**
     * Obtiene el equipo local
     *
     * @return equipo local
     */
    public EquipoDTO getLocal() {
        return local;
    }

    /**
     * Establece el equipo local
     *
     * @param local equipo local
     */
    public void setLocal(EquipoDTO local) {
        this.local = local;
    }

    /**
     * Obtiene el equipo visitante
     *
     * @return equipo visitante
     */
    public EquipoDTO getVisitante() {
        return visitante;
    }

    /**
     * Establece el equipo visitante
     *
     * @param visitante equipo visitante
     */
    public void setVisitante(EquipoDTO visitante) {
        this.visitante = visitante;
    }
}