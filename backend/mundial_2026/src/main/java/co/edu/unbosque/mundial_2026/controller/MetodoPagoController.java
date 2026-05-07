package co.edu.unbosque.mundial_2026.controller;


import java.util.List;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import co.edu.unbosque.mundial_2026.dto.request.MetodoPagoRequestDTO;
import co.edu.unbosque.mundial_2026.dto.response.MetodoPagoResponseDTO;
import co.edu.unbosque.mundial_2026.service.MetodoPagoService;

@RestController
@RequestMapping("/payments")
public class MetodoPagoController {

    private final MetodoPagoService metodoPagoService;

    public MetodoPagoController(MetodoPagoService metodoPagoService) {
        this.metodoPagoService = metodoPagoService;
    }

    @GetMapping
    public ResponseEntity<List<MetodoPagoResponseDTO>> listar(@RequestParam Long userId) {
        return ResponseEntity.ok(metodoPagoService.listarPorUsuario(userId));
    }

    @PostMapping
    public ResponseEntity<MetodoPagoResponseDTO> agregar(@RequestBody MetodoPagoRequestDTO dto) {
        MetodoPagoResponseDTO resultado = metodoPagoService.agregar(dto);
        if (resultado == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(resultado);
    }

  
}