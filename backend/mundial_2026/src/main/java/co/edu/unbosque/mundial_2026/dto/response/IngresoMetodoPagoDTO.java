package co.edu.unbosque.mundial_2026.dto.response;

/**
 * DTO de respuesta para los ingresos por metodo de pago
 * Contiene informacion de ordenes e ingresos generados
 */
public class IngresoMetodoPagoDTO {

    /**
     * Tipo de metodo de pago
     */
    private String tipo;

    /**
     * Total de ordenes realizadas
     */
    private int totalOrdenes;

    /**
     * Ingreso total generado
     */
    private double ingresoTotal;

    /**
     * Constructor vacio de la clase
     */
    public IngresoMetodoPagoDTO() {
    }

    /**
     * Constructor con todos los atributos de la clase
     *
     * @param tipo tipo de metodo de pago
     * @param totalOrdenes total de ordenes realizadas
     * @param ingresoTotal ingreso total generado
     */
    public IngresoMetodoPagoDTO(String tipo, int totalOrdenes, double ingresoTotal) {
        this.tipo = tipo;
        this.totalOrdenes = totalOrdenes;
        this.ingresoTotal = ingresoTotal;
    }

    /**
     * Obtiene el tipo de metodo de pago
     *
     * @return tipo de metodo de pago
     */
    public String getTipo() {
        return tipo;
    }

    /**
     * Establece el tipo de metodo de pago
     *
     * @param tipo tipo de metodo de pago
     */
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    /**
     * Obtiene el total de ordenes realizadas
     *
     * @return total de ordenes realizadas
     */
    public int getTotalOrdenes() {
        return totalOrdenes;
    }

    /**
     * Establece el total de ordenes realizadas
     *
     * @param totalOrdenes total de ordenes realizadas
     */
    public void setTotalOrdenes(int totalOrdenes) {
        this.totalOrdenes = totalOrdenes;
    }

    /**
     * Obtiene el ingreso total generado
     *
     * @return ingreso total generado
     */
    public double getIngresoTotal() {
        return ingresoTotal;
    }

    /**
     * Establece el ingreso total generado
     *
     * @param ingresoTotal ingreso total generado
     */
    public void setIngresoTotal(double ingresoTotal) {
        this.ingresoTotal = ingresoTotal;
    }
}