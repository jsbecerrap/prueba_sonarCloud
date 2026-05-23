package co.edu.unbosque.mundial_2026.exception;
/**
 * Excepción lanzada cuando se intenta acceder a un usuario y este no existe 
 */
public class UsuarioNotFoundException extends RuntimeException {
    /**
     * Construye una nueva excepción con el mensaje especificado.
     *
     * @param message mensaje descriptivo del error
     */
    public UsuarioNotFoundException(String mensaje) {
        super(mensaje);
    }
}