package carga.demo.controllers;

import carga.demo.modelo.Documento;
import carga.demo.servicios.DocumentoServiceImpl;
import carga.demo.servicios.UsuarioServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Controller
public class DocumentoController {

    @Autowired
    private DocumentoServiceImpl documentoService;

    @Autowired
    private UsuarioServiceImpl usuarioService;

    @GetMapping("/verificarDocumento")
    @ResponseBody
    public Map<String, Boolean> verificarDocumento(@RequestParam("numeroDoc") String numeroDoc) {
        Map<String, Boolean> response = new HashMap<>();
        boolean exists = documentoService.existeDocumento(numeroDoc); // Verifica en la base de datos
        response.put("exists", exists);
        return response;
    }

    //tamaño máximo del archivo en bytes
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;

    @PostMapping("/cargar_datos")
    public String cargarDatos(
            @RequestParam("numeroDoc") String numeroDoc,
            @RequestParam("fechaElaboracion") @DateTimeFormat(pattern = "yyyy-MM-dd") Date fechaElaboracion,
            @RequestParam("fechaIngreso") @DateTimeFormat(pattern = "yyyy-MM-dd") Date fechaIngreso,
            @RequestParam("propietarioDoc") String propietarioDoc,
            @RequestParam("adjuntarPdf") MultipartFile adjuntarPdf,
            HttpServletRequest request
    ){

            // 1. Validación de la fecha de elaboración no sea mayor que la fecha de ingreso
            if (fechaElaboracion.after(fechaIngreso)) {
                return "error";  // Redirigir a una página de error o mostrar mensaje de error
            }

            // 2. Validar que el archivo no sea mayor a 10 MB
            if (adjuntarPdf.getSize() > MAX_FILE_SIZE) {
                return "error";  // Redirigir a una página de error o mostrar mensaje de error
            }

            // 3. Validación para evitar duplicación de documentos
            if (documentoService.existeDocumento(numeroDoc)) {
                return "error";  // Redirigir a una página de error o mostrar mensaje de error
            }


        // 5. Obtener la IP del usuario
        String ipUsuario = obtenerIpUsuario(request);  // Usar la función para obtener la IP

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
            documento.setNumero(Integer.parseInt(numeroDoc));  // Asegúrate de que el número de documento sea un valor válido
            documento.setFecha_elaboracion(fechaElaboracion);
            documento.setFecha_ingreso(fechaIngreso);
            documento.setPropietario(propietarioDoc);
            documento.setAdjuntar_Archivo(fileName);  // Guardar solo el nombre del archivo
            documento.setTamaño_archivo(String.valueOf(adjuntarPdf.getSize()));  // Tamaño del archivo
            documento.setIp_usuario(ipUsuario);  // Aquí deberías obtener la IP real del usuario
            documento.setAccion("Cargar");  // El tipo de acción
            // Asignar el usuario al documento

            documentoService.guardarDocumento(documento);

        } catch (IOException e) {
            e.printStackTrace();
            return "error";  // Devuelve un error si ocurre algún problema al guardar el archivo
        }

        return "redirect:/oficial";  // Redirigir a otra página, como la página de oficial
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
