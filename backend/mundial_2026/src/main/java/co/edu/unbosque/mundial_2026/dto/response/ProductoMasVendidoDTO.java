package co.edu.unbosque.mundial_2026.dto.response;

/**
 * DTO de respuesta para productos mas vendidos
 * Contiene informacion de ventas e ingresos por producto
 */
public class ProductoMasVendidoDTO {

    /**
     * Identificador del producto
     */
    private Long productoId;

    /**
     * Nombre del producto
     */
    private String nombre;

    /**
     * Categoria del producto
     */
    private String categoria;

    /**
     * Cantidad total vendida
     */
    private int cantidadVendida;

    /**
     * Ingreso total generado
     */
    private double ingresoTotal;

    /**
     * Constructor vacio de la clase
     */
    public ProductoMasVendidoDTO() {
    }

    /**
     * Constructor con todos los atributos de la clase
     *
     * @param productoId identificador del producto
     * @param nombre nombre del producto
     * @param categoria categoria del producto
     * @param cantidadVendida cantidad total vendida
     * @param ingresoTotal ingreso total generado
     */
    public ProductoMasVendidoDTO(Long productoId, String nombre, String categoria, int cantidadVendida,
            double ingresoTotal) {
        this.productoId = productoId;
        this.nombre = nombre;
        this.categoria = categoria;
        this.cantidadVendida = cantidadVendida;
        this.ingresoTotal = ingresoTotal;
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
     * Obtiene la categoria del producto
     *
     * @return categoria del producto
     */
    public String getCategoria() {
        return categoria;
    }

    /**
     * Establece la categoria del producto
     *
     * @param categoria categoria del producto
     */
    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    /**
     * Obtiene la cantidad vendida
     *
     * @return cantidad vendida
     */
    public int getCantidadVendida() {
        return cantidadVendida;
    }

    /**
     * Establece la cantidad vendida
     *
     * @param cantidadVendida cantidad vendida
     */
    public void setCantidadVendida(int cantidadVendida) {
        this.cantidadVendida = cantidadVendida;
    }

    /**
     * Obtiene el ingreso total
     *
     * @return ingreso total
     */
    public double getIngresoTotal() {
        return ingresoTotal;
    }

    /**
     * Establece el ingreso total
     *
     * @param ingresoTotal ingreso total
     */
    public void setIngresoTotal(double ingresoTotal) {
        this.ingresoTotal = ingresoTotal;
    }
}