package co.edu.unbosque.mundial_2026.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO de respuesta para los equipos del sistema
 * Contiene la informacion basica de un equipo
 */
public class EquipoDTO {

    /**
     * Identificador del equipo
     */
    @JsonProperty("id")
    private Long id;

    /**
     * Nombre del equipo
     */
    @JsonProperty("name")
    private String nombre;

    /**
     * Logo del equipo
     */
    @JsonProperty("logo")
    private String logo;

    /**
     * Obtiene el identificador del equipo
     *
     * @return identificador del equipo
     */
    public Long getId() {
        return id;
    }

    /**
     * Establece el identificador del equipo
     *
     * @param id identificador del equipo
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Obtiene el nombre del equipo
     *
     * @return nombre del equipo
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Establece el nombre del equipo
     *
     * @param nombre nombre del equipo
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Obtiene el logo del equipo
     *
     * @return logo del equipo
     */
    public String getLogo() {
        return logo;
    }

    /**
     * Establece el logo del equipo
     *
     * @param logo logo del equipo
     */
    public void setLogo(String logo) {
        this.logo = logo;
    }
}