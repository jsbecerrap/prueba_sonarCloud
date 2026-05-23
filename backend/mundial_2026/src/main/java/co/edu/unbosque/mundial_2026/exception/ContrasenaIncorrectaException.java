package co.edu.unbosque.mundial_2026.exception;
/**
 * Excepción lanzada cuando se hace registra con contraseña invalida 
 */
public class ContrasenaIncorrectaException extends RuntimeException {
    /**
     * Construye una nueva excepción con el mensaje especificado.
     *
     * @param message mensaje descriptivo del error
     */
    public ContrasenaIncorrectaException(String mensaje) {
        super(mensaje);
    }
}