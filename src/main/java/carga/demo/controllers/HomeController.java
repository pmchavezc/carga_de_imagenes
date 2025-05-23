package carga.demo.controllers;
import carga.demo.servicios.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

@Controller
@Slf4j
public class HomeController {

    @Autowired
    private RolServiceImpl rolService;


    @GetMapping("/")


    public String Login(){
        return "Auth/dashboard";
    }

    @ModelAttribute
    public void defaultAttribute(Model model){
        model.addAttribute("roles", rolService.listarTodos());
    }
}
