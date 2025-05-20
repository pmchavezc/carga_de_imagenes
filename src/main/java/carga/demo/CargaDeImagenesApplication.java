package carga.demo;

import jakarta.jws.WebService;
import jakarta.servlet.annotation.WebServlet;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
@WebService
@WebServlet
@SpringBootApplication
public class CargaDeImagenesApplication extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(CargaDeImagenesApplication.class, args);
	}

}
