package co.edu.unbosque.mundial_2026.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO de respuesta para las ligas
 * Contiene informacion de la liga y la ronda
 */
public class LigaDTO {

    /**
     * Identificador de la liga
     */
    @JsonProperty("id")
    private Long id;

    /**
     * Nombre de la liga
     */
    @JsonProperty("name")
    private String nombre;

    /**
     * Ronda de la liga
     */
    @JsonProperty("round")
    private String ronda;

    /**
     * Obtiene el identificador de la liga
     *
     * @return identificador de la liga
     */
    public Long getId() {
        return id;
    }

    /**
     * Establece el identificador de la liga
     *
     * @param id identificador de la liga
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Obtiene el nombre de la liga
     *
     * @return nombre de la liga
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Establece el nombre de la liga
     *
     * @param nombre nombre de la liga
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Obtiene la ronda de la liga
     *
     * @return ronda de la liga
     */
    public String getRonda() {
        return ronda;
    }

    /**
     * Establece la ronda de la liga
     *
     * @param ronda ronda de la liga
     */
    public void setRonda(String ronda) {
        this.ronda = ronda;
    }
}