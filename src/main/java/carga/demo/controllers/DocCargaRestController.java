package carga.demo.controllers;

import carga.demo.Utils;
import carga.demo.modelo.Documento;
import carga.demo.modelo.ResponseWrapper;
import carga.demo.modelo.Usuario;
import carga.demo.servicios.DocumentoServiceImpl;
import carga.demo.servicios.UsuarioServiceImpl;
import carga.demo.providers.CurrentUserProvider;  // Importar para obtener el ID del usuario
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;


@RestController
public class DocCargaRestController {

    @Autowired
    private DocumentoServiceImpl documentoService;

    @Autowired
    private UsuarioServiceImpl usuarioService;

    @Autowired
    private CurrentUserProvider currentUserProvider;  // Inyectamos para obtener el ID del usuario logueado
    @Autowired
    private DocumentoServiceImpl documentoServiceImpl;

    // Tamaño máximo del archivo en bytes
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;

    @GetMapping("/verificarDocumento")
    @ResponseBody
    public Map<String, Boolean> verificarDocumento(@RequestParam("numeroDoc") String numeroDoc) {
        Map<String, Boolean> response = new HashMap<>();
        boolean exists = documentoService.existeDocumento(numeroDoc); // Verifica en la base de datos
        response.put("exists", exists);
        return response;
    }

    @PostMapping("/cargar_datos")
    public ResponseEntity<ResponseWrapper> cargarDatos(
            @RequestParam("numeroDoc") String numeroDoc,
            @RequestParam("fechaElaboracion") @DateTimeFormat(pattern = "yyyy-MM-dd") Date fechaElaboracion,
            @RequestParam("fechaIngreso") @DateTimeFormat(pattern = "yyyy-MM-dd") Date fechaIngreso,
            @RequestParam("propietarioDoc") String propietarioDoc,
            @RequestParam("adjuntarPdf") MultipartFile adjuntarPdf,
            HttpServletRequest request
    ){

        //Validación de la fecha de elaboración no sea mayor que la fecha de ingreso
        if (fechaElaboracion.after(fechaIngreso)) {
            return ResponseEntity
                    .badRequest()
                    .body(ResponseWrapper.error("La fecha de elaboración no puede ser mayor a la fecha de ingreso"));  // Redirigir a una página de error o mostrar mensaje de error
        }

        // Validar que el archivo no sea mayor a 10 MB
        if (adjuntarPdf.getSize() > MAX_FILE_SIZE) {
            return ResponseEntity
                    .badRequest()
                    .body(ResponseWrapper.error("El archivo debe ser menor a 10 MB"));  // Redirigir a una página de error o mostrar mensaje de error
        }

        // Validación para evitar duplicación de documentos
        if (documentoService.existeDocumento(numeroDoc)) {
            return ResponseEntity
                    .badRequest()
                    .body(ResponseWrapper.error("El número de documento ya existe"));  // Redirigir a una página de error o mostrar mensaje de error
        }

        // Obtener el ID del usuario logueado
        Long userId = currentUserProvider.getCurrentUserId();  // Usamos el método para obtener el ID del usuario autenticado

        if (userId == null) {
            return ResponseEntity
                    .badRequest()
                    .body(ResponseWrapper.error("Usuario no valido"));  // Si no se puede obtener el ID del usuario, redirigir a error
        }

        // Obtener la IP del usuario
        String ipUsuario = obtenerIpUsuario(request);  // Usamos la función para obtener la IP

        // Guardar el archivo PDF
        String fileName = adjuntarPdf.getOriginalFilename();
        String uploadDir = "uploads/";  // Directorio donde guardamos los archivos
        Path uploadPath = Paths.get(uploadDir);

        if (!Files.exists(uploadPath)) {
            try {
                Files.createDirectories(uploadPath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (adjuntarPdf.isEmpty()) {
            return ResponseEntity
                    .badRequest()
                    .body(ResponseWrapper.error("El archivo está vacío"));
        }

        try {
            if (Utils.isFileEncryptedOrEmptyBody(adjuntarPdf)) {
                return ResponseEntity
                        .badRequest()
                        .body(ResponseWrapper.error("El archivo podría estar encriptado, o su contenido está vacío"));
            }
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseWrapper.error("Error al procesar el archivo PDF"));
        }



        try {
            // Guardamos el archivo en el servidor
            Path filePath = uploadPath.resolve(fileName);
            adjuntarPdf.transferTo(filePath);

            // Creamos un nuevo documento
            Documento documento = new Documento();
            documento.setNumero(Integer.parseInt(numeroDoc));  // nos aseguramos que el número de documento sea un valor válido
            documento.setFechaElaboracion(fechaElaboracion);
            documento.setFecha_ingreso(fechaIngreso);
            documento.setPropietario(propietarioDoc);
            documento.setAdjuntar_Archivo(fileName);  // guardamos solo el nombre del archivo
            documento.setTamaño_archivo(String.valueOf(adjuntarPdf.getSize()));  // Tamaño del archivo
            documento.setIp_usuario(ipUsuario);  // obtenemos la IP real del usuario
            documento.setAccion("Cargar");  // El tipo de acción
            // Asignar el usuario al documento
            Usuario usuario = usuarioService.buscarPorId(userId);
            documento.setUsuario(usuario);

            documentoService.guardarDocumento(documento);  // Guardamos el documento en la base de datos

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity
                    .badRequest()
                    .body(ResponseWrapper.error("Error al guardar el archivo"));  // Devuelve un error si ocurre algún problema al guardar el archivo
        }

        return ResponseEntity
                .badRequest()
                .body(ResponseWrapper.error("Archivo guardado exitosamente"));  // Redirigir a otra página, como la página de oficial
    }

    private String obtenerIpUsuario(HttpServletRequest request) {
        // Verificar si la solicitud contiene el encabezado X-Forwarded-For (para casos de proxies o balanceadores de carga)
        String ip = request.getHeader("X-Forwarded-For");

        if (ip == null || ip.isEmpty()) {
            // Si no hay encabezado X-Forwarded-For, obtener la IP directamente desde la solicitud
            ip = request.getRemoteAddr();
        }

        // Si hay una lista de IPs separadas por comas en X-Forwarded-For, tomamos la primera
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0];  // Tomamos la primera IP
        }

        // Retornar la IP del cliente
        return ip;
    }
}
