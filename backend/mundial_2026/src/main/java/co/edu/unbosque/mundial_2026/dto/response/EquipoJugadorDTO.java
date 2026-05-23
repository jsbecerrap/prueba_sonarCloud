package co.edu.unbosque.mundial_2026.dto.response;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO de respuesta para los jugadores de un equipo
 * Contiene la lista de jugadores asociados
 */
public class EquipoJugadorDTO {

    /**
     * Lista de jugadores del equipo
     */
    @JsonProperty("players")
    private List<JugadorDTO> jugadores;

    /**
     * Obtiene la lista de jugadores
     *
     * @return lista de jugadores
     */
    public List<JugadorDTO> getJugadores() {
        return jugadores;
    }

    /**
     * Establece la lista de jugadores
     *
     * @param jugadores lista de jugadores
     */
    public void setJugadores(List<JugadorDTO> jugadores) {
        this.jugadores = jugadores;
    }
}