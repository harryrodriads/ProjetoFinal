package com.example.sghss.controller;
import com.example.sghss.model.Especialidade;
import com.example.sghss.service.EspecialidadeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/especialidades")
public class EspecialidadeController {

    @Autowired
    private EspecialidadeService especialidadeService;

    @GetMapping
    public String listarEspecialidades(Model model) {
        List<Especialidade> lista = especialidadeService.listarTodas();
        model.addAttribute("especialidades", lista);
        return "listarEspecialidades";
    }

    @GetMapping("/cadastrar")
    public String exibirFormCadastro(Model model) {
        model.addAttribute("especialidade", new Especialidade());
        return "cadastrarEspecialidade";
    }

    @PostMapping("/salvar")
    public String salvarEspecialidade(
        @Valid @ModelAttribute("especialidade") Especialidade especialidade,
        BindingResult result,
        Model model
    ) {
        if (result.hasErrors()) {
            model.addAttribute("errorMessage", "Corrija os erros e tente novamente!");
            return "mensagemErro";
        }

        String usuario = obterUsuarioLogado();

        especialidadeService.salvar(especialidade, usuario);

        model.addAttribute("successMessage", "Especialidade salva com sucesso!");
        model.addAttribute("redirectUrl", "/especialidades");
        return "mensagemSucesso";
    }

    @GetMapping("/editar/{id}")
    public String exibirFormEdicao(@PathVariable Long id, Model model) {
        Especialidade especialidade = especialidadeService.buscarPorId(id).orElse(null);
        if (especialidade == null) {
            return "redirect:/especialidades";
        }
        model.addAttribute("especialidade", especialidade);
        return "cadastrarEspecialidade";
    }

    @GetMapping("/excluir/{id}")
    public String excluirEspecialidade(@PathVariable Long id) {
        String usuario = obterUsuarioLogado();
        especialidadeService.deletar(id, usuario);
        return "redirect:/especialidades";
    }

    private String obterUsuarioLogado() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        }
        return "Desconhecido";
    }
}
