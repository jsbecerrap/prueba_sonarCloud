package co.edu.unbosque.mundial_2026.entity;

import java.util.List;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

/**
 * Entidad que representa una selección nacional dentro del sistema.
 * Una selección puede estar asociada a múltiples usuarios
 * y un usuario puede tener varias selecciones favoritas.
 */
@Entity
@Table(name = "selecciones")
public class Seleccion {

    /**
     * Identificador único de la selección.
     */
    @Id
    private Long id;

    /**
     * Nombre único de la selección.
     */
    @Column(name = "nombre", unique = true, nullable = false)
    private String nombre;

    /**
     * Lista de usuarios asociados a esta selección.
     * Relación muchos a muchos gestionada desde la entidad Usuario.
     */
    @ManyToMany(mappedBy = "seleccionesU")
    private List<Usuario> usuarios;

    /**
     * Constructor vacío.
     */
    public Seleccion() {
        // Constructor vacío
    }

    /**
     * Retorna el identificador de la selección.
     *
     * @return id de la selección
     */
    public Long getId() {
        return id;
    }

    /**
     * Define el identificador de la selección.
     *
     * @param id nuevo identificador
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Retorna el nombre de la selección.
     *
     * @return nombre de la selección
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Define el nombre de la selección.
     *
     * @param nombre nuevo nombre
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Retorna la lista de usuarios asociados.
     *
     * @return lista de usuarios
     */
    public List<Usuario> getUsuarios() {
        return usuarios;
    }

    /**
     * Define la lista de usuarios asociados.
     *
     * @param usuarios nueva lista de usuarios
     */
    public void setUsuarios(List<Usuario> usuarios) {
        this.usuarios = usuarios;
    }

    /**
     * Compara si dos selecciones son iguales
     * basándose en su identificador.
     *
     * @param o objeto a comparar
     * @return true si son iguales, false en caso contrario
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        return (o instanceof Seleccion seleccion) && Objects.equals(id, seleccion.id);
    }

    /**
     * Genera el hash code basado en el identificador.
     *
     * @return hash code de la selección
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}