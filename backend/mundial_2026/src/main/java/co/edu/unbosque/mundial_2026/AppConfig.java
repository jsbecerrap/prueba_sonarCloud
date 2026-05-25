package co.edu.unbosque.mundial_2026;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import co.edu.unbosque.mundial_2026.dto.response.EquipoMundialDTO;
import co.edu.unbosque.mundial_2026.dto.response.EquipoInfoDTO;
import co.edu.unbosque.mundial_2026.entity.CiudadFavorita;
import co.edu.unbosque.mundial_2026.entity.EstadioFavorito;
import co.edu.unbosque.mundial_2026.entity.Rol;
import co.edu.unbosque.mundial_2026.entity.Seleccion;
import co.edu.unbosque.mundial_2026.repository.CiudadRepository;
import co.edu.unbosque.mundial_2026.repository.EstadioRepository;
import co.edu.unbosque.mundial_2026.repository.PartidoRepository;
import co.edu.unbosque.mundial_2026.repository.RolRepository;
import co.edu.unbosque.mundial_2026.repository.SeleccionRepository;
import co.edu.unbosque.mundial_2026.service.PartidoService;
/**
 * Configuración de arranque de la aplicación que inicializa los datos base
 * necesarios para el funcionamiento del sistema mediante {@link CommandLineRunner}
 *
 * <p>Cada bean se ejecuta en orden estricto al iniciar la aplicación y solo
 * inserta datos si la tabla correspondiente está vacía, evitando duplicados</p>
 */
@Configuration
public class AppConfig {

    private static final Logger logger = Logger.getLogger(AppConfig.class.getName());

    /**
     * Carga los roles del sistema si aún no existen en la base de datos
     * Los roles son: ROLE_USUARIO, ROLE_ADMIN, ROLE_OPERADOR, ROLE_SOPORTE y ROLE_LEGAL
     *
     * @param rolRepository repositorio JPA de {@link Rol}
     */
    @Bean
    @Order(0)
    public CommandLineRunner cargarRoles(RolRepository rolRepository) {
        return args -> {
            String[] roles = {
                    "ROLE_USUARIO",
                    "ROLE_ADMIN",
                    "ROLE_OPERADOR",
                    "ROLE_SOPORTE",
                    "ROLE_LEGAL"
            };
            for (String nombreRol : roles) {
                if (rolRepository.findByNombre(nombreRol).isEmpty()) {
                    Rol rol = new Rol();
                    rol.setNombre(nombreRol);
                    rolRepository.save(rol);
                }
            }
           if (logger.isLoggable(Level.INFO)) {
    logger.log(Level.INFO, "Roles cargados: {0}", roles.length);
}
        };
    }

    /**
     * Sincroniza los partidos del Mundial desde la API externa si la tabla
     * de partidos está vacía
     *
     * @param partidoService    servicio que consume la API externa de partidos
     * @param partidoRepository repositorio JPA de partidos para verificar si ya hay datos
     */
    @Bean
@Order(1)
@org.springframework.context.annotation.Profile("!test")
public CommandLineRunner cargarPartidos(PartidoService partidoService,
            PartidoRepository partidoRepository) {
        return args -> {
            if (partidoRepository.count() == 0) {
                int total = partidoService.sincronizarDesdeAPI();
                if (logger.isLoggable(Level.INFO)) {
                    logger.log(Level.INFO, "Partidos cargados: {0}", total);
                }
            }
        };
    }

    /**
     * Obtiene las selecciones participantes desde el servicio de partidos y las
     * persiste en base de datos si la tabla está vacía — evita duplicados
     * verificando el ID antes de guardar
     *
     * @param seleccionRepository repositorio JPA de {@link Seleccion}
     * @param partidoService      servicio que provee la lista de equipos del mundial
     */
    @Bean
@Order(2)
@org.springframework.context.annotation.Profile("!test")
public CommandLineRunner cargarSelecciones(
            SeleccionRepository seleccionRepository,
            PartidoService partidoService) {
        return args -> {
            if (seleccionRepository.count() == 0) {
                final List<EquipoMundialDTO> equipos = partidoService.obtenerSelecciones();//obtiene los equipos con seleccion y estadio para sacar la seleccion
                int total = 0;

                for (EquipoMundialDTO equipoDTO : equipos) {
                    final EquipoInfoDTO equipo = equipoDTO.getSeleccion();
                    if (equipo != null && equipo.getId() != null && !seleccionRepository.existsById(equipo.getId())) {//verifica que exista y no este 
                        Seleccion s = new Seleccion();
                        s.setId(equipo.getId());
                        s.setNombre(equipo.getNombre());
                        seleccionRepository.save(s);
                        total++;
                    }
                }
                if (logger.isLoggable(Level.INFO)) {
    logger.log(Level.INFO, "Selecciones cargadas: {0}", total);
}
            }
        };
    }

