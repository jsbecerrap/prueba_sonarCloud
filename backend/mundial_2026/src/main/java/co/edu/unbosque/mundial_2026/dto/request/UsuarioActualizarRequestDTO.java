package co.edu.unbosque.mundial_2026.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class UsuarioActualizarRequestDTO {

    

    @Size(min = 2, max = 50, message = "El nombre debe tener entre 2 y 50 caracteres")
    @Pattern(
        regexp = "^[A-Za-zÁÉÍÓÚáéíóúÑñ\\s'-]*$",
        message = "El nombre solo puede tener letras, espacios, apóstrofes o guiones"
    )
    private String nombre;

    @Size(min = 2, max = 50, message = "El apellido debe tener entre 2 y 50 caracteres")
    @Pattern(
        regexp = "^[A-Za-zÁÉÍÓÚáéíóúÑñ\\s'-]*$",
        message = "El apellido solo puede tener letras, espacios, apóstrofes o guiones"
    )
    private String apellido;

    @Email(message = "El correo nuevo no es válido")
    @Size(max = 100, message = "El correo no puede tener más de 100 caracteres")
    private String correoNuevo;

    @Size(max = 72, message = "La contraseña actual no puede tener más de 72 caracteres")
    private String contrasenaActual;

   
    @Size(min = 8, max = 72, message = "La contraseña debe tener entre 8 y 72 caracteres")
    @Pattern(regexp = "^(?=.*[A-Z]).+$", message = "La contraseña debe tener al menos una letra mayúscula")
    @Pattern(regexp = "^(?=.*[a-z]).+$", message = "La contraseña debe tener al menos una letra minúscula")
    @Pattern(regexp = "^(?=.*\\d).+$", message = "La contraseña debe tener al menos un número")
    @Pattern(regexp = "^(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]).+$", message = "La contraseña debe tener al menos un símbolo")
    private String contrasenaNueva;

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getCorreoNuevo() {
        return correoNuevo;
    }

    public void setCorreoNuevo(String correoNuevo) {
        this.correoNuevo = correoNuevo;
    }

    public String getContrasenaActual() {
        return contrasenaActual;
    }

    public void setContrasenaActual(String contrasenaActual) {
        this.contrasenaActual = contrasenaActual;
    }

    public String getContrasenaNueva() {
        return contrasenaNueva;
    }

    public void setContrasenaNueva(String contrasenaNueva) {
        this.contrasenaNueva = contrasenaNueva;
    }
}