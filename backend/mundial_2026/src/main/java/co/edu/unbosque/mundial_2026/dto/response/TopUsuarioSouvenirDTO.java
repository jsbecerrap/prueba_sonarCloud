package co.edu.unbosque.mundial_2026.dto.response;

/**
 * DTO de respuesta para usuarios con mas compras de souvenirs
 * Contiene informacion de ordenes y gasto total
 */
public class TopUsuarioSouvenirDTO {

    /**
     * Identificador del usuario
     */
    private Long usuarioId;

    /**
     * Nombre del usuario
     */
    private String nombre;

    /**
     * Apellido del usuario
     */
    private String apellido;

    /**
     * Correo del usuario
     */
    private String correo;

    /**
     * Total de ordenes realizadas
     */
    private int totalOrdenes;

    /**
     * Total gastado por el usuario
     */
    private double totalGastado;

    /**
     * Constructor vacio de la clase
     */
    public TopUsuarioSouvenirDTO() {
    }

    /**
     * Constructor con todos los atributos de la clase
     *
     * @param usuarioId identificador del usuario
     * @param nombre nombre del usuario
     * @param apellido apellido del usuario
     * @param correo correo del usuario
     * @param totalOrdenes total de ordenes realizadas
     * @param totalGastado total gastado por el usuario
     */
    public TopUsuarioSouvenirDTO(Long usuarioId, String nombre, String apellido, String correo, int totalOrdenes,
            double totalGastado) {
        this.usuarioId = usuarioId;
        this.nombre = nombre;
        this.apellido = apellido;
        this.correo = correo;
        this.totalOrdenes = totalOrdenes;
        this.totalGastado = totalGastado;
    }

    /**
     * Obtiene el id del usuario
     *
     * @return id del usuario
     */
    public Long getUsuarioId() {
        return usuarioId;
    }

    /**
     * Establece el id del usuario
     *
     * @param usuarioId id del usuario
     */
    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
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
     * Obtiene el correo del usuario
     *
     * @return correo del usuario
     */
    public String getCorreo() {
        return correo;
    }

    /**
     * Establece el correo del usuario
     *
     * @param correo correo del usuario
     */
    public void setCorreo(String correo) {
        this.correo = correo;
    }

    /**
     * Obtiene el total de ordenes
     *
     * @return total de ordenes
     */
    public int getTotalOrdenes() {
        return totalOrdenes;
    }

    /**
     * Establece el total de ordenes
     *
     * @param totalOrdenes total de ordenes
     */
    public void setTotalOrdenes(int totalOrdenes) {
        this.totalOrdenes = totalOrdenes;
    }

    /**
     * Obtiene el total gastado
     *
     * @return total gastado
     */
    public double getTotalGastado() {
        return totalGastado;
    }

    /**
     * Establece el total gastado
     *
     * @param totalGastado total gastado
     */
    public void setTotalGastado(double totalGastado) {
        this.totalGastado = totalGastado;
    }
}