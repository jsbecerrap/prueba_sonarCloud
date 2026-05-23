package co.edu.unbosque.mundial_2026.dto.response;

/**
 * DTO de respuesta para usuario
 * Contiene informacion basica del usuario
 */
public class UsuarioResponseDTO {

    /**
     * Identificador del usuario
     */
    private Long id;

    /**
     * Correo del usuario
     */
    private String correoUsuario;

    /**
     * Nombre del usuario
     */
    private String nombre;

    /**
     * Apellido del usuario
     */
    private String apellido;

    /**
     * Rol asignado al usuario
     */
    private String rol;

    /**
     * Indica si el usuario esta activo
     */
    private boolean activo;

    /**
     * Fecha de registro del usuario
     */
    private java.time.LocalDateTime fechaRegistro;

    /**
     * Obtiene el id del usuario
     *
     * @return id del usuario
     */
    public Long getId() {
        return id;
    }

    /**
     * Establece el id del usuario
     *
     * @param id id del usuario
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Obtiene el correo del usuario
     *
     * @return correo del usuario
     */
    public String getCorreoUsuario() {
        return correoUsuario;
    }

    /**
     * Establece el correo del usuario
     *
     * @param correoUsuario correo del usuario
     */
    public void setCorreoUsuario(String correoUsuario) {
        this.correoUsuario = correoUsuario;
    }

    /**
     * Obtiene el nombre del usuario
     *
     * @return nombre del usuario
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Establece el nombre del usuario
     *
     * @param nombre nombre del usuario
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Obtiene el apellido del usuario
     *
     * @return apellido del usuario
     */
    public String getApellido() {
        return apellido;
    }

    /**
     * Establece el apellido del usuario
     *
     * @param apellido apellido del usuario
     */
    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    /**
     * Obtiene el rol del usuario
     *
     * @return rol del usuario
     */
    public String getRol() {
        return rol;
    }

    /**
     * Establece el rol del usuario
     *
     * @param rol rol del usuario
     */
    public void setRol(String rol) {
        this.rol = rol;
    }

    /**
     * Indica si el usuario esta activo
     *
     * @return estado activo del usuario
     */
    public boolean isActivo() {
        return activo;
    }

    /**
     * Establece el estado activo del usuario
     *
     * @param activo estado activo
     */
    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    /**
     * Obtiene la fecha de registro del usuario
     *
     * @return fecha de registro
     */
    public java.time.LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    /**
     * Establece la fecha de registro del usuario
     *
     * @param fechaRegistro fecha de registro
     */
    public void setFechaRegistro(java.time.LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }
}