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
        try {
            String usuario = obterUsuarioLogado();
            leitoService.salvar(leito, usuario);
            return "redirect:/leitos";
        } catch (Exception e) {
            model.addAttribute("erro", "Erro ao salvar o leito. Verifique os dados e tente novamente.");
            model.addAttribute("leito", leito);
            model.addAttribute("statusList", StatusLeito.values());
            return "cadastrarLeito";
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
