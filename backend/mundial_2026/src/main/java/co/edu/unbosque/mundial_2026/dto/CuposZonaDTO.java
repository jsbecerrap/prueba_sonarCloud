package co.edu.unbosque.mundial_2026.dto;

import java.util.ArrayList;
import java.util.List;

public class CuposZonaDTO {

    private String zona;
    private int limite;
    private int vendidos;
    private int disponibles;
    private List<CuposFilaDTO> filas;

    public CuposZonaDTO() {
        this.filas = new ArrayList<>();
    }

    /**
     * Constructor original sin filas. Se mantiene por compatibilidad con
     * cualquier código que ya lo invoque. La lista de filas queda vacía.
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
     */
    public CuposZonaDTO(String zona, int limite, int vendidos, List<CuposFilaDTO> filas) {
        this.zona = zona;
        this.limite = limite;
        this.vendidos = vendidos;
        this.disponibles = limite - vendidos;
        this.filas = filas != null ? filas : new ArrayList<>();
    }

    public String getZona() {
        return zona;
    }

    public void setZona(String zona) {
        this.zona = zona;
    }

    public int getLimite() {
        return limite;
    }

    public void setLimite(int limite) {
        this.limite = limite;
    }

    public int getVendidos() {
        return vendidos;
    }

    public void setVendidos(int vendidos) {
        this.vendidos = vendidos;
    }

    public int getDisponibles() {
        return disponibles;
    }

    public void setDisponibles(int disponibles) {
        this.disponibles = disponibles;
    }

    public List<CuposFilaDTO> getFilas() {
        return filas;
    }

    public void setFilas(List<CuposFilaDTO> filas) {
        this.filas = filas != null ? filas : new ArrayList<>();
    }
}