package carga.demo.servicios;

import carga.demo.modelo.Rol;
import carga.demo.repositorio.RolRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RolServiceImpl implements CrudService<Rol>{

    @Autowired
    private RolRepositorio rolRepositorio;

    @Cacheable("roles")
    @Override
    public List<Rol> listarTodos() {
        return rolRepositorio.findAll();
    }

    @Override
    public Rol buscarPorId(Long id) {
        return listarTodos()
                .stream()
                .filter(rol -> rol.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    @Override
    public Rol guardar(Rol entidad) {
       throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void eliminar(Long id) {
       throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void actualizar(Rol entidad) {

    }
}
