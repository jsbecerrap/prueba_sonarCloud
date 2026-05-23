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

class ApuestaIT extends BaseIntegrationTest {

    @MockitoBean
    private ApuestaService apuestaService;

    

    @Test
    void crear_conDatosValidos_retorna200() throws Exception {
        ApuestaRequestDTO request = new ApuestaRequestDTO();
        request.setNombre("Polla Mundial");
        request.setFechaCierre(LocalDateTime.now().plusDays(10));
        request.setUsuarioId(1L);

        ApuestaDTO response = new ApuestaDTO();
        when(apuestaService.crearApuesta(any())).thenReturn(response);

        mockMvc.perform(post("/api/apuestas/crear")
                        .header("Authorization", "Bearer " + tokenUsuario())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void crear_sinToken_retorna401() throws Exception {
        ApuestaRequestDTO request = new ApuestaRequestDTO();
        request.setNombre("Polla Mundial");
        request.setFechaCierre(LocalDateTime.now().plusDays(10));
        request.setUsuarioId(1L);

        mockMvc.perform(post("/api/apuestas/crear")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void crear_nombreVacio_retorna400() throws Exception {
        ApuestaRequestDTO request = new ApuestaRequestDTO();
        request.setNombre("");
        request.setFechaCierre(LocalDateTime.now().plusDays(10));
        request.setUsuarioId(1L);

        mockMvc.perform(post("/api/apuestas/crear")
                        .header("Authorization", "Bearer " + tokenUsuario())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void crear_nombreDemasiadoCorto_retorna400() throws Exception {
        ApuestaRequestDTO request = new ApuestaRequestDTO();
        request.setNombre("AB");
        request.setFechaCierre(LocalDateTime.now().plusDays(10));
        request.setUsuarioId(1L);

        mockMvc.perform(post("/api/apuestas/crear")
                        .header("Authorization", "Bearer " + tokenUsuario())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void crear_fechaPasada_retorna400() throws Exception {
        ApuestaRequestDTO request = new ApuestaRequestDTO();
        request.setNombre("Polla Mundial");
        request.setFechaCierre(LocalDateTime.now().minusDays(1));
        request.setUsuarioId(1L);

        mockMvc.perform(post("/api/apuestas/crear")
                        .header("Authorization", "Bearer " + tokenUsuario())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void crear_usuarioIdNulo_retorna400() throws Exception {
        ApuestaRequestDTO request = new ApuestaRequestDTO();
        request.setNombre("Polla Mundial");
        request.setFechaCierre(LocalDateTime.now().plusDays(10));

        mockMvc.perform(post("/api/apuestas/crear")
                        .header("Authorization", "Bearer " + tokenUsuario())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    

    @Test
    void unirse_codigoValido_retorna200() throws Exception {
        when(apuestaService.unirseApuesta(eq("CODIGO123"), eq(1L))).thenReturn(new ApuestaDTO());

        mockMvc.perform(post("/api/apuestas/unirse/1")
                        .header("Authorization", "Bearer " + tokenUsuario())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("\"CODIGO123\""))
                .andExpect(status().isOk());
    }

    @Test
    void unirse_codigoFormatoInvalido_retorna400() throws Exception {
        mockMvc.perform(post("/api/apuestas/unirse/1")
                        .header("Authorization", "Bearer " + tokenUsuario())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("\"co\""))
                .andExpect(status().isBadRequest());
    }

    @Test
    void unirse_sinToken_retorna401() throws Exception {
        mockMvc.perform(post("/api/apuestas/unirse/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("\"CODIGO123\""))
                .andExpect(status().isUnauthorized());
    }

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

        mockMvc.perform(post("/api/apuestas/pronostico")
                        .header("Authorization", "Bearer " + tokenUsuario())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void registrarPronostico_resultadoInvalido_retorna400() throws Exception {
        PronosticoRequestDTO request = new PronosticoRequestDTO();
        request.setResultadoPronosticado("NINGUNO");
        request.setGolesLocalPronosticados(2);
        request.setGolesVisitantePronosticados(1);

        mockMvc.perform(post("/api/apuestas/pronostico")
                        .header("Authorization", "Bearer " + tokenUsuario())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void registrarPronostico_golesNegativos_retorna400() throws Exception {
        PronosticoRequestDTO request = new PronosticoRequestDTO();
        request.setResultadoPronosticado("LOCAL");
        request.setGolesLocalPronosticados(-1);
        request.setGolesVisitantePronosticados(0);

        mockMvc.perform(post("/api/apuestas/pronostico")
                        .header("Authorization", "Bearer " + tokenUsuario())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void registrarPronostico_sinToken_retorna401() throws Exception {
        mockMvc.perform(post("/api/apuestas/pronostico")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isUnauthorized());
    }


    @Test
    void obtenerRanking_conToken_retorna200() throws Exception {
        when(apuestaService.obtenerRanking(1L)).thenReturn(List.of(new ParticipacionDTO()));

        mockMvc.perform(get("/api/apuestas/ranking/1")
                        .header("Authorization", "Bearer " + tokenUsuario()))
                .andExpect(status().isOk());
    }

    @Test
    void obtenerRanking_sinToken_retorna401() throws Exception {
        mockMvc.perform(get("/api/apuestas/ranking/1"))
                .andExpect(status().isUnauthorized());
    }


    @Test
    void cerrar_conRolAdmin_retorna200() throws Exception {
        when(apuestaService.cerrarApuesta(1L)).thenReturn(new ApuestaDTO());

        mockMvc.perform(post("/api/apuestas/cerrar/1")
                        .header("Authorization", "Bearer " + tokenAdmin()))
                .andExpect(status().isOk());
    }

    @Test
    void cerrar_conRolUser_retorna403() throws Exception {
        mockMvc.perform(post("/api/apuestas/cerrar/1")
                        .header("Authorization", "Bearer " + tokenUsuario()))
                .andExpect(status().isForbidden());
    }

    @Test
    void cerrar_sinToken_retorna401() throws Exception {
        mockMvc.perform(post("/api/apuestas/cerrar/1"))
                .andExpect(status().isUnauthorized());
    }


    @Test
    void calcularPuntos_conRolAdmin_retorna200() throws Exception {
        when(apuestaService.calcularPuntos(1L)).thenReturn(List.of());

        mockMvc.perform(get("/api/apuestas/puntos/1")
                        .header("Authorization", "Bearer " + tokenAdmin()))
                .andExpect(status().isOk());
    }

    @Test
    void calcularPuntos_conRolUser_retorna403() throws Exception {
        mockMvc.perform(get("/api/apuestas/puntos/1")
                        .header("Authorization", "Bearer " + tokenUsuario()))
                .andExpect(status().isForbidden());
    }


    @Test
    void listarPorUsuario_conToken_retorna200() throws Exception {
        when(apuestaService.listarApuestasPorUsuario(1L)).thenReturn(List.of());

        mockMvc.perform(get("/api/apuestas/usuario/1")
                        .header("Authorization", "Bearer " + tokenUsuario()))
                .andExpect(status().isOk());
    }

    @Test
    void listarPorUsuario_sinToken_retorna401() throws Exception {
        mockMvc.perform(get("/api/apuestas/usuario/1"))
                .andExpect(status().isUnauthorized());
    }


    @Test
    void obtenerApuesta_conToken_retorna200() throws Exception {
        when(apuestaService.obtenerApuesta(1L)).thenReturn(new ApuestaDTO());

        mockMvc.perform(get("/api/apuestas/1")
                        .header("Authorization", "Bearer " + tokenUsuario()))
                .andExpect(status().isOk());
    }

    @Test
    void obtenerApuesta_sinToken_retorna401() throws Exception {
        mockMvc.perform(get("/api/apuestas/1"))
                .andExpect(status().isUnauthorized());
    }

    

    @Test
    void listarParticipantes_conToken_retorna200() throws Exception {
        when(apuestaService.listarParticipantes(1L)).thenReturn(List.of());

        mockMvc.perform(get("/api/apuestas/participantes/1")
                        .header("Authorization", "Bearer " + tokenUsuario()))
                .andExpect(status().isOk());
    }

    @Test
    void listarParticipantes_sinToken_retorna401() throws Exception {
        mockMvc.perform(get("/api/apuestas/participantes/1"))
                .andExpect(status().isUnauthorized());
    }

 

    @Test
    void verificarPronostico_conToken_retorna200() throws Exception {
        when(apuestaService.verificarPronostico(1L)).thenReturn(new PronosticoDTO());

        mockMvc.perform(get("/api/apuestas/verificar/1")
                        .header("Authorization", "Bearer " + tokenUsuario()))
                .andExpect(status().isOk());
    }

    @Test
    void verificarPronostico_sinToken_retorna401() throws Exception {
        mockMvc.perform(get("/api/apuestas/verificar/1"))
                .andExpect(status().isUnauthorized());
    }

   

    @Test
    void misPronosticos_conToken_retorna200() throws Exception {
        when(apuestaService.misPronosticos(1L, 1L)).thenReturn(List.of());

        mockMvc.perform(get("/api/apuestas/mis-pronosticos/1/1")
                        .header("Authorization", "Bearer " + tokenUsuario()))
                .andExpect(status().isOk());
    }

    @Test
    void misPronosticos_sinToken_retorna401() throws Exception {
        mockMvc.perform(get("/api/apuestas/mis-pronosticos/1/1"))
                .andExpect(status().isUnauthorized());
    }



    @Test
    void editarPronostico_conDatosValidos_retorna200() throws Exception {
        PronosticoRequestDTO request = new PronosticoRequestDTO();
        request.setResultadoPronosticado("EMPATE");
        request.setGolesLocalPronosticados(1);
        request.setGolesVisitantePronosticados(1);

        when(apuestaService.editarPronostico(eq(1L), any(), eq(USER_EMAIL)))
                .thenReturn(new PronosticoDTO());

        mockMvc.perform(put("/api/apuestas/pronostico/1")
                        .header("Authorization", "Bearer " + tokenUsuario())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void editarPronostico_resultadoInvalido_retorna400() throws Exception {
        PronosticoRequestDTO request = new PronosticoRequestDTO();
        request.setResultadoPronosticado("INVALIDO");
        request.setGolesLocalPronosticados(1);
        request.setGolesVisitantePronosticados(1);

        mockMvc.perform(put("/api/apuestas/pronostico/1")
                        .header("Authorization", "Bearer " + tokenUsuario())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void editarPronostico_sinToken_retorna401() throws Exception {
        mockMvc.perform(put("/api/apuestas/pronostico/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isUnauthorized());
    }

    

    @Test
    void eliminarPronostico_conToken_retorna204() throws Exception {
        doNothing().when(apuestaService).eliminarPronostico(1L, USER_EMAIL);

        mockMvc.perform(delete("/api/apuestas/pronostico/1")
                        .header("Authorization", "Bearer " + tokenUsuario()))
                .andExpect(status().isNoContent());
    }

    @Test
    void eliminarPronostico_sinToken_retorna401() throws Exception {
        mockMvc.perform(delete("/api/apuestas/pronostico/1"))
                .andExpect(status().isUnauthorized());
    }



    @Test
    void calcularPuntosParciales_conToken_retorna200() throws Exception {
        when(apuestaService.calcularPuntosParciales(1L)).thenReturn(List.of());

        mockMvc.perform(get("/api/apuestas/puntos-parciales/1")
                        .header("Authorization", "Bearer " + tokenUsuario()))
                .andExpect(status().isOk());
    }

    @Test
    void calcularPuntosParciales_sinToken_retorna401() throws Exception {
        mockMvc.perform(get("/api/apuestas/puntos-parciales/1"))
                .andExpect(status().isUnauthorized());
    }

   

    @Test
    void listarTodas_conRolAdmin_retorna200() throws Exception {
        when(apuestaService.listarTodas()).thenReturn(List.of());

        mockMvc.perform(get("/api/apuestas/todas")
                        .header("Authorization", "Bearer " + tokenAdmin()))
                .andExpect(status().isOk());
    }

    @Test
    void listarTodas_conRolUser_retorna403() throws Exception {
        mockMvc.perform(get("/api/apuestas/todas")
                        .header("Authorization", "Bearer " + tokenUsuario()))
                .andExpect(status().isForbidden());
    }

    @Test
    void listarTodas_sinToken_retorna401() throws Exception {
        mockMvc.perform(get("/api/apuestas/todas"))
                .andExpect(status().isUnauthorized());
    }

    

    @Test
    void eliminarApuesta_conRolAdmin_retorna204() throws Exception {
        doNothing().when(apuestaService).eliminarApuesta(1L);

        mockMvc.perform(delete("/api/apuestas/1")
                        .header("Authorization", "Bearer " + tokenAdmin()))
                .andExpect(status().isNoContent());
    }

    @Test
    void eliminarApuesta_conRolUser_retorna403() throws Exception {
        mockMvc.perform(delete("/api/apuestas/1")
                        .header("Authorization", "Bearer " + tokenUsuario()))
                .andExpect(status().isForbidden());
    }

    @Test
    void eliminarApuesta_sinToken_retorna401() throws Exception {
        mockMvc.perform(delete("/api/apuestas/1"))
                .andExpect(status().isUnauthorized());
    }

    

    @Test
    void listarPorUsuarioCompleto_conToken_retorna200() throws Exception {
        when(apuestaService.listarApuestasPorUsuarioCompleto(1L)).thenReturn(List.of(new ApuestaConParticipantesDTO()));

        mockMvc.perform(get("/api/apuestas/usuario/1/completo")
                        .header("Authorization", "Bearer " + tokenUsuario()))
                .andExpect(status().isOk());
    }

    @Test
    void listarPorUsuarioCompleto_sinToken_retorna401() throws Exception {
        mockMvc.perform(get("/api/apuestas/usuario/1/completo"))
                .andExpect(status().isUnauthorized());
    }
}