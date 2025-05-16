package carga.demo.repositorio;

import carga.demo.modelo.Documento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.util.List;

@Repository
public interface DocumentoRepositorio extends JpaRepository<Documento, Long> {

    // Método para buscar un documento por su número de documento

    Documento findDocumentoByNumero(int numero);  // Asegúrate de que esta sea la propiedad correcta

    // colocamos para poder hacer la consulta de los propietarios
    @Query("SELECT DISTINCT d.propietario FROM Documento d")
    List<String> findDistinctPropietarios();

    List<Documento> findByPropietarioAndFechaElaboracionBetween(String propietario, Date startDate, Date endDate);
}
