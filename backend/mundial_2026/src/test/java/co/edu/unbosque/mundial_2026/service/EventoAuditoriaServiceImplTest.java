package co.edu.unbosque.mundial_2026.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;


import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import co.edu.unbosque.mundial_2026.dto.EventoAuditoriaDTO;
import co.edu.unbosque.mundial_2026.entity.EventoAuditoria;
import co.edu.unbosque.mundial_2026.entity.Usuario;
import co.edu.unbosque.mundial_2026.repository.EventoAuditoriaRepository;

/**
 * Pruebas unitarias para EventoAuditoriaServiceImpl.
 */
@ExtendWith(MockitoExtension.class)
class EventoAuditoriaServiceImplTest {

    /**
     * Mock del repositorio de auditoría.
     */
    @Mock
    private EventoAuditoriaRepository repository;

    /**
     * Mock del servicio de usuarios.
     */
    @Mock
    private UsuarioService usuarioService;

    /**
     * Instancia del servicio bajo prueba.
     */
    @InjectMocks
    private EventoAuditoriaServiceImpl auditoriaService;

    /**
     * Evento de prueba.
     */
    private EventoAuditoria evento;

    /**
     * Configuración de paginación para pruebas.
     */
    private Pageable pageable;

    /**
     * Constante de tipo de evento registrado.
     */
    private static final String USUARIO_REGISTRADO = "USUARIO_REGISTRADO";

