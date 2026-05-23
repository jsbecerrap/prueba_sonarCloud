package co.edu.unbosque.mundial_2026.exception;
/**
 * Excepción lanzada cuando se intenta buscar un pronostico realizado y este no existe 
 */
public class ParticipacionNotFoundException extends RuntimeException {
    /**
     * Construye una nueva excepción con el mensaje especificado.
     *
     * @param message mensaje descriptivo del error
     */
    public ParticipacionNotFoundException(String message) { super(message); }
}