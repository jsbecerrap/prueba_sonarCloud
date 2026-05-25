package co.edu.unbosque.mundial_2026.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import co.edu.unbosque.mundial_2026.dto.request.MetodoPagoRequestDTO;
import co.edu.unbosque.mundial_2026.dto.response.MetodoPagoResponseDTO;
import co.edu.unbosque.mundial_2026.entity.MetodoPago;
import co.edu.unbosque.mundial_2026.entity.Usuario;
import co.edu.unbosque.mundial_2026.exception.MetodoPagoInvalidoException;
import co.edu.unbosque.mundial_2026.exception.MetodoPagoNotFoundException;
import co.edu.unbosque.mundial_2026.repository.MetodoPagoRepository;

/**
 * Pruebas unitarias para MetodoPagoServiceImpl usando Mockito
 */
@ExtendWith(MockitoExtension.class)
class MetodoPagoServiceImplTest {

    /**
     * Mock del repositorio de métodos de pago
     */
    @Mock
    private MetodoPagoRepository metodoPagoRepository;

    /**
     * Mock del servicio de usuarios
     */
    @Mock
    private UsuarioService usuarioService;

    /**
     * Mock del servicio de auditoría
     */
    @Mock
    private EventoAuditoriaService auditoriaService;

    /**
     * Instancia del servicio bajo prueba con mocks inyectados
     */
    @InjectMocks
    private MetodoPagoServiceImpl metodoPagoService;

    /**
     * Correo de prueba reutilizado en los tests
     */
private static final String CORREO_TEST = "test@test.com";

    /**
     * Tipo de pago CARD de prueba
     */
private static final String CARD = "CARD";

    /**
     * Tipo de pago PSE de prueba
     */
private static final String PSE = "PSE";

    /**
     * Usuario base usado en los escenarios de prueba
     */
    private Usuario usuario;

    /**
     * Método de pago base usado en los escenarios de prueba
     */
    private MetodoPago metodoPago;

    /**
     * Inicializa los datos base antes de cada prueba
     */
    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setCorreoUsuario(CORREO_TEST);
        usuario.setNombre("Juan");
        usuario.setApellido("Perez");

