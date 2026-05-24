package co.edu.unbosque.mundial_2026.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.logging.Logger;

import co.edu.unbosque.mundial_2026.dto.PartidoCapacidadDTO;
import co.edu.unbosque.mundial_2026.dto.response.EquipoMundialDTO;
import co.edu.unbosque.mundial_2026.dto.response.EquipoMundialResponseDTO;
import co.edu.unbosque.mundial_2026.dto.response.JugadorDTO;
import co.edu.unbosque.mundial_2026.dto.response.JugadorResponseDTO;
import co.edu.unbosque.mundial_2026.dto.response.EquipoConEstadioDTO;
import co.edu.unbosque.mundial_2026.dto.response.EquipoDTO;
import co.edu.unbosque.mundial_2026.dto.response.EstadioDTO;
import co.edu.unbosque.mundial_2026.dto.response.EstadoDTO;
import co.edu.unbosque.mundial_2026.dto.response.InfoPartidoDTO;
import co.edu.unbosque.mundial_2026.dto.response.LigaDTO;
import co.edu.unbosque.mundial_2026.dto.response.MarcadorDTO;
import co.edu.unbosque.mundial_2026.dto.response.PartidoDTO;
import co.edu.unbosque.mundial_2026.dto.response.PartidoResponseDTO;
import co.edu.unbosque.mundial_2026.dto.response.PosicionDTO;
import co.edu.unbosque.mundial_2026.dto.response.PreferenciaDTO;
import co.edu.unbosque.mundial_2026.dto.response.StandingResponseDTO;
import co.edu.unbosque.mundial_2026.entity.Partido;
import co.edu.unbosque.mundial_2026.entity.Usuario;
import co.edu.unbosque.mundial_2026.exception.PartidoNotFoundException;
import co.edu.unbosque.mundial_2026.repository.PartidoRepository;
import co.edu.unbosque.mundial_2026.repository.SeleccionRepository;

/**
 * Implementación del servicio encargado de gestionar los partidos del Mundial 2026.
 * Consume la API externa de fútbol para obtener fixtures, standings, selecciones y jugadores,
 * y sincroniza los resultados en la base de datos local.
 * También permite filtrar partidos por selecciones, estadios y ciudades favoritas del usuario,
 * actualizar capacidades de los estadios y registrar resultados manualmente.
 * El mapa de estadios a ciudades se usa para asociar partidos a las ciudades sede
 */
@Service
public class PartidoServiceImpl implements PartidoService {

    private static final int LIGA_MUNDIAL = 1;
    private static final int TEMPORADA_MUNDIAL = 2026;
    private static final String SEASON_PARAM = "&season=";
    private static final String BASE_FIXTURES = "/fixtures?league=" + LIGA_MUNDIAL + SEASON_PARAM + TEMPORADA_MUNDIAL;
    private final RestClient footballClient;
    private final PartidoRepository partidoRepository;
    private final UsuarioService usuarioService;
    private final SeleccionRepository seleccionRepository;
    private static final java.util.Map<String, String> ESTADIO_CIUDAD = new java.util.HashMap<>();
private static final String POR_CONFIRMAR = "Por confirmar";
    static {
        ESTADIO_CIUDAD.put("Estadio Azteca", "Ciudad de Mexico");
        ESTADIO_CIUDAD.put("Estadio Akron", "Guadalajara");
        ESTADIO_CIUDAD.put("Estadio BBVA", "Monterrey");
        ESTADIO_CIUDAD.put("BMO Field", "Toronto");
        ESTADIO_CIUDAD.put("BC Place", "Vancouver");
        ESTADIO_CIUDAD.put("SoFi Stadium", "Los Angeles");
        ESTADIO_CIUDAD.put("MetLife Stadium", "East Rutherford");
        ESTADIO_CIUDAD.put("Gillette Stadium", "Boston");
        ESTADIO_CIUDAD.put("NRG Stadium", "Houston");
        ESTADIO_CIUDAD.put("Lincoln Financial Field", "Philadelphia");
        ESTADIO_CIUDAD.put("Mercedes-Benz Stadium", "Atlanta");
        ESTADIO_CIUDAD.put("Lumen Field", "Seattle");
        ESTADIO_CIUDAD.put("Hard Rock Stadium", "Miami");
        ESTADIO_CIUDAD.put("Arrowhead Stadium", "Kansas City");
    }

    private static final Logger logger = Logger.getLogger(PartidoServiceImpl.class.getName());

