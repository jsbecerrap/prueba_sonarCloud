package co.edu.unbosque.mundial_2026.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import co.edu.unbosque.mundial_2026.dto.ApuestaConParticipantesDTO;
import co.edu.unbosque.mundial_2026.dto.ApuestaDTO;
import co.edu.unbosque.mundial_2026.dto.ParticipacionDTO;
import co.edu.unbosque.mundial_2026.dto.PronosticoDTO;
import co.edu.unbosque.mundial_2026.dto.request.ApuestaRequestDTO;
import co.edu.unbosque.mundial_2026.dto.request.PronosticoRequestDTO;
import co.edu.unbosque.mundial_2026.exception.CodigoInvalidoException;
import co.edu.unbosque.mundial_2026.service.ApuestaService;

/**
 * Pruebas unitarias para {@link ApuestaController}
 * Verifica el comportamiento del controlador usando mocks del servicio {@link ApuestaService}
 */
@ExtendWith(MockitoExtension.class)
class ApuestaRestControllerTest {

    @Mock
    private ApuestaService apuestaService;

    @InjectMocks
    private ApuestaController controller;

    /** Correo de usuario de prueba utilizado en los tests de edicion y eliminacion de pronosticos */
    private static final String USER_CORREO = "user@test.com";

    /** 
     * @return ApuestaRequestDTO
     */
    private ApuestaRequestDTO apuestaRequestValido() {
        ApuestaRequestDTO dto = new ApuestaRequestDTO();
        dto.setNombre("Polla2026");
        dto.setFechaCierre(LocalDateTime.now().plusDays(5));
        dto.setUsuarioId(1L);
        return dto;
    }

    /** 
     * @return PronosticoRequestDTO
     */
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

    /**
     * Verifica que al crear una apuesta con datos validos el controlador retorna HTTP 200
     * y el cuerpo de la respuesta no es nulo
     */
    @Test
    void crearApuesta_valido_retorna200() {
        when(apuestaService.crearApuesta(any())).thenReturn(new ApuestaDTO());

        ResponseEntity<ApuestaDTO> res = controller.crearApuesta(apuestaRequestValido());

        assertEquals(200, res.getStatusCode().value());
        assertNotNull(res.getBody());
        verify(apuestaService).crearApuesta(any());
    }

    /**
     * Verifica que el DTO retornado por el servicio es exactamente el que llega en la respuesta del controlador
     */
    @Test
    void crearApuesta_serviceRetornaDTO_retornaEseDTO() {
        ApuestaDTO esperado = new ApuestaDTO();
        esperado.setId(42L);
        when(apuestaService.crearApuesta(any())).thenReturn(esperado);

        ResponseEntity<ApuestaDTO> res = controller.crearApuesta(apuestaRequestValido());

        assertEquals(42L, res.getBody().getId());
    }

    /**
     * Verifica que si el servicio lanza una excepcion al crear apuesta, el controlador la propaga sin suprimirla
     */
    @Test
    void crearApuesta_serviceLanzaExcepcion_propaga() {
        when(apuestaService.crearApuesta(any())).thenThrow(new RuntimeException("error"));

        assertThrows(RuntimeException.class, () -> controller.crearApuesta(apuestaRequestValido()));
    }

    /**
     * Verifica que unirse a una apuesta con un codigo alfanumerico valido retorna HTTP 200
     * y el cuerpo de la respuesta no es nulo
     */
    @Test
    void unirseApuesta_codigoValido_retorna200() {
        when(apuestaService.unirseApuesta(anyString(), anyLong())).thenReturn(new ApuestaDTO());

        ResponseEntity<ApuestaDTO> res = controller.unirseApuesta(1L, "\"AbCd1234\"");

        assertEquals(200, res.getStatusCode().value());
        assertNotNull(res.getBody());
    }

    /**
     * Verifica que unirse a una apuesta con un codigo en formato UUID retorna HTTP 200
     */
    @Test
    void unirseApuesta_codigoUUID_retorna200() {
        when(apuestaService.unirseApuesta(anyString(), anyLong())).thenReturn(new ApuestaDTO());

        ResponseEntity<ApuestaDTO> res = controller.unirseApuesta(1L, "\"550e8400-e29b-41d4-a716-446655440000\"");

        assertEquals(200, res.getStatusCode().value());
    }

