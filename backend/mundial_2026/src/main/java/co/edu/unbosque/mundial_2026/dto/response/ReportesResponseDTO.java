package co.edu.unbosque.mundial_2026.dto.response;

/**
 * DTO de respuesta para reportes generales
 * Contiene estadisticas del sistema
 */
public class ReportesResponseDTO {

    /**
     * Total de usuarios registrados
     */
    private int totalUsuarios;

    /**
     * Total de partidos registrados
     */
    private int totalPartidos;

    /**
     * Total de transacciones realizadas
     */
    private int totalTransacciones;

    /**
     * Total de usuarios activos
     */
    private int usuariosActivos;

    /**
     * Constructor vacio de la clase
     */
    public ReportesResponseDTO() {
        //Constructor comentario que requiere sonar
    }

    /**
     * Obtiene el total de usuarios
     *
     * @return total de usuarios
     */
    public int getTotalUsuarios() {
        return totalUsuarios;
    }

    /**
     * Establece el total de usuarios
     *
     * @param totalUsuarios total de usuarios
     */
    public void setTotalUsuarios(int totalUsuarios) {
        this.totalUsuarios = totalUsuarios;
    }

    /**
     * Obtiene el total de partidos
     *
     * @return total de partidos
     */
    public int getTotalPartidos() {
        return totalPartidos;
    }

    /**
     * Establece el total de partidos
     *
     * @param totalPartidos total de partidos
     */
    public void setTotalPartidos(int totalPartidos) {
        this.totalPartidos = totalPartidos;
    }

    /**
     * Obtiene el total de transacciones
     *
     * @return total de transacciones
     */
    public int getTotalTransacciones() {
        return totalTransacciones;
    }

    /**
     * Establece el total de transacciones
     *
     * @param totalTransacciones total de transacciones
     */
    public void setTotalTransacciones(int totalTransacciones) {
        this.totalTransacciones = totalTransacciones;
    }

    /**
     * Obtiene el total de usuarios activos
     *
     * @return usuarios activos
     */
    public int getUsuariosActivos() {
        return usuariosActivos;
    }

    /**
     * Establece los usuarios activos
     *
     * @param usuariosActivos usuarios activos
     */
    public void setUsuariosActivos(int usuariosActivos) {
        this.usuariosActivos = usuariosActivos;
    }
}