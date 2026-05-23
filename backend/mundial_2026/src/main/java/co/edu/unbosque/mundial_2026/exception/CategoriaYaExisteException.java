package co.edu.unbosque.mundial_2026.exception;
/**
 * Excepción lanzada cuando se intenta crear una categoria con el mismo nombre
 */
public class CategoriaYaExisteException extends RuntimeException {
    /**
     * Construye una nueva excepción con el mensaje especificado.
     *
     * @param message mensaje descriptivo del error
     */
    public CategoriaYaExisteException(String message) { super(message); }
}