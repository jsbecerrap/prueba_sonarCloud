package co.edu.unbosque.mundial_2026.service;

/**
 * Pruebas unitarias del servicio de categoría
 * Valida creación actualización desactivación reactivación y consultas de categorías
 */
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

import co.edu.unbosque.mundial_2026.dto.request.CategoriaRequestDTO;
import co.edu.unbosque.mundial_2026.dto.response.CategoriaResponseDTO;
import co.edu.unbosque.mundial_2026.dto.response.DesactivarCategoriaResponseDTO;
import co.edu.unbosque.mundial_2026.dto.response.ProductoResponseDTO;
import co.edu.unbosque.mundial_2026.dto.response.ReactivarCategoriaResponseDTO;
import co.edu.unbosque.mundial_2026.entity.Categoria;
import co.edu.unbosque.mundial_2026.entity.Producto;
import co.edu.unbosque.mundial_2026.exception.CategoriaNotFoundException;
import co.edu.unbosque.mundial_2026.exception.CategoriaYaExisteException;
import co.edu.unbosque.mundial_2026.repository.CategoriaRepository;
import co.edu.unbosque.mundial_2026.repository.ProductoRepository;

@ExtendWith(MockitoExtension.class)
/**
 * Clase de pruebas para CategoriaServiceImpl
 * Usa Mockito para simular repositorios y servicios externos
 */
class CategoriaServiceImplTest {

    @Mock
    /**
     * Repositorio de categorías simulado para pruebas
     */
    private CategoriaRepository categoriaRepository;

    @Mock
    /**
     * Repositorio de productos simulado para pruebas
     */
    private ProductoRepository productoRepository;

    @Mock
    /**
     * Servicio de auditoría simulado
     */
    private EventoAuditoriaService auditoriaService;

    @InjectMocks
    /**
     * Servicio de categoría bajo prueba
     */
    private CategoriaServiceImpl categoriaService;

    /**
     * Entidad de categoría usada en pruebas
     */
    private Categoria categoria;

    /**
     * Nombre constante de categoría usada en pruebas
     */
private static final String CAMISETAS = "Camisetas";

    /**
     * Inicialización antes de cada prueba
     */
    @BeforeEach
    void setUp() {
        categoria = new Categoria();
        categoria.setId(1L);
        categoria.setNombre(CAMISETAS);
        categoria.setDescripcion("Ropa deportiva");
        categoria.setActivo(true);
    }

    @Nested
    @DisplayName("crear")
    /**
     * Pruebas de creación de categorías
     */
    class Crear {

        @Test
        /**
         * Verifica creación cuando el nombre no existe
         */
        void cuandoNombreNoExiste_creaCategoria() {
            CategoriaRequestDTO dto = new CategoriaRequestDTO();
            dto.setNombre("Nueva");
            dto.setDescripcion("Una nueva categoria");

            when(categoriaRepository.findByNombre("Nueva")).thenReturn(Optional.empty());
            when(categoriaRepository.save(any(Categoria.class))).thenAnswer(inv -> {
                Categoria c = inv.getArgument(0);
                c.setId(2L);
                return c;
            });

            CategoriaResponseDTO resultado = categoriaService.crear(dto);

            assertNotNull(resultado);
            assertEquals("Nueva", resultado.getNombre());
            verify(categoriaRepository).save(any(Categoria.class));
            verify(auditoriaService).registrar(eq("CATEGORIA_CREADA"), anyString(), eq(null), anyString(), eq("Categoria"));
        }

