package co.edu.unbosque.mundial_2026.dto;


public class CuposFilaDTO {

    private String nombre;
    private int limite;
    private int vendidos;
    private int disponibles;

    public CuposFilaDTO() {
        //Constructor vacio
    }

    public CuposFilaDTO(String nombre, int limite, int vendidos) {
        this.nombre = nombre;
        this.limite = limite;
        this.vendidos = vendidos;
        this.disponibles = limite - vendidos;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
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