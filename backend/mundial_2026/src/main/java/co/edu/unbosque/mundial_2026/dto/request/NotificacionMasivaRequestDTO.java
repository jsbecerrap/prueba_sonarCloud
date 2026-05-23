package co.edu.unbosque.mundial_2026.dto.request;

import java.util.List;

/**
 * DTO de solicitud para enviar una notificación masiva a múltiples usuarios.
 * <p>
 * Permite especificar el contenido y el canal de la notificación, junto con
 * la lista de usuarios destinatarios.
 * </p>
 */
public class NotificacionMasivaRequestDTO {

    /** Tipo de notificación que se enviará (por ejemplo: SISTEMA, APUESTA, ORDEN). */
    private String tipo;

    /** Título corto de la notificación que se mostrará a los usuarios. */
    private String titulo;

    /** Contenido del mensaje de la notificación. */
    private String mensaje;

    /** Canal por el que se enviará la notificación (por ejemplo: EMAIL, PUSH, IN_APP). */
    private String canal;

    /** Lista de identificadores de los usuarios que recibirán la notificación. */
    private List<Long> usuarioIds;

    /**
     * Constructor vacío requerido por frameworks de serialización.
     */
    public NotificacionMasivaRequestDTO() {
        //Constructor vacio
    }

    /**
     * Obtiene el tipo de notificación.
     *
     * @return tipo de notificación
     */
    public String getTipo() {
        return tipo;
    }

    /**
     * Establece el tipo de notificación.
     *
     * @param tipo tipo de notificación
     */
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    /**
     * Obtiene el título de la notificación.
     *
     * @return título de la notificación
     */
    public String getTitulo() {
        return titulo;
    }

    /**
     * Establece el título de la notificación.
     *
     * @param titulo título de la notificación
     */
    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    /**
     * Obtiene el mensaje de la notificación.
     *
     * @return mensaje de la notificación
     */
    public String getMensaje() {
        return mensaje;
    }

    /**
     * Establece el mensaje de la notificación.
     *
     * @param mensaje mensaje de la notificación
     */
    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    /**
     * Obtiene el canal de envío de la notificación.
     *
     * @return canal de envío
     */
    public String getCanal() {
        return canal;
    }

    /**
     * Establece el canal de envío de la notificación.
     *
     * @param canal canal de envío
     */
    public void setCanal(String canal) {
        this.canal = canal;
    }

    /**
     * Obtiene la lista de IDs de los usuarios destinatarios.
     *
     * @return lista de IDs de usuarios
     */
    public List<Long> getUsuarioIds() {
        return usuarioIds;
    }

    /**
     * Establece la lista de IDs de los usuarios destinatarios.
     *
     * @param usuarioIds lista de IDs de usuarios
     */
    public void setUsuarioIds(List<Long> usuarioIds) {
        this.usuarioIds = usuarioIds;
    }
}