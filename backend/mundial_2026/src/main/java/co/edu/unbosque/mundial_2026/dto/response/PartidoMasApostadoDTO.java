package co.edu.unbosque.mundial_2026.dto.response;

/**
 * DTO de respuesta para los partidos mas apostados
 * Contiene informacion del partido y el total de pronosticos
 */
public class PartidoMasApostadoDTO {

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
     * Total de pronosticos realizados
     */
    private int totalPronosticos;

    /**
     * Constructor vacio de la clase
     */
    public PartidoMasApostadoDTO() {
    }

    /**
     * Constructor con todos los atributos de la clase
     *
     * @param partidoId identificador del partido
     * @param local nombre del equipo local
     * @param visitante nombre del equipo visitante
     * @param ronda ronda del partido
     * @param totalPronosticos total de pronosticos realizados
     */
    public PartidoMasApostadoDTO(Long partidoId, String local, String visitante, String ronda, int totalPronosticos) {
        this.partidoId = partidoId;
        this.local = local;
        this.visitante = visitante;
        this.ronda = ronda;
        this.totalPronosticos = totalPronosticos;
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
     * Obtiene el total de pronosticos realizados
     *
     * @return total de pronosticos realizados
     */
    public int getTotalPronosticos() {
        return totalPronosticos;
    }

    /**
     * Establece el total de pronosticos realizados
     *
     * @param totalPronosticos total de pronosticos realizados
     */
    public void setTotalPronosticos(int totalPronosticos) {
        this.totalPronosticos = totalPronosticos;
    }
}