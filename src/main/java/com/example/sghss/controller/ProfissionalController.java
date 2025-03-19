package com.example.sghss.controller;

import com.example.sghss.model.Especialidade;
import com.example.sghss.model.Profissional;
import com.example.sghss.service.ProfissionalService;
import com.example.sghss.service.ConsultaService;
import com.example.sghss.service.EspecialidadeService;
import jakarta.validation.Valid;
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

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/profissionais")
public class ProfissionalController {

    @Autowired
    private ProfissionalService profissionalService;

    @Autowired
    private EspecialidadeService especialidadeService;
    
    @Autowired
    private ConsultaService consultaService;

    // FRONT-END HTML
    
    @GetMapping
    public String listarProfissionais(Model model) {
        List<Profissional> lista = profissionalService.listarTodos();
        model.addAttribute("profissionais", lista);
        return "listarProfissionais";
    }
    
    @GetMapping("/cadastrar")
    public String exibirFormCadastro(Model model) {
        model.addAttribute("profissional", new Profissional());
        model.addAttribute("especialidades", especialidadeService.listarTodas());
        return "cadastrarProfissional";
    }
    
    @PostMapping("/salvar")
    public String salvarProfissional(
            @Valid @ModelAttribute("profissional") Profissional profissional,
            @RequestParam("especialidadeId") Long especialidadeId,
            BindingResult result,
            Model model
    ) {
        if (result.hasErrors()) {
            model.addAttribute("errorMessage", "Corrija os erros e tente novamente!");
            return "mensagemErro";
        }
        
        Especialidade especialidade = especialidadeService.buscarPorId(especialidadeId)
                .orElseThrow(() -> new IllegalArgumentException("Especialidade não encontrada"));
        profissional.setEspecialidade(especialidade);
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String usuarioLogado = authentication.getName();
        
        if (!profissionalService.salvar(profissional, usuarioLogado)) {
            model.addAttribute("errorMessage", "Registro profissional já cadastrado!");
            return "mensagemErro";
        }
        
        model.addAttribute("successMessage", "Profissional salvo com sucesso!");
        model.addAttribute("redirectUrl", "/profissionais");
        return "mensagemSucesso";
    }
    
    @GetMapping("/editar/{id}")
    public String exibirFormEdicao(@PathVariable Long id, Model model) {
        Optional<Profissional> profissionalOpt = profissionalService.buscarPorId(id);
        if (profissionalOpt.isEmpty()) {
            return "redirect:/profissionais";
        }
        model.addAttribute("profissional", profissionalOpt.get());
        model.addAttribute("especialidades", especialidadeService.listarTodas());
        return "cadastrarProfissional";
    }
    
    @GetMapping("/excluir/{id}")
    public String excluirProfissionalWeb(@PathVariable Long id, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String usuarioLogado = authentication.getName();
        if (consultaService.existeConsultaPorProfissional(id)) {
            model.addAttribute("errorMessage", "Não é possível excluir, pois há consultas associadas a este profissional.");
            return "mensagemErro";
        }
        profissionalService.excluir(id, usuarioLogado);
        return "redirect:/profissionais";
    }

    // VERIFICAÇÃO VIA API
    
    @RestController
    @RequestMapping("/api/profissionais")
    public class ProfissionaisApiController {

        @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
        public ResponseEntity<List<Profissional>> listarProfissionaisApi() {
            return ResponseEntity.ok(profissionalService.listarTodos());
        }
    
        @PostMapping(value = "/cadastrar", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
        public ResponseEntity<?> cadastrarProfissionalApi(@Valid @RequestBody Profissional profissional, BindingResult result) {
            if (result.hasErrors()) {
                return ResponseEntity.badRequest().body("Erro de validação: " + result.getAllErrors());
            }
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String usuarioLogado = authentication.getName();
            profissionalService.salvar(profissional, usuarioLogado);
            return ResponseEntity.status(HttpStatus.CREATED).body(profissional);
        }
    
        @PutMapping(value = "/editar/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
        public ResponseEntity<?> editarProfissionalApi(@PathVariable Long id, @Valid @RequestBody Profissional profissional, BindingResult result) {
            if (result.hasErrors()) {
                return ResponseEntity.badRequest().body("Erro de validação: " + result.getAllErrors());
            }
            Optional<Profissional> profissionalExistente = profissionalService.buscarPorId(id);
            if (profissionalExistente.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Profissional não encontrado.");
            }
            profissional.setId(id);
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String usuarioLogado = authentication.getName();
            profissionalService.salvar(profissional, usuarioLogado);
            return ResponseEntity.ok(profissional);
        }
    
        @DeleteMapping(value = "/excluir/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
        public ResponseEntity<?> excluirProfissionalApi(@PathVariable Long id) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String usuarioLogado = authentication.getName();
            if (consultaService.existeConsultaPorProfissional(id)) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("Não é possível excluir, pois há consultas associadas a este profissional.");
            }
            profissionalService.excluir(id, usuarioLogado);
            return ResponseEntity.ok("Profissional excluído com sucesso.");
        }
    }
}
