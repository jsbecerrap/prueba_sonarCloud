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
import static org.mockito.Mockito.times;
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

import co.edu.unbosque.mundial_2026.dto.request.ProductoActualizarRequestDTO;
import co.edu.unbosque.mundial_2026.dto.request.ProductoRequestDTO;
import co.edu.unbosque.mundial_2026.dto.response.ProductoListadoDTO;
import co.edu.unbosque.mundial_2026.dto.response.ProductoResponseDTO;
import co.edu.unbosque.mundial_2026.entity.Categoria;
import co.edu.unbosque.mundial_2026.entity.Producto;
import co.edu.unbosque.mundial_2026.entity.VarianteProducto;
import co.edu.unbosque.mundial_2026.exception.ProductoNotFoundException;
import co.edu.unbosque.mundial_2026.repository.ProductoRepository;
import co.edu.unbosque.mundial_2026.repository.VarianteProductoRepository;

/**
 * Pruebas unitarias para ProductoServiceImpl
 */
@ExtendWith(MockitoExtension.class)
class ProductoServiceImplTest {

    /**
     * Mock del repositorio de productos
     */
    @Mock
    private ProductoRepository productoRepository;

    /**
     * Mock del repositorio de variantes
     */
    @Mock
    private VarianteProductoRepository varianteRepository;

    /**
     * Mock del servicio de categorías
     */
    @Mock
    private CategoriaService categoriaService;

    /**
     * Mock del servicio de auditoría
     */
    @Mock
    private EventoAuditoriaService auditoriaService;

    /**
     * Servicio bajo prueba
     */
    @InjectMocks
    private ProductoServiceImpl productoService;

    /**
     * Constante de imagen de prueba
     */
private static final String IMG_JPG = "img.jpg";

    /**
     * Categoría de prueba
     */
    private Categoria categoria;

    /**
     * Producto de prueba
     */
    private Producto producto;

    /**
     * Variante de prueba
     */
    private VarianteProducto variante;
    

    /**
     * Configura los datos iniciales antes de cada prueba
     */
    @BeforeEach
    void setUp() {
        categoria = new Categoria();
        categoria.setId(1L);
        categoria.setNombre("Camisetas");

        variante = new VarianteProducto();
        variante.setId(100L);
        variante.setEspecificacion("M");
        variante.setStock(10);

        producto = new Producto();
        producto.setId(10L);
        producto.setNombre("Camiseta Brasil");
        producto.setDescripcion("Camiseta oficial");
        producto.setPrecio(50.0);
        producto.setImagenUrl(IMG_JPG );
        producto.setActivo(true);
        producto.setCategoria(categoria);
        producto.setCodigoProducto("BRA-2026");
        producto.setEquipo("Brasil");
        producto.setBandera("br");
        producto.setDestacado(false);
        List<VarianteProducto> variantes = new ArrayList<>();
        variantes.add(variante);
        producto.setVariantes(variantes);
    }

    /**
     * Pruebas para reactivar
     */
    @Nested
    @DisplayName("reactivar")
    class Reactivar {

        /**
         * Verifica que se reactive un producto existente
         */
        @Test
        void cuandoExiste_reactivaProducto() {
            producto.setActivo(false);
            when(productoRepository.findById(10L)).thenReturn(Optional.of(producto));

            productoService.reactivar(10L);

            assertTrue(producto.getActivo());
            verify(productoRepository).save(producto);
        }

        /**
         * Verifica excepción cuando el producto no existe
         */
        @Test
        void cuandoNoExiste_lanzaProductoNotFoundException() {
            when(productoRepository.findById(99L)).thenReturn(Optional.empty());

            assertThrows(ProductoNotFoundException.class, () -> productoService.reactivar(99L));
            verify(productoRepository, never()).save(any());
        }
    }

    /**
     * Pruebas para crear
     */
    @Nested
    @DisplayName("crear")
    class Crear {

