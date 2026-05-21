package co.edu.unbosque.mundial_2026.dto.response;

public class TopUsuarioSouvenirDTO {
    private Long usuarioId;
    private String nombre;
    private String apellido;
    private String correo;
    private int totalOrdenes;
    private double totalGastado;

    public TopUsuarioSouvenirDTO() {
    }

    public TopUsuarioSouvenirDTO(Long usuarioId, String nombre, String apellido, String correo, int totalOrdenes,
            double totalGastado) {
        this.usuarioId = usuarioId;
        this.nombre = nombre;
        this.apellido = apellido;
        this.correo = correo;
        this.totalOrdenes = totalOrdenes;
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

    public int getTotalOrdenes() {
        return totalOrdenes;
    }

    public void setTotalOrdenes(int totalOrdenes) {
        this.totalOrdenes = totalOrdenes;
    }

    public double getTotalGastado() {
        return totalGastado;
    }

    public void setTotalGastado(double totalGastado) {
        this.totalGastado = totalGastado;
    }
}