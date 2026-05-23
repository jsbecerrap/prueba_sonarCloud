package co.edu.unbosque.mundial_2026.dto.request;

import jakarta.validation.constraints.NotNull;

/**
 * DTO de solicitud para confirmar una orden de compra.
 * <p>
 * Especifica el método de pago que el usuario desea usar
 * para finalizar su compra pendiente.
 * </p>
 */
public class ConfirmarOrdenDTO {

    /**
     * Identificador del método de pago seleccionado por el usuario.
     * Es obligatorio para poder procesar el pago.
     */
    @NotNull(message = "El metodo de pago es obligatorio")
    private Long metodoPagoId;

    /**
     * Obtiene el identificador del método de pago.
     *
     * @return ID del método de pago seleccionado
     */
    public Long getMetodoPagoId() {
        return metodoPagoId;
    }

    /**
     * Establece el identificador del método de pago.
     *
     * @param metodoPagoId ID del método de pago seleccionado
     */
    public void setMetodoPagoId(Long metodoPagoId) {
        this.metodoPagoId = metodoPagoId;
    }
}