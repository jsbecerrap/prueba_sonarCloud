package co.edu.unbosque.mundial_2026.dto.response;

/**
 * DTO de respuesta para ventas por categoria
 * Contiene resumen de ventas agrupadas
 */
public class VentasPorCategoriaDTO {

    /**
     * Nombre de la categoria
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
    public VentasPorCategoriaDTO() {
    }

    /**
     * Constructor con todos los atributos de la clase
     *
     * @param categoria nombre de la categoria
     * @param cantidadVendida cantidad total vendida
     * @param ingresoTotal ingreso total generado
     */
    public VentasPorCategoriaDTO(String categoria, int cantidadVendida, double ingresoTotal) {
        this.categoria = categoria;
        this.cantidadVendida = cantidadVendida;
        this.ingresoTotal = ingresoTotal;
    }

    /**
     * Obtiene la categoria
     *
     * @return categoria
     */
    public String getCategoria() {
        return categoria;
    }

    /**
     * Establece la categoria
     *
     * @param categoria categoria
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