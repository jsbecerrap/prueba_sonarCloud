package co.edu.unbosque.mundial_2026.security;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Clase auxiliar utilizada para la deserialización de autoridades
 * en el proceso de manejo de JWT con Jackson
 * permite reconstruir roles desde JSON de forma segura
 */
public abstract class SimpleGrantedAuthorityJsonCreator {

    /**
     * Constructor usado por Jackson para crear instancias
     * a partir del campo authority en el token JWT
     *
     * @param role nombre del rol o autoridad del usuario
     */
    @JsonCreator
    protected SimpleGrantedAuthorityJsonCreator(@JsonProperty("authority") String role) {
    }
}