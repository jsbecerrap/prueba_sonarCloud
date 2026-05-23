package co.edu.unbosque.mundial_2026.dto.response;

import java.util.List;

/**
 * DTO de respuesta para productos
 * Contiene informacion general del producto y sus variantes
 */
public class ProductoResponseDTO {

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
     * Estado activo del producto
     */
    private Boolean activo;

    /**
     * Nombre de la categoria del producto
     */
    private String categoriaNombre;

    /**
     * Codigo del producto
     */
    private String codigoProducto;

    /**
     * Equipo asociado al producto
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
     * Lista de variantes del producto
     */
    private List<VarianteResponseDTO> variantes;

    /**
     * Stock total del producto
     */
    private Integer stockTotal;

    /**
     * DTO de respuesta para las variantes del producto
     */
    public static class VarianteResponseDTO {

        /**
         * Identificador de la variante
         */
        private Long id;

        /**
         * Especificacion de la variante
         */
        private String especificacion;

        /**
         * Stock de la variante
         */
        private Integer stock;

        /**
         * Obtiene el id de la variante
         */
        public Long getId() {
            return id;
        }

        /**
         * Establece el id de la variante
         */
        public void setId(Long id) {
            this.id = id;
        }

        /**
         * Obtiene la especificacion de la variante
         */
        public String getEspecificacion() {
            return especificacion;
        }

        /**
         * Establece la especificacion de la variante
         */
        public void setEspecificacion(String especificacion) {
            this.especificacion = especificacion;
        }

        /**
         * Obtiene el stock de la variante
         */
        public Integer getStock() {
            return stock;
        }

        /**
         * Establece el stock de la variante
         */
        public void setStock(Integer stock) {
            this.stock = stock;
        }
    }

    /**
     * Obtiene el id del producto
     */
    public Long getId() { return id; }

    /**
     * Establece el id del producto
     */
    public void setId(Long id) { this.id = id; }

    /**
     * Obtiene el nombre del producto
     */
    public String getNombre() { return nombre; }

    /**
     * Establece el nombre del producto
     */
    public void setNombre(String nombre) { this.nombre = nombre; }

    /**
     * Obtiene la descripcion del producto
     */
    public String getDescripcion() { return descripcion; }

    /**
     * Establece la descripcion del producto
     */
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    /**
     * Obtiene el precio del producto
     */
    public Double getPrecio() { return precio; }

    /**
     * Establece el precio del producto
     */
    public void setPrecio(Double precio) { this.precio = precio; }

    /**
     * Obtiene la imagen del producto
     */
    public String getImagenUrl() { return imagenUrl; }

    /**
     * Establece la imagen del producto
     */
    public void setImagenUrl(String imagenUrl) { this.imagenUrl = imagenUrl; }

    /**
     * Obtiene el estado del producto
     */
    public Boolean getActivo() { return activo; }

    /**
     * Establece el estado del producto
     */
    public void setActivo(Boolean activo) { this.activo = activo; }

    /**
     * Obtiene la categoria del producto
     */
    public String getCategoriaNombre() { return categoriaNombre; }

    /**
     * Establece la categoria del producto
     */
    public void setCategoriaNombre(String categoriaNombre) { this.categoriaNombre = categoriaNombre; }

    /**
     * Obtiene el codigo del producto
     */
    public String getCodigoProducto() { return codigoProducto; }

    /**
     * Establece el codigo del producto
     */
    public void setCodigoProducto(String codigoProducto) { this.codigoProducto = codigoProducto; }

    /**
     * Obtiene el equipo asociado
     */
    public String getEquipo() { return equipo; }

    /**
     * Establece el equipo asociado
     */
    public void setEquipo(String equipo) { this.equipo = equipo; }

    /**
     * Obtiene la bandera del equipo
     */
    public String getBandera() { return bandera; }

    /**
     * Establece la bandera del equipo
     */
    public void setBandera(String bandera) { this.bandera = bandera; }

    /**
     * Obtiene si es destacado
     */
    public Boolean getDestacado() { return destacado; }

    /**
     * Establece si es destacado
     */
    public void setDestacado(Boolean destacado) { this.destacado = destacado; }

    /**
     * Obtiene las variantes
     */
    public List<VarianteResponseDTO> getVariantes() { return variantes; }

    /**
     * Establece las variantes
     */
    public void setVariantes(List<VarianteResponseDTO> variantes) { this.variantes = variantes; }

    /**
     * Obtiene el stock total
     */
    public Integer getStockTotal() { return stockTotal; }

    /**
     * Establece el stock total
     */
    public void setStockTotal(Integer stockTotal) { this.stockTotal = stockTotal; }
}