package co.edu.unbosque.mundial_2026.dto.response;

/**
 * DTO de respuesta para los items de una orden
 * Contiene informacion del producto y la compra realizada
 */
public class ItemOrdenResponseDTO {

    /**
     * Identificador del item
     */
    private Long id;

    /**
     * Identificador del producto
     */
    private Long productoId;

    /**
     * Identificador de la variante
     */
    private Long varianteId;

    /**
     * Nombre del producto
     */
    private String productoNombre;

    /**
     * URL de la imagen del producto
     */
    private String productoImagenUrl;

    /**
     * Especificacion de la variante
     */
    private String especificacion;

    /**
     * Cantidad comprada
     */
    private Integer cantidad;

    /**
     * Precio unitario del producto
     */
    private Double precioUnitario;

    /**
     * Subtotal del item
     */
    private Double subtotal;

    /**
     * Nombre de la categoria del producto
     */
    private String categoriaNombre;

    /**
     * Obtiene el identificador del item
     *
     * @return identificador del item
     */
    public Long getId() {
        return id;
    }

    /**
     * Establece el identificador del item
     *
     * @param id identificador del item
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Obtiene el identificador del producto
     *
     * @return identificador del producto
     */
    public Long getProductoId() {
        return productoId;
    }

    /**
     * Establece el identificador del producto
     *
     * @param productoId identificador del producto
     */
    public void setProductoId(Long productoId) {
        this.productoId = productoId;
    }

    /**
     * Obtiene el identificador de la variante
     *
     * @return identificador de la variante
     */
    public Long getVarianteId() {
        return varianteId;
    }

    /**
     * Establece el identificador de la variante
     *
     * @param varianteId identificador de la variante
     */
    public void setVarianteId(Long varianteId) {
        this.varianteId = varianteId;
    }

    /**
     * Obtiene el nombre del producto
     *
     * @return nombre del producto
     */
    public String getProductoNombre() {
        return productoNombre;
    }

    /**
     * Establece el nombre del producto
     *
     * @param productoNombre nombre del producto
     */
    public void setProductoNombre(String productoNombre) {
        this.productoNombre = productoNombre;
    }

    /**
     * Obtiene la URL de la imagen del producto
     *
     * @return URL de la imagen del producto
     */
    public String getProductoImagenUrl() {
        return productoImagenUrl;
    }

    /**
     * Establece la URL de la imagen del producto
     *
     * @param productoImagenUrl URL de la imagen del producto
     */
    public void setProductoImagenUrl(String productoImagenUrl) {
        this.productoImagenUrl = productoImagenUrl;
    }

    /**
     * Obtiene la especificacion de la variante
     *
     * @return especificacion de la variante
     */
    public String getEspecificacion() {
        return especificacion;
    }

    /**
     * Establece la especificacion de la variante
     *
     * @param especificacion especificacion de la variante
     */
    public void setEspecificacion(String especificacion) {
        this.especificacion = especificacion;
    }

    /**
     * Obtiene la cantidad comprada
     *
     * @return cantidad comprada
     */
    public Integer getCantidad() {
        return cantidad;
    }

    /**
     * Establece la cantidad comprada
     *
     * @param cantidad cantidad comprada
     */
    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    /**
     * Obtiene el precio unitario del producto
     *
     * @return precio unitario del producto
     */
    public Double getPrecioUnitario() {
        return precioUnitario;
    }

    /**
     * Establece el precio unitario del producto
     *
     * @param precioUnitario precio unitario del producto
     */
    public void setPrecioUnitario(Double precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    /**
     * Obtiene el subtotal del item
     *
     * @return subtotal del item
     */
    public Double getSubtotal() {
        return subtotal;
    }

    /**
     * Establece el subtotal del item
     *
     * @param subtotal subtotal del item
     */
    public void setSubtotal(Double subtotal) {
        this.subtotal = subtotal;
    }

    /**
     * Obtiene el nombre de la categoria del producto
     *
     * @return nombre de la categoria del producto
     */
    public String getCategoriaNombre() {
        return categoriaNombre;
    }

    /**
     * Establece el nombre de la categoria del producto
     *
     * @param categoriaNombre nombre de la categoria del producto
     */
    public void setCategoriaNombre(String categoriaNombre) {
        this.categoriaNombre = categoriaNombre;
    }
}