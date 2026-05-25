package co.edu.unbosque.mundial_2026.integration;

import co.edu.unbosque.mundial_2026.dto.response.EquipoMundialDTO;
import co.edu.unbosque.mundial_2026.dto.response.JugadorDTO;
import co.edu.unbosque.mundial_2026.dto.response.PartidoDTO;
import co.edu.unbosque.mundial_2026.dto.response.PreferenciaDTO;
import co.edu.unbosque.mundial_2026.entity.Partido;
import co.edu.unbosque.mundial_2026.service.PartidoService;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Pruebas de integracion para los endpoints de partidos
 * 
 * Valida:
 * autenticacion
 * autorizacion
 * acceso a informacion de partidos
 * sincronizacion
 * actualizacion de resultados
 * y consultas por preferencias
 */
class PartidoIT extends BaseIntegrationTest {

    /**
     * URL base de partidos
     */
    private static final String BASE_URL = "/api/partidos";

    /**
     * URL para consultar partidos por equipo
     */
    private static final String URL_EQUIPO = "/api/partidos/equipo/1";

    /**
     * URL para consultar standings
     */
    private static final String URL_STANDINGS = "/api/partidos/standings";

    /**
     * URL para consultar selecciones
     */
    private static final String URL_SELECCIONES = "/api/partidos/selecciones";

    /**
     * URL para consultar jugadores por seleccion
     */
    private static final String URL_JUGADORES =
            "/api/partidos/selecciones/1/jugadores";

    /**
     * URL para consultar partidos por fecha
     */
    private static final String URL_FECHA =
            "/api/partidos/fecha/2026-06-10";

    /**
     * URL para consultar partidos en vivo
     */
    private static final String URL_ENVIVO = "/api/partidos/envivo";

    /**
     * URL para consultar partidos por selecciones favoritas
     */
    private static final String URL_PREF_SELECCIONES =
            "/api/partidos/preferencias/selecciones";

    /**
     * URL para consultar partidos por estadios favoritos
     */
    private static final String URL_PREF_ESTADIOS =
            "/api/partidos/preferencias/estadios";

    /**
     * URL para consultar partidos por ciudades favoritas
     */
    private static final String URL_PREF_CIUDADES =
            "/api/partidos/preferencias/ciudades";

    /**
     * URL para sincronizacion de partidos
     */
    private static final String URL_SINCRONIZAR =
            "/api/partidos/sincronizar/1/2026/2026-06-10";

    /**
     * URL para actualizar resultados
     */
    private static final String URL_RESULTADO =
            "/api/partidos/1/resultado/2/1/90";

    /**
     * URL para consultar partido por id
     */
    private static final String URL_POR_ID = "/api/partidos/1";

    /**
     * URL para consultar catalogo de selecciones
     */
    private static final String URL_CATALOGO =
            "/api/partidos/catalogo/selecciones";

    /**
     * URL para listar partidos desde base de datos
     */
    private static final String URL_BD_TODOS =
            "/api/partidos/bd/todos";

    /**
     * Header de autorizacion
     */
    private static final String AUTH_HEADER = "Authorization";

    /**
     * Prefijo Bearer para autenticacion JWT
     */
    private static final String BEARER_PREFIX = "Bearer ";

    @MockitoBean
    private PartidoService partidoService;

    /**
     * Verifica acceso denegado al listado de partidos
     * cuando no se envia token
     * 
     * @throws Exception error durante la prueba
     */
    @Test
    void listarPartidos_sinToken_retorna401() throws Exception {

        mockMvc.perform(get(BASE_URL))
                .andExpect(status().isUnauthorized());
    }

    /**
     * Verifica consulta exitosa de partidos por equipo
     * con usuario autenticado
     * 
     * @throws Exception error durante la prueba
     */
    @Test
    void obtenerPartidosPorEquipo_conToken_retorna200() throws Exception {

        when(partidoService.obtenerPartidosPorEquipo(1L))
                .thenReturn(List.of());

        mockMvc.perform(get(URL_EQUIPO)
                        .header(AUTH_HEADER,
                                BEARER_PREFIX + tokenUsuario()))
                .andExpect(status().isOk());
    }

