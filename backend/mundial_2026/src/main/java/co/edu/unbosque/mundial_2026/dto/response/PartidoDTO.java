package co.edu.unbosque.mundial_2026.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO de respuesta para los partidos
 * Contiene informacion general del partido
 */
public class PartidoDTO {

    /**
     * Informacion del partido
     */
    @JsonProperty("fixture")
    private InfoPartidoDTO informacion;

    /**
     * Informacion de la liga
     */
    @JsonProperty("league")
    private LigaDTO liga;

    /**
     * Equipos del partido
     */
    @JsonProperty("teams")
    private EquipoConEstadioDTO equipos;

    /**
     * Marcador del partido
     */
    @JsonProperty("goals")
    private MarcadorDTO goles;

    /**
     * Obtiene la informacion del partido
     *
     * @return informacion del partido
     */
    public InfoPartidoDTO getInformacion() {
        return informacion;
    }

    /**
     * Establece la informacion del partido
     *
     * @param informacion informacion del partido
     */
    public void setInformacion(InfoPartidoDTO informacion) {
        this.informacion = informacion;
    }

    /**
     * Obtiene la informacion de la liga
     *
     * @return informacion de la liga
     */
    public LigaDTO getLiga() {
        return liga;
    }

    /**
     * Establece la informacion de la liga
     *
     * @param liga informacion de la liga
     */
    public void setLiga(LigaDTO liga) {
        this.liga = liga;
    }

    /**
     * Obtiene los equipos del partido
     *
     * @return equipos del partido
     */
    public EquipoConEstadioDTO getEquipos() {
        return equipos;
    }

    /**
     * Establece los equipos del partido
     *
     * @param equipos equipos del partido
     */
    public void setEquipos(EquipoConEstadioDTO equipos) {
        this.equipos = equipos;
    }

    /**
     * Obtiene el marcador del partido
     *
     * @return marcador del partido
     */
    public MarcadorDTO getGoles() {
        return goles;
    }

    /**
     * Establece el marcador del partido
     *
     * @param goles marcador del partido
     */
    public void setGoles(MarcadorDTO goles) {
        this.goles = goles;
    }
}