package co.edu.unbosque.mundial_2026.service;

import java.util.List;
import java.util.Map;

import co.edu.unbosque.mundial_2026.dto.request.UsuarioActualizarRequestDTO;
import co.edu.unbosque.mundial_2026.dto.request.UsuarioRequestDTO;
import co.edu.unbosque.mundial_2026.dto.response.PreferenciaDTO;
import co.edu.unbosque.mundial_2026.dto.response.UsuarioResponseDTO;
import co.edu.unbosque.mundial_2026.entity.Usuario;



public interface UsuarioService {
    List<UsuarioResponseDTO> listarTodos();
    UsuarioResponseDTO registrarUsuario(UsuarioRequestDTO dto);
    UsuarioResponseDTO obtenerUsuario(Long usuarioId);
    UsuarioResponseDTO obtenerPorCorreo(String correo);
    void eliminarUsuario(Long usuarioId);
    Map<String, Object> actualizarPerfil(String correoUsuario, UsuarioActualizarRequestDTO dto);

  void agregarSeleccion(String correo, List<Long> ids);
void agregarEstadio(String correo, List<Long> ids);
void agregarCiudad(String correo, List<Long> ids);
    List<PreferenciaDTO> seleccionesUsuario(String correo);
  
    void eliminarSeleccion(String correo, Long seleccionId);

    List<PreferenciaDTO> estadiosUsuario(String correo);
   
    void eliminarEstadio(String correo, Long estadioId);

   
    List<PreferenciaDTO> ciudadesUsuario(String correo);
  
    void eliminarCiudad(String correo, Long ciudadId);

    List<PreferenciaDTO> listarEstadios();
    List<PreferenciaDTO> listarCiudades();
    Usuario obtenerEntidadPorId(Long usuarioId);
    Usuario obtenerEntidadPorCorreo(String correo);
    void actualizarFcmToken(String correo, String fcmToken);
    
}