        @Test
        /**
         * Verifica error cuando la categoría ya existe
         */
        void cuandoNombreYaExiste_lanzaCategoriaYaExisteException() {
            CategoriaRequestDTO dto = new CategoriaRequestDTO();
            dto.setNombre(CAMISETAS);

            when(categoriaRepository.findByNombre(CAMISETAS)).thenReturn(Optional.of(categoria));

            assertThrows(CategoriaYaExisteException.class, () -> categoriaService.crear(dto));
            verify(categoriaRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("actualizar")
    /**
     * Pruebas de actualización de categorías
     */
    class Actualizar {

        @Test
        /**
         * Verifica actualización de nombre y descripción
         */
        void cuandoActualizaNombreYDescripcion_funciona() {
            CategoriaRequestDTO dto = new CategoriaRequestDTO();
            dto.setNombre("NuevoNombre");
            dto.setDescripcion("Nueva desc");

            when(categoriaRepository.findByIdAndActivoTrue(1L)).thenReturn(Optional.of(categoria));
            when(categoriaRepository.findByNombre("NuevoNombre")).thenReturn(Optional.empty());
            when(categoriaRepository.save(any(Categoria.class))).thenReturn(categoria);

            CategoriaResponseDTO resultado = categoriaService.actualizar(1L, dto);

            assertEquals("NuevoNombre", categoria.getNombre());
            assertEquals("Nueva desc", categoria.getDescripcion());
            assertNotNull(resultado);
        }

        @Test
        /**
         * Verifica error por nombre duplicado en otra categoría
         */
        void cuandoNombreDuplicadoEnOtraCategoria_lanzaCategoriaYaExiste() {
            CategoriaRequestDTO dto = new CategoriaRequestDTO();
            dto.setNombre("Existente");

            Categoria otra = new Categoria();
            otra.setId(99L);
            otra.setNombre("Existente");

            when(categoriaRepository.findByIdAndActivoTrue(1L)).thenReturn(Optional.of(categoria));
            when(categoriaRepository.findByNombre("Existente")).thenReturn(Optional.of(otra));

            assertThrows(CategoriaYaExisteException.class, () -> categoriaService.actualizar(1L, dto));
        }

        @Test
        /**
         * Verifica que no falle si el nombre es el mismo
         */
        void cuandoNombreDuplicadoEnMismaCategoria_noLanza() {
            CategoriaRequestDTO dto = new CategoriaRequestDTO();
            dto.setNombre(CAMISETAS);

            when(categoriaRepository.findByIdAndActivoTrue(1L)).thenReturn(Optional.of(categoria));
            when(categoriaRepository.findByNombre(CAMISETAS)).thenReturn(Optional.of(categoria));
            when(categoriaRepository.save(any(Categoria.class))).thenReturn(categoria);

            CategoriaResponseDTO resultado = categoriaService.actualizar(1L, dto);

            assertNotNull(resultado);
        }

        @Test
        /**
         * Verifica que no actualiza campos vacíos o nulos
         */
        void cuandoCamposNullOBlanco_noActualiza() {
            CategoriaRequestDTO dto = new CategoriaRequestDTO();
            dto.setNombre(null);
            dto.setDescripcion("   ");

            when(categoriaRepository.findByIdAndActivoTrue(1L)).thenReturn(Optional.of(categoria));
            when(categoriaRepository.save(any(Categoria.class))).thenReturn(categoria);

            categoriaService.actualizar(1L, dto);

            assertEquals(CAMISETAS, categoria.getNombre());
            assertEquals("Ropa deportiva", categoria.getDescripcion());
        }

        @Test
        /**
         * Verifica error cuando la categoría no existe
         */
        void cuandoNoExiste_lanzaCategoriaNotFoundException() {
            CategoriaRequestDTO dto = new CategoriaRequestDTO();

            when(categoriaRepository.findByIdAndActivoTrue(99L)).thenReturn(Optional.empty());

            assertThrows(CategoriaNotFoundException.class, () -> categoriaService.actualizar(99L, dto));
        }
    }

    @Nested
    @DisplayName("desactivar")
    /**
     * Pruebas de desactivación de categorías
     */
    class Desactivar {

        @Test
        /**
         * Verifica desactivación de categoría con productos
         */
        void cuandoExiste_desactivaCategoriaYProductos() {
            Producto p1 = new Producto();
            p1.setId(10L);
            p1.setNombre("Camiseta Brasil");
            p1.setActivo(true);
            p1.setCategoria(categoria);

            Producto p2 = new Producto();
            p2.setId(11L);
            p2.setNombre("Camiseta Argentina");
            p2.setActivo(true);
            p2.setCategoria(categoria);

            when(categoriaRepository.findByIdAndActivoTrue(1L)).thenReturn(Optional.of(categoria));
            when(productoRepository.findByCategoriaId(1L)).thenReturn(List.of(p1, p2));

            DesactivarCategoriaResponseDTO resultado = categoriaService.desactivar(1L);

            assertFalse(categoria.getActivo());
            assertFalse(p1.getActivo());
            assertFalse(p2.getActivo());
            assertEquals(2, resultado.getProductosAfectados().size());
            verify(productoRepository, times(2)).save(any(Producto.class));
        }

        @Test
        /**
         * Verifica desactivación sin productos
         */
        void cuandoNoTieneProductos_desactivaSoloCategoria() {
            when(categoriaRepository.findByIdAndActivoTrue(1L)).thenReturn(Optional.of(categoria));
            when(productoRepository.findByCategoriaId(1L)).thenReturn(Collections.emptyList());

            DesactivarCategoriaResponseDTO resultado = categoriaService.desactivar(1L);

            assertFalse(categoria.getActivo());
            assertTrue(resultado.getProductosAfectados().isEmpty());
            verify(productoRepository, never()).save(any());
        }

        @Test
        /**
         * Verifica error cuando no existe categoría
         */
        void cuandoNoExiste_lanzaCategoriaNotFoundException() {
            when(categoriaRepository.findByIdAndActivoTrue(99L)).thenReturn(Optional.empty());

            assertThrows(CategoriaNotFoundException.class, () -> categoriaService.desactivar(99L));
        }
    }

    @Nested
    @DisplayName("reactivar")
    /**
     * Pruebas de reactivación de categorías
     */
    class Reactivar {

        @Test
        /**
         * Verifica reactivación de categoría
         */
        void cuandoExiste_reactivaCategoria() {
            categoria.setActivo(false);
            Producto p = new Producto();
            p.setId(10L);
            p.setNombre("Camiseta");
            p.setCategoria(categoria);

            when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));
            when(productoRepository.findByCategoriaId(1L)).thenReturn(List.of(p));

            ReactivarCategoriaResponseDTO resultado = categoriaService.reactivar(1L);

            assertTrue(categoria.getActivo());
            assertEquals(1, resultado.getProductos().size());
        }

        @Test
        /**
         * Verifica error cuando no existe categoría
         */
        void cuandoNoExiste_lanzaCategoriaNotFoundException() {
            when(categoriaRepository.findById(99L)).thenReturn(Optional.empty());

            assertThrows(CategoriaNotFoundException.class, () -> categoriaService.reactivar(99L));
        }
    }

