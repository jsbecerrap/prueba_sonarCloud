package co.edu.unbosque.mundial_2026.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * DTO de solicitud para registrar un nuevo usuario en el sistema.
 * <p>
 * Contiene todos los campos requeridos para el registro: nombre, apellido,
 * correo electrónico y contraseña, con sus respectivas validaciones de formato
 * y seguridad. El campo de rol es opcional.
 * </p>
 */
public class UsuarioRequestDTO {

    /**
     * Nombre del usuario. Es obligatorio, debe tener entre 2 y 50 caracteres
     * y solo puede contener letras, espacios, apóstrofes o guiones.
     */
    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 50, message = "El nombre debe tener entre 2 y 50 caracteres")
    @Pattern(
        regexp = "^[A-Za-zÁÉÍÓÚáéíóúÑñ\\s'-]+$",
        message = "El nombre solo puede tener letras, espacios, apóstrofes o guiones"
    )
    private String nombre;

    /**
     * Apellido del usuario. Es obligatorio, debe tener entre 2 y 50 caracteres
     * y solo puede contener letras, espacios, apóstrofes o guiones.
     */
    @NotBlank(message = "El apellido es obligatorio")
    @Size(min = 2, max = 50, message = "El apellido debe tener entre 2 y 50 caracteres")
    @Pattern(
        regexp = "^[A-Za-zÁÉÍÓÚáéíóúÑñ\\s'-]+$",
        message = "El apellido solo puede tener letras, espacios, apóstrofes o guiones"
    )
    private String apellido;

    /**
     * Correo electrónico del usuario. Es obligatorio, debe tener un formato
     * de correo válido y no puede superar los 100 caracteres.
     */
    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "El correo no es válido")
    @Size(max = 100, message = "El correo no puede tener más de 100 caracteres")
    private String correoUsuario;

    /**
     * Contraseña del usuario. Es obligatoria, debe tener entre 8 y 72 caracteres
     * y cumplir los requisitos de seguridad: al menos una mayúscula, una minúscula,
     * un número y un símbolo especial.
     */
    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 8, max = 72, message = "La contraseña debe tener entre 8 y 72 caracteres")
    @Pattern(regexp = "^(?=.*[A-Z]).+$", message = "La contraseña debe tener al menos una letra mayúscula")
    @Pattern(regexp = "^(?=.*[a-z]).+$", message = "La contraseña debe tener al menos una letra minúscula")
    @Pattern(regexp = "^(?=.*\\d).+$", message = "La contraseña debe tener al menos un número")
    @Pattern(regexp = "^(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]).+$", message = "La contraseña debe tener al menos un símbolo")
    private String contrasena;

    /**
     * Rol asignado al usuario (por ejemplo: ROLE_USER, ROLE_ADMIN).
     * Es opcional; si no se proporciona, se asignará el rol por defecto.
     */
    private String rol;

    /**
     * Obtiene el nombre del usuario.
     *
     * @return nombre del usuario
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Establece el nombre del usuario.
     *
     * @param nombre nombre del usuario
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Obtiene el apellido del usuario.
     *
     * @return apellido del usuario
     */
    public String getApellido() {
        return apellido;
    }

    /**
     * Establece el apellido del usuario.
     *
     * @param apellido apellido del usuario
     */
    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    /**
     * Obtiene el correo electrónico del usuario.
     *
     * @return correo electrónico
     */
    public String getCorreoUsuario() {
        return correoUsuario;
    }

    /**
     * Establece el correo electrónico del usuario.
     *
     * @param correoUsuario correo electrónico
     */
    public void setCorreoUsuario(String correoUsuario) {
        this.correoUsuario = correoUsuario;
    }

    /**
     * Obtiene la contraseña del usuario.
     *
     * @return contraseña del usuario
     */
    public String getContrasena() {
        return contrasena;
    }

    /**
     * Establece la contraseña del usuario.
     *
     * @param contrasena contraseña del usuario
     */
    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    /**
     * Obtiene el rol asignado al usuario.
     *
     * @return rol del usuario
     */
    public String getRol() {
        return rol;
    }

    /**
     * Establece el rol asignado al usuario.
     *
     * @param rol rol del usuario
     */
    public void setRol(String rol) {
        this.rol = rol;
    }
}