package co.edu.unbosque.mundial_2026.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * DTO de solicitud para actualizar los datos de perfil de un usuario existente.
 * <p>
 * Todos los campos son opcionales; solo se actualizarán aquellos que se envíen
 * con un valor no nulo o no vacío. Incluye validaciones de formato para
 * nombre, apellido, correo y contraseñas.
 * </p>
 */
public class UsuarioActualizarRequestDTO {

    /**
     * Nuevo nombre del usuario. Si se proporciona, debe tener entre 2 y 50 caracteres
     * y solo puede contener letras, espacios, apóstrofes o guiones.
     */
    @Size(min = 2, max = 50, message = "El nombre debe tener entre 2 y 50 caracteres")
    @Pattern(
        regexp = "^[A-Za-zÁÉÍÓÚáéíóúÑñ\\s'-]*$",
        message = "El nombre solo puede tener letras, espacios, apóstrofes o guiones"
    )
    private String nombre;

    /**
     * Nuevo apellido del usuario. Si se proporciona, debe tener entre 2 y 50 caracteres
     * y solo puede contener letras, espacios, apóstrofes o guiones.
     */
    @Size(min = 2, max = 50, message = "El apellido debe tener entre 2 y 50 caracteres")
    @Pattern(
        regexp = "^[A-Za-zÁÉÍÓÚáéíóúÑñ\\s'-]*$",
        message = "El apellido solo puede tener letras, espacios, apóstrofes o guiones"
    )
    private String apellido;

    /**
     * Nuevo correo electrónico del usuario. Debe tener un formato de correo válido
     * y no puede superar los 100 caracteres.
     */
    @Email(message = "El correo nuevo no es válido")
    @Size(max = 100, message = "El correo no puede tener más de 100 caracteres")
    private String correoNuevo;

    /**
     * Contraseña actual del usuario. Requerida para confirmar la identidad
     * antes de realizar cambios sensibles. Máximo 72 caracteres.
     */
    @Size(max = 72, message = "La contraseña actual no puede tener más de 72 caracteres")
    private String contrasenaActual;

    /**
     * Nueva contraseña del usuario. Debe tener entre 8 y 72 caracteres y cumplir
     * los requisitos de seguridad: al menos una mayúscula, una minúscula, un número
     * y un símbolo especial.
     */
    @Size(min = 8, max = 72, message = "La contraseña debe tener entre 8 y 72 caracteres")
    @Pattern(regexp = "^(?=.*[A-Z]).+$", message = "La contraseña debe tener al menos una letra mayúscula")
    @Pattern(regexp = "^(?=.*[a-z]).+$", message = "La contraseña debe tener al menos una letra minúscula")
    @Pattern(regexp = "^(?=.*\\d).+$", message = "La contraseña debe tener al menos un número")
    @Pattern(regexp = "^(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]).+$", message = "La contraseña debe tener al menos un símbolo")
    private String contrasenaNueva;

    /**
     * Obtiene el nuevo nombre del usuario.
     *
     * @return nombre del usuario
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Establece el nuevo nombre del usuario.
     *
     * @param nombre nombre del usuario
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Obtiene el nuevo apellido del usuario.
     *
     * @return apellido del usuario
     */
    public String getApellido() {
        return apellido;
    }

    /**
     * Establece el nuevo apellido del usuario.
     *
     * @param apellido apellido del usuario
     */
    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    /**
     * Obtiene el nuevo correo electrónico del usuario.
     *
     * @return correo electrónico nuevo
     */
    public String getCorreoNuevo() {
        return correoNuevo;
    }

    /**
     * Establece el nuevo correo electrónico del usuario.
     *
     * @param correoNuevo correo electrónico nuevo
     */
    public void setCorreoNuevo(String correoNuevo) {
        this.correoNuevo = correoNuevo;
    }

    /**
     * Obtiene la contraseña actual del usuario.
     *
     * @return contraseña actual
     */
    public String getContrasenaActual() {
        return contrasenaActual;
    }

    /**
     * Establece la contraseña actual del usuario.
     *
     * @param contrasenaActual contraseña actual
     */
    public void setContrasenaActual(String contrasenaActual) {
        this.contrasenaActual = contrasenaActual;
    }

    /**
     * Obtiene la nueva contraseña del usuario.
     *
     * @return nueva contraseña
     */
    public String getContrasenaNueva() {
        return contrasenaNueva;
    }

    /**
     * Establece la nueva contraseña del usuario.
     *
     * @param contrasenaNueva nueva contraseña
     */
    public void setContrasenaNueva(String contrasenaNueva) {
        this.contrasenaNueva = contrasenaNueva;
    }
}