    /**
     * Configura los datos iniciales antes de cada prueba.
     */
    @BeforeEach
    void setUp() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);

        evento = new EventoAuditoria(
                USUARIO_REGISTRADO,
                "Descripcion del evento",
                LocalDateTime.now(),
                "corr-001",
                "Usuario",
                usuario);
        evento.setId(10L);

        pageable = PageRequest.of(0, 10);
    }

    /**
     * Pruebas para el método registrar.
     */
    @Nested
    @DisplayName("registrar")
    class Registrar {

        /**
         * Verifica que se asigne la referencia del usuario cuando el id no es null.
         */
        @Test
        void cuandoUsuarioIdNoEsNull_asignaReferenciaDeUsuario() {
            auditoriaService.registrar("TIPO_X", "desc", 5L, "corr-1", "Entidad");

            ArgumentCaptor<EventoAuditoria> captor = ArgumentCaptor.forClass(EventoAuditoria.class);
            verify(repository).save(captor.capture());

            EventoAuditoria guardado = captor.getValue();
            assertEquals("TIPO_X", guardado.getTipo());
            assertEquals("desc", guardado.getDescripcion());
            assertEquals("corr-1", guardado.getIdCorrelacion());
            assertEquals("Entidad", guardado.getEntidadCorrelacion());
            assertNotNull(guardado.getUsuario());
            assertEquals(5L, guardado.getUsuario().getId());
            assertNotNull(guardado.getFecha());
        }

        /**
         * Verifica que no se asigne usuario cuando el id es null.
         */
        @Test
        void cuandoUsuarioIdEsNull_noAsignaUsuario() {
            auditoriaService.registrar("TIPO_SISTEMA", "desc sistema", null, "corr-2", "Sistema");

            ArgumentCaptor<EventoAuditoria> captor = ArgumentCaptor.forClass(EventoAuditoria.class);
            verify(repository).save(captor.capture());

            EventoAuditoria guardado = captor.getValue();
            assertNull(guardado.getUsuario());
            assertEquals("TIPO_SISTEMA", guardado.getTipo());
        }
    }

    /**
     * Pruebas para el método obtenerTodos.
     */
    @Nested
    @DisplayName("obtenerTodos")
    class ObtenerTodos {

        /**
         * Verifica que se retorne una página de eventos.
         */
        @Test
        void retornaPageDeEventos() {
            Page<EventoAuditoria> page = new PageImpl<>(List.of(evento), pageable, 1);
            when(repository.findAll(pageable)).thenReturn(page);

            Page<EventoAuditoriaDTO> resultado = auditoriaService.obtenerTodos(pageable);

            assertEquals(1, resultado.getTotalElements());
            assertEquals(USUARIO_REGISTRADO, resultado.getContent().get(0).getTipo());
            assertEquals(1L, resultado.getContent().get(0).getUsuarioId());
        }

        /**
         * Verifica que el usuarioId sea null cuando el evento no tiene usuario.
         */
        @Test
        void cuandoEventoSinUsuario_mapeaUsuarioIdNull() {
            evento.setUsuario(null);
            Page<EventoAuditoria> page = new PageImpl<>(List.of(evento), pageable, 1);
            when(repository.findAll(pageable)).thenReturn(page);

            Page<EventoAuditoriaDTO> resultado = auditoriaService.obtenerTodos(pageable);

            assertNull(resultado.getContent().get(0).getUsuarioId());
        }
    }

    /**
     * Pruebas para el método buscarPorUsuario.
     */
    @Nested
    @DisplayName("buscarPorUsuario")
    class BuscarPorUsuario {

        /**
         * Verifica que se retornen eventos de un usuario.
         */
        @Test
        void retornaEventosDelUsuario() {
            Page<EventoAuditoria> page = new PageImpl<>(List.of(evento), pageable, 1);
            when(repository.findByUsuarioId(1L, pageable)).thenReturn(page);

            Page<EventoAuditoriaDTO> resultado = auditoriaService.buscarPorUsuario(1L, pageable);

            assertEquals(1, resultado.getTotalElements());
        }
    }

    /**
     * Pruebas para el método buscarPorTipo.
     */
    @Nested
    @DisplayName("buscarPorTipo")
    class BuscarPorTipo {

        /**
         * Verifica que se retornen eventos de un tipo.
         */
        @Test
        void retornaEventosDelTipo() {
            Page<EventoAuditoria> page = new PageImpl<>(List.of(evento), pageable, 1);
            when(repository.findByTipo(USUARIO_REGISTRADO, pageable)).thenReturn(page);

            Page<EventoAuditoriaDTO> resultado = auditoriaService.buscarPorTipo(USUARIO_REGISTRADO, pageable);

            assertEquals(1, resultado.getTotalElements());
            assertEquals(USUARIO_REGISTRADO, resultado.getContent().get(0).getTipo());
        }
    }

    /**
     * Pruebas para el método buscarPorCorrelacion.
     */
    @Nested
    @DisplayName("buscarPorCorrelacion")
    class BuscarPorCorrelacion {

        /**
         * Verifica que se retornen eventos por correlación.
         */
        @Test
        void retornaEventosDeCorrelacion() {
            Page<EventoAuditoria> page = new PageImpl<>(List.of(evento), pageable, 1);
            when(repository.findByIdCorrelacion("corr-001", pageable)).thenReturn(page);

            Page<EventoAuditoriaDTO> resultado = auditoriaService.buscarPorCorrelacion("corr-001", pageable);

            assertEquals(1, resultado.getTotalElements());
        }
    }

    /**
     * Pruebas para el método buscarPorEntidad.
     */
    @Nested
    @DisplayName("buscarPorEntidad")
    class BuscarPorEntidad {

        /**
         * Verifica que se retornen eventos de una entidad.
         */
        @Test
        void retornaEventosDeLaEntidad() {
            Page<EventoAuditoria> page = new PageImpl<>(List.of(evento), pageable, 1);
            when(repository.findByEntidadCorrelacion("Usuario", pageable)).thenReturn(page);

            Page<EventoAuditoriaDTO> resultado = auditoriaService.buscarPorEntidad("Usuario", pageable);

            assertEquals(1, resultado.getTotalElements());
        }
    }

    /**
     * Pruebas para el método buscarPorFecha.
     */
    @Nested
    @DisplayName("buscarPorFecha")
    class BuscarPorFecha {

        /**
         * Verifica que se retornen eventos dentro de un rango de fechas.
         */
        @Test
        void retornaEventosEnRangoDeFechas() {
            LocalDateTime inicio = LocalDateTime.of(2026, 1, 1, 0, 0);
            LocalDateTime fin = LocalDateTime.of(2026, 12, 31, 23, 59);

            Page<EventoAuditoria> page = new PageImpl<>(List.of(evento), pageable, 1);
            when(repository.findByFechaBetween(inicio, fin, pageable)).thenReturn(page);

            Page<EventoAuditoriaDTO> resultado = auditoriaService.buscarPorFecha(inicio, fin, pageable);

            assertEquals(1, resultado.getTotalElements());
        }
    }

    /**
     * Pruebas para el método buscarConFiltros.
     */
    @Nested
    @DisplayName("buscarConFiltros")
    class BuscarConFiltros {

        /**
         * Verifica el envío de fechas como String cuando todos los filtros tienen valor.
         */
        @Test
void cuandoTodosLosFiltrosTienenValor_pasaStringsDeFecha() {
    LocalDateTime inicio = LocalDateTime.of(2026, 1, 1, 0, 0);
    LocalDateTime fin = LocalDateTime.of(2026, 12, 31, 23, 59);

    Page<EventoAuditoria> page = new PageImpl<>(List.of(evento), pageable, 1);
    when(repository.buscarConFiltros(1L, USUARIO_REGISTRADO,
            inicio.toString(), fin.toString(), pageable))
            .thenReturn(page);

    Page<EventoAuditoriaDTO> resultado = auditoriaService.buscarConFiltros(
            1L, USUARIO_REGISTRADO, inicio, fin, pageable);

    assertEquals(1, resultado.getTotalElements());
}

        /**
         * Verifica el envío de null en fechas al repositorio.
         */
@Test
void cuandoFechasNull_pasaNullAlRepositorio() {
    Page<EventoAuditoria> page = new PageImpl<>(List.of(evento), pageable, 1);
    when(repository.buscarConFiltros(1L, USUARIO_REGISTRADO, null, null, pageable))
            .thenReturn(page);

    Page<EventoAuditoriaDTO> resultado = auditoriaService.buscarConFiltros(
            1L, USUARIO_REGISTRADO, null, null, pageable);

    assertEquals(1, resultado.getTotalElements());
}

        /**
         * Verifica el envío de todos los filtros en null.
         */
@Test
void cuandoFiltrosNull_pasaTodosNulls() {
    Page<EventoAuditoria> page = new PageImpl<>(List.of(evento), pageable, 1);
    when(repository.buscarConFiltros(null, null, null, null, pageable))
            .thenReturn(page);

    Page<EventoAuditoriaDTO> resultado = auditoriaService.buscarConFiltros(
            null, null, null, null, pageable);

    assertEquals(1, resultado.getTotalElements());
}
}}