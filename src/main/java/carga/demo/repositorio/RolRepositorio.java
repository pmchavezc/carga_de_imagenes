package carga.demo.repositorio;


import carga.demo.modelo.Rol;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RolRepositorio extends JpaRepository<Rol,Long> {
}
