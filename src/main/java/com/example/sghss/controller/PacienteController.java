package com.example.sghss.controller;
import com.example.sghss.model.Paciente;
import com.example.sghss.service.PacienteService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Controller
@RequestMapping("/pacientes")
public class PacienteController {

    @Autowired
    private PacienteService pacienteService;
    
    @GetMapping
    public String listarPacientes(Model model) {
        List<Paciente> lista = pacienteService.listarTodos();
        model.addAttribute("pacientes", lista);
        return "listarPacientes";
    }

    @GetMapping("/cadastrar")
    public String exibirFormCadastro(Model model) {
        model.addAttribute("paciente", new Paciente());
        return "cadastrarPaciente";
    }

    @PostMapping("/salvar")
    public String salvarPaciente(
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
    public String exibirFormEdicao(@PathVariable Long id, Model model) {
        Paciente paciente = pacienteService.buscarPorId(id);
        if (paciente == null) {
            return "redirect:/pacientes";
        }
        model.addAttribute("paciente", paciente);
        return "cadastrarPaciente";
    }

    @GetMapping("/excluir/{id}")
    public String excluirPaciente(@PathVariable Long id) {
    	Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String usuarioLogado = authentication.getName(); 
        pacienteService.excluir(id, usuarioLogado);
        return "redirect:/pacientes";
    }
}
