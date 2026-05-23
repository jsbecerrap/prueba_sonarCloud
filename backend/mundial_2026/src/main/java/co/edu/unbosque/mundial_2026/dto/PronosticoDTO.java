package co.edu.unbosque.mundial_2026.dto;

/**
 * DTO que representa el pronóstico realizado por un usuario para un partido
 * dentro de una apuesta.
 * <p>
 * Contiene el resultado esperado, los goles pronosticados y los puntos
 * obtenidos una vez que el partido ha sido disputado.
 * </p>
 */
public class PronosticoDTO {

    /** Identificador único del pronóstico. */
    private Long id;

    /** Resultado pronosticado por el usuario (LOCAL, VISITANTE o EMPATE). */
    private String resultadoPronosticado;

    /** Número de goles pronosticados para el equipo local. */
    private Integer golesLocalPronosticados;

    /** Número de goles pronosticados para el equipo visitante. */
    private Integer golesVisitantePronosticados;

    /** Puntos obtenidos por el usuario tras verificar el resultado real del partido. */
    private Integer puntosObtenidos;

    /** Identificador del usuario que realizó el pronóstico. */
    private Long usuarioId;

    /** Identificador de la apuesta a la que pertenece este pronóstico. */
    private Long apuestaId;

    /** Identificador del partido sobre el cual se realizó el pronóstico. */
    private Long partidoId;

    /**
     * Constructor vacío requerido por frameworks de serialización.
     */
    public PronosticoDTO() {
    }

    /**
     * Constructor completo para inicializar todos los campos del pronóstico.
     *
     * @param id                          identificador único del pronóstico
     * @param resultadoPronosticado       resultado esperado (LOCAL, VISITANTE o EMPATE)
     * @param golesLocalPronosticados     goles esperados del equipo local
     * @param golesVisitantePronosticados goles esperados del equipo visitante
     * @param puntosObtenidos             puntos obtenidos tras el resultado real
     * @param usuarioId                   ID del usuario que realizó el pronóstico
     * @param apuestaId                   ID de la apuesta asociada
     * @param partidoId                   ID del partido pronosticado
     */
    public PronosticoDTO(Long id, String resultadoPronosticado, Integer golesLocalPronosticados,
            Integer golesVisitantePronosticados, Integer puntosObtenidos,
            Long usuarioId, Long apuestaId, Long partidoId) {
        this.id = id;
        this.resultadoPronosticado = resultadoPronosticado;
        this.golesLocalPronosticados = golesLocalPronosticados;
        this.golesVisitantePronosticados = golesVisitantePronosticados;
        this.puntosObtenidos = puntosObtenidos;
        this.usuarioId = usuarioId;
        this.apuestaId = apuestaId;
        this.partidoId = partidoId;
    }

    /**
     * Obtiene el identificador único del pronóstico.
     *
     * @return ID del pronóstico
     */
    public Long getId() {
        return id;
    }

    /**
     * Establece el identificador único del pronóstico.
     *
     * @param id ID del pronóstico
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Obtiene el resultado pronosticado por el usuario.
     *
     * @return resultado pronosticado (LOCAL, VISITANTE o EMPATE)
     */
    public String getResultadoPronosticado() {
        return resultadoPronosticado;
    }

    /**
     * Establece el resultado pronosticado por el usuario.
     *
     * @param r resultado pronosticado
     */
    public void setResultadoPronosticado(String r) {
        this.resultadoPronosticado = r;
    }

    /**
     * Obtiene el número de goles pronosticados para el equipo local.
     *
     * @return goles pronosticados del local
     */
    public Integer getGolesLocalPronosticados() {
        return golesLocalPronosticados;
    }

    /**
     * Establece el número de goles pronosticados para el equipo local.
     *
     * @param g goles pronosticados del local
     */
    public void setGolesLocalPronosticados(Integer g) {
        this.golesLocalPronosticados = g;
    }

    /**
     * Obtiene el número de goles pronosticados para el equipo visitante.
     *
     * @return goles pronosticados del visitante
     */
    public Integer getGolesVisitantePronosticados() {
        return golesVisitantePronosticados;
    }

    /**
     * Establece el número de goles pronosticados para el equipo visitante.
     *
     * @param g goles pronosticados del visitante
     */
    public void setGolesVisitantePronosticados(Integer g) {
        this.golesVisitantePronosticados = g;
    }

    /**
     * Obtiene los puntos obtenidos por el usuario en este pronóstico.
     *
     * @return puntos obtenidos
     */
    public Integer getPuntosObtenidos() {
        return puntosObtenidos;
    }

    /**
     * Establece los puntos obtenidos por el usuario en este pronóstico.
     *
     * @param p puntos obtenidos
     */
    public void setPuntosObtenidos(Integer p) {
        this.puntosObtenidos = p;
    }

    /**
     * Obtiene el ID del usuario que realizó el pronóstico.
     *
     * @return ID del usuario
     */
    public Long getUsuarioId() {
        return usuarioId;
    }

    /**
     * Establece el ID del usuario que realizó el pronóstico.
     *
     * @param usuarioId ID del usuario
     */
    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    /**
     * Obtiene el ID de la apuesta asociada al pronóstico.
     *
     * @return ID de la apuesta
     */
    public Long getApuestaId() {
        return apuestaId;
    }

    /**
     * Establece el ID de la apuesta asociada al pronóstico.
     *
     * @param apuestaId ID de la apuesta
     */
    public void setApuestaId(Long apuestaId) {
        this.apuestaId = apuestaId;
    }

    /**
     * Obtiene el ID del partido pronosticado.
     *
     * @return ID del partido
     */
    public Long getPartidoId() {
        return partidoId;
    }

    /**
     * Establece el ID del partido pronosticado.
     *
     * @param partidoId ID del partido
     */
    public void setPartidoId(Long partidoId) {
        this.partidoId = partidoId;
    }
}