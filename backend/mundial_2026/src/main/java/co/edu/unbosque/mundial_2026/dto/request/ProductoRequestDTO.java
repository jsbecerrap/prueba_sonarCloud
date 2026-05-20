package co.edu.unbosque.mundial_2026.dto.request;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public class ProductoRequestDTO {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    @Pattern(
        regexp = "^[A-Za-zÁÉÍÓÚáéíóúÑñ0-9\\s_.-]+$",
        message = "El nombre solo puede tener letras, números, espacios, puntos, guiones bajos o guiones"
    )
    private String nombre;

    @Size(max = 500, message = "La descripción no puede tener más de 500 caracteres")
    @Pattern(
        regexp = "^[A-Za-zÁÉÍÓÚáéíóúÑñ0-9\\s_.,;:()\\-]*$",
        message = "La descripción contiene caracteres no permitidos"
    )
    private String descripcion;

    @NotNull(message = "El precio es obligatorio")
    @PositiveOrZero(message = "El precio no puede ser negativo")
    @DecimalMax(value = "99999999.99", message = "El precio supera el máximo permitido")
    private Double precio;

    @Size(max = 500, message = "La URL de imagen no puede tener más de 500 caracteres")
    @Pattern(
        regexp = "^https://[a-zA-Z0-9._/?&=#%~+\\-:,]+$",
        message = "La URL de la imagen debe ser HTTPS y tener un formato válido"
    )
    private String imagenUrl;

    @NotNull(message = "La categoria es obligatoria")
    private Long categoriaId;

    @Size(max = 50, message = "El código de producto no puede tener más de 50 caracteres")
    @Pattern(
        regexp = "^[A-Za-z0-9_\\-]*$",
        message = "El código de producto solo puede tener letras, números, guiones bajos o guiones"
    )
    private String codigoProducto;

    @Size(max = 60, message = "El equipo no puede tener más de 60 caracteres")
    @Pattern(
        regexp = "^[A-Za-zÁÉÍÓÚáéíóúÑñ0-9\\s_.-]*$",
        message = "El equipo solo puede tener letras, números, espacios, puntos, guiones bajos o guiones"
    )
    private String equipo;

    @Size(max = 10, message = "El código de bandera no puede tener más de 10 caracteres")
    @Pattern(
        regexp = "^[A-Za-z]*$",
        message = "La bandera debe ser un código de país en letras"
    )
    private String bandera;

    private Boolean destacado = false;

    @NotNull(message = "Las variantes son obligatorias")
    @NotEmpty(message = "Debe agregar al menos una variante")
    @Size(max = 50, message = "No puede tener más de 50 variantes")
    @Valid
    private List<VarianteRequestDTO> variantes;

    public static class VarianteRequestDTO {

        @Size(max = 100, message = "La especificación no puede tener más de 100 caracteres")
        @Pattern(
            regexp = "^[A-Za-zÁÉÍÓÚáéíóúÑñ0-9\\s_.,;:()\\-]*$",
            message = "La especificación contiene caracteres no permitidos"
        )
        private String especificacion;

        @NotNull(message = "El stock de la variante es obligatorio")
        @PositiveOrZero(message = "El stock no puede ser negativo")
        private Integer stock;

        public String getEspecificacion() {
            return especificacion;
        }

        public void setEspecificacion(String especificacion) {
            this.especificacion = especificacion;
        }

        public Integer getStock() {
            return stock;
        }

        public void setStock(Integer stock) {
            this.stock = stock;
        }
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

    public Long getCategoriaId() {
        return categoriaId;
    }

    public void setCategoriaId(Long categoriaId) {
        this.categoriaId = categoriaId;
    }

    public String getCodigoProducto() {
        return codigoProducto;
    }

    public void setCodigoProducto(String codigoProducto) {
        this.codigoProducto = codigoProducto;
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

    public List<VarianteRequestDTO> getVariantes() {
        return variantes;
    }

    public void setVariantes(List<VarianteRequestDTO> variantes) {
        this.variantes = variantes;
    }
}