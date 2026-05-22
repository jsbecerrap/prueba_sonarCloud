package co.edu.unbosque.mundial_2026.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class MetodoPagoRequestDTO {

    private Long usuarioId;

    @NotBlank(message = "El tipo es obligatorio")
    @Pattern(
        regexp = "CARD|PSE|CASH|TRANSFER",
        message = "El tipo debe ser CARD, PSE, CASH o TRANSFER"
    )
    private String type;

    @NotBlank(message = "El nombre del método es obligatorio")
    @Size(min = 4, max = 40, message = "El nombre debe tener entre 4 y 40 caracteres")
    @Pattern(
        regexp = "^[A-Za-zÁÉÍÓÚáéíóúÑñ0-9\\s_-]+$",
        message = "El nombre solo puede tener letras, números, espacios, guiones bajos o guiones"
    )
    private String label;

    @Size(min = 4, max = 40, message = "La referencia debe tener entre 4 y 40 caracteres")
    @Pattern(
        regexp = "^[A-Za-z0-9\\s*._-]+$",
        message = "La referencia solo puede tener letras, números, espacios, puntos, guiones o asteriscos"
    )
    private String details;

    public MetodoPagoRequestDTO() {
        //Constructor(Comentario requerido por sonar)
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }
}