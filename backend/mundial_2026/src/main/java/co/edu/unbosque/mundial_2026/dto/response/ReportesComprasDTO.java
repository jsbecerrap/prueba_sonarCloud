package co.edu.unbosque.mundial_2026.dto.response;

import java.util.List;

/**
 * DTO de respuesta para reportes de compras
 * Contiene informacion general de ingresos y ventas
 */
public class ReportesComprasDTO {

    /**
     * Ingreso total generado
     */
    private double ingresoTotal;

    /**
     * Total de ordenes realizadas
     */
    private int totalOrdenes;

    /**
     * Total de entradas vendidas
     */
    private long totalEntradasVendidas;

    /**
     * Lista de productos mas vendidos
     */
    private List<ProductoMasVendidoDTO> productosMasVendidos;

    /**
     * Lista de ventas por categoria
     */
    private List<VentasPorCategoriaDTO> ventasPorCategoria;

    /**
     * Constructor vacio de la clase
     */
    public ReportesComprasDTO() {
        //Constructor comentario que requiere sonar
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

    /**
     * Obtiene el total de ordenes
     *
     * @return total de ordenes
     */
    public int getTotalOrdenes() {
        return totalOrdenes;
    }

    /**
     * Establece el total de ordenes
     *
     * @param totalOrdenes total de ordenes
     */
    public void setTotalOrdenes(int totalOrdenes) {
        this.totalOrdenes = totalOrdenes;
    }

    /**
     * Obtiene el total de entradas vendidas
     *
     * @return total de entradas vendidas
     */
    public long getTotalEntradasVendidas() {
        return totalEntradasVendidas;
    }

    /**
     * Establece el total de entradas vendidas
     *
     * @param totalEntradasVendidas total de entradas vendidas
     */
    public void setTotalEntradasVendidas(long totalEntradasVendidas) {
        this.totalEntradasVendidas = totalEntradasVendidas;
    }

    /**
     * Obtiene los productos mas vendidos
     *
     * @return lista de productos mas vendidos
     */
    public List<ProductoMasVendidoDTO> getProductosMasVendidos() {
        return productosMasVendidos;
    }

    /**
     * Establece los productos mas vendidos
     *
     * @param productosMasVendidos lista de productos mas vendidos
     */
    public void setProductosMasVendidos(List<ProductoMasVendidoDTO> productosMasVendidos) {
        this.productosMasVendidos = productosMasVendidos;
    }

    /**
     * Obtiene las ventas por categoria
     *
     * @return lista de ventas por categoria
     */
    public List<VentasPorCategoriaDTO> getVentasPorCategoria() {
        return ventasPorCategoria;
    }

    /**
     * Establece las ventas por categoria
     *
     * @param ventasPorCategoria lista de ventas por categoria
     */
    public void setVentasPorCategoria(List<VentasPorCategoriaDTO> ventasPorCategoria) {
        this.ventasPorCategoria = ventasPorCategoria;
    }
}