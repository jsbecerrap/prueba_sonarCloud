package co.edu.unbosque.mundial_2026.exception;
/**
 * Excepción lanzada cuando se intenta realizar una accion y el estado es invalido para el usuario 
 */
public class EstadoInvalidoException extends RuntimeException {
    /**
     * Construye una nueva excepción con el mensaje especificado.
     *
     * @param message mensaje descriptivo del error
     */
    public EstadoInvalidoException(String mensaje) {
        super(mensaje);
    }
}