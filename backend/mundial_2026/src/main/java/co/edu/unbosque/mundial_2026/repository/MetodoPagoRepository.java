package co.edu.unbosque.mundial_2026.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import co.edu.unbosque.mundial_2026.entity.MetodoPago;

/**
 * Repositorio para gestionar las operaciones de acceso a datos
 * de la entidad MetodoPago.
 */
public interface MetodoPagoRepository extends JpaRepository<MetodoPago, Long> {

    /**
     * Obtiene todos los métodos de pago asociados a un usuario.
     *
     * @param usuarioId identificador del usuario
     * @return lista de métodos de pago del usuario
     */
    List<MetodoPago> findByUsuarioId(Long usuarioId);

    /**
     * Obtiene los métodos de pago de un usuario ordenados
     * por fecha de creación descendente.
     *
     * @param usuarioId identificador del usuario
     * @return lista de métodos de pago ordenados por fecha de creación
     */
    List<MetodoPago> findByUsuarioIdOrderByCreatedAtDesc(Long usuarioId);

    /**
     * Obtiene los métodos de pago de un usuario priorizando
     * el método predeterminado y luego ordenando por fecha
     * de creación descendente.
     *
     * @param usuarioId identificador del usuario
     * @return lista de métodos de pago ordenados por prioridad y fecha
     */
    List<MetodoPago> findByUsuarioIdOrderByIsDefaultDescCreatedAtDesc(Long usuarioId);
}