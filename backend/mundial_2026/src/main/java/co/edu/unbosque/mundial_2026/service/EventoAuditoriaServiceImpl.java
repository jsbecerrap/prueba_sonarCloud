package co.edu.unbosque.mundial_2026.service;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.unbosque.mundial_2026.dto.EventoAuditoriaDTO;
import co.edu.unbosque.mundial_2026.entity.EventoAuditoria;
import co.edu.unbosque.mundial_2026.entity.Usuario;
import co.edu.unbosque.mundial_2026.repository.EventoAuditoriaRepository;

@Service
public class EventoAuditoriaServiceImpl implements EventoAuditoriaService {

    private final EventoAuditoriaRepository repository;
    

    public EventoAuditoriaServiceImpl(EventoAuditoriaRepository repository) {
        this.repository = repository;
     
    }

    @Override
@Async
@Transactional
public void registrar(String tipo, String descripcion, Long usuarioId,
        String idCorrelacion, String entidadCorrelacion) {
    EventoAuditoria evento = new EventoAuditoria(
            tipo, descripcion, LocalDateTime.now(),
            idCorrelacion, entidadCorrelacion, null);
    
    // Solo asignar referencia, NO cargar el usuario completo
    if (usuarioId != null) {
        Usuario refUsuario = new Usuario();
        refUsuario.setId(usuarioId);
        evento.setUsuario(refUsuario);
    }
    
    repository.save(evento);
}
    @Override
    @Transactional(readOnly = true)
    public Page<EventoAuditoriaDTO> obtenerTodos(Pageable pageable) {
        return repository.findAll(pageable).map(this::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EventoAuditoriaDTO> buscarPorUsuario(Long usuarioId, Pageable pageable) {
        return repository.findByUsuarioId(usuarioId, pageable).map(this::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EventoAuditoriaDTO> buscarPorTipo(String tipo, Pageable pageable) {
        return repository.findByTipo(tipo, pageable).map(this::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EventoAuditoriaDTO> buscarPorCorrelacion(String correlacion, Pageable pageable) {
        return repository.findByIdCorrelacion(correlacion, pageable).map(this::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EventoAuditoriaDTO> buscarPorEntidad(String entidadCorrelacion, Pageable pageable) {
        return repository.findByEntidadCorrelacion(entidadCorrelacion, pageable).map(this::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EventoAuditoriaDTO> buscarPorFecha(LocalDateTime fechaInicio,
            LocalDateTime fechaFin, Pageable pageable) {
        return repository.findByFechaBetween(fechaInicio, fechaFin, pageable).map(this::toDTO);
    }

   @Override
@Transactional(readOnly = true)
public Page<EventoAuditoriaDTO> buscarConFiltros(Long usuarioId, String tipos,
        LocalDateTime fechaInicio, LocalDateTime fechaFin, Pageable pageable) {
    String fi = fechaInicio != null ? fechaInicio.toString() : null;
    String ff = fechaFin != null ? fechaFin.toString() : null;
    return repository.buscarConFiltros(usuarioId, tipos, fi, ff, pageable)
            .map(this::toDTO);
}
    private EventoAuditoriaDTO toDTO(EventoAuditoria evento) {
        Long usuarioId = evento.getUsuario() != null ? evento.getUsuario().getId() : null;
        return new EventoAuditoriaDTO(
                evento.getId(), evento.getTipo(), evento.getDescripcion(),
                evento.getFecha(), evento.getIdCorrelacion(),
                evento.getEntidadCorrelacion(), usuarioId);
    }
}