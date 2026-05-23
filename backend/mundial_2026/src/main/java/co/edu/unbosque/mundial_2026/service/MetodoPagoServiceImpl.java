package co.edu.unbosque.mundial_2026.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.unbosque.mundial_2026.dto.request.MetodoPagoRequestDTO;
import co.edu.unbosque.mundial_2026.dto.response.MetodoPagoResponseDTO;
import co.edu.unbosque.mundial_2026.entity.MetodoPago;
import co.edu.unbosque.mundial_2026.entity.Usuario;
import co.edu.unbosque.mundial_2026.exception.MetodoPagoInvalidoException;
import co.edu.unbosque.mundial_2026.exception.MetodoPagoNotFoundException;
import co.edu.unbosque.mundial_2026.repository.MetodoPagoRepository;

/**
 * Implementación del servicio encargado de gestionar los métodos de pago
 * asociados a cada usuario de la plataforma.
 * Permite agregar, eliminar, actualizar y consultar métodos de pago,
 * así como definir cuál es el método predeterminado.
 * El primer método registrado queda automáticamente como default,
 * y si el default es eliminado, el más reciente de los restantes toma su lugar.
 * Los tipos válidos son: CARD, PSE, CASH y TRANSFER.
 * Las operaciones relevantes quedan registradas en auditoría
 */
@Service
public class MetodoPagoServiceImpl implements MetodoPagoService {

    private static final Set<String> TIPOS_VALIDOS = Set.of("CARD", "PSE", "CASH", "TRANSFER");
    private static final String ENTIDAD_METODO_PAGO = "MetodoPago";
    private static final String METODO_NO_ENCONTRADO = "Método de pago no encontrado";
    private static final String METODO_NO_PERTENECE = "Este método de pago no pertenece al usuario";
    private final MetodoPagoRepository metodoPagoRepository;
    private final UsuarioService usuarioService;
    private final EventoAuditoriaService auditoriaService;

    public MetodoPagoServiceImpl(MetodoPagoRepository metodoPagoRepository,
            UsuarioService usuarioService,
            EventoAuditoriaService auditoriaService) {
        this.metodoPagoRepository = metodoPagoRepository;
        this.usuarioService = usuarioService;
        this.auditoriaService = auditoriaService;
    }

    /**
     * Agrega un nuevo método de pago al usuario. Si es el primero que registra,
     * queda marcado automáticamente como default. La operación queda registrada en auditoría
     *
     * @param correo correo del usuario al que se le agrega el método
     * @param dto    datos del método: tipo, etiqueta y detalles
     * @return {@link MetodoPagoResponseDTO} con la información del método registrado
     * @throws MetodoPagoInvalidoException si el tipo no está entre los valores permitidos
     */
    @Transactional
    @Override
    public MetodoPagoResponseDTO agregar(String correo, MetodoPagoRequestDTO dto) {
        validarTipo(dto.getType());

        Usuario usuario = usuarioService.obtenerEntidadPorCorreo(correo);
        List<MetodoPago> existentes = metodoPagoRepository.findByUsuarioId(usuario.getId());
        boolean esElPrimero = existentes.isEmpty();

        MetodoPago metodo = new MetodoPago();
        metodo.setUsuario(usuario);
        metodo.setTipo(dto.getType());
        metodo.setLabel(dto.getLabel());
        metodo.setDetails(dto.getDetails());
        metodo.setDefault(esElPrimero);
        metodo.setCreatedAt(LocalDateTime.now().toString());

        metodoPagoRepository.save(metodo);

        auditoriaService.registrar(
                "METODO_PAGO_AGREGADO",
                "Metodo de pago agregado para " + usuario.getCorreoUsuario()
                        + " | tipo: " + dto.getType()
                        + " | label: " + dto.getLabel()
                        + (esElPrimero ? " | asignado como default" : ""),
                usuario.getId(),
                UUID.randomUUID().toString(),
                ENTIDAD_METODO_PAGO);

        return toDTO(metodo);
    }

    /**
     * Elimina un método de pago del usuario. Si el método eliminado era el default,
     * el más reciente de los restantes queda como nuevo default automáticamente.
     * La operación queda registrada en auditoría
     *
     * @param correo correo del usuario propietario del método
     * @param id     id del método de pago a eliminar
     * @throws MetodoPagoNotFoundException si el método no existe o no pertenece al usuario
     */
    @Override
    @Transactional
    public void eliminar(String correo, Long id) {
        Usuario usuario = usuarioService.obtenerEntidadPorCorreo(correo);
        MetodoPago metodo = metodoPagoRepository.findById(id)
                .orElseThrow(() -> new MetodoPagoNotFoundException(METODO_NO_ENCONTRADO));
        if (!metodo.getUsuario().getId().equals(usuario.getId())) {
            throw new MetodoPagoNotFoundException(METODO_NO_PERTENECE);
        }

        boolean eraDefault = metodo.isDefault();
        String tipo = metodo.getTipo();
        String label = metodo.getLabel();

        metodoPagoRepository.delete(metodo);

        if (eraDefault) {
            List<MetodoPago> restantes = metodoPagoRepository
                    .findByUsuarioIdOrderByCreatedAtDesc(usuario.getId());
            if (!restantes.isEmpty()) {
                restantes.get(0).setDefault(true);
                metodoPagoRepository.save(restantes.get(0));
            }
        }

        auditoriaService.registrar(
                "METODO_PAGO_ELIMINADO",
                "Metodo de pago eliminado para " + usuario.getCorreoUsuario()
                        + " | tipo: " + tipo
                        + " | label: " + label
                        + (eraDefault ? " | era el metodo default" : ""),
                usuario.getId(),
                UUID.randomUUID().toString(),
                ENTIDAD_METODO_PAGO);
    }

