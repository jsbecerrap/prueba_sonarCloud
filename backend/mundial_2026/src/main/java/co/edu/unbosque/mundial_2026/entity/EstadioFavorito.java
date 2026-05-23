package co.edu.unbosque.mundial_2026.entity;

import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * Representa un estadio favorito
 */
@Entity
@Table(name = "estadiosFavoritos")
public class EstadioFavorito {

    /**
     * Identificador del estadio
     */
    @Id
    private Long id;

    /**
     * Nombre del estadio
     */
    private String nombre;

    /**
     * Ciudad asociada al estadio
     *
     * Relación de muchos a uno
     * Relación mediante la columna ciudad_id
     */
    @ManyToOne
    @JoinColumn(name = "ciudad_id")
    private CiudadFavorita ciudad;

    /**
     * Usuarios asociados al estadio
     *
     * Relación de muchos a muchos
     * Relación inversa definida por preferenciasu
     */
    @ManyToMany(mappedBy = "preferenciasu")
    private List<Usuario> usuarios;

    /**
     * Crea un estadio favorito
     */
    public EstadioFavorito() {
        // Constructor vacio
    }

    /**
     * Obtiene el identificador del estadio
     *
     * @return identificador del estadio
     */
    public Long getId() {
        return id;
    }

    /**
     * Asigna el identificador del estadio
     *
     * @param id identificador del estadio
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Obtiene el nombre del estadio
     *
     * @return nombre del estadio
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Asigna el nombre del estadio
     *
     * @param nombre nombre del estadio
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Obtiene la ciudad asociada
     *
     * @return ciudad asociada
     */
    public CiudadFavorita getCiudad() {
        return ciudad;
    }

    /**
     * Asigna la ciudad asociada
     *
     * @param ciudad ciudad asociada
     */
    public void setCiudad(CiudadFavorita ciudad) {
        this.ciudad = ciudad;
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
     * Compara dos estadios favoritos
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
        EstadioFavorito estadio = (EstadioFavorito) o;
        return id != null && id.equals(estadio.id);
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