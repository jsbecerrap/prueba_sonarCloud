package co.edu.unbosque.mundial_2026.exception;
/**
 * Excepción lanzada cuando se intenta acceder a un item y no existe 
 */
public class ItemNotFoundException extends RuntimeException {
    /**
     * Construye una nueva excepción con el mensaje especificado.
     *
     * @param message mensaje descriptivo del error
     */
    public ItemNotFoundException(String message) { super(message); }
}