        /**
         * Verifica creación con variantes
         */
        @Test
        void cuandoTieneVariantes_creaProductoYVariantes() {
            ProductoRequestDTO dto = new ProductoRequestDTO();
            dto.setNombre("Nuevo");
            dto.setDescripcion("Desc");
            dto.setPrecio(80.0);
            dto.setImagenUrl(IMG_JPG );
            dto.setCategoriaId(1L);
            dto.setCodigoProducto("COD1");
            dto.setEquipo("Argentina");
            dto.setBandera("ar");
            dto.setDestacado(true);

            ProductoRequestDTO.VarianteRequestDTO v1 = new ProductoRequestDTO.VarianteRequestDTO();
            v1.setEspecificacion("S");
            v1.setStock(5);

            ProductoRequestDTO.VarianteRequestDTO v2 = new ProductoRequestDTO.VarianteRequestDTO();
            v2.setEspecificacion("M");
            v2.setStock(10);

            dto.setVariantes(List.of(v1, v2));

            when(categoriaService.obtenerEntidadPorId(1L)).thenReturn(categoria);
            when(productoRepository.save(any(Producto.class))).thenAnswer(inv -> {
                Producto p = inv.getArgument(0);
                p.setId(20L);
                return p;
            });

            ProductoResponseDTO resultado = productoService.crear(dto);

            assertNotNull(resultado);
            assertEquals("Nuevo", resultado.getNombre());
            verify(varianteRepository, times(2)).save(any(VarianteProducto.class));
            verify(auditoriaService).registrar(eq("PRODUCTO_CREADO"), anyString(), eq(null), anyString(), eq("Producto"));
        }

        /**
         * Verifica creación sin variantes
         */
        @Test
        void cuandoSinVariantes_creaProductoSolo() {
            ProductoRequestDTO dto = new ProductoRequestDTO();
            dto.setNombre("SinVar");
            dto.setPrecio(20.0);
            dto.setCategoriaId(1L);
            dto.setVariantes(null);

            when(categoriaService.obtenerEntidadPorId(1L)).thenReturn(categoria);
            when(productoRepository.save(any(Producto.class))).thenAnswer(inv -> inv.getArgument(0));

            ProductoResponseDTO resultado = productoService.crear(dto);

            assertNotNull(resultado);
            verify(varianteRepository, never()).save(any());
        }
    }

    /**
     * Pruebas para actualizar
     */
    @Nested
    @DisplayName("actualizar")
    class Actualizar {

        /**
         * Verifica actualización de todos los campos
         */
        @Test
        void actualizaTodosLosCampos() {
            ProductoActualizarRequestDTO dto = new ProductoActualizarRequestDTO();
            dto.setPrecio(99.99);
            dto.setImagenUrl("nueva.jpg");
            dto.setDescripcion("Nueva desc");
            dto.setCodigoProducto("NEW-COD");
            dto.setEquipo("Colombia");
            dto.setBandera("co");
            dto.setDestacado(true);

            when(productoRepository.findById(10L)).thenReturn(Optional.of(producto));
            when(productoRepository.save(any(Producto.class))).thenReturn(producto);

            ProductoResponseDTO resultado = productoService.actualizar(10L, dto);

            assertEquals(99.99, producto.getPrecio());
            assertEquals("nueva.jpg", producto.getImagenUrl());
            assertEquals("Nueva desc", producto.getDescripcion());
            assertEquals("NEW-COD", producto.getCodigoProducto());
            assertEquals("Colombia", producto.getEquipo());
            assertEquals("co", producto.getBandera());
            assertTrue(producto.getDestacado());
            assertNotNull(resultado);
        }

        /**
         * Verifica que campos null no actualicen
         */
        @Test
        void cuandoCamposNull_noActualiza() {
            ProductoActualizarRequestDTO dto = new ProductoActualizarRequestDTO();

            when(productoRepository.findById(10L)).thenReturn(Optional.of(producto));
            when(productoRepository.save(any(Producto.class))).thenReturn(producto);

            productoService.actualizar(10L, dto);

            assertEquals(50.0, producto.getPrecio());
            assertEquals(IMG_JPG , producto.getImagenUrl());
        }

        /**
         * Verifica que campos en blanco no actualicen
         */
        @Test
        void cuandoCamposBlancos_noActualiza() {
            ProductoActualizarRequestDTO dto = new ProductoActualizarRequestDTO();
            dto.setImagenUrl("   ");
            dto.setDescripcion("");
            dto.setCodigoProducto("");
            dto.setEquipo("   ");
            dto.setBandera("");

            when(productoRepository.findById(10L)).thenReturn(Optional.of(producto));
            when(productoRepository.save(any(Producto.class))).thenReturn(producto);

            productoService.actualizar(10L, dto);

            assertEquals(IMG_JPG , producto.getImagenUrl());
            assertEquals("Camiseta oficial", producto.getDescripcion());
            assertEquals("BRA-2026", producto.getCodigoProducto());
        }