    private final EventoAuditoriaService auditoriaService;
    private static final String ENTIDAD_PARTIDO = "Partido";

    public PartidoServiceImpl(RestClient footballClient,
            PartidoRepository partidoRepository,
            UsuarioService usuarioService,
            SeleccionRepository seleccionRepository,
            EventoAuditoriaService auditoriaService) {
        this.footballClient = footballClient;
        this.partidoRepository = partidoRepository;
        this.usuarioService = usuarioService;
        this.seleccionRepository = seleccionRepository;
        this.auditoriaService = auditoriaService;
    }

    /**
     * Retorna todos los partidos del Mundial 2026 consultando la API externa
     *
     * @return lista de {@link PartidoDTO} con todos los partidos del torneo
     */
    @Transactional(readOnly = true)
    @Override
    public List<PartidoDTO> obtenerPartidos() {
        try {
            final PartidoResponseDTO response = footballClient.get()
                    .uri(BASE_FIXTURES)
                    .retrieve()
                    .body(PartidoResponseDTO.class);
            return response.getPartidos();
        } catch (RestClientException e) {
            logger.warning("API Football no disponible en obtenerPartidos, usando BD local: " + e.getMessage());
            return partidoRepository.findAll().stream().map(this::partidoADTO).toList();
        }
    }

    /**
     * Retorna todos los partidos en los que participa una selección específica,
     * consultando la API externa por id de equipo
     *
     * @param equipoId id del equipo en la API externa
     * @return lista de {@link PartidoDTO} con los partidos del equipo indicado
     */
    @Transactional(readOnly = true)
    @Override
    public List<PartidoDTO> obtenerPartidosPorEquipo(Long equipoId) {
        final PartidoResponseDTO response = footballClient.get()
                .uri(BASE_FIXTURES + "&team=" + equipoId)
                .retrieve()
                .body(PartidoResponseDTO.class);
        return response.getPartidos();
    }

    /**
     * Retorna un partido específico de la API externa por su id de fixture
     *
     * @param fixtureId id del fixture en la API externa
     * @return {@link PartidoDTO} con los datos del partido
     * @throws PartidoNotFoundException si la API no devuelve ningún partido con ese id
     */
    @Transactional(readOnly = true)
    @Override
    public PartidoDTO obtenerPartidoPorId(Long fixtureId) {
        final PartidoResponseDTO response = footballClient.get()
                .uri("/fixtures?id=" + fixtureId)
                .retrieve()
                .body(PartidoResponseDTO.class);
        if (response.getPartidos() == null || response.getPartidos().isEmpty()) {
            throw new PartidoNotFoundException("Partido no encontrado con id: " + fixtureId);
        }
        return response.getPartidos().get(0);
    }

    /**
     * Retorna las tablas de posiciones del Mundial 2026 consultando la API externa
     *
     * @return lista de listas de {@link PosicionDTO} con los grupos y posiciones de cada selección
     */
    @Transactional(readOnly = true)
    @Override
    public List<List<PosicionDTO>> obtenerStandings() {
        final StandingResponseDTO response = footballClient.get()
                .uri("/standings?league=" + LIGA_MUNDIAL + SEASON_PARAM + TEMPORADA_MUNDIAL)
                .retrieve()
                .body(StandingResponseDTO.class);
        return response.getRespuesta().get(0).getTablas().getTablas();
    }

    /**
     * Retorna todas las selecciones participantes del Mundial 2026 desde la API externa
     *
     * @return lista de {@link EquipoMundialDTO} con los datos de cada selección
     */
    @Transactional(readOnly = true)
    @Override
    public List<EquipoMundialDTO> obtenerSelecciones() {
        final EquipoMundialResponseDTO response = footballClient.get()
                .uri("/teams?league=" + LIGA_MUNDIAL + SEASON_PARAM + TEMPORADA_MUNDIAL)
                .retrieve()
                .body(EquipoMundialResponseDTO.class);
        return response.getEquipos();
    }

    /**
     * Retorna la nómina de jugadores de un equipo específico consultando la API externa
     *
     * @param equipoId id del equipo en la API externa
     * @return lista de {@link JugadorDTO} con los jugadores del equipo
     */
    @Transactional(readOnly = true)
    @Override
    public List<JugadorDTO> obtenerJugadoresPorEquipo(Long equipoId) {
        final JugadorResponseDTO response = footballClient.get()
                .uri("/players/squads?team=" + equipoId)
                .retrieve()
                .body(JugadorResponseDTO.class);
        return response.getRespuesta().get(0).getJugadores();
    }