    /**
     * Verifica acceso no autorizado a partidos por equipo
     * cuando no se envia token
     * 
     * @throws Exception error durante la prueba
     */
    @Test
    void obtenerPartidosPorEquipo_sinToken_retorna401() throws Exception {

        mockMvc.perform(get(URL_EQUIPO))
                .andExpect(status().isUnauthorized());
    }

    /**
     * Verifica consulta exitosa de standings
     * con usuario autenticado
     * 
     * @throws Exception error durante la prueba
     */
    @Test
    void obtenerStandings_conToken_retorna200() throws Exception {

        when(partidoService.obtenerStandings())
                .thenReturn(List.of());

        mockMvc.perform(get(URL_STANDINGS)
                        .header(AUTH_HEADER,
                                BEARER_PREFIX + tokenUsuario()))
                .andExpect(status().isOk());
    }

    /**
     * Verifica acceso no autorizado a standings
     * cuando no se envia token
     * 
     * @throws Exception error durante la prueba
     */
    @Test
    void obtenerStandings_sinToken_retorna401() throws Exception {

        mockMvc.perform(get(URL_STANDINGS))
                .andExpect(status().isUnauthorized());
    }

    /**
     * Verifica consulta exitosa de selecciones
     * con usuario autenticado
     * 
     * @throws Exception error durante la prueba
     */
    @Test
    void obtenerSelecciones_conToken_retorna200() throws Exception {

        when(partidoService.obtenerSelecciones())
                .thenReturn(List.of(new EquipoMundialDTO()));

        mockMvc.perform(get(URL_SELECCIONES)
                        .header(AUTH_HEADER,
                                BEARER_PREFIX + tokenUsuario()))
                .andExpect(status().isOk());
    }

    /**
     * Verifica acceso no autorizado a selecciones
     * cuando no se envia token
     * 
     * @throws Exception error durante la prueba
     */
    @Test
    void obtenerSelecciones_sinToken_retorna401() throws Exception {

        mockMvc.perform(get(URL_SELECCIONES))
                .andExpect(status().isUnauthorized());
    }

    /**
     * Verifica consulta exitosa de jugadores por equipo
     * con usuario autenticado
     * 
     * @throws Exception error durante la prueba
     */
    @Test
    void obtenerJugadoresPorEquipo_conToken_retorna200() throws Exception {

        when(partidoService.obtenerJugadoresPorEquipo(1L))
                .thenReturn(List.of(new JugadorDTO()));

        mockMvc.perform(get(URL_JUGADORES)
                        .header(AUTH_HEADER,
                                BEARER_PREFIX + tokenUsuario()))
                .andExpect(status().isOk());
    }

    /**
     * Verifica acceso no autorizado a jugadores por equipo
     * cuando no se envia token
     * 
     * @throws Exception error durante la prueba
     */
    @Test
    void obtenerJugadoresPorEquipo_sinToken_retorna401() throws Exception {

        mockMvc.perform(get(URL_JUGADORES))
                .andExpect(status().isUnauthorized());
    }

    /**
     * Verifica consulta exitosa de partidos por fecha
     * con usuario autenticado
     * 
     * @throws Exception error durante la prueba
     */
    @Test
    void obtenerPartidosPorFecha_conToken_retorna200() throws Exception {

        when(partidoService.obtenerPartidosPorFecha("2026-06-10"))
                .thenReturn(List.of());

        mockMvc.perform(get(URL_FECHA)
                        .header(AUTH_HEADER,
                                BEARER_PREFIX + tokenUsuario()))
                .andExpect(status().isOk());
    }

    /**
     * Verifica acceso no autorizado a partidos por fecha
     * cuando no se envia token
     * 
     * @throws Exception error durante la prueba
     */
    @Test
    void obtenerPartidosPorFecha_sinToken_retorna401() throws Exception {

        mockMvc.perform(get(URL_FECHA))
                .andExpect(status().isUnauthorized());
    }

    /**
     * Verifica consulta exitosa de partidos en vivo
     * con usuario autenticado
     * 
     * @throws Exception error durante la prueba
     */
    @Test
    void obtenerPartidosEnVivo_conToken_retorna200() throws Exception {

        when(partidoService.obtenerPartidosEnVivo())
                .thenReturn(List.of());

        mockMvc.perform(get(URL_ENVIVO)
                        .header(AUTH_HEADER,
                                BEARER_PREFIX + tokenUsuario()))
                .andExpect(status().isOk());
    }