    /**
     * Verifica que al pasar un codigo con caracteres invalidos se lanza {@link CodigoInvalidoException}
     */
    @Test
    void unirseApuesta_codigoInvalido_lanzaCodigoInvalidoException() {
        assertThrows(CodigoInvalidoException.class,
                () -> controller.unirseApuesta(1L, "\"!!inv\""));
    }

    /**
     * Verifica que al pasar un codigo vacio se lanza {@link CodigoInvalidoException}
     */
    @Test
    void unirseApuesta_codigoVacio_lanzaCodigoInvalidoException() {
        assertThrows(CodigoInvalidoException.class,
                () -> controller.unirseApuesta(1L, "\"\""));
    }

    /**
     * Verifica que si el servicio lanza una excepcion al unirse a la apuesta, el controlador la propaga
     */
    @Test
    void unirseApuesta_serviceLanzaExcepcion_propaga() {
        when(apuestaService.unirseApuesta(anyString(), anyLong()))
                .thenThrow(new CodigoInvalidoException("Codigo ya usado"));

        assertThrows(CodigoInvalidoException.class,
                () -> controller.unirseApuesta(1L, "\"AbCd1234\""));
    }

    /**
     * Verifica que registrar un pronostico con datos validos retorna HTTP 200
     * y el cuerpo de la respuesta no es nulo
     */
    @Test
    void registrarPronostico_valido_retorna200() {
        when(apuestaService.registrarPronostico(any())).thenReturn(new PronosticoDTO());

        ResponseEntity<PronosticoDTO> res = controller.registrarPronostico(pronosticoRequestValido());

        assertEquals(200, res.getStatusCode().value());
        assertNotNull(res.getBody());
        verify(apuestaService).registrarPronostico(any());
    }

    /**
     * Verifica que registrar un pronostico con resultado EMPATE retorna HTTP 200
     */
    @Test
    void registrarPronostico_empate_retorna200() {
        PronosticoRequestDTO dto = pronosticoRequestValido();
        dto.setResultadoPronosticado("EMPATE");
        when(apuestaService.registrarPronostico(any())).thenReturn(new PronosticoDTO());

        ResponseEntity<PronosticoDTO> res = controller.registrarPronostico(dto);

        assertEquals(200, res.getStatusCode().value());
    }

    /**
     * Verifica que registrar un pronostico con resultado VISITANTE retorna HTTP 200
     */
    @Test
    void registrarPronostico_visitante_retorna200() {
        PronosticoRequestDTO dto = pronosticoRequestValido();
        dto.setResultadoPronosticado("VISITANTE");
        when(apuestaService.registrarPronostico(any())).thenReturn(new PronosticoDTO());

        ResponseEntity<PronosticoDTO> res = controller.registrarPronostico(dto);

        assertEquals(200, res.getStatusCode().value());
    }

    /**
     * Verifica que si el servicio lanza una excepcion al registrar pronostico, el controlador la propaga
     */
    @Test
    void registrarPronostico_serviceLanzaExcepcion_propaga() {
        when(apuestaService.registrarPronostico(any())).thenThrow(new RuntimeException("error"));

        assertThrows(RuntimeException.class,
                () -> controller.registrarPronostico(pronosticoRequestValido()));
    }

    /**
     * Verifica que obtener el ranking de una apuesta retorna HTTP 200
     * y la lista contiene exactamente un elemento
     */
    @Test
    void obtenerRanking_retorna200ConLista() {
        when(apuestaService.obtenerRanking(1L)).thenReturn(List.of(new ParticipacionDTO()));

        ResponseEntity<List<ParticipacionDTO>> res = controller.obtenerRanking(1L);

        assertEquals(200, res.getStatusCode().value());
        assertEquals(1, res.getBody().size());
        verify(apuestaService).obtenerRanking(1L);
    }

    /**
     * Verifica que obtener el ranking de una apuesta sin participantes retorna HTTP 200 con lista vacia
     */
    @Test
    void obtenerRanking_listaVacia_retorna200() {
        when(apuestaService.obtenerRanking(99L)).thenReturn(List.of());

        ResponseEntity<List<ParticipacionDTO>> res = controller.obtenerRanking(99L);

        assertEquals(200, res.getStatusCode().value());
        assertTrue(res.getBody().isEmpty());
    }

