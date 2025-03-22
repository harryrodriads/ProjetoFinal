package com.example.sghss.controller;

import com.example.sghss.model.Usuario;
import com.example.sghss.service.ConsultaService;
import com.example.sghss.service.ProntuarioService;
import com.example.sghss.service.UsuarioService;
import com.example.sghss.service.VideochamadaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

@Controller
public class HomeController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private ConsultaService consultaService;

    @Autowired
    private ProntuarioService prontuarioService;

    @Autowired
    private VideochamadaService videochamadaService;

    @GetMapping("/")
    public String index(Model model, Principal principal) {

        if (principal != null) {
            String username = principal.getName();
            Usuario usuario = usuarioService.buscarPorUsername(username)
                    .orElse(null);
            model.addAttribute("usuario", usuario);

            if (usuario != null && usuario.getPaciente() != null) {
                Long pacienteId = usuario.getPaciente().getId();

                model.addAttribute("consultas", consultaService.buscarPorPacienteId(pacienteId));
                model.addAttribute("prontuario", prontuarioService.buscarPorPacienteId(pacienteId).orElse(null));
                model.addAttribute("videochamadas", videochamadaService.buscarPorPacienteId(pacienteId));
            }
        }

        return "index";
    }
}
