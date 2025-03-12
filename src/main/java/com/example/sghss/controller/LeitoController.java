package com.example.sghss.controller;
import com.example.sghss.model.Leito;
import com.example.sghss.model.StatusLeito;
import com.example.sghss.service.LeitoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/leitos")
public class LeitoController {

    @Autowired
    private LeitoService leitoService;

    @GetMapping
    public String listarTodos(Model model) {
        List<Leito> leitos = leitoService.listarTodos();
        model.addAttribute("leitos", leitos);
        return "leitos";
    }

    @GetMapping("/cadastrar")
    public String cadastrarForm(Model model) {
        model.addAttribute("leito", new Leito());
        model.addAttribute("statusList", StatusLeito.values());
        return "cadastrarLeito";
    }

    @PostMapping("/salvar")
    public String salvar(@ModelAttribute Leito leito, Model model) {
    	String usuario = obterUsuarioLogado();
        boolean isEdit = (leito.getId() != null);
        
        leitoService.salvar(leito, usuario);
        model.addAttribute("successMessage", isEdit ? "Leito atualizado com sucesso!" : "Leito cadastrado com sucesso!");
        model.addAttribute("redirectUrl", "/leitos");
        return "mensagemSucesso";
    }

    @DeleteMapping("/excluir/{id}")
    public String excluirLeito(@PathVariable Long id) {
        String usuario = obterUsuarioLogado();
        leitoService.deletar(id, usuario);
        return "redirect:/leitos";
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

    private String obterUsuarioLogado() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        }
        return "Desconhecido";
    }
}
