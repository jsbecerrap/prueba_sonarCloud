package co.edu.unbosque.mundial_2026.dto;

import java.time.LocalDateTime;
import java.util.List;

public class ApuestaConParticipantesDTO {

    private Long id;
    private String nombre;
    private String estado;
    private String codigoInvitacion;
    private LocalDateTime fechaCierre;
    private Long creadoPor;
    private List<ParticipacionDTO> participantes;

    public ApuestaConParticipantesDTO() {
    }

    public ApuestaConParticipantesDTO(Long id, String nombre, String estado,
            String codigoInvitacion, LocalDateTime fechaCierre,
            Long creadoPor, List<ParticipacionDTO> participantes) {
        this.id = id;
        this.nombre = nombre;
        this.estado = estado;
        this.codigoInvitacion = codigoInvitacion;
        this.fechaCierre = fechaCierre;
        this.creadoPor = creadoPor;
        this.participantes = participantes;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getCodigoInvitacion() {
        return codigoInvitacion;
    }

    public void setCodigoInvitacion(String codigoInvitacion) {
        this.codigoInvitacion = codigoInvitacion;
    }

    public LocalDateTime getFechaCierre() {
        return fechaCierre;
    }

    public void setFechaCierre(LocalDateTime fechaCierre) {
        this.fechaCierre = fechaCierre;
    }

    public Long getCreadoPor() {
        return creadoPor;
    }

    public void setCreadoPor(Long creadoPor) {
        this.creadoPor = creadoPor;
    }

    public List<ParticipacionDTO> getParticipantes() {
        return participantes;
    }

    public void setParticipantes(List<ParticipacionDTO> participantes) {
        this.participantes = participantes;
    }
}