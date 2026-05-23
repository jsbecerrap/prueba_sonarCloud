package co.edu.unbosque.mundial_2026.exception;
/**
 * Excepción lanzada cuando se intenta realizar una acción
 * sobre una apuesta que ya se encuentra cerrada.
 */
public class ApuestaCerradaException extends RuntimeException {
    /**
     * Construye una nueva excepción con el mensaje especificado.
     *
     * @param message mensaje descriptivo del error
     */
    public ApuestaCerradaException(String message) { super(message); }
}