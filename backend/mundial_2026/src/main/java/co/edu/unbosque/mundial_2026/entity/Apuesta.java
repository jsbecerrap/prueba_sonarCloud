package co.edu.unbosque.mundial_2026.entity;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

/**
 * Representa una apuesta
 */
@Entity
@Table(name = "apuestas")
public class Apuesta {

    /**
     * Identificador de la apuesta
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Nombre de la apuesta
     */
    @Column(nullable = false, length = 50)
    private String nombre;

    /**
     * Estado de la apuesta
     */
    @Column(nullable = false, length = 20)
    private String estado;

    /**
     * Código de invitación
     */
    @Column(name = "codigo_invitacion", unique = true, nullable = false, length = 36)
    private String codigoInvitacion;

    /**
     * Fecha de cierre
     */
    @Column(name = "fecha_cierre")
    private LocalDateTime fechaCierre;

    /**
     * Indica si los puntos fueron calculados
     */
    @Column(name = "puntos_calculados", nullable = false, columnDefinition = "boolean default false")
    private boolean puntosCalculados;

    /**
     * Usuario creador de la apuesta
     */
    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario creadaPor;

    /**
     * Participaciones de la apuesta
     */
    @OneToMany(mappedBy = "apuesta")
    private List<Participacion> participaciones;

    /**
     * Pronósticos de la apuesta
     */
    @OneToMany(mappedBy = "apuesta")
    private List<Pronostico> pronosticos;

    /**
     * Crea una apuesta
     */
    public Apuesta() {
        //Constructor(Comentario que requiere sonarcloud)
    }

    /**
     * Obtiene el identificador de la apuesta
     *
     * @return identificador de la apuesta
     */
    public Long getId() {
        return id;
    }

    /**
     * Asigna el identificador de la apuesta
     *
     * @param id identificador de la apuesta
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Obtiene el nombre de la apuesta
     *
     * @return nombre de la apuesta
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Asigna el nombre de la apuesta
     *
     * @param nombre nombre de la apuesta
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Obtiene el estado de la apuesta
     *
     * @return estado de la apuesta
     */
    public String getEstado() {
        return estado;
    }

    /**
     * Asigna el estado de la apuesta
     *
     * @param estado estado de la apuesta
     */
    public void setEstado(String estado) {
        this.estado = estado;
    }

    /**
     * Obtiene el código de invitación
     *
     * @return código de invitación
     */
    public String getCodigoInvitacion() {
        return codigoInvitacion;
    }

    /**
     * Asigna el código de invitación
     *
     * @param codigoInvitacion código de invitación
     */
    public void setCodigoInvitacion(String codigoInvitacion) {
        this.codigoInvitacion = codigoInvitacion;
    }

    /**
     * Obtiene la fecha de cierre
     *
     * @return fecha de cierre
     */
    public LocalDateTime getFechaCierre() {
        return fechaCierre;
    }

    /**
     * Asigna la fecha de cierre
     *
     * @param fechaCierre fecha de cierre
     */
    public void setFechaCierre(LocalDateTime fechaCierre) {
        this.fechaCierre = fechaCierre;
    }

    /**
     * Indica si los puntos fueron calculados
     *
     * @return estado de cálculo de puntos
     */
    public boolean isPuntosCalculados() {
        return puntosCalculados;
    }

    /**
     * Asigna el estado de cálculo de puntos
     *
     * @param puntosCalculados estado de cálculo de puntos
     */
    public void setPuntosCalculados(boolean puntosCalculados) {
        this.puntosCalculados = puntosCalculados;
    }

    /**
     * Obtiene el usuario creador
     *
     * @return usuario creador
     */
    public Usuario getCreadaPor() {
        return creadaPor;
    }

    /**
     * Asigna el usuario creador
     *
     * @param creadaPor usuario creador
     */
    public void setCreadaPor(Usuario creadaPor) {
        this.creadaPor = creadaPor;
    }

    /**
     * Obtiene las participaciones
     *
     * @return participaciones
     */
    public List<Participacion> getParticipaciones() {
        return participaciones;
    }

    /**
     * Asigna las participaciones
     *
     * @param participaciones participaciones
     */
    public void setParticipaciones(List<Participacion> participaciones) {
        this.participaciones = participaciones;
    }

    /**
     * Obtiene los pronósticos
     *
     * @return pronósticos
     */
    public List<Pronostico> getPronosticos() {
        return pronosticos;
    }

    /**
     * Asigna los pronósticos
     *
     * @param pronosticos pronósticos
     */
    public void setPronosticos(List<Pronostico> pronosticos) {
        this.pronosticos = pronosticos;
    }
}