package co.edu.unbosque.mundial_2026.exception;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

/**
 * Pruebas unitarias para la clase GlobalExceptionHandler.
 * Verifica que cada excepción retorne
 * el código HTTP y la respuesta esperada.
 */
class GlobalExceptionHandlerTest {

    private static final String MENSAJE = "mensaje";
    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    /**
     * Verifica que UsuarioNotFoundException retorne 404.
     */
    @Test
    void handleUsuarioNotFound_retorna404() {
        ResponseEntity<Map<String, Object>> res =
                handler.handleUsuarioNotFound(new UsuarioNotFoundException("no encontrado"));
        assertEquals(404, res.getStatusCode().value());
        assertEquals("no encontrado", res.getBody().get(MENSAJE));
    }

    /**
     * Verifica que CorreoEnUsoException retorne 409.
     */
    @Test
    void handleCorreoEnUso_retorna409() {
        ResponseEntity<Map<String, Object>> res =
                handler.handleCorreoEnUso(new CorreoEnUsoException("correo en uso"));
        assertEquals(409, res.getStatusCode().value());
        assertEquals("correo en uso", res.getBody().get(MENSAJE));
    }

    /**
     * Verifica que ContrasenaIncorrectaException retorne 401.
     */
    @Test
    void handleContrasenaIncorrecta_retorna401() {
        ResponseEntity<Map<String, Object>> res =
                handler.handleContrasenaIncorrecta(new ContrasenaIncorrectaException("incorrecta"));
        assertEquals(401, res.getStatusCode().value());
        assertEquals("incorrecta", res.getBody().get(MENSAJE));
    }

    /**
     * Verifica que RolNotFoundException retorne 404.
     */
    @Test
    void handleRolNotFound_retorna404() {
        ResponseEntity<Map<String, Object>> res =
                handler.handleRolNotFound(new RolNotFoundException("rol no encontrado"));
        assertEquals(404, res.getStatusCode().value());
    }

    /**
     * Verifica que IllegalStateException retorne 400.
     */
    @Test
    void handleIllegalState_retorna400() {
        ResponseEntity<Map<String, Object>> res =
                handler.handleIllegalState(new IllegalStateException("estado inválido"));
        assertEquals(400, res.getStatusCode().value());
        assertEquals("estado inválido", res.getBody().get(MENSAJE));
    }

    /**
     * Verifica que IllegalArgumentException retorne 400.
     */
    @Test
    void handleIllegalArgument_retorna400() {
        ResponseEntity<Map<String, Object>> res =
                handler.handleIllegalArgument(new IllegalArgumentException("argumento inválido"));
        assertEquals(400, res.getStatusCode().value());
        assertEquals("argumento inválido", res.getBody().get(MENSAJE));
    }

    /**
     * Verifica que errores de validación retornen 400
     * incluyendo los campos con error.
     */
    @Test
    void handleValidation_retorna400ConCampos()  {
        BeanPropertyBindingResult bindingResult =
                new BeanPropertyBindingResult(new Object(), "objeto");
        bindingResult.addError(new FieldError("objeto", "nombre", "no puede estar vacío"));
        MethodArgumentNotValidException ex =
                new MethodArgumentNotValidException(null, bindingResult);

        ResponseEntity<Map<String, Object>> res = handler.handleValidation(ex);

        assertEquals(400, res.getStatusCode().value());
        assertNotNull(res.getBody().get("campos"));
        @SuppressWarnings("unchecked")
        Map<String, String> campos = (Map<String, String>) res.getBody().get("campos");
        assertEquals("no puede estar vacío", campos.get("nombre"));
    }

    /**
     * Verifica que PartidoNotFoundException retorne 404.
     */
    @Test
    void handlePartidoNotFound_retorna404() {
        ResponseEntity<Map<String, Object>> res =
                handler.handlePartidoNotFound(new PartidoNotFoundException("partido no encontrado"));
        assertEquals(404, res.getStatusCode().value());
    }

