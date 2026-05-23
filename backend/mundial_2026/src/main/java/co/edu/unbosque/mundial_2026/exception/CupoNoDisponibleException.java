package co.edu.unbosque.mundial_2026.exception;
/**
 * Excepción lanzada cuando se ingresa y no hay cupo en los grupos de amigos 
 */
public class CupoNoDisponibleException extends RuntimeException {
    /**
     * Construye una nueva excepción con el mensaje especificado.
     *
     * @param message mensaje descriptivo del error
     */
    public CupoNoDisponibleException(String mensaje) {
        super(mensaje);
    }
}