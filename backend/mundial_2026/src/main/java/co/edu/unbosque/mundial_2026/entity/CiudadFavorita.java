package co.edu.unbosque.mundial_2026.entity;

import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

/**
 * Representa una ciudad favorita
 */
@Entity
@Table(name = "ciudades_favoritas")
public class CiudadFavorita {

    /**
     * Identificador de la ciudad
     */
    @Id
    private Long id;

    /**
     * Nombre de la ciudad
     */
    private String nombre;

    /**
     * País de la ciudad
     */
    private String pais;

    /**
     * Usuarios asociados a la ciudad
     */
    @ManyToMany(mappedBy = "ciudadFavoritas")
    private List<Usuario> usuarios;

    /**
     * Crea una ciudad favorita
     */
    public CiudadFavorita() {
        // Constructor vacio
    }

    /**
     * Obtiene el identificador de la ciudad
     *
     * @return identificador de la ciudad
     */
    public Long getId() {
        return id;
    }

    /**
     * Asigna el identificador de la ciudad
     *
     * @param id identificador de la ciudad
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Obtiene el nombre de la ciudad
     *
     * @return nombre de la ciudad
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Asigna el nombre de la ciudad
     *
     * @param nombre nombre de la ciudad
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Obtiene el país de la ciudad
     *
     * @return país de la ciudad
     */
    public String getPais() {
        return pais;
    }

    /**
     * Asigna el país de la ciudad
     *
     * @param pais país de la ciudad
     */
    public void setPais(String pais) {
        this.pais = pais;
    }

    /**
     * Obtiene los usuarios asociados
     *
     * @return usuarios asociados
     */
    public List<Usuario> getUsuarios() {
        return usuarios;
    }

    /**
     * Asigna los usuarios asociados
     *
     * @param usuarios usuarios asociados
     */
    public void setUsuarios(List<Usuario> usuarios) {
        this.usuarios = usuarios;
    }

    /**
     * Compara dos ciudades favoritas
     *
     * @param o objeto a comparar
     * @return true si son iguales
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CiudadFavorita ciudad = (CiudadFavorita) o;
        return id != null && id.equals(ciudad.id);
    }

    /**
     * Obtiene el código hash
     *
     * @return código hash
     */
    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}