    /**
     * Verifica que cerrar una apuesta existente retorna HTTP 200 y el cuerpo no es nulo
     */
    @Test
    void cerrarApuesta_retorna200() {
        when(apuestaService.cerrarApuesta(1L)).thenReturn(new ApuestaDTO());

        ResponseEntity<ApuestaDTO> res = controller.cerrarApuesta(1L);

        assertEquals(200, res.getStatusCode().value());
        assertNotNull(res.getBody());
        verify(apuestaService).cerrarApuesta(1L);
    }

    /**
     * Verifica que si el servicio lanza una excepcion al cerrar la apuesta, el controlador la propaga
     */
    @Test
    void cerrarApuesta_serviceLanzaExcepcion_propaga() {
        when(apuestaService.cerrarApuesta(99L)).thenThrow(new RuntimeException("no encontrada"));

        assertThrows(RuntimeException.class, () -> controller.cerrarApuesta(99L));
    }

    /**
     * Verifica que calcular puntos de una apuesta retorna HTTP 200
     * y la lista contiene exactamente un elemento
     */
    @Test
    void calcularPuntos_retorna200ConLista() {
        when(apuestaService.calcularPuntos(1L)).thenReturn(List.of(new PronosticoDTO()));

        ResponseEntity<List<PronosticoDTO>> res = controller.calcularPuntos(1L);

        assertEquals(200, res.getStatusCode().value());
        assertEquals(1, res.getBody().size());
        verify(apuestaService).calcularPuntos(1L);
    }

    /**
     * Verifica que calcular puntos cuando no hay pronosticos retorna HTTP 200 con lista vacia
     */
    @Test
    void calcularPuntos_listaVacia_retorna200() {
        when(apuestaService.calcularPuntos(1L)).thenReturn(List.of());

        ResponseEntity<List<PronosticoDTO>> res = controller.calcularPuntos(1L);

        assertEquals(200, res.getStatusCode().value());
        assertTrue(res.getBody().isEmpty());
    }

    /**
     * Verifica que listar apuestas de un usuario retorna HTTP 200
     * y la lista contiene exactamente un elemento
     */
    @Test
    void listarApuestasPorUsuario_retorna200ConLista() {
        when(apuestaService.listarApuestasPorUsuario(1L)).thenReturn(List.of(new ApuestaDTO()));

        ResponseEntity<List<ApuestaDTO>> res = controller.listarApuestasPorUsuario(1L);

        assertEquals(200, res.getStatusCode().value());
        assertEquals(1, res.getBody().size());
        verify(apuestaService).listarApuestasPorUsuario(1L);
    }

    /**
     * Verifica que listar apuestas de un usuario sin apuestas retorna HTTP 200 con lista vacia
     */
    @Test
    void listarApuestasPorUsuario_listaVacia_retorna200() {
        when(apuestaService.listarApuestasPorUsuario(99L)).thenReturn(List.of());

        ResponseEntity<List<ApuestaDTO>> res = controller.listarApuestasPorUsuario(99L);

        assertEquals(200, res.getStatusCode().value());
        assertTrue(res.getBody().isEmpty());
    }

    /**
     * Verifica que obtener una apuesta existente retorna HTTP 200
     * y el ID del DTO coincide con el solicitado
     */
    @Test
    void obtenerApuesta_existente_retorna200() {
        ApuestaDTO dto = new ApuestaDTO();
        dto.setId(1L);
        when(apuestaService.obtenerApuesta(1L)).thenReturn(dto);

        ResponseEntity<ApuestaDTO> res = controller.obtenerApuesta(1L);

        assertEquals(200, res.getStatusCode().value());
        assertEquals(1L, res.getBody().getId());
        verify(apuestaService).obtenerApuesta(1L);
    }

    /**
     * Verifica que obtener una apuesta inexistente propaga la excepcion lanzada por el servicio
     */
    @Test
    void obtenerApuesta_noExistente_propaga() {
        when(apuestaService.obtenerApuesta(99L)).thenThrow(new RuntimeException("no encontrada"));

        assertThrows(RuntimeException.class, () -> controller.obtenerApuesta(99L));
    }

    /**
     * Verifica que listar participantes de una apuesta retorna HTTP 200
     * y la lista contiene exactamente un elemento
     */
    @Test
    void listarParticipantes_retorna200ConLista() {
        when(apuestaService.listarParticipantes(1L)).thenReturn(List.of(new ParticipacionDTO()));

        ResponseEntity<List<ParticipacionDTO>> res = controller.listarParticipantes(1L);

        assertEquals(200, res.getStatusCode().value());
        assertEquals(1, res.getBody().size());
        verify(apuestaService).listarParticipantes(1L);
    }

