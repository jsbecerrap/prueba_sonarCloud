package co.edu.unbosque.mundial_2026.service;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import co.edu.unbosque.mundial_2026.dto.EventoAuditoriaDTO;

/**
 * Contrato del servicio de auditoría — define el registro y consulta paginada
 * de eventos que ocurren en el sistema para trazabilidad y control
 */
public interface EventoAuditoriaService {

    /**
     * Registra un nuevo evento de auditoría en el sistema
     *
     * @param tipo                tipo de evento (ej: LOGIN, COMPRA, MODIFICACION)
     * @param descripcion         detalle legible de lo que ocurrió
     * @param usuarioId           ID del usuario que generó el evento, puede ser null si es del sistema
     * @param idCorrelacion       identificador que permite agrupar eventos relacionados entre sí
     * @param entidadCorrelacion  nombre de la entidad sobre la que ocurrió el evento (ej: Orden, Entrada)
     */
    void registrar(String tipo, String descripcion, Long usuarioId, String idCorrelacion, String entidadCorrelacion);

    /**
     * Retorna todos los eventos de auditoría del sistema de forma paginada
     *
     * @param pageable configuración de paginación y ordenamiento
     * @return página de eventos de auditoría
     */
    Page<EventoAuditoriaDTO> obtenerTodos(Pageable pageable);

    /**
     * Filtra los eventos de auditoría generados por un usuario específico
     *
     * @param usuarioId ID del usuario
     * @param pageable  configuración de paginación
     * @return página de eventos del usuario
     */
    Page<EventoAuditoriaDTO> buscarPorUsuario(Long usuarioId, Pageable pageable);

    /**
     * Filtra los eventos de auditoría por tipo de acción
     *
     * @param tipo     tipo de evento a buscar
     * @param pageable configuración de paginación
     * @return página de eventos del tipo indicado
     */
    Page<EventoAuditoriaDTO> buscarPorTipo(String tipo, Pageable pageable);

    /**
     * Filtra los eventos que comparten un mismo identificador de correlación,
     * útil para rastrear el flujo completo de una operación distribuida
     *
     * @param correlacion identificador de correlación
     * @param pageable    configuración de paginación
     * @return página de eventos correlacionados
     */
    Page<EventoAuditoriaDTO> buscarPorCorrelacion(String correlacion, Pageable pageable);

    /**
     * Filtra los eventos asociados a una entidad del dominio específica
     *
     * @param entidadCorrelacion nombre de la entidad (ej: Orden, Entrada, Usuario)
     * @param pageable           configuración de paginación
     * @return página de eventos de esa entidad
     */
    Page<EventoAuditoriaDTO> buscarPorEntidad(String entidadCorrelacion, Pageable pageable);

    /**
     * Filtra los eventos ocurridos dentro de un rango de fechas
     *
     * @param fechaInicio límite inferior del rango (inclusive)
     * @param fechaFin    límite superior del rango (inclusive)
     * @param pageable    configuración de paginación
     * @return página de eventos dentro del rango indicado
     */
    Page<EventoAuditoriaDTO> buscarPorFecha(LocalDateTime fechaInicio, LocalDateTime fechaFin, Pageable pageable);

    /**
     * Búsqueda combinada con múltiples filtros opcionales — permite consultar eventos
     * cruzando usuario, uno o varios tipos separados por coma y rango de fechas
     * en una sola consulta
     *
     * @param usuarioId   ID del usuario, puede ser null para no filtrar por usuario
     * @param tipos       tipos de evento separados por coma, puede ser null para incluir todos
     * @param fechaInicio límite inferior del rango de fechas, puede ser null
     * @param fechaFin    límite superior del rango de fechas, puede ser null
     * @param pageable    configuración de paginación
     * @return página de eventos que cumplen los filtros indicados
     */
    Page<EventoAuditoriaDTO> buscarConFiltros(Long usuarioId, String tipos, LocalDateTime fechaInicio, LocalDateTime fechaFin, Pageable pageable);
}