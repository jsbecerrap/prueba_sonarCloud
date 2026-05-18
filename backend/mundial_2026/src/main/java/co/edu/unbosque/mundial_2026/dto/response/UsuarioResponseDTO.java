package co.edu.unbosque.mundial_2026.dto.response;

public class UsuarioResponseDTO {

    private Long id;
    private String correoUsuario;
    private String nombre;
    private String apellido;
  private String rol;
    private boolean activo;
    private java.time.LocalDateTime fechaRegistro;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCorreoUsuario() {
        return correoUsuario;
    }

    public void setCorreoUsuario(String correoUsuario) {
        this.correoUsuario = correoUsuario;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }
    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public java.time.LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(java.time.LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }
}