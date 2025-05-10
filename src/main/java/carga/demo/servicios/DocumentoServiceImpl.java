package carga.demo.servicios;

import carga.demo.modelo.Documento;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DocumentoServiceImpl implements CrudService<Documento> {

    @Override
    public List<Documento> listarTodos() {
        return List.of();
    }

    @Override
    public Documento buscarPorId(Long id) {
        return null;
    }

    @Override
    public Documento guardar(Documento entidad) {
        return null;
    }

    @Override
    public void eliminar(Long id) {

    }

    @Override
    public void actualizar(Documento entidad) {

    }
}
