package co.edu.unbosque.mundial_2026.dto.response;

/**
 * DTO de respuesta para las preferencias
 * Contiene informacion basica de la preferencia
 */
public class PreferenciaDTO {

    /**
     * Identificador de la preferencia
     */
    private final Long id;

    /**
     * Nombre de la preferencia
     */
    private final String nombre;

    /**
     * Constructor con todos los atributos de la clase
     *
     * @param id identificador de la preferencia
     * @param nombre nombre de la preferencia
     */
    public PreferenciaDTO(Long id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    /**
     * Obtiene el identificador de la preferencia
     *
     * @return identificador de la preferencia
     */
    public Long getId() {
        return id;
    }

    /**
     * Obtiene el nombre de la preferencia
     *
     * @return nombre de la preferencia
     */
    public String getNombre() {
        return nombre;
    }
}