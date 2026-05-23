package co.edu.unbosque.mundial_2026.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * DTO de solicitud para registrar o actualizar el pronóstico de un usuario
 * sobre el resultado de un partido dentro de una apuesta.
 * <p>
 * Incluye el resultado esperado, los goles pronosticados para cada equipo
 * y las referencias al usuario, apuesta y partido correspondientes.
 * </p>
 */
public class PronosticoRequestDTO {

    /**
     * Resultado pronosticado por el usuario. Es obligatorio y debe ser
     * uno de los valores: LOCAL, VISITANTE o EMPATE.
     */
    @NotBlank(message = "El resultado pronosticado es obligatorio")
    @Pattern(
        regexp = "LOCAL|VISITANTE|EMPATE",
        message = "El resultado debe ser LOCAL, VISITANTE o EMPATE"
    )
    private String resultadoPronosticado;

    /**
     * Número de goles pronosticados para el equipo local.
     * Debe estar entre 0 y 20.
     */
    @Min(value = 0, message = "Los goles no pueden ser negativos")
    @Max(value = 20, message = "Los goles no pueden ser mayores a 20")
    private Integer golesLocalPronosticados;

    /**
     * Número de goles pronosticados para el equipo visitante.
     * Debe estar entre 0 y 20.
     */
    @Min(value = 0, message = "Los goles no pueden ser negativos")
    @Max(value = 20, message = "Los goles no pueden ser mayores a 20")
    private Integer golesVisitantePronosticados;

    /** Identificador del usuario que realiza el pronóstico. */
    private Long usuarioId;

    /** Identificador de la apuesta a la que pertenece el pronóstico. */
    private Long apuestaId;

    /** Identificador del partido sobre el que se realiza el pronóstico. */
    private Long partidoId;

    /**
     * Obtiene el resultado pronosticado.
     *
     * @return resultado pronosticado (LOCAL, VISITANTE o EMPATE)
     */
    public String getResultadoPronosticado() {
        return resultadoPronosticado;
    }

    /**
     * Establece el resultado pronosticado.
     *
     * @param r resultado pronosticado (LOCAL, VISITANTE o EMPATE)
     */
    public void setResultadoPronosticado(String r) {
        this.resultadoPronosticado = r;
    }

    /**
     * Obtiene los goles pronosticados para el equipo local.
     *
     * @return goles pronosticados del local
     */
    public Integer getGolesLocalPronosticados() {
        return golesLocalPronosticados;
    }

    /**
     * Establece los goles pronosticados para el equipo local.
     *
     * @param g goles pronosticados del local
     */
    public void setGolesLocalPronosticados(Integer g) {
        this.golesLocalPronosticados = g;
    }

    /**
     * Obtiene los goles pronosticados para el equipo visitante.
     *
     * @return goles pronosticados del visitante
     */
    public Integer getGolesVisitantePronosticados() {
        return golesVisitantePronosticados;
    }

    /**
     * Establece los goles pronosticados para el equipo visitante.
     *
     * @param g goles pronosticados del visitante
     */
    public void setGolesVisitantePronosticados(Integer g) {
        this.golesVisitantePronosticados = g;
    }

    /**
     * Obtiene el ID del usuario que realiza el pronóstico.
     *
     * @return ID del usuario
     */
    public Long getUsuarioId() {
        return usuarioId;
    }

    /**
     * Establece el ID del usuario que realiza el pronóstico.
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