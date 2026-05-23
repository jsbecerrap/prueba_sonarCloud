package co.edu.unbosque.mundial_2026.exception;
/**
 * Excepción lanzada cuando no encuentra la apuesta
 */
public class ApuestaNotFoundException extends RuntimeException {
    /**
     * Construye una nueva excepción con el mensaje especificado.
     *
     * @param message mensaje descriptivo del error
     */
    public ApuestaNotFoundException(String message) {
        super(message);
    }
}