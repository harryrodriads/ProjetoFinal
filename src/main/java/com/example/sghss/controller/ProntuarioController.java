package com.example.sghss.controller;
import com.example.sghss.model.Prontuario;
import com.example.sghss.model.Paciente;
import com.example.sghss.service.ProntuarioService;
import com.example.sghss.service.ConsultaService;
import com.example.sghss.service.PacienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/prontuarios")
public class ProntuarioController {

    @Autowired
    private ProntuarioService prontuarioService;

    @Autowired
    private PacienteService pacienteService;

    @Autowired
    private ConsultaService consultaService;

    @GetMapping("/visualizar/{pacienteId}")
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
