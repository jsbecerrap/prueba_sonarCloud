package co.edu.unbosque.mundial_2026.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO de respuesta para los equipos del mundial
 * Contiene la seleccion y el estadio asociado
 */
public class EquipoMundialDTO {

    /**
     * Informacion de la seleccion
     */
    @JsonProperty("team")
    private EquipoInfoDTO seleccion;

    /**
     * Informacion del estadio
     */
    @JsonProperty("venue")
    private EstadioDTO estadio;

    /**
     * Obtiene la informacion de la seleccion
     *
     * @return informacion de la seleccion
     */
    public EquipoInfoDTO getSeleccion() {
        return seleccion;
    }

    /**
     * Establece la informacion de la seleccion
     *
     * @param seleccion informacion de la seleccion
     */
    public void setSeleccion(EquipoInfoDTO seleccion) {
        this.seleccion = seleccion;
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