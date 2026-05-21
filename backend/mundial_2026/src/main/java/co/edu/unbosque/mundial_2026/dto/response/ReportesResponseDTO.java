package co.edu.unbosque.mundial_2026.dto.response;

public class ReportesResponseDTO {
    private int totalUsuarios;
    private int totalPartidos;
    private int totalTransacciones;
    private int usuariosActivos;

    public ReportesResponseDTO() {
    }

    public int getTotalUsuarios() {
        return totalUsuarios;
    }

    public void setTotalUsuarios(int totalUsuarios) {
        this.totalUsuarios = totalUsuarios;
    }

    public int getTotalPartidos() {
        return totalPartidos;
    }

    public void setTotalPartidos(int totalPartidos) {
        this.totalPartidos = totalPartidos;
    }

    public int getTotalTransacciones() {
        return totalTransacciones;
    }

    public void setTotalTransacciones(int totalTransacciones) {
        this.totalTransacciones = totalTransacciones;
    }

    public int getUsuariosActivos() {
        return usuariosActivos;
    }

    public void setUsuariosActivos(int usuariosActivos) {
        this.usuariosActivos = usuariosActivos;
    }
}