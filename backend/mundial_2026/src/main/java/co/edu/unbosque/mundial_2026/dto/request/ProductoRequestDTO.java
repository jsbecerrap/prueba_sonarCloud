package co.edu.unbosque.mundial_2026.dto.request;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

/**
 * DTO de solicitud para crear un nuevo producto en el catálogo.
 * <p>
 * Incluye todas las validaciones necesarias para garantizar la integridad
 * de los datos, incluyendo nombre, precio, imagen, categoría y las
 * variantes de stock del producto.
 * </p>
 */
public class ProductoRequestDTO {

    /**
     * Nombre del producto. Es obligatorio, debe tener entre 2 y 100 caracteres
     * y solo puede contener letras, números, espacios, puntos, guiones bajos o guiones.
     */
    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    @Pattern(
        regexp = "^[A-Za-zÁÉÍÓÚáéíóúÑñ0-9\\s_.-]+$",
        message = "El nombre solo puede tener letras, números, espacios, puntos, guiones bajos o guiones"
    )
    private String nombre;

    /**
     * Descripción del producto. Opcional; máximo 500 caracteres con solo
     * caracteres alfanuméricos y puntuación básica.
     */
    @Size(max = 500, message = "La descripción no puede tener más de 500 caracteres")
    @Pattern(
        regexp = "^[A-Za-zÁÉÍÓÚáéíóúÑñ0-9\\s_.,;:()\\-]*$",
        message = "La descripción contiene caracteres no permitidos"
    )
    private String descripcion;

    /**
     * Precio del producto. Es obligatorio, no puede ser negativo
     * y no puede superar 99.999.999,99.
     */
    @NotNull(message = "El precio es obligatorio")
    @PositiveOrZero(message = "El precio no puede ser negativo")
    @DecimalMax(value = "99999999.99", message = "El precio supera el máximo permitido")
    private Double precio;

    /**
     * URL de la imagen del producto. Debe ser una URL HTTPS válida con máximo 500 caracteres.
     */
    @Size(max = 500, message = "La URL de imagen no puede tener más de 500 caracteres")
    @Pattern(
        regexp = "^https://[a-zA-Z0-9._/?&=#%~+\\-:,]+$",
        message = "La URL de la imagen debe ser HTTPS y tener un formato válido"
    )
    private String imagenUrl;

    /**
     * Identificador de la categoría a la que pertenece el producto. Es obligatorio.
     */
    @NotNull(message = "La categoria es obligatoria")
    private Long categoriaId;

    /**
     * Código único de identificación del producto. Máximo 50 caracteres,
     * solo letras, números, guiones bajos o guiones.
     */
    @Size(max = 50, message = "El código de producto no puede tener más de 50 caracteres")
    @Pattern(
        regexp = "^[A-Za-z0-9_\\-]*$",
        message = "El código de producto solo puede tener letras, números, guiones bajos o guiones"
    )
    private String codigoProducto;

    /**
     * Nombre del equipo o selección asociada al producto. Máximo 60 caracteres.
     */
    @Size(max = 60, message = "El equipo no puede tener más de 60 caracteres")
    @Pattern(
        regexp = "^[A-Za-zÁÉÍÓÚáéíóúÑñ0-9\\s_.-]*$",
        message = "El equipo solo puede tener letras, números, espacios, puntos, guiones bajos o guiones"
    )
    private String equipo;

    /**
     * Código de la bandera del país asociado al producto (código ISO de letras).
     * Máximo 10 caracteres, solo letras.
     */
    @Size(max = 10, message = "El código de bandera no puede tener más de 10 caracteres")
    @Pattern(
        regexp = "^[A-Za-z]*$",
        message = "La bandera debe ser un código de país en letras"
    )
    private String bandera;

    /** Indica si el producto debe ser marcado como destacado en el catálogo. Por defecto es {@code false}. */
    private Boolean destacado = false;

    /**
     * Lista de variantes del producto (por ejemplo, tallas o colores).
     * Es obligatoria y debe contener al menos una variante, con un máximo de 50.
     */
    @NotNull(message = "Las variantes son obligatorias")
    @NotEmpty(message = "Debe agregar al menos una variante")
    @Size(max = 50, message = "No puede tener más de 50 variantes")
    @Valid
    private List<VarianteRequestDTO> variantes;

    /**
     * Clase interna que representa una variante de producto
     * (por ejemplo, una talla o especificación específica con su stock).
     */
    public static class VarianteRequestDTO {

