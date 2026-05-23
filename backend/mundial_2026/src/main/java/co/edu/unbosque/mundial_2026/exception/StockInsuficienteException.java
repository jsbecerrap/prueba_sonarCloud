package co.edu.unbosque.mundial_2026.exception;
/**
 * Excepción lanzada cuando se intenta comprar un producto pero no hay suficiente cantidades
 */
public class StockInsuficienteException extends RuntimeException {
    /**
     * Construye una nueva excepción con el mensaje especificado.
     *
     * @param message mensaje descriptivo del error
     */
    public StockInsuficienteException(String message) {
        super(message);
    }
}