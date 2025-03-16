package com.example.sghss.controller;
import com.example.sghss.model.EstoqueSuprimento;
import com.example.sghss.service.EstoqueSuprimentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/estoque")
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class EstoqueSuprimentoController {

    @Autowired
    private EstoqueSuprimentoService estoqueService;

    // VERIFICAÇÃO VIA API

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<List<EstoqueSuprimento>> listarEstoqueApi() {
        return ResponseEntity.ok(estoqueService.listarTodos());
    }

    @PostMapping(value = "/cadastrar", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> cadastrarItemApi(@Valid @RequestBody EstoqueSuprimento item, BindingResult result) {
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body("Erro de validação: " + result.getAllErrors());
        }
        estoqueService.salvar(item);
        return ResponseEntity.status(HttpStatus.CREATED).body(item);
    }

    @PutMapping(value = "/editar/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> editarItemApi(@PathVariable Long id, @Valid @RequestBody EstoqueSuprimento item, BindingResult result) {
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body("Erro de validação: " + result.getAllErrors());
        }
        Optional<EstoqueSuprimento> itemExistente = estoqueService.buscarPorId(id);
        if (itemExistente.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Item não encontrado no estoque.");
        }
        item.setId(id);
        estoqueService.salvar(item);
        return ResponseEntity.ok(item);
    }

    @DeleteMapping(value = "/excluir/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> excluirItemApi(@PathVariable Long id) {
        estoqueService.deletar(id);
        return ResponseEntity.ok("Item excluído com sucesso.");
    }

    // FRONT-END HTML

    @GetMapping
    public String listarEstoque(Model model) {
        model.addAttribute("estoque", estoqueService.listarTodos());
        return "listarEstoque";
    }

    @GetMapping("/cadastrar")
    public String exibirFormularioCadastro(Model model) {
        model.addAttribute("item", new EstoqueSuprimento());
        return "cadastrarItem";
    }

    @PostMapping("/salvar")
    public String salvarItemWeb(@Valid @ModelAttribute("item") EstoqueSuprimento item, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("errorMessage", "Corrija os erros e tente novamente!");
            return "cadastrarItem";
        }
        estoqueService.salvar(item);
        model.addAttribute("successMessage", "Suprimento cadastrado com sucesso!");
        model.addAttribute("redirectUrl", "/estoque");
        return "mensagemSucesso";
    }

    @GetMapping("/editar/{id}")
    public String editarItemWeb(@PathVariable Long id, Model model) {
        Optional<EstoqueSuprimento> itemOpt = estoqueService.buscarPorId(id);
        if (itemOpt.isPresent()) {
            model.addAttribute("item", itemOpt.get());
            return "cadastrarItem";
        }
        return "redirect:/estoque";
    }

    @GetMapping("/excluir/{id}")
    public String excluirItemWeb(@PathVariable Long id) {
        estoqueService.deletar(id);
        return "redirect:/estoque";
    }

    @GetMapping("/relatorio")
    public String gerarRelatorioWeb(Model model) {
        List<EstoqueSuprimento> estoque = estoqueService.listarTodos();
        int totalQuantidade = estoque.stream().mapToInt(EstoqueSuprimento::getQuantidade).sum();
        model.addAttribute("estoque", estoque);
        model.addAttribute("totalQuantidade", totalQuantidade);
        return "relatorio";
    }
}
