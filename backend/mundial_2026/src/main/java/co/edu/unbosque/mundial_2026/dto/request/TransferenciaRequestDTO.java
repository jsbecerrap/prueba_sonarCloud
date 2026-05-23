package co.edu.unbosque.mundial_2026.dto.request;

/**
 * DTO de solicitud para transferir un método de pago entre usuarios.
 * <p>
 * Contiene el correo electrónico del usuario destinatario que recibirá
 * el método de pago transferido.
 * </p>
 */
public class TransferenciaRequestDTO {

    /** Correo electrónico del usuario al que se transfiere el método de pago. */
    private String correoDestino;

    /**
     * Constructor vacío requerido por frameworks de serialización.
     */
    public TransferenciaRequestDTO() {
        //Constructor vacio
    }

    /**
     * Obtiene el correo electrónico del usuario destinatario.
     *
     * @return correo del usuario destinatario
     */
    public String getCorreoDestino() {
        return correoDestino;
    }

    /**
     * Establece el correo electrónico del usuario destinatario.
     *
     * @param correoDestino correo del usuario destinatario
     */
    public void setCorreoDestino(String correoDestino) {
        this.correoDestino = correoDestino;
    }
}