    /**
     * Retorna todos los partidos del Mundial 2026 que se juegan en una fecha específica,
     * consultando la API externa
     *
     * @param fecha fecha en formato yyyy-MM-dd
     * @return lista de {@link PartidoDTO} con los partidos de esa fecha
     */
    @Transactional(readOnly = true)
    @Override
    public List<PartidoDTO> obtenerPartidosPorFecha(String fecha) {
        try {
            final PartidoResponseDTO response = footballClient.get()
                    .uri(BASE_FIXTURES + "&date=" + fecha)
                    .retrieve()
                    .body(PartidoResponseDTO.class);
            return response.getPartidos();
        } catch (RestClientException e) {
            logger.warning("API Football no disponible en obtenerPartidosPorFecha, usando BD local: " + e.getMessage());
            return partidoRepository.findAll().stream()
                    .filter(p -> p.getFecha() != null && p.getFecha().toLocalDate().toString().equals(fecha))
                    .map(this::partidoADTO).toList();
        }
    }

    /**
     * Retorna los partidos del Mundial 2026 que están en curso en este momento,
     * consultando la API externa en tiempo real
     *
     * @return lista de {@link PartidoDTO} con los partidos en vivo
     */
    @Transactional(readOnly = true)
    @Override
    public List<PartidoDTO> obtenerPartidosEnVivo() {
        try {
            final PartidoResponseDTO response = footballClient.get()
                    .uri("/fixtures?live=all&league=" + LIGA_MUNDIAL + SEASON_PARAM + TEMPORADA_MUNDIAL)
                    .retrieve()
                    .body(PartidoResponseDTO.class);
            return response.getPartidos();
        } catch (RestClientException e) {
            logger.warning("API Football no disponible en obtenerPartidosEnVivo, usando BD local: " + e.getMessage());
            return partidoRepository.findAll().stream()
                    .filter(p -> "1H".equals(p.getEstado()) || "2H".equals(p.getEstado())
                            || "HT".equals(p.getEstado()) || "LIVE".equals(p.getEstado()))
                    .map(this::partidoADTO).toList();
        }
    }

    /**
     * Sincroniza los partidos de una fecha, liga y temporada específicas desde la API externa
     * hacia la base de datos local. Si el partido ya existe, lo actualiza; si no, lo crea
     *
     * @param fecha     fecha a sincronizar en formato yyyy-MM-dd
     * @param liga      id de la liga en la API externa
     * @param temporada año de la temporada
     * @return cantidad de partidos procesados y guardados
     */
    @Transactional
    @Override
    public int sincronizarPorFechaYLiga(String fecha, int liga, int temporada) {
        final PartidoResponseDTO response = footballClient.get()
                .uri("/fixtures?league=" + liga + SEASON_PARAM + temporada + "&date=" + fecha)
                .retrieve()
                .body(PartidoResponseDTO.class);

        final List<Partido> partidos = response.getPartidos().stream()
                .map(this::procesarPartido)
                .toList();

        partidoRepository.saveAll(partidos);
        return partidos.size();
    }

    /**
     * Retorna todos los partidos en los que participan las selecciones favoritas del usuario,
     * consultando la API externa por cada selección registrada en su perfil
     *
     * @param correo correo del usuario
     * @return lista de {@link PartidoDTO} con los partidos de sus selecciones favoritas
     */
    @Transactional(readOnly = true)
    @Override
    public List<PartidoDTO> obtenerPartidosPorSeleccionesFav(String correo) {
        final Usuario usuario = usuarioService.obtenerEntidadPorCorreo(correo);
        final List<PartidoDTO> listaPartidos = new ArrayList<>();
        for (int i = 0; i < usuario.getSeleccionesU().size(); i++) {
            List<PartidoDTO> partidos = obtenerPartidosPorEquipo(usuario.getSeleccionesU().get(i).getId());
            listaPartidos.addAll(partidos);
        }
        return listaPartidos;
    }

