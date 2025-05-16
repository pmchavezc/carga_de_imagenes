package carga.demo;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Random;

public class Utils {

    public static String encriptarContrasenia(String password) {
        return new BCryptPasswordEncoder().encode(password);
    }

    public static String generarNumeroDeExpediente() {
        Random random = new Random();
        int firstGroup = random.nextInt(10000); // Generate a random number between 0 and 9999
        int secondGroup = random.nextInt(100);
        int thirdGroup = random.nextInt(100);
        int fourthGroup = random.nextInt(100);
        int fifthGroup = random.nextInt(10000000); // Generate a random number between 0 and 9999999
        return String.format("%04d-%02d-%02d-%02d-%07d", firstGroup, secondGroup, thirdGroup, fourthGroup, fifthGroup);
    }

    public static String generarNumeroSolicitudRandom(String tipoUsuario) {
        Date dateOfReception = new Date();
        Random random = new Random();
        String letters = tipoUsuario.equalsIgnoreCase("externo") ? "EX" : "IN";
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        String date = dateFormat.format(dateOfReception);
        int numbers = random.nextInt(100000);
        return String.format("%s-%s-%05d", letters, date, numbers);
    }

    public static String generarNumeroMuestraRandom() {
        String letras = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        Random random = new Random();
        StringBuilder muestra = new StringBuilder();
        // Generar 5 letras aleatorias
        for (int i = 0; i < 5; i++) {
            muestra.append(letras.charAt(random.nextInt(letras.length())));
        }
        // Generar 1 o 2 letras aleatorias y 1 o 2 números aleatorios
        int cantidadLetras = random.nextBoolean() ? 1 : 2;
        for (int i = 0; i < cantidadLetras; i++) {
            muestra.append(random.nextBoolean() ? letras.charAt(random.nextInt(letras.length())) : random.nextInt(10));
        }
        // Generar 2 números aleatorios
        muestra.append(String.format("%02d", random.nextInt(100)));

        // Generar 6 números aleatorios
        muestra.append(String.format("%06d", random.nextInt(1000000)));
        return muestra.insert(5, '-').insert(8, '-').toString();
    }

    public static Date convertirFecha(String fechaString) {

        // Especifica el patrón de formato de la cadena de fecha
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        try {
            // Convierte la cadena de fecha a un objeto LocalDate
            LocalDate fechaIngresada = LocalDate.parse(fechaString, formatter);

            // Obtiene la fecha actual
            LocalDate fechaHoy = LocalDate.now();

            // Compara las fechas
            if (fechaIngresada.isEqual(fechaHoy) || fechaIngresada.isAfter(fechaHoy)) {
                return convertirLocalDateADate(fechaIngresada);
            } else {
                return null;
            }
        } catch (Exception e) {
           return null; // Manejo de la excepción en caso de que la cadena de fecha sea inválida
        }
    }

    public static Date convertirLocalDateADate(LocalDate localDate) {
        // Convierte de LocalDate a Date
        return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    public static boolean isFileEncryptedOrEmptyBody(MultipartFile file) throws IOException {
        File tempFile = File.createTempFile("temp", ".pdf");
        file.transferTo(tempFile);
        try (PDDocument pdDocument = PDDocument.load(new File(tempFile.getAbsolutePath()))) {
            if (!pdDocument.isEncrypted()) {
                return isFileEmptyBody(pdDocument);
            }else {
                return true;
            }
        } catch (Exception e) {
            return true;
        }
    }

    public static boolean isFileEmptyBody(PDDocument file) throws IOException {
        try  {
            PDFTextStripper pdfTextStripper = new PDFTextStripper();
            String text = pdfTextStripper.getText(file);
            return text.trim().isEmpty();
        } catch (Exception e) {
            return true;
        }
    }


}
