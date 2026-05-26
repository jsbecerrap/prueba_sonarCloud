package co.edu.unbosque.mundial_2026.dto.request;

import jakarta.validation.constraints.PositiveOrZero;

/**
 * DTO de solicitud para actualizar una variante de producto existente.
 * <p>
 * Contiene el identificador de la variante a modificar junto con
 * los campos opcionales que se desean actualizar: especificación y stock.
 * </p>
 */
public class VarianteActualizarDTO {

    /**
     * Identificador único de la variante a actualizar.
     */
    private Long id;

    /**
     * Nueva especificación de la variante.
     * Ejemplos: "S", "M", "XL", "38", "25 cm".
     * Si es nulo o vacío, no se modifica.
     */
    private String especificacion;

    /**
     * Nueva cantidad disponible en inventario para esta variante.
     * No puede ser un valor negativo.
     */
    @PositiveOrZero(message = "El stock no puede ser negativo")
    private Integer stock;

    /**
     * Retorna el identificador de la variante.
     *
     * @return id de la variante
     */
    public Long getId() { return id; }

    /**
     * Establece el identificador de la variante.
     *
     * @param id identificador de la variante
     */
    public void setId(Long id) { this.id = id; }

    /**
     * Retorna la nueva especificación de la variante.
     *
     * @return especificación de la variante
     */
    public String getEspecificacion() { return especificacion; }

    /**
     * Establece la nueva especificación de la variante.
     *
     * @param especificacion nueva especificación
     */
    public void setEspecificacion(String especificacion) { this.especificacion = especificacion; }

    /**
     * Retorna la nueva cantidad en stock de la variante.
     *
     * @return stock disponible
     */
    public Integer getStock() { return stock; }

    /**
     * Establece la nueva cantidad en stock de la variante.
     *
     * @param stock nueva cantidad disponible
     */
    public void setStock(Integer stock) { this.stock = stock; }
}