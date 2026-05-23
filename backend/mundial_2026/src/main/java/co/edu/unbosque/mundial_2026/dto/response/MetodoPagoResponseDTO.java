package co.edu.unbosque.mundial_2026.dto.response;

/**
 * DTO de respuesta para los metodos de pago
 * Contiene informacion del metodo de pago del usuario
 */
public class MetodoPagoResponseDTO {

    /**
     * Identificador del metodo de pago
     */
    private String id;

    /**
     * Identificador del usuario
     */
    private String userId;

    /**
     * Tipo de metodo de pago
     */
    private String type;

    /**
     * Nombre o etiqueta del metodo de pago
     */
    private String label;

    /**
     * Detalles del metodo de pago
     */
    private String details;

    /**
     * Indica si el metodo de pago es predeterminado
     */
    private boolean isDefault;

    /**
     * Fecha de creacion del metodo de pago
     */
    private String createdAt;

    /**
     * Constructor vacio de la clase
     */
    public MetodoPagoResponseDTO() {
        //Constructor vacio
    }

    /**
     * Obtiene el identificador del metodo de pago
     *
     * @return identificador del metodo de pago
     */
    public String getId() {
        return id;
    }

    /**
     * Establece el identificador del metodo de pago
     *
     * @param id identificador del metodo de pago
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Obtiene el identificador del usuario
     *
     * @return identificador del usuario
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Establece el identificador del usuario
     *
     * @param userId identificador del usuario
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * Obtiene el tipo de metodo de pago
     *
     * @return tipo de metodo de pago
     */
    public String getType() {
        return type;
    }

    /**
     * Establece el tipo de metodo de pago
     *
     * @param type tipo de metodo de pago
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Obtiene el nombre del metodo de pago
     *
     * @return nombre del metodo de pago
     */
    public String getLabel() {
        return label;
    }

    /**
     * Establece el nombre del metodo de pago
     *
     * @param label nombre del metodo de pago
     */
    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * Obtiene los detalles del metodo de pago
     *
     * @return detalles del metodo de pago
     */
    public String getDetails() {
        return details;
    }

    /**
     * Establece los detalles del metodo de pago
     *
     * @param details detalles del metodo de pago
     */
    public void setDetails(String details) {
        this.details = details;
    }

    /**
     * Indica si el metodo de pago es predeterminado
     *
     * @return true si es predeterminado
     */
    public boolean isDefault() {
        return isDefault;
    }

    /**
     * Establece si el metodo de pago es predeterminado
     *
     * @param isDefault valor del estado predeterminado
     */
    public void setDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }

    /**
     * Obtiene la fecha de creacion
     *
     * @return fecha de creacion
     */
    public String getCreatedAt() {
        return createdAt;
    }

    /**
     * Establece la fecha de creacion
     *
     * @param createdAt fecha de creacion
     */
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}