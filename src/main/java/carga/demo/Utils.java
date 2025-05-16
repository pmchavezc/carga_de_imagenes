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