    /**
     * Verifica que EntradaNotFoundException retorne 404.
     */
    @Test
    void handleEntradaNotFound_retorna404() {
        ResponseEntity<Map<String, Object>> res =
                handler.handleEntradaNotFound(new EntradaNotFoundException("entrada no encontrada"));
        assertEquals(404, res.getStatusCode().value());
    }

    /**
     * Verifica que CupoNoDisponibleException retorne 409.
     */
    @Test
    void handleCupoNoDisponible_retorna409() {
        ResponseEntity<Map<String, Object>> res =
                handler.handleCupoNoDisponible(new CupoNoDisponibleException("sin cupo"));
        assertEquals(409, res.getStatusCode().value());
    }

    /**
     * Verifica que LimiteSuperadoException retorne 429.
     */
    @Test
    void handleLimiteSuperado_retorna429() {
        ResponseEntity<Map<String, Object>> res =
                handler.handleLimiteSuperado(new LimiteSuperadoException("límite superado"));
        assertEquals(429, res.getStatusCode().value());
    }

    /**
     * Verifica que EstadoInvalidoException retorne 400.
     */
    @Test
    void handleEstadoInvalido_retorna400() {
        ResponseEntity<Map<String, Object>> res =
                handler.handleEstadoInvalido(new EstadoInvalidoException("estado inválido"));
        assertEquals(400, res.getStatusCode().value());
    }

    /**
     * Verifica que PagoStripeException retorne 402.
     */
    @Test
    void handlePagoStripe_retorna402() {
        ResponseEntity<Map<String, Object>> res =
                handler.handlePagoStripe(new PagoStripeException("error stripe"));
        assertEquals(402, res.getStatusCode().value());
    }

    /**
     * Verifica que MetodoPagoNotFoundException retorne 404.
     */
    @Test
    void handleMetodoPagoNotFound_retorna404() {
        ResponseEntity<Map<String, Object>> res =
                handler.handleMetodoPagoNotFound(new MetodoPagoNotFoundException("método no encontrado"));
        assertEquals(404, res.getStatusCode().value());
    }

    /**
     * Verifica que ProductoNotFoundException retorne 404.
     */
    @Test
    void handleProductoNotFound_retorna404() {
        ResponseEntity<Map<String, Object>> res =
                handler.handleProductoNotFound(new ProductoNotFoundException("producto no encontrado"));
        assertEquals(404, res.getStatusCode().value());
    }

    /**
     * Verifica que OrdenNotFoundException retorne 404.
     */
    @Test
    void handleOrdenNotFound_retorna404() {
        ResponseEntity<Map<String, Object>> res =
                handler.handleOrdenNotFound(new OrdenNotFoundException("orden no encontrada"));
        assertEquals(404, res.getStatusCode().value());
    }

    /**
     * Verifica que StockInsuficienteException retorne 409.
     */
    @Test
    void handleStockInsuficiente_retorna409() {
        ResponseEntity<Map<String, Object>> res =
                handler.handleStockInsuficiente(new StockInsuficienteException("sin stock"));
        assertEquals(409, res.getStatusCode().value());
    }

    /**
     * Verifica que CategoriaNotFoundException retorne 404.
     */
    @Test
    void handleCategoriaNotFound_retorna404() {
        ResponseEntity<Map<String, Object>> res =
                handler.handleCategoriaNotFound(new CategoriaNotFoundException("categoría no encontrada"));
        assertEquals(404, res.getStatusCode().value());
    }

    /**
     * Verifica que ApuestaNotFoundException retorne 404.
     */
    @Test
    void handleApuestaNotFound_retorna404() {
        ResponseEntity<Map<String, Object>> res =
                handler.handleApuestaNotFound(new ApuestaNotFoundException("apuesta no encontrada"));
        assertEquals(404, res.getStatusCode().value());
    }

    /**
     * Verifica que PronosticoNotFoundException retorne 404.
     */
    @Test
    void handlePronosticoNotFound_retorna404() {
        ResponseEntity<Map<String, Object>> res =
                handler.handlePronosticoNotFound(new PronosticoNotFoundException("pronóstico no encontrado"));
        assertEquals(404, res.getStatusCode().value());
    }