    /**
     * Verifica que listar participantes de una apuesta sin participantes retorna HTTP 200 con lista vacia
     */
    @Test
    void listarParticipantes_listaVacia_retorna200() {
        when(apuestaService.listarParticipantes(1L)).thenReturn(List.of());

        ResponseEntity<List<ParticipacionDTO>> res = controller.listarParticipantes(1L);

        assertEquals(200, res.getStatusCode().value());
        assertTrue(res.getBody().isEmpty());
    }

    /**
     * Verifica que verificar un pronostico existente retorna HTTP 200 y el cuerpo no es nulo
     */
    @Test
    void verificarPronostico_retorna200() {
        when(apuestaService.verificarPronostico(1L)).thenReturn(new PronosticoDTO());

        ResponseEntity<PronosticoDTO> res = controller.verificarPronostico(1L);

        assertEquals(200, res.getStatusCode().value());
        assertNotNull(res.getBody());
        verify(apuestaService).verificarPronostico(1L);
    }

    /**
     * Verifica que verificar un pronostico inexistente propaga la excepcion lanzada por el servicio
     */
    @Test
    void verificarPronostico_noExistente_propaga() {
        when(apuestaService.verificarPronostico(99L)).thenThrow(new RuntimeException("no encontrado"));

        assertThrows(RuntimeException.class, () -> controller.verificarPronostico(99L));
    }

    /**
     * Verifica que obtener los pronosticos de un usuario en una apuesta retorna HTTP 200
     * y la lista contiene exactamente un elemento
     */
    @Test
    void misPronosticos_retorna200ConLista() {
        when(apuestaService.misPronosticos(1L, 2L)).thenReturn(List.of(new PronosticoDTO()));

        ResponseEntity<List<PronosticoDTO>> res = controller.misPronosticos(1L, 2L);

        assertEquals(200, res.getStatusCode().value());
        assertEquals(1, res.getBody().size());
        verify(apuestaService).misPronosticos(1L, 2L);
    }

    /**
     * Verifica que obtener los pronosticos cuando el usuario no tiene ninguno retorna HTTP 200 con lista vacia
     */
    @Test
    void misPronosticos_listaVacia_retorna200() {
        when(apuestaService.misPronosticos(1L, 2L)).thenReturn(List.of());

        ResponseEntity<List<PronosticoDTO>> res = controller.misPronosticos(1L, 2L);

        assertEquals(200, res.getStatusCode().value());
        assertTrue(res.getBody().isEmpty());
    }

    /**
     * Verifica que editar un pronostico con datos validos retorna HTTP 200 y el cuerpo no es nulo
     */
    @Test
    void editarPronostico_valido_retorna200() {
        when(apuestaService.editarPronostico(anyLong(), any(), any())).thenReturn(new PronosticoDTO());

        ResponseEntity<PronosticoDTO> res = controller.editarPronostico(1L, pronosticoRequestValido(), USER_CORREO);

        assertEquals(200, res.getStatusCode().value());
        assertNotNull(res.getBody());
        verify(apuestaService).editarPronostico(eq(1L), any(), eq(USER_CORREO));
    }

    /**
     * Verifica que si el servicio lanza una excepcion al editar el pronostico, el controlador la propaga
     */
    @Test
    void editarPronostico_serviceLanzaExcepcion_propaga() {
        when(apuestaService.editarPronostico(anyLong(), any(), any()))
                .thenThrow(new RuntimeException("no autorizado"));

        assertThrows(RuntimeException.class,
                () -> controller.editarPronostico(1L, pronosticoRequestValido(), USER_CORREO));
    }

    /**
     * Verifica que eliminar un pronostico existente retorna HTTP 204 y el cuerpo es nulo
     */
    @Test
    void eliminarPronostico_retorna204() {
        doNothing().when(apuestaService).eliminarPronostico(anyLong(), any());

        ResponseEntity<Void> res = controller.eliminarPronostico(1L, USER_CORREO);

        assertEquals(204, res.getStatusCode().value());
        assertNull(res.getBody());
        verify(apuestaService).eliminarPronostico(1L, USER_CORREO);
    }

    /**
     * Verifica que si el servicio lanza una excepcion al eliminar el pronostico, el controlador la propaga
     */
    @Test
    void eliminarPronostico_serviceLanzaExcepcion_propaga() {
        doThrow(new RuntimeException("error interno")).when(apuestaService)
                .eliminarPronostico(anyLong(), anyString());

        assertThrows(RuntimeException.class,
                () -> controller.eliminarPronostico(99L, USER_CORREO));
    }

