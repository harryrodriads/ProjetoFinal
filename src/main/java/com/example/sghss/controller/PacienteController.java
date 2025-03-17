package com.example.sghss.controller;
import com.example.sghss.model.Paciente;
import com.example.sghss.service.PacienteService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.List;

@Controller
@RequestMapping("/pacientes")
public class PacienteController {

    @Autowired
    private PacienteService pacienteService;

    // VERIFICAÇÃO VIA API
    
    @RestController
    @RequestMapping("/api/pacientes")
    public class PacienteApiController {

	    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	    @ResponseBody
	    public ResponseEntity<List<Paciente>> listarPacientesApi() {
	        return ResponseEntity.ok(pacienteService.listarTodos());
	    }
	
	    @PostMapping(value = "/cadastrar", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	    @ResponseBody
	    public ResponseEntity<?> cadastrarPacienteApi(@Valid @RequestBody Paciente paciente, BindingResult result) {
	        if (result.hasErrors()) {
	            return ResponseEntity.badRequest().body("Erro de validação: " + result.getAllErrors());
	        }
	        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	        String usuarioLogado = authentication.getName();
	        pacienteService.salvar(paciente, usuarioLogado);
	        return ResponseEntity.status(HttpStatus.CREATED).body(paciente);
	    }
	
	    @PutMapping(value = "/editar/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	    @ResponseBody
	    public ResponseEntity<?> editarPacienteApi(@PathVariable Long id, @Valid @RequestBody Paciente paciente, BindingResult result) {
	        if (result.hasErrors()) {
	            return ResponseEntity.badRequest().body("Erro de validação: " + result.getAllErrors());
	        }
	        Paciente pacienteExistente = pacienteService.buscarPorId(id);
	        if (pacienteExistente == null) {
	            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Paciente não encontrado.");
	        }
	        paciente.setId(id);
	        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	        String usuarioLogado = authentication.getName();
	        pacienteService.salvar(paciente, usuarioLogado);
	        return ResponseEntity.ok(paciente);
	    }
	
	    @DeleteMapping(value = "/excluir/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	    @ResponseBody
	    public ResponseEntity<?> excluirPacienteApi(@PathVariable Long id) {
	        try {
	            Paciente paciente = pacienteService.buscarPorId(id);
	            if (paciente == null) {
	                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Paciente não encontrado.");
	            }
	            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	            String usuario = (auth != null) ? auth.getName() : "Desconhecido";
	            pacienteService.excluir(id, usuario);
	            return ResponseEntity.ok("Paciente excluído com sucesso.");
	        } catch (DataIntegrityViolationException e) {
	            return ResponseEntity.status(HttpStatus.CONFLICT).body("Não pode excluir um paciente que tem uma Internação Em Andamento.");
	        }
	    }
    }

    // FRONT-END HTML

    @GetMapping(produces = MediaType.TEXT_HTML_VALUE)
    public String listarPacientesWeb(Model model) {
        model.addAttribute("pacientes", pacienteService.listarTodos());
        return "listarPacientes";
    }

    @GetMapping("/cadastrar")
    public String exibirFormCadastro(Model model) {
        model.addAttribute("paciente", new Paciente());
        return "cadastrarPaciente";
    }

    @PostMapping("/salvar")
    public String salvarPacienteWeb(
        @Valid @ModelAttribute("paciente") Paciente paciente,
        BindingResult result,
        Model model
    ) {
        if (result.hasErrors()) {
            model.addAttribute("errorMessage", "Corrija os erros e tente novamente!");
            return "cadastrarPaciente";
        }
        boolean isEdit = (paciente.getId() != null);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String usuarioLogado = authentication.getName();
        pacienteService.salvar(paciente, usuarioLogado);

        model.addAttribute("successMessage", isEdit ? "Paciente atualizado com sucesso!" : "Paciente cadastrado com sucesso!");
        model.addAttribute("redirectUrl", "/pacientes");
        return "mensagemSucesso";
    }

    @GetMapping("/editar/{id}")
    public String exibirFormEdicaoWeb(@PathVariable Long id, Model model) {
        Paciente paciente = pacienteService.buscarPorId(id);
        if (paciente == null) {
            return "redirect:/pacientes";
        }
        model.addAttribute("paciente", paciente);
        return "cadastrarPaciente";
    }

    @GetMapping("/excluir/{id}")
    public String excluirPacienteWeb(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Paciente paciente = pacienteService.buscarPorId(id);
            if (paciente == null) {
                redirectAttributes.addFlashAttribute("erro", "Paciente não encontrado.");
                return "redirect:/pacientes";
            }
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String usuario = (auth != null) ? auth.getName() : "Desconhecido";
            pacienteService.excluir(id, usuario);
            redirectAttributes.addFlashAttribute("sucesso", "Paciente excluído com sucesso!");
        } catch (DataIntegrityViolationException e) {
            redirectAttributes.addFlashAttribute("erro", "Não pode excluir um paciente que tem uma Internação Em Andamento.");
        }
        return "redirect:/pacientes";
    }
}