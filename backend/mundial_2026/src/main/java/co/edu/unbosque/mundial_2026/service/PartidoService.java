package co.edu.unbosque.mundial_2026.service;

import java.util.List;

import co.edu.unbosque.mundial_2026.dto.PartidoCapacidadDTO;
import co.edu.unbosque.mundial_2026.dto.response.EquipoMundialDTO;
import co.edu.unbosque.mundial_2026.dto.response.JugadorDTO;
import co.edu.unbosque.mundial_2026.dto.response.PartidoDTO;
import co.edu.unbosque.mundial_2026.dto.response.PosicionDTO;
import co.edu.unbosque.mundial_2026.dto.response.PreferenciaDTO;
import co.edu.unbosque.mundial_2026.entity.Partido;

/**
 * Contrato del servicio de partidos — gestiona la consulta, sincronización y
 * personalización de partidos del Mundial 2026, integrando datos de la API externa
 * con las preferencias de cada usuario
 */
public interface PartidoService {

    /**
     * Retorna todos los partidos del Mundial disponibles en el sistema
     *
     * @return lista completa de partidos
     */
    List<PartidoDTO> obtenerPartidos();

    /**
     * Retorna los partidos en los que participa un equipo específico
     *
     * @param equipoId ID del equipo
     * @return lista de partidos del equipo
     */
    List<PartidoDTO> obtenerPartidosPorEquipo(Long equipoId);

    /**
     * Obtiene el detalle de un partido por su ID de fixture
     *
     * @param fixtureId ID del partido en la API externa
     * @return datos completos del partido
     */
    PartidoDTO obtenerPartidoPorId(Long fixtureId);

    /**
     * Retorna las tablas de posiciones del Mundial agrupadas por grupo
     *
     * @return lista de grupos, donde cada grupo contiene su tabla de posiciones
     */
    List<List<PosicionDTO>> obtenerStandings();

    /**
     * Retorna las selecciones participantes en el Mundial con su información básica
     *
     * @return lista de selecciones del mundial
     */
    List<EquipoMundialDTO> obtenerSelecciones();

    /**
     * Retorna los jugadores que pertenecen a un equipo específico
     *
     * @param equipoId ID del equipo
     * @return lista de jugadores del equipo
     */
    List<JugadorDTO> obtenerJugadoresPorEquipo(Long equipoId);

    /**
     * Filtra los partidos que se juegan en una fecha específica
     *
     * @param fecha fecha en formato YYYY-MM-DD
     * @return lista de partidos de esa fecha
     */
    List<PartidoDTO> obtenerPartidosPorFecha(String fecha);

    /**
     * Retorna los partidos que se están jugando en este momento
     *
     * @return lista de partidos en vivo
     */
    List<PartidoDTO> obtenerPartidosEnVivo();

    /**
     * Sincroniza con la API externa los partidos de una fecha, liga y temporada específicas
     * y los persiste en la base de datos
     *
     * @param fecha     fecha en formato YYYY-MM-DD
     * @param liga      ID de la liga en la API externa
     * @param temporada año de la temporada
     * @return número de partidos sincronizados
     */
    int sincronizarPorFechaYLiga(String fecha, int liga, int temporada);

    /**
     * Actualiza el resultado de un partido con los goles y el estado del partido
     *
     * @param partidoId      ID del partido
     * @param golesLocal     goles del equipo local
     * @param golesVisitante goles del equipo visitante
     * @param estadoPartido  código del estado (ej: en juego, finalizado)
     * @return número de registros actualizados
     */
    int actualizarResultado(Long partidoId, int golesLocal, int golesVisitante, int estadoPartido);

    /**
     * Sincroniza todos los partidos del Mundial desde la API externa
     * y los persiste en la base de datos — usado al arrancar la aplicación
     *
     * @return número total de partidos sincronizados
     */
    int sincronizarDesdeAPI();

    /**
     * Retorna los partidos de las selecciones marcadas como favoritas por el usuario
     *
     * @param correo correo del usuario
     * @return lista de partidos de sus selecciones favoritas
     */
    List<PartidoDTO> obtenerPartidosPorSeleccionesFav(String correo);

    /**
     * Retorna los partidos que se juegan en los estadios favoritos del usuario
     *
     * @param correo correo del usuario
     * @return lista de partidos en sus estadios favoritos
     */
    List<PartidoDTO> obtenerPartidosPorEstadiosFav(String correo);

    /**
     * Retorna los partidos que se juegan en las ciudades favoritas del usuario
     *
     * @param correo correo del usuario
     * @return lista de partidos en sus ciudades favoritas
     */
    List<PartidoDTO> obtenerPartidosPorCiudadesFav(String correo);

    /**
     * Filtra entidades {@link Partido} por nombre de selección — uso interno entre servicios
     *
     * @param nombre nombre de la selección
     * @return lista de partidos donde participa esa selección
     */
    List<Partido> filtrarPorSeleccion(String nombre);

    /**
     * Filtra entidades {@link Partido} por nombre de estadio — uso interno entre servicios
     *
     * @param nombre nombre del estadio
     * @return lista de partidos jugados en ese estadio
     */
    List<Partido> filtrarPorEstadio(String nombre);

    /**
     * Filtra entidades {@link Partido} por nombre de ciudad — uso interno entre servicios
     *
     * @param nombre nombre de la ciudad
     * @return lista de partidos jugados en esa ciudad
     */
    List<Partido> filtrarPorCiudad(String nombre);

    /**
     * Retorna el catálogo de selecciones disponibles para que el usuario
     * las marque como favoritas en su perfil
     *
     * @return lista de selecciones con su ID y nombre
     */
    List<PreferenciaDTO> obtenerCatalogoSelecciones();

    /**
     * Obtiene la entidad {@link Partido} directamente desde la base de datos —
     * pensado para uso interno entre servicios, no para exponer en el controlador
     *
     * @param partidoId ID del partido
     * @return entidad {@link Partido}
     */
    Partido obtenerPartidoEntidadPorId(Long partidoId);

    /**
     * Actualiza la capacidad disponible de un partido restando los cupos ocupados
     *
     * @param partidoId ID del partido
     * @param cantidad  número de cupos a descontar
     */
    void actualizarCapacidad(Long partidoId, int cantidad);

    /**
     * Retorna todos los partidos con su capacidad total, ocupada y disponible
     *
     * @return lista de partidos con información de capacidad
     */
    List<PartidoCapacidadDTO> listarPartidosConCapacidad();

    /**
     * Retorna todos los partidos almacenados en la base de datos como entidades —
     * uso interno para procesos que requieren acceso directo a la entidad
     *
     * @return lista de entidades {@link Partido}
     */
    List<Partido> listarDesdeBD();
}