    /**
     * Verifica acceso no autorizado a partidos en vivo
     * cuando no se envia token
     * 
     * @throws Exception error durante la prueba
     */
    @Test
    void obtenerPartidosEnVivo_sinToken_retorna401() throws Exception {

        mockMvc.perform(get(URL_ENVIVO))
                .andExpect(status().isUnauthorized());
    }

    /**
     * Verifica consulta de partidos por selecciones favoritas
     * con usuario autenticado
     * 
     * @throws Exception error durante la prueba
     */
    @Test
    void obtenerPartidosPorSeleccionesFav_conToken_retorna200()
            throws Exception {

        when(partidoService.obtenerPartidosPorSeleccionesFav(USER_EMAIL))
                .thenReturn(List.of());

        mockMvc.perform(get(URL_PREF_SELECCIONES)
                        .header(AUTH_HEADER,
                                BEARER_PREFIX + tokenUsuario()))
                .andExpect(status().isOk());
    }

    /**
     * Verifica acceso no autorizado a partidos
     * por selecciones favoritas
     * 
     * @throws Exception error durante la prueba
     */
    @Test
    void obtenerPartidosPorSeleccionesFav_sinToken_retorna401()
            throws Exception {

        mockMvc.perform(get(URL_PREF_SELECCIONES))
                .andExpect(status().isUnauthorized());
    }

    /**
     * Verifica consulta de partidos por estadios favoritos
     * con usuario autenticado
     * 
     * @throws Exception error durante la prueba
     */
    @Test
    void obtenerPartidosPorEstadiosFav_conToken_retorna200()
            throws Exception {

        when(partidoService.obtenerPartidosPorEstadiosFav(USER_EMAIL))
                .thenReturn(List.of());

        mockMvc.perform(get(URL_PREF_ESTADIOS)
                        .header(AUTH_HEADER,
                                BEARER_PREFIX + tokenUsuario()))
                .andExpect(status().isOk());
    }

    /**
     * Verifica acceso no autorizado a partidos
     * por estadios favoritos
     * 
     * @throws Exception error durante la prueba
     */
    @Test
    void obtenerPartidosPorEstadiosFav_sinToken_retorna401()
            throws Exception {

        mockMvc.perform(get(URL_PREF_ESTADIOS))
                .andExpect(status().isUnauthorized());
    }

    /**
     * Verifica consulta de partidos por ciudades favoritas
     * con usuario autenticado
     * 
     * @throws Exception error durante la prueba
     */
    @Test
    void obtenerPartidosPorCiudadesFav_conToken_retorna200()
            throws Exception {

        when(partidoService.obtenerPartidosPorCiudadesFav(USER_EMAIL))
                .thenReturn(List.of());

        mockMvc.perform(get(URL_PREF_CIUDADES)
                        .header(AUTH_HEADER,
                                BEARER_PREFIX + tokenUsuario()))
                .andExpect(status().isOk());
    }

    /**
     * Verifica acceso no autorizado a partidos
     * por ciudades favoritas
     * 
     * @throws Exception error durante la prueba
     */
    @Test
    void obtenerPartidosPorCiudadesFav_sinToken_retorna401()
            throws Exception {

        mockMvc.perform(get(URL_PREF_CIUDADES))
                .andExpect(status().isUnauthorized());
    }

    /**
     * Verifica sincronizacion de partidos
     * con rol administrador
     * 
     * @throws Exception error durante la prueba
     */
    @Test
    void sincronizar_conRolAdmin_retorna200() throws Exception {

        when(partidoService.sincronizarPorFechaYLiga(
                "2026-06-10",
                1,
                2026))
                .thenReturn(5);

        mockMvc.perform(get(URL_SINCRONIZAR)
                        .header(AUTH_HEADER,
                                BEARER_PREFIX + tokenAdmin()))
                .andExpect(status().isOk());
    }

    /**
     * Verifica acceso denegado a sincronizacion
     * para usuarios sin permisos administrativos
     * 
     * @throws Exception error durante la prueba
     */
    @Test
    void sincronizar_conRolUser_retorna403() throws Exception {

        mockMvc.perform(get(URL_SINCRONIZAR)
                        .header(AUTH_HEADER,
                                BEARER_PREFIX + tokenUsuario()))
                .andExpect(status().isForbidden());
    }

