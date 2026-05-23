package co.edu.unbosque.mundial_2026.dto.request;

import java.time.LocalDateTime;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * DTO de solicitud para crear una nueva apuesta (polla).
 * <p>
 * Incluye validaciones sobre el nombre, la fecha de cierre y el usuario creador
 * para garantizar la integridad de los datos recibidos.
 * </p>
 */
public class ApuestaRequestDTO {

    /**
     * Nombre de la apuesta. Es obligatorio, debe tener entre 4 y 50 caracteres
     * y solo puede contener letras, números, espacios, guiones bajos o guiones.
     */
    @NotBlank(message = "El nombre de la polla es obligatorio")
    @Size(min = 4, max = 50, message = "El nombre debe tener entre 4 y 50 caracteres")
    @Pattern(
        regexp = "^[A-Za-zÁÉÍÓÚáéíóúÑñ0-9\\s_-]+$",
        message = "El nombre solo puede tener letras, números, espacios, guiones bajos o guiones"
    )
    private String nombre;

    /**
     * Fecha y hora de cierre de la apuesta. Es obligatoria y debe ser una fecha futura.
     */
    @NotNull(message = "La fecha de cierre es obligatoria")
    @Future(message = "La fecha de cierre debe ser futura")
    private LocalDateTime fechaCierre;

    /**
     * Identificador del usuario que crea la apuesta. Es obligatorio.
     */
    @NotNull(message = "El usuario es obligatorio")
    private Long usuarioId;

    /**
     * Obtiene el nombre de la apuesta.
     *
     * @return nombre de la apuesta
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Establece el nombre de la apuesta.
     *
     * @param nombre nombre de la apuesta
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Obtiene la fecha y hora de cierre de la apuesta.
     *
     * @return fecha y hora de cierre
     */
    public LocalDateTime getFechaCierre() {
        return fechaCierre;
    }

    /**
     * Establece la fecha y hora de cierre de la apuesta.
     *
     * @param fechaCierre fecha y hora de cierre (debe ser futura)
     */
    public void setFechaCierre(LocalDateTime fechaCierre) {
        this.fechaCierre = fechaCierre;
    }

    /**
     * Obtiene el ID del usuario creador de la apuesta.
     *
     * @return ID del usuario creador
     */
    public Long getUsuarioId() {
        return usuarioId;
    }

    /**
     * Establece el ID del usuario creador de la apuesta.
     *
     * @param usuarioId ID del usuario creador
     */
    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }
}