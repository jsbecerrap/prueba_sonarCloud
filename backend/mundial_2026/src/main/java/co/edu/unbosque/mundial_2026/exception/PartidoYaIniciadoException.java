package co.edu.unbosque.mundial_2026.exception;
/**
 * Excepción lanzada cuando se intenta apostar a un partido que ya inicio 
 */
public class PartidoYaIniciadoException extends RuntimeException {
    /**
     * Construye una nueva excepción con el mensaje especificado.
     *
     * @param message mensaje descriptivo del error
     */
    public PartidoYaIniciadoException(String message) { super(message); }
}