package co.edu.unbosque.mundial_2026.security;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import io.jsonwebtoken.Jwts;

/**
 * Componente encargado de manejar una lista en memoria de tokens JWT invalidados
 * permite bloquear sesiones activas y evitar reutilización de tokens expirados o cerrados
 */
@Component
public class TokenBlacklist {

    private final Map<String, Long> tokens = new ConcurrentHashMap<>();

    /**
     * Agrega un token a la lista de tokens invalidados
     * obtiene su fecha de expiración para gestionar limpieza automática
     *
     * @param token JWT que será marcado como inválido
     */
    public void agregar(final String token) {
        final long expiracion = Jwts.parser()
                .verifyWith(TokenJwt.getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getExpiration()
                .getTime();

        tokens.put(token, expiracion);
    }

    /**
     * Verifica si un token ya fue invalidado previamente
     * y elimina automáticamente los que ya expiraron
     *
     * @param token JWT a validar
     * @return true si el token está en blacklist
     */
    public boolean estaInvalidado(final String token) {
        limpiarExpirados();
        return tokens.containsKey(token);
    }

    /**
     * Limpia automáticamente los tokens expirados de la blacklist
     * evitando crecimiento innecesario en memoria
     */
    private void limpiarExpirados() {
        final long ahora = System.currentTimeMillis();
        tokens.entrySet().removeIf(entry -> entry.getValue() < ahora);
    }
}