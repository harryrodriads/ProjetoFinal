package com.example.sghss.controller;
import com.example.sghss.service.AuditoriaService;
import com.example.sghss.model.Auditoria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import java.util.List;

@Controller
@RequestMapping("/auditoria")
public class AuditoriaController {

    @Autowired
    private AuditoriaService auditoriaService;

    @GetMapping
    public String listarAuditorias(Model model) {
        List<Auditoria> auditorias = auditoriaService.listarTodas();
        model.addAttribute("auditorias", auditorias);
        return "auditoria";
    }
}
