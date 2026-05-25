package co.edu.unbosque.mundial_2026.security;

import java.util.Base64;

import javax.crypto.SecretKey;

import io.jsonwebtoken.security.Keys;

/**
 * Clase utilitaria encargada de gestionar la clave secreta utilizada
 * para la firma y validación de tokens JWT dentro del sistema
 */
public final class TokenJwt {

    public static final String PREFIX_TOKEN = "Bearer ";
    public static final String HEADER_AUTHORIZATION = "Authorization";
    public static final String CONTENT_TYPE = "application/json";

    private static SecretKey secretKey;

    private TokenJwt() {}

    /**
     * Inicializa la clave secreta del JWT a partir de un valor en Base64
     * decodifica la cadena y genera una llave segura para firmar tokens
     *
     * @param secret clave secreta en formato Base64
     */
    public static void init(final String secret) {//se trae la clave 
        final byte[] keyBytes = Base64.getDecoder().decode(secret);//decodifica en bytes
        secretKey = Keys.hmacShaKeyFor(keyBytes);//se necesita empaquetar la secret key a envolver los bytes y indicar el algoritmo de firmar usando SHA-256
    }

    /**
     * Retorna la clave secreta utilizada para firmar y validar JWT
     *
     * @return clave secreta del sistema
     */
    public static SecretKey getSecretKey() {
        return secretKey;
    }
}