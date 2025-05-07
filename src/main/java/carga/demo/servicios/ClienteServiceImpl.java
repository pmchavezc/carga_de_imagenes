package carga.demo.servicios;

import carga.demo.modelo.Cliente;
import carga.demo.repositorio.ClienteRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClienteServiceImpl implements CrudService<Cliente>{

    @Autowired
    private ClienteRepositorio clienteRepositorio;
    @Override
    public List<Cliente> listarTodos() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Cliente buscarPorId(Long id) {
        return clienteRepositorio.findById(id).orElse(null);
    }

    @Override
    public Cliente guardar(Cliente entidad) {
        System.out.println("Guardando cliente");
        clienteRepositorio.save(entidad);
        return new Cliente();
    }

    @Override
    public void eliminar(Long id) {
       throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void actualizar(Cliente entidad) {

    }

    public Cliente buscarPorUsuarioId(Long id){
        return clienteRepositorio.findByUsuarioId(id);
    }

    public Cliente buscarPorCui(String cui){
        return clienteRepositorio.findByCui(cui);
    }
    public Cliente buscarPorNit(String nit){
        return clienteRepositorio.findByNit(nit);
    }
}
