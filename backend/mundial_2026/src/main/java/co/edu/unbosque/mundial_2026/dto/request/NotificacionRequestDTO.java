package co.edu.unbosque.mundial_2026.dto.request;

/**
 * DTO de solicitud para enviar una notificación individual a un usuario.
 * <p>
 * Permite especificar el contenido de la notificación, el canal de envío
 * y el usuario destinatario.
 * </p>
 */
public class NotificacionRequestDTO {

    /** Tipo de notificación (por ejemplo: SISTEMA, APUESTA, ORDEN). */
    private String tipo;

    /** Título corto de la notificación. */
    private String titulo;

    /** Contenido del mensaje de la notificación. */
    private String mensaje;

    /** Canal por el que se enviará la notificación (por ejemplo: EMAIL, PUSH, IN_APP). */
    private String canal;

    /** Identificador del usuario destinatario de la notificación. */
    private Long usuarioId;

    /**
     * Constructor vacío requerido por frameworks de serialización.
     */
    public NotificacionRequestDTO() {
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
     * Obtiene el ID del usuario destinatario.
     *
     * @return ID del usuario
     */
    public Long getUsuarioId() {
        return usuarioId;
    }

    /**
     * Establece el ID del usuario destinatario.
     *
     * @param usuarioId ID del usuario
     */
    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }
}