package co.edu.unbosque.mundial_2026.exception;
/**
 * Excepción lanzada cuando se intenta acceder a una categoria y no existe o esta desactivada
 */
public class CategoriaNotFoundException extends RuntimeException {
    /**
     * Construye una nueva excepción con el mensaje especificado.
     *
     * @param message mensaje descriptivo del error
     */
    public CategoriaNotFoundException(String message) {
        super(message);
    }
}