package co.edu.unbosque.mundial_2026.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * DTO que representa la disponibilidad de cupos en una zona completa del estadio.
 * <p>
 * Agrupa el resumen de capacidad de la zona (límite, vendidos, disponibles)
 * junto con el detalle por cada fila dentro de esa zona.
 * </p>
 */
public class CuposZonaDTO {

    /** Nombre de la zona dentro del estadio (por ejemplo: "VIP", "GENERAL"). */
    private String zona;

    /** Número máximo de entradas permitidas en esta zona. */
    private int limite;

    /** Número de entradas ya vendidas en esta zona. */
    private int vendidos;

    /** Número de entradas aún disponibles (calculado como {@code limite - vendidos}). */
    private int disponibles;

    /** Lista con el detalle de cupos disponibles por cada fila de la zona. */
    private List<CuposFilaDTO> filas;

    /**
     * Constructor vacío que inicializa la lista de filas vacía.
     */
    public CuposZonaDTO() {
        this.filas = new ArrayList<>();
    }

    /**
     * Constructor original sin filas. Se mantiene por compatibilidad con
     * cualquier código que ya lo invoque. La lista de filas queda vacía.
     *
     * @param zona     nombre de la zona
     * @param limite   número máximo de entradas en la zona
     * @param vendidos número de entradas ya vendidas
     */
    public CuposZonaDTO(String zona, int limite, int vendidos) {
        this.zona = zona;
        this.limite = limite;
        this.vendidos = vendidos;
        this.disponibles = limite - vendidos;
        this.filas = new ArrayList<>();
    }

    /**
     * Constructor completo con detalle de filas. Es el que usa el servicio
     * en obtenerCuposPorZona después del refactor.
     *
     * @param zona     nombre de la zona
     * @param limite   número máximo de entradas en la zona
     * @param vendidos número de entradas ya vendidas
     * @param filas    lista con el detalle de cupos por fila (puede ser {@code null})
     */
    public CuposZonaDTO(String zona, int limite, int vendidos, List<CuposFilaDTO> filas) {
        this.zona = zona;
        this.limite = limite;
        this.vendidos = vendidos;
        this.disponibles = limite - vendidos;
        this.filas = filas != null ? filas : new ArrayList<>();
    }

    /**
     * Obtiene el nombre de la zona.
     *
     * @return nombre de la zona
     */
    public String getZona() {
        return zona;
    }

    /**
     * Establece el nombre de la zona.
     *
     * @param zona nombre de la zona
     */
    public void setZona(String zona) {
        this.zona = zona;
    }

    /**
     * Obtiene el límite de entradas de la zona.
     *
     * @return número máximo de entradas permitidas
     */
    public int getLimite() {
        return limite;
    }

    /**
     * Establece el límite de entradas de la zona.
     *
     * @param limite número máximo de entradas permitidas
     */
    public void setLimite(int limite) {
        this.limite = limite;
    }

    /**
     * Obtiene el número de entradas vendidas en la zona.
     *
     * @return entradas vendidas
     */
    public int getVendidos() {
        return vendidos;
    }

    /**
     * Establece el número de entradas vendidas en la zona.
     *
     * @param vendidos entradas vendidas
     */
    public void setVendidos(int vendidos) {
        this.vendidos = vendidos;
    }

    /**
     * Obtiene el número de entradas disponibles en la zona.
     *
     * @return entradas disponibles
     */
    public int getDisponibles() {
        return disponibles;
    }

    /**
     * Establece el número de entradas disponibles en la zona.
     *
     * @param disponibles entradas disponibles
     */
    public void setDisponibles(int disponibles) {
        this.disponibles = disponibles;
    }

    /**
     * Obtiene la lista de cupos detallados por fila de la zona.
     *
     * @return lista de filas con sus cupos
     */
    public List<CuposFilaDTO> getFilas() {
        return filas;
    }

    /**
     * Establece la lista de cupos detallados por fila de la zona.
     * Si se pasa {@code null}, se asigna una lista vacía.
     *
     * @param filas lista de filas con sus cupos
     */
    public void setFilas(List<CuposFilaDTO> filas) {
        this.filas = filas != null ? filas : new ArrayList<>();
    }
}