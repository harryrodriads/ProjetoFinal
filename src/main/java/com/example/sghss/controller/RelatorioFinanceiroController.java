package com.example.sghss.controller;
import com.example.sghss.model.RelatorioFinanceiro;
import com.example.sghss.service.RelatorioFinanceiroService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
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
    
    // VERIFICAÇÃO VIA API

    @RestController
    @RequestMapping("/api/relatoriosFinanceiros")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public class RelatorioFinanceiroApiController {

        @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
        public ResponseEntity<List<RelatorioFinanceiro>> listarRelatoriosApi() {
            return ResponseEntity.ok(relatorioService.listarTodos());
        }

        @PostMapping(value = "/cadastrar", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
        public ResponseEntity<?> cadastrarRelatorioApi(@Valid @RequestBody RelatorioFinanceiro relatorio, BindingResult result) {
            if (result.hasErrors()) {
                return ResponseEntity.badRequest().body("Erro de validação: " + result.getAllErrors());
            }
            try {
                relatorioService.salvar(relatorio);
                return ResponseEntity.status(HttpStatus.CREATED).body(relatorio);
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao salvar o relatório financeiro.");
            }
        }

        @PutMapping(value = "/editar/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
        public ResponseEntity<?> editarRelatorioApi(@PathVariable Long id, @Valid @RequestBody RelatorioFinanceiro relatorio, BindingResult result) {
            if (result.hasErrors()) {
                return ResponseEntity.badRequest().body("Erro de validação: " + result.getAllErrors());
            }
            try {
                relatorio.setId(id);
                relatorioService.salvar(relatorio);
                return ResponseEntity.ok(relatorio);
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao editar o relatório financeiro.");
            }
        }

        @DeleteMapping(value = "/excluir/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
        public ResponseEntity<?> excluirRelatorioApi(@PathVariable Long id) {
            try {
                relatorioService.deletar(id);
                return ResponseEntity.ok("Relatório financeiro excluído com sucesso.");
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao excluir o relatório financeiro.");
            }
        }

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
	    public String salvarRelatorio(@Valid @ModelAttribute("relatorioFinanceiro") RelatorioFinanceiro relatorio, 
	                                  BindingResult result, Model model, RedirectAttributes redirectAttributes) {
	        if (result.hasErrors()) {
	            model.addAttribute("errorMessage", "Corrija os erros e tente novamente!");
	            return "cadastrarRelatorio";
	        }
	        try {
	            relatorioService.salvar(relatorio);
	            redirectAttributes.addFlashAttribute("successMessage", "Relatório Financeiro cadastrado com sucesso!");
	            return "redirect:/relatoriosFinanceiros";
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
	
	        BigDecimal totalEntrada = relatorios.stream().map(RelatorioFinanceiro::getEntrada).reduce(BigDecimal.ZERO, BigDecimal::add);
	        BigDecimal totalDespesa = relatorios.stream().map(RelatorioFinanceiro::getDespesa).reduce(BigDecimal.ZERO, BigDecimal::add);
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
	            redirectAttributes.addFlashAttribute("successMessage", "Relatório financeiro excluído com sucesso!");
	        } catch (DataIntegrityViolationException e) {
	            redirectAttributes.addFlashAttribute("errorMessage", "Não é possível excluir um relatório que está em uso.");
	        } catch (Exception e) {
	            redirectAttributes.addFlashAttribute("errorMessage", "Erro ao excluir o relatório financeiro.");
	        }
	        return "redirect:/relatoriosFinanceiros";
	    }
    }
}