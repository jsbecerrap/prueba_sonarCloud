package co.edu.unbosque.mundial_2026.exception;
/**
 * Excepción lanzada cuando se intenta pagar con un metodo de pago invalido 
 */
public class MetodoPagoInvalidoException extends RuntimeException {
    /**
     * Construye una nueva excepción con el mensaje especificado.
     *
     * @param message mensaje descriptivo del error
     */
    public MetodoPagoInvalidoException(String message) { super(message); }
}