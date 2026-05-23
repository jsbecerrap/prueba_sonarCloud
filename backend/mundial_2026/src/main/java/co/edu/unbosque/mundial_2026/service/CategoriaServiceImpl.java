package co.edu.unbosque.mundial_2026.service;

import java.util.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

/**
 * Implementación del servicio encargado de gestionar las categorías de productos
 * disponibles en la plataforma del Mundial 2026.
 * Permite crear, actualizar, desactivar y reactivar categorías,
 * así como consultar los productos asociados a cada una.
 * Cuando una categoría se desactiva, todos sus productos quedan desactivados también.
 * Cada operación relevante queda registrada en el sistema de auditoría
 */
@Service
public class CategoriaServiceImpl implements CategoriaService {

    private final CategoriaRepository categoriaRepository;
    private final ProductoRepository productoRepository;
    private static final String CATEGORIA_NO_ENCONTRADA = "Categoría no encontrada con id: ";

    private final EventoAuditoriaService auditoriaService;
    private static final String ENTIDAD_CATEGORIA = "Categoria";

    public CategoriaServiceImpl(CategoriaRepository categoriaRepository,
            ProductoRepository productoRepository,
            EventoAuditoriaService auditoriaService) {
        this.categoriaRepository = categoriaRepository;
        this.productoRepository = productoRepository;
        this.auditoriaService = auditoriaService;
    }

    /**
     * Crea una nueva categoría verificando que no exista otra con el mismo nombre.
     * La operación queda registrada en auditoría
     *
     * @param dto datos de la categoría a crear (nombre y descripción)
     * @return {@link CategoriaResponseDTO} con la información de la categoría creada
     * @throws CategoriaYaExisteException si ya existe una categoría con ese nombre
     */
    @Override
    @Transactional
    public CategoriaResponseDTO crear(CategoriaRequestDTO dto) {
        Optional<Categoria> existente = categoriaRepository.findByNombre(dto.getNombre());
        if (existente.isPresent()) {
            throw new CategoriaYaExisteException("Ya existe un nombre de esa categoria");
        }
        Categoria categoria = toEntity(dto);
        categoriaRepository.save(categoria);

        auditoriaService.registrar(
                "CATEGORIA_CREADA",
                "Categoria creada: '" + categoria.getNombre() + "'",
                null,
                UUID.randomUUID().toString(),
                ENTIDAD_CATEGORIA);

        return toDTO(categoria);
    }

    /**
     * Actualiza el nombre o la descripción de una categoría activa.
     * Solo modifica los campos que vienen con valor en el request.
     * Valida que el nuevo nombre no esté en uso por otra categoría diferente.
     * Los cambios realizados quedan registrados en auditoría
     *
     * @param id  id de la categoría a actualizar
     * @param dto campos a actualizar (nombre y/o descripción)
     * @return {@link CategoriaResponseDTO} con los datos actualizados
     * @throws CategoriaNotFoundException si la categoría no existe o está inactiva
     * @throws CategoriaYaExisteException si el nuevo nombre ya lo usa otra categoría
     */
    @Override
    @Transactional
    public CategoriaResponseDTO actualizar(Long id, CategoriaRequestDTO dto) {
        Categoria categoria = categoriaRepository.findByIdAndActivoTrue(id)
                .orElseThrow(() -> new CategoriaNotFoundException(CATEGORIA_NO_ENCONTRADA + id));

        final StringBuilder cambios = new StringBuilder(100);
        if (dto.getNombre() != null && !dto.getNombre().isBlank()) {
            Optional<Categoria> existente = categoriaRepository.findByNombre(dto.getNombre());
            if (existente.isPresent() && !existente.get().getId().equals(id)) {
                throw new CategoriaYaExisteException("Ya existe una categoría con ese nombre");
            }
            cambios.append(" | nombre: ").append(categoria.getNombre()).append(" -> ").append(dto.getNombre());
            categoria.setNombre(dto.getNombre());
        }
        if (dto.getDescripcion() != null && !dto.getDescripcion().isBlank()) {
            cambios.append(" | descripcion actualizada");
            categoria.setDescripcion(dto.getDescripcion());
        }
        categoriaRepository.save(categoria);

        auditoriaService.registrar(
                "CATEGORIA_ACTUALIZADA",
                "Categoria actualizada: '" + categoria.getNombre() + "'" + cambios,
                null,
                UUID.randomUUID().toString(),
                ENTIDAD_CATEGORIA);

        return toDTO(categoria);
    }

    /**
     * Desactiva una categoría y en cascada desactiva todos los productos que pertenecen a ella,
     * dejándolos invisibles en la plataforma. La operación queda registrada en auditoría
     * indicando cuántos productos fueron afectados
     *
     * @param id id de la categoría a desactivar
     * @return {@link DesactivarCategoriaResponseDTO} con la categoría desactivada y la lista de productos afectados
     * @throws CategoriaNotFoundException si la categoría no existe o ya está inactiva
     */
    @Override
    @Transactional
    public DesactivarCategoriaResponseDTO desactivar(Long id) {
        Categoria categoria = categoriaRepository.findByIdAndActivoTrue(id)
                .orElseThrow(() -> new CategoriaNotFoundException(CATEGORIA_NO_ENCONTRADA + id));

        categoria.setActivo(false);
        categoriaRepository.save(categoria);

        List<Producto> productos = productoRepository.findByCategoriaId(id);
        for (Producto p : productos) {
            p.setActivo(false);
            productoRepository.save(p);
        }

        auditoriaService.registrar(
                "CATEGORIA_DESACTIVADA",
                "Categoria desactivada: '" + categoria.getNombre() + "'"
                        + " | productos afectados: " + productos.size(),
                null,
                UUID.randomUUID().toString(),
                ENTIDAD_CATEGORIA);

        List<ProductoResponseDTO> productosAfectados = productos.stream()
                .map(this::toProductoDTO)
                .toList();

        DesactivarCategoriaResponseDTO response = new DesactivarCategoriaResponseDTO();
        response.setCategoria(toDTO(categoria));
        response.setProductosAfectados(productosAfectados);
        return response;
    }

