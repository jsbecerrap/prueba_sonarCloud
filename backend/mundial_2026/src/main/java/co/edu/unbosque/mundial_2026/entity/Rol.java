package co.edu.unbosque.mundial_2026.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Entidad que representa un rol dentro del sistema.
 * Un rol define los permisos o tipo de usuario
 * (por ejemplo: ADMIN, USUARIO).
 */
@Entity
@Table(name = "roles")
public class Rol {

    /**
     * Identificador único del rol.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Nombre único del rol.
     */
    @Column(name = "nombre", unique = true, nullable = false)
    private String nombre;

    /**
     * Constructor vacío.
     */
    public Rol() {
        // Constructor vacío
    }

    /**
     * Retorna el identificador del rol.
     *
     * @return id del rol
     */
    public Long getId() {
        return id;
    }

    /**
     * Define el identificador del rol.
     *
     * @param id nuevo identificador
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Retorna el nombre del rol.
     *
     * @return nombre del rol
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Define el nombre del rol.
     *
     * @param nombre nuevo nombre del rol
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}