    /**
     * Verifica acceso no autorizado a sincronizacion
     * cuando no se envia token
     * 
     * @throws Exception error durante la prueba
     */
    @Test
    void sincronizar_sinToken_retorna401() throws Exception {

        mockMvc.perform(get(URL_SINCRONIZAR))
                .andExpect(status().isUnauthorized());
    }

    /**
     * Verifica actualizacion de resultados
     * con usuario autenticado
     * 
     * @throws Exception error durante la prueba
     */
    @Test
    void actualizarResultado_conToken_retorna200() throws Exception {

        when(partidoService.actualizarResultado(1L, 2, 1, 90))
                .thenReturn(1);

        mockMvc.perform(put(URL_RESULTADO)
                        .header(AUTH_HEADER,
                                BEARER_PREFIX + tokenUsuario()))
                .andExpect(status().isOk());
    }

    /**
     * Verifica acceso no autorizado a actualizacion
     * de resultados cuando no se envia token
     * 
     * @throws Exception error durante la prueba
     */
    @Test
    void actualizarResultado_sinToken_retorna401() throws Exception {

        mockMvc.perform(put(URL_RESULTADO))
                .andExpect(status().isUnauthorized());
    }

    /**
     * Verifica consulta de partido por identificador
     * con usuario autenticado
     * 
     * @throws Exception error durante la prueba
     */
    @Test
    void obtenerPorId_conToken_retorna200() throws Exception {

        when(partidoService.obtenerPartidoPorId(1L))
                .thenReturn(new PartidoDTO());

        mockMvc.perform(get(URL_POR_ID)
                        .header(AUTH_HEADER,
                                BEARER_PREFIX + tokenUsuario()))
                .andExpect(status().isOk());
    }

    /**
     * Verifica acceso no autorizado a consulta por id
     * cuando no se envia token
     * 
     * @throws Exception error durante la prueba
     */
    @Test
    void obtenerPorId_sinToken_retorna401() throws Exception {

        mockMvc.perform(get(URL_POR_ID))
                .andExpect(status().isUnauthorized());
    }

    /**
     * Verifica consulta del catalogo de selecciones
     * con usuario autenticado
     * 
     * @throws Exception error durante la prueba
     */
    @Test
    void obtenerCatalogoSelecciones_conToken_retorna200()
            throws Exception {

        when(partidoService.obtenerCatalogoSelecciones())
                .thenReturn(List.of(
                        new PreferenciaDTO(1L, "Test")));

        mockMvc.perform(get(URL_CATALOGO)
                        .header(AUTH_HEADER,
                                BEARER_PREFIX + tokenUsuario()))
                .andExpect(status().isOk());
    }

    /**
     * Verifica acceso no autorizado al catalogo
     * de selecciones cuando no se envia token
     * 
     * @throws Exception error durante la prueba
     */
    @Test
    void obtenerCatalogoSelecciones_sinToken_retorna401()
            throws Exception {

        mockMvc.perform(get(URL_CATALOGO))
                .andExpect(status().isUnauthorized());
    }

    /**
     * Verifica listado de partidos desde base de datos
     * con rol administrador
     * 
     * @throws Exception error durante la prueba
     */
    @Test
    void listarDesdeBD_conRolAdmin_retorna200() throws Exception {

        when(partidoService.listarDesdeBD())
                .thenReturn(List.of(new Partido()));

        mockMvc.perform(get(URL_BD_TODOS)
                        .header(AUTH_HEADER,
                                BEARER_PREFIX + tokenAdmin()))
                .andExpect(status().isOk());
    }

    /**
     * Verifica acceso denegado al listado desde base de datos
     * para usuarios sin permisos administrativos
     * 
     * @throws Exception error durante la prueba
     */
    @Test
    void listarDesdeBD_conRolUser_retorna403() throws Exception {

        mockMvc.perform(get(URL_BD_TODOS)
                        .header(AUTH_HEADER,
                                BEARER_PREFIX + tokenUsuario()))
                .andExpect(status().isForbidden());
    }

    /**
     * Verifica acceso no autorizado al listado desde base de datos
     * cuando no se envia token
     * 
     * @throws Exception error durante la prueba
     */
    @Test
    void listarDesdeBD_sinToken_retorna401() throws Exception {

        mockMvc.perform(get(URL_BD_TODOS))
                .andExpect(status().isUnauthorized());
    }
}