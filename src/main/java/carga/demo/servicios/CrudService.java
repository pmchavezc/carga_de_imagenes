package carga.demo.servicios;

import java.util.List;

public interface CrudService<T> {

    List<T> listarTodos();
    T buscarPorId(Long id);
    T guardar(T entidad);
    void eliminar(Long id);
    void actualizar(T entidad);
}
