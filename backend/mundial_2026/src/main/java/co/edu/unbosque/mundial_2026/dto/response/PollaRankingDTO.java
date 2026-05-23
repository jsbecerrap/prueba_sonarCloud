package co.edu.unbosque.mundial_2026.dto.response;

/**
 * DTO de respuesta para el ranking de pollas
 * Contiene informacion de participantes y estado de la apuesta
 */
public class PollaRankingDTO {

    /**
     * Identificador de la apuesta
     */
    private Long apuestaId;

    /**
     * Nombre de la apuesta
     */
    private String nombre;

    /**
     * Estado de la apuesta
     */
    private String estado;

    /**
     * Total de participantes
     */
    private int totalParticipantes;

    /**
     * Constructor vacio de la clase
     */
    public PollaRankingDTO() {
    }

    /**
     * Constructor con todos los atributos de la clase
     *
     * @param apuestaId identificador de la apuesta
     * @param nombre nombre de la apuesta
     * @param estado estado de la apuesta
     * @param totalParticipantes total de participantes
     */
    public PollaRankingDTO(Long apuestaId, String nombre, String estado, int totalParticipantes) {
        this.apuestaId = apuestaId;
        this.nombre = nombre;
        this.estado = estado;
        this.totalParticipantes = totalParticipantes;
    }

    /**
     * Obtiene el identificador de la apuesta
     *
     * @return identificador de la apuesta
     */
    public Long getApuestaId() {
        return apuestaId;
    }

    /**
     * Establece el identificador de la apuesta
     *
     * @param apuestaId identificador de la apuesta
     */
    public void setApuestaId(Long apuestaId) {
        this.apuestaId = apuestaId;
    }

    /**
     * Obtiene el nombre de la apuesta
     *
     * @return nombre de la apuesta
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Establece el nombre de la apuesta
     *
     * @param nombre nombre de la apuesta
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Obtiene el estado de la apuesta
     *
     * @return estado de la apuesta
     */
    public String getEstado() {
        return estado;
    }

    /**
     * Establece el estado de la apuesta
     *
     * @param estado estado de la apuesta
     */
    public void setEstado(String estado) {
        this.estado = estado;
    }

    /**
     * Obtiene el total de participantes
     *
     * @return total de participantes
     */
    public int getTotalParticipantes() {
        return totalParticipantes;
    }

    /**
     * Establece el total de participantes
     *
     * @param totalParticipantes total de participantes
     */
    public void setTotalParticipantes(int totalParticipantes) {
        this.totalParticipantes = totalParticipantes;
    }
}