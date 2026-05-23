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
 * Entidad que representa un pronóstico realizado por un usuario
 * sobre el resultado de un partido dentro de una apuesta.
 */
@Entity
@Table(name = "pronosticos")
public class Pronostico {

    /**
     * Identificador único del pronóstico.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Resultado pronosticado por el usuario
     * (ejemplo: LOCAL, EMPATE, VISITANTE).
     */
    @Column(name = "resultado_pronosticado", nullable = false, length = 20)
    private String resultadoPronosticado;

    /**
     * Cantidad de goles pronosticados para el equipo local.
     */
    @Column(name = "goles_local_pronosticados")
    private Integer golesLocalPronosticados;

    /**
     * Cantidad de goles pronosticados para el equipo visitante.
     */
    @Column(name = "goles_visitante_pronosticados")
    private Integer golesVisitantePronosticados;

    /**
     * Puntos obtenidos por el usuario según la precisión del pronóstico.
     */
    @Column(name = "puntos_obtenidos")
    private Integer puntosObtenidos;

    /**
     * Apuesta a la que pertenece este pronóstico.
     */
    @ManyToOne
    @JoinColumn(name = "apuesta_id", nullable = false)
    private Apuesta apuesta;

    /**
     * Usuario que realizó el pronóstico.
     */
    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    /**
     * Partido sobre el cual se realiza el pronóstico.
     */
    @ManyToOne
    @JoinColumn(name = "partido_id", nullable = false)
    private Partido partido;

    /**
     * Constructor vacío.
     */
    public Pronostico() {
        // Constructor (Comentario que requiere sonarcloud)
    }

    /**
     * Retorna el identificador del pronóstico.
     *
     * @return id del pronóstico
     */
    public Long getId() {
        return id;
    }

    /**
     * Define el identificador del pronóstico.
     *
     * @param id nuevo identificador
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Retorna el resultado pronosticado.
     *
     * @return resultado pronosticado
     */
    public String getResultadoPronosticado() {
        return resultadoPronosticado;
    }

    /**
     * Define el resultado pronosticado.
     *
     * @param r nuevo resultado pronosticado
     */
    public void setResultadoPronosticado(String r) {
        this.resultadoPronosticado = r;
    }

    /**
     * Retorna los goles pronosticados para el equipo local.
     *
     * @return goles del equipo local
     */
    public Integer getGolesLocalPronosticados() {
        return golesLocalPronosticados;
    }

    /**
     * Define los goles pronosticados para el equipo local.
     *
     * @param g nueva cantidad de goles
     */
    public void setGolesLocalPronosticados(Integer g) {
        this.golesLocalPronosticados = g;
    }

    /**
     * Retorna los goles pronosticados para el equipo visitante.
     *
     * @return goles del equipo visitante
     */
    public Integer getGolesVisitantePronosticados() {
        return golesVisitantePronosticados;
    }

    /**
     * Define los goles pronosticados para el equipo visitante.
     *
     * @param g nueva cantidad de goles
     */
    public void setGolesVisitantePronosticados(Integer g) {
        this.golesVisitantePronosticados = g;
    }

    /**
     * Retorna los puntos obtenidos por este pronóstico.
     *
     * @return puntos obtenidos
     */
    public Integer getPuntosObtenidos() {
        return puntosObtenidos;
    }

    /**
     * Define los puntos obtenidos por este pronóstico.
     *
     * @param p nueva cantidad de puntos
     */
    public void setPuntosObtenidos(Integer p) {
        this.puntosObtenidos = p;
    }

    /**
     * Retorna la apuesta asociada.
     *
     * @return apuesta del pronóstico
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
     * Retorna el usuario que realizó el pronóstico.
     *
     * @return usuario asociado
     */
    public Usuario getUsuario() {
        return usuario;
    }

    /**
     * Define el usuario que realizó el pronóstico.
     *
     * @param usuario nuevo usuario
     */
    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    /**
     * Retorna el partido asociado al pronóstico.
     *
     * @return partido pronosticado
     */
    public Partido getPartido() {
        return partido;
    }

    /**
     * Define el partido asociado al pronóstico.
     *
     * @param partido nuevo partido
     */
    public void setPartido(Partido partido) {
        this.partido = partido;
    }
}