    /**
     * Retorna todos los métodos de pago de un usuario, ordenados primero por default
     * y luego por fecha de creación descendente
     *
     * @param correo correo del usuario a consultar
     * @return lista de {@link MetodoPagoResponseDTO} con los métodos del usuario
     */
    @Transactional(readOnly = true)
    @Override
    public List<MetodoPagoResponseDTO> listarPorCorreo(String correo) {
        Usuario usuario = usuarioService.obtenerEntidadPorCorreo(correo);
        List<MetodoPago> lista = metodoPagoRepository
                .findByUsuarioIdOrderByIsDefaultDescCreatedAtDesc(usuario.getId());
        List<MetodoPagoResponseDTO> dtos = new ArrayList<>();
        for (MetodoPago m : lista) {
            dtos.add(toDTO(m));
        }
        return dtos;
    }

    /**
     * Cambia el método de pago predeterminado del usuario. Desmarca todos los métodos
     * actuales y asigna el default al que se indique por id
     *
     * @param correo        correo del usuario
     * @param metodoPagoId  id del método que se desea marcar como default
     * @throws MetodoPagoNotFoundException si el método indicado no pertenece al usuario
     */
    @Override
    @Transactional
    public void setDefaultPorCorreo(String correo, Long metodoPagoId) {
        Usuario usuario = usuarioService.obtenerEntidadPorCorreo(correo);
        List<MetodoPago> todos = metodoPagoRepository.findByUsuarioId(usuario.getId());
        MetodoPago target = null;
        for (MetodoPago m : todos) {
            m.setDefault(false);
            if (m.getId().equals(metodoPagoId)) {
                target = m;
            }
        }
        if (target == null) {
            throw new MetodoPagoNotFoundException(METODO_NO_ENCONTRADO);
        }
        target.setDefault(true);
        metodoPagoRepository.saveAll(todos);
    }

    /**
     * Actualiza los campos de un método de pago existente. Solo modifica los campos
     * que vengan con valor en el request; los que lleguen nulos o en blanco se ignoran
     *
     * @param correo correo del usuario propietario del método
     * @param id     id del método a actualizar
     * @param dto    campos a actualizar: tipo, etiqueta y/o detalles
     * @return {@link MetodoPagoResponseDTO} con los datos actualizados
     * @throws MetodoPagoNotFoundException si el método no existe o no pertenece al usuario
     * @throws MetodoPagoInvalidoException si el nuevo tipo no está entre los valores permitidos
     */
    @Override
    @Transactional
    public MetodoPagoResponseDTO actualizar(String correo, Long id, MetodoPagoRequestDTO dto) {
        Usuario usuario = usuarioService.obtenerEntidadPorCorreo(correo);
        MetodoPago metodo = metodoPagoRepository.findById(id)
                .orElseThrow(() -> new MetodoPagoNotFoundException(METODO_NO_ENCONTRADO));
        if (!metodo.getUsuario().getId().equals(usuario.getId())) {
            throw new MetodoPagoNotFoundException(METODO_NO_PERTENECE);
        }
        if (dto.getType() != null && !dto.getType().isBlank()) {
            validarTipo(dto.getType());
            metodo.setTipo(dto.getType());
        }
        if (dto.getLabel() != null && !dto.getLabel().isBlank()) {
            metodo.setLabel(dto.getLabel());
        }
        if (dto.getDetails() != null && !dto.getDetails().isBlank()) {
            metodo.setDetails(dto.getDetails());
        }
        metodoPagoRepository.save(metodo);
        return toDTO(metodo);
    }

    /**
     * Retorna la entidad {@link MetodoPago} directamente desde la base de datos.
     * Usado internamente por otros servicios que necesitan la entidad completa
     *
     * @param id id del método de pago a buscar
     * @return entidad {@link MetodoPago} encontrada
     * @throws MetodoPagoNotFoundException si el método no existe
     */
    @Override
    @Transactional(readOnly = true)
    public MetodoPago obtenerEntidadPorId(final Long id) {
        return metodoPagoRepository.findById(id)
                .orElseThrow(() -> new MetodoPagoNotFoundException(
                        "Método de pago no encontrado con id: " + id));
    }

    /**
     * Valida que el tipo de método de pago sea uno de los valores permitidos:
     * CARD, PSE, CASH o TRANSFER
     *
     * @param tipo tipo a validar
     * @throws MetodoPagoInvalidoException si el tipo es nulo o no está en la lista de valores válidos
     */
    private void validarTipo(String tipo) {
        if (tipo == null || !TIPOS_VALIDOS.contains(tipo)) {
            throw new MetodoPagoInvalidoException(
                    "Tipo de método de pago inválido. Valores permitidos: CARD, PSE, CASH, TRANSFER");
        }
    }

    /**
     * Convierte una entidad {@link MetodoPago} a su representación DTO de respuesta
     *
     * @param metodo entidad a convertir
     * @return {@link MetodoPagoResponseDTO} con los datos del método de pago
     */
    private MetodoPagoResponseDTO toDTO(MetodoPago metodo) {
        MetodoPagoResponseDTO dto = new MetodoPagoResponseDTO();
        dto.setId(String.valueOf(metodo.getId()));
        dto.setUserId(String.valueOf(metodo.getUsuario().getId()));
        dto.setType(metodo.getTipo());
        dto.setLabel(metodo.getLabel());
        dto.setDetails(metodo.getDetails());
        dto.setDefault(metodo.isDefault());
        dto.setCreatedAt(metodo.getCreatedAt());
        return dto;
    }
}