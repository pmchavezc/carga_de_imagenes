package carga.demo.modelo;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
//pruebas para la rama
@Getter
@Setter
@DynamicInsert
@DynamicUpdate
@Entity
@Table(name = "roles")
public class Rol {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;
    private String descripcion;

    @Column(nullable = false, updatable = false, insertable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime fechaCreacion;

    private LocalDateTime fechaActualizacion;
    private Boolean eliminado;

    @OneToMany(mappedBy = "rol")
    private List<Usuario> usuarioList = new ArrayList<>();

}
