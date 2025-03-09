package com.example.sghss.controller;
import com.example.sghss.model.Perfil;
import com.example.sghss.model.Usuario;
import com.example.sghss.service.PacienteService;
import com.example.sghss.service.ProfissionalService;
import com.example.sghss.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    /*@Autowired
    private UsuarioRepository usuarioRepository;*/
    
    @Autowired
    private PacienteService pacienteService;

    @Autowired
    private ProfissionalService profissionalService;

    @Autowired
    private PasswordEncoder passwordEncoder;

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

        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));

        if (usuario.getPerfil() == Perfil.PACIENTE && pacienteId != null) {
            usuario.setPaciente(pacienteService.buscarPorId(pacienteId));
        }
        
        if (usuario.getPerfil() == Perfil.MEDICO && profissionalId != null) {
            usuario.setProfissional(profissionalService.buscarPorId(profissionalId).orElse(null));
        }

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String senhaCriptografada = encoder.encode(usuario.getPassword());
        usuario.setPassword(senhaCriptografada);
        usuarioService.salvar(usuario);

        model.addAttribute("successMessage", "Usuário cadastrado com sucesso!");
        model.addAttribute("redirectUrl", "/usuarios");
        return "mensagemSucesso";
    }


    @GetMapping
    public String listarUsuarios(Model model) {
        model.addAttribute("usuarios", usuarioService.listarTodos());
        return "listarUsuarios";
    }
    
    /*@GetMapping("/criar")
    @ResponseBody
    public String criarUsuario(@RequestParam String username, @RequestParam String senha, @RequestParam Perfil perfil) {
        if (usuarioRepository.findByUsername(username).isPresent()) {
            return "Erro: Usuário já existe!";
        }

        String senhaCriptografada = passwordEncoder.encode(senha);
        Usuario novoUsuario = new Usuario();
        novoUsuario.setUsername(username);
        novoUsuario.setPassword(senhaCriptografada);
        novoUsuario.setPerfil(perfil);

        usuarioRepository.save(novoUsuario);

        return "Usuário criado com sucesso! Username: " + username;
    }*/
}
