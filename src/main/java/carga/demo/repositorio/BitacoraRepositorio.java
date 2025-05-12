package carga.demo.repositorio;

import carga.demo.modelo.bitacora;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BitacoraRepositorio extends JpaRepository<bitacora, Long> {

}
