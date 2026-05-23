package co.edu.unbosque.mundial_2026.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * Entidad que representa un método de pago registrado por un usuario.
 * Contiene la información básica del método, su identificación
 * y si está marcado como predeterminado.
 */
@Entity
@Table(name = "metodos_pago")
public class MetodoPago {

    /**
     * Identificador único del método de pago.
     * Se genera automáticamente en la base de datos.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Usuario propietario del método de pago.
     * Se relaciona mediante la clave foránea usuario_id.
     */
    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    /**
     * Tipo de método de pago.
     * Es un campo obligatorio con un máximo de 20 caracteres.
     */
    @Column(nullable = false, length = 20)
    private String tipo;

    /**
     * Nombre o etiqueta identificadora del método de pago.
     * Es un campo obligatorio con un máximo de 40 caracteres.
     */
    @Column(nullable = false, length = 40)
    private String label;

    /**
     * Detalles adicionales del método de pago.
     * Permite almacenar hasta 40 caracteres.
     */
    @Column(length = 40)
    private String details;

    /**
     * Indica si este es el método de pago predeterminado.
     */
    @Column(name = "is_default")
    private boolean isDefault;

    /**
     * Fecha de creación del método de pago.
     * Se almacena en la columna created_at
     * y es un campo obligatorio con un máximo de 40 caracteres.
     */
    @Column(name = "created_at", nullable = false, length = 40)
    private String createdAt;

    /**
     * Constructor vacío requerido por JPA.
     */
    public MetodoPago() {
        // Constructor vacío
    }

    /**
     * Obtiene el identificador del método de pago.
     *
     * @return identificador del método de pago.
     */
    public Long getId() {
        return id;
    }

    /**
     * Asigna el identificador del método de pago.
     *
     * @param id nuevo identificador.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Obtiene el usuario propietario.
     *
     * @return usuario asociado.
     */
    public Usuario getUsuario() {
        return usuario;
    }

    /**
     * Asigna el usuario propietario.
     *
     * @param usuario nuevo usuario asociado.
     */
    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    /**
     * Obtiene el tipo de método de pago.
     *
     * @return tipo del método de pago.
     */
    public String getTipo() {
        return tipo;
    }

    /**
     * Asigna el tipo de método de pago.
     *
     * @param tipo nuevo tipo.
     */
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    /**
     * Obtiene la etiqueta identificadora del método de pago.
     *
     * @return etiqueta del método de pago.
     */
    public String getLabel() {
        return label;
    }

    /**
     * Asigna la etiqueta identificadora del método de pago.
     *
     * @param label nueva etiqueta.
     */
    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * Obtiene los detalles adicionales del método de pago.
     *
     * @return detalles del método de pago.
     */
    public String getDetails() {
        return details;
    }

    /**
     * Asigna los detalles adicionales del método de pago.
     *
     * @param details nuevos detalles.
     */
    public void setDetails(String details) {
        this.details = details;
    }

    /**
     * Indica si el método de pago es el predeterminado.
     *
     * @return true si es predeterminado, false en caso contrario.
     */
    public boolean isDefault() {
        return isDefault;
    }

    /**
     * Define si el método de pago será predeterminado.
     *
     * @param isDefault nuevo estado predeterminado.
     */
    public void setDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }

    /**
     * Obtiene la fecha de creación del método de pago.
     *
     * @return fecha de creación.
     */
    public String getCreatedAt() {
        return createdAt;
    }

    /**
     * Asigna la fecha de creación del método de pago.
     *
     * @param createdAt nueva fecha de creación.
     */
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}