    /**
     * Verifica que calcular puntos parciales de una apuesta retorna HTTP 200
     * y la lista contiene exactamente un elemento
     */
    @Test
    void calcularPuntosParciales_retorna200ConLista() {
        when(apuestaService.calcularPuntosParciales(1L)).thenReturn(List.of(new PronosticoDTO()));

        ResponseEntity<List<PronosticoDTO>> res = controller.calcularPuntosParciales(1L);

        assertEquals(200, res.getStatusCode().value());
        assertEquals(1, res.getBody().size());
        verify(apuestaService).calcularPuntosParciales(1L);
    }

    /**
     * Verifica que calcular puntos parciales cuando no hay pronosticos retorna HTTP 200 con lista vacia
     */
    @Test
    void calcularPuntosParciales_listaVacia_retorna200() {
        when(apuestaService.calcularPuntosParciales(1L)).thenReturn(List.of());

        ResponseEntity<List<PronosticoDTO>> res = controller.calcularPuntosParciales(1L);

        assertEquals(200, res.getStatusCode().value());
        assertTrue(res.getBody().isEmpty());
    }

    /**
     * Verifica que listar todas las apuestas retorna HTTP 200
     * y la lista contiene exactamente un elemento
     */
    @Test
    void listarTodas_retorna200ConLista() {
        when(apuestaService.listarTodas()).thenReturn(List.of(new ApuestaDTO()));

        ResponseEntity<List<ApuestaDTO>> res = controller.listarTodas();

        assertEquals(200, res.getStatusCode().value());
        assertEquals(1, res.getBody().size());
        verify(apuestaService).listarTodas();
    }

    /**
     * Verifica que listar todas las apuestas cuando no hay ninguna retorna HTTP 200 con lista vacia
     */
    @Test
    void listarTodas_listaVacia_retorna200() {
        when(apuestaService.listarTodas()).thenReturn(List.of());

        ResponseEntity<List<ApuestaDTO>> res = controller.listarTodas();

        assertEquals(200, res.getStatusCode().value());
        assertTrue(res.getBody().isEmpty());
    }

    /**
     * Verifica que eliminar una apuesta existente retorna HTTP 204 y el cuerpo es nulo
     */
    @Test
    void eliminarApuesta_retorna204() {
        doNothing().when(apuestaService).eliminarApuesta(1L);

        ResponseEntity<Void> res = controller.eliminarApuesta(1L);

        assertEquals(204, res.getStatusCode().value());
        assertNull(res.getBody());
        verify(apuestaService).eliminarApuesta(1L);
    }

    /**
     * Verifica que si el servicio lanza una excepcion al eliminar la apuesta, el controlador la propaga
     */
    @Test
    void eliminarApuesta_serviceLanzaExcepcion_propaga() {
        doThrow(new RuntimeException("no encontrada")).when(apuestaService).eliminarApuesta(99L);

        assertThrows(RuntimeException.class, () -> controller.eliminarApuesta(99L));
    }

    /**
     * Verifica que listar apuestas completas de un usuario retorna HTTP 200
     * y la lista contiene exactamente un elemento con informacion de participantes
     */
    @Test
    void listarApuestasPorUsuarioCompleto_retorna200ConLista() {
        when(apuestaService.listarApuestasPorUsuarioCompleto(1L))
                .thenReturn(List.of(new ApuestaConParticipantesDTO()));

        ResponseEntity<List<ApuestaConParticipantesDTO>> res = controller.listarApuestasPorUsuarioCompleto(1L);

        assertEquals(200, res.getStatusCode().value());
        assertEquals(1, res.getBody().size());
        verify(apuestaService).listarApuestasPorUsuarioCompleto(1L);
    }

    /**
     * Verifica que listar apuestas completas de un usuario sin apuestas retorna HTTP 200 con lista vacia
     */
    @Test
    void listarApuestasPorUsuarioCompleto_listaVacia_retorna200() {
        when(apuestaService.listarApuestasPorUsuarioCompleto(99L)).thenReturn(List.of());

        ResponseEntity<List<ApuestaConParticipantesDTO>> res = controller.listarApuestasPorUsuarioCompleto(99L);

        assertEquals(200, res.getStatusCode().value());
        assertTrue(res.getBody().isEmpty());
    }
}