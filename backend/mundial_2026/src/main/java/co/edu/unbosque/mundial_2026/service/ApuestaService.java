package co.edu.unbosque.mundial_2026.service;

import java.util.List;

import co.edu.unbosque.mundial_2026.dto.ApuestaConParticipantesDTO;
import co.edu.unbosque.mundial_2026.dto.ApuestaDTO;
import co.edu.unbosque.mundial_2026.dto.ParticipacionDTO;
import co.edu.unbosque.mundial_2026.dto.PronosticoDTO;
import co.edu.unbosque.mundial_2026.dto.request.ApuestaRequestDTO;
import co.edu.unbosque.mundial_2026.dto.request.PronosticoRequestDTO;

/**
 * Contrato del servicio de apuestas — define todas las operaciones del ciclo de vida
 * de una apuesta: creación, participación, pronósticos, puntuación y cierre
 */
public interface ApuestaService {

    /**
     * Crea una nueva apuesta con los datos del DTO y genera su código único de invitación
     *
     * @param dto datos de configuración de la apuesta
     * @return apuesta creada con su código y estado inicial
     */
    ApuestaDTO crearApuesta(ApuestaRequestDTO dto);

    /**
     * Inscribe a un usuario en una apuesta existente usando el código de invitación
     *
     * @param codigo     código único de la apuesta
     * @param usuarioId  ID del usuario que quiere unirse
     * @return apuesta actualizada con el nuevo participante
     */
    ApuestaDTO unirseApuesta(String codigo, Long usuarioId);

    /**
     * Registra el pronóstico de un participante para un partido dentro de una apuesta
     *
     * @param dto datos del pronóstico: partido, marcador esperado y apuesta asociada
     * @return pronóstico persistido
     */
    PronosticoDTO registrarPronostico(PronosticoRequestDTO dto);

    /**
     * Retorna la lista de participantes de una apuesta ordenada por puntos de mayor a menor
     *
     * @param apuestaId ID de la apuesta
     * @return ranking actualizado de participantes
     */
    List<ParticipacionDTO> obtenerRanking(Long apuestaId);

    /**
     * Calcula y actualiza los puntos de todos los pronósticos de una apuesta
     * comparando los marcadores pronosticados contra los resultados reales de los partidos
     *
     * @param apuestaId ID de la apuesta a evaluar
     * @return lista de pronósticos con sus puntos actualizados
     */
    List<PronosticoDTO> calcularPuntos(Long apuestaId);

    /**
     * Cierra manualmente una apuesta, impidiendo nuevas participaciones y pronósticos
     *
     * @param apuestaId ID de la apuesta a cerrar
     * @return apuesta con estado actualizado a cerrado
     */
    ApuestaDTO cerrarApuesta(Long apuestaId);

    /**
     * Obtiene el detalle de una apuesta por su ID
     *
     * @param apuestaId ID de la apuesta
     * @return datos completos de la apuesta
     */
    ApuestaDTO obtenerApuesta(Long apuestaId);

    /**
     * Lista todas las apuestas en las que participa un usuario
     *
     * @param usuarioId ID del usuario
     * @return lista de apuestas del usuario
     */
    List<ApuestaDTO> listarApuestasPorUsuario(Long usuarioId);

    /**
     * Retorna los participantes registrados en una apuesta
     *
     * @param apuestaId ID de la apuesta
     * @return lista de participaciones con sus datos
     */
    List<ParticipacionDTO> listarParticipantes(Long apuestaId);

    /**
     * Consulta y verifica el estado de un pronóstico específico,
     * incluyendo si ya fue evaluado contra el resultado real
     *
     * @param pronosticoId ID del pronóstico
     * @return pronóstico con su estado de verificación
     */
    PronosticoDTO verificarPronostico(Long pronosticoId);

    /**
     * Cierra automáticamente todas las apuestas cuya fecha de vencimiento
     * ya expiró — diseñado para ejecutarse desde un scheduler
     */
    void cerrarApuestasVencidas();

    /**
     * Recorre todas las apuestas activas y recalcula los puntos de sus pronósticos
     * según los resultados disponibles — diseñado para ejecutarse desde un scheduler
     */
    void calcularPuntosAutomatico();

    /**
     * Retorna los pronósticos que un usuario específico registró dentro de una apuesta
     *
     * @param apuestaId ID de la apuesta
     * @param usuarioId ID del usuario
     * @return lista de pronósticos del usuario en esa apuesta
     */
    List<PronosticoDTO> misPronosticos(Long apuestaId, Long usuarioId);

    /**
     * Elimina un pronóstico validando que el correo del solicitante
     * corresponda al dueño del pronóstico
     *
     * @param pronosticoId  ID del pronóstico a eliminar
     * @param correoUsuario correo del usuario que solicita la eliminación
     */
    void eliminarPronostico(Long pronosticoId, String correoUsuario);

    /**
     * Calcula puntos parciales de una apuesta considerando únicamente
     * los partidos que ya tienen resultado, sin esperar a que cierre la apuesta
     *
     * @param apuestaId ID de la apuesta
     * @return lista de pronósticos con puntos parciales calculados
     */
    List<PronosticoDTO> calcularPuntosParciales(Long apuestaId);

    /**
     * Lista todas las apuestas existentes en el sistema sin filtro alguno
     *
     * @return lista completa de apuestas
     */
    List<ApuestaDTO> listarTodas();

    /**
     * Elimina permanentemente una apuesta y sus datos asociados
     *
     * @param apuestaId ID de la apuesta a eliminar
     */
    void eliminarApuesta(Long apuestaId);

    /**
     * Lista las apuestas de un usuario incluyendo el detalle completo
     * de sus participantes y pronósticos
     *
     * @param usuarioId ID del usuario
     * @return lista de apuestas con participantes anidados
     */
    List<ApuestaConParticipantesDTO> listarApuestasPorUsuarioCompleto(Long usuarioId);

    /**
     * Edita un pronóstico existente validando que el solicitante sea su dueño —
     * solo se permite antes de que el partido inicie o la apuesta cierre
     *
     * @param pronosticoId  ID del pronóstico a modificar
     * @param dto           nuevos datos del pronóstico
     * @param correoUsuario correo del usuario que solicita la edición
     * @return pronóstico actualizado
     */
    PronosticoDTO editarPronostico(Long pronosticoId, PronosticoRequestDTO dto, String correoUsuario);
}