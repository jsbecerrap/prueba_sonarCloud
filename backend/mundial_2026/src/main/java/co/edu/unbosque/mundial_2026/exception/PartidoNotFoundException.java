package co.edu.unbosque.mundial_2026.exception;
/**
 * Excepción lanzada cuando se intenta acceder a un partido y este no existe 
 */
public class PartidoNotFoundException extends RuntimeException {
    /**
     * Construye una nueva excepción con el mensaje especificado.
     *
     * @param message mensaje descriptivo del error
     */
    public PartidoNotFoundException(String mensaje) {
        super(mensaje);
    }
}