package co.edu.unbosque.mundial_2026.integration;

import co.edu.unbosque.mundial_2026.dto.request.UsuarioActualizarRequestDTO;
import co.edu.unbosque.mundial_2026.dto.request.UsuarioRequestDTO;
import co.edu.unbosque.mundial_2026.dto.response.PreferenciaDTO;
import co.edu.unbosque.mundial_2026.dto.response.UsuarioResponseDTO;
import co.edu.unbosque.mundial_2026.security.TokenBlacklist;
import co.edu.unbosque.mundial_2026.service.UsuarioService;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class UsuarioIT extends BaseIntegrationTest {

    private static final String URL_LISTAR = "/api/usuarios/listar";
    private static final String URL_USUARIO_ID = "/api/usuarios/1";
    private static final String URL_PERFIL = "/api/usuarios/perfil";
    private static final String URL_REGISTRAR = "/api/usuarios/registrar";
    private static final String URL_LOGOUT = "/api/auth/logout";
    private static final String URL_SELECCIONES_FAV = "/api/usuarios/seleccionesFavoritas";
    private static final String URL_SELECCION_ID = "/api/usuarios/seleccionesFavoritas/1";
    private static final String URL_ESTADIOS_FAV = "/api/usuarios/estadiosFav";
    private static final String URL_ESTADIO_ID = "/api/usuarios/estadiosFav/1";
    private static final String URL_CIUDADES_FAV = "/api/usuarios/ciudadesFav";
    private static final String URL_CIUDAD_ID = "/api/usuarios/ciudadesFav/1";
    private static final String URL_ESTADIOS_CATALOGO = "/api/estadios";
    private static final String URL_CIUDADES_CATALOGO = "/api/ciudades";
    private static final String URL_FCM_TOKEN = "/api/usuarios/fcm-token";
    private static final String URL_NOMBRE_USUARIO = "/api/usuarios/1/nombre";
    private static final String URL_ADMIN_REGISTRAR = "/api/usuarios/admin/registrar";
    private static final String CORREO_VALIDO = "nuevo@test.com";
    private static final String CONTRASENA_VALIDA = "Contrasena1!";

 private static final String AUTH_HEADER = "Authorization";
private static final String BEARER_PREFIX = "Bearer ";
private static final String IDS_JSON = "[1,2]";
    @MockitoBean
    private UsuarioService service;

    @MockitoBean
    private TokenBlacklist tokenBlacklist;

    private UsuarioRequestDTO registroValido() {
        UsuarioRequestDTO dto = new UsuarioRequestDTO();
        dto.setNombre("Juan");
        dto.setApellido("García");
        dto.setCorreoUsuario(CORREO_VALIDO);
        dto.setContrasena(CONTRASENA_VALIDA);
        return dto;
    }

    @Test
    void listarTodos_conRolAdmin_retorna200() throws Exception {
        when(service.listarTodos()).thenReturn(List.of(new UsuarioResponseDTO()));

        mockMvc.perform(get(URL_LISTAR)
                        .header(AUTH_HEADER, BEARER_PREFIX + tokenAdmin()))
                .andExpect(status().isOk());
    }

    @Test
    void listarTodos_conRolUser_retorna403() throws Exception {
        mockMvc.perform(get(URL_LISTAR)
                        .header(AUTH_HEADER, BEARER_PREFIX + tokenUsuario()))
                .andExpect(status().isForbidden());
    }

    @Test
    void listarTodos_sinToken_retorna401() throws Exception {
        mockMvc.perform(get(URL_LISTAR))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void obtenerUsuario_conRolAdmin_retorna200() throws Exception {
        when(service.obtenerUsuario(1L)).thenReturn(new UsuarioResponseDTO());

        mockMvc.perform(get(URL_USUARIO_ID)
                        .header(AUTH_HEADER, BEARER_PREFIX + tokenAdmin()))
                .andExpect(status().isOk());
    }

    @Test
    void obtenerUsuario_conRolUser_retorna403() throws Exception {
        mockMvc.perform(get(URL_USUARIO_ID)
                        .header(AUTH_HEADER, BEARER_PREFIX + tokenUsuario()))
                .andExpect(status().isForbidden());
    }

    @Test
    void obtenerUsuario_sinToken_retorna401() throws Exception {
        mockMvc.perform(get(URL_USUARIO_ID))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void obtenerPerfil_conToken_retorna200() throws Exception {
        when(service.obtenerPorCorreo(USER_EMAIL)).thenReturn(new UsuarioResponseDTO());

        mockMvc.perform(get(URL_PERFIL)
                        .header(AUTH_HEADER, BEARER_PREFIX + tokenUsuario()))
                .andExpect(status().isOk());
    }

    @Test
    void obtenerPerfil_sinToken_retorna401() throws Exception {
        mockMvc.perform(get(URL_PERFIL))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void registrar_conDatosValidos_retorna201() throws Exception {
        when(service.registrarUsuario(any())).thenReturn(new UsuarioResponseDTO());

        mockMvc.perform(post(URL_REGISTRAR)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registroValido())))
                .andExpect(status().isCreated());
    }

    @Test
    void registrar_nombreVacio_retorna400() throws Exception {
        UsuarioRequestDTO dto = registroValido();
        dto.setNombre("");

        mockMvc.perform(post(URL_REGISTRAR)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void registrar_apellidoVacio_retorna400() throws Exception {
        UsuarioRequestDTO dto = registroValido();
        dto.setApellido("");

        mockMvc.perform(post(URL_REGISTRAR)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void registrar_correoInvalido_retorna400() throws Exception {
        UsuarioRequestDTO dto = registroValido();
        dto.setCorreoUsuario("no-es-correo");

        mockMvc.perform(post(URL_REGISTRAR)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void registrar_contrasenaSinMayuscula_retorna400() throws Exception {
        UsuarioRequestDTO dto = registroValido();
        dto.setContrasena("contrasena1!");

        mockMvc.perform(post(URL_REGISTRAR)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void registrar_contrasenaSinNumero_retorna400() throws Exception {
        UsuarioRequestDTO dto = registroValido();
        dto.setContrasena("Contrasena!");

        mockMvc.perform(post(URL_REGISTRAR)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void registrar_contrasenaSinSimbolo_retorna400() throws Exception {
        UsuarioRequestDTO dto = registroValido();
        dto.setContrasena("Contrasena1");

        mockMvc.perform(post(URL_REGISTRAR)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void eliminarUsuario_conRolAdmin_retorna204() throws Exception {
        doNothing().when(service).eliminarUsuario(1L);

        mockMvc.perform(delete(URL_USUARIO_ID)
                        .header(AUTH_HEADER, BEARER_PREFIX + tokenAdmin()))
                .andExpect(status().isNoContent());
    }

    @Test
    void eliminarUsuario_conRolUser_retorna403() throws Exception {
        mockMvc.perform(delete(URL_USUARIO_ID)
                        .header(AUTH_HEADER, BEARER_PREFIX + tokenUsuario()))
                .andExpect(status().isForbidden());
    }

    @Test
    void eliminarUsuario_sinToken_retorna401() throws Exception {
        mockMvc.perform(delete(URL_USUARIO_ID))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void actualizarPerfil_sinCambioCorreo_retorna200() throws Exception {
        Map<String, Object> resultado = new HashMap<>();
        resultado.put("correocambio", false);
        resultado.put("usuario", new UsuarioResponseDTO());
        when(service.actualizarPerfil(eq(USER_EMAIL), any())).thenReturn(resultado);

        UsuarioActualizarRequestDTO dto = new UsuarioActualizarRequestDTO();
        dto.setNombre("NuevoNombre");

        mockMvc.perform(put(URL_PERFIL)
                        .header(AUTH_HEADER, BEARER_PREFIX + tokenUsuario())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    void actualizarPerfil_conCambioCorreo_retorna200ConMensaje() throws Exception {
        Map<String, Object> resultado = new HashMap<>();
        resultado.put("correocambio", true);
        resultado.put("usuario", new UsuarioResponseDTO());
        when(service.actualizarPerfil(eq(USER_EMAIL), any())).thenReturn(resultado);

        UsuarioActualizarRequestDTO dto = new UsuarioActualizarRequestDTO();
        dto.setCorreoNuevo("otro@test.com");

        mockMvc.perform(put(URL_PERFIL)
                        .header(AUTH_HEADER, BEARER_PREFIX + tokenUsuario())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje").value("Correo actualizado, inicia sesión nuevamente"));
    }

    @Test
    void actualizarPerfil_sinToken_retorna401() throws Exception {
        mockMvc.perform(put(URL_PERFIL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void logout_conToken_retorna200() throws Exception {
        mockMvc.perform(post(URL_LOGOUT)
                        .header(AUTH_HEADER, BEARER_PREFIX + tokenUsuario()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje").value("Sesión cerrada correctamente"));
    }

    @Test
    void logout_sinToken_retorna401() throws Exception {
        mockMvc.perform(post(URL_LOGOUT))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void obtenerSelecciones_conToken_retorna200() throws Exception {
        when(service.seleccionesUsuario(USER_EMAIL)).thenReturn(List.of(new PreferenciaDTO(1L, "Test")));

        mockMvc.perform(get(URL_SELECCIONES_FAV)
                        .header(AUTH_HEADER, BEARER_PREFIX + tokenUsuario()))
                .andExpect(status().isOk());
    }

    @Test
    void obtenerSelecciones_sinToken_retorna401() throws Exception {
        mockMvc.perform(get(URL_SELECCIONES_FAV))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void eliminarSeleccion_conToken_retorna204() throws Exception {
        doNothing().when(service).eliminarSeleccion(USER_EMAIL, 1L);

        mockMvc.perform(delete(URL_SELECCION_ID)
                        .header(AUTH_HEADER, BEARER_PREFIX + tokenUsuario()))
                .andExpect(status().isNoContent());
    }

    @Test
    void eliminarSeleccion_sinToken_retorna401() throws Exception {
        mockMvc.perform(delete(URL_SELECCION_ID))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void obtenerEstadios_conToken_retorna200() throws Exception {
        when(service.estadiosUsuario(USER_EMAIL)).thenReturn(List.of());

        mockMvc.perform(get(URL_ESTADIOS_FAV)
                        .header(AUTH_HEADER, BEARER_PREFIX + tokenUsuario()))
                .andExpect(status().isOk());
    }

    @Test
    void obtenerEstadios_sinToken_retorna401() throws Exception {
        mockMvc.perform(get(URL_ESTADIOS_FAV))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void eliminarEstadio_conToken_retorna204() throws Exception {
        doNothing().when(service).eliminarEstadio(USER_EMAIL, 1L);

        mockMvc.perform(delete(URL_ESTADIO_ID)
                        .header(AUTH_HEADER, BEARER_PREFIX + tokenUsuario()))
                .andExpect(status().isNoContent());
    }

    @Test
    void eliminarEstadio_sinToken_retorna401() throws Exception {
        mockMvc.perform(delete(URL_ESTADIO_ID))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void obtenerCiudades_conToken_retorna200() throws Exception {
        when(service.ciudadesUsuario(USER_EMAIL)).thenReturn(List.of());

        mockMvc.perform(get(URL_CIUDADES_FAV)
                        .header(AUTH_HEADER, BEARER_PREFIX + tokenUsuario()))
                .andExpect(status().isOk());
    }

    @Test
    void obtenerCiudades_sinToken_retorna401() throws Exception {
        mockMvc.perform(get(URL_CIUDADES_FAV))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void eliminarCiudad_conToken_retorna204() throws Exception {
        doNothing().when(service).eliminarCiudad(USER_EMAIL, 1L);

        mockMvc.perform(delete(URL_CIUDAD_ID)
                        .header(AUTH_HEADER, BEARER_PREFIX + tokenUsuario()))
                .andExpect(status().isNoContent());
    }

    @Test
    void eliminarCiudad_sinToken_retorna401() throws Exception {
        mockMvc.perform(delete(URL_CIUDAD_ID))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void listarEstadiosCatalogo_conToken_retorna200() throws Exception {
        when(service.listarEstadios()).thenReturn(List.of(new PreferenciaDTO(1L, "Test")));

        mockMvc.perform(get(URL_ESTADIOS_CATALOGO)
                        .header(AUTH_HEADER, BEARER_PREFIX + tokenUsuario()))
                .andExpect(status().isOk());
    }

    @Test
    void listarEstadiosCatalogo_sinToken_retorna401() throws Exception {
        mockMvc.perform(get(URL_ESTADIOS_CATALOGO))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void listarCiudadesCatalogo_conToken_retorna200() throws Exception {
        when(service.listarCiudades()).thenReturn(List.of(new PreferenciaDTO(1L, "Test")));

        mockMvc.perform(get(URL_CIUDADES_CATALOGO)
                        .header(AUTH_HEADER, BEARER_PREFIX + tokenUsuario()))
                .andExpect(status().isOk());
    }

    @Test
    void listarCiudadesCatalogo_sinToken_retorna401() throws Exception {
        mockMvc.perform(get(URL_CIUDADES_CATALOGO))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void actualizarFcmToken_conToken_retorna204() throws Exception {
        doNothing().when(service).actualizarFcmToken(eq(USER_EMAIL), any());

        mockMvc.perform(put(URL_FCM_TOKEN)
                        .header(AUTH_HEADER, BEARER_PREFIX + tokenUsuario())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"fcmToken\":\"token-dispositivo-123\"}"))
                .andExpect(status().isNoContent());
    }

    @Test
    void actualizarFcmToken_sinToken_retorna401() throws Exception {
        mockMvc.perform(put(URL_FCM_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"fcmToken\":\"token-dispositivo-123\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void agregarSelecciones_conToken_retorna204() throws Exception {
        doNothing().when(service).agregarSeleccion(eq(USER_EMAIL), any());

        mockMvc.perform(put(URL_SELECCIONES_FAV)
                        .header(AUTH_HEADER, BEARER_PREFIX + tokenUsuario())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[1,2,3]"))
                .andExpect(status().isNoContent());
    }

    @Test
    void agregarSelecciones_sinToken_retorna401() throws Exception {
        mockMvc.perform(put(URL_SELECCIONES_FAV)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[1,2,3]"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void agregarEstadios_conToken_retorna204() throws Exception {
        doNothing().when(service).agregarEstadio(eq(USER_EMAIL), any());

        mockMvc.perform(put(URL_ESTADIOS_FAV)
                        .header(AUTH_HEADER, BEARER_PREFIX + tokenUsuario())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(IDS_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void agregarEstadios_sinToken_retorna401() throws Exception {
        mockMvc.perform(put(URL_ESTADIOS_FAV)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(IDS_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void agregarCiudades_conToken_retorna204() throws Exception {
        doNothing().when(service).agregarCiudad(eq(USER_EMAIL), any());

        mockMvc.perform(put(URL_CIUDADES_FAV)
                        .header(AUTH_HEADER, BEARER_PREFIX + tokenUsuario())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(IDS_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void agregarCiudades_sinToken_retorna401() throws Exception {
        mockMvc.perform(put(URL_CIUDADES_FAV)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(IDS_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void obtenerNombreUsuario_conToken_retorna200() throws Exception {
        UsuarioResponseDTO u = new UsuarioResponseDTO();
        u.setNombre("Juan");
        u.setApellido("García");
        when(service.obtenerUsuario(1L)).thenReturn(u);

        mockMvc.perform(get(URL_NOMBRE_USUARIO)
                        .header(AUTH_HEADER, BEARER_PREFIX + tokenUsuario()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Juan"))
                .andExpect(jsonPath("$.apellido").value("García"));
    }

    @Test
    void obtenerNombreUsuario_sinToken_retorna401() throws Exception {
        mockMvc.perform(get(URL_NOMBRE_USUARIO))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void registrarPorAdmin_conRolAdmin_retorna201() throws Exception {
        when(service.registrarUsuarioComoAdmin(any())).thenReturn(new UsuarioResponseDTO());

        mockMvc.perform(post(URL_ADMIN_REGISTRAR)
                        .header(AUTH_HEADER, BEARER_PREFIX + tokenAdmin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registroValido())))
                .andExpect(status().isCreated());
    }

    @Test
    void registrarPorAdmin_conRolUser_retorna403() throws Exception {
        mockMvc.perform(post(URL_ADMIN_REGISTRAR)
                        .header(AUTH_HEADER, BEARER_PREFIX + tokenUsuario())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registroValido())))
                .andExpect(status().isForbidden());
    }

    @Test
    void registrarPorAdmin_sinToken_retorna401() throws Exception {
        mockMvc.perform(post(URL_ADMIN_REGISTRAR)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registroValido())))
                .andExpect(status().isUnauthorized());
    }
}