    /**
     * Verifica que CodigoInvalidoException retorne 400.
     */
    @Test
    void handleCodigoInvalido_retorna400() {
        ResponseEntity<Map<String, Object>> res =
                handler.handleCodigoInvalido(new CodigoInvalidoException("código inválido"));
        assertEquals(400, res.getStatusCode().value());
    }

    /**
     * Verifica que ParticipacionNotFoundException retorne 404.
     */
    @Test
    void handleParticipacionNotFound_retorna404() {
        ResponseEntity<Map<String, Object>> res =
                handler.handleParticipacionNotFound(new ParticipacionNotFoundException("participación no encontrada"));
        assertEquals(404, res.getStatusCode().value());
    }

    /**
     * Verifica que UsuarioYaEnApuestaException retorne 409.
     */
    @Test
    void handleUsuarioYaEnApuesta_retorna409() {
        ResponseEntity<Map<String, Object>> res =
                handler.handleUsuarioYaEnApuesta(new UsuarioYaEnApuestaException("ya está en la apuesta"));
        assertEquals(409, res.getStatusCode().value());
    }

    /**
     * Verifica que ApuestaCerradaException retorne 400.
     */
    @Test
    void handleApuestaCerrada_retorna400() {
        ResponseEntity<Map<String, Object>> res =
                handler.handleApuestaCerrada(new ApuestaCerradaException("apuesta cerrada"));
        assertEquals(400, res.getStatusCode().value());
    }

    /**
     * Verifica que CarritoVacioException retorne 400.
     */
    @Test
    void handleCarritoVacio_retorna400() {
        ResponseEntity<Map<String, Object>> res =
                handler.handleCarritoVacio(new CarritoVacioException("carrito vacío"));
        assertEquals(400, res.getStatusCode().value());
    }

    /**
     * Verifica que MetodoPagoInvalidoException retorne 400.
     */
    @Test
    void handleMetodoPagoInvalido_retorna400() {
        ResponseEntity<Map<String, Object>> res =
                handler.handleMetodoPagoInvalido(new MetodoPagoInvalidoException("método inválido"));
        assertEquals(400, res.getStatusCode().value());
    }

    /**
     * Verifica que ItemNotFoundException retorne 404.
     */
    @Test
    void handleItemNotFound_retorna404() {
        ResponseEntity<Map<String, Object>> res =
                handler.handleItemNotFound(new ItemNotFoundException("item no encontrado"));
        assertEquals(404, res.getStatusCode().value());
    }

    /**
     * Verifica que CategoriaYaExisteException retorne 409.
     */
    @Test
    void handleCategoriaYaExiste_retorna409() {
        ResponseEntity<Map<String, Object>> res =
                handler.handleCategoriaYaExiste(new CategoriaYaExisteException("categoría ya existe"));
        assertEquals(409, res.getStatusCode().value());
    }

    /**
     * Verifica que CategoriaConProductosException retorne 409.
     */
    @Test
    void handleCategoriaConProductos_retorna409() {
        ResponseEntity<Map<String, Object>> res =
                handler.handleCategoriaConProductos(new CategoriaConProductosException("tiene productos"));
        assertEquals(409, res.getStatusCode().value());
    }

    /**
     * Verifica que PartidoYaIniciadoException retorne 400.
     */
    @Test
    void handlePartidoYaIniciado_retorna400() {
        ResponseEntity<Map<String, Object>> res =
                handler.handlePartidoYaIniciado(new PartidoYaIniciadoException("partido ya iniciado"));
        assertEquals(400, res.getStatusCode().value());
    }

    /**
     * Verifica que la respuesta incluya
     * fecha, estado y error.
     */
    @Test
    void buildResponse_incluyeDateYStatus() {
        ResponseEntity<Map<String, Object>> res =
                handler.handleUsuarioNotFound(new UsuarioNotFoundException("test"));
        assertNotNull(res.getBody().get("date"));
        assertNotNull(res.getBody().get("status"));
        assertNotNull(res.getBody().get("error"));
    }
}