package com.example.sghss.controller;
import com.example.sghss.model.RelatorioFinanceiro;
import com.example.sghss.service.RelatorioFinanceiroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/relatoriosFinanceiros")
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class RelatorioFinanceiroController {

    @Autowired
    private RelatorioFinanceiroService relatorioService;

    // FRONT-END HTML

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
    public String salvarRelatorio(@ModelAttribute("relatorioFinanceiro") RelatorioFinanceiro relatorio, 
                                  Model model) {
        try {
            relatorioService.salvar(relatorio);
            model.addAttribute("successMessage", "Relatório Financeiro cadastrado com sucesso!");
            model.addAttribute("redirectUrl", "/relatoriosFinanceiros");
            return "mensagemSucesso";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Erro ao salvar o relatório financeiro. Tente novamente!");
            return "cadastrarRelatorio";
        }
    }

    @GetMapping("/verRelatorio")
    public String visualizarRelatorio(Model model) {
        List<RelatorioFinanceiro> relatorios = relatorioService.listarTodos();

        if (relatorios.isEmpty()) {
            model.addAttribute("errorMessage", "Nenhum relatório encontrado.");
            return "listarRelatorios";
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
    public String excluirRelatorio(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            relatorioService.deletar(id);
            redirectAttributes.addFlashAttribute("sucesso", "Relatório financeiro excluído com sucesso!");
        } catch (DataIntegrityViolationException e) {
            redirectAttributes.addFlashAttribute("erro", "Não é possível excluir um relatório que está em uso.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", "Erro ao excluir o relatório financeiro.");
        }
        return "redirect:/relatoriosFinanceiros";
    }
}
