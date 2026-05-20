package co.edu.unbosque.mundial_2026.dto.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public class ProductoActualizarRequestDTO {

    @PositiveOrZero(message = "El precio no puede ser negativo")
    @DecimalMax(value = "99999999.99", message = "El precio supera el máximo permitido")
    private Double precio;

    @Size(max = 500, message = "La URL de imagen no puede tener más de 500 caracteres")
    @Pattern(
        regexp = "^https://[a-zA-Z0-9._/?&=#%~+\\-:,]+$",
        message = "La URL de la imagen debe ser HTTPS y tener un formato válido"
    )
    private String imagenUrl;

    @Size(max = 500, message = "La descripción no puede tener más de 500 caracteres")
    @Pattern(
        regexp = "^[A-Za-zÁÉÍÓÚáéíóúÑñ0-9\\s_.,;:()\\-]*$",
        message = "La descripción contiene caracteres no permitidos"
    )
    private String descripcion;

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

    private Boolean destacado;

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

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
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
}