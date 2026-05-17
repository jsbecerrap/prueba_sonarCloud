package co.edu.unbosque.mundial_2026.service;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import co.edu.unbosque.mundial_2026.dto.EventoAuditoriaDTO;

public interface EventoAuditoriaService {
    void registrar(String tipo, String descripcion, Long usuarioId, String idCorrelacion, String entidadCorrelacion);
    Page<EventoAuditoriaDTO> obtenerTodos(Pageable pageable);
    Page<EventoAuditoriaDTO> buscarPorUsuario(Long usuarioId, Pageable pageable);
    Page<EventoAuditoriaDTO> buscarPorTipo(String tipo, Pageable pageable);
    Page<EventoAuditoriaDTO> buscarPorCorrelacion(String correlacion, Pageable pageable);
    Page<EventoAuditoriaDTO> buscarPorEntidad(String entidadCorrelacion, Pageable pageable);
    Page<EventoAuditoriaDTO> buscarPorFecha(LocalDateTime fechaInicio, LocalDateTime fechaFin, Pageable pageable);
  Page<EventoAuditoriaDTO> buscarConFiltros(Long usuarioId, String tipos, LocalDateTime fechaInicio, LocalDateTime fechaFin, Pageable pageable);
}