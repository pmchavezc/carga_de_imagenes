package carga.demo.controllers;

import carga.demo.modelo.Documento;
import carga.demo.servicios.DocumentoServiceImpl;
import carga.demo.servicios.UsuarioServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;

@Controller
public class DocumentoController {

    @Autowired
    private DocumentoServiceImpl documentoService;

    @Autowired
    private UsuarioServiceImpl usuarioService;

    @PostMapping("/cargar_datos")
    public String cargarDatos(
            @RequestParam("numeroDoc") String numeroDoc,
            @RequestParam("fechaElaboracion") @DateTimeFormat(pattern = "yyyy-MM-dd") Date fechaElaboracion,
            @RequestParam("fechaIngreso") @DateTimeFormat(pattern = "yyyy-MM-dd") Date fechaIngreso,
            @RequestParam("propietarioDoc") String propietarioDoc,
            @RequestParam("adjuntarPdf") MultipartFile adjuntarPdf){

        // Guardar el archivo PDF
        String fileName = adjuntarPdf.getOriginalFilename();
        String uploadDir = "uploads/";  // Directorio donde guardarás los archivos
        Path uploadPath = Paths.get(uploadDir);

        if (!Files.exists(uploadPath)) {
            try {
                Files.createDirectories(uploadPath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            // Guardar el archivo en el servidor
            Path filePath = uploadPath.resolve(fileName);
            adjuntarPdf.transferTo(filePath);

            // Crear un nuevo documento
            Documento documento = new Documento();
            documento.setNumero_documento(Integer.parseInt(numeroDoc));  // Asegúrate de que el número de documento sea un valor válido
            documento.setFecha_elaboracion(fechaElaboracion);
            documento.setFecha_ingreso(fechaIngreso);
            documento.setPropietario(propietarioDoc);
            documento.setAdjuntar_Archivo(fileName);  // Guardar solo el nombre del archivo
            documento.setTamaño_archivo(String.valueOf(adjuntarPdf.getSize()));  // Tamaño del archivo
            documento.setIp_usuario("127.0.0.1");  // Aquí deberías obtener la IP real del usuario
            documento.setAccion("Cargar");  // El tipo de acción

            // Asociar usuario (si es necesario, puedes buscar el usuario por su ID)
            // documento.setUsuario(usuarioService.buscarPorId(usuarioId));

            // Guardar en la base de datos
            documentoService.guardarDocumento(documento);

        } catch (IOException e) {
            e.printStackTrace();
            return "error";  // Devuelve un error si ocurre algún problema al guardar el archivo
        }

        return "redirect:/oficial";  // Redirigir a otra página, como la página de oficial
    }
}
