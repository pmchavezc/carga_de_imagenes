package carga.demo.providers;

import carga.demo.modelo.Usuario;
import carga.demo.servicios.UsuarioServiceImpl;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component  // Asegúrate de marcarlo como un Bean para que Spring lo administre
public class SecurityCurrentUserProvider implements CurrentUserProvider {

    private final UsuarioServiceImpl userService;

    // Constructor que recibe el servicio UsuarioServiceImpl
    public SecurityCurrentUserProvider(UsuarioServiceImpl userService) {
        this.userService = userService;
    }

    // Método para obtener el ID del usuario actual
    @Override
    public Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            String userName = authentication.getName();
            // Usamos el correo para buscar al usuario en la base de datos
            Usuario usuario = userService.buscarPorCorreo(userName);
            if (usuario != null) {
                return usuario.getId();  // Devuelve el ID del usuario
            }
        }
        return null;  // Si no se encuentra al usuario o no está autenticado, retorna null
    }
}
