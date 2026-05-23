package co.edu.unbosque.mundial_2026.exception;
/**
 * Excepción lanzada cuando se intenta acceder a una entrada que no existe 
 */
public class EntradaNotFoundException extends RuntimeException {
    /**
     * Construye una nueva excepción con el mensaje especificado.
     *
     * @param message mensaje descriptivo del error
     */
    public EntradaNotFoundException(String mensaje) {
        super(mensaje);
    }
}