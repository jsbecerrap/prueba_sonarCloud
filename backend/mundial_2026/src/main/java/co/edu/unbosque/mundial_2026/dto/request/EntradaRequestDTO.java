package co.edu.unbosque.mundial_2026.dto.request;

/**
 * DTO de solicitud para comprar entradas a un partido del Mundial.
 * <p>
 * Especifica el partido al que se desea asistir, la cantidad de entradas,
 * la categoría de precio y el sector del estadio.
 * </p>
 */
public class EntradaRequestDTO {

    /** Identificador del partido para el cual se compran las entradas. */
    private Long partidoId;

    /** Número de entradas que el usuario desea adquirir. */
    private Integer cantidad;

    /** Categoría de la entrada según el precio (por ejemplo: VIP, PREFERENCIAL, GENERAL). */
    private String categoria;

    /** Sector del estadio donde se ubicarán los asientos (por ejemplo: NORTE, SUR, ORIENTAL). */
    private String sector;

    /**
     * Constructor vacío requerido por frameworks de serialización.
     */
    public EntradaRequestDTO() {
        //Constructor vacio
    }

    /**
     * Obtiene el identificador del partido.
     *
     * @return ID del partido
     */
    public Long getPartidoId() {
        return partidoId;
    }

    /**
     * Establece el identificador del partido.
     *
     * @param partidoId ID del partido
     */
    public void setPartidoId(Long partidoId) {
        this.partidoId = partidoId;
    }

    /**
     * Obtiene la cantidad de entradas solicitadas.
     *
     * @return número de entradas
     */
    public Integer getCantidad() {
        return cantidad;
    }

    /**
     * Establece la cantidad de entradas solicitadas.
     *
     * @param cantidad número de entradas
     */
    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    /**
     * Obtiene la categoría de la entrada.
     *
     * @return categoría de la entrada
     */
    public String getCategoria() {
        return categoria;
    }

    /**
     * Establece la categoría de la entrada.
     *
     * @param categoria categoría de la entrada
     */
    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    /**
     * Obtiene el sector del estadio.
     *
     * @return sector del estadio
     */
    public String getSector() {
        return sector;
    }

    /**
     * Establece el sector del estadio.
     *
     * @param sector sector del estadio
     */
    public void setSector(String sector) {
        this.sector = sector;
    }
}