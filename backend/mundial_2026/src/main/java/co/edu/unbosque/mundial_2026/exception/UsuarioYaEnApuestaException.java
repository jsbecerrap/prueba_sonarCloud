package co.edu.unbosque.mundial_2026.exception;
/**
 * Excepción lanzada cuando se intenta acceder a una apuesta y ya el usuario esta en ella  
 */
public class UsuarioYaEnApuestaException extends RuntimeException {
    /**
     * Construye una nueva excepción con el mensaje especificado.
     *
     * @param message mensaje descriptivo del error
     */
    public UsuarioYaEnApuestaException(String message) { super(message); }
}