    /**
     * Retorna todos los partidos que se juegan en los estadios favoritos del usuario,
     * filtrando los partidos de la API por el nombre del estadio registrado en sus preferencias
     *
     * @param correo correo del usuario
     * @return lista de {@link PartidoDTO} con los partidos en sus estadios favoritos
     */
    @Transactional(readOnly = true)
    @Override
    public List<PartidoDTO> obtenerPartidosPorEstadiosFav(final String correo) {
        final Usuario usuario = usuarioService.obtenerEntidadPorCorreo(correo);
        final List<PartidoDTO> listaPartidos = new ArrayList<>();
        final PartidoResponseDTO response = footballClient.get()
                .uri(BASE_FIXTURES)
                .retrieve()
                .body(PartidoResponseDTO.class);
        for (int i = 0; i < usuario.getPreferenciasu().size(); i++) {
            final String nombreEstadio = usuario.getPreferenciasu().get(i).getNombre();
            response.getPartidos().stream()
                    .filter(p -> {
                        final String nombre = p.getInformacion().getEstadio().getNombre();
                        return nombre != null && nombre.equalsIgnoreCase(nombreEstadio);
                    })
                    .forEach(listaPartidos::add);
        }
        return listaPartidos;
    }

    /**
     * Retorna todos los partidos que se juegan en ciudades favoritas del usuario.
     * Usa el mapa de estadios a ciudades para determinar en qué ciudad se juega cada partido
     *
     * @param correo correo del usuario
     * @return lista de {@link PartidoDTO} con los partidos en sus ciudades favoritas
     */
    @Transactional(readOnly = true)
    @Override
    public List<PartidoDTO> obtenerPartidosPorCiudadesFav(final String correo) {
        final Usuario usuario = usuarioService.obtenerEntidadPorCorreo(correo);
        final List<PartidoDTO> listaPartidos = new ArrayList<>();
        final PartidoResponseDTO response = footballClient.get()
                .uri(BASE_FIXTURES)
                .retrieve()
                .body(PartidoResponseDTO.class);

        for (int i = 0; i < usuario.getCiudadFavoritas().size(); i++) {
            final String nombreCiudad = usuario.getCiudadFavoritas().get(i).getNombre();
            for (int j = 0; j < response.getPartidos().size(); j++) {
                final String nombreEstadio = response.getPartidos().get(j)
                        .getInformacion().getEstadio().getNombre();
                if (nombreEstadio != null) {
                    final String ciudadDelEstadio = ESTADIO_CIUDAD.get(nombreEstadio);
                    if (ciudadDelEstadio != null && ciudadDelEstadio.equalsIgnoreCase(nombreCiudad)) {
                        listaPartidos.add(response.getPartidos().get(j));
                    }
                }
            }
        }
        return listaPartidos;
    }

    /**
     * Filtra los partidos almacenados en la base de datos local por el nombre de una selección,
     * ya sea como local o visitante
     *
     * @param nombre nombre de la selección a buscar
     * @return lista de {@link Partido} que involucran a esa selección
     */
    @Transactional(readOnly = true)
    @Override
    public List<Partido> filtrarPorSeleccion(final String nombre) {
        return partidoRepository.findBySeleccion(nombre);
    }

    /**
     * Filtra los partidos almacenados en la base de datos local por el nombre del estadio
     *
     * @param nombre nombre del estadio a buscar
     * @return lista de {@link Partido} que se juegan en ese estadio
     */
    @Transactional(readOnly = true)
    @Override
    public List<Partido> filtrarPorEstadio(final String nombre) {
        return partidoRepository.findByEstadio(nombre);
    }

    /**
     * Filtra los partidos almacenados en la base de datos local por ciudad,
     * usando el mapa de estadios a ciudades para determinar la ubicación de cada partido
     *
     * @param nombre nombre de la ciudad a buscar
     * @return lista de {@link Partido} que se juegan en esa ciudad
     */
    @Transactional(readOnly = true)
    @Override
    public List<Partido> filtrarPorCiudad(final String nombre) {
        return partidoRepository.findAll().stream()
                .filter(p -> {
                    final String ciudadDelEstadio = ESTADIO_CIUDAD.get(p.getEstadio());
                    return ciudadDelEstadio != null && ciudadDelEstadio.equalsIgnoreCase(nombre);
                })
                .toList();
    }

