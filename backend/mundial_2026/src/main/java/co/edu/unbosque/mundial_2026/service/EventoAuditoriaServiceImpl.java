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

/**
 * Implementación del servicio encargado de registrar y consultar los eventos de auditoría
 * generados por las operaciones del sistema.
 * Cada acción relevante (creación de pollas, pagos, transferencias, etc.) queda
 * almacenada como un evento con tipo, descripción, fecha, usuario y correlación.
 * El registro se ejecuta de forma asíncrona para no bloquear el flujo principal.
 * Las consultas soportan filtros por usuario, tipo, correlación, entidad y rango de fechas,
 * todas paginadas para manejar volúmenes grandes de registros
 */
@Service
public class EventoAuditoriaServiceImpl implements EventoAuditoriaService {

    private final EventoAuditoriaRepository repository;

    public EventoAuditoriaServiceImpl(EventoAuditoriaRepository repository) {
        this.repository = repository;
    }

    /**
     * Registra un nuevo evento de auditoría de forma asíncrona.
     * Si se proporciona un id de usuario, se asigna solo la referencia (sin cargar
     * la entidad completa) para evitar consultas innecesarias a la base de datos
     *
     * @param tipo                tipo del evento (ej: "APUESTA_CREADA", "ENTRADA_PAGADA")
     * @param descripcion         descripción detallada de lo que ocurrió
     * @param usuarioId           id del usuario que generó el evento, puede ser null para eventos del sistema
     * @param idCorrelacion       identificador que relaciona el evento con la entidad afectada
     * @param entidadCorrelacion  nombre de la entidad relacionada (ej: "Apuesta", "Entrada")
     */
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

    /**
     * Retorna todos los eventos de auditoría registrados en el sistema de forma paginada
     *
     * @param pageable configuración de paginación y ordenamiento
     * @return página de {@link EventoAuditoriaDTO} con todos los eventos
     */
    @Override
    @Transactional(readOnly = true)
    public Page<EventoAuditoriaDTO> obtenerTodos(Pageable pageable) {
        return repository.findAll(pageable).map(this::toDTO);
    }

    /**
     * Busca los eventos de auditoría generados por un usuario específico
     *
     * @param usuarioId id del usuario a consultar
     * @param pageable  configuración de paginación y ordenamiento
     * @return página de {@link EventoAuditoriaDTO} con los eventos del usuario
     */
    @Override
    @Transactional(readOnly = true)
    public Page<EventoAuditoriaDTO> buscarPorUsuario(Long usuarioId, Pageable pageable) {
        return repository.findByUsuarioId(usuarioId, pageable).map(this::toDTO);
    }

    /**
     * Busca los eventos de auditoría que coincidan con un tipo específico
     *
     * @param tipo     tipo del evento a filtrar (ej: "ENTRADA_PAGADA")
     * @param pageable configuración de paginación y ordenamiento
     * @return página de {@link EventoAuditoriaDTO} con los eventos del tipo indicado
     */
    @Override
    @Transactional(readOnly = true)
    public Page<EventoAuditoriaDTO> buscarPorTipo(String tipo, Pageable pageable) {
        return repository.findByTipo(tipo, pageable).map(this::toDTO);
    }

    /**
     * Busca los eventos de auditoría asociados a un id de correlación específico,
     * permitiendo rastrear todos los eventos relacionados con una entidad concreta
     *
     * @param correlacion id de correlación a buscar
     * @param pageable    configuración de paginación y ordenamiento
     * @return página de {@link EventoAuditoriaDTO} con los eventos que coincidan
     */
    @Override
    @Transactional(readOnly = true)
    public Page<EventoAuditoriaDTO> buscarPorCorrelacion(String correlacion, Pageable pageable) {
        return repository.findByIdCorrelacion(correlacion, pageable).map(this::toDTO);
    }

    /**
     * Busca los eventos de auditoría relacionados con un tipo de entidad específica
     *
     * @param entidadCorrelacion nombre de la entidad a filtrar (ej: "Apuesta", "Entrada")
     * @param pageable           configuración de paginación y ordenamiento
     * @return página de {@link EventoAuditoriaDTO} con los eventos de esa entidad
     */
    @Override
    @Transactional(readOnly = true)
    public Page<EventoAuditoriaDTO> buscarPorEntidad(String entidadCorrelacion, Pageable pageable) {
        return repository.findByEntidadCorrelacion(entidadCorrelacion, pageable).map(this::toDTO);
    }

    /**
     * Busca los eventos de auditoría ocurridos dentro de un rango de fechas
     *
     * @param fechaInicio fecha y hora de inicio del rango (inclusive)
     * @param fechaFin    fecha y hora de fin del rango (inclusive)
     * @param pageable    configuración de paginación y ordenamiento
     * @return página de {@link EventoAuditoriaDTO} con los eventos en ese rango
     */
    @Override
    @Transactional(readOnly = true)
    public Page<EventoAuditoriaDTO> buscarPorFecha(LocalDateTime fechaInicio,
            LocalDateTime fechaFin, Pageable pageable) {
        return repository.findByFechaBetween(fechaInicio, fechaFin, pageable).map(this::toDTO);
    }

    /**
     * Busca eventos de auditoría combinando múltiples filtros opcionales: usuario, tipo,
     * y rango de fechas. Los filtros que vengan nulos son ignorados por la consulta,
     * permitiendo cualquier combinación de criterios
     *
     * @param usuarioId   id del usuario, puede ser null
     * @param tipos       tipo del evento, puede ser null
     * @param fechaInicio fecha de inicio del rango, puede ser null
     * @param fechaFin    fecha de fin del rango, puede ser null
     * @param pageable    configuración de paginación y ordenamiento
     * @return página de {@link EventoAuditoriaDTO} con los eventos que cumplan los filtros
     */
    @Override
    @Transactional(readOnly = true)
    public Page<EventoAuditoriaDTO> buscarConFiltros(Long usuarioId, String tipos,
            LocalDateTime fechaInicio, LocalDateTime fechaFin, Pageable pageable) {
        String fi = fechaInicio != null ? fechaInicio.toString() : null;
        String ff = fechaFin != null ? fechaFin.toString() : null;
        return repository.buscarConFiltros(usuarioId, tipos, fi, ff, pageable)
                .map(this::toDTO);
    }

    /**
     * Convierte una entidad {@link EventoAuditoria} a su representación DTO.
     * Si el evento no tiene usuario asociado, el id de usuario se retorna como null
     *
     * @param evento entidad a convertir
     * @return {@link EventoAuditoriaDTO} con los datos del evento
     */
    private EventoAuditoriaDTO toDTO(EventoAuditoria evento) {
        Long usuarioId = evento.getUsuario() != null ? evento.getUsuario().getId() : null;
        return new EventoAuditoriaDTO(
                evento.getId(), evento.getTipo(), evento.getDescripcion(),
                evento.getFecha(), evento.getIdCorrelacion(),
                evento.getEntidadCorrelacion(), usuarioId);
    }
}