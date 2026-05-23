package co.edu.unbosque.mundial_2026.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Entidad que representa un partido del mundial.
 */
@Entity
@Table(name = "partidos")
public class Partido {

    /**
     * Identificador único del partido.
     */
    @Id
    private Long id;

    /**
     * Fecha y hora programada del partido.
     */
    @Column(nullable = false)
    private LocalDateTime fecha;

    /**
     * Estado actual del partido.
     */
    @Column(nullable = false)
    private String estado;

    /**
     * Ronda o fase del torneo a la que pertenece el partido.
     */
    private String ronda;

    /**
     * Cantidad de goles anotados por la selección local.
     */
    @Column(name = "goles_local")
    private Integer golesLocal;

    /**
     * Cantidad de goles anotados por la selección visitante.
     */
    @Column(name = "goles_visitante")
    private Integer golesVisitante;

    /**
     * Nombre de la selección local.
     */
    @Column(name = "seleccion_local")
    private String seleccionLocal;

    /**
     * Nombre de la selección visitante.
     */
    @Column(name = "seleccion_visitante")
    private String seleccionVisitante;

    /**
     * Cantidad de cupos disponibles para el partido.
     */
    @Column(name = "capacidad_disponible")
    private Integer capacidadDisponible;

    /**
     * Nombre del estadio donde se juega el partido.
     */
    private String estadio;

    /**
     * Constructor vacío.
     */
    public Partido() {
        // Constructor vacio
    }

    /**
     * Retorna la capacidad disponible.
     *
     * @return cupos disponibles
     */
    public Integer getCapacidadDisponible() {
        return capacidadDisponible;
    }

    /**
     * Define la capacidad disponible.
     *
     * @param capacidadDisponible nueva capacidad disponible
     */
    public void setCapacidadDisponible(Integer capacidadDisponible) {
        this.capacidadDisponible = capacidadDisponible;
    }

    /**
     * Retorna el identificador del partido.
     *
     * @return id del partido
     */
    public Long getId() {
        return id;
    }

    /**
     * Define el identificador del partido.
     *
     * @param id nuevo identificador
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Retorna la fecha del partido.
     *
     * @return fecha y hora programada
     */
    public LocalDateTime getFecha() {
        return fecha;
    }

    /**
     * Define la fecha del partido.
     *
     * @param fecha nueva fecha y hora
     */
    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    /**
     * Retorna el estado del partido.
     *
     * @return estado actual
     */
    public String getEstado() {
        return estado;
    }

    /**
     * Define el estado del partido.
     *
     * @param estado nuevo estado
     */
    public void setEstado(String estado) {
        this.estado = estado;
    }

    /**
     * Retorna la ronda del torneo.
     *
     * @return ronda del partido
     */
    public String getRonda() {
        return ronda;
    }

    /**
     * Define la ronda del torneo.
     *
     * @param ronda nueva ronda
     */
    public void setRonda(String ronda) {
        this.ronda = ronda;
    }

    /**
     * Retorna los goles del equipo local.
     *
     * @return goles del local
     */
    public Integer getGolesLocal() {
        return golesLocal;
    }

    /**
     * Define los goles del equipo local.
     *
     * @param golesLocal nueva cantidad de goles
     */
    public void setGolesLocal(Integer golesLocal) {
        this.golesLocal = golesLocal;
    }

    /**
     * Retorna los goles del equipo visitante.
     *
     * @return goles del visitante
     */
    public Integer getGolesVisitante() {
        return golesVisitante;
    }

    /**
     * Define los goles del equipo visitante.
     *
     * @param golesVisitante nueva cantidad de goles
     */
    public void setGolesVisitante(Integer golesVisitante) {
        this.golesVisitante = golesVisitante;
    }

    /**
     * Retorna la selección local.
     *
     * @return nombre de la selección local
     */
    public String getSeleccionLocal() {
        return seleccionLocal;
    }

    /**
     * Define la selección local.
     *
     * @param seleccionLocal nueva selección local
     */
    public void setSeleccionLocal(String seleccionLocal) {
        this.seleccionLocal = seleccionLocal;
    }

    /**
     * Retorna la selección visitante.
     *
     * @return nombre de la selección visitante
     */
    public String getSeleccionVisitante() {
        return seleccionVisitante;
    }

    /**
     * Define la selección visitante.
     *
     * @param seleccionVisitante nueva selección visitante
     */
    public void setSeleccionVisitante(String seleccionVisitante) {
        this.seleccionVisitante = seleccionVisitante;
    }

    /**
     * Retorna el estadio del partido.
     *
     * @return nombre del estadio
     */
    public String getEstadio() {
        return estadio;
    }

    /**
     * Define el estadio del partido.
     *
     * @param estadio nuevo estadio
     */
    public void setEstadio(String estadio) {
        this.estadio = estadio;
    }
}