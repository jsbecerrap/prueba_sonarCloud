package co.edu.unbosque.mundial_2026.integration;

import co.edu.unbosque.mundial_2026.dto.ApuestaConParticipantesDTO;
import co.edu.unbosque.mundial_2026.dto.ApuestaDTO;
import co.edu.unbosque.mundial_2026.dto.ParticipacionDTO;
import co.edu.unbosque.mundial_2026.dto.PronosticoDTO;
import co.edu.unbosque.mundial_2026.dto.request.ApuestaRequestDTO;
import co.edu.unbosque.mundial_2026.dto.request.PronosticoRequestDTO;
import co.edu.unbosque.mundial_2026.service.ApuestaService;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Pruebas de integración para el controlador de apuestas (pollas).
 *
 * <p>Extiende {@link BaseIntegrationTest} para heredar la configuración de MockMvc,
 * ObjectMapper y los métodos auxiliares {@code tokenUsuario()} y {@code tokenAdmin()},
 * que generan JWT válidos para cada rol sin levantar el servidor completo</p>
 *
 * <p>El {@link ApuestaService} se reemplaza por un mock de Mockito mediante
 * {@code @MockitoBean}, de modo que los tests validan únicamente la capa HTTP
 * (seguridad, validaciones de Bean Validation y mapeo de rutas) sin tocar
 * la lógica de negocio ni la base de datos</p>
 *
 * <p>Convención de nombres usada en todos los métodos:
 * {@code <accion>_<condicion>_retorna<HttpStatus>}</p>
 *
 * @author Equipo Mundial 2026
 * @version 1.0
 * @see BaseIntegrationTest
 * @see ApuestaService
 */
class ApuestaIT extends BaseIntegrationTest {

    /**
     * Mock del servicio de apuestas inyectado en el contexto de Spring para
     * interceptar llamadas reales durante las pruebas de integración
     */
    @MockitoBean
    private ApuestaService apuestaService;

    /** Prefijo estándar del esquema Bearer usado en el header de autorización */
    private static final String BEARER = "Bearer ";

    /** Ruta del endpoint que apunta a la apuesta con ID 1 */
    private static final String API_APUESTAS_ID = "/api/apuestas/1";

    /** Nombre del header HTTP de autorización */
    private static final String AUTH_HEADER = "Authorization";

    /** Nombre de apuesta usado como dato de prueba en múltiples tests */
    private static final String POLLA_MUNDIAL = "Polla Mundial";

    /** Ruta del endpoint para crear una apuesta */
    private static final String API_APUESTAS_CREAR = "/api/apuestas/crear";

    /** Ruta del endpoint para registrar o editar pronósticos */
    private static final String API_APUESTAS_PRONOSTICO = "/api/apuestas/pronostico";

