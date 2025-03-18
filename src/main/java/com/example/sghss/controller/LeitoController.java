package com.example.sghss.controller;
import com.example.sghss.model.Leito;
import com.example.sghss.model.StatusLeito;
import com.example.sghss.service.LeitoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/leitos")
public class LeitoController {

    @Autowired
    private LeitoService leitoService;

    // VERIFICAÇÃO VIA API
    
    @Controller
    @RequestMapping("/api/leitos")
    public class LeitoApiController {

	    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	    @ResponseBody
	    public ResponseEntity<List<Leito>> listarLeitosApi() {
	        return ResponseEntity.ok(leitoService.listarTodos());
	    }
	
	    @PostMapping(value = "/cadastrar", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	    @ResponseBody
	    public ResponseEntity<?> cadastrarLeitoApi(@Valid @RequestBody Leito leito, BindingResult result) {
	        if (result.hasErrors()) {
	            return ResponseEntity.badRequest().body("Erro de validação: " + result.getAllErrors());
	        }
	        String usuario = obterUsuarioLogado();
	        leitoService.salvar(leito, usuario);
	        return ResponseEntity.status(HttpStatus.CREATED).body(leito);
	    }
	
	    @PutMapping(value = "/editar/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	    @ResponseBody
	    public ResponseEntity<?> editarLeitoApi(@PathVariable Long id, @Valid @RequestBody Leito leito, BindingResult result) {
	        if (result.hasErrors()) {
	            return ResponseEntity.badRequest().body("Erro de validação: " + result.getAllErrors());
	        }
	        Optional<Leito> leitoExistente = leitoService.buscarPorId(id);
	        if (leitoExistente.isEmpty()) {
	            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Leito não encontrado.");
	        }
	        leito.setId(id);
	        String usuario = obterUsuarioLogado();
	        leitoService.salvar(leito, usuario);
	        return ResponseEntity.ok(leito);
	    }
	
	    @DeleteMapping(value = "/excluir/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	    @ResponseBody
	    public ResponseEntity<?> excluirLeitoApi(@PathVariable Long id) {
	        String usuario = obterUsuarioLogado();
	        leitoService.deletar(id, usuario);
	        return ResponseEntity.ok("Leito excluído com sucesso.");
	    }

	    // FRONT-END HTML
	
	    @GetMapping
	    public String listarTodos(Model model) {
	        model.addAttribute("leitos", leitoService.listarTodos());
	        return "leitos";
	    }
	
	    @GetMapping("/cadastrar")
	    public String cadastrarForm(Model model) {
	        model.addAttribute("leito", new Leito());
	        model.addAttribute("statusList", StatusLeito.values());
	        return "cadastrarLeito";
	    }
	
	    @PostMapping("/salvar")
	    public String salvarLeitoWeb(@Valid @ModelAttribute("leito") Leito leito, BindingResult result, Model model) {
	        if (result.hasErrors()) {
	            model.addAttribute("errorMessage", "Corrija os erros e tente novamente!");
	            return "cadastrarLeito";
	        }
	        String usuario = obterUsuarioLogado();
	        boolean isEdit = (leito.getId() != null);
	        leitoService.salvar(leito, usuario);
	        model.addAttribute("successMessage", isEdit ? "Leito atualizado com sucesso!" : "Leito cadastrado com sucesso!");
	        model.addAttribute("redirectUrl", "/leitos");
	        return "mensagemSucesso";
	    }
	
	    @GetMapping("/editar/{id}")
	    public String editarForm(@PathVariable Long id, Model model) {
	        Optional<Leito> leitoOpt = leitoService.buscarPorId(id);
	        if (leitoOpt.isPresent()) {
	            model.addAttribute("leito", leitoOpt.get());
	            model.addAttribute("statusList", StatusLeito.values());
	            return "cadastrarLeito";
	        }
	        return "redirect:/leitos";
	    }
	
	    @GetMapping("/excluir/{id}")
	    public String excluirLeitoWeb(@PathVariable Long id) {
	        String usuario = obterUsuarioLogado();
	        leitoService.deletar(id, usuario);
	        return "redirect:/leitos";
	    }
	
	    private String obterUsuarioLogado() {
	        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	        if (principal instanceof UserDetails) {
	            return ((UserDetails) principal).getUsername();
	        }
	        return "Desconhecido";
	    }
    }
}
