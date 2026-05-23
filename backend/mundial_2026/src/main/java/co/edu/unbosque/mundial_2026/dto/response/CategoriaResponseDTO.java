package co.edu.unbosque.mundial_2026.dto.response;

/**
 * DTO de respuesta para las categorias del sistema
 * Contiene la informacion basica de una categoria
 */
public class CategoriaResponseDTO {

    /**
     * Identificador unico de la categoria
     */
    private Long id;

    /**
     * Nombre de la categoria
     */
    private String nombre;

    /**
     * Descripcion de la categoria
     */
    private String descripcion;

    /**
     * Estado de la categoria
     * Indica si esta activa o no
     */
    private Boolean activo;

    /**
     * Obtiene el identificador de la categoria
     *
     * @return identificador de la categoria
     */
    public Long getId() {
        return id;
    }

    /**
     * Establece el identificador de la categoria
     *
     * @param id identificador de la categoria
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Obtiene el nombre de la categoria
     *
     * @return nombre de la categoria
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Establece el nombre de la categoria
     *
     * @param nombre nombre de la categoria
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Obtiene la descripcion de la categoria
     *
     * @return descripcion de la categoria
     */
    public String getDescripcion() {
        return descripcion;
    }

    /**
     * Establece la descripcion de la categoria
     *
     * @param descripcion descripcion de la categoria
     */
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    /**
     * Obtiene el estado de la categoria
     *
     * @return estado de la categoria
     */
    public Boolean getActivo() {
        return activo;
    }

    /**
     * Establece el estado de la categoria
     *
     * @param activo estado de la categoria
     */
    public void setActivo(Boolean activo) {
        this.activo = activo;
    }
}