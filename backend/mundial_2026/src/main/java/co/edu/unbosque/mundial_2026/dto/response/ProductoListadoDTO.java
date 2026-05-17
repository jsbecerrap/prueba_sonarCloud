package co.edu.unbosque.mundial_2026.dto.response;

import java.util.List;

public class ProductoListadoDTO {
    private Long id;
    private String nombre;
    private String descripcion;
    private Double precio;
    private String imagenUrl;
    private String categoriaNombre;
    private String equipo;
    private String bandera;
    private Boolean destacado;
    private Integer stockTotal;
    private Boolean tieneVariantes;
    private List<VarianteDTO> variantes;

    public ProductoListadoDTO() {
    }

    public ProductoListadoDTO(Long id, String nombre, String descripcion,
            Double precio, String imagenUrl, String categoriaNombre,
            String equipo, String bandera, Boolean destacado,
            Long stockTotal, Long cantidadVariantes) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.imagenUrl = imagenUrl;
        this.categoriaNombre = categoriaNombre;
        this.equipo = equipo;
        this.bandera = bandera;
        this.destacado = destacado;
        this.stockTotal = stockTotal != null ? stockTotal.intValue() : 0;
        this.tieneVariantes = cantidadVariantes != null && cantidadVariantes > 1;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Double getPrecio() {
        return precio;
    }

    public void setPrecio(Double precio) {
        this.precio = precio;
    }

    public String getImagenUrl() {
        return imagenUrl;
    }

    public void setImagenUrl(String imagenUrl) {
        this.imagenUrl = imagenUrl;
    }

    public String getCategoriaNombre() {
        return categoriaNombre;
    }

    public void setCategoriaNombre(String categoriaNombre) {
        this.categoriaNombre = categoriaNombre;
    }

    public String getEquipo() {
        return equipo;
    }

    public void setEquipo(String equipo) {
        this.equipo = equipo;
    }

    public String getBandera() {
        return bandera;
    }

    public void setBandera(String bandera) {
        this.bandera = bandera;
    }

    public Boolean getDestacado() {
        return destacado;
    }

    public void setDestacado(Boolean destacado) {
        this.destacado = destacado;
    }

    public Integer getStockTotal() {
        return stockTotal;
    }

    public void setStockTotal(Integer stockTotal) {
        this.stockTotal = stockTotal;
    }

    public Boolean getTieneVariantes() {
        return tieneVariantes;
    }

    public void setTieneVariantes(Boolean tieneVariantes) {
        this.tieneVariantes = tieneVariantes;
    }

    public List<VarianteDTO> getVariantes() {
        return variantes;
    }

    public void setVariantes(List<VarianteDTO> variantes) {
        this.variantes = variantes;
    }

    public static class VarianteDTO {
        private Long id;
        private String especificacion;
        private Integer stock;

        public VarianteDTO(Long id, String especificacion, Integer stock) {
            this.id = id;
            this.especificacion = especificacion;
            this.stock = stock;
        }

        public Long getId() {
            return id;
        }

        public String getEspecificacion() {
            return especificacion;
        }

        public Integer getStock() {
            return stock;
        }
    }
}