        /**
         * Verifica excepción cuando no existe
         */
        @Test
        void cuandoNoExiste_lanzaProductoNotFoundException() {
            ProductoActualizarRequestDTO dto = new ProductoActualizarRequestDTO();
            when(productoRepository.findById(99L)).thenReturn(Optional.empty());

            assertThrows(ProductoNotFoundException.class, () -> productoService.actualizar(99L, dto));
        }
    }

    /**
     * Pruebas para listarTodos
     */
    @Nested
    @DisplayName("listarTodos")
    class ListarTodos {

        /**
         * Verifica listado de activos con variantes
         */
        @Test
        void retornaSoloActivosConVariantes() {
            when(productoRepository.findByActivoTrueWithVariantes()).thenReturn(List.of(producto));

            List<ProductoResponseDTO> resultado = productoService.listarTodos();

            assertEquals(1, resultado.size());
            assertEquals("Camiseta Brasil", resultado.get(0).getNombre());
            assertEquals(10, resultado.get(0).getStockTotal());
        }

        /**
         * Verifica listado con solo activos
         */
        @Test
        void conSoloActivosTrue_usaActivos() {
            when(productoRepository.findByActivoTrueWithVariantes()).thenReturn(List.of(producto));

            List<ProductoResponseDTO> resultado = productoService.listarTodos(true);

            assertEquals(1, resultado.size());
        }

        /**
         * Verifica listado de todos
         */
        @Test
        void conSoloActivosFalse_usaTodos() {
            when(productoRepository.findAllWithVariantes()).thenReturn(List.of(producto));

            List<ProductoResponseDTO> resultado = productoService.listarTodos(false);

            assertEquals(1, resultado.size());
        }

        /**
         * Verifica lista vacía cuando no hay productos
         */
        @Test
        void cuandoNoHay_retornaListaVacia() {
            when(productoRepository.findByActivoTrueWithVariantes()).thenReturn(Collections.emptyList());

            List<ProductoResponseDTO> resultado = productoService.listarTodos();

            assertTrue(resultado.isEmpty());
        }
    }

    /**
     * Pruebas para listarPorCategoria
     */
    @Nested
    @DisplayName("listarPorCategoria")
    class ListarPorCategoria {

        /**
         * Verifica listado por categoría
         */
        @Test
        void retornaProductosDeLaCategoria() {
            when(categoriaService.obtenerEntidadPorId(1L)).thenReturn(categoria);
            when(productoRepository.findByCategoriaIdAndActivoTrue(1L)).thenReturn(List.of(producto));

            List<ProductoResponseDTO> resultado = productoService.listarPorCategoria(1L);

            assertEquals(1, resultado.size());
        }
    }

    /**
     * Pruebas para obtenerPorId
     */
    @Nested
    @DisplayName("obtenerPorId")
    class ObtenerPorId {

        /**
         * Verifica retorno cuando existe y está activo
         */
        @Test
        void cuandoExisteYActivo_retornaDTO() {
            when(productoRepository.findById(10L)).thenReturn(Optional.of(producto));

            ProductoResponseDTO resultado = productoService.obtenerPorId(10L);

            assertEquals(10L, resultado.getId());
        }

        /**
         * Verifica excepción cuando no existe
         */
        @Test
        void cuandoNoExiste_lanzaProductoNotFoundException() {
            when(productoRepository.findById(99L)).thenReturn(Optional.empty());

            assertThrows(ProductoNotFoundException.class, () -> productoService.obtenerPorId(99L));
        }

        /**
         * Verifica excepción cuando está inactivo
         */
        @Test
        void cuandoExistePeroInactivo_lanzaProductoNotFoundException() {
            producto.setActivo(false);
            when(productoRepository.findById(10L)).thenReturn(Optional.of(producto));

            assertThrows(ProductoNotFoundException.class, () -> productoService.obtenerPorId(10L));
        }
    }

    /**
     * Pruebas para obtenerEntidadPorId
     */
    @Nested
    @DisplayName("obtenerEntidadPorId")
    class ObtenerEntidadPorId {

