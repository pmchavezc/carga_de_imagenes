package carga.demo.servicios;

import carga.demo.modelo.Usuario;
import carga.demo.repositorio.UsuarioRepositorio;
import jakarta.transaction.Transactional;
import org.springframework.data.annotation.ReadOnlyProperty;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class UsuarioServiceImpl implements CrudService<Usuario>{

    private final UsuarioRepositorio usuarioRepositorio;

    public UsuarioServiceImpl(UsuarioRepositorio usuarioRepositorio) {

        this.usuarioRepositorio = usuarioRepositorio;
    }
    @Transactional
    @ReadOnlyProperty
    public Usuario buscarPorCorreo(String correo) {return usuarioRepositorio.findByCorreo(correo);}

    @Override
    public List<Usuario> listarTodos() {
        return usuarioRepositorio.findAll();
    }

    @Override
    public Usuario buscarPorId(Long id) {
        return usuarioRepositorio.findById(id).orElse(null);
    }

    @Override
    public Usuario guardar(Usuario entidad) {
        return usuarioRepositorio.save(entidad);
    }



    @Override
    public void eliminar(Long id) {

    }

    @Override
    public void actualizar(Usuario entidad) {

    }


}
