package co.edu.unbosque.mundial_2026.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import co.edu.unbosque.mundial_2026.dto.ApuestaConParticipantesDTO;
import co.edu.unbosque.mundial_2026.dto.ApuestaDTO;
import co.edu.unbosque.mundial_2026.dto.ParticipacionDTO;
import co.edu.unbosque.mundial_2026.dto.PronosticoDTO;
import co.edu.unbosque.mundial_2026.dto.request.ApuestaRequestDTO;
import co.edu.unbosque.mundial_2026.dto.request.PronosticoRequestDTO;
import co.edu.unbosque.mundial_2026.exception.CodigoInvalidoException;
import co.edu.unbosque.mundial_2026.service.ApuestaService;

@WebMvcTest(ApuestaRestController.class)
@ActiveProfiles("test")
class ApuestaRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ApuestaService apuestaService;

    private final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    private ApuestaRequestDTO apuestaRequestValido() {
        ApuestaRequestDTO dto = new ApuestaRequestDTO();
        dto.setNombre("Polla2026");
        dto.setFechaCierre(LocalDateTime.now().plusDays(5));
        dto.setUsuarioId(1L);
        return dto;
    }

    private PronosticoRequestDTO pronosticoRequestValido() {
        PronosticoRequestDTO dto = new PronosticoRequestDTO();
        dto.setResultadoPronosticado("LOCAL");
        dto.setGolesLocalPronosticados(2);
        dto.setGolesVisitantePronosticados(0);
        dto.setUsuarioId(1L);
        dto.setApuestaId(1L);
        dto.setPartidoId(1L);
        return dto;
    }

    @Test
    void crearApuesta_valido_retorna200() throws Exception {
        when(apuestaService.crearApuesta(any())).thenReturn(new ApuestaDTO());

        mockMvc.perform(post("/api/apuestas/crear")
                .with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(apuestaRequestValido())))
                .andExpect(status().isOk());
    }

    @Test
    void crearApuesta_bodyInvalido_retorna400() throws Exception {
        ApuestaRequestDTO dto = new ApuestaRequestDTO();

        mockMvc.perform(post("/api/apuestas/crear")
                .with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void crearApuesta_sinAutenticacion_retorna401() throws Exception {
        mockMvc.perform(post("/api/apuestas/crear")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(apuestaRequestValido())))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void unirseApuesta_codigoValido_retorna200() throws Exception {
        when(apuestaService.unirseApuesta(anyString(), anyLong())).thenReturn(new ApuestaDTO());

        mockMvc.perform(post("/api/apuestas/unirse/1")
                .with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content("\"AbCd1234\""))
                .andExpect(status().isOk());
    }

    @Test
    void unirseApuesta_codigoUUID_retorna200() throws Exception {
        when(apuestaService.unirseApuesta(anyString(), anyLong())).thenReturn(new ApuestaDTO());

        mockMvc.perform(post("/api/apuestas/unirse/1")
                .with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content("\"550e8400-e29b-41d4-a716-446655440000\""))
                .andExpect(status().isOk());
    }

    @Test
    void unirseApuesta_codigoInvalido_retorna400() throws Exception {
        mockMvc.perform(post("/api/apuestas/unirse/1")
                .with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content("\"!!inv\""))
                .andExpect(status().isBadRequest());
    }

    @Test
    void unirseApuesta_codigoNull_retorna400() throws Exception {
        mockMvc.perform(post("/api/apuestas/unirse/1")
                .with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content("null"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void registrarPronostico_valido_retorna200() throws Exception {
        when(apuestaService.registrarPronostico(any())).thenReturn(new PronosticoDTO());

        mockMvc.perform(post("/api/apuestas/pronostico")
                .with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(pronosticoRequestValido())))
                .andExpect(status().isOk());
    }

    @Test
    void registrarPronostico_resultadoInvalido_retorna400() throws Exception {
        PronosticoRequestDTO dto = pronosticoRequestValido();
        dto.setResultadoPronosticado("INVALIDO");

        mockMvc.perform(post("/api/apuestas/pronostico")
                .with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void registrarPronostico_sinResultado_retorna400() throws Exception {
        PronosticoRequestDTO dto = pronosticoRequestValido();
        dto.setResultadoPronosticado(null);

        mockMvc.perform(post("/api/apuestas/pronostico")
                .with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void obtenerRanking_retorna200() throws Exception {
        when(apuestaService.obtenerRanking(1L)).thenReturn(List.of(new ParticipacionDTO()));

        mockMvc.perform(get("/api/apuestas/ranking/1")
                .with(jwt()))
                .andExpect(status().isOk());
    }

    @Test
    void cerrarApuesta_conRolAdmin_retorna200() throws Exception {
        when(apuestaService.cerrarApuesta(1L)).thenReturn(new ApuestaDTO());

        mockMvc.perform(post("/api/apuestas/cerrar/1")
                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
                .andExpect(status().isOk());
    }

    @Test
    void cerrarApuesta_sinRolAdmin_retorna403() throws Exception {
        mockMvc.perform(post("/api/apuestas/cerrar/1")
                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_USER"))))
                .andExpect(status().isForbidden());
    }

    @Test
    void calcularPuntos_conRolAdmin_retorna200() throws Exception {
        when(apuestaService.calcularPuntos(1L)).thenReturn(List.of());

        mockMvc.perform(get("/api/apuestas/puntos/1")
                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
                .andExpect(status().isOk());
    }

    @Test
    void calcularPuntos_sinRolAdmin_retorna403() throws Exception {
        mockMvc.perform(get("/api/apuestas/puntos/1")
                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_USER"))))
                .andExpect(status().isForbidden());
    }

    @Test
    void listarApuestasPorUsuario_retorna200() throws Exception {
        when(apuestaService.listarApuestasPorUsuario(1L)).thenReturn(List.of(new ApuestaDTO()));

        mockMvc.perform(get("/api/apuestas/usuario/1")
                .with(jwt()))
                .andExpect(status().isOk());
    }

    @Test
    void obtenerApuesta_retorna200() throws Exception {
        when(apuestaService.obtenerApuesta(1L)).thenReturn(new ApuestaDTO());

        mockMvc.perform(get("/api/apuestas/1")
                .with(jwt()))
                .andExpect(status().isOk());
    }

    @Test
    void listarParticipantes_retorna200() throws Exception {
        when(apuestaService.listarParticipantes(1L)).thenReturn(List.of());

        mockMvc.perform(get("/api/apuestas/participantes/1")
                .with(jwt()))
                .andExpect(status().isOk());
    }

    @Test
    void verificarPronostico_retorna200() throws Exception {
        when(apuestaService.verificarPronostico(1L)).thenReturn(new PronosticoDTO());

        mockMvc.perform(get("/api/apuestas/verificar/1")
                .with(jwt()))
                .andExpect(status().isOk());
    }

    @Test
    void misPronosticos_retorna200() throws Exception {
        when(apuestaService.misPronosticos(1L, 2L)).thenReturn(List.of());

        mockMvc.perform(get("/api/apuestas/mis-pronosticos/1/2")
                .with(jwt()))
                .andExpect(status().isOk());
    }

    @Test
    void editarPronostico_valido_retorna200() throws Exception {
        when(apuestaService.editarPronostico(anyLong(), any(), any())).thenReturn(new PronosticoDTO());

        mockMvc.perform(put("/api/apuestas/pronostico/1")
                .with(jwt().jwt(j -> j.subject("user@test.com")))
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(pronosticoRequestValido())))
                .andExpect(status().isOk());
    }

    @Test
    void editarPronostico_bodyInvalido_retorna400() throws Exception {
        PronosticoRequestDTO dto = pronosticoRequestValido();
        dto.setResultadoPronosticado(null);

        mockMvc.perform(put("/api/apuestas/pronostico/1")
                .with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void eliminarPronostico_retorna204() throws Exception {
        doNothing().when(apuestaService).eliminarPronostico(anyLong(), any());

        mockMvc.perform(delete("/api/apuestas/pronostico/1")
                .with(jwt().jwt(j -> j.subject("user@test.com"))))
                .andExpect(status().isNoContent());
    }

    @Test
    void eliminarPronostico_sinAuth_retorna401() throws Exception {
        mockMvc.perform(delete("/api/apuestas/pronostico/1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void calcularPuntosParciales_retorna200() throws Exception {
        when(apuestaService.calcularPuntosParciales(1L)).thenReturn(List.of());

        mockMvc.perform(get("/api/apuestas/puntos-parciales/1")
                .with(jwt()))
                .andExpect(status().isOk());
    }

    @Test
    void listarTodas_conRolAdmin_retorna200() throws Exception {
        when(apuestaService.listarTodas()).thenReturn(List.of());

        mockMvc.perform(get("/api/apuestas/todas")
                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
                .andExpect(status().isOk());
    }

    @Test
    void listarTodas_sinRolAdmin_retorna403() throws Exception {
        mockMvc.perform(get("/api/apuestas/todas")
                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_USER"))))
                .andExpect(status().isForbidden());
    }

    @Test
    void eliminarApuesta_conRolAdmin_retorna204() throws Exception {
        doNothing().when(apuestaService).eliminarApuesta(1L);

        mockMvc.perform(delete("/api/apuestas/1")
                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
                .andExpect(status().isNoContent());
    }

    @Test
    void eliminarApuesta_sinRolAdmin_retorna403() throws Exception {
        mockMvc.perform(delete("/api/apuestas/1")
                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_USER"))))
                .andExpect(status().isForbidden());
    }

    @Test
    void listarApuestasPorUsuarioCompleto_retorna200() throws Exception {
        when(apuestaService.listarApuestasPorUsuarioCompleto(1L)).thenReturn(List.of(new ApuestaConParticipantesDTO()));

        mockMvc.perform(get("/api/apuestas/usuario/1/completo")
                .with(jwt()))
                .andExpect(status().isOk());
    }

    @Test
    void unirseApuesta_serviceLanzaExcepcion_retorna400() throws Exception {
        when(apuestaService.unirseApuesta(anyString(), anyLong()))
                .thenThrow(new CodigoInvalidoException("Codigo ya usado"));

        mockMvc.perform(post("/api/apuestas/unirse/1")
                .with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content("\"AbCd1234\""))
                .andExpect(status().isBadRequest());
    }

    @Test
    void editarPronostico_golesNegativos_retorna400() throws Exception {
        PronosticoRequestDTO dto = pronosticoRequestValido();
        dto.setGolesLocalPronosticados(-1);

        mockMvc.perform(put("/api/apuestas/pronostico/1")
                .with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void editarPronostico_golesExcesivos_retorna400() throws Exception {
        PronosticoRequestDTO dto = pronosticoRequestValido();
        dto.setGolesVisitantePronosticados(21);

        mockMvc.perform(put("/api/apuestas/pronostico/1")
                .with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void crearApuesta_nombreCorto_retorna400() throws Exception {
        ApuestaRequestDTO dto = apuestaRequestValido();
        dto.setNombre("Ab");

        mockMvc.perform(post("/api/apuestas/crear")
                .with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void crearApuesta_fechaPasada_retorna400() throws Exception {
        ApuestaRequestDTO dto = apuestaRequestValido();
        dto.setFechaCierre(LocalDateTime.now().minusDays(1));

        mockMvc.perform(post("/api/apuestas/crear")
                .with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void doNothing_eliminarPronostico_conServiceThrows_retorna500() throws Exception {
        doThrow(new RuntimeException("Error interno")).when(apuestaService).eliminarPronostico(anyLong(), anyString());

        mockMvc.perform(delete("/api/apuestas/pronostico/99")
                .with(jwt().jwt(j -> j.subject("user@test.com"))))
                .andExpect(status().is5xxServerError());
    }

    @Test
    void registrarPronostico_empate_retorna200() throws Exception {
        PronosticoRequestDTO dto = pronosticoRequestValido();
        dto.setResultadoPronosticado("EMPATE");
        when(apuestaService.registrarPronostico(any())).thenReturn(new PronosticoDTO());

        mockMvc.perform(post("/api/apuestas/pronostico")
                .with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    void registrarPronostico_visitante_retorna200() throws Exception {
        PronosticoRequestDTO dto = pronosticoRequestValido();
        dto.setResultadoPronosticado("VISITANTE");
        when(apuestaService.registrarPronostico(any())).thenReturn(new PronosticoDTO());

        mockMvc.perform(post("/api/apuestas/pronostico")
                .with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    void editarPronostico_sinAuth_retorna401() throws Exception {
        mockMvc.perform(put("/api/apuestas/pronostico/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(pronosticoRequestValido())))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void listarApuestasPorUsuario_listaVacia_retorna200() throws Exception {
        when(apuestaService.listarApuestasPorUsuario(eq(99L))).thenReturn(List.of());

        mockMvc.perform(get("/api/apuestas/usuario/99")
                .with(jwt()))
                .andExpect(status().isOk());
    }
}