        /**
         * Verifica retorno del producto
         */
        @Test
        void cuandoExiste_retornaProducto() {
            when(productoRepository.findById(10L)).thenReturn(Optional.of(producto));

            Producto resultado = productoService.obtenerEntidadPorId(10L);

            assertEquals(10L, resultado.getId());
        }

        /**
         * Verifica excepción cuando no existe
         */
        @Test
        void cuandoNoExiste_lanzaProductoNotFoundException() {
            when(productoRepository.findById(99L)).thenReturn(Optional.empty());

            assertThrows(ProductoNotFoundException.class, () -> productoService.obtenerEntidadPorId(99L));
        }
    }

    /**
     * Pruebas para actualizarStock
     */
    @Nested
    @DisplayName("actualizarStock")
    class ActualizarStock {

        /**
         * Verifica descuento de stock
         */
        @Test
        void cuandoExiste_descuentaStock() {
            when(varianteRepository.findById(100L)).thenReturn(Optional.of(variante));

            productoService.actualizarStock(100L, 3);

            assertEquals(7, variante.getStock());
            verify(varianteRepository).save(variante);
        }

        /**
         * Verifica excepción cuando no existe
         */
        @Test
        void cuandoNoExiste_lanzaProductoNotFoundException() {
            when(varianteRepository.findById(999L)).thenReturn(Optional.empty());

            assertThrows(ProductoNotFoundException.class, () -> productoService.actualizarStock(999L, 1));
            verify(varianteRepository, never()).save(any());
        }
    }

    /**
     * Pruebas para listarTodosLiviano
     */
    @Nested
    @DisplayName("listarTodosLiviano")
    class ListarTodosLiviano {

        /**
         * Verifica listado liviano
         */
        @Test
        void retornaListadoLiviano() {
            ProductoListadoDTO lite = new ProductoListadoDTO(10L, "Camiseta", "Desc", 50.0,
                    IMG_JPG , "Camisetas", "Brasil", "br", false, 10L, 1L);
            when(productoRepository.findAllLiviano()).thenReturn(List.of(lite));

            List<ProductoListadoDTO> resultado = productoService.listarTodosLiviano();

            assertEquals(1, resultado.size());
        }
    }

    /**
     * Pruebas para activarLote
     */
    @Nested
    @DisplayName("activarLote")
    class ActivarLote {

        /**
         * Verifica activación de productos en lote
         */
        @Test
        void activaTodosLosProductos() {
            Producto p1 = new Producto();
            p1.setId(10L);
            p1.setActivo(false);
            Producto p2 = new Producto();
            p2.setId(11L);
            p2.setActivo(false);

            when(productoRepository.findAllById(List.of(10L, 11L))).thenReturn(List.of(p1, p2));

            productoService.activarLote(List.of(10L, 11L));

            assertTrue(p1.getActivo());
            assertTrue(p2.getActivo());
            verify(productoRepository, times(2)).save(any(Producto.class));
        }

        /**
         * Verifica que no haga nada con lista vacía
         */
        @Test
        void cuandoListaVacia_noHaceNada() {
            when(productoRepository.findAllById(Collections.emptyList())).thenReturn(Collections.emptyList());

            productoService.activarLote(Collections.emptyList());

            verify(productoRepository, never()).save(any());
        }
    }

    /**
     * Pruebas para eliminar
     */
    @Nested
    @DisplayName("eliminar")
    class Eliminar {

        /**
         * Verifica desactivación de producto
         */
        @Test
        void cuandoExiste_desactivaProducto() {
            when(productoRepository.findById(10L)).thenReturn(Optional.of(producto));

            productoService.eliminar(10L);

            assertFalse(producto.getActivo());
            verify(productoRepository).save(producto);
            verify(auditoriaService).registrar(eq("PRODUCTO_DESACTIVADO"), anyString(), eq(null), anyString(), eq("Producto"));
        }

        /**
         * Verifica excepción cuando no existe
         */
        @Test
        void cuandoNoExiste_lanzaProductoNotFoundException() {
            when(productoRepository.findById(99L)).thenReturn(Optional.empty());

            assertThrows(ProductoNotFoundException.class, () -> productoService.eliminar(99L));
        }
    }
}