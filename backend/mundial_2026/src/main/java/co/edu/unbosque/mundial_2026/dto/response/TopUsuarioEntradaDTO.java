package co.edu.unbosque.mundial_2026.dto.response;

public class TopUsuarioEntradaDTO {
    private Long usuarioId;
    private String nombre;
    private String apellido;
    private String correo;
    private int totalEntradas;
    private double totalGastado;

    public TopUsuarioEntradaDTO() {
    }

    public TopUsuarioEntradaDTO(Long usuarioId, String nombre, String apellido, String correo, int totalEntradas,
            double totalGastado) {
        this.usuarioId = usuarioId;
        this.nombre = nombre;
        this.apellido = apellido;
        this.correo = correo;
        this.totalEntradas = totalEntradas;
        this.totalGastado = totalGastado;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
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

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public int getTotalEntradas() {
        return totalEntradas;
    }

    public void setTotalEntradas(int totalEntradas) {
        this.totalEntradas = totalEntradas;
    }

    public double getTotalGastado() {
        return totalGastado;
    }

    public void setTotalGastado(double totalGastado) {
        this.totalGastado = totalGastado;
    }
}