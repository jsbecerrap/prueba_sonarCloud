package co.edu.unbosque.mundial_2026.dto.response;

/**
 * DTO de respuesta para las entradas vendidas por partido
 * Contiene informacion del partido y las ventas realizadas
 */
public class EntradaPorPartidoDTO {

    /**
     * Identificador del partido
     */
    private Long partidoId;

    /**
     * Nombre del equipo local
     */
    private String local;

    /**
     * Nombre del equipo visitante
     */
    private String visitante;

    /**
     * Ronda del partido
     */
    private String ronda;

    /**
     * Nombre del estadio
     */
    private String estadio;

    /**
     * Cantidad de entradas vendidas
     */
    private int cantidadVendida;

    /**
     * Ingreso total generado por las ventas
     */
    private double ingresoTotal;

    /**
     * Constructor vacio de la clase
     */
    public EntradaPorPartidoDTO() {
    }

    /**
     * Constructor con todos los atributos de la clase
     *
     * @param partidoId identificador del partido
     * @param local nombre del equipo local
     * @param visitante nombre del equipo visitante
     * @param ronda ronda del partido
     * @param estadio nombre del estadio
     * @param cantidadVendida cantidad de entradas vendidas
     * @param ingresoTotal ingreso total generado
     */
    public EntradaPorPartidoDTO(Long partidoId, String local, String visitante, String ronda, String estadio,
            int cantidadVendida, double ingresoTotal) {
        this.partidoId = partidoId;
        this.local = local;
        this.visitante = visitante;
        this.ronda = ronda;
        this.estadio = estadio;
        this.cantidadVendida = cantidadVendida;
        this.ingresoTotal = ingresoTotal;
    }

    /**
     * Obtiene el identificador del partido
     *
     * @return identificador del partido
     */
    public Long getPartidoId() {
        return partidoId;
    }

    /**
     * Establece el identificador del partido
     *
     * @param partidoId identificador del partido
     */
    public void setPartidoId(Long partidoId) {
        this.partidoId = partidoId;
    }

    /**
     * Obtiene el nombre del equipo local
     *
     * @return nombre del equipo local
     */
    public String getLocal() {
        return local;
    }

    /**
     * Establece el nombre del equipo local
     *
     * @param local nombre del equipo local
     */
    public void setLocal(String local) {
        this.local = local;
    }

    /**
     * Obtiene el nombre del equipo visitante
     *
     * @return nombre del equipo visitante
     */
    public String getVisitante() {
        return visitante;
    }

    /**
     * Establece el nombre del equipo visitante
     *
     * @param visitante nombre del equipo visitante
     */
    public void setVisitante(String visitante) {
        this.visitante = visitante;
    }

    /**
     * Obtiene la ronda del partido
     *
     * @return ronda del partido
     */
    public String getRonda() {
        return ronda;
    }

    /**
     * Establece la ronda del partido
     *
     * @param ronda ronda del partido
     */
    public void setRonda(String ronda) {
        this.ronda = ronda;
    }

    /**
     * Obtiene el nombre del estadio
     *
     * @return nombre del estadio
     */
    public String getEstadio() {
        return estadio;
    }

    /**
     * Establece el nombre del estadio
     *
     * @param estadio nombre del estadio
     */
    public void setEstadio(String estadio) {
        this.estadio = estadio;
    }

    /**
     * Obtiene la cantidad de entradas vendidas
     *
     * @return cantidad de entradas vendidas
     */
    public int getCantidadVendida() {
        return cantidadVendida;
    }

    /**
     * Establece la cantidad de entradas vendidas
     *
     * @param cantidadVendida cantidad de entradas vendidas
     */
    public void setCantidadVendida(int cantidadVendida) {
        this.cantidadVendida = cantidadVendida;
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