    /**
     * Carga los 14 estadios sede del Mundial 2026 junto con sus ciudades anfitrionas
     * (México, Canadá y EE.UU.) si la tabla de ciudades está vacía
     *
     * <p>Usa un mapa ordenado estadio→ciudad para garantizar que cada ciudad
     * se crea una sola vez aunque albergue varios estadios</p>
     *
     * @param ciudadRepository  repositorio JPA de {@link CiudadFavorita}
     * @param estadioRepository repositorio JPA de {@link EstadioFavorito}
     */
    @Bean
    @Order(3)
    public CommandLineRunner cargarCiudadesYEstadios(
            CiudadRepository ciudadRepository,
            EstadioRepository estadioRepository) {
        return args -> {
            if (ciudadRepository.count() == 0) {

                java.util.Map<String, String> estadioCiudad = new java.util.LinkedHashMap<>();
                estadioCiudad.put("Estadio Azteca", "Ciudad de Mexico");//ya que el api los devuelve vacios
                estadioCiudad.put("Estadio Akron", "Guadalajara");
                estadioCiudad.put("Estadio BBVA", "Monterrey");
                estadioCiudad.put("BMO Field", "Toronto");
                estadioCiudad.put("BC Place", "Vancouver");
                estadioCiudad.put("SoFi Stadium", "Los Angeles");
                estadioCiudad.put("MetLife Stadium", "East Rutherford");
                estadioCiudad.put("Gillette Stadium", "Boston");
                estadioCiudad.put("NRG Stadium", "Houston");
                estadioCiudad.put("Lincoln Financial Field", "Philadelphia");
                estadioCiudad.put("Mercedes-Benz Stadium", "Atlanta");
                estadioCiudad.put("Lumen Field", "Seattle");
                estadioCiudad.put("Hard Rock Stadium", "Miami");
                estadioCiudad.put("Arrowhead Stadium", "Kansas City");

                java.util.Map<String, CiudadFavorita> ciudadesGuardadas = new java.util.HashMap<>();
                long ciudadId = 1;
                long estadioId = 1;

                for (java.util.Map.Entry<String, String> entry : estadioCiudad.entrySet()) {
                    final String nombreEstadio = entry.getKey();
                    final String nombreCiudad = entry.getValue();

                    CiudadFavorita ciudad = ciudadesGuardadas.get(nombreCiudad);//retifica y guarda la ciudad(se hace asi es por si escala y una ciudad tiene 2 estadios no guardarla 2 veces)
                    if (ciudad == null) {
                        ciudad = new CiudadFavorita();
                        ciudad.setId(ciudadId);
                        ciudadId++;
                        ciudad.setNombre(nombreCiudad);
                        ciudad.setPais("USA/CAN/MEX");
                        ciudadRepository.save(ciudad);
                        ciudadesGuardadas.put(nombreCiudad, ciudad);
                    }

                    EstadioFavorito estadio = new EstadioFavorito();//guarda los estadios de igual manera secuencialmente 
                    estadio.setId(estadioId);
                    estadioId++;
                    estadio.setNombre(nombreEstadio);
                    estadio.setCiudad(ciudad);
                    estadioRepository.save(estadio);
                }

               if (logger.isLoggable(Level.INFO)) {
    logger.log(Level.INFO, "Ciudades cargadas: {0}", ciudadesGuardadas.size());
}
if (logger.isLoggable(Level.INFO)) {
    logger.log(Level.INFO, "Estadios cargados: {0}", estadioCiudad.size());
}
            }
        };
    }

    /**
     * Configura el {@link ObjectMapper} de Jackson deshabilitando la serialización
     * de fechas como timestamps y estableciendo UTC como zona horaria global
     */
    @Bean
    public com.fasterxml.jackson.databind.ObjectMapper objectMapper() {//traductor entre json y java 
        com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
        mapper.disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);//formato correcto de mandar las fechas
        mapper.setTimeZone(java.util.TimeZone.getTimeZone("UTC"));//convierte las fechas a donde este ubicado el programa ejecutando mas no el servidor 
        return mapper;
    }
}