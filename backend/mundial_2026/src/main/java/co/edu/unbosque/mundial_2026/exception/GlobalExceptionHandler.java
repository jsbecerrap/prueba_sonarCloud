package co.edu.unbosque.mundial_2026.exception;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

/**
 * Manejador global de excepciones para la aplicación.
 * Captura excepciones personalizadas y genera respuestas
 * HTTP estandarizadas para la API.
 */
@RestControllerAdvice
public class GlobalExceptionHandler{

    /**
     * Maneja la excepción cuando no se encuentra un usuario.
     *
     * @param e excepción lanzada
     * @return respuesta HTTP con el error
     */
    @ExceptionHandler(UsuarioNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleUsuarioNotFound(UsuarioNotFoundException e) {
        return buildResponse(HttpStatus.NOT_FOUND, e.getMessage());
    }

    /**
     * Maneja la excepción cuando un correo ya está en uso.
     *
     * @param e excepción lanzada
     * @return respuesta HTTP con el error
     */
    @ExceptionHandler(CorreoEnUsoException.class)
    public ResponseEntity<Map<String, Object>> handleCorreoEnUso(CorreoEnUsoException e) {
        return buildResponse(HttpStatus.CONFLICT, e.getMessage());
    }

    /**
     * Maneja la excepción cuando la contraseña es incorrecta.
     *
     * @param e excepción lanzada
     * @return respuesta HTTP con el error
     */
    @ExceptionHandler(ContrasenaIncorrectaException.class)
    public ResponseEntity<Map<String, Object>> handleContrasenaIncorrecta(ContrasenaIncorrectaException e) {
        return buildResponse(HttpStatus.UNAUTHORIZED, e.getMessage());
    }

    /**
     * Maneja la excepción cuando no se encuentra un rol.
     *
     * @param e excepción lanzada
     * @return respuesta HTTP con el error
     */
    @ExceptionHandler(RolNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleRolNotFound(RolNotFoundException e) {
        return buildResponse(HttpStatus.NOT_FOUND, e.getMessage());
    }

    /**
     * Maneja excepciones de estado inválido.
     *
     * @param e excepción lanzada
     * @return respuesta HTTP con el error
     */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalState(IllegalStateException e) {
        return buildResponse(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    /**
     * Maneja excepciones por argumentos inválidos.
     *
     * @param e excepción lanzada
     * @return respuesta HTTP con el error
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException e) {
        return buildResponse(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    /**
     * Maneja errores de validación en los datos de entrada.
     *
     * @param e excepción lanzada
     * @return respuesta HTTP con detalles de validación
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException e) {
        final Map<String, Object> errors = new HashMap<>();
        errors.put("date", ZonedDateTime.now());
        errors.put("status", HttpStatus.BAD_REQUEST.value());
        errors.put("error", "Error de validación");
        final Map<String, String> campos = new HashMap<>();
        for (FieldError field : e.getBindingResult().getFieldErrors()) {
            campos.put(field.getField(), field.getDefaultMessage());
        }
        errors.put("campos", campos);
        return ResponseEntity.badRequest().body(errors);
    }

    /**
     * Maneja la excepción cuando no se encuentra un endpoint.
     *
     * @param e excepción lanzada
     * @return respuesta HTTP con el error
     */
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNoResource(NoResourceFoundException e) {
        return buildResponse(HttpStatus.NOT_FOUND, "Endpoint no encontrado");
    }

    /**
     * Construye una respuesta HTTP estándar para errores.
     *
     * @param status estado HTTP de la respuesta
     * @param mensaje mensaje descriptivo del error
     * @return respuesta HTTP estructurada
     */
    private ResponseEntity<Map<String, Object>> buildResponse(HttpStatus status, String mensaje) {
        final Map<String, Object> body = new HashMap<>();
        body.put("date", ZonedDateTime.now());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("mensaje", mensaje);
        return ResponseEntity.status(status).body(body);
    }

    /** Maneja cuando no se encuentra un partido. */
    @ExceptionHandler(PartidoNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handlePartidoNotFound(PartidoNotFoundException e) {
        return buildResponse(HttpStatus.NOT_FOUND, e.getMessage());
    }

    /** Maneja cuando no se encuentra una entrada. */
    @ExceptionHandler(EntradaNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleEntradaNotFound(EntradaNotFoundException e) {
        return buildResponse(HttpStatus.NOT_FOUND, e.getMessage());
    }

    /** Maneja cuando no hay cupo disponible. */
    @ExceptionHandler(CupoNoDisponibleException.class)
    public ResponseEntity<Map<String, Object>> handleCupoNoDisponible(CupoNoDisponibleException e) {
        return buildResponse(HttpStatus.CONFLICT, e.getMessage());
    }

    /** Maneja cuando se supera un límite permitido. */
    @ExceptionHandler(LimiteSuperadoException.class)
    public ResponseEntity<Map<String, Object>> handleLimiteSuperado(LimiteSuperadoException e) {
        return buildResponse(HttpStatus.TOO_MANY_REQUESTS, e.getMessage());
    }

    /** Maneja estados inválidos. */
    @ExceptionHandler(EstadoInvalidoException.class)
    public ResponseEntity<Map<String, Object>> handleEstadoInvalido(EstadoInvalidoException e) {
        return buildResponse(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    /** Maneja errores de pago con Stripe. */
    @ExceptionHandler(PagoStripeException.class)
    public ResponseEntity<Map<String, Object>> handlePagoStripe(PagoStripeException e) {
        return buildResponse(HttpStatus.PAYMENT_REQUIRED, e.getMessage());
    }

    /** Maneja cuando no se encuentra un método de pago. */
    @ExceptionHandler(MetodoPagoNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleMetodoPagoNotFound(MetodoPagoNotFoundException e) {
        return buildResponse(HttpStatus.NOT_FOUND, e.getMessage());
    }

    /** Maneja cuando no se encuentra un producto. */
    @ExceptionHandler(ProductoNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleProductoNotFound(ProductoNotFoundException e) {
        return buildResponse(HttpStatus.NOT_FOUND, e.getMessage());
    }

    /** Maneja cuando no se encuentra una orden. */
    @ExceptionHandler(OrdenNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleOrdenNotFound(OrdenNotFoundException e) {
        return buildResponse(HttpStatus.NOT_FOUND, e.getMessage());
    }

    /** Maneja cuando no hay stock suficiente. */
    @ExceptionHandler(StockInsuficienteException.class)
    public ResponseEntity<Map<String, Object>> handleStockInsuficiente(StockInsuficienteException e) {
        return buildResponse(HttpStatus.CONFLICT, e.getMessage());
    }

    /** Maneja cuando no se encuentra una categoría. */
    @ExceptionHandler(CategoriaNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleCategoriaNotFound(CategoriaNotFoundException e) {
        return buildResponse(HttpStatus.NOT_FOUND, e.getMessage());
    }

    /** Maneja cuando no se encuentra una apuesta. */
    @ExceptionHandler(ApuestaNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleApuestaNotFound(ApuestaNotFoundException e) {
        return buildResponse(HttpStatus.NOT_FOUND, e.getMessage());
    }

    /** Maneja cuando no se encuentra un pronóstico. */
    @ExceptionHandler(PronosticoNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handlePronosticoNotFound(PronosticoNotFoundException e) {
        return buildResponse(HttpStatus.NOT_FOUND, e.getMessage());
    }

    /** Maneja códigos inválidos. */
    @ExceptionHandler(CodigoInvalidoException.class)
    public ResponseEntity<Map<String, Object>> handleCodigoInvalido(CodigoInvalidoException e) {
        return buildResponse(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    /** Maneja cuando no se encuentra una participación. */
    @ExceptionHandler(ParticipacionNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleParticipacionNotFound(ParticipacionNotFoundException e) {
        return buildResponse(HttpStatus.NOT_FOUND, e.getMessage());
    }

    /** Maneja cuando el usuario ya está en la apuesta. */
    @ExceptionHandler(UsuarioYaEnApuestaException.class)
    public ResponseEntity<Map<String, Object>> handleUsuarioYaEnApuesta(UsuarioYaEnApuestaException e) {
        return buildResponse(HttpStatus.CONFLICT, e.getMessage());
    }

    /** Maneja cuando una apuesta ya está cerrada. */
    @ExceptionHandler(ApuestaCerradaException.class)
    public ResponseEntity<Map<String, Object>> handleApuestaCerrada(ApuestaCerradaException e) {
        return buildResponse(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    /** Maneja cuando el carrito está vacío. */
    @ExceptionHandler(CarritoVacioException.class)
    public ResponseEntity<Map<String, Object>> handleCarritoVacio(CarritoVacioException e) {
        return buildResponse(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    /** Maneja métodos de pago inválidos. */
    @ExceptionHandler(MetodoPagoInvalidoException.class)
    public ResponseEntity<Map<String, Object>> handleMetodoPagoInvalido(MetodoPagoInvalidoException e) {
        return buildResponse(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    /** Maneja cuando no se encuentra un item. */
    @ExceptionHandler(ItemNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleItemNotFound(ItemNotFoundException e) {
        return buildResponse(HttpStatus.NOT_FOUND, e.getMessage());
    }

    /** Maneja cuando una categoría ya existe. */
    @ExceptionHandler(CategoriaYaExisteException.class)
    public ResponseEntity<Map<String, Object>> handleCategoriaYaExiste(CategoriaYaExisteException e) {
        return buildResponse(HttpStatus.CONFLICT, e.getMessage());
    }

    /** Maneja cuando una categoría tiene productos asociados. */
    @ExceptionHandler(CategoriaConProductosException.class)
    public ResponseEntity<Map<String, Object>> handleCategoriaConProductos(CategoriaConProductosException ex) {
        return buildResponse(HttpStatus.CONFLICT, ex.getMessage());
    }

    /** Maneja cuando un partido ya inició. */
    @ExceptionHandler(PartidoYaIniciadoException.class)
    public ResponseEntity<Map<String, Object>> handlePartidoYaIniciado(PartidoYaIniciadoException e) {
        return buildResponse(HttpStatus.BAD_REQUEST, e.getMessage());
    }
}