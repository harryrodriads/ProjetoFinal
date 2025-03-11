package com.example.sghss.controller;
import com.example.sghss.model.Internacao;
import com.example.sghss.service.InternacaoService;
import com.example.sghss.service.PacienteService;
import com.example.sghss.service.ProfissionalService;
import com.example.sghss.service.LeitoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/internacoes")
public class InternacaoController {

    @Autowired
    private InternacaoService internacaoService;

    @Autowired
    private PacienteService pacienteService;

    @Autowired
    private ProfissionalService profissionalService;

    @Autowired
    private LeitoService leitoService;

    @GetMapping
    public String listarInternacoes(Model model) {
        List<Internacao> internacoes = internacaoService.listarTodas();
        model.addAttribute("internacoes", internacoes);
        return "internacoes";
    }

    @GetMapping("/cadastrar")
    public String cadastrarForm(Model model) {
        model.addAttribute("internacao", new Internacao());
        model.addAttribute("pacientes", pacienteService.listarTodos());
        model.addAttribute("profissionais", profissionalService.listarTodos());
        model.addAttribute("leitos", leitoService.listarTodos());
        return "cadastrarInternacao";
    }

    @PostMapping("/salvar")
    public String salvar(@ModelAttribute Internacao internacao, Model model) {
        internacao.setDataEntrada(LocalDateTime.now());
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String usuarioLogado = authentication.getName();
        
        boolean isEdit = (internacao.getId() != null);
        internacaoService.salvar(internacao, usuarioLogado);
        
        model.addAttribute("successMessage", isEdit ? "Internação atualizada com sucesso!" : "Internação agendada com sucesso!");
        model.addAttribute("redirectUrl", "/internacoes");
        return "mensagemSucesso";
    }

    @GetMapping("/editar/{id}")
    public String editarForm(@PathVariable Long id, Model model) {
        Optional<Internacao> internacao = internacaoService.buscarPorId(id);
        if (internacao.isPresent()) {
            model.addAttribute("internacao", internacao.get());
            model.addAttribute("pacientes", pacienteService.listarTodos());
            model.addAttribute("profissionais", profissionalService.listarTodos());
            model.addAttribute("leitos", leitoService.listarTodos());
            return "cadastrarInternacao";
        }
        return "redirect:/internacoes";
    }

    @GetMapping("/excluir/{id}")
    public String excluir(@PathVariable Long id) {
    	Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String usuarioLogado = authentication.getName();
        internacaoService.deletar(id, usuarioLogado);
        return "redirect:/internacoes";
    }
}
