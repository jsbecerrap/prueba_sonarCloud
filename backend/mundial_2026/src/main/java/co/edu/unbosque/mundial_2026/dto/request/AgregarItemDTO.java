package co.edu.unbosque.mundial_2026.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * DTO de solicitud para agregar un ítem (producto con variante) al carrito de compras.
 * <p>
 * Valida que el producto, la cantidad mínima y la variante sean proporcionados
 * antes de procesar la solicitud.
 * </p>
 */
public class AgregarItemDTO {

    /**
     * Identificador del producto que se desea agregar al carrito.
     * Es obligatorio.
     */
    @NotNull(message = "El producto es obligatorio")
    private Long productoId;

    /**
     * Cantidad de unidades a agregar.
     * Es obligatoria y debe ser al menos 1.
     */
    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad minima es 1")
    private Integer cantidad;

    /**
     * Identificador de la variante del producto (talla, color, etc.).
     * Es obligatorio.
     */
    @NotNull(message = "La variante es obligatoria")
    private Long varianteId;

    /**
     * Obtiene el identificador del producto.
     *
     * @return ID del producto
     */
    public Long getProductoId() {
        return productoId;
    }

    /**
     * Establece el identificador del producto.
     *
     * @param productoId ID del producto
     */
    public void setProductoId(Long productoId) {
        this.productoId = productoId;
    }

    /**
     * Obtiene la cantidad de unidades a agregar.
     *
     * @return cantidad de unidades
     */
    public Integer getCantidad() {
        return cantidad;
    }

    /**
     * Establece la cantidad de unidades a agregar.
     *
     * @param cantidad cantidad de unidades (mínimo 1)
     */
    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    /**
     * Obtiene el identificador de la variante del producto.
     *
     * @return ID de la variante
     */
    public Long getVarianteId() {
        return varianteId;
    }

    /**
     * Establece el identificador de la variante del producto.
     *
     * @param varianteId ID de la variante
     */
    public void setVarianteId(Long varianteId) {
        this.varianteId = varianteId;
    }
}