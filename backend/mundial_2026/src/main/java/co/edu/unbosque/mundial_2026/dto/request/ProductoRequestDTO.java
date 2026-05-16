package co.edu.unbosque.mundial_2026.dto.request;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class ProductoRequestDTO {

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    private String descripcion;

    @NotNull(message = "El precio es obligatorio")
    @Positive(message = "El precio debe ser mayor a 0")
    private Double precio;

    private String imagenUrl;

    @NotNull(message = "La categoria es obligatoria")
    private Long categoriaId;

    private String codigoProducto;
    private String equipo;
    private String bandera;
    private Boolean destacado = false;

    @NotNull(message = "Las variantes son obligatorias")
    private List<VarianteRequestDTO> variantes;

    public static class VarianteRequestDTO {
        private String especificacion;

        @NotNull(message = "El stock de la variante es obligatorio")
        @Positive(message = "El stock debe ser mayor a 0")
        private Integer stock;

        public String getEspecificacion() { return especificacion; }
        public void setEspecificacion(String especificacion) { this.especificacion = especificacion; }
        public Integer getStock() { return stock; }
        public void setStock(Integer stock) { this.stock = stock; }
    }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public Double getPrecio() { return precio; }
    public void setPrecio(Double precio) { this.precio = precio; }

    public String getImagenUrl() { return imagenUrl; }
    public void setImagenUrl(String imagenUrl) { this.imagenUrl = imagenUrl; }

    public Long getCategoriaId() { return categoriaId; }
    public void setCategoriaId(Long categoriaId) { this.categoriaId = categoriaId; }

    public String getCodigoProducto() { return codigoProducto; }
    public void setCodigoProducto(String codigoProducto) { this.codigoProducto = codigoProducto; }

    public String getEquipo() { return equipo; }
    public void setEquipo(String equipo) { this.equipo = equipo; }

    public String getBandera() { return bandera; }
    public void setBandera(String bandera) { this.bandera = bandera; }

    public Boolean getDestacado() { return destacado; }
    public void setDestacado(Boolean destacado) { this.destacado = destacado; }

    public List<VarianteRequestDTO> getVariantes() { return variantes; }
    public void setVariantes(List<VarianteRequestDTO> variantes) { this.variantes = variantes; }
}