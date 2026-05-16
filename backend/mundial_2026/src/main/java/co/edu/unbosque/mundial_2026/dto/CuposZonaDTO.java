package co.edu.unbosque.mundial_2026.dto;

public class CuposZonaDTO {
    private String zona;
    private int limite;
    private int vendidos;
    private int disponibles;

    public CuposZonaDTO() {
    }

    public CuposZonaDTO(String zona, int limite, int vendidos) {
        this.zona = zona;
        this.limite = limite;
        this.vendidos = vendidos;
        this.disponibles = limite - vendidos;
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
}