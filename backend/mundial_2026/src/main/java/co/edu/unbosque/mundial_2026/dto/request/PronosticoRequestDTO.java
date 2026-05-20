package co.edu.unbosque.mundial_2026.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class PronosticoRequestDTO {

    @NotBlank(message = "El resultado pronosticado es obligatorio")
    @Pattern(
        regexp = "LOCAL|VISITANTE|EMPATE",
        message = "El resultado debe ser LOCAL, VISITANTE o EMPATE"
    )
    private String resultadoPronosticado;

    @Min(value = 0, message = "Los goles no pueden ser negativos")
    @Max(value = 20, message = "Los goles no pueden ser mayores a 20")
    private Integer golesLocalPronosticados;

    @Min(value = 0, message = "Los goles no pueden ser negativos")
    @Max(value = 20, message = "Los goles no pueden ser mayores a 20")
    private Integer golesVisitantePronosticados;

    private Long usuarioId;

    private Long apuestaId;

    private Long partidoId;

    public String getResultadoPronosticado() {
        return resultadoPronosticado;
    }

    public void setResultadoPronosticado(String r) {
        this.resultadoPronosticado = r;
    }

    public Integer getGolesLocalPronosticados() {
        return golesLocalPronosticados;
    }

    public void setGolesLocalPronosticados(Integer g) {
        this.golesLocalPronosticados = g;
    }

    public Integer getGolesVisitantePronosticados() {
        return golesVisitantePronosticados;
    }

    public void setGolesVisitantePronosticados(Integer g) {
        this.golesVisitantePronosticados = g;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public Long getApuestaId() {
        return apuestaId;
    }

    public void setApuestaId(Long apuestaId) {
        this.apuestaId = apuestaId;
    }

    public Long getPartidoId() {
        return partidoId;
    }

    public void setPartidoId(Long partidoId) {
        this.partidoId = partidoId;
    }
}