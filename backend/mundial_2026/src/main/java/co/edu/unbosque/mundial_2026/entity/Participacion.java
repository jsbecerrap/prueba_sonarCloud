package co.edu.unbosque.mundial_2026.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

/**
 * Entidad que representa la participación de un usuario en una apuesta.
 * La restricción única evita que un mismo usuario participe
 * más de una vez en la misma apuesta.
 */
@Entity
@Table(name = "participaciones", uniqueConstraints = @UniqueConstraint(columnNames = { "usuario_id", "apuesta_id" }))
public class Participacion {

    /**
     * Identificador único de la participación.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Usuario asociado a la participación.
     */
    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    /**
     * Apuesta asociada a la participación.
     */
    @ManyToOne
    @JoinColumn(name = "apuesta_id", nullable = false)
    private Apuesta apuesta;

    /**
     * Puntos acumulados por el usuario en la apuesta.
     * Su valor inicial es 0.
     */
    @Column(nullable = false)
    private Integer puntos = 0;

    /**
     * Posición del usuario dentro del ranking de la apuesta.
     */
    @Column(name = "posicion_ranking")
    private Integer posicionRanking;

    /**
     * Constructor vacío.
     */
    public Participacion() {
        // Constructor vacio
    }

    /**
     * Retorna el identificador de la participación.
     *
     * @return id de la participación
     */
    public Long getId() {
        return id;
    }

    /**
     * Define el identificador de la participación.
     *
     * @param id nuevo identificador
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Retorna el usuario asociado.
     *
     * @return usuario de la participación
     */
    public Usuario getUsuario() {
        return usuario;
    }

    /**
     * Define el usuario asociado.
     *
     * @param usuario nuevo usuario
     */
    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    /**
     * Retorna la apuesta asociada.
     *
     * @return apuesta de la participación
     */
    public Apuesta getApuesta() {
        return apuesta;
    }

    /**
     * Define la apuesta asociada.
     *
     * @param apuesta nueva apuesta
     */
    public void setApuesta(Apuesta apuesta) {
        this.apuesta = apuesta;
    }

    /**
     * Retorna los puntos acumulados.
     *
     * @return cantidad de puntos
     */
    public Integer getPuntos() {
        return puntos;
    }

    /**
     * Define los puntos acumulados.
     *
     * @param puntos nueva cantidad de puntos
     */
    public void setPuntos(Integer puntos) {
        this.puntos = puntos;
    }

    /**
     * Retorna la posición en el ranking.
     *
     * @return posición actual en el ranking
     */
    public Integer getPosicionRanking() {
        return posicionRanking;
    }

    /**
     * Define la posición en el ranking.
     *
     * @param posicionRanking nueva posición
     */
    public void setPosicionRanking(Integer posicionRanking) {
        this.posicionRanking = posicionRanking;
    }
}