    /**
     * Reactiva una categoría que estaba desactivada. Los productos asociados
     * a ella no se reactivan automáticamente, pero quedan visibles en la respuesta
     * para que el administrador tenga contexto de cuáles existen.
     * La operación queda registrada en auditoría
     *
     * @param id id de la categoría a reactivar
     * @return {@link ReactivarCategoriaResponseDTO} con la categoría reactivada y sus productos asociados
     * @throws CategoriaNotFoundException si la categoría no existe
     */
    @Override
    @Transactional
    public ReactivarCategoriaResponseDTO reactivar(Long id) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new CategoriaNotFoundException(CATEGORIA_NO_ENCONTRADA + id));

        categoria.setActivo(true);
        categoriaRepository.save(categoria);

        List<ProductoResponseDTO> productos = productoRepository.findByCategoriaId(id).stream()
                .map(this::toProductoDTO)
                .toList();

        auditoriaService.registrar(
                "CATEGORIA_REACTIVADA",
                "Categoria reactivada: '" + categoria.getNombre() + "'"
                        + " | productos visibles nuevamente: " + productos.size(),
                null,
                UUID.randomUUID().toString(),
                ENTIDAD_CATEGORIA);

        ReactivarCategoriaResponseDTO response = new ReactivarCategoriaResponseDTO();
        response.setCategoria(toDTO(categoria));
        response.setProductos(productos);
        return response;
    }

    /**
     * Retorna únicamente las categorías que están activas en la plataforma
     *
     * @return lista de {@link CategoriaResponseDTO} con las categorías activas
     */
    @Override
    @Transactional(readOnly = true)
    public List<CategoriaResponseDTO> listar() {
        return categoriaRepository.findByActivoTrue().stream()
                .map(this::toDTO)
                .toList();
    }

    /**
     * Retorna todas las categorías registradas en el sistema, incluyendo las inactivas.
     * Útil para vistas administrativas donde se necesita visibilidad completa
     *
     * @return lista de {@link CategoriaResponseDTO} con todas las categorías
     */
    @Override
    @Transactional(readOnly = true)
    public List<CategoriaResponseDTO> listarTodas() {
        return categoriaRepository.findAll().stream()
                .map(this::toDTO)
                .toList();
    }

    /**
     * Retorna la entidad {@link Categoria} directamente desde la base de datos.
     * Usado internamente por otros servicios que necesitan la entidad completa,
     * no el DTO
     *
     * @param id id de la categoría a buscar
     * @return entidad {@link Categoria} activa
     * @throws CategoriaNotFoundException si la categoría no existe o está inactiva
     */
    @Override
    @Transactional(readOnly = true)
    public Categoria obtenerEntidadPorId(Long id) {
        return categoriaRepository.findByIdAndActivoTrue(id)
                .orElseThrow(() -> new CategoriaNotFoundException(CATEGORIA_NO_ENCONTRADA + id));
    }

    /**
     * Convierte una entidad {@link Categoria} a su representación DTO para la respuesta
     *
     * @param categoria entidad a convertir
     * @return {@link CategoriaResponseDTO} con los datos de la categoría
     */
    private CategoriaResponseDTO toDTO(Categoria categoria) {
        CategoriaResponseDTO response = new CategoriaResponseDTO();
        response.setId(categoria.getId());
        response.setNombre(categoria.getNombre());
        response.setDescripcion(categoria.getDescripcion());
        response.setActivo(categoria.getActivo());
        return response;
    }

    /**
     * Convierte un DTO de request a una entidad {@link Categoria} lista para persistir
     *
     * @param dto datos del request con nombre y descripción
     * @return entidad {@link Categoria} construida a partir del DTO
     */
    private Categoria toEntity(CategoriaRequestDTO dto) {
        Categoria categoria = new Categoria();
        categoria.setNombre(dto.getNombre());
        categoria.setDescripcion(dto.getDescripcion());
        return categoria;
    }

    /**
     * Convierte una entidad {@link Producto} a su representación DTO de respuesta
     *
     * @param p entidad producto a convertir
     * @return {@link ProductoResponseDTO} con los datos del producto
     */
    private ProductoResponseDTO toProductoDTO(Producto p) {
        ProductoResponseDTO dto = new ProductoResponseDTO();
        dto.setId(p.getId());
        dto.setNombre(p.getNombre());
        dto.setDescripcion(p.getDescripcion());
        dto.setPrecio(p.getPrecio());
        dto.setImagenUrl(p.getImagenUrl());
        dto.setActivo(p.getActivo());
        dto.setCategoriaNombre(p.getCategoria().getNombre());
        return dto;
    }

    /**
     * Retorna todos los productos que pertenecen a una categoría específica,
     * sin importar si están activos o no
     *
     * @param id id de la categoría
     * @return lista de {@link ProductoResponseDTO} con los productos de esa categoría
     */
    @Override
    @Transactional(readOnly = true)
    public List<ProductoResponseDTO> obtenerProductosPorCategoria(Long id) {
        return productoRepository.findByCategoriaId(id).stream()
                .map(this::toProductoDTO)
                .toList();
    }

}