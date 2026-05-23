package co.edu.unbosque.mundial_2026.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import co.edu.unbosque.mundial_2026.entity.CiudadFavorita;

/**
 * Repositorio encargado de centralizar y proveer las operaciones básicas de persistencia y acceso a datos para las entidades de tipo CiudadFavorita
 */
@Repository
public interface CiudadRepository extends JpaRepository<CiudadFavorita, Long> {}