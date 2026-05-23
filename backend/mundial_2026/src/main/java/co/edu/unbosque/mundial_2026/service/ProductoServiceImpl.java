package co.edu.unbosque.mundial_2026.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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

/**
 * Implementación del servicio encargado de gestionar los productos de la tienda
 * del Mundial 2026.
 * Permite crear, actualizar, desactivar, reactivar y consultar productos,
 * así como gestionar el stock de sus variantes.
 * La desactivación es lógica: el producto no se elimina de la base de datos sino que
 * se marca como inactivo. Cada operación relevante queda registrada en auditoría
 */
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

    /**
     * Reactiva un producto que estaba desactivado marcándolo como activo nuevamente
     *
     * @param id id del producto a reactivar
     * @throws ProductoNotFoundException si el producto no existe
     */
    @Override
    @Transactional
    public void reactivar(Long id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ProductoNotFoundException(PRODUCTO_NO_ENCONTRADO));
        producto.setActivo(true);
        productoRepository.save(producto);
    }

    /**
     * Crea un nuevo producto activo con sus variantes de stock. Valida que la categoría
     * exista y esté activa. Si el request incluye variantes, cada una se persiste
     * con su especificación y stock correspondiente. La operación queda registrada en auditoría
     *
     * @param dto datos del producto a crear: nombre, descripción, precio, imagen, categoría y variantes
     * @return {@link ProductoResponseDTO} con la información del producto creado
     * @throws co.edu.unbosque.mundial_2026.exception.CategoriaNotFoundException si la categoría no existe o está inactiva
     */
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

    /**
     * Actualiza los campos de un producto existente. Solo modifica los campos
     * que vengan con valor en el request; los nulos o en blanco se ignoran.
     * Los cambios realizados quedan registrados en auditoría
     *
     * @param id  id del producto a actualizar
     * @param dto campos a actualizar: precio, imagen, descripción, código, equipo, bandera y destacado
     * @return {@link ProductoResponseDTO} con los datos actualizados
     * @throws ProductoNotFoundException si el producto no existe
     */
    @Override
    @Transactional
    public ProductoResponseDTO actualizar(Long id, ProductoActualizarRequestDTO dto) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ProductoNotFoundException(PRODUCTO_NO_ENCONTRADO));

        final StringBuilder cambios = new StringBuilder(128);
        if (dto.getPrecio() != null) {
            cambios.append(" | precio: ").append(producto.getPrecio()).append(" -> ").append(dto.getPrecio());
            producto.setPrecio(dto.getPrecio());
        }
        if (dto.getImagenUrl() != null && !dto.getImagenUrl().isBlank()) {
            cambios.append(" | imagenUrl actualizada");
            producto.setImagenUrl(dto.getImagenUrl());
        }
        if (dto.getDescripcion() != null && !dto.getDescripcion().isBlank()) {
            cambios.append(" | descripcion actualizada");
            producto.setDescripcion(dto.getDescripcion());
        }
        if (dto.getCodigoProducto() != null && !dto.getCodigoProducto().isBlank()) {
            cambios.append(" | codigo: ").append(dto.getCodigoProducto());
            producto.setCodigoProducto(dto.getCodigoProducto());
        }
        if (dto.getEquipo() != null && !dto.getEquipo().isBlank()) {
            cambios.append(" | equipo: ").append(dto.getEquipo());
            producto.setEquipo(dto.getEquipo());
        }
        if (dto.getBandera() != null && !dto.getBandera().isBlank()) {
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

    /**
     * Retorna todos los productos activos con sus variantes de stock
     *
     * @return lista de {@link ProductoResponseDTO} con los productos activos
     */
    @Override
    @Transactional(readOnly = true)
    public List<ProductoResponseDTO> listarTodos() {
        List<Producto> productos = productoRepository.findByActivoTrueWithVariantes();
        List<ProductoResponseDTO> responseDTOs = new ArrayList<>();
        for (Producto p : productos) {
            responseDTOs.add(toDTO(p));
        }
        return responseDTOs;
    }

    /**
     * Retorna productos con sus variantes, filtrando por estado activo según el parámetro recibido
     *
     * @param soloActivos si es true retorna solo productos activos; si es false retorna todos
     * @return lista de {@link ProductoResponseDTO} según el filtro indicado
     */
    @Override
    @Transactional(readOnly = true)
    public List<ProductoResponseDTO> listarTodos(boolean soloActivos) {
        List<Producto> productos = soloActivos
                ? productoRepository.findByActivoTrueWithVariantes()
                : productoRepository.findAllWithVariantes();
        List<ProductoResponseDTO> responseDTOs = new ArrayList<>();
        for (Producto p : productos) {
            responseDTOs.add(toDTO(p));
        }
        return responseDTOs;
    }

    /**
     * Retorna todos los productos activos que pertenecen a una categoría específica
     *
     * @param categoriaId id de la categoría a filtrar
     * @return lista de {@link ProductoResponseDTO} con los productos activos de esa categoría
     * @throws co.edu.unbosque.mundial_2026.exception.CategoriaNotFoundException si la categoría no existe o está inactiva
     */
    @Override
    @Transactional(readOnly = true)
    public List<ProductoResponseDTO> listarPorCategoria(Long categoriaId) {
        categoriaService.obtenerEntidadPorId(categoriaId);
        List<Producto> productos = productoRepository.findByCategoriaIdAndActivoTrue(categoriaId);
        List<ProductoResponseDTO> responseDTOs = new ArrayList<>();
        for (Producto p : productos) {
            responseDTOs.add(toDTO(p));
        }
        return responseDTOs;
    }

    /**
     * Busca y retorna un producto activo por su id
     *
     * @param id id del producto a consultar
     * @return {@link ProductoResponseDTO} con los datos del producto
     * @throws ProductoNotFoundException si el producto no existe o está inactivo
     */
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

    /**
     * Retorna la entidad {@link Producto} directamente desde la base de datos.
     * Usado internamente por otros servicios que necesitan la entidad completa
     *
     * @param id id del producto a buscar
     * @return entidad {@link Producto} encontrada
     * @throws ProductoNotFoundException si el producto no existe
     */
    @Override
    @Transactional(readOnly = true)
    public Producto obtenerEntidadPorId(final Long id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new ProductoNotFoundException(
                        "Producto no encontrado con id: " + id));
    }

    /**
     * Descuenta la cantidad indicada del stock de una variante específica.
     * Se usa al confirmar el pago de una orden en la tienda
     *
     * @param varianteId id de la variante a actualizar
     * @param cantidad   cantidad a descontar del stock
     * @throws ProductoNotFoundException si la variante no existe
     */
    @Override
    @Transactional
    public void actualizarStock(final Long varianteId, final int cantidad) {
        VarianteProducto variante = varianteRepository.findById(varianteId)
                .orElseThrow(() -> new ProductoNotFoundException(
                        "Variante no encontrada con id: " + varianteId));
        variante.setStock(variante.getStock() - cantidad);
        varianteRepository.save(variante);
    }

    /**
     * Convierte una entidad {@link Producto} a su representación DTO de respuesta,
     * incluyendo la lista de variantes y el stock total calculado como suma de todas ellas
     *
     * @param producto entidad a convertir
     * @return {@link ProductoResponseDTO} con los datos completos del producto
     */
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

    /**
     * Convierte un DTO de request a una entidad {@link Producto} lista para persistir.
     * El producto se crea con estado activo por defecto
     *
     * @param dto      datos del request con los campos del producto
     * @param categoria entidad de categoría ya validada y cargada
     * @return entidad {@link Producto} construida a partir del DTO
     */
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

    /**
     * Retorna todos los productos en un formato liviano sin variantes ni detalles completos,
     * optimizado para listados administrativos donde no se necesita toda la información
     *
     * @return lista de {@link ProductoListadoDTO} con los datos resumidos de cada producto
     */
    @Override
    @Transactional(readOnly = true)
    public List<ProductoListadoDTO> listarTodosLiviano() {
        return productoRepository.findAllLiviano();
    }

    /**
     * Activa un lote de productos por sus ids. Útil para reactivar múltiples productos
     * en una sola operación desde la vista administrativa
     *
     * @param ids lista de ids de los productos a activar
     */
    @Override
    @Transactional
    public void activarLote(List<Long> ids) {
        List<Producto> productos = productoRepository.findAllById(ids);
        for (Producto p : productos) {
            p.setActivo(true);
            productoRepository.save(p);
        }
    }

    /**
     * Desactiva un producto de forma lógica marcándolo como inactivo sin eliminarlo.
     * La operación queda registrada en auditoría
     *
     * @param id id del producto a desactivar
     * @throws ProductoNotFoundException si el producto no existe
     */
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