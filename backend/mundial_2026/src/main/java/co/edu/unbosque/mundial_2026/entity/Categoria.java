package co.edu.unbosque.mundial_2026.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Representa una categoría
 */
@Entity
@Table(name = "categorias")
public class Categoria {

    /**
     * Identificador de la categoría
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Nombre de la categoría
     */
    @Column(nullable = false, unique = true, length = 60)
    private String nombre;

    /**
     * Descripción de la categoría
     */
    @Column(length = 250)
    private String descripcion;

    /**
     * Estado de la categoría
     */
    @Column(nullable = false)
    private Boolean activo = true;

    /**
     * Crea una categoría
     */
    public Categoria() {
          //Constructor(Comentario que requiere sonarcloud)
    }

    /**
     * Obtiene el estado de la categoría
     *
     * @return estado de la categoría
     */
    public Boolean getActivo() {
        return activo;
    }

    /**
     * Asigna el estado de la categoría
     *
     * @param activo estado de la categoría
     */
    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    /**
     * Obtiene el identificador de la categoría
     *
     * @return identificador de la categoría
     */
    public Long getId() {
        return id;
    }

    /**
     * Asigna el identificador de la categoría
     *
     * @param id identificador de la categoría
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Obtiene el nombre de la categoría
     *
     * @return nombre de la categoría
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Asigna el nombre de la categoría
     *
     * @param nombre nombre de la categoría
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Obtiene la descripción de la categoría
     *
     * @return descripción de la categoría
     */
    public String getDescripcion() {
        return descripcion;
    }

    /**
     * Asigna la descripción de la categoría
     *
     * @param descripcion descripción de la categoría
     */
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}