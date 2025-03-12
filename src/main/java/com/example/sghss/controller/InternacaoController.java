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
    public String salvarInternacao(@ModelAttribute Internacao internacao, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String usuarioLogado = authentication.getName();

        boolean isEdicao = (internacao.getId() != null);

        internacaoService.salvar(internacao, usuarioLogado);
        model.addAttribute("successMessage", isEdicao ? "Internação atualizada com sucesso!" : "Internação cadastrada com sucesso!");
        model.addAttribute("redirectUrl", "/internacoes");
        
        return "mensagemSucesso"; 
    }

    @GetMapping("/editar/{id}")
    public String editarInternacao(@PathVariable Long id, Model model) {
        Optional<Internacao> internacaoOpt = internacaoService.buscarPorId(id);
        if (internacaoOpt.isPresent()) {
            model.addAttribute("internacao", internacaoOpt.get());
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
