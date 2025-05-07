package carga.demo.controllers;

import carga.demo.modelo.Cliente;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@Slf4j
public class AuthController {

    @GetMapping("/login")
    public String Login(){
        return "Auth/Login";
    }

    @GetMapping("/register")
    public String Register(Cliente cliente){
        return "Auth/Register";
    }
}
