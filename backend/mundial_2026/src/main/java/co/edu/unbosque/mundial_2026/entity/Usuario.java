package co.edu.unbosque.mundial_2026.entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "usuarios")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Usuario {

    private static final String USUARIO_ID = "usuario_id";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "usuario", unique = true, nullable = false, length = 100)
    @NotBlank
    private String correoUsuario;

    /*
     * length = 72 corresponde al límite de bcrypt; el hash resultante
     * tiene siempre 60 caracteres ($2a$10$ + 53 chars), pero dejamos
     * margen por si en el futuro se usa otra estrategia.
     */
    @Column(name = "contrasena", nullable = false, length = 72)
    @NotBlank
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String contrasena;

    @Column(name = "nombre_usuario", nullable = false, length = 50)
    private String nombre;

    @Column(name = "apellido_usuario", nullable = false, length = 50)
    private String apellido;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rol_id", nullable = false)
    private Rol rol;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "usuarios_selecciones",
            joinColumns = @JoinColumn(name = USUARIO_ID),
            inverseJoinColumns = @JoinColumn(name = "seleccion_id"),
            uniqueConstraints = @UniqueConstraint(columnNames = { USUARIO_ID, "seleccion_id" }))
    private List<Seleccion> seleccionesU;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "usuarios_preferenciasUbi",
            joinColumns = @JoinColumn(name = USUARIO_ID),
            inverseJoinColumns = @JoinColumn(name = "estadio_id"),
            uniqueConstraints = @UniqueConstraint(columnNames = { USUARIO_ID, "estadio_id" }))
    private List<EstadioFavorito> preferenciasu;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(joinColumns = @JoinColumn(name = USUARIO_ID),
            inverseJoinColumns = @JoinColumn(name = "ciudad_id"),
            uniqueConstraints = @UniqueConstraint(columnNames = { USUARIO_ID, "ciudad_id" }))
    private List<CiudadFavorita> ciudadFavoritas;

    @Column(name = "fecha_registro")
    private java.time.LocalDateTime fechaRegistro;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private boolean activo;

    @Column(name = "fcm_token")
    private String fcmtoken;

   
    @Column(name = "intentos_fallidos", nullable = false)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Integer intentosFallidos = 0;

    @Column(name = "bloqueado_hasta")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private java.time.LocalDateTime bloqueadoHasta;

    public Usuario() {
    }

    public Usuario(String correoUsuario, String contrasena) {
        this.correoUsuario = correoUsuario;
        this.contrasena = contrasena;
    }

    @PrePersist
    public void prePersist() {
        this.activo = true;
        this.fechaRegistro = java.time.LocalDateTime.now();
        if (this.intentosFallidos == null) {
            this.intentosFallidos = 0;
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCorreoUsuario() {
        return correoUsuario;
    }

    public void setCorreoUsuario(String correoUsuario) {
        this.correoUsuario = correoUsuario;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public List<Seleccion> getSeleccionesU() {
        return seleccionesU;
    }

    public void setSeleccionesU(List<Seleccion> seleccionesU) {
        this.seleccionesU = seleccionesU;
    }

    public List<EstadioFavorito> getPreferenciasu() {
        return preferenciasu;
    }

    public void setPreferenciasu(List<EstadioFavorito> preferenciasu) {
        this.preferenciasu = preferenciasu;
    }

    public List<CiudadFavorita> getCiudadFavoritas() {
        return ciudadFavoritas;
    }

    public void setCiudadFavoritas(List<CiudadFavorita> ciudadFavoritas) {
        this.ciudadFavoritas = ciudadFavoritas;
    }

    public String getFcmtoken() {
        return fcmtoken;
    }

    public void setFcmtoken(String fcmtoken) {
        this.fcmtoken = fcmtoken;
    }

    public java.time.LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(java.time.LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public Integer getIntentosFallidos() {
        return intentosFallidos;
    }

    public void setIntentosFallidos(Integer intentosFallidos) {
        this.intentosFallidos = intentosFallidos;
    }

    public java.time.LocalDateTime getBloqueadoHasta() {
        return bloqueadoHasta;
    }

    public void setBloqueadoHasta(java.time.LocalDateTime bloqueadoHasta) {
        this.bloqueadoHasta = bloqueadoHasta;
    }
}