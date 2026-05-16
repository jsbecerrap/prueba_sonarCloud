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

    @Override
    @Transactional
    public CategoriaResponseDTO actualizar(Long id, CategoriaRequestDTO dto) {
        Categoria categoria = categoriaRepository.findByIdAndActivoTrue(id)
                .orElseThrow(() -> new CategoriaNotFoundException(CATEGORIA_NO_ENCONTRADA + id));

        final StringBuilder cambios = new StringBuilder();
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
                "Categoria actualizada (id: " + id + ")" + cambios,
                null,
                UUID.randomUUID().toString(),
                ENTIDAD_CATEGORIA);

        return toDTO(categoria);
    }

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
                "Categoria desactivada: '" + categoria.getNombre() + "' (id: " + id + ")"
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
                "Categoria reactivada: '" + categoria.getNombre() + "' (id: " + id + ")"
                        + " | productos visibles nuevamente: " + productos.size(),
                null,
                UUID.randomUUID().toString(),
                ENTIDAD_CATEGORIA);

        ReactivarCategoriaResponseDTO response = new ReactivarCategoriaResponseDTO();
        response.setCategoria(toDTO(categoria));
        response.setProductos(productos);
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoriaResponseDTO> listar() {
        return categoriaRepository.findByActivoTrue().stream()
                .map(this::toDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoriaResponseDTO> listarTodas() {
        return categoriaRepository.findAll().stream()
                .map(this::toDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Categoria obtenerEntidadPorId(Long id) {
        return categoriaRepository.findByIdAndActivoTrue(id)
                .orElseThrow(() -> new CategoriaNotFoundException(CATEGORIA_NO_ENCONTRADA + id));
    }

    private CategoriaResponseDTO toDTO(Categoria categoria) {
        CategoriaResponseDTO response = new CategoriaResponseDTO();
        response.setId(categoria.getId());
        response.setNombre(categoria.getNombre());
        response.setDescripcion(categoria.getDescripcion());
        response.setActivo(categoria.getActivo());
        return response;
    }

    private Categoria toEntity(CategoriaRequestDTO dto) {
        Categoria categoria = new Categoria();
        categoria.setNombre(dto.getNombre());
        categoria.setDescripcion(dto.getDescripcion());
        return categoria;
    }

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

    @Override
    @Transactional(readOnly = true)
    public List<ProductoResponseDTO> obtenerProductosPorCategoria(Long id) {
        return productoRepository.findByCategoriaId(id).stream()
                .map(this::toProductoDTO)
                .toList();
    }

}