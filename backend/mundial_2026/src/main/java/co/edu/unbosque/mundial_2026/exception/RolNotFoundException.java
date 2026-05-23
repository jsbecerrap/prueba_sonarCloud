package co.edu.unbosque.mundial_2026.exception;
/**
 * Excepción lanzada cuando se intenta acceder a un rol y este no existe 
 */
public class RolNotFoundException extends RuntimeException {
    /**
     * Construye una nueva excepción con el mensaje especificado.
     *
     * @param message mensaje descriptivo del error
     */
    public RolNotFoundException(String mensaje) {
        super(mensaje);
    }
}