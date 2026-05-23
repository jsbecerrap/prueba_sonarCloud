package co.edu.unbosque.mundial_2026.dto;

/**
 * DTO que representa un partido con su capacidad de entradas disponibles.
 * <p>
 * Se utiliza para mostrar al usuario qué partidos tienen cupos de entradas
 * disponibles y en qué estadio y ciudad se juegan.
 * </p>
 */
public class PartidoCapacidadDTO {

    /** Identificador único del partido. */
    private Long id;

    /** Nombre de la selección que juega como local. */
    private String local;

    /** Nombre de la selección que juega como visitante. */
    private String visitante;

    /** Nombre del estadio donde se disputará el partido. */
    private String estadio;

    /** Ciudad sede donde se encuentra el estadio. */
    private String ciudad;

    /** Número de entradas aún disponibles para este partido. */
    private Integer capacidadDisponible;

    /** Ronda o fase del torneo en la que se disputa el partido (por ejemplo: "Fase de Grupos", "Cuartos de Final"). */
    private String ronda;

    /**
     * Constructor vacío requerido por frameworks de serialización.
     */
    public PartidoCapacidadDTO() {
        //Constructor vacio
    }

    /**
     * Obtiene el identificador único del partido.
     *
     * @return ID del partido
     */
    public Long getId() {
        return id;
    }

    /**
     * Establece el identificador único del partido.
     *
     * @param id ID del partido
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Obtiene el nombre de la selección local.
     *
     * @return nombre del equipo local
     */
    public String getLocal() {
        return local;
    }

    /**
     * Establece el nombre de la selección local.
     *
     * @param local nombre del equipo local
     */
    public void setLocal(String local) {
        this.local = local;
    }

    /**
     * Obtiene el nombre de la selección visitante.
     *
     * @return nombre del equipo visitante
     */
    public String getVisitante() {
        return visitante;
    }

    /**
     * Establece el nombre de la selección visitante.
     *
     * @param visitante nombre del equipo visitante
     */
    public void setVisitante(String visitante) {
        this.visitante = visitante;
    }

    /**
     * Obtiene el nombre del estadio del partido.
     *
     * @return nombre del estadio
     */
    public String getEstadio() {
        return estadio;
    }

    /**
     * Establece el nombre del estadio del partido.
     *
     * @param estadio nombre del estadio
     */
    public void setEstadio(String estadio) {
        this.estadio = estadio;
    }

    /**
     * Obtiene la ciudad sede del partido.
     *
     * @return ciudad sede
     */
    public String getCiudad() {
        return ciudad;
    }

    /**
     * Establece la ciudad sede del partido.
     *
     * @param ciudad ciudad sede
     */
    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    /**
     * Obtiene la cantidad de entradas disponibles para este partido.
     *
     * @return entradas disponibles
     */
    public Integer getCapacidadDisponible() {
        return capacidadDisponible;
    }

    /**
     * Establece la cantidad de entradas disponibles para este partido.
     *
     * @param capacidadDisponible entradas disponibles
     */
    public void setCapacidadDisponible(Integer capacidadDisponible) {
        this.capacidadDisponible = capacidadDisponible;
    }

    /**
     * Obtiene la ronda o fase del torneo del partido.
     *
     * @return ronda del torneo
     */
    public String getRonda() {
        return ronda;
    }

    /**
     * Establece la ronda o fase del torneo del partido.
     *
     * @param ronda ronda del torneo
     */
    public void setRonda(String ronda) {
        this.ronda = ronda;
    }
}