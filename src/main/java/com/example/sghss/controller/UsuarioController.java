package com.example.sghss.controller;
import com.example.sghss.model.Perfil;
import com.example.sghss.model.Usuario;
import com.example.sghss.service.PacienteService;
import com.example.sghss.service.ProfissionalService;
import com.example.sghss.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;
    
    @Autowired
    private PacienteService pacienteService;

    @Autowired
    private ProfissionalService profissionalService;

    @GetMapping("/cadastrar")
    public String exibirFormCadastro(Model model) {
        model.addAttribute("usuario", new Usuario());
        model.addAttribute("perfis", Perfil.values());
        model.addAttribute("pacientes", pacienteService.listarTodos());
        model.addAttribute("profissionais", profissionalService.listarTodos());
        return "cadastrarUsuario";
    }

    @PostMapping("/salvar")
    public String salvarUsuario(
        @Valid @ModelAttribute("usuario") Usuario usuario,
        BindingResult result,
        @RequestParam(value = "paciente", required = false) Long pacienteId,
        @RequestParam(value = "profissional", required = false) Long profissionalId,
        Model model
    ) {
        if (result.hasErrors()) {
            model.addAttribute("perfis", Perfil.values());
            model.addAttribute("pacientes", pacienteService.listarTodos());
            model.addAttribute("profissionais", profissionalService.listarTodos());
            return "cadastrarUsuario";
        }
        
        if (usuario.getPerfil() == Perfil.ADMIN) {
            model.addAttribute("errorMessage", "Não é permitido cadastrar um usuário como ADMIN.");
            return "mensagemErro";
        }

        if (usuario.getPerfil() == Perfil.PACIENTE && pacienteId != null) {
            usuario.setPaciente(pacienteService.buscarPorId(pacienteId));
        }
        
        if (usuario.getPerfil() == Perfil.MEDICO && profissionalId != null) {
            usuario.setProfissional(profissionalService.buscarPorId(profissionalId).orElse(null));
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String usuarioLogado = authentication.getName();
        usuarioService.salvar(usuario, usuarioLogado);

        model.addAttribute("successMessage", "Usuário cadastrado com sucesso!");
        model.addAttribute("redirectUrl", "/usuarios");
        return "mensagemSucesso";
    }

    @GetMapping
    public String listarUsuarios(Model model) {
        model.addAttribute("usuarios", usuarioService.listarTodos());
        return "listarUsuarios";
    }
    
    @PostMapping("/admin")
    public ResponseEntity<String> criarAdmin(@RequestBody Usuario usuario) {
        usuario.setPerfil(Perfil.ADMIN);
        usuario.setPassword(new BCryptPasswordEncoder().encode(usuario.getPassword()));
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String usuarioLogado = authentication.getName();
        usuarioService.salvar(usuario, usuarioLogado);

        return ResponseEntity.ok("Usuário ADMIN criado com sucesso!");
    }

}
