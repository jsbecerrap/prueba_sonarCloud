package co.edu.unbosque.mundial_2026.exception;
/**
 * Excepción lanzada cuando se crea una cuenta con un correo que ya existe 
 */
public class CorreoEnUsoException extends RuntimeException {
    /**
     * Construye una nueva excepción con el mensaje especificado.
     *
     * @param message mensaje descriptivo del error
     */
    public CorreoEnUsoException(String mensaje) {
        super(mensaje);
    }
}