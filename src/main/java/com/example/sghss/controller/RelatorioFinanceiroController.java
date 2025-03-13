package com.example.sghss.controller;
import com.example.sghss.model.RelatorioFinanceiro;
import com.example.sghss.service.RelatorioFinanceiroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/relatoriosFinanceiros")
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class RelatorioFinanceiroController {

    @Autowired
    private RelatorioFinanceiroService relatorioService;

    @GetMapping
    public String listarRelatorios(Model model) {
        List<RelatorioFinanceiro> relatorios = relatorioService.listarTodos();
        model.addAttribute("relatorios", relatorios);
        return "listarRelatorios";
    }


    @GetMapping("/cadastrar")
    public String exibirFormularioCadastro(Model model) {
        model.addAttribute("relatorioFinanceiro", new RelatorioFinanceiro());
        return "cadastrarRelatorio";
    }

    @PostMapping("/salvar")
    public String salvarRelatorio(@ModelAttribute("relatorioFinanceiro") RelatorioFinanceiro relatorio) {
        relatorioService.salvar(relatorio);
        return "redirect:/relatoriosFinanceiros";
    }

    @GetMapping("/verRelatorio")
    public String visualizarRelatorio(Model model) {
        List<RelatorioFinanceiro> relatorios = relatorioService.listarTodos();

        if (relatorios.isEmpty()) {
            return "redirect:/relatoriosFinanceiros";
        }
        
        BigDecimal totalEntrada = BigDecimal.ZERO;
        BigDecimal totalDespesa = BigDecimal.ZERO;

        for (RelatorioFinanceiro relatorio : relatorios) {
            totalEntrada = totalEntrada.add(relatorio.getEntrada());
            totalDespesa = totalDespesa.add(relatorio.getDespesa());
        }

        BigDecimal totalFinal = totalEntrada.subtract(totalDespesa);

        model.addAttribute("relatorios", relatorios);
        model.addAttribute("totalEntrada", totalEntrada);
        model.addAttribute("totalDespesa", totalDespesa);
        model.addAttribute("totalFinal", totalFinal);

        return "verRelatorio"; 
    }

    @GetMapping("/excluir/{id}")
    public String excluirRelatorio(@PathVariable Long id) {
        relatorioService.deletar(id);
        return "redirect:/relatoriosFinanceiros";
    }
}
