package co.edu.unbosque.mundial_2026.dto.response;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO de respuesta para las clasificaciones
 * Contiene la informacion de la tabla de posiciones
 */
public class StandingResponseDTO {

    /**
     * Lista de respuestas de standings
     */
    @JsonProperty("response")
    private List<LigaStandingDTO> respuesta;

    /**
     * Obtiene la lista de standings
     *
     * @return lista de standings
     */
    public List<LigaStandingDTO> getRespuesta() {
        return respuesta;
    }

    /**
     * Establece la lista de standings
     *
     * @param respuesta lista de standings
     */
    public void setRespuesta(List<LigaStandingDTO> respuesta) {
        this.respuesta = respuesta;
    }
}