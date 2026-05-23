package co.edu.unbosque.mundial_2026.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * Representa un evento de auditoría
 *
 * Incluye índices para optimizar búsquedas
 */
@Entity
@Table(name = "eventos_auditoria", indexes = {
    @Index(name = "idx_auditoria_usuario",    columnList = "usuario_id"),
    @Index(name = "idx_auditoria_tipo",       columnList = "tipo"),
    @Index(name = "idx_auditoria_fecha",      columnList = "fecha"),
    @Index(name = "idx_auditoria_entidad",    columnList = "entidad_correlacion"),
    @Index(name = "idx_auditoria_fecha_tipo", columnList = "fecha, tipo")
})
public class EventoAuditoria {

    /**
     * Identificador del evento
     *
     * Generado automáticamente
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Tipo de evento
     *
     * Campo obligatorio
     */
    @Column(nullable = false)
    private String tipo;

    /**
     * Descripción del evento
     *
     * Campo obligatorio
     * Usa tipo TEXT para almacenar contenido extenso
     */
    @Column(nullable = false, columnDefinition = "TEXT")
    private String descripcion;

    /**
     * Fecha del evento
     *
     * Campo obligatorio
     */
    @Column(nullable = false)
    private LocalDateTime fecha;

    /**
     * Identificador de correlación
     *
     * Permite relacionar eventos
     */
    @Column(name = "id_correlacion")
    private String idCorrelacion;

    /**
     * Entidad de correlación
     *
     * Permite identificar la entidad relacionada
     */
    @Column(name = "entidad_correlacion")
    private String entidadCorrelacion;

    /**
     * Usuario asociado al evento
     *
     * Relación de muchos a uno
     * Relación mediante la columna usuario_id
     * Puede ser nulo
     */
    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = true)
    private Usuario usuario;

    /**
     * Crea un evento de auditoría
     */
    public EventoAuditoria() {
        //Constructor vacio
    }

    /**
     * Crea un evento de auditoría con datos iniciales
     *
     * @param tipo tipo de evento
     * @param descripcion descripción del evento
     * @param fecha fecha del evento
     * @param idCorrelacion identificador de correlación
     * @param entidadCorrelacion entidad de correlación
     * @param usuario usuario asociado
     */
    public EventoAuditoria(String tipo, String descripcion, LocalDateTime fecha,
            String idCorrelacion, String entidadCorrelacion, Usuario usuario) {
        this.tipo = tipo;
        this.descripcion = descripcion;
        this.fecha = fecha;
        this.idCorrelacion = idCorrelacion;
        this.entidadCorrelacion = entidadCorrelacion;
        this.usuario = usuario;
    }

    /**
     * Obtiene el identificador del evento
     *
     * @return identificador del evento
     */
    public Long getId() {
        return id;
    }

    /**
     * Asigna el identificador del evento
     *
     * @param id identificador del evento
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Obtiene el tipo de evento
     *
     * @return tipo de evento
     */
    public String getTipo() {
        return tipo;
    }

    /**
     * Asigna el tipo de evento
     *
     * @param tipo tipo de evento
     */
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    /**
     * Obtiene la descripción del evento
     *
     * @return descripción del evento
     */
    public String getDescripcion() {
        return descripcion;
    }

    /**
     * Asigna la descripción del evento
     *
     * @param descripcion descripción del evento
     */
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    /**
     * Obtiene la fecha del evento
     *
     * @return fecha del evento
     */
    public LocalDateTime getFecha() {
        return fecha;
    }

    /**
     * Asigna la fecha del evento
     *
     * @param fecha fecha del evento
     */
    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    /**
     * Obtiene el identificador de correlación
     *
     * @return identificador de correlación
     */
    public String getIdCorrelacion() {
        return idCorrelacion;
    }

    /**
     * Asigna el identificador de correlación
     *
     * @param idCorrelacion identificador de correlación
     */
    public void setIdCorrelacion(String idCorrelacion) {
        this.idCorrelacion = idCorrelacion;
    }

    /**
     * Obtiene la entidad de correlación
     *
     * @return entidad de correlación
     */
    public String getEntidadCorrelacion() {
        return entidadCorrelacion;
    }

    /**
     * Asigna la entidad de correlación
     *
     * @param entidadCorrelacion entidad de correlación
     */
    public void setEntidadCorrelacion(String entidadCorrelacion) {
        this.entidadCorrelacion = entidadCorrelacion;
    }

    /**
     * Obtiene el usuario asociado
     *
     * @return usuario asociado
     */
    public Usuario getUsuario() {
        return usuario;
    }

    /**
     * Asigna el usuario asociado
     *
     * @param usuario usuario asociado
     */
    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
}