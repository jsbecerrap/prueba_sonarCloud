package co.edu.unbosque.mundial_2026.service;

import java.util.List;

import co.edu.unbosque.mundial_2026.dto.request.MetodoPagoRequestDTO;
import co.edu.unbosque.mundial_2026.dto.response.MetodoPagoResponseDTO;
import co.edu.unbosque.mundial_2026.entity.MetodoPago;

/**
 * Contrato del servicio de métodos de pago — gestiona los medios de pago
 * asociados a cada usuario, permitiendo agregar, consultar, actualizar y eliminar
 */
public interface MetodoPagoService {

    /**
     * Agrega un nuevo método de pago al perfil del usuario identificado por correo
     *
     * @param correo correo del usuario
     * @param dto    datos del método de pago a registrar
     * @return método de pago creado
     */
    MetodoPagoResponseDTO agregar(String correo, MetodoPagoRequestDTO dto);

    /**
     * Lista todos los métodos de pago registrados por un usuario
     *
     * @param correo correo del usuario
     * @return lista de métodos de pago del usuario
     */
    List<MetodoPagoResponseDTO> listarPorCorreo(String correo);

    /**
     * Establece un método de pago como predeterminado para el usuario,
     * desmarcando el que estuviera activo previamente
     *
     * @param correo        correo del usuario
     * @param metodoPagoId  ID del método de pago a marcar como predeterminado
     */
    void setDefaultPorCorreo(String correo, Long metodoPagoId);

    /**
     * Elimina un método de pago del perfil del usuario validando que le pertenezca
     *
     * @param correo correo del usuario
     * @param id     ID del método de pago a eliminar
     */
    void eliminar(String correo, Long id);

    /**
     * Actualiza los datos de un método de pago existente del usuario
     *
     * @param correo correo del usuario
     * @param id     ID del método de pago a actualizar
     * @param dto    nuevos datos del método de pago
     * @return método de pago con los datos actualizados
     */
    MetodoPagoResponseDTO actualizar(String correo, Long id, MetodoPagoRequestDTO dto);

    /**
     * Obtiene la entidad {@link MetodoPago} directamente desde la base de datos —
     * pensado para uso interno entre servicios, no para exponer en el controlador
     *
     * @param id ID del método de pago
     * @return entidad {@link MetodoPago}
     */
    MetodoPago obtenerEntidadPorId(Long id);
}