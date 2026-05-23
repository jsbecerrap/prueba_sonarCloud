package co.edu.unbosque.mundial_2026.exception;
/**
 * Excepción lanzada cuando se intenta pagar y el metodo de pago no existe 
 */
public class MetodoPagoNotFoundException extends RuntimeException {
    /**
     * Construye una nueva excepción con el mensaje especificado.
     *
     * @param message mensaje descriptivo del error
     */
    public MetodoPagoNotFoundException(String mensaje) {
        super(mensaje);
    }
}