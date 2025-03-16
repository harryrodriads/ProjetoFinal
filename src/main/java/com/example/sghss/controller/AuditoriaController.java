package com.example.sghss.controller;
import com.example.sghss.service.AuditoriaService;
import com.example.sghss.model.Auditoria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import java.util.List;

@Controller
@RequestMapping("/auditoria")
public class AuditoriaController {

    @Autowired
    private AuditoriaService auditoriaService;

    // VERIFICAÇÃO VIA API
    
    @GetMapping(produces = MediaType.TEXT_HTML_VALUE)
    public String listarAuditoriasWeb(Model model) {
        List<Auditoria> auditorias = auditoriaService.listarTodas();
        model.addAttribute("auditorias", auditorias);
        return "auditoria";
    }

    // FRONT-END HTML
    
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<List<Auditoria>> listarAuditoriasApi() {
        return ResponseEntity.ok(auditoriaService.listarTodas());
    }
}
