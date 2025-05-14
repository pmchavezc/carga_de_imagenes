package carga.demo.repositorio;

import carga.demo.modelo.Documento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DocumentoRepositorio extends JpaRepository<Documento, Long> {

    // Método para buscar un documento por su número de documento

    Documento findDocumentoByNumero(int numero);  // Asegúrate de que esta sea la propiedad correcta

}
