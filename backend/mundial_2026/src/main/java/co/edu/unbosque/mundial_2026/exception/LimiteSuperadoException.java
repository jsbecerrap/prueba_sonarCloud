package co.edu.unbosque.mundial_2026.exception;
/**
 * Excepción lanzada cuando se supera el limite en compra de entradas
 */
public class LimiteSuperadoException extends RuntimeException {
    /**
     * Construye una nueva excepción con el mensaje especificado.
     *
     * @param message mensaje descriptivo del error
     */
    public LimiteSuperadoException(String mensaje) {
        super(mensaje);
    }
}