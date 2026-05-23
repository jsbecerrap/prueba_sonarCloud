package co.edu.unbosque.mundial_2026.exception;
/**
 * Excepción lanzada cuando se ingresa a una apuesta con codigo invalido 
 */
public class CodigoInvalidoException extends RuntimeException {
    /**
     * Construye una nueva excepción con el mensaje especificado.
     *
     * @param message mensaje descriptivo del error
     */
    public CodigoInvalidoException(String message) { super(message); }
}