package co.edu.unbosque.mundial_2026.dto.response;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO de respuesta para los equipos del mundial
 * Contiene la lista de equipos obtenidos desde la API
 */
public class EquipoMundialResponseDTO {

    /**
     * Lista de equipos del mundial
     */
    @JsonProperty("response")
    private List<EquipoMundialDTO> equipos;

    /**
     * Obtiene la lista de equipos
     *
     * @return lista de equipos
     */
    public List<EquipoMundialDTO> getEquipos() {
        return equipos;
    }

    /**
     * Establece la lista de equipos
     *
     * @param equipos lista de equipos
     */
    public void setEquipos(List<EquipoMundialDTO> equipos) {
        this.equipos = equipos;
    }
}