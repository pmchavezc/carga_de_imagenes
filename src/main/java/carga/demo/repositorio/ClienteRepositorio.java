package carga.demo.repositorio;

import carga.demo.modelo.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClienteRepositorio extends JpaRepository<Cliente, Long> {
    Cliente findByUsuarioId(Long id);
    Cliente findByCui(String cui);
    Cliente findByNit(String nit);
}
