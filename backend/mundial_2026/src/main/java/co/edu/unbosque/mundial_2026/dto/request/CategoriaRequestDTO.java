package co.edu.unbosque.mundial_2026.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * DTO de solicitud para crear o actualizar una categoría de productos.
 * <p>
 * Incluye validaciones sobre el nombre y la descripción para garantizar
 * que los datos sean consistentes y no contengan caracteres no permitidos.
 * </p>
 */
public class CategoriaRequestDTO {

    /**
     * Nombre de la categoría. Es obligatorio, debe tener entre 2 y 60 caracteres
     * y solo puede contener letras, números, espacios, puntos, guiones bajos o guiones.
     */
    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 60, message = "El nombre debe tener entre 2 y 60 caracteres")
    @Pattern(
        regexp = "^[A-Za-zÁÉÍÓÚáéíóúÑñ0-9\\s_.-]+$",
        message = "El nombre solo puede tener letras, números, espacios, puntos, guiones bajos o guiones"
    )
    private String nombre;

    /**
     * Descripción de la categoría. Opcional; si se proporciona, no puede superar
     * los 250 caracteres y solo puede contener caracteres alfanuméricos y puntuación básica.
     */
    @Size(max = 250, message = "La descripción no puede tener más de 250 caracteres")
    @Pattern(
        regexp = "^[A-Za-zÁÉÍÓÚáéíóúÑñ0-9\\s_.,;:()\\-]*$",
        message = "La descripción contiene caracteres no permitidos"
    )
    private String descripcion;

    /**
     * Obtiene el nombre de la categoría.
     *
     * @return nombre de la categoría
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Establece el nombre de la categoría.
     *
     * @param nombre nombre de la categoría
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Obtiene la descripción de la categoría.
     *
     * @return descripción de la categoría, puede ser {@code null}
     */
    public String getDescripcion() {
        return descripcion;
    }

    /**
     * Establece la descripción de la categoría.
     *
     * @param descripcion descripción de la categoría
     */
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}