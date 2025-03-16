package com.example.sghss.controller;
import com.example.sghss.model.Internacao;
import com.example.sghss.service.InternacaoService;
import com.example.sghss.service.PacienteService;
import com.example.sghss.service.ProfissionalService;
import com.example.sghss.service.LeitoService;
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

    // VERIFICAÇÃO VIA API

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<List<Internacao>> listarInternacoesApi() {
        return ResponseEntity.ok(internacaoService.listarTodas());
    }

    @PostMapping(value = "/cadastrar", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> cadastrarInternacaoApi(@Valid @RequestBody Internacao internacao, BindingResult result) {
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body("Erro de validação: " + result.getAllErrors());
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String usuarioLogado = authentication.getName();
        internacaoService.salvar(internacao, usuarioLogado);
        return ResponseEntity.status(HttpStatus.CREATED).body(internacao);
    }

    @PutMapping(value = "/editar/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> editarInternacaoApi(@PathVariable Long id, @Valid @RequestBody Internacao internacao, BindingResult result) {
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body("Erro de validação: " + result.getAllErrors());
        }
        Optional<Internacao> internacaoExistente = internacaoService.buscarPorId(id);
        if (internacaoExistente.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Internação não encontrada.");
        }
        internacao.setId(id);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String usuarioLogado = authentication.getName();
        internacaoService.salvar(internacao, usuarioLogado);
        return ResponseEntity.ok(internacao);
    }

    @DeleteMapping(value = "/excluir/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> excluirInternacaoApi(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String usuarioLogado = authentication.getName();
        internacaoService.deletar(id, usuarioLogado);
        return ResponseEntity.ok("Internação excluída com sucesso.");
    }

    // FRONT-END HTML

    @GetMapping
    public String listarInternacoes(Model model) {
        model.addAttribute("internacoes", internacaoService.listarTodas());
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
    public String salvarInternacaoWeb(@Valid @ModelAttribute("internacao") Internacao internacao, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("errorMessage", "Corrija os erros e tente novamente!");
            return "cadastrarInternacao";
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String usuarioLogado = authentication.getName();
        boolean isEdicao = (internacao.getId() != null);
        internacaoService.salvar(internacao, usuarioLogado);
        model.addAttribute("successMessage", isEdicao ? "Internação atualizada com sucesso!" : "Internação cadastrada com sucesso!");
        model.addAttribute("redirectUrl", "/internacoes");
        return "mensagemSucesso";
    }

    @GetMapping("/editar/{id}")
    public String editarInternacaoWeb(@PathVariable Long id, Model model) {
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
    public String excluirInternacaoWeb(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String usuarioLogado = authentication.getName();
        internacaoService.deletar(id, usuarioLogado);
        return "redirect:/internacoes";
    }
}