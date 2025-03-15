package com.example.sghss.controller;
import com.example.sghss.model.EstoqueSuprimento;
import com.example.sghss.service.EstoqueSuprimentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/estoque")
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class EstoqueSuprimentoController {

    @Autowired
    private EstoqueSuprimentoService estoqueService;

    @GetMapping
    public String listarEstoque(Model model) {
        List<EstoqueSuprimento> lista = estoqueService.listarTodos();
        model.addAttribute("estoque", lista);
        return "/listarEstoque";
    }

    @GetMapping("/cadastrar")
    public String exibirFormularioCadastro(Model model) {
        EstoqueSuprimento item = new EstoqueSuprimento();
        model.addAttribute("item", item);
        return "/cadastrarItem";
    }

    @PostMapping("/salvar")
    public String salvarItem(@ModelAttribute("item") EstoqueSuprimento item, Model model) {
        estoqueService.salvar(item);
        model.addAttribute("successMessage", "Suprimento cadastrado com sucesso!");
        model.addAttribute("redirectUrl", "/estoque");
        return "mensagemSucesso";
    }
    
    @GetMapping("/excluir/{id}")
    public String excluirItem(@PathVariable Long id) {
        estoqueService.deletar(id);
        return "redirect:/estoque";
    }
    
    @GetMapping("/relatorio")
    public String gerarRelatorio(Model model) {
        List<EstoqueSuprimento> estoque = estoqueService.listarTodos();
        int totalQuantidade = estoque.stream().mapToInt(EstoqueSuprimento::getQuantidade).sum();

        model.addAttribute("estoque", estoque);
        model.addAttribute("totalQuantidade", totalQuantidade);

        return "/relatorio";
    }


    @GetMapping("/editar/{id}")
    public String editarItem(@PathVariable Long id, Model model) {
        Optional<EstoqueSuprimento> itemOpt = estoqueService.buscarPorId(id);
        if (itemOpt.isPresent()) {
            model.addAttribute("item", itemOpt.get());
            return "/cadastrarItem";
        }
        return "redirect:/estoque";
    }
}
