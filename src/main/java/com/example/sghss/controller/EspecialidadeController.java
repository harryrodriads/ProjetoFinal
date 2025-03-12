package com.example.sghss.controller;
import com.example.sghss.model.Especialidade;
import com.example.sghss.service.EspecialidadeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

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
            return "cadastrarEspecialidade";
        }

        String usuario = obterUsuarioLogado();
        if (especialidade.getId() != null) {
            Optional<Especialidade> existente = especialidadeService.buscarPorId(especialidade.getId());
            if (existente.isPresent()) {
                Especialidade especialidadeAtualizada = existente.get();
                especialidadeAtualizada.setNome(especialidade.getNome());
                especialidadeAtualizada.setDescricao(especialidade.getDescricao());

                especialidadeService.salvar(especialidadeAtualizada, usuario);
                model.addAttribute("successMessage", "Especialidade atualizada com sucesso!");
            } else {
                especialidadeService.salvar(especialidade, usuario);
                model.addAttribute("successMessage", "Especialidade cadastrada com sucesso!");
            }
        } else {
            especialidadeService.salvar(especialidade, usuario);
            model.addAttribute("successMessage", "Especialidade cadastrada com sucesso!");
        }

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

    @DeleteMapping("/excluir/{id}")
    public ResponseEntity<?> excluirEspecialidade(@PathVariable Long id) {
        String usuario = obterUsuarioLogado();

        try {
            especialidadeService.deletar(id, usuario);
            return ResponseEntity.ok().body("Especialidade excluída com sucesso!");
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    private String obterUsuarioLogado() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        }
        return "Desconhecido";
    }
}