    @Nested
    @DisplayName("listar y listarTodas")
    /**
     * Pruebas de listado de categorías
     */
    class Listar {

        @Test
        /**
         * Verifica listado de categorías activas
         */
        void listar_retornaSoloActivas() {
            when(categoriaRepository.findByActivoTrue()).thenReturn(List.of(categoria));

            List<CategoriaResponseDTO> resultado = categoriaService.listar();

            assertEquals(1, resultado.size());
            assertEquals(CAMISETAS, resultado.get(0).getNombre());
        }

        @Test
        /**
         * Verifica listado vacío
         */
        void listar_cuandoNoHay_retornaListaVacia() {
            when(categoriaRepository.findByActivoTrue()).thenReturn(Collections.emptyList());

            List<CategoriaResponseDTO> resultado = categoriaService.listar();

            assertTrue(resultado.isEmpty());
        }

        @Test
        /**
         * Verifica listado total
         */
        void listarTodas_retornaTodas() {
            when(categoriaRepository.findAll()).thenReturn(List.of(categoria));

            List<CategoriaResponseDTO> resultado = categoriaService.listarTodas();

            assertEquals(1, resultado.size());
        }
    }

    @Nested
    @DisplayName("obtenerEntidadPorId")
    /**
     * Pruebas de obtención de entidad
     */
    class ObtenerEntidadPorId {

        @Test
        /**
         * Verifica obtención correcta
         */
        void cuandoExiste_retornaCategoria() {
            when(categoriaRepository.findByIdAndActivoTrue(1L)).thenReturn(Optional.of(categoria));

            Categoria resultado = categoriaService.obtenerEntidadPorId(1L);

            assertEquals(1L, resultado.getId());
            assertEquals(CAMISETAS, resultado.getNombre());
        }

        @Test
        /**
         * Verifica error cuando no existe
         */
        void cuandoNoExiste_lanzaCategoriaNotFoundException() {
            when(categoriaRepository.findByIdAndActivoTrue(99L)).thenReturn(Optional.empty());

            assertThrows(CategoriaNotFoundException.class, () -> categoriaService.obtenerEntidadPorId(99L));
        }
    }

    @Nested
    @DisplayName("obtenerProductosPorCategoria")
    /**
     * Pruebas de obtención de productos por categoría
     */
    class ObtenerProductosPorCategoria {

        @Test
        /**
         * Verifica productos por categoría
         */
        void retornaProductosDeLaCategoria() {
            Producto p = new Producto();
            p.setId(10L);
            p.setNombre("Camiseta Brasil");
            p.setCategoria(categoria);

            when(productoRepository.findByCategoriaId(1L)).thenReturn(List.of(p));

            List<ProductoResponseDTO> resultado = categoriaService.obtenerProductosPorCategoria(1L);

            assertEquals(1, resultado.size());
            assertEquals("Camiseta Brasil", resultado.get(0).getNombre());
        }

        @Test
        /**
         * Verifica lista vacía
         */
        void cuandoNoHayProductos_retornaListaVacia() {
            when(productoRepository.findByCategoriaId(1L)).thenReturn(Collections.emptyList());

            List<ProductoResponseDTO> resultado = categoriaService.obtenerProductosPorCategoria(1L);

            assertTrue(resultado.isEmpty());
        }
    }
}