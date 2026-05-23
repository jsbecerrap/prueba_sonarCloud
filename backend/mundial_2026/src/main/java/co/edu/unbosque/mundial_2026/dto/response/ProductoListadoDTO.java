package co.edu.unbosque.mundial_2026.dto.response;

/**
 * DTO de respuesta para el listado de productos
 * Contiene informacion general del producto
 */
public class ProductoListadoDTO {

    /**
     * Identificador del producto
     */
    private Long id;

    /**
     * Nombre del producto
     */
    private String nombre;

    /**
     * Descripcion del producto
     */
    private String descripcion;

    /**
     * Precio del producto
     */
    private Double precio;

    /**
     * URL de la imagen del producto
     */
    private String imagenUrl;

    /**
     * Nombre de la categoria del producto
     */
    private String categoriaNombre;

    /**
     * Nombre del equipo asociado
     */
    private String equipo;

    /**
     * Bandera del equipo asociado
     */
    private String bandera;

    /**
     * Indica si el producto es destacado
     */
    private Boolean destacado;

    /**
     * Stock total del producto
     */
    private Integer stockTotal;

    /**
     * Indica si el producto tiene variantes
     */
    private Boolean tieneVariantes;

    /**
     * Constructor vacio de la clase
     */
    public ProductoListadoDTO() {
    }

    /**
     * Constructor con todos los atributos de la clase
     *
     * @param id identificador del producto
     * @param nombre nombre del producto
     * @param descripcion descripcion del producto
     * @param precio precio del producto
     * @param imagenUrl URL de la imagen del producto
     * @param categoriaNombre nombre de la categoria
     * @param equipo nombre del equipo asociado
     * @param bandera bandera del equipo asociado
     * @param destacado estado destacado del producto
     * @param stockTotal stock total del producto
     * @param cantidadVariantes cantidad de variantes del producto
     */
    public ProductoListadoDTO(Long id, String nombre, String descripcion,
            Double precio, String imagenUrl, String categoriaNombre,
            String equipo, String bandera, Boolean destacado,
            Long stockTotal, Long cantidadVariantes) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.imagenUrl = imagenUrl;
        this.categoriaNombre = categoriaNombre;
        this.equipo = equipo;
        this.bandera = bandera;
        this.destacado = destacado;
        this.stockTotal = stockTotal != null ? stockTotal.intValue() : 0;
        this.tieneVariantes = cantidadVariantes != null && cantidadVariantes > 1;
    }

    /**
     * Obtiene el identificador del producto
     *
     * @return identificador del producto
     */
    public Long getId() {
        return id;
    }

    /**
     * Establece el identificador del producto
     *
     * @param id identificador del producto
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Obtiene el nombre del producto
     *
     * @return nombre del producto
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Establece el nombre del producto
     *
     * @param nombre nombre del producto
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Obtiene la descripcion del producto
     *
     * @return descripcion del producto
     */
    public String getDescripcion() {
        return descripcion;
    }

    /**
     * Establece la descripcion del producto
     *
     * @param descripcion descripcion del producto
     */
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    /**
     * Obtiene el precio del producto
     *
     * @return precio del producto
     */
    public Double getPrecio() {
        return precio;
    }

    /**
     * Establece el precio del producto
     *
     * @param precio precio del producto
     */
    public void setPrecio(Double precio) {
        this.precio = precio;
    }

    /**
     * Obtiene la URL de la imagen del producto
     *
     * @return URL de la imagen del producto
     */
    public String getImagenUrl() {
        return imagenUrl;
    }

    /**
     * Establece la URL de la imagen del producto
     *
     * @param imagenUrl URL de la imagen del producto
     */
    public void setImagenUrl(String imagenUrl) {
        this.imagenUrl = imagenUrl;
    }

    /**
     * Obtiene el nombre de la categoria
     *
     * @return nombre de la categoria
     */
    public String getCategoriaNombre() {
        return categoriaNombre;
    }

    /**
     * Establece el nombre de la categoria
     *
     * @param categoriaNombre nombre de la categoria
     */
    public void setCategoriaNombre(String categoriaNombre) {
        this.categoriaNombre = categoriaNombre;
    }

    /**
     * Obtiene el nombre del equipo asociado
     *
     * @return nombre del equipo asociado
     */
    public String getEquipo() {
        return equipo;
    }

    /**
     * Establece el nombre del equipo asociado
     *
     * @param equipo nombre del equipo asociado
     */
    public void setEquipo(String equipo) {
        this.equipo = equipo;
    }

    /**
     * Obtiene la bandera del equipo asociado
     *
     * @return bandera del equipo asociado
     */
    public String getBandera() {
        return bandera;
    }

    /**
     * Establece la bandera del equipo asociado
     *
     * @param bandera bandera del equipo asociado
     */
    public void setBandera(String bandera) {
        this.bandera = bandera;
    }

    /**
     * Obtiene el estado destacado del producto
     *
     * @return estado destacado del producto
     */
    public Boolean getDestacado() {
        return destacado;
    }

    /**
     * Establece el estado destacado del producto
     *
     * @param destacado estado destacado del producto
     */
    public void setDestacado(Boolean destacado) {
        this.destacado = destacado;
    }

    /**
     * Obtiene el stock total del producto
     *
     * @return stock total del producto
     */
    public Integer getStockTotal() {
        return stockTotal;
    }

    /**
     * Establece el stock total del producto
     *
     * @param stockTotal stock total del producto
     */
    public void setStockTotal(Integer stockTotal) {
        this.stockTotal = stockTotal;
    }

    /**
     * Obtiene si el producto tiene variantes
     *
     * @return estado de variantes del producto
     */
    public Boolean getTieneVariantes() {
        return tieneVariantes;
    }

    /**
     * Establece si el producto tiene variantes
     *
     * @param tieneVariantes estado de variantes del producto
     */
    public void setTieneVariantes(Boolean tieneVariantes) {
        this.tieneVariantes = tieneVariantes;
    }
}