package carga.demo.servicios;

import carga.demo.modelo.Documento;
import carga.demo.repositorio.DocumentoRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.List;

@Service
public class DocumentoServiceImpl implements CrudService<Documento> {

    @Autowired
    private DocumentoRepositorio documentoRepositorio;

    public boolean existeDocumento(String numero) {
        // Aquí buscamos el documento por número de documento
        return documentoRepositorio.findDocumentoByNumero(Integer.parseInt(numero)) != null;
    }

    public void guardarDocumento(Documento documento) {
        documentoRepositorio.save(documento);
    }


    // metodo para los metodos de buscar los propietarario
    public List<String> gerPropietarios() {
        return documentoRepositorio.findDistinctPropietarios();
    }

    // metodo para buscar los documentos por propietario y rango de fecha
    public List<Documento> getDocumentosPorFechasYPropietario(String propietario, Date fechaInicio, Date fechaFin) {
        return documentoRepositorio.findByPropietarioAndFechaElaboracionBetween(propietario, fechaInicio, fechaFin);
    }

    @Override
    public List<Documento> listarTodos() {
        return List.of();
    }

    @Override
    public Documento buscarPorId(Long id) {
        return documentoRepositorio.findById(id).orElse(null);
    }

    @Override
    public Documento guardar(Documento documento) {
        return null;
    }

    @Override
    public void eliminar(Long id) {

    }

    @Override
    public void actualizar(Documento entidad) {

    }

}
