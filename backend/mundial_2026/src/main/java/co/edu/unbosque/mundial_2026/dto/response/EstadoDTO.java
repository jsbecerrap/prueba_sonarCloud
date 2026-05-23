package co.edu.unbosque.mundial_2026.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO de respuesta para el estado de un partido
 * Contiene el codigo y la descripcion del estado
 */
public class EstadoDTO {

    /**
     * Codigo corto del estado
     */
    @JsonProperty("short")
    private String codigo;

    /**
     * Descripcion del estado
     */
    @JsonProperty("long")
    private String descripcion;

    /**
     * Obtiene el codigo del estado
     *
     * @return codigo del estado
     */
    public String getCodigo() {
        return codigo;
    }

    /**
     * Establece el codigo del estado
     *
     * @param codigo codigo del estado
     */
    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    /**
     * Obtiene la descripcion del estado
     *
     * @return descripcion del estado
     */
    public String getDescripcion() {
        return descripcion;
    }

    /**
     * Establece la descripcion del estado
     *
     * @param descripcion descripcion del estado
     */
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}