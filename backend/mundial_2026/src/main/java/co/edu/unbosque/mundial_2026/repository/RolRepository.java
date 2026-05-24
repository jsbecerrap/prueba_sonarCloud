package co.edu.unbosque.mundial_2026.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import co.edu.unbosque.mundial_2026.entity.Rol;
@SuppressWarnings("PMD.ImplicitFunctionalInterface")
/**
 * Repositorio para gestionar las operaciones de acceso a datos
 * de la entidad Rol.
 */
@Repository
public interface RolRepository extends JpaRepository<Rol, Long> {

    /**
     * Busca un rol por su nombre.
     *
     * @param nombre nombre del rol
     * @return rol encontrado, si existe
     */
    Optional<Rol> findByNombre(String nombre);
}