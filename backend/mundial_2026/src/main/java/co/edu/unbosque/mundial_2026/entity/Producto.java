package co.edu.unbosque.mundial_2026.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

/**
 * Entidad que representa un producto disponible en el sistema.
 */
@Entity
@Table(name = "productos")
public class Producto {

    /**
     * Identificador único del producto.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Nombre del producto.
     */
    @Column(nullable = false, length = 100)
    private String nombre;

    /**
     * Descripción detallada del producto.
     */
    @Column(length = 500)
    private String descripcion;

    /**
     * Precio base del producto.
     */
    @Column(nullable = false)
    private Double precio;

    /**
     * URL de la imagen representativa del producto.
     */
    @Column(name = "imagen_url", length = 500)
    private String imagenUrl;

    /**
     * Indica si el producto está activo y disponible.
     */
    @Column(nullable = false)
    private Boolean activo;

    /**
     * Código único asociado al producto.
     */
    @Column(name = "codigo_producto", unique = true, length = 50)
    private String codigoProducto;

    /**
     * Equipo relacionado con el producto.
     */
    @Column(name = "equipo", length = 60)
    private String equipo;

    /**
     * Bandera o símbolo representativo del producto.
     */
    @Column(name = "bandera", length = 10)
    private String bandera;

    /**
     * Indica si el producto está marcado como destacado.
     * Su valor inicial es false.
     */
    @Column(name = "destacado", nullable = false)
    private Boolean destacado = false;

    /**
     * Categoría asociada al producto.
     * Se carga de forma lazy, por lo que Hibernate
     * solo recupera la categoría cuando se accede a ella.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id", nullable = false)
    private Categoria categoria;

    /**
     * Variantes disponibles del producto.
     * Se cargan de forma lazy para evitar traerlas
     * inmediatamente desde la base de datos.
     */
    @OneToMany(mappedBy = "producto", fetch = FetchType.LAZY)
    private List<VarianteProducto> variantes = new ArrayList<>();

    /**
     * Constructor vacío.
     */
    public Producto() {
        // Constructor (Comentario que requiere sonarcloud)
    }

    /**
     * Retorna la lista de variantes del producto.
     *
     * @return lista de variantes
     */
    public List<VarianteProducto> getVariantes() {
        return variantes;
    }

    /**
     * Define la lista de variantes del producto.
     *
     * @param variantes nueva lista de variantes
     */
    public void setVariantes(List<VarianteProducto> variantes) {
        this.variantes = variantes;
    }

    /**
     * Retorna el identificador del producto.
     *
     * @return id del producto
     */
    public Long getId() {
        return id;
    }

    /**
     * Define el identificador del producto.
     *
     * @param id nuevo identificador
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Retorna el nombre del producto.
     *
     * @return nombre del producto
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Define el nombre del producto.
     *
     * @param nombre nuevo nombre
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Retorna la descripción del producto.
     *
     * @return descripción del producto
     */
    public String getDescripcion() {
        return descripcion;
    }

    /**
     * Define la descripción del producto.
     *
     * @param descripcion nueva descripción
     */
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    /**
     * Retorna el precio del producto.
     *
     * @return precio actual
     */
    public Double getPrecio() {
        return precio;
    }

    /**
     * Define el precio del producto.
     *
     * @param precio nuevo precio
     */
    public void setPrecio(Double precio) {
        this.precio = precio;
    }

    /**
     * Retorna la URL de la imagen.
     *
     * @return URL de la imagen
     */
    public String getImagenUrl() {
        return imagenUrl;
    }

    /**
     * Define la URL de la imagen.
     *
     * @param imagenUrl nueva URL
     */
    public void setImagenUrl(String imagenUrl) {
        this.imagenUrl = imagenUrl;
    }

    /**
     * Retorna si el producto está activo.
     *
     * @return true si está activo, false en caso contrario
     */
    public Boolean getActivo() {
        return activo;
    }

    /**
     * Define si el producto está activo.
     *
     * @param activo nuevo estado
     */
    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    /**
     * Retorna el código único del producto.
     *
     * @return código del producto
     */
    public String getCodigoProducto() {
        return codigoProducto;
    }

    /**
     * Define el código único del producto.
     *
     * @param codigoProducto nuevo código
     */
    public void setCodigoProducto(String codigoProducto) {
        this.codigoProducto = codigoProducto;
    }

    /**
     * Retorna el equipo asociado al producto.
     *
     * @return equipo relacionado
     */
    public String getEquipo() {
        return equipo;
    }

    /**
     * Define el equipo asociado al producto.
     *
     * @param equipo nuevo equipo
     */
    public void setEquipo(String equipo) {
        this.equipo = equipo;
    }

    /**
     * Retorna la bandera asociada al producto.
     *
     * @return bandera o símbolo
     */
    public String getBandera() {
        return bandera;
    }

    /**
     * Define la bandera asociada al producto.
     *
     * @param bandera nueva bandera
     */
    public void setBandera(String bandera) {
        this.bandera = bandera;
    }

    /**
     * Retorna si el producto está destacado.
     *
     * @return true si está destacado, false en caso contrario
     */
    public Boolean getDestacado() {
        return destacado;
    }

    /**
     * Define si el producto está destacado.
     *
     * @param destacado nuevo estado
     */
    public void setDestacado(Boolean destacado) {
        this.destacado = destacado;
    }

    /**
     * Retorna la categoría asociada.
     *
     * @return categoría del producto
     */
    public Categoria getCategoria() {
        return categoria;
    }

    /**
     * Define la categoría asociada.
     *
     * @param categoria nueva categoría
     */
    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    /**
     * Calcula el stock total sumando el stock
     * de todas las variantes del producto.
     *
     * @return stock total disponible
     */
    public Integer getStock() {
        return variantes.stream().mapToInt(VarianteProducto::getStock).sum();
    }
}