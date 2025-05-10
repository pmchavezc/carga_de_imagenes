package carga.demo.modelo;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;


@Getter
@Setter
@DynamicInsert
@DynamicUpdate
@Entity
@Table(name = "clientes")
public class Cliente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty(message = "El número de CUI no puede estar vacío")
    @Size(min = 13, max = 13, message = "El número de CUI debe tener 13 dígitos")
    private String cui;

    @NotEmpty(message = "El número de NIT no puede estar vacío")
    @Size(min = 9, max = 9, message = "El número de NIT debe tener 9 dígitos")
    private String nit;

    @NotEmpty(message = "El nombre no puede estar vacío")
    private String nombre;

    @NotEmpty(message = "El apellido no puede estar vacío")
    private String apellido;

    @NotEmpty(message = "La dirección no puede estar vacía")
    private String direccion;

    @NotEmpty(message = "El teléfono no puede estar vacío")
    @Size(min = 8, max = 8, message = "El número de teléfono debe tener 8 dígitos")
    private String telefono;

    @NotEmpty(message = "El genero no puede estar vacío")
    private String genero;

    @NotNull(message = "El usuario no puede estar vacío")
    @OneToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

}
