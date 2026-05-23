package co.edu.unbosque.mundial_2026.entity;

import java.time.LocalDateTime;
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

/**
 * Entidad que representa un usuario dentro del sistema.
 * Contiene la información de autenticación, perfil, preferencias
 * y datos de seguridad asociados a cada usuario registrado.
 */
@Entity
@Table(name = "usuarios")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Usuario {

    /**
     * Constante utilizada para evitar repetir el nombre
     * de la columna usuario_id en las relaciones.
     */
    private static final String USUARIO_ID = "usuario_id";

    /**
     * Identificador único del usuario.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Correo electrónico del usuario.
     * Debe ser único dentro del sistema.
     */
    @Column(name = "usuario", unique = true, nullable = false, length = 100)
    @NotBlank
    private String correoUsuario;

    /**
     * Contraseña cifrada del usuario.
     *
     * length = 72 corresponde al límite de bcrypt;
     * el hash generado normalmente ocupa 60 caracteres,
     * pero se deja margen para futuras estrategias.
     *
     * WRITE_ONLY evita que se exponga en respuestas JSON.
     */
    @Column(name = "contrasena", nullable = false, length = 72)
    @NotBlank
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String contrasena;

    /**
     * Nombre del usuario.
     */
    @Column(name = "nombre_usuario", nullable = false, length = 50)
    private String nombre;

    /**
     * Apellido del usuario.
     */
    @Column(name = "apellido_usuario", nullable = false, length = 50)
    private String apellido;

    /**
     * Rol asociado al usuario
     * (por ejemplo: ADMIN, USUARIO).
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rol_id", nullable = false)
    private Rol rol;

    /**
     * Selecciones favoritas asociadas al usuario.
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "usuarios_selecciones",
        joinColumns = @JoinColumn(name = USUARIO_ID),
        inverseJoinColumns = @JoinColumn(name = "seleccion_id"),
        uniqueConstraints = @UniqueConstraint(columnNames = { USUARIO_ID, "seleccion_id" })
    )
    private List<Seleccion> seleccionesU;

    /**
     * Estadios favoritos o preferencias de ubicación del usuario.
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "usuarios_preferenciasUbi",
        joinColumns = @JoinColumn(name = USUARIO_ID),
        inverseJoinColumns = @JoinColumn(name = "estadio_id"),
        uniqueConstraints = @UniqueConstraint(columnNames = { USUARIO_ID, "estadio_id" })
    )
    private List<EstadioFavorito> preferenciasu;

    /**
     * Ciudades favoritas del usuario.
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "usuarios_ciudadesFavoritas",
        joinColumns = @JoinColumn(name = USUARIO_ID),
        inverseJoinColumns = @JoinColumn(name = "ciudad_id"),
        uniqueConstraints = @UniqueConstraint(columnNames = { USUARIO_ID, "ciudad_id" })
    )
    private List<CiudadFavorita> ciudadFavoritas;

    /**
     * Fecha y hora en la que el usuario fue registrado.
     */
    @Column(name = "fecha_registro")
    private LocalDateTime fechaRegistro;

    /**
     * Indica si el usuario está activo dentro del sistema.
     *
     * WRITE_ONLY evita exponerlo en JSON.
     */
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private boolean activo;

    /**
     * Token de Firebase Cloud Messaging
     * utilizado para notificaciones push.
     */
    @Column(name = "fcm_token")
    private String fcmtoken;

    /**
     * Número de intentos fallidos de inicio de sesión.
     *
     * WRITE_ONLY evita exponerlo en respuestas JSON.
     */
    @Column(name = "intentos_fallidos", nullable = false)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Integer intentosFallidos = 0;

    /**
     * Fecha hasta la cual el usuario permanece bloqueado
     * después de múltiples intentos fallidos.
     *
     * WRITE_ONLY evita exponerlo en JSON.
     */
    @Column(name = "bloqueado_hasta")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private LocalDateTime bloqueadoHasta;

    /**
     * Constructor vacío requerido por JPA.
     */
    public Usuario() {
        // Constructor vacío
    }

    /**
     * Constructor básico para autenticación rápida.
     *
     * @param correoUsuario correo del usuario
     * @param contrasena contraseña cifrada
     */
    public Usuario(String correoUsuario, String contrasena) {
        this.correoUsuario = correoUsuario;
        this.contrasena = contrasena;
    }

    /**
     * Método ejecutado antes de persistir el usuario.
     * Inicializa valores por defecto.
     */
    @PrePersist
    public void prePersist() {
        this.activo = true;
        this.fechaRegistro = LocalDateTime.now();

        if (this.intentosFallidos == null) {
            this.intentosFallidos = 0;
        }
    }

    /**
     * @return id del usuario
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id nuevo id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return correo del usuario
     */
    public String getCorreoUsuario() {
        return correoUsuario;
    }

    /**
     * @param correoUsuario nuevo correo
     */
    public void setCorreoUsuario(String correoUsuario) {
        this.correoUsuario = correoUsuario;
    }

    /**
     * @return contraseña cifrada
     */
    public String getContrasena() {
        return contrasena;
    }

    /**
     * @param contrasena nueva contraseña cifrada
     */
    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    /**
     * @return nombre del usuario
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * @param nombre nuevo nombre
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * @return apellido del usuario
     */
    public String getApellido() {
        return apellido;
    }

    /**
     * @param apellido nuevo apellido
     */
    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    /**
     * @return rol asociado
     */
    public Rol getRol() {
        return rol;
    }

    /**
     * @param rol nuevo rol
     */
    public void setRol(Rol rol) {
        this.rol = rol;
    }

    /**
     * @return true si está activo
     */
    public boolean isActivo() {
        return activo;
    }

    /**
     * @param activo nuevo estado
     */
    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    /**
     * @return selecciones favoritas
     */
    public List<Seleccion> getSeleccionesU() {
        return seleccionesU;
    }

    /**
     * @param seleccionesU nuevas selecciones
     */
    public void setSeleccionesU(List<Seleccion> seleccionesU) {
        this.seleccionesU = seleccionesU;
    }

    /**
     * @return preferencias de estadios
     */
    public List<EstadioFavorito> getPreferenciasu() {
        return preferenciasu;
    }

    /**
     * @param preferenciasu nuevas preferencias
     */
    public void setPreferenciasu(List<EstadioFavorito> preferenciasu) {
        this.preferenciasu = preferenciasu;
    }

    /**
     * @return ciudades favoritas
     */
    public List<CiudadFavorita> getCiudadFavoritas() {
        return ciudadFavoritas;
    }

    /**
     * @param ciudadFavoritas nuevas ciudades favoritas
     */
    public void setCiudadFavoritas(List<CiudadFavorita> ciudadFavoritas) {
        this.ciudadFavoritas = ciudadFavoritas;
    }

    /**
     * @return token FCM
     */
    public String getFcmtoken() {
        return fcmtoken;
    }

    /**
     * @param fcmtoken nuevo token FCM
     */
    public void setFcmtoken(String fcmtoken) {
        this.fcmtoken = fcmtoken;
    }

    /**
     * @return fecha de registro
     */
    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    /**
     * @param fechaRegistro nueva fecha de registro
     */
    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    /**
     * @return número de intentos fallidos
     */
    public Integer getIntentosFallidos() {
        return intentosFallidos;
    }

    /**
     * @param intentosFallidos nuevos intentos fallidos
     */
    public void setIntentosFallidos(Integer intentosFallidos) {
        this.intentosFallidos = intentosFallidos;
    }

    /**
     * @return fecha de desbloqueo
     */
    public LocalDateTime getBloqueadoHasta() {
        return bloqueadoHasta;
    }

    /**
     * @param bloqueadoHasta nueva fecha de desbloqueo
     */
    public void setBloqueadoHasta(LocalDateTime bloqueadoHasta) {
        this.bloqueadoHasta = bloqueadoHasta;
    }
}