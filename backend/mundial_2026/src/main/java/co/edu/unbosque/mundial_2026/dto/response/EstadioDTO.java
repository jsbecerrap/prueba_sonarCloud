package co.edu.unbosque.mundial_2026.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO de respuesta para los estadios
 * Contiene el nombre y la ciudad del estadio
 */
public class EstadioDTO {

    /**
     * Nombre del estadio
     */
    @JsonProperty("name")
    private String nombre;

    /**
     * Ciudad del estadio
     */
    @JsonProperty("city")
    private String ciudad;

    /**
     * Obtiene el nombre del estadio
     *
     * @return nombre del estadio
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Establece el nombre del estadio
     *
     * @param nombre nombre del estadio
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Obtiene la ciudad del estadio
     *
     * @return ciudad del estadio
     */
    public String getCiudad() {
        return ciudad;
    }

    /**
     * Establece la ciudad del estadio
     *
     * @param ciudad ciudad del estadio
     */
    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }
}