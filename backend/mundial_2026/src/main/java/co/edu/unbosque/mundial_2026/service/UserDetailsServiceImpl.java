package co.edu.unbosque.mundial_2026.service;

import java.util.List;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import co.edu.unbosque.mundial_2026.entity.Usuario;
import co.edu.unbosque.mundial_2026.repository.UsuarioRepository;

/**
 * Implementación de {@link UserDetailsService} que permite a Spring Security
 * cargar los datos de un usuario desde la base de datos durante el proceso de autenticación
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    /**
     * Inyecta el repositorio de usuarios necesario para consultar las credenciales
     *
     * @param usuarioRepository repositorio JPA de {@link Usuario}
     */
    public UserDetailsServiceImpl(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    /**
     * Busca al usuario por correo electrónico y construye el objeto {@link UserDetails}
     * que Spring Security usa para validar credenciales y asignar permisos
     *
     * <p>Lanza {@link UsernameNotFoundException} si el usuario no existe en la base
     * de datos o si su cuenta está marcada como inactiva, impidiendo el login
     * sin revelar cuál de las dos condiciones falló</p>
     *
     * @param correo correo electrónico del usuario que intenta autenticarse
     * @return objeto {@link UserDetails} con correo, contraseña cifrada y rol asignado
     * @throws UsernameNotFoundException si el usuario no existe o está inactivo
     */
    @Override
    public UserDetails loadUserByUsername(String correo) throws UsernameNotFoundException {
        final Usuario usuario = usuarioRepository.findByCorreoUsuarioConRol(correo)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + correo));

        if (!usuario.isActivo()) {
            throw new UsernameNotFoundException("Usuario inactivo");
        }

        return new User(
                usuario.getCorreoUsuario(),
                usuario.getContrasena(),
                List.of(new SimpleGrantedAuthority(usuario.getRol().getNombre())));
    }
}