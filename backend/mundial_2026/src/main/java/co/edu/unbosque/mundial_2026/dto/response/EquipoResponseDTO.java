package co.edu.unbosque.mundial_2026.dto.response;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO de respuesta para los equipos del sistema
 * Contiene la lista de equipos y sus estadios
 */
public class EquipoResponseDTO {

    /**
     * Lista de equipos con informacion del estadio
     */
    @JsonProperty("response")
    private List<EquipoConEstadioDTO> equipos;

    /**
     * Obtiene la lista de equipos
     *
     * @return lista de equipos
     */
    public List<EquipoConEstadioDTO> getEquipos() {
        return equipos;
    }

    /**
     * Establece la lista de equipos
     *
     * @param equipos lista de equipos
     */
    public void setEquipos(List<EquipoConEstadioDTO> equipos) {
        this.equipos = equipos;
    }
}