package carga.demo.controllers;

import carga.demo.modelo.Documento;
import carga.demo.servicios.DocumentoServiceImpl;
import carga.demo.servicios.UsuarioServiceImpl;
import carga.demo.providers.CurrentUserProvider;  // Importar para obtener el ID del usuario
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

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

    // Inyectamos para obtener el ID del usuario logueado
    @Autowired
    private CurrentUserProvider currentUserProvider;
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
            return "Auth/oficial";
        }

        // Convertir java.util.Date a java.sql.Date
        java.sql.Date sqlFechaInicio = new java.sql.Date(fechaInicio.getTime());
        java.sql.Date sqlFechaFinal = new java.sql.Date(fechaFinal.getTime());

        // Obtener los documentos filtrados
        List<Documento> documentos = documentoServiceImpl.getDocumentosPorFechasYPropietario(propietario, sqlFechaInicio, sqlFechaFinal);

        // Verificar si no hay documentos
        if (documentos.isEmpty()) {
            model.addAttribute("error", "No se encontraron documentos para los parámetros dados.");
            return "Auth/oficial";
        }

        // Pasar los documentos al modelo
        model.addAttribute("propietario", propietario);
        model.addAttribute("fechaInicio", fechaInicio);
        model.addAttribute("fechaFinal", fechaFinal);
        model.addAttribute("documentos", documentos);

        return "Auth/descarga";  // Vista que muestra los documentos
    }
    // Ver documento individual
    @GetMapping("/verDocumento")
    public void verDocumento(@RequestParam Long id, HttpServletResponse response) throws IOException {
        Documento doc = documentoServiceImpl.buscarPorId(id);

        if (doc == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Documento no encontrado");
            return;
        }

        // Ajusta aquí la ruta absoluta donde están guardados tus PDFs
        String basePath = "uploads/";
        String fileName = doc.getAdjuntar_Archivo().trim();

        Path file = Paths.get(basePath, fileName);
        if (!Files.exists(file)) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Archivo no encontrado en el servidor");
            return;
        }

        // Establece Content-Type correcto para PDF
        response.setContentType("application/pdf");

        // Indica inline para que el navegador muestre el PDF en la pestaña (no como descarga)
        response.setHeader("Content-Disposition", "inline; filename=\"" + fileName + "\"");

        try (InputStream fis = Files.newInputStream(file);
             OutputStream os = response.getOutputStream()) {

            byte[] buffer = new byte[4096];
            int length;
            while ((length = fis.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }
            os.flush();
        }
    }

    // Descargar documento individual
    @GetMapping("/descargarDocumento")
    public void descargarDocumento(@RequestParam Long id, HttpServletResponse response) {
        try {
            Documento doc = documentoServiceImpl.buscarPorId(id);

            if (doc == null) {
                System.out.println(" Documento no encontrado para ID: " + id);
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Documento no encontrado");
                return;
            }

            String basePath = "uploads/";  // Ruta absoluta correcta
            String fileName = doc.getAdjuntar_Archivo().trim();

            Path file = Paths.get(basePath, fileName);

            System.out.println("Ruta completa del archivo: " + file.toAbsolutePath());
            System.out.println("El archivo existe? " + Files.exists(file));

            if (!Files.exists(file)) {
                System.out.println(" Archivo no encontrado en el servidor: " + file.toAbsolutePath());
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Archivo no encontrado en el servidor");
                return;
            }

            response.setContentType(Files.probeContentType(file));
            response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");

            try (InputStream fis = Files.newInputStream(file);
                 OutputStream os = response.getOutputStream()) {

                byte[] buffer = new byte[4096];
                int length;
                while ((length = fis.read(buffer)) > 0) {
                    os.write(buffer, 0, length);
                }
                os.flush();
                System.out.println(" Descarga completada.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


     // Descarga de todos los documentos en un ZIP

    @GetMapping("/descargarTodos")
    public void descargarTodos(
            @RequestParam String propietario,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") java.util.Date fechaInicio,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") java.util.Date fechaFinal,
            HttpServletResponse response) {

        try {
            System.out.println("=== Iniciando descarga masiva ===");

            java.sql.Date sqlFechaInicio = new java.sql.Date(fechaInicio.getTime());
            java.sql.Date sqlFechaFinal = new java.sql.Date(fechaFinal.getTime());

            List<Documento> documentos = documentoServiceImpl.getDocumentosPorFechasYPropietario(propietario, sqlFechaInicio, sqlFechaFinal);

            if (documentos.isEmpty()) {
                System.out.println(" No se encontraron documentos.");
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
                    } else {
                        System.out.println(" Archivo no encontrado en el servidor: " + filePath.toAbsolutePath());
                    }
                }
                zos.finish();
                System.out.println(" Descarga masiva completada.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    }
