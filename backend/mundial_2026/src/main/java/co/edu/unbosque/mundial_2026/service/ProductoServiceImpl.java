package co.edu.unbosque.mundial_2026.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.unbosque.mundial_2026.dto.request.ProductoActualizarRequestDTO;
import co.edu.unbosque.mundial_2026.dto.request.ProductoRequestDTO;
import co.edu.unbosque.mundial_2026.dto.response.ProductoListadoDTO;
import co.edu.unbosque.mundial_2026.dto.response.ProductoResponseDTO;
import co.edu.unbosque.mundial_2026.dto.response.ProductoResponseDTO.VarianteResponseDTO;
import co.edu.unbosque.mundial_2026.entity.Categoria;
import co.edu.unbosque.mundial_2026.entity.Producto;
import co.edu.unbosque.mundial_2026.entity.VarianteProducto;
import co.edu.unbosque.mundial_2026.exception.ProductoNotFoundException;
import co.edu.unbosque.mundial_2026.repository.ProductoRepository;
import co.edu.unbosque.mundial_2026.repository.VarianteProductoRepository;

@Service
public class ProductoServiceImpl implements ProductoService {

    private final ProductoRepository productoRepository;
    private final VarianteProductoRepository varianteRepository;
    private final CategoriaService categoriaService;

    private static final String PRODUCTO_NO_ENCONTRADO = "No existe ese producto";

   
private final EventoAuditoriaService auditoriaService;
private static final String ENTIDAD_PRODUCTO = "Producto";
 

public ProductoServiceImpl(ProductoRepository productoRepository,
        VarianteProductoRepository varianteRepository,
        CategoriaService categoriaService,
        EventoAuditoriaService auditoriaService) {
    this.productoRepository = productoRepository;
    this.varianteRepository = varianteRepository;
    this.categoriaService = categoriaService;
    this.auditoriaService = auditoriaService;
}
 @Override
    @Transactional
    public void reactivar(Long id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ProductoNotFoundException(PRODUCTO_NO_ENCONTRADO));
        producto.setActivo(true);
        productoRepository.save(producto);
    }
   @Override
@Transactional
public ProductoResponseDTO crear(ProductoRequestDTO dto) {
    Categoria categoria = categoriaService.obtenerEntidadPorId(dto.getCategoriaId());
    Producto producto = toEntity(dto, categoria);
    productoRepository.save(producto);
 
    if (dto.getVariantes() != null) {
        for (ProductoRequestDTO.VarianteRequestDTO vDto : dto.getVariantes()) {
            VarianteProducto variante = new VarianteProducto();
            variante.setProducto(producto);
            variante.setEspecificacion(vDto.getEspecificacion());
            variante.setStock(vDto.getStock());
            varianteRepository.save(variante);
        }
    }
 
    auditoriaService.registrar(
            "PRODUCTO_CREADO",
            "Producto creado: '" + producto.getNombre() + "'"
                    + " | categoria: " + categoria.getNombre()
                    + " | precio: " + producto.getPrecio(),
            null,
            UUID.randomUUID().toString(),
            ENTIDAD_PRODUCTO);
 
    return toDTO(producto);
}

 @Override
