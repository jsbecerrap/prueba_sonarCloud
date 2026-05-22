package co.edu.unbosque.mundial_2026.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import co.edu.unbosque.mundial_2026.dto.NotificacionDTO;
import co.edu.unbosque.mundial_2026.dto.request.NotificacionMasivaRequestDTO;
import co.edu.unbosque.mundial_2026.dto.request.NotificacionRequestDTO;
import co.edu.unbosque.mundial_2026.dto.response.UsuarioResponseDTO;
import co.edu.unbosque.mundial_2026.service.NotificacionService;
import co.edu.unbosque.mundial_2026.service.UsuarioService;

@WebMvcTest(NotificacionController.class)
@ActiveProfiles("test")
class NotificacionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NotificacionService notificacionService;

    @MockBean
    private UsuarioService usuarioService;

    private final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    private UsuarioResponseDTO usuarioMock(Long id) {
        UsuarioResponseDTO u = new UsuarioResponseDTO();
        u.setId(id);
        return u;
    }

    private NotificacionDTO notificacionLeida() {
        return new NotificacionDTO(1L, "INFO", "Titulo", "Mensaje",
                "PUSH", "ENVIADO", true, LocalDateTime.now(), 1L);
    }

    private NotificacionDTO notificacionNoLeida() {
        return new NotificacionDTO(2L, "INFO", "Titulo2", "Mensaje2",
                "PUSH", "ENVIADO", false, LocalDateTime.now(), 1L);
    }

    @Test
    void listarMisNotificaciones_retorna200() throws Exception {
        when(usuarioService.obtenerPorCorreo("user@test.com")).thenReturn(usuarioMock(1L));
        when(notificacionService.listarPorUsuarioPaginado(eq(1L), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(notificacionLeida())));

        mockMvc.perform(get("/api/notificaciones")
                .with(jwt().jwt(j -> j.subject("user@test.com"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    void listarMisNotificaciones_paginacionPersonalizada_retorna200() throws Exception {
        when(usuarioService.obtenerPorCorreo("user@test.com")).thenReturn(usuarioMock(1L));
        when(notificacionService.listarPorUsuarioPaginado(eq(1L), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));

        mockMvc.perform(get("/api/notificaciones?page=1&size=5")
                .with(jwt().jwt(j -> j.subject("user@test.com"))))
                .andExpect(status().isOk());
    }

    @Test
    void listarMisNotificaciones_sinAutenticacion_retorna401() throws Exception {
        mockMvc.perform(get("/api/notificaciones"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void marcarLeida_retorna204() throws Exception {
        doNothing().when(notificacionService).marcarLeida(1L);

        mockMvc.perform(put("/api/notificaciones/1/leida")
                .with(jwt()))
                .andExpect(status().isNoContent());
    }

    @Test
    void marcarLeida_sinAutenticacion_retorna401() throws Exception {
        mockMvc.perform(put("/api/notificaciones/1/leida"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void marcarLeida_serviceLanzaExcepcion_retorna500() throws Exception {
        doThrow(new RuntimeException("No encontrada"))
                .when(notificacionService).marcarLeida(eq(99L));

        mockMvc.perform(put("/api/notificaciones/99/leida")
                .with(jwt().jwt(j -> j.subject("user@test.com"))))
                .andExpect(status().is5xxServerError());
    }

    @Test
    void marcarTodasLeidas_retorna204() throws Exception {
        when(usuarioService.obtenerPorCorreo("user@test.com")).thenReturn(usuarioMock(1L));
        doNothing().when(notificacionService).marcarTodasLeidas(1L);

        mockMvc.perform(put("/api/notificaciones/leidas")
                .with(jwt().jwt(j -> j.subject("user@test.com"))))
                .andExpect(status().isNoContent());
    }

    @Test
    void marcarTodasLeidas_sinAutenticacion_retorna401() throws Exception {
        mockMvc.perform(put("/api/notificaciones/leidas"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void enviarIndividual_conRolAdmin_retorna204() throws Exception {
        doNothing().when(notificacionService).enviarNotificacion(any());

        NotificacionRequestDTO dto = new NotificacionRequestDTO();
        dto.setTipo("INFO");
        dto.setTitulo("Test");
        dto.setMensaje("Mensaje test");
        dto.setUsuarioId(1L);

        mockMvc.perform(post("/api/notificaciones/enviar")
                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isNoContent());
    }

    @Test
    void enviarIndividual_sinRolAdmin_retorna403() throws Exception {
        NotificacionRequestDTO dto = new NotificacionRequestDTO();

        mockMvc.perform(post("/api/notificaciones/enviar")
                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_USER")))
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isForbidden());
    }

    @Test
    void enviarIndividual_sinAutenticacion_retorna401() throws Exception {
        mockMvc.perform(post("/api/notificaciones/enviar")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void enviarMasiva_conRolAdmin_retorna204() throws Exception {
        doNothing().when(notificacionService).enviarMasiva(any());

        NotificacionMasivaRequestDTO dto = new NotificacionMasivaRequestDTO();
        dto.setTipo("INFO");
        dto.setTitulo("Masiva");
        dto.setMensaje("Mensaje masivo");
        dto.setUsuarioIds(List.of(1L, 2L, 3L));

        mockMvc.perform(post("/api/notificaciones/masiva")
                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isNoContent());
    }

    @Test
    void enviarMasiva_sinRolAdmin_retorna403() throws Exception {
        mockMvc.perform(post("/api/notificaciones/masiva")
                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_USER")))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void enviarMasiva_sinAutenticacion_retorna401() throws Exception {
        mockMvc.perform(post("/api/notificaciones/masiva")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void notificarPorPartido_conRolAdmin_retorna204() throws Exception {
        doNothing().when(notificacionService)
                .notificarPorPartido(anyLong(), anyString(), anyString(), anyString());

        NotificacionRequestDTO dto = new NotificacionRequestDTO();
        dto.setTipo("PARTIDO");
        dto.setTitulo("Inicio partido");
        dto.setMensaje("El partido comienza");

        mockMvc.perform(post("/api/notificaciones/partido/1")
                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isNoContent());
    }

    @Test
    void notificarPorPartido_sinRolAdmin_retorna403() throws Exception {
        mockMvc.perform(post("/api/notificaciones/partido/1")
                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_USER")))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void notificarPorPartido_sinAutenticacion_retorna401() throws Exception {
        mockMvc.perform(post("/api/notificaciones/partido/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void buscarPorFecha_retorna200() throws Exception {
        when(usuarioService.obtenerPorCorreo("user@test.com")).thenReturn(usuarioMock(1L));
        when(notificacionService.listarPorFecha(eq(1L), any(LocalDateTime.class),
                any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(notificacionLeida())));

        mockMvc.perform(get("/api/notificaciones/buscar?desde=2026-01-01&hasta=2026-12-31")
                .with(jwt().jwt(j -> j.subject("user@test.com"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    void buscarPorFecha_conPaginacion_retorna200() throws Exception {
        when(usuarioService.obtenerPorCorreo("user@test.com")).thenReturn(usuarioMock(1L));
        when(notificacionService.listarPorFecha(eq(1L), any(LocalDateTime.class),
                any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));

        mockMvc.perform(get("/api/notificaciones/buscar?desde=2026-05-01&hasta=2026-05-31&page=0&size=10")
                .with(jwt().jwt(j -> j.subject("user@test.com"))))
                .andExpect(status().isOk());
    }

    @Test
    void buscarPorFecha_sinAutenticacion_retorna401() throws Exception {
        mockMvc.perform(get("/api/notificaciones/buscar?desde=2026-01-01&hasta=2026-12-31"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void buscarPorFecha_sinParametros_retorna400() throws Exception {
        mockMvc.perform(get("/api/notificaciones/buscar")
                .with(jwt().jwt(j -> j.subject("user@test.com"))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void contarSinLeer_retorna200ConTotal() throws Exception {
        when(usuarioService.obtenerPorCorreo("user@test.com")).thenReturn(usuarioMock(1L));
        when(notificacionService.listarPorUsuario(1L))
                .thenReturn(List.of(notificacionLeida(), notificacionNoLeida()));

        mockMvc.perform(get("/api/notificaciones/sin-leer/conteo")
                .with(jwt().jwt(j -> j.subject("user@test.com"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(1));
    }

    @Test
    void contarSinLeer_todasLeidas_retorna0() throws Exception {
        when(usuarioService.obtenerPorCorreo("user@test.com")).thenReturn(usuarioMock(1L));
        when(notificacionService.listarPorUsuario(1L))
                .thenReturn(List.of(notificacionLeida(), notificacionLeida()));

        mockMvc.perform(get("/api/notificaciones/sin-leer/conteo")
                .with(jwt().jwt(j -> j.subject("user@test.com"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(0));
    }

    @Test
    void contarSinLeer_listaVacia_retorna0() throws Exception {
        when(usuarioService.obtenerPorCorreo("user@test.com")).thenReturn(usuarioMock(1L));
        when(notificacionService.listarPorUsuario(1L)).thenReturn(List.of());

        mockMvc.perform(get("/api/notificaciones/sin-leer/conteo")
                .with(jwt().jwt(j -> j.subject("user@test.com"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(0));
    }

    @Test
    void contarSinLeer_sinAutenticacion_retorna401() throws Exception {
        mockMvc.perform(get("/api/notificaciones/sin-leer/conteo"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void contarSinLeer_todasSinLeer_retornaTodas() throws Exception {
        when(usuarioService.obtenerPorCorreo("user@test.com")).thenReturn(usuarioMock(1L));
        when(notificacionService.listarPorUsuario(1L))
                .thenReturn(List.of(notificacionNoLeida(), notificacionNoLeida(), notificacionNoLeida()));

        mockMvc.perform(get("/api/notificaciones/sin-leer/conteo")
                .with(jwt().jwt(j -> j.subject("user@test.com"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(3));
    }
}