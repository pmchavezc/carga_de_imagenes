package carga.demo.controllers;


import carga.demo.Utils;
import carga.demo.modelo.Cliente;
import carga.demo.modelo.Rol;
import carga.demo.servicios.ClienteServiceImpl;
import carga.demo.servicios.RolServiceImpl;
import carga.demo.servicios.UsuarioServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.HashMap;
import java.util.Map;

@Controller
public class ClienteController {
    @Autowired
    private ClienteServiceImpl clienteService;

    @Autowired
    private RolServiceImpl rolService;

    @Autowired
    private UsuarioServiceImpl usuarioService;

    @PostMapping("/guardarCliente")
    public String guardarCliente(@Valid Cliente cliente, BindingResult bindingResult){
        if(clienteService.buscarPorCui(cliente.getCui()) != null){
            bindingResult.rejectValue("cui", "error.cliente", "Ya existe un cliente con ese CUI");
        }
        if(clienteService.buscarPorNit(cliente.getNit()) != null){
            bindingResult.rejectValue("nit", "error.cliente", "Ya existe un cliente con ese NIT");
        }
        Rol rol = rolService.buscarPorId(2L);
        cliente.getUsuario().setRol(rol);
        cliente.getUsuario().setContrasenia(Utils.encriptarContrasenia(cliente.getUsuario().getContrasenia()));
        cliente.setNumeroExpediente(Utils.generarNumeroDeExpediente());
        cliente.getUsuario().setTipoUsuario("externo");
        cliente.getUsuario().setNombreUsuario(cliente.getNombre() + " " + cliente.getApellido());

        if(bindingResult.hasErrors()){
            return "Auth/Register";
        }

        usuarioService.guardar(cliente.getUsuario());
        clienteService.guardar(cliente);
        return "redirect:/login";
    }

    @GetMapping("/informacionGeneralCliente/{clienteId}")
    public String informacionGeneralCliente(@PathVariable Long clienteId, Model model){
        Cliente cliente = clienteService.buscarPorId(clienteId);

        Map<String, String> informacionGeneralCliente = new HashMap<>();

        informacionGeneralCliente.put("nombre", cliente.getNombre());
        informacionGeneralCliente.put("nit", cliente.getNit());

        model.addAttribute("informacionGeneralCliente", cliente);
        return "redirect:/";
    }
}
