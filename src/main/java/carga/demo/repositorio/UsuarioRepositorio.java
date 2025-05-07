package carga.demo.repositorio;


import carga.demo.modelo.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepositorio extends JpaRepository<Usuario, Long> {
    Usuario findByCorreo(String correo);
}