@Transactional
public ProductoResponseDTO actualizar(Long id, ProductoActualizarRequestDTO dto) {
    Producto producto = productoRepository.findById(id)
            .orElseThrow(() -> new ProductoNotFoundException(PRODUCTO_NO_ENCONTRADO));
 
    final StringBuilder cambios = new StringBuilder();
    if (dto.getPrecio() != null) {
        cambios.append(" | precio: ").append(producto.getPrecio()).append(" -> ").append(dto.getPrecio());
        producto.setPrecio(dto.getPrecio());
    }
    if (dto.getImagenUrl() != null) {
        cambios.append(" | imagenUrl actualizada");
        producto.setImagenUrl(dto.getImagenUrl());
    }
    if (dto.getDescripcion() != null) {
        cambios.append(" | descripcion actualizada");
        producto.setDescripcion(dto.getDescripcion());
    }
    if (dto.getCodigoProducto() != null) {
        cambios.append(" | codigo: ").append(dto.getCodigoProducto());
        producto.setCodigoProducto(dto.getCodigoProducto());
    }
    if (dto.getEquipo() != null) {
        cambios.append(" | equipo: ").append(dto.getEquipo());
        producto.setEquipo(dto.getEquipo());
    }
    if (dto.getBandera() != null) {
        cambios.append(" | bandera actualizada");
        producto.setBandera(dto.getBandera());
    }
    if (dto.getDestacado() != null) {
        cambios.append(" | destacado: ").append(dto.getDestacado());
        producto.setDestacado(dto.getDestacado());
    }
 
    productoRepository.save(producto);
 
    auditoriaService.registrar(
            "PRODUCTO_ACTUALIZADO",
            "Producto actualizado: '" + producto.getNombre() + "'" + cambios,
            null,
            UUID.randomUUID().toString(),
            ENTIDAD_PRODUCTO);
 
    return toDTO(producto);
}

    @Override
    @Transactional(readOnly = true)
    public List<ProductoResponseDTO> listarTodos() {
     List<Producto> productos = productoRepository.findByActivoTrueWithVariantes();
        List<ProductoResponseDTO> responseDTOs = new ArrayList<>();
        for (int i = 0; i < productos.size(); i++) {
            responseDTOs.add(toDTO(productos.get(i)));
        }
        return responseDTOs;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductoResponseDTO> listarTodos(boolean soloActivos) {
    List<Producto> productos = soloActivos
            ? productoRepository.findByActivoTrueWithVariantes()
            : productoRepository.findAllWithVariantes();
        List<ProductoResponseDTO> responseDTOs = new ArrayList<>();
        for (int i = 0; i < productos.size(); i++) {
            responseDTOs.add(toDTO(productos.get(i)));
        }
        return responseDTOs;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductoResponseDTO> listarPorCategoria(Long categoriaId) {
        categoriaService.obtenerEntidadPorId(categoriaId);
        List<Producto> productos = productoRepository.findByCategoriaIdAndActivoTrue(categoriaId);
        List<ProductoResponseDTO> responseDTOs = new ArrayList<>();
        for (int i = 0; i < productos.size(); i++) {
            responseDTOs.add(toDTO(productos.get(i)));
        }
        return responseDTOs;
    }

    @Override
    @Transactional(readOnly = true)
    public ProductoResponseDTO obtenerPorId(Long id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ProductoNotFoundException(PRODUCTO_NO_ENCONTRADO));
        if (Boolean.FALSE.equals(producto.getActivo())) {
            throw new ProductoNotFoundException("Este producto no está disponible");
        }
        return toDTO(producto);
    }

    @Override
    @Transactional(readOnly = true)
    public Producto obtenerEntidadPorId(final Long id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new ProductoNotFoundException(
                        "Producto no encontrado con id: " + id));
    }

    @Override
    @Transactional
    public void actualizarStock(final Long varianteId, final int cantidad) {
        VarianteProducto variante = varianteRepository.findById(varianteId)
                .orElseThrow(() -> new ProductoNotFoundException(
                        "Variante no encontrada con id: " + varianteId));
        variante.setStock(variante.getStock() - cantidad);
        varianteRepository.save(variante);
    }

    private ProductoResponseDTO toDTO(Producto producto) {
        ProductoResponseDTO response = new ProductoResponseDTO();
        response.setId(producto.getId());
        response.setNombre(producto.getNombre());
        response.setDescripcion(producto.getDescripcion());
        response.setPrecio(producto.getPrecio());
        response.setImagenUrl(producto.getImagenUrl());
        response.setActivo(producto.getActivo());
        response.setCategoriaNombre(producto.getCategoria().getNombre());
        response.setCodigoProducto(producto.getCodigoProducto());
        response.setEquipo(producto.getEquipo());
        response.setBandera(producto.getBandera());
        response.setDestacado(producto.getDestacado());

       List<VarianteProducto> variantes = producto.getVariantes();
        List<VarianteResponseDTO> varianteDTOs = new ArrayList<>();
        int stockTotal = 0;
        for (VarianteProducto v : variantes) {
            VarianteResponseDTO vDto = new VarianteResponseDTO();
            vDto.setId(v.getId());
            vDto.setEspecificacion(v.getEspecificacion());
            vDto.setStock(v.getStock());
            varianteDTOs.add(vDto);
            stockTotal += v.getStock();
        }
        response.setVariantes(varianteDTOs);
        response.setStockTotal(stockTotal);
        return response;
    }

    private Producto toEntity(ProductoRequestDTO dto, Categoria categoria) {
        Producto producto = new Producto();
        producto.setNombre(dto.getNombre());
        producto.setDescripcion(dto.getDescripcion());
        producto.setPrecio(dto.getPrecio());
        producto.setImagenUrl(dto.getImagenUrl());
        producto.setActivo(true);
        producto.setCategoria(categoria);
        producto.setCodigoProducto(dto.getCodigoProducto());
        producto.setEquipo(dto.getEquipo());
        producto.setBandera(dto.getBandera());
        producto.setDestacado(Boolean.TRUE.equals(dto.getDestacado()));
        return producto;
    }
    @Override
@Transactional(readOnly = true)
public List<ProductoListadoDTO> listarTodosLiviano() {
    return productoRepository.findAllLiviano();
}
@Override
@Transactional
public void activarLote(List<Long> ids) {
    List<Producto> productos = productoRepository.findAllById(ids);
    for (Producto p : productos) {
        p.setActivo(true);
        productoRepository.save(p);
    }
}
@Override
@Transactional
public void eliminar(Long id) {
    Producto producto = productoRepository.findById(id)
            .orElseThrow(() -> new ProductoNotFoundException(PRODUCTO_NO_ENCONTRADO));
    producto.setActivo(false);
    productoRepository.save(producto);
 
    auditoriaService.registrar(
            "PRODUCTO_DESACTIVADO",
            "Producto desactivado: '" + producto.getNombre() + "'",
            null,
            UUID.randomUUID().toString(),
            ENTIDAD_PRODUCTO);
}
}