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
 * Entidad que representa una variante específica de un producto.
 * Una variante puede diferenciarse por talla, medida, color
 * u otra característica específica.
 */
@Entity
@Table(name = "variantes_producto")
public class VarianteProducto {

    /**
     * Identificador único de la variante.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Producto al que pertenece esta variante.
     * Se carga de forma lazy para optimizar consultas.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    /**
     * Especificación de la variante.
     * Ejemplos: "S", "M", "38", "25 cm".
     * Puede ser null si el producto no tiene variantes específicas.
     */
    @Column(name = "especificacion")
    private String especificacion;

    /**
     * Cantidad disponible en inventario para esta variante.
     */
    @Column(nullable = false)
    private Integer stock;

    /**
     * Constructor vacío requerido por JPA.
     */
    public VarianteProducto() {
        // Constructor (Comentario que requiere SonarCloud)
    }

    /**
     * Retorna el identificador de la variante.
     *
     * @return id de la variante
     */
    public Long getId() {
        return id;
    }

    /**
     * Define el identificador de la variante.
     *
     * @param id nuevo identificador
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Retorna el producto asociado a esta variante.
     *
     * @return producto de la variante
     */
    public Producto getProducto() {
        return producto;
    }

    /**
     * Define el producto asociado a esta variante.
     *
     * @param producto nuevo producto
     */
    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    /**
     * Retorna la especificación de la variante.
     *
     * @return especificación (talla, medida, etc.)
     */
    public String getEspecificacion() {
        return especificacion;
    }

    /**
     * Define la especificación de la variante.
     *
     * @param especificacion nueva especificación
     */
    public void setEspecificacion(String especificacion) {
        this.especificacion = especificacion;
    }

    /**
     * Retorna la cantidad disponible en stock.
     *
     * @return stock disponible
     */
    public Integer getStock() {
        return stock;
    }

    /**
     * Define la cantidad disponible en stock.
     *
     * @param stock nueva cantidad
     */
    public void setStock(Integer stock) {
        this.stock = stock;
    }
}