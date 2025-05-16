package carga.demo.modelo;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.util.Date;

@Getter
@Setter
@DynamicInsert
@DynamicUpdate
@Entity
@Table(name = "documentos")

public class Documento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty(message = "El propietario no puede estar vacía")
    @Column (nullable = false)
    private String propietario;

    //este es el numero de documento
    @Column(name = "numero")
    private int numero;

    @Column(name = "fecha_elaboracion", nullable = false)
    private Date fechaElaboracion;

    @Column(nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fecha_ingreso;

    @NotEmpty (message = "el archivo no puede estar vacía")
    private String adjuntar_Archivo;
    private String tamaño_archivo;

    @Column(nullable = false)
    private String ip_usuario;

    private String accion;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;


}
