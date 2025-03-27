package com.example.sghss.controller;
import com.example.sghss.model.Perfil;
import com.example.sghss.model.Usuario;
import com.example.sghss.service.PacienteService;
import com.example.sghss.service.ProfissionalService;
import com.example.sghss.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;
    
    @Autowired
    private PacienteService pacienteService;

    @Autowired
    private ProfissionalService profissionalService;

    // VERIFICAÇÃO VIA API

    @GetMapping(value = "/login", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<List<Usuario>> listarUsuariosApi() {
        return ResponseEntity.ok(usuarioService.listarTodos());
    }

    @PostMapping(value = "/cadastrar", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> cadastrarUsuarioApi(@Valid @RequestBody Usuario usuario, BindingResult result) {
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body("Erro de validação: " + result.getAllErrors());
        }
        if (usuario.getPerfil() == Perfil.ADMIN) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Não é permitido cadastrar um usuário como ADMIN.");
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String usuarioLogado = authentication.getName();
        usuarioService.salvar(usuario, usuarioLogado);
        return ResponseEntity.status(HttpStatus.CREATED).body("Usuário cadastrado com sucesso!");
    }

    // FRONT-END HTML

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
        @RequestParam(value = "adminKey", required = false) String adminKey,
        RedirectAttributes redirectAttributes,
        Model model
    ) {
        if (result.hasErrors()) {
            model.addAttribute("perfis", Perfil.values());
            model.addAttribute("pacientes", pacienteService.listarTodos());
            model.addAttribute("profissionais", profissionalService.listarTodos());
            return "cadastrarUsuario";
        }

        if (usuario.getPerfil() == Perfil.ADMIN && !"0000".equals(adminKey)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Senha de segurança do ADMIN incorreta!");
            return "redirect:/usuarios/cadastrar";
        }

        if (usuario.getPerfil() == Perfil.PACIENTE && pacienteId != null) {
            usuario.setPaciente(pacienteService.buscarPorId(pacienteId));
        }

        if (usuario.getPerfil() == Perfil.PROFISSIONAL && profissionalId != null) {
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
