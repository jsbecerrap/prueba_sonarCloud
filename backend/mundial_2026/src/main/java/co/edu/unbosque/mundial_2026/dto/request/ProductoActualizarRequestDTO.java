package co.edu.unbosque.mundial_2026.dto.request;

import java.util.List;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

/**
 * DTO de solicitud para actualizar los datos de un producto existente.
 * <p>
 * Todos los campos son opcionales; solo se actualizarán aquellos que se envíen
 * con un valor no nulo. Incluye validaciones de formato y rango.
 * </p>
 */
public class ProductoActualizarRequestDTO {

    /**
     * Nuevo precio del producto. No puede ser negativo y no puede superar 99.999.999,99.
     */
    @PositiveOrZero(message = "El precio no puede ser negativo")
    @DecimalMax(value = "99999999.99", message = "El precio supera el máximo permitido")
    private Double precio;

    /**
     * Nueva URL de la imagen del producto. Debe ser una URL HTTPS válida
     * con máximo 500 caracteres.
     */
    @Size(max = 500, message = "La URL de imagen no puede tener más de 500 caracteres")
    @Pattern(
        regexp = "^https://[a-zA-Z0-9._/?&=#%~+\\-:,]+$",
        message = "La URL de la imagen debe ser HTTPS y tener un formato válido"
    )
    private String imagenUrl;

    /**
     * Nueva descripción del producto. Máximo 500 caracteres y solo puede
     * contener caracteres alfanuméricos y puntuación básica.
     */
    @Size(max = 500, message = "La descripción no puede tener más de 500 caracteres")
    @Pattern(
        regexp = "^[A-Za-zÁÉÍÓÚáéíóúÑñ0-9\\s_.,;:()\\-]*$",
        message = "La descripción contiene caracteres no permitidos"
    )
    private String descripcion;

    /**
     * Nuevo código de identificación del producto. Máximo 50 caracteres,
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

    /** Indica si el producto debe ser marcado como destacado en el catálogo. */
    private Boolean destacado;
    /**
 * Lista de variantes a actualizar. Cada elemento contiene el id de la variante
 * junto con los campos a modificar: especificación y/o stock.
 * Si es nulo o vacío, no se modifican las variantes del producto.
 */
private List<VarianteActualizarDTO> variantes;

/**
 * Retorna la lista de variantes a actualizar.
 *
 * @return lista de {@link VarianteActualizarDTO}
 */
public List<VarianteActualizarDTO> getVariantes() { return variantes; }

/**
 * Establece la lista de variantes a actualizar.
 *
 * @param variantes lista de variantes con sus nuevos valores
 */
public void setVariantes(List<VarianteActualizarDTO> variantes) { this.variantes = variantes; }
    /**
     * Obtiene el nuevo precio del producto.
     *
     * @return precio del producto
     */
    public Double getPrecio() {
        return precio;
    }

    /**
     * Establece el nuevo precio del producto.
     *
     * @param precio precio del producto
     */
    public void setPrecio(Double precio) {
        this.precio = precio;
    }

    /**
     * Obtiene la nueva URL de la imagen del producto.
     *
     * @return URL de la imagen
     */
    public String getImagenUrl() {
        return imagenUrl;
    }

    /**
     * Establece la nueva URL de la imagen del producto.
     *
     * @param imagenUrl URL de la imagen
     */
    public void setImagenUrl(String imagenUrl) {
        this.imagenUrl = imagenUrl;
    }

    /**
     * Obtiene la nueva descripción del producto.
     *
     * @return descripción del producto
     */
    public String getDescripcion() {
        return descripcion;
    }

    /**
     * Establece la nueva descripción del producto.
     *
     * @param descripcion descripción del producto
     */
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    /**
     * Obtiene el nuevo código de identificación del producto.
     *
     * @return código del producto
     */
    public String getCodigoProducto() {
        return codigoProducto;
    }

    /**
     * Establece el nuevo código de identificación del producto.
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
     * Indica si el producto debe ser marcado como destacado.
     *
     * @return {@code true} si debe ser destacado, {@code false} en caso contrario
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
}