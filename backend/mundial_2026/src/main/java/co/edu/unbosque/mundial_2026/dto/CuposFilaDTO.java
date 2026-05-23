package co.edu.unbosque.mundial_2026.dto;

/**
 * DTO que representa la disponibilidad de cupos en una fila específica de un sector.
 * <p>
 * Contiene el nombre de la fila, su límite de entradas, las vendidas y
 * el cálculo automático de las disponibles.
 * </p>
 */
public class CuposFilaDTO {

    /** Nombre o identificador de la fila dentro del sector. */
    private String nombre;

    /** Número máximo de entradas permitidas en esta fila. */
    private int limite;

    /** Número de entradas ya vendidas en esta fila. */
    private int vendidos;

    /** Número de entradas aún disponibles (calculado como {@code limite - vendidos}). */
    private int disponibles;

    /**
     * Constructor vacío requerido por frameworks de serialización.
     */
    public CuposFilaDTO() {
        //Constructor vacio
    }

    /**
     * Constructor que inicializa los datos de la fila y calcula automáticamente
     * las entradas disponibles.
     *
     * @param nombre   nombre o identificador de la fila
     * @param limite   número máximo de entradas en la fila
     * @param vendidos número de entradas ya vendidas
     */
    public CuposFilaDTO(String nombre, int limite, int vendidos) {
        this.nombre = nombre;
        this.limite = limite;
        this.vendidos = vendidos;
        this.disponibles = limite - vendidos;
    }

    /**
     * Obtiene el nombre de la fila.
     *
     * @return nombre de la fila
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Establece el nombre de la fila.
     *
     * @param nombre nombre de la fila
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Obtiene el límite de entradas de la fila.
     *
     * @return número máximo de entradas permitidas
     */
    public int getLimite() {
        return limite;
    }

    /**
     * Establece el límite de entradas de la fila.
     *
     * @param limite número máximo de entradas permitidas
     */
    public void setLimite(int limite) {
        this.limite = limite;
    }

    /**
     * Obtiene el número de entradas vendidas en la fila.
     *
     * @return entradas vendidas
     */
    public int getVendidos() {
        return vendidos;
    }

    /**
     * Establece el número de entradas vendidas en la fila.
     *
     * @param vendidos entradas vendidas
     */
    public void setVendidos(int vendidos) {
        this.vendidos = vendidos;
    }

    /**
     * Obtiene el número de entradas disponibles en la fila.
     *
     * @return entradas disponibles
     */
    public int getDisponibles() {
        return disponibles;
    }

    /**
     * Establece el número de entradas disponibles en la fila.
     *
     * @param disponibles entradas disponibles
     */
    public void setDisponibles(int disponibles) {
        this.disponibles = disponibles;
    }
}