    /**
     * Verifica que un usuario autenticado puede crear una apuesta con datos válidos
     * y que el endpoint responde con HTTP 200
     *
     * @throws Exception si MockMvc falla al ejecutar la petición
     */
    @Test
    void crear_conDatosValidos_retorna200() throws Exception {
        ApuestaRequestDTO request = new ApuestaRequestDTO();
        request.setNombre(POLLA_MUNDIAL);
        request.setFechaCierre(LocalDateTime.now().plusDays(10));
        request.setUsuarioId(1L);

        ApuestaDTO response = new ApuestaDTO();
        when(apuestaService.crearApuesta(any())).thenReturn(response);

        mockMvc.perform(post(API_APUESTAS_CREAR)
                        .header(AUTH_HEADER, BEARER + tokenUsuario())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    /**
     * Verifica que una petición de creación sin token JWT es rechazada
     * con HTTP 401 Unauthorized
     *
     * @throws Exception si MockMvc falla al ejecutar la petición
     */
    @Test
    void crear_sinToken_retorna401() throws Exception {
        ApuestaRequestDTO request = new ApuestaRequestDTO();
        request.setNombre(POLLA_MUNDIAL);
        request.setFechaCierre(LocalDateTime.now().plusDays(10));
        request.setUsuarioId(1L);

        mockMvc.perform(post(API_APUESTAS_CREAR)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    /**
     * Verifica que enviar un nombre vacío al crear una apuesta
     * dispara la validación de Bean Validation y retorna HTTP 400
     *
     * @throws Exception si MockMvc falla al ejecutar la petición
     */
    @Test
    void crear_nombreVacio_retorna400() throws Exception {
        ApuestaRequestDTO request = new ApuestaRequestDTO();
        request.setNombre("");
        request.setFechaCierre(LocalDateTime.now().plusDays(10));
        request.setUsuarioId(1L);

        mockMvc.perform(post(API_APUESTAS_CREAR)
                        .header(AUTH_HEADER, BEARER + tokenUsuario())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    /**
     * Verifica que un nombre con menos caracteres de los requeridos
     * es rechazado por Bean Validation con HTTP 400
     *
     * @throws Exception si MockMvc falla al ejecutar la petición
     */
    @Test
    void crear_nombreDemasiadoCorto_retorna400() throws Exception {
        ApuestaRequestDTO request = new ApuestaRequestDTO();
        request.setNombre("AB");
        request.setFechaCierre(LocalDateTime.now().plusDays(10));
        request.setUsuarioId(1L);

        mockMvc.perform(post(API_APUESTAS_CREAR)
                        .header(AUTH_HEADER, BEARER + tokenUsuario())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    /**
     * Verifica que una fecha de cierre en el pasado es rechazada
     * por Bean Validation con HTTP 400
     *
     * @throws Exception si MockMvc falla al ejecutar la petición
     */
    @Test
    void crear_fechaPasada_retorna400() throws Exception {
        ApuestaRequestDTO request = new ApuestaRequestDTO();
        request.setNombre(POLLA_MUNDIAL);
        request.setFechaCierre(LocalDateTime.now().minusDays(1));
        request.setUsuarioId(1L);

        mockMvc.perform(post(API_APUESTAS_CREAR)
                        .header(AUTH_HEADER, BEARER + tokenUsuario())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    /**
     * Verifica que omitir el {@code usuarioId} en la petición de creación
     * dispara la validación de Bean Validation y retorna HTTP 400
     *
     * @throws Exception si MockMvc falla al ejecutar la petición
     */
    @Test
    void crear_usuarioIdNulo_retorna400() throws Exception {
        ApuestaRequestDTO request = new ApuestaRequestDTO();
        request.setNombre(POLLA_MUNDIAL);
        request.setFechaCierre(LocalDateTime.now().plusDays(10));

        mockMvc.perform(post(API_APUESTAS_CREAR)
                        .header(AUTH_HEADER, BEARER + tokenUsuario())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    /**
     * Verifica que un usuario autenticado puede unirse a una apuesta
     * enviando un código con formato válido y recibe HTTP 200
     *
     * @throws Exception si MockMvc falla al ejecutar la petición
     */
    @Test
    void unirse_codigoValido_retorna200() throws Exception {
        when(apuestaService.unirseApuesta(eq("CODIGO123"), eq(1L))).thenReturn(new ApuestaDTO());

        mockMvc.perform(post("/api/apuestas/unirse/1")
                        .header(AUTH_HEADER, BEARER + tokenUsuario())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("\"CODIGO123\""))
                .andExpect(status().isOk());
    }

    /**
     * Verifica que un código de invitación demasiado corto o con formato
     * inválido es rechazado con HTTP 400
     *
     * @throws Exception si MockMvc falla al ejecutar la petición
     */
    @Test
    void unirse_codigoFormatoInvalido_retorna400() throws Exception {
        mockMvc.perform(post("/api/apuestas/unirse/1")
                        .header(AUTH_HEADER, BEARER + tokenUsuario())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("\"co\""))
                .andExpect(status().isBadRequest());
    }

    /**
     * Verifica que intentar unirse sin token JWT retorna HTTP 401
     *
     * @throws Exception si MockMvc falla al ejecutar la petición
     */
    @Test
    void unirse_sinToken_retorna401() throws Exception {
        mockMvc.perform(post("/api/apuestas/unirse/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("\"CODIGO123\""))
                .andExpect(status().isUnauthorized());
    }

    /**
     * Verifica que un usuario autenticado puede registrar un pronóstico
     * con todos los campos válidos y recibe HTTP 200
     *
     * @throws Exception si MockMvc falla al ejecutar la petición
     */
    @Test
    void registrarPronostico_conDatosValidos_retorna200() throws Exception {
        PronosticoRequestDTO request = new PronosticoRequestDTO();
        request.setResultadoPronosticado("LOCAL");
        request.setGolesLocalPronosticados(2);
        request.setGolesVisitantePronosticados(1);
        request.setUsuarioId(1L);
        request.setApuestaId(1L);
        request.setPartidoId(1L);

        when(apuestaService.registrarPronostico(any())).thenReturn(new PronosticoDTO());

        mockMvc.perform(post(API_APUESTAS_PRONOSTICO)
                        .header(AUTH_HEADER, BEARER + tokenUsuario())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    /**
     * Verifica que un valor de resultado no permitido (distinto de LOCAL, VISITANTE o EMPATE)
     * es rechazado por Bean Validation con HTTP 400
     *
     * @throws Exception si MockMvc falla al ejecutar la petición
     */
    @Test
    void registrarPronostico_resultadoInvalido_retorna400() throws Exception {
        PronosticoRequestDTO request = new PronosticoRequestDTO();
        request.setResultadoPronosticado("NINGUNO");
        request.setGolesLocalPronosticados(2);
        request.setGolesVisitantePronosticados(1);

        mockMvc.perform(post(API_APUESTAS_PRONOSTICO)
                        .header(AUTH_HEADER, BEARER + tokenUsuario())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    /**
     * Verifica que un número de goles negativo es rechazado
     * por Bean Validation con HTTP 400
     *
     * @throws Exception si MockMvc falla al ejecutar la petición
     */
    @Test
    void registrarPronostico_golesNegativos_retorna400() throws Exception {
        PronosticoRequestDTO request = new PronosticoRequestDTO();
        request.setResultadoPronosticado("LOCAL");
        request.setGolesLocalPronosticados(-1);
        request.setGolesVisitantePronosticados(0);

        mockMvc.perform(post(API_APUESTAS_PRONOSTICO)
                        .header(AUTH_HEADER, BEARER + tokenUsuario())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    /**
     * Verifica que registrar un pronóstico sin token JWT retorna HTTP 401
     *
     * @throws Exception si MockMvc falla al ejecutar la petición
     */
    @Test
    void registrarPronostico_sinToken_retorna401() throws Exception {
        mockMvc.perform(post(API_APUESTAS_PRONOSTICO)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isUnauthorized());
    }

    /**
     * Verifica que un usuario autenticado puede consultar el ranking
     * de una apuesta por su ID y recibe HTTP 200
     *
     * @throws Exception si MockMvc falla al ejecutar la petición
     */
    @Test
    void obtenerRanking_conToken_retorna200() throws Exception {
        when(apuestaService.obtenerRanking(1L)).thenReturn(List.of(new ParticipacionDTO()));

        mockMvc.perform(get("/api/apuestas/ranking/1")
                        .header(AUTH_HEADER, BEARER + tokenUsuario()))
                .andExpect(status().isOk());
    }

    /**
     * Verifica que consultar el ranking sin token JWT retorna HTTP 401
     *
     * @throws Exception si MockMvc falla al ejecutar la petición
     */
    @Test
    void obtenerRanking_sinToken_retorna401() throws Exception {
        mockMvc.perform(get("/api/apuestas/ranking/1"))
                .andExpect(status().isUnauthorized());
    }

    /**
     * Verifica que un usuario con rol ADMIN puede cerrar una apuesta
     * y recibe HTTP 200
     *
     * @throws Exception si MockMvc falla al ejecutar la petición
     */
    @Test
    void cerrar_conRolAdmin_retorna200() throws Exception {
        when(apuestaService.cerrarApuesta(1L)).thenReturn(new ApuestaDTO());

        mockMvc.perform(post("/api/apuestas/cerrar/1")
                        .header(AUTH_HEADER, BEARER + tokenAdmin()))
                .andExpect(status().isOk());
    }

    /**
     * Verifica que un usuario con rol USER no tiene permiso para cerrar
     * una apuesta y recibe HTTP 403 Forbidden
     *
     * @throws Exception si MockMvc falla al ejecutar la petición
     */
    @Test
    void cerrar_conRolUser_retorna403() throws Exception {
        mockMvc.perform(post("/api/apuestas/cerrar/1")
                        .header(AUTH_HEADER, BEARER + tokenUsuario()))
                .andExpect(status().isForbidden());
    }

    /**
     * Verifica que intentar cerrar una apuesta sin token JWT retorna HTTP 401
     *
     * @throws Exception si MockMvc falla al ejecutar la petición
     */
    @Test
    void cerrar_sinToken_retorna401() throws Exception {
        mockMvc.perform(post("/api/apuestas/cerrar/1"))
                .andExpect(status().isUnauthorized());
    }

    /**
     * Verifica que un usuario con rol ADMIN puede calcular los puntos
     * finales de una apuesta y recibe HTTP 200
     *
     * @throws Exception si MockMvc falla al ejecutar la petición
     */
    @Test
    void calcularPuntos_conRolAdmin_retorna200() throws Exception {
        when(apuestaService.calcularPuntos(1L)).thenReturn(List.of());

        mockMvc.perform(get("/api/apuestas/puntos/1")
                        .header(AUTH_HEADER, BEARER + tokenAdmin()))
                .andExpect(status().isOk());
    }

    /**
     * Verifica que un usuario con rol USER no puede calcular puntos finales
     * y recibe HTTP 403 Forbidden
     *
     * @throws Exception si MockMvc falla al ejecutar la petición
     */
    @Test
    void calcularPuntos_conRolUser_retorna403() throws Exception {
        mockMvc.perform(get("/api/apuestas/puntos/1")
                        .header(AUTH_HEADER, BEARER + tokenUsuario()))
                .andExpect(status().isForbidden());
    }

    /**
     * Verifica que un usuario autenticado puede listar sus apuestas
     * por ID de usuario y recibe HTTP 200
     *
     * @throws Exception si MockMvc falla al ejecutar la petición
     */
    @Test
    void listarPorUsuario_conToken_retorna200() throws Exception {
        when(apuestaService.listarApuestasPorUsuario(1L)).thenReturn(List.of());

        mockMvc.perform(get("/api/apuestas/usuario/1")
                        .header(AUTH_HEADER, BEARER + tokenUsuario()))
                .andExpect(status().isOk());
    }

    /**
     * Verifica que listar apuestas por usuario sin token JWT retorna HTTP 401
     *
     * @throws Exception si MockMvc falla al ejecutar la petición
     */
    @Test
    void listarPorUsuario_sinToken_retorna401() throws Exception {
        mockMvc.perform(get("/api/apuestas/usuario/1"))
                .andExpect(status().isUnauthorized());
    }

    /**
     * Verifica que un usuario autenticado puede obtener el detalle
     * de una apuesta por su ID y recibe HTTP 200
     *
     * @throws Exception si MockMvc falla al ejecutar la petición
     */
    @Test
    void obtenerApuesta_conToken_retorna200() throws Exception {
        when(apuestaService.obtenerApuesta(1L)).thenReturn(new ApuestaDTO());

        mockMvc.perform(get(API_APUESTAS_ID)
                        .header(AUTH_HEADER, BEARER + tokenUsuario()))
                .andExpect(status().isOk());
    }

    /**
     * Verifica que obtener una apuesta sin token JWT retorna HTTP 401
     *
     * @throws Exception si MockMvc falla al ejecutar la petición
     */
    @Test
    void obtenerApuesta_sinToken_retorna401() throws Exception {
        mockMvc.perform(get(API_APUESTAS_ID))
                .andExpect(status().isUnauthorized());
    }

    /**
     * Verifica que un usuario autenticado puede listar los participantes
     * de una apuesta y recibe HTTP 200
     *
     * @throws Exception si MockMvc falla al ejecutar la petición
     */
    @Test
    void listarParticipantes_conToken_retorna200() throws Exception {
        when(apuestaService.listarParticipantes(1L)).thenReturn(List.of());

        mockMvc.perform(get("/api/apuestas/participantes/1")
                        .header(AUTH_HEADER, BEARER + tokenUsuario()))
                .andExpect(status().isOk());
    }

    /**
     * Verifica que listar participantes sin token JWT retorna HTTP 401
     *
     * @throws Exception si MockMvc falla al ejecutar la petición
     */
    @Test
    void listarParticipantes_sinToken_retorna401() throws Exception {
        mockMvc.perform(get("/api/apuestas/participantes/1"))
                .andExpect(status().isUnauthorized());
    }

    /**
     * Verifica que un usuario autenticado puede verificar el estado
     * de un pronóstico por su ID y recibe HTTP 200
     *
     * @throws Exception si MockMvc falla al ejecutar la petición
     */
    @Test
    void verificarPronostico_conToken_retorna200() throws Exception {
        when(apuestaService.verificarPronostico(1L)).thenReturn(new PronosticoDTO());

        mockMvc.perform(get("/api/apuestas/verificar/1")
                        .header(AUTH_HEADER, BEARER + tokenUsuario()))
                .andExpect(status().isOk());
    }

    /**
     * Verifica que verificar un pronóstico sin token JWT retorna HTTP 401
     *
     * @throws Exception si MockMvc falla al ejecutar la petición
     */
    @Test
    void verificarPronostico_sinToken_retorna401() throws Exception {
        mockMvc.perform(get("/api/apuestas/verificar/1"))
                .andExpect(status().isUnauthorized());
    }

    /**
     * Verifica que un usuario autenticado puede consultar sus propios pronósticos
     * dentro de una apuesta específica y recibe HTTP 200
     *
     * @throws Exception si MockMvc falla al ejecutar la petición
     */
    @Test
    void misPronosticos_conToken_retorna200() throws Exception {
        when(apuestaService.misPronosticos(1L, 1L)).thenReturn(List.of());

        mockMvc.perform(get("/api/apuestas/mis-pronosticos/1/1")
                        .header(AUTH_HEADER, BEARER + tokenUsuario()))
                .andExpect(status().isOk());
    }

    /**
     * Verifica que consultar mis pronósticos sin token JWT retorna HTTP 401
     *
     * @throws Exception si MockMvc falla al ejecutar la petición
     */
    @Test
    void misPronosticos_sinToken_retorna401() throws Exception {
        mockMvc.perform(get("/api/apuestas/mis-pronosticos/1/1"))
                .andExpect(status().isUnauthorized());
    }

    /**
     * Verifica que un usuario autenticado puede editar un pronóstico existente
     * con datos válidos y recibe HTTP 200
     *
     * @throws Exception si MockMvc falla al ejecutar la petición
     */
    @Test
    void editarPronostico_conDatosValidos_retorna200() throws Exception {
        PronosticoRequestDTO request = new PronosticoRequestDTO();
        request.setResultadoPronosticado("EMPATE");
        request.setGolesLocalPronosticados(1);
        request.setGolesVisitantePronosticados(1);

        when(apuestaService.editarPronostico(eq(1L), any(), eq(USER_EMAIL)))
                .thenReturn(new PronosticoDTO());

        mockMvc.perform(put(API_APUESTAS_PRONOSTICO + "/1")
                        .header(AUTH_HEADER, BEARER + tokenUsuario())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    /**
     * Verifica que editar un pronóstico con un resultado no permitido
     * es rechazado por Bean Validation con HTTP 400
     *
     * @throws Exception si MockMvc falla al ejecutar la petición
     */
    @Test
    void editarPronostico_resultadoInvalido_retorna400() throws Exception {
        PronosticoRequestDTO request = new PronosticoRequestDTO();
        request.setResultadoPronosticado("INVALIDO");
        request.setGolesLocalPronosticados(1);
        request.setGolesVisitantePronosticados(1);

        mockMvc.perform(put(API_APUESTAS_PRONOSTICO + "/1")
                        .header(AUTH_HEADER, BEARER + tokenUsuario())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    /**
     * Verifica que editar un pronóstico sin token JWT retorna HTTP 401
     *
     * @throws Exception si MockMvc falla al ejecutar la petición
     */
    @Test
    void editarPronostico_sinToken_retorna401() throws Exception {
        mockMvc.perform(put(API_APUESTAS_PRONOSTICO + "/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isUnauthorized());
    }

    /**
     * Verifica que un usuario autenticado puede eliminar un pronóstico propio
     * y recibe HTTP 204 No Content
     *
     * @throws Exception si MockMvc falla al ejecutar la petición
     */
    @Test
    void eliminarPronostico_conToken_retorna204() throws Exception {
        doNothing().when(apuestaService).eliminarPronostico(1L, USER_EMAIL);

        mockMvc.perform(delete(API_APUESTAS_PRONOSTICO + "/1")
                        .header(AUTH_HEADER, BEARER + tokenUsuario()))
                .andExpect(status().isNoContent());
    }

    /**
     * Verifica que eliminar un pronóstico sin token JWT retorna HTTP 401
     *
     * @throws Exception si MockMvc falla al ejecutar la petición
     */
    @Test
    void eliminarPronostico_sinToken_retorna401() throws Exception {
        mockMvc.perform(delete(API_APUESTAS_PRONOSTICO + "/1"))
                .andExpect(status().isUnauthorized());
    }

    /**
     * Verifica que un usuario autenticado puede calcular los puntos parciales
     * de una apuesta en curso y recibe HTTP 200
     *
     * @throws Exception si MockMvc falla al ejecutar la petición
     */
    @Test
    void calcularPuntosParciales_conToken_retorna200() throws Exception {
        when(apuestaService.calcularPuntosParciales(1L)).thenReturn(List.of());

        mockMvc.perform(get("/api/apuestas/puntos-parciales/1")
                        .header(AUTH_HEADER, BEARER + tokenUsuario()))
                .andExpect(status().isOk());
    }

    /**
     * Verifica que calcular puntos parciales sin token JWT retorna HTTP 401
     *
     * @throws Exception si MockMvc falla al ejecutar la petición
     */
    @Test
    void calcularPuntosParciales_sinToken_retorna401() throws Exception {
        mockMvc.perform(get("/api/apuestas/puntos-parciales/1"))
                .andExpect(status().isUnauthorized());
    }

    /**
     * Verifica que un usuario con rol ADMIN puede listar todas las apuestas
     * del sistema y recibe HTTP 200
     *
     * @throws Exception si MockMvc falla al ejecutar la petición
     */
    @Test
    void listarTodas_conRolAdmin_retorna200() throws Exception {
        when(apuestaService.listarTodas()).thenReturn(List.of());

        mockMvc.perform(get("/api/apuestas/todas")
                        .header(AUTH_HEADER, BEARER + tokenAdmin()))
                .andExpect(status().isOk());
    }

    /**
     * Verifica que un usuario con rol USER no puede listar todas las apuestas
     * y recibe HTTP 403 Forbidden
     *
     * @throws Exception si MockMvc falla al ejecutar la petición
     */
    @Test
    void listarTodas_conRolUser_retorna403() throws Exception {
        mockMvc.perform(get("/api/apuestas/todas")
                        .header(AUTH_HEADER, BEARER + tokenUsuario()))
                .andExpect(status().isForbidden());
    }

    /**
     * Verifica que listar todas las apuestas sin token JWT retorna HTTP 401
     *
     * @throws Exception si MockMvc falla al ejecutar la petición
     */
    @Test
    void listarTodas_sinToken_retorna401() throws Exception {
        mockMvc.perform(get("/api/apuestas/todas"))
                .andExpect(status().isUnauthorized());
    }

    /**
     * Verifica que un usuario con rol ADMIN puede eliminar una apuesta
     * por su ID y recibe HTTP 204 No Content
     *
     * @throws Exception si MockMvc falla al ejecutar la petición
     */
    @Test
    void eliminarApuesta_conRolAdmin_retorna204() throws Exception {
        doNothing().when(apuestaService).eliminarApuesta(1L);

        mockMvc.perform(delete(API_APUESTAS_ID)
                        .header(AUTH_HEADER, BEARER + tokenAdmin()))
                .andExpect(status().isNoContent());
    }

    /**
     * Verifica que un usuario con rol USER no puede eliminar una apuesta
     * y recibe HTTP 403 Forbidden
     *
     * @throws Exception si MockMvc falla al ejecutar la petición
     */
    @Test
    void eliminarApuesta_conRolUser_retorna403() throws Exception {
        mockMvc.perform(delete(API_APUESTAS_ID)
                        .header(AUTH_HEADER, BEARER + tokenUsuario()))
                .andExpect(status().isForbidden());
    }

    /**
     * Verifica que eliminar una apuesta sin token JWT retorna HTTP 401
     *
     * @throws Exception si MockMvc falla al ejecutar la petición
     */
    @Test
    void eliminarApuesta_sinToken_retorna401() throws Exception {
        mockMvc.perform(delete(API_APUESTAS_ID))
                .andExpect(status().isUnauthorized());
    }

    /**
     * Verifica que un usuario autenticado puede obtener el listado completo
     * de sus apuestas incluyendo los participantes de cada una, recibiendo HTTP 200
     *
     * @throws Exception si MockMvc falla al ejecutar la petición
     */
    @Test
    void listarPorUsuarioCompleto_conToken_retorna200() throws Exception {
        when(apuestaService.listarApuestasPorUsuarioCompleto(1L)).thenReturn(List.of(new ApuestaConParticipantesDTO()));

        mockMvc.perform(get("/api/apuestas/usuario/1/completo")
                        .header(AUTH_HEADER, BEARER + tokenUsuario()))
                .andExpect(status().isOk());
    }

    /**
     * Verifica que listar apuestas completas por usuario sin token JWT retorna HTTP 401
     *
     * @throws Exception si MockMvc falla al ejecutar la petición
     */
    @Test
    void listarPorUsuarioCompleto_sinToken_retorna401() throws Exception {
        mockMvc.perform(get("/api/apuestas/usuario/1/completo"))
                .andExpect(status().isUnauthorized());
    }
}