        metodoPago = new MetodoPago();
        metodoPago.setId(100L);
        metodoPago.setUsuario(usuario);
        metodoPago.setTipo(CARD);
        metodoPago.setLabel("Visa 1234");
        metodoPago.setDetails("4242424242424242");
        metodoPago.setDefault(true);
        metodoPago.setCreatedAt("2026-01-01T10:00:00");
    }

    /**
     * Pruebas relacionadas con agregar métodos de pago
     */
    @Nested
    @DisplayName("agregar")
    class Agregar {

        @Test
        void cuandoEsPrimerMetodo_seMarcaComoDefault() {
            MetodoPagoRequestDTO dto = new MetodoPagoRequestDTO();
            dto.setType(CARD);
            dto.setLabel("Visa");
            dto.setDetails("1234");

            when(usuarioService.obtenerEntidadPorCorreo(CORREO_TEST)).thenReturn(usuario);
            when(metodoPagoRepository.findByUsuarioId(1L)).thenReturn(Collections.emptyList());
            when(metodoPagoRepository.save(any(MetodoPago.class))).thenAnswer(inv -> {
                MetodoPago m = inv.getArgument(0);
                m.setId(101L);
                return m;
            });

            MetodoPagoResponseDTO resultado = metodoPagoService.agregar(CORREO_TEST, dto);

            assertNotNull(resultado);
            assertEquals(CARD, resultado.getType());
            assertTrue(resultado.isDefault());
            verify(auditoriaService).registrar(eq("METODO_PAGO_AGREGADO"), anyString(), eq(1L), anyString(), eq("MetodoPago"));
        }

        @Test
        void cuandoNoEsPrimerMetodo_noEsDefault() {
            MetodoPagoRequestDTO dto = new MetodoPagoRequestDTO();
            dto.setType(PSE);
            dto.setLabel("Banco");
            dto.setDetails("ref-001");

            when(usuarioService.obtenerEntidadPorCorreo(CORREO_TEST)).thenReturn(usuario);
            when(metodoPagoRepository.findByUsuarioId(1L)).thenReturn(List.of(metodoPago));
            when(metodoPagoRepository.save(any(MetodoPago.class))).thenAnswer(inv -> {
                MetodoPago m = inv.getArgument(0);
                m.setId(102L);
                return m;
            });

            MetodoPagoResponseDTO resultado = metodoPagoService.agregar(CORREO_TEST, dto);

            assertFalse(resultado.isDefault());
        }

        @Test
        void cuandoTipoEsInvalido_lanzaMetodoPagoInvalidoException() {
            MetodoPagoRequestDTO dto = new MetodoPagoRequestDTO();
            dto.setType("CRYPTO");

            assertThrows(MetodoPagoInvalidoException.class,
                    () -> metodoPagoService.agregar(CORREO_TEST, dto));
            verify(metodoPagoRepository, never()).save(any());
        }

        @Test
        void cuandoTipoEsNull_lanzaMetodoPagoInvalidoException() {
            MetodoPagoRequestDTO dto = new MetodoPagoRequestDTO();
            dto.setType(null);

            assertThrows(MetodoPagoInvalidoException.class,
                    () -> metodoPagoService.agregar(CORREO_TEST, dto));
        }

        @Test
        void aceptaTodosLosTiposValidos() {
            for (String tipo : new String[] { CARD, PSE, "CASH", "TRANSFER" }) {
                MetodoPagoRequestDTO dto = new MetodoPagoRequestDTO();
                dto.setType(tipo);
                dto.setLabel("L");
                dto.setDetails("D");

                when(usuarioService.obtenerEntidadPorCorreo(CORREO_TEST)).thenReturn(usuario);
                when(metodoPagoRepository.findByUsuarioId(1L)).thenReturn(Collections.emptyList());
                when(metodoPagoRepository.save(any(MetodoPago.class))).thenAnswer(inv -> inv.getArgument(0));

                MetodoPagoResponseDTO resultado = metodoPagoService.agregar(CORREO_TEST, dto);

                assertEquals(tipo, resultado.getType());
            }
        }
    }

    /**
     * Pruebas relacionadas con eliminar métodos de pago
     */
    @Nested
    @DisplayName("eliminar")
    class Eliminar {

        @Test
        void cuandoEsDefaultYQuedanOtros_asignaNuevoDefault() {
            MetodoPago otro = new MetodoPago();
            otro.setId(101L);
            otro.setUsuario(usuario);
            otro.setTipo(PSE);
            otro.setDefault(false);

            when(usuarioService.obtenerEntidadPorCorreo(CORREO_TEST)).thenReturn(usuario);
            when(metodoPagoRepository.findById(100L)).thenReturn(Optional.of(metodoPago));
            when(metodoPagoRepository.findByUsuarioIdOrderByCreatedAtDesc(1L)).thenReturn(List.of(otro));

            metodoPagoService.eliminar(CORREO_TEST, 100L);

            assertTrue(otro.isDefault());
            verify(metodoPagoRepository).delete(metodoPago);
            verify(metodoPagoRepository).save(otro);
        }

        @Test
        void cuandoEsDefaultYNoQuedanOtros_noAsignaNuevoDefault() {
            when(usuarioService.obtenerEntidadPorCorreo(CORREO_TEST)).thenReturn(usuario);
            when(metodoPagoRepository.findById(100L)).thenReturn(Optional.of(metodoPago));
            when(metodoPagoRepository.findByUsuarioIdOrderByCreatedAtDesc(1L)).thenReturn(Collections.emptyList());

            metodoPagoService.eliminar(CORREO_TEST, 100L);

            verify(metodoPagoRepository).delete(metodoPago);
            verify(metodoPagoRepository, never()).save(any());
        }

        @Test
        void cuandoNoEsDefault_soloElimina() {
            metodoPago.setDefault(false);

            when(usuarioService.obtenerEntidadPorCorreo(CORREO_TEST)).thenReturn(usuario);
            when(metodoPagoRepository.findById(100L)).thenReturn(Optional.of(metodoPago));

            metodoPagoService.eliminar(CORREO_TEST, 100L);

            verify(metodoPagoRepository).delete(metodoPago);
            verify(metodoPagoRepository, never()).findByUsuarioIdOrderByCreatedAtDesc(any());
        }

        @Test
        void cuandoNoExiste_lanzaMetodoPagoNotFoundException() {
            when(usuarioService.obtenerEntidadPorCorreo(CORREO_TEST)).thenReturn(usuario);
            when(metodoPagoRepository.findById(999L)).thenReturn(Optional.empty());

            assertThrows(MetodoPagoNotFoundException.class,
                    () -> metodoPagoService.eliminar(CORREO_TEST, 999L));
        }

        @Test
        void cuandoMetodoNoPerteneceAlUsuario_lanzaMetodoPagoNotFoundException() {
            Usuario otroUsuario = new Usuario();
            otroUsuario.setId(99L);
            metodoPago.setUsuario(otroUsuario);

            when(usuarioService.obtenerEntidadPorCorreo(CORREO_TEST)).thenReturn(usuario);
            when(metodoPagoRepository.findById(100L)).thenReturn(Optional.of(metodoPago));

            assertThrows(MetodoPagoNotFoundException.class,
                    () -> metodoPagoService.eliminar(CORREO_TEST, 100L));
        }
    }

    /**
     * Pruebas relacionadas con listar métodos de pago por correo
     */
    @Nested
    @DisplayName("listarPorCorreo")
    class ListarPorCorreo {

        @Test
        void retornaListaDeMetodos() {
            when(usuarioService.obtenerEntidadPorCorreo(CORREO_TEST)).thenReturn(usuario);
            when(metodoPagoRepository.findByUsuarioIdOrderByIsDefaultDescCreatedAtDesc(1L))
                    .thenReturn(List.of(metodoPago));

            List<MetodoPagoResponseDTO> resultado = metodoPagoService.listarPorCorreo(CORREO_TEST);

            assertEquals(1, resultado.size());
            assertEquals(CARD, resultado.get(0).getType());
        }

        @Test
        void cuandoNoHay_retornaListaVacia() {
            when(usuarioService.obtenerEntidadPorCorreo(CORREO_TEST)).thenReturn(usuario);
            when(metodoPagoRepository.findByUsuarioIdOrderByIsDefaultDescCreatedAtDesc(1L))
                    .thenReturn(Collections.emptyList());

            List<MetodoPagoResponseDTO> resultado = metodoPagoService.listarPorCorreo(CORREO_TEST);

            assertTrue(resultado.isEmpty());
        }
    }

    /**
     * Pruebas relacionadas con cambiar el método por defecto
     */
    @Nested
    @DisplayName("setDefaultPorCorreo")
    class SetDefaultPorCorreo {

        @Test
        void cambiaElDefaultCorrectamente() {
            MetodoPago otro = new MetodoPago();
            otro.setId(101L);
            otro.setUsuario(usuario);
            otro.setDefault(false);

            List<MetodoPago> todos = new ArrayList<>(List.of(metodoPago, otro));
            when(usuarioService.obtenerEntidadPorCorreo(CORREO_TEST)).thenReturn(usuario);
            when(metodoPagoRepository.findByUsuarioId(1L)).thenReturn(todos);

            metodoPagoService.setDefaultPorCorreo(CORREO_TEST, 101L);

            assertFalse(metodoPago.isDefault());
            assertTrue(otro.isDefault());
            verify(metodoPagoRepository).saveAll(todos);
        }

        @Test
        void cuandoIdNoEstaEnLaLista_lanzaMetodoPagoNotFoundException() {
            when(usuarioService.obtenerEntidadPorCorreo(CORREO_TEST)).thenReturn(usuario);
            when(metodoPagoRepository.findByUsuarioId(1L)).thenReturn(List.of(metodoPago));

            assertThrows(MetodoPagoNotFoundException.class,
                    () -> metodoPagoService.setDefaultPorCorreo(CORREO_TEST, 999L));
        }
    }

    /**
     * Pruebas relacionadas con actualizar métodos de pago
     */
    @Nested
    @DisplayName("actualizar")
    class Actualizar {

        @Test
        void actualizaTodosLosCampos() {
            MetodoPagoRequestDTO dto = new MetodoPagoRequestDTO();
            dto.setType(PSE);
            dto.setLabel("Nuevo Banco");
            dto.setDetails("ref-nueva");

            when(usuarioService.obtenerEntidadPorCorreo(CORREO_TEST)).thenReturn(usuario);
            when(metodoPagoRepository.findById(100L)).thenReturn(Optional.of(metodoPago));
            when(metodoPagoRepository.save(any(MetodoPago.class))).thenReturn(metodoPago);

            MetodoPagoResponseDTO resultado = metodoPagoService.actualizar(CORREO_TEST, 100L, dto);

            assertEquals(PSE, metodoPago.getTipo());
            assertEquals("Nuevo Banco", metodoPago.getLabel());
            assertEquals("ref-nueva", metodoPago.getDetails());
            assertNotNull(resultado);
        }

        @Test
        void cuandoCamposNull_noActualiza() {
            MetodoPagoRequestDTO dto = new MetodoPagoRequestDTO();

            when(usuarioService.obtenerEntidadPorCorreo(CORREO_TEST)).thenReturn(usuario);
            when(metodoPagoRepository.findById(100L)).thenReturn(Optional.of(metodoPago));
            when(metodoPagoRepository.save(any(MetodoPago.class))).thenReturn(metodoPago);

            metodoPagoService.actualizar(CORREO_TEST, 100L, dto);

            assertEquals(CARD, metodoPago.getTipo());
            assertEquals("Visa 1234", metodoPago.getLabel());
        }

        @Test
        void cuandoCamposBlancos_noActualiza() {
            MetodoPagoRequestDTO dto = new MetodoPagoRequestDTO();
            dto.setType("   ");
            dto.setLabel("");
            dto.setDetails("   ");

            when(usuarioService.obtenerEntidadPorCorreo(CORREO_TEST)).thenReturn(usuario);
            when(metodoPagoRepository.findById(100L)).thenReturn(Optional.of(metodoPago));
            when(metodoPagoRepository.save(any(MetodoPago.class))).thenReturn(metodoPago);

            metodoPagoService.actualizar(CORREO_TEST, 100L, dto);

            assertEquals(CARD, metodoPago.getTipo());
        }

        @Test
        void cuandoTipoInvalido_lanzaMetodoPagoInvalidoException() {
            MetodoPagoRequestDTO dto = new MetodoPagoRequestDTO();
            dto.setType("CRYPTO");

            when(usuarioService.obtenerEntidadPorCorreo(CORREO_TEST)).thenReturn(usuario);
            when(metodoPagoRepository.findById(100L)).thenReturn(Optional.of(metodoPago));

            assertThrows(MetodoPagoInvalidoException.class,
                    () -> metodoPagoService.actualizar(CORREO_TEST, 100L, dto));
        }

        @Test
        void cuandoNoExiste_lanzaMetodoPagoNotFoundException() {
            MetodoPagoRequestDTO dto = new MetodoPagoRequestDTO();

            when(usuarioService.obtenerEntidadPorCorreo(CORREO_TEST)).thenReturn(usuario);
            when(metodoPagoRepository.findById(999L)).thenReturn(Optional.empty());

            assertThrows(MetodoPagoNotFoundException.class,
                    () -> metodoPagoService.actualizar(CORREO_TEST, 999L, dto));
        }

        @Test
        void cuandoMetodoNoPerteneceAlUsuario_lanzaMetodoPagoNotFoundException() {
            MetodoPagoRequestDTO dto = new MetodoPagoRequestDTO();

            Usuario otroUsuario = new Usuario();
            otroUsuario.setId(99L);
            metodoPago.setUsuario(otroUsuario);

            when(usuarioService.obtenerEntidadPorCorreo(CORREO_TEST)).thenReturn(usuario);
            when(metodoPagoRepository.findById(100L)).thenReturn(Optional.of(metodoPago));

            assertThrows(MetodoPagoNotFoundException.class,
                    () -> metodoPagoService.actualizar(CORREO_TEST, 100L, dto));
        }
    }

    /**
     * Pruebas relacionadas con obtener una entidad por su id
     */
    @Nested
    @DisplayName("obtenerEntidadPorId")
    class ObtenerEntidadPorId {

        @Test
        void cuandoExiste_retornaMetodoPago() {
            when(metodoPagoRepository.findById(100L)).thenReturn(Optional.of(metodoPago));

            MetodoPago resultado = metodoPagoService.obtenerEntidadPorId(100L);

            assertEquals(100L, resultado.getId());
        }

        @Test
        void cuandoNoExiste_lanzaMetodoPagoNotFoundException() {
            when(metodoPagoRepository.findById(999L)).thenReturn(Optional.empty());

            assertThrows(MetodoPagoNotFoundException.class,
                    () -> metodoPagoService.obtenerEntidadPorId(999L));
        }
    }
}