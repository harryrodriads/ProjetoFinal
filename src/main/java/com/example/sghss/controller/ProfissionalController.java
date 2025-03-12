package com.example.sghss.controller;
import com.example.sghss.model.Profissional;
import com.example.sghss.model.Especialidade;
import com.example.sghss.service.ProfissionalService;
import com.example.sghss.service.ConsultaService;
import com.example.sghss.service.EspecialidadeService;
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
@RequestMapping("/profissionais")
public class ProfissionalController {

    @Autowired
    private ProfissionalService profissionalService;

    @Autowired
    private EspecialidadeService especialidadeService;
    
    @Autowired
    private ConsultaService consultaService;

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
        Profissional profissional = profissionalService.buscarPorId(id).orElse(null);
        if (profissional == null) {
            return "redirect:/profissionais";
        }
        model.addAttribute("profissional", profissional);
        model.addAttribute("especialidades", especialidadeService.listarTodas());
        return "cadastrarProfissional";
    }

    
    @DeleteMapping("/excluir/{id}")
    @ResponseBody
    public String excluir(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String usuarioLogado = authentication.getName();
        if (consultaService.existeConsultaPorProfissional(id)) {
            return "erro";
        }

        profissionalService.excluir(id, usuarioLogado);
        return "sucesso";
    }
}
