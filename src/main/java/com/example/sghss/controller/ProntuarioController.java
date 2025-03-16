package com.example.sghss.controller;
import com.example.sghss.model.Prontuario;
import com.example.sghss.model.Paciente;
import com.example.sghss.service.ProntuarioService;
import com.example.sghss.service.ConsultaService;
import com.example.sghss.service.PacienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/prontuarios")
public class ProntuarioController {

    @Autowired
    private ProntuarioService prontuarioService;

    @Autowired
    private PacienteService pacienteService;

    @Autowired
    private ConsultaService consultaService;

 // VERIFICAÇÃO VIA API

    @GetMapping(value = "/visualizar/{pacienteId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> visualizarProntuarioApi(@PathVariable Long pacienteId) {
        Paciente paciente = pacienteService.buscarPorId(pacienteId);
        if (paciente == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Paciente não encontrado."));
        }

        Optional<Prontuario> prontuarioOpt = prontuarioService.buscarPorPacienteId(pacienteId);
        if (prontuarioOpt.isEmpty()) {
            System.out.println("Prontuário não encontrado para o paciente com ID: " + pacienteId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Prontuário não encontrado para o paciente."));
        }

        return ResponseEntity.ok(prontuarioOpt.get());
    }

    @PostMapping(value = "/salvar", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> salvarProntuarioApi(@Valid @RequestBody Prontuario prontuario, BindingResult result) {
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body("Erro de validação: " + result.getAllErrors());
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String usuarioLogado = authentication.getName();
        prontuario.setDataAtualizacao(LocalDateTime.now());
        prontuarioService.salvar(prontuario, usuarioLogado);
        return ResponseEntity.ok("Prontuário atualizado com sucesso.");
    }

    // FRONT-END HTML

    @GetMapping(value = "/visualizar/{pacienteId}", produces = MediaType.TEXT_HTML_VALUE)
    public String visualizarProntuario(@PathVariable Long pacienteId, Model model) {
        Paciente paciente = pacienteService.buscarPorId(pacienteId);

        if (paciente == null) {
            return "redirect:/pacientes";
        }

        Prontuario prontuario = prontuarioService.buscarPorPacienteId(pacienteId).orElse(new Prontuario());
        List<Map<String, Object>> consultas = consultaService.buscarConsultasPorPaciente(pacienteId);

        model.addAttribute("paciente", paciente);
        model.addAttribute("consultas", consultas);
        model.addAttribute("prontuario", prontuario);

        return "visualizarProntuario";
    }
    
    @PostMapping("/salvar")
    public String salvarProntuario(
            @ModelAttribute Prontuario prontuario,
            @RequestParam("pacienteId") Long pacienteId,
            Model model) {

        Paciente paciente = pacienteService.buscarPorId(pacienteId);
        if (paciente == null) {
            return "redirect:/pacientes";
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String usuarioLogado = authentication.getName();
        Prontuario prontuarioExistente = prontuarioService.buscarPorPacienteId(pacienteId).orElse(null);

        if (prontuarioExistente != null) {
            prontuarioExistente.setHistoricoMedico(prontuario.getHistoricoMedico());
            prontuarioExistente.setObservacoes(prontuario.getObservacoes());
            prontuarioExistente.setDiagnostico(prontuario.getDiagnostico());
            prontuarioExistente.setTratamento(prontuario.getTratamento());
            prontuarioExistente.setPrescricao(prontuario.getPrescricao());
            prontuarioExistente.setDataAtualizacao(LocalDateTime.now());
            prontuarioService.salvar(prontuarioExistente, usuarioLogado);
        } else {
            prontuario.setPaciente(paciente);
            prontuario.setDataAtualizacao(LocalDateTime.now());
            prontuarioService.salvar(prontuario, usuarioLogado);
        }

        model.addAttribute("successMessage", "Prontuário atualizado com sucesso!");
        model.addAttribute("redirectUrl", "/prontuarios/visualizar/" + pacienteId);
        return "mensagemSucesso";
    }
}