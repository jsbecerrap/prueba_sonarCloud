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

    @Transactional(readOnly = true)
    @Override
    public List<MetodoPagoResponseDTO> listarPorCorreo(String correo) {
        Usuario usuario = usuarioService.obtenerEntidadPorCorreo(correo);
        List<MetodoPago> lista = metodoPagoRepository
                .findByUsuarioIdOrderByIsDefaultDescCreatedAtDesc(usuario.getId());
        List<MetodoPagoResponseDTO> dtos = new ArrayList<>();
        for (int i = 0; i < lista.size(); i++) {
            dtos.add(toDTO(lista.get(i)));
        }
        return dtos;
    }

    @Override
    @Transactional
    public void setDefaultPorCorreo(String correo, Long metodoPagoId) {
        Usuario usuario = usuarioService.obtenerEntidadPorCorreo(correo);
        List<MetodoPago> todos = metodoPagoRepository.findByUsuarioId(usuario.getId());
        MetodoPago target = null;
        for (int i = 0; i < todos.size(); i++) {
            todos.get(i).setDefault(false);
            if (todos.get(i).getId().equals(metodoPagoId)) {
                target = todos.get(i);
            }
        }
        if (target == null) {
            throw new MetodoPagoNotFoundException(METODO_NO_ENCONTRADO);
        }
        target.setDefault(true);
        metodoPagoRepository.saveAll(todos);
    }

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

    @Override
    @Transactional(readOnly = true)
    public MetodoPago obtenerEntidadPorId(final Long id) {
        return metodoPagoRepository.findById(id)
                .orElseThrow(() -> new MetodoPagoNotFoundException(
                        "Método de pago no encontrado con id: " + id));
    }

    private void validarTipo(String tipo) {
        if (tipo == null || !TIPOS_VALIDOS.contains(tipo)) {
            throw new MetodoPagoInvalidoException(
                    "Tipo de método de pago inválido. Valores permitidos: CARD, PSE, CASH, TRANSFER");
        }
    }

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