    /**
     * Convierte un {@link PartidoDTO} de la API externa a una entidad {@link Partido} para persistir.
     * Si el partido ya existe en la base de datos, lo actualiza; si no, crea uno nuevo.
     * Los goles solo se asignan si el objeto de goles está presente en la respuesta
     *
     * @param dto datos del partido provenientes de la API externa
     * @return entidad {@link Partido} lista para guardar
     */
    private PartidoDTO partidoADTO(final Partido p) {
        final InfoPartidoDTO info = new InfoPartidoDTO();
        info.setId(p.getId());
        info.setFecha(p.getFecha() != null ? p.getFecha().toString() + "Z" : null);
        final EstadoDTO estado = new EstadoDTO();
        estado.setCodigo(p.getEstado() != null ? p.getEstado() : "NS");
        estado.setDescripcion(p.getEstado() != null ? p.getEstado() : "NS");
        info.setEstado(estado);
        final EstadioDTO estadio = new EstadioDTO();
        estadio.setNombre(p.getEstadio() != null ? p.getEstadio() : POR_CONFIRMAR);
        estadio.setCiudad(ESTADIO_CIUDAD.getOrDefault(p.getEstadio(), POR_CONFIRMAR));
        info.setEstadio(estadio);

        final LigaDTO liga = new LigaDTO();
        liga.setRonda(p.getRonda() != null ? p.getRonda() : "");
        liga.setNombre("FIFA World Cup");

        final EquipoDTO local = new EquipoDTO();
        local.setNombre(p.getSeleccionLocal() != null ? p.getSeleccionLocal() : POR_CONFIRMAR);
        final EquipoDTO visitante = new EquipoDTO();
        visitante.setNombre(p.getSeleccionVisitante() != null ? p.getSeleccionVisitante() : POR_CONFIRMAR);
        final EquipoConEstadioDTO equipos = new EquipoConEstadioDTO();
        equipos.setLocal(local);
        equipos.setVisitante(visitante);

        final MarcadorDTO goles = new MarcadorDTO();
        goles.setLocal(p.getGolesLocal());
        goles.setVisitante(p.getGolesVisitante());

        final PartidoDTO dto = new PartidoDTO();
        dto.setInformacion(info);
        dto.setLiga(liga);
        dto.setEquipos(equipos);
        dto.setGoles(goles);
        return dto;
    }

    private Partido procesarPartido(final PartidoDTO dto) {
        final Long partidoId = dto.getInformacion().getId();
        final Partido partido = partidoRepository.findById(partidoId).orElse(new Partido());
        partido.setId(partidoId);
        partido.setFecha(LocalDateTime.parse(dto.getInformacion().getFecha(),
                DateTimeFormatter.ISO_OFFSET_DATE_TIME));
        partido.setEstado(dto.getInformacion().getEstado().getCodigo());
        partido.setRonda(dto.getLiga().getRonda());
        partido.setSeleccionLocal(dto.getEquipos().getLocal().getNombre());
        partido.setSeleccionVisitante(dto.getEquipos().getVisitante().getNombre());
        partido.setEstadio(dto.getInformacion().getEstadio().getNombre());
        if (dto.getGoles() != null) {
            partido.setGolesLocal(dto.getGoles().getLocal());
            partido.setGolesVisitante(dto.getGoles().getVisitante());
        }
        return partido;
    }

    /**
     * Retorna el catálogo de selecciones disponibles en la base de datos local,
     * usado para que el usuario configure sus selecciones favoritas
     *
     * @return lista de {@link PreferenciaDTO} con id y nombre de cada selección
     */
    @Transactional(readOnly = true)
    @Override
    public List<PreferenciaDTO> obtenerCatalogoSelecciones() {
        return seleccionRepository.findAll().stream()
                .map(s -> new PreferenciaDTO(s.getId(), s.getNombre()))
                .toList();
    }

    /**
     * Retorna la entidad {@link Partido} directamente desde la base de datos local por su id.
     * Usado internamente por otros servicios que necesitan la entidad completa
     *
     * @param partidoId id del partido en la base de datos
     * @return entidad {@link Partido} encontrada
     * @throws PartidoNotFoundException si el partido no existe en la base de datos
     */
    @Override
    @Transactional(readOnly = true)
    public Partido obtenerPartidoEntidadPorId(final Long partidoId) {
        return partidoRepository.findById(partidoId)
                .orElseThrow(() -> new PartidoNotFoundException(
                        "Partido no encontrado en base de datos con id: " + partidoId));
    }

    /**
     * Actualiza la capacidad disponible de un partido sumando o restando la cantidad indicada.
     * Se usa al reservar entradas (valor negativo) o al cancelar/expirar reservas (valor positivo)
     *
     * @param partidoId id del partido a actualizar
     * @param cantidad  cantidad a sumar o restar a la capacidad disponible
     * @throws PartidoNotFoundException si el partido no existe
     */
    @Override
    @Transactional
    public void actualizarCapacidad(final Long partidoId, final int cantidad) {
        final Partido partido = partidoRepository.findById(partidoId)
                .orElseThrow(() -> new PartidoNotFoundException(
                        "Partido no encontrado con id: " + partidoId));
        partido.setCapacidadDisponible(partido.getCapacidadDisponible() + cantidad);
        partidoRepository.save(partido);
    }

