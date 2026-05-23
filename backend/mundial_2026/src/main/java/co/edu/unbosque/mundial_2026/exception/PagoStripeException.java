package co.edu.unbosque.mundial_2026.exception;

/**
 * Excepción lanzada cuando ocurre un error
 * durante el procesamiento de pagos con Stripe.
 */
public class PagoStripeException extends RuntimeException {

    /**
     * Construye una nueva excepción con el mensaje especificado.
     *
     * @param mensaje mensaje descriptivo del error
     */
    public PagoStripeException(String mensaje) {
        super(mensaje);
    }

    /**
     * Construye una nueva excepción con el mensaje
     * y la causa especificados.
     *
     * @param mensaje mensaje descriptivo del error
     * @param causa excepción original que causó el error
     */
    public PagoStripeException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}