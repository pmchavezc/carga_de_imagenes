package carga.demo.controllers;

import carga.demo.modelo.Documento;
import carga.demo.modelo.Usuario;
import carga.demo.servicios.DocumentoServiceImpl;
import carga.demo.servicios.UsuarioServiceImpl;
import carga.demo.providers.CurrentUserProvider;  // Importar para obtener el ID del usuario
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Controller
public class DocumentoController {

    @Autowired
    private DocumentoServiceImpl documentoService;

    @Autowired
    private UsuarioServiceImpl usuarioService;

    @Autowired
    private CurrentUserProvider currentUserProvider;  // Inyectamos para obtener el ID del usuario logueado
    @Autowired
    private DocumentoServiceImpl documentoServiceImpl;

    // Obtener lista de propietarios
    @GetMapping("/getPropietarios")
    @ResponseBody
    public List<String> getPropietarios() {
        return documentoServiceImpl.gerPropietarios();
    }

    @GetMapping("/getDocumentos")
    public String getDocumentos(
            @RequestParam String propietario,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date fechaInicio,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date fechaFinal,
            Model model) {

        // Verificar que las fechas sean correctas
        if (fechaInicio.after(fechaFinal)) {
            model.addAttribute("error", "La fecha de inicio no puede ser mayor que la fecha final.");
            return "errorPage";  // Redirigir a una página de error si las fechas son incorrectas
        }

        // Convertir java.util.Date a java.sql.Date
        java.sql.Date sqlFechaInicio = new java.sql.Date(fechaInicio.getTime());
        java.sql.Date sqlFechaFinal = new java.sql.Date(fechaFinal.getTime());

        // Obtener los documentos filtrados
        List<Documento> documentos = documentoServiceImpl.getDocumentosPorFechasYPropietario(propietario, sqlFechaInicio, sqlFechaFinal);

        // Verificar si no hay documentos
        if (documentos.isEmpty()) {
            model.addAttribute("error", "No se encontraron documentos para los parámetros dados.");
            return "errorPage";  // Página de error si no hay documentos
        }

        // Pasar los documentos al modelo
        model.addAttribute("propietario", propietario);
        model.addAttribute("fechaInicio", fechaInicio);
        model.addAttribute("fechaFinal", fechaFinal);
        model.addAttribute("documentos", documentos);

        return "Auth/Descarga";  // Vista que muestra los documentos
    }


    // Descargar documento individual
    @GetMapping("/descargarDocumento")
    public void descargarDocumento(@RequestParam Long id, HttpServletResponse response) {
        try {
            Documento doc = documentoServiceImpl.buscarPorId(id);

            if (doc == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Documento no encontrado");
                return;
            }

            Path file = Paths.get("uploads/").resolve(doc.getAdjuntar_Archivo());

            if (!Files.exists(file)) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Archivo no encontrado en el servidor");
                return;
            }

            String mimeType = Files.probeContentType(file);
            if (mimeType == null) {
                mimeType = "application/octet-stream";
            }

            response.setContentType(mimeType);
            response.setHeader("Content-Disposition", "attachment; filename=\"" + doc.getAdjuntar_Archivo() + "\"");

            try (InputStream fis = Files.newInputStream(file);
                 OutputStream os = response.getOutputStream()) {

                byte[] buffer = new byte[4096];
                int length;
                while ((length = fis.read(buffer)) > 0) {
                    os.write(buffer, 0, length);
                }
                os.flush();
            }
        } catch (IOException e) {
            try {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error al descargar el archivo");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }


    // Descargar todos los documentos en un archivo ZIP
    @GetMapping("/descargarTodos")
    public void descargarTodos(
            @RequestParam String propietario,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date fechaInicio,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date fechaFinal,
            HttpServletResponse response) {

        try {
            List<Documento> documentos = documentoServiceImpl.getDocumentosPorFechasYPropietario(propietario, (java.sql.Date) fechaInicio, (java.sql.Date) fechaFinal);

            if (documentos.isEmpty()) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "No se encontraron documentos para los criterios seleccionados");
                return;
            }

            String zipFileName = "documentos_" + propietario + ".zip";

            response.setContentType("application/zip");
            response.setHeader("Content-Disposition", "attachment; filename=\"" + zipFileName + "\"");

            try (ZipOutputStream zos = new ZipOutputStream(response.getOutputStream())) {
                for (Documento doc : documentos) {
                    Path filePath = Paths.get("uploads/").resolve(doc.getAdjuntar_Archivo());

                    if (Files.exists(filePath)) {
                        zos.putNextEntry(new ZipEntry(doc.getAdjuntar_Archivo()));

                        try (InputStream fis = Files.newInputStream(filePath)) {
                            byte[] buffer = new byte[4096];
                            int length;
                            while ((length = fis.read(buffer)) > 0) {
                                zos.write(buffer, 0, length);
                            }
                        }

                        zos.closeEntry();
                    }
                }
                zos.finish();
            }
        } catch (IOException e) {
            try {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error al crear el ZIP");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }




    @GetMapping("/verificarDocumento")
    @ResponseBody
    public Map<String, Boolean> verificarDocumento(@RequestParam("numeroDoc") String numeroDoc) {
        Map<String, Boolean> response = new HashMap<>();
        boolean exists = documentoService.existeDocumento(numeroDoc); // Verifica en la base de datos
        response.put("exists", exists);
        return response;
    }

    // Tamaño máximo del archivo en bytes
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

        // 5. Obtener el ID del usuario logueado
        Long userId = currentUserProvider.getCurrentUserId();  // Usamos el método para obtener el ID del usuario autenticado

        if (userId == null) {
            return "error";  // Si no se puede obtener el ID del usuario, redirigir a error
        }

        // 6. Obtener la IP del usuario
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
            documento.setFechaElaboracion(fechaElaboracion);
            documento.setFecha_ingreso(fechaIngreso);
            documento.setPropietario(propietarioDoc);
            documento.setAdjuntar_Archivo(fileName);  // Guardar solo el nombre del archivo
            documento.setTamaño_archivo(String.valueOf(adjuntarPdf.getSize()));  // Tamaño del archivo
            documento.setIp_usuario(ipUsuario);  // Aquí deberías obtener la IP real del usuario
            documento.setAccion("Cargar");  // El tipo de acción
            // Asignar el usuario al documento
            Usuario usuario = usuarioService.buscarPorId(userId);
            documento.setUsuario(usuario);

            documentoService.guardarDocumento(documento);  // Guardar el documento en la base de datos

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