        /**
         * Especificación de la variante (por ejemplo: "Talla M", "Color Rojo").
         * Máximo 100 caracteres.
         */
        @Size(max = 100, message = "La especificación no puede tener más de 100 caracteres")
        @Pattern(
            regexp = "^[A-Za-zÁÉÍÓÚáéíóúÑñ0-9\\s_.,;:()\\-]*$",
            message = "La especificación contiene caracteres no permitidos"
        )
        private String especificacion;

        /**
         * Stock disponible para esta variante. Es obligatorio y no puede ser negativo.
         */
        @NotNull(message = "El stock de la variante es obligatorio")
        @PositiveOrZero(message = "El stock no puede ser negativo")
        private Integer stock;

        /**
         * Obtiene la especificación de la variante.
         *
         * @return especificación de la variante
         */
        public String getEspecificacion() {
            return especificacion;
        }

        /**
         * Establece la especificación de la variante.
         *
         * @param especificacion especificación de la variante
         */
        public void setEspecificacion(String especificacion) {
            this.especificacion = especificacion;
        }

        /**
         * Obtiene el stock disponible de la variante.
         *
         * @return stock de la variante
         */
        public Integer getStock() {
            return stock;
        }

        /**
         * Establece el stock disponible de la variante.
         *
         * @param stock stock de la variante
         */
        public void setStock(Integer stock) {
            this.stock = stock;
        }
    }

    /**
     * Obtiene el nombre del producto.
     *
     * @return nombre del producto
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Establece el nombre del producto.
     *
     * @param nombre nombre del producto
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Obtiene la descripción del producto.
     *
     * @return descripción del producto
     */
    public String getDescripcion() {
        return descripcion;
    }

    /**
     * Establece la descripción del producto.
     *
     * @param descripcion descripción del producto
     */
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    /**
     * Obtiene el precio del producto.
     *
     * @return precio del producto
     */
    public Double getPrecio() {
        return precio;
    }

    /**
     * Establece el precio del producto.
     *
     * @param precio precio del producto
     */
    public void setPrecio(Double precio) {
        this.precio = precio;
    }

    /**
     * Obtiene la URL de la imagen del producto.
     *
     * @return URL de la imagen
     */
    public String getImagenUrl() {
        return imagenUrl;
    }

    /**
     * Establece la URL de la imagen del producto.
     *
     * @param imagenUrl URL de la imagen
     */
    public void setImagenUrl(String imagenUrl) {
        this.imagenUrl = imagenUrl;
    }

    /**
     * Obtiene el ID de la categoría del producto.
     *
     * @return ID de la categoría
     */
    public Long getCategoriaId() {
        return categoriaId;
    }

    /**
     * Establece el ID de la categoría del producto.
     *
     * @param categoriaId ID de la categoría
     */
    public void setCategoriaId(Long categoriaId) {
        this.categoriaId = categoriaId;
    }

    /**
     * Obtiene el código de identificación del producto.
     *
     * @return código del producto
     */
    public String getCodigoProducto() {
        return codigoProducto;
    }

    /**
     * Establece el código de identificación del producto.
     *
     * @param codigoProducto código del producto
     */
    public void setCodigoProducto(String codigoProducto) {
        this.codigoProducto = codigoProducto;
    }

    /**
     * Obtiene el nombre del equipo asociado al producto.
     *
     * @return nombre del equipo
     */
    public String getEquipo() {
        return equipo;
    }

    /**
     * Establece el nombre del equipo asociado al producto.
     *
     * @param equipo nombre del equipo
     */
    public void setEquipo(String equipo) {
        this.equipo = equipo;
    }

    /**
     * Obtiene el código de bandera del país.
     *
     * @return código de bandera
     */
    public String getBandera() {
        return bandera;
    }

    /**
     * Establece el código de bandera del país.
     *
     * @param bandera código de bandera
     */
    public void setBandera(String bandera) {
        this.bandera = bandera;
    }

    /**
     * Indica si el producto está marcado como destacado.
     *
     * @return {@code true} si es destacado, {@code false} en caso contrario
     */
    public Boolean getDestacado() {
        return destacado;
    }

    /**
     * Establece si el producto debe ser marcado como destacado.
     *
     * @param destacado {@code true} para marcar como destacado
     */
    public void setDestacado(Boolean destacado) {
        this.destacado = destacado;
    }

    /**
     * Obtiene la lista de variantes del producto.
     *
     * @return lista de variantes
     */
    public List<VarianteRequestDTO> getVariantes() {
        return variantes;
    }

    /**
     * Establece la lista de variantes del producto.
     *
     * @param variantes lista de variantes
     */
    public void setVariantes(List<VarianteRequestDTO> variantes) {
        this.variantes = variantes;
    }
}