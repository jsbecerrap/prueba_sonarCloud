package co.edu.unbosque.mundial_2026.exception;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleUsuarioNotFound_retorna404() {
        ResponseEntity<Map<String, Object>> res =
                handler.handleUsuarioNotFound(new UsuarioNotFoundException("no encontrado"));
        assertEquals(404, res.getStatusCode().value());
        assertEquals("no encontrado", res.getBody().get("mensaje"));
    }

    @Test
    void handleCorreoEnUso_retorna409() {
        ResponseEntity<Map<String, Object>> res =
                handler.handleCorreoEnUso(new CorreoEnUsoException("correo en uso"));
        assertEquals(409, res.getStatusCode().value());
        assertEquals("correo en uso", res.getBody().get("mensaje"));
    }

    @Test
    void handleContrasenaIncorrecta_retorna401() {
        ResponseEntity<Map<String, Object>> res =
                handler.handleContrasenaIncorrecta(new ContrasenaIncorrectaException("incorrecta"));
        assertEquals(401, res.getStatusCode().value());
        assertEquals("incorrecta", res.getBody().get("mensaje"));
    }

    @Test
    void handleRolNotFound_retorna404() {
        ResponseEntity<Map<String, Object>> res =
                handler.handleRolNotFound(new RolNotFoundException("rol no encontrado"));
        assertEquals(404, res.getStatusCode().value());
    }

    @Test
    void handleIllegalState_retorna400() {
        ResponseEntity<Map<String, Object>> res =
                handler.handleIllegalState(new IllegalStateException("estado inválido"));
        assertEquals(400, res.getStatusCode().value());
        assertEquals("estado inválido", res.getBody().get("mensaje"));
    }

    @Test
    void handleIllegalArgument_retorna400() {
        ResponseEntity<Map<String, Object>> res =
                handler.handleIllegalArgument(new IllegalArgumentException("argumento inválido"));
        assertEquals(400, res.getStatusCode().value());
        assertEquals("argumento inválido", res.getBody().get("mensaje"));
    }

    @Test
    void handleValidation_retorna400ConCampos() throws Exception {
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

    @Test
    void handlePartidoNotFound_retorna404() {
        ResponseEntity<Map<String, Object>> res =
                handler.handlePartidoNotFound(new PartidoNotFoundException("partido no encontrado"));
        assertEquals(404, res.getStatusCode().value());
    }

    @Test
    void handleEntradaNotFound_retorna404() {
        ResponseEntity<Map<String, Object>> res =
                handler.handleEntradaNotFound(new EntradaNotFoundException("entrada no encontrada"));
        assertEquals(404, res.getStatusCode().value());
    }

    @Test
    void handleCupoNoDisponible_retorna409() {
        ResponseEntity<Map<String, Object>> res =
                handler.handleCupoNoDisponible(new CupoNoDisponibleException("sin cupo"));
        assertEquals(409, res.getStatusCode().value());
    }

    @Test
    void handleLimiteSuperado_retorna429() {
        ResponseEntity<Map<String, Object>> res =
                handler.handleLimiteSuperado(new LimiteSuperadoException("límite superado"));
        assertEquals(429, res.getStatusCode().value());
    }

    @Test
    void handleEstadoInvalido_retorna400() {
        ResponseEntity<Map<String, Object>> res =
                handler.handleEstadoInvalido(new EstadoInvalidoException("estado inválido"));
        assertEquals(400, res.getStatusCode().value());
    }

    @Test
    void handlePagoStripe_retorna402() {
        ResponseEntity<Map<String, Object>> res =
                handler.handlePagoStripe(new PagoStripeException("error stripe"));
        assertEquals(402, res.getStatusCode().value());
    }

    @Test
    void handleMetodoPagoNotFound_retorna404() {
        ResponseEntity<Map<String, Object>> res =
                handler.handleMetodoPagoNotFound(new MetodoPagoNotFoundException("método no encontrado"));
        assertEquals(404, res.getStatusCode().value());
    }

    @Test
    void handleProductoNotFound_retorna404() {
        ResponseEntity<Map<String, Object>> res =
                handler.handleProductoNotFound(new ProductoNotFoundException("producto no encontrado"));
        assertEquals(404, res.getStatusCode().value());
    }

    @Test
    void handleOrdenNotFound_retorna404() {
        ResponseEntity<Map<String, Object>> res =
                handler.handleOrdenNotFound(new OrdenNotFoundException("orden no encontrada"));
        assertEquals(404, res.getStatusCode().value());
    }

    @Test
    void handleStockInsuficiente_retorna409() {
        ResponseEntity<Map<String, Object>> res =
                handler.handleStockInsuficiente(new StockInsuficienteException("sin stock"));
        assertEquals(409, res.getStatusCode().value());
    }

    @Test
    void handleCategoriaNotFound_retorna404() {
        ResponseEntity<Map<String, Object>> res =
                handler.handleCategoriaNotFound(new CategoriaNotFoundException("categoría no encontrada"));
        assertEquals(404, res.getStatusCode().value());
    }

    @Test
    void handleApuestaNotFound_retorna404() {
        ResponseEntity<Map<String, Object>> res =
                handler.handleApuestaNotFound(new ApuestaNotFoundException("apuesta no encontrada"));
        assertEquals(404, res.getStatusCode().value());
    }

    @Test
    void handlePronosticoNotFound_retorna404() {
        ResponseEntity<Map<String, Object>> res =
                handler.handlePronosticoNotFound(new PronosticoNotFoundException("pronóstico no encontrado"));
        assertEquals(404, res.getStatusCode().value());
    }

    @Test
    void handleCodigoInvalido_retorna400() {
        ResponseEntity<Map<String, Object>> res =
                handler.handleCodigoInvalido(new CodigoInvalidoException("código inválido"));
        assertEquals(400, res.getStatusCode().value());
    }

    @Test
    void handleParticipacionNotFound_retorna404() {
        ResponseEntity<Map<String, Object>> res =
                handler.handleParticipacionNotFound(new ParticipacionNotFoundException("participación no encontrada"));
        assertEquals(404, res.getStatusCode().value());
    }

    @Test
    void handleUsuarioYaEnApuesta_retorna409() {
        ResponseEntity<Map<String, Object>> res =
                handler.handleUsuarioYaEnApuesta(new UsuarioYaEnApuestaException("ya está en la apuesta"));
        assertEquals(409, res.getStatusCode().value());
    }

    @Test
    void handleApuestaCerrada_retorna400() {
        ResponseEntity<Map<String, Object>> res =
                handler.handleApuestaCerrada(new ApuestaCerradaException("apuesta cerrada"));
        assertEquals(400, res.getStatusCode().value());
    }

    @Test
    void handleCarritoVacio_retorna400() {
        ResponseEntity<Map<String, Object>> res =
                handler.handleCarritoVacio(new CarritoVacioException("carrito vacío"));
        assertEquals(400, res.getStatusCode().value());
    }

    @Test
    void handleMetodoPagoInvalido_retorna400() {
        ResponseEntity<Map<String, Object>> res =
                handler.handleMetodoPagoInvalido(new MetodoPagoInvalidoException("método inválido"));
        assertEquals(400, res.getStatusCode().value());
    }

    @Test
    void handleItemNotFound_retorna404() {
        ResponseEntity<Map<String, Object>> res =
                handler.handleItemNotFound(new ItemNotFoundException("item no encontrado"));
        assertEquals(404, res.getStatusCode().value());
    }

    @Test
    void handleCategoriaYaExiste_retorna409() {
        ResponseEntity<Map<String, Object>> res =
                handler.handleCategoriaYaExiste(new CategoriaYaExisteException("categoría ya existe"));
        assertEquals(409, res.getStatusCode().value());
    }

    @Test
    void handleCategoriaConProductos_retorna409() {
        ResponseEntity<Map<String, Object>> res =
                handler.handleCategoriaConProductos(new CategoriaConProductosException("tiene productos"));
        assertEquals(409, res.getStatusCode().value());
    }

    @Test
    void handlePartidoYaIniciado_retorna400() {
        ResponseEntity<Map<String, Object>> res =
                handler.handlePartidoYaIniciado(new PartidoYaIniciadoException("partido ya iniciado"));
        assertEquals(400, res.getStatusCode().value());
    }

    @Test
    void buildResponse_incluyeDateYStatus() {
        ResponseEntity<Map<String, Object>> res =
                handler.handleUsuarioNotFound(new UsuarioNotFoundException("test"));
        assertNotNull(res.getBody().get("date"));
        assertNotNull(res.getBody().get("status"));
        assertNotNull(res.getBody().get("error"));
    }
}