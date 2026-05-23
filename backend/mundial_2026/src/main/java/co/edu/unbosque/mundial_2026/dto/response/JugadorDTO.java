package co.edu.unbosque.mundial_2026.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO de respuesta para los jugadores
 * Contiene informacion basica del jugador
 */
public class JugadorDTO {

    /**
     * Identificador del jugador
     */
    @JsonProperty("id")
    private Long id;

    /**
     * Nombre del jugador
     */
    @JsonProperty("name")
    private String nombre;

    /**
     * Edad del jugador
     */
    @JsonProperty("age")
    private Integer edad;

    /**
     * Nacionalidad del jugador
     */
    @JsonProperty("nationality")
    private String nacionalidad;

    /**
     * Foto del jugador
     */
    @JsonProperty("photo")
    private String foto;

    /**
     * Obtiene el identificador del jugador
     *
     * @return identificador del jugador
     */
    public Long getId() {
        return id;
    }

    /**
     * Establece el identificador del jugador
     *
     * @param id identificador del jugador
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Obtiene el nombre del jugador
     *
     * @return nombre del jugador
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Establece el nombre del jugador
     *
     * @param nombre nombre del jugador
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Obtiene la edad del jugador
     *
     * @return edad del jugador
     */
    public Integer getEdad() {
        return edad;
    }

    /**
     * Establece la edad del jugador
     *
     * @param edad edad del jugador
     */
    public void setEdad(Integer edad) {
        this.edad = edad;
    }

    /**
     * Obtiene la nacionalidad del jugador
     *
     * @return nacionalidad del jugador
     */
    public String getNacionalidad() {
        return nacionalidad;
    }

    /**
     * Establece la nacionalidad del jugador
     *
     * @param nacionalidad nacionalidad del jugador
     */
    public void setNacionalidad(String nacionalidad) {
        this.nacionalidad = nacionalidad;
    }

    /**
     * Obtiene la foto del jugador
     *
     * @return foto del jugador
     */
    public String getFoto() {
        return foto;
    }

    /**
     * Establece la foto del jugador
     *
     * @param foto foto del jugador
     */
    public void setFoto(String foto) {
        this.foto = foto;
    }
}