    /**
     * Retorna todos los partidos almacenados en la base de datos con su capacidad disponible
     * y la ciudad correspondiente según el mapa de estadios.
     * Si la ciudad no está mapeada, se muestra POR_CONFIRMAR
     *
     * @return lista de {@link PartidoCapacidadDTO} con cada partido y sus cupos disponibles
     */
    @Override
    @Transactional(readOnly = true)
    public List<PartidoCapacidadDTO> listarPartidosConCapacidad() {
        final List<Partido> partidos = partidoRepository.findAll();
        final List<PartidoCapacidadDTO> dtos = new ArrayList<>();
        for (final Partido p : partidos) {
            final PartidoCapacidadDTO dto = new PartidoCapacidadDTO();
            dto.setId(p.getId());
            dto.setLocal(p.getSeleccionLocal());
            dto.setVisitante(p.getSeleccionVisitante());
            dto.setEstadio(p.getEstadio());
            dto.setCiudad(ESTADIO_CIUDAD.getOrDefault(p.getEstadio(), POR_CONFIRMAR));
            dto.setCapacidadDisponible(p.getCapacidadDisponible() != null ? p.getCapacidadDisponible() : 60000);
            dto.setRonda(p.getRonda());
            dtos.add(dto);
        }
        return dtos;
    }

    /**
     * Retorna todos los partidos almacenados en la base de datos local sin ningún filtro
     *
     * @return lista de entidades {@link Partido} con todos los partidos registrados
     */
    @Override
    @Transactional(readOnly = true)
    public List<Partido> listarDesdeBD() {
        return partidoRepository.findAll();
    }

    /**
     * Actualiza manualmente el resultado de un partido en la base de datos.
     * Guarda los goles anteriores en auditoría para tener trazabilidad del cambio
     *
     * @param partidoId      id del partido a actualizar
     * @param golesLocal     nuevos goles del equipo local
     * @param golesVisitante nuevos goles del equipo visitante
     * @param estadoPartido  nuevo código de estado del partido
     * @return 1 si la actualización fue exitosa
     * @throws PartidoNotFoundException si el partido no existe
     */
    @Transactional
    @Override
    public int actualizarResultado(Long partidoId, int golesLocal,
            int golesVisitante, int estadoPartido) {
        final Partido partido = partidoRepository.findById(partidoId)
                .orElseThrow(() -> new PartidoNotFoundException(
                        "Partido no encontrado con id: " + partidoId));

        final Integer golesLocalAnterior = partido.getGolesLocal();
        final Integer golesVisitanteAnterior = partido.getGolesVisitante();

        partido.setGolesLocal(golesLocal);
        partido.setGolesVisitante(golesVisitante);
        partido.setEstado(String.valueOf(estadoPartido));
        partidoRepository.save(partido);

        auditoriaService.registrar(
                "PARTIDO_RESULTADO_ACTUALIZADO",
                partido.getSeleccionLocal() + " vs " + partido.getSeleccionVisitante()
                        + " | antes: " + golesLocalAnterior + "-" + golesVisitanteAnterior
                        + " | después: " + golesLocal + "-" + golesVisitante,
                null,
                UUID.randomUUID().toString(),
                ENTIDAD_PARTIDO);

        return 1;
    }

    /**
     * Sincroniza todos los partidos del Mundial 2026 desde la API externa hacia la base de datos.
     * Si la API no devuelve partidos, registra una advertencia en el log y retorna 0.
     * La operación queda registrada en auditoría con la cantidad de partidos procesados
     *
     * @return cantidad de partidos sincronizados, o 0 si la API no devolvió datos
     */
    @Transactional
    @Override
    public int sincronizarDesdeAPI() {
        final PartidoResponseDTO response = footballClient.get()
                .uri(BASE_FIXTURES)
                .retrieve()
                .body(PartidoResponseDTO.class);

        if (response == null || response.getPartidos() == null
                || response.getPartidos().isEmpty()) {
            logger.warning("La API no devolvió partidos, se omite sincronización.");
            return 0;
        }

        final List<Partido> partidos = response.getPartidos().stream()
                .map(this::procesarPartido)
                .toList();

        partidoRepository.saveAll(partidos);

        auditoriaService.registrar(
                "PARTIDOS_SINCRONIZADOS",
                "Sincronizacion manual desde API | partidos procesados: " + partidos.size(),
                null,
                UUID.randomUUID().toString(),
                ENTIDAD_PARTIDO);

        return partidos.size();
    }
}