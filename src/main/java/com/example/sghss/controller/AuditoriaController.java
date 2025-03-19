package com.example.sghss.controller;
import com.example.sghss.model.Auditoria;
import com.example.sghss.service.AuditoriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Controller
@RequestMapping("/auditoria")
public class AuditoriaController {

    @Autowired
    private AuditoriaService auditoriaService;
    
    // FRONT-END HTML
    
    @GetMapping(produces = MediaType.TEXT_HTML_VALUE)
    public String listarAuditoriasWeb(Model model) {
        List<Auditoria> auditorias = auditoriaService.listarTodas();
        model.addAttribute("auditorias", auditorias);
        return "auditoria";
    }

    // VERIFICAÇÃO VIA API
    
    @RestController
    @RequestMapping("/api/auditoria")
    public class AuditoriaApiController {

        @Autowired
        private AuditoriaService auditoriaService;

        @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
        public ResponseEntity<List<Auditoria>> listarAuditoriasApi() {
            List<Auditoria> auditorias = auditoriaService.listarTodas();
            return ResponseEntity.ok(auditorias);
        }
    }
}
