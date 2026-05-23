package co.edu.unbosque.mundial_2026.dto.response;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO de respuesta para los partidos
 * Contiene la lista de partidos obtenidos desde la API
 */
public class PartidoResponseDTO {

    /**
     * Lista de partidos
     */
    @JsonProperty("response")
    private List<PartidoDTO> partidos;

    /**
     * Obtiene la lista de partidos
     *
     * @return lista de partidos
     */
    public List<PartidoDTO> getPartidos() {
        return partidos;
    }

    /**
     * Establece la lista de partidos
     *
     * @param partidos lista de partidos
     */
    public void setPartidos(List<PartidoDTO> partidos) {
        this.partidos = partidos;
    }
}