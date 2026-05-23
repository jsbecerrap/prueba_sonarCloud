package co.edu.unbosque.mundial_2026.exception;

/**
 * Excepción lanzada cuando se intenta realizar una acción
 * sobre una categoría que aún tiene productos asociados.
 */
public class CategoriaConProductosException extends RuntimeException {

    /**
     * Construye una nueva excepción con el mensaje especificado.
     *
     * @param message mensaje descriptivo del error
     */
    public CategoriaConProductosException(String message) {
        super(message);
    }
}