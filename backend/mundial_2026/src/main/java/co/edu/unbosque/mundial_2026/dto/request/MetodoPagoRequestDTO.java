package co.edu.unbosque.mundial_2026.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * DTO de solicitud para registrar un nuevo método de pago para un usuario.
 * <p>
 * Incluye validaciones sobre el tipo de pago, el nombre del método y
 * los detalles de referencia para garantizar la integridad de los datos.
 * </p>
 */
public class MetodoPagoRequestDTO {

    /** Identificador del usuario al que se asociará el método de pago. */
    private Long usuarioId;

    /**
     * Tipo de método de pago. Debe ser uno de los valores permitidos:
     * CARD, PSE, CASH o TRANSFER.
     */
    @NotBlank(message = "El tipo es obligatorio")
    @Pattern(
        regexp = "CARD|PSE|CASH|TRANSFER",
        message = "El tipo debe ser CARD, PSE, CASH o TRANSFER"
    )
    private String type;

    /**
     * Nombre descriptivo del método de pago (por ejemplo: "Visa Débito").
     * Debe tener entre 4 y 40 caracteres y solo puede contener letras, números,
     * espacios, guiones bajos o guiones.
     */
    @NotBlank(message = "El nombre del método es obligatorio")
    @Size(min = 4, max = 40, message = "El nombre debe tener entre 4 y 40 caracteres")
    @Pattern(
        regexp = "^[A-Za-zÁÉÍÓÚáéíóúÑñ0-9\\s_-]+$",
        message = "El nombre solo puede tener letras, números, espacios, guiones bajos o guiones"
    )
    private String label;

    /**
     * Referencia o detalles adicionales del método (por ejemplo: últimos 4 dígitos).
     * Opcional; si se proporciona, debe tener entre 4 y 40 caracteres.
     */
    @Size(min = 4, max = 40, message = "La referencia debe tener entre 4 y 40 caracteres")
    @Pattern(
        regexp = "^[A-Za-z0-9\\s*._-]+$",
        message = "La referencia solo puede tener letras, números, espacios, puntos, guiones o asteriscos"
    )
    private String details;

    /**
     * Constructor vacío requerido por frameworks de serialización.
     */
    public MetodoPagoRequestDTO() {
        //Constructor(Comentario requerido por sonar)
    }

    /**
     * Obtiene el ID del usuario al que pertenece el método de pago.
     *
     * @return ID del usuario
     */
    public Long getUsuarioId() {
        return usuarioId;
    }

    /**
     * Establece el ID del usuario al que pertenece el método de pago.
     *
     * @param usuarioId ID del usuario
     */
    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    /**
     * Obtiene el tipo del método de pago.
     *
     * @return tipo del método (CARD, PSE, CASH o TRANSFER)
     */
    public String getType() {
        return type;
    }

    /**
     * Establece el tipo del método de pago.
     *
     * @param type tipo del método (CARD, PSE, CASH o TRANSFER)
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Obtiene el nombre descriptivo del método de pago.
     *
     * @return nombre del método de pago
     */
    public String getLabel() {
        return label;
    }

    /**
     * Establece el nombre descriptivo del método de pago.
     *
     * @param label nombre del método de pago
     */
    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * Obtiene los detalles de referencia del método de pago.
     *
     * @return detalles del método de pago
     */
    public String getDetails() {
        return details;
    }

    /**
     * Establece los detalles de referencia del método de pago.
     *
     * @param details detalles del método de pago
     */
    public void setDetails(String details) {
        this.details = details;
    }
}