package co.edu.unbosque.mundial_2026.dto.request;

import jakarta.validation.constraints.Positive;

public class ProductoActualizarRequestDTO {

    @Positive(message = "El precio debe ser mayor a 0")
    private Double precio;

    @Positive(message = "El stock debe ser mayor a 0")
    private Integer stock;

    private String imagenUrl;

    private String descripcion;

    public Double getPrecio() {
        return precio;
    }

    public void setPrecio(Double precio) {
        this.precio = precio;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public String getImagenUrl() {
        return imagenUrl;
    }

    public void setImagenUrl(String imagenUrl) {
        this.imagenUrl = imagenUrl;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}