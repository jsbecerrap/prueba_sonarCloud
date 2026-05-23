package co.edu.unbosque.mundial_2026.exception;
/**
 * Excepción lanzada cuando se intenta pagar y no tiene una orden activa 
 */
public class OrdenNotFoundException extends RuntimeException {
    /**
     * Construye una nueva excepción con el mensaje especificado.
     *
     * @param message mensaje descriptivo del error
     */
    public OrdenNotFoundException(String message) {
        super(message);
    }
}