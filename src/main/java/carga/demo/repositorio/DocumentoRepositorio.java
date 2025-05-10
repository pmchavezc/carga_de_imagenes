package carga.demo.repositorio;

import carga.demo.modelo.Documento;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentoRepositorio extends JpaRepository<Documento, Long> {
}
