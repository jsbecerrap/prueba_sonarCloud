package co.edu.unbosque.mundial_2026.exception;
/**
 * Excepción lanzada cuando se intenta pagar y el carro esta vacio
 */
public class CarritoVacioException extends RuntimeException {
    /**
     * Construye una nueva excepción con el mensaje especificado.
     *
     * @param message mensaje descriptivo del error
     */
    public CarritoVacioException(String message) { super(message); }
}
