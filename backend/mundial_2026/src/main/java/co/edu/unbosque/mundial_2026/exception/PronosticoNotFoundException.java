package co.edu.unbosque.mundial_2026.exception;
/**
 * Excepción lanzada cuando se intenta acceder a un pronostico y este no existe 
 */
public class PronosticoNotFoundException extends RuntimeException {
    /**
     * Construye una nueva excepción con el mensaje especificado.
     *
     * @param message mensaje descriptivo del error
     */
    public PronosticoNotFoundException(String message) {
        super(message);
    }
}