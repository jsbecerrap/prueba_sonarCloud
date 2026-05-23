package co.edu.unbosque.mundial_2026.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * Entidad que representa un ítem de una orden.
 * Contiene la información del producto, su variante,
 * la cantidad solicitada y el precio unitario registrado.
 * 
 * Usa actualización dinámica para modificar únicamente
 * los campos que cambian en la base de datos.
 */
@Entity
@Table(name = "items_orden")
@org.hibernate.annotations.DynamicUpdate
public class ItemOrden {

    /**
     * Identificador único del ítem.
     * Se genera automáticamente en la base de datos.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Orden asociada al ítem.
     * La relación se carga de forma diferida (LAZY)
     * y se referencia mediante la clave foránea orden_id.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orden_id", nullable = false)
    private Orden orden;

    /**
     * Producto asociado al ítem.
     * La relación se carga de forma diferida (LAZY)
     * y se referencia mediante la clave foránea producto_id.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    /**
     * Variante del producto asociada al ítem.
     * La relación se carga de forma diferida (LAZY)
     * y se referencia mediante la clave foránea variante_id.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "variante_id", nullable = false)
    private VarianteProducto variante;

    /**
     * Cantidad de unidades registradas en el ítem.
     * Es un campo obligatorio.
     */
    @Column(nullable = false)
    private Integer cantidad;

    /**
     * Precio unitario registrado para el producto.
     * Se almacena en la columna precio_unitario
     * y es un campo obligatorio.
     */
    @Column(name = "precio_unitario", nullable = false)
    private Double precioUnitario;

    /**
     * Constructor vacío requerido por JPA.
     */
    public ItemOrden() {
        // Constructor vacío
    }

    /**
     * Obtiene el identificador del ítem.
     *
     * @return identificador del ítem.
     */
    public Long getId() {
        return id;
    }

    /**
     * Asigna el identificador del ítem.
     *
     * @param id nuevo identificador.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Obtiene la orden asociada.
     *
     * @return orden correspondiente.
     */
    public Orden getOrden() {
        return orden;
    }

    /**
     * Asigna la orden asociada.
     *
     * @param orden nueva orden.
     */
    public void setOrden(Orden orden) {
        this.orden = orden;
    }

    /**
     * Obtiene el producto asociado.
     *
     * @return producto correspondiente.
     */
    public Producto getProducto() {
        return producto;
    }

    /**
     * Asigna el producto asociado.
     *
     * @param producto nuevo producto.
     */
    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    /**
     * Obtiene la variante del producto.
     *
     * @return variante correspondiente.
     */
    public VarianteProducto getVariante() {
        return variante;
    }

    /**
     * Asigna la variante del producto.
     *
     * @param variante nueva variante.
     */
    public void setVariante(VarianteProducto variante) {
        this.variante = variante;
    }

    /**
     * Obtiene la cantidad registrada.
     *
     * @return cantidad del ítem.
     */
    public Integer getCantidad() {
        return cantidad;
    }

    /**
     * Asigna la cantidad del ítem.
     *
     * @param cantidad nueva cantidad.
     */
    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    /**
     * Obtiene el precio unitario registrado.
     *
     * @return precio unitario del ítem.
     */
    public Double getPrecioUnitario() {
        return precioUnitario;
    }

    /**
     * Asigna el precio unitario.
     *
     * @param precioUnitario nuevo precio unitario.
     */
    public void setPrecioUnitario(Double precioUnitario) {
        this.precioUnitario = precioUnitario;
    }
}