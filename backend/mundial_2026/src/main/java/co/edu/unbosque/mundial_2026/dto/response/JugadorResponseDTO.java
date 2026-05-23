package co.edu.unbosque.mundial_2026.dto.response;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO de respuesta para los jugadores de los equipos
 * Contiene la lista de respuestas obtenidas desde la API
 */
public class JugadorResponseDTO {

    /**
     * Lista de respuestas con jugadores por equipo
     */
    @JsonProperty("response")
    private List<EquipoJugadorDTO> respuesta;

    /**
     * Obtiene la lista de respuestas
     *
     * @return lista de respuestas
     */
    public List<EquipoJugadorDTO> getRespuesta() {
        return respuesta;
    }

    /**
     * Establece la lista de respuestas
     *
     * @param respuesta lista de respuestas
     */
    public void setRespuesta(List<EquipoJugadorDTO> respuesta) {
        this.respuesta = respuesta;
    }
}