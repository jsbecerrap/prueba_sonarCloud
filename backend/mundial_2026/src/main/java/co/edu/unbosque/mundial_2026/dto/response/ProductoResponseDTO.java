package co.edu.unbosque.mundial_2026.dto.response;

import java.util.List;

public class ProductoResponseDTO {

    private Long id;
    private String nombre;
    private String descripcion;
    private Double precio;
    private String imagenUrl;
    private Boolean activo;
    private String categoriaNombre;
    private String codigoProducto;
    private String equipo;
    private String bandera;
    private Boolean destacado;
    private List<VarianteResponseDTO> variantes;

    // Stock total calculado (suma de todas las variantes)
    private Integer stockTotal;

    public static class VarianteResponseDTO {
        private Long id;
        private String especificacion; // null si no aplica (balón, copa, etc.)
        private Integer stock;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public String getEspecificacion() { return especificacion; }
        public void setEspecificacion(String especificacion) { this.especificacion = especificacion; }

        public Integer getStock() { return stock; }
        public void setStock(Integer stock) { this.stock = stock; }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public Double getPrecio() { return precio; }
    public void setPrecio(Double precio) { this.precio = precio; }

    public String getImagenUrl() { return imagenUrl; }
    public void setImagenUrl(String imagenUrl) { this.imagenUrl = imagenUrl; }

    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }

    public String getCategoriaNombre() { return categoriaNombre; }
    public void setCategoriaNombre(String categoriaNombre) { this.categoriaNombre = categoriaNombre; }

    public String getCodigoProducto() { return codigoProducto; }
    public void setCodigoProducto(String codigoProducto) { this.codigoProducto = codigoProducto; }

    public String getEquipo() { return equipo; }
    public void setEquipo(String equipo) { this.equipo = equipo; }

    public String getBandera() { return bandera; }
    public void setBandera(String bandera) { this.bandera = bandera; }

    public Boolean getDestacado() { return destacado; }
    public void setDestacado(Boolean destacado) { this.destacado = destacado; }

    public List<VarianteResponseDTO> getVariantes() { return variantes; }
    public void setVariantes(List<VarianteResponseDTO> variantes) { this.variantes = variantes; }

    public Integer getStockTotal() { return stockTotal; }
    public void setStockTotal(Integer stockTotal) { this.stockTotal = stockTotal; }
}