package com.example.sghss.controller;
import com.example.sghss.model.Consulta;
import com.example.sghss.model.Paciente;
import com.example.sghss.model.Profissional;
import com.example.sghss.service.ConsultaService;
import com.example.sghss.service.PacienteService;
import com.example.sghss.service.ProfissionalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/consultas")
public class ConsultaController {

    @Autowired
    private ConsultaService consultaService;

    @Autowired
    private PacienteService pacienteService;

    @Autowired
    private ProfissionalService profissionalService;
    
    @GetMapping
    public String listarConsultas(Model model) {
        List<Consulta> lista = consultaService.listarTodas();
        model.addAttribute("consultas", lista);
        return "listarConsultas";
    }

    @GetMapping("/cadastrar")
    public String exibirFormCadastro(Model model) {
        Consulta novaConsulta = new Consulta();
        model.addAttribute("consulta", novaConsulta);
        model.addAttribute("profissionais", profissionalService.listarTodos());
        model.addAttribute("pacientes", pacienteService.listarTodos());
        return "cadastrarConsulta";
    }


    @PostMapping("/salvar")
    public String salvarConsulta(
            @ModelAttribute("consulta") Consulta consulta,
            @RequestParam("dataStr") String dataStr,
            @RequestParam("horaStr") String horaStr,
            @RequestParam("pacienteId") Long pacienteId,
            @RequestParam("profissional") Long profissionalId,
            @RequestParam("status") String status,
            Model model
    ) {
        LocalDate data = LocalDate.parse(dataStr);
        LocalTime hora = LocalTime.parse(horaStr);

        if (consulta.getId() != null) {
            Consulta consultaExistente = consultaService.buscarPorId(consulta.getId()).orElse(null);
            if (consultaExistente != null) {
                consulta.setId(consultaExistente.getId());
            }
        }

        consulta.setDataHora(LocalDateTime.of(data, hora));
        consulta.setStatus(status);

        Paciente paciente = pacienteService.buscarPorId(pacienteId);
        if (paciente == null) {
            model.addAttribute("errorMessage", "Erro: Paciente não encontrado.");
            return "mensagemErro";
        }
        consulta.setPaciente(paciente);

        Optional<Profissional> profissionalOpt = profissionalService.buscarPorId(profissionalId);
        if (profissionalOpt.isPresent()) {
            consulta.setProfissional(profissionalOpt.get());
        } else {
            model.addAttribute("errorMessage", "Erro: Profissional não encontrado.");
            return "mensagemErro";
        }

        String usuario = obterUsuarioLogado();

        boolean isEdit = (consulta.getId() != null);
        consultaService.salvar(consulta, usuario);

        model.addAttribute("successMessage", isEdit ? "Consulta atualizada com sucesso!" : "Consulta agendada com sucesso!");
        model.addAttribute("redirectUrl", "/consultas");
        return "mensagemSucesso";
    }


    @GetMapping("/editar/{id}")
    public String exibirFormEdicao(@PathVariable Long id, Model model) {
        Consulta consulta = consultaService.buscarPorId(id).orElse(null);
        if (consulta == null) {
            return "redirect:/consultas";
        }
        model.addAttribute("consulta", consulta);
        model.addAttribute("profissionais", profissionalService.listarTodos());
        model.addAttribute("pacientes", pacienteService.listarTodos());
        return "cadastrarConsulta";
    }

    @GetMapping("/excluir/{id}")
    public String excluirConsulta(@PathVariable Long id) {
    	String usuario = obterUsuarioLogado();
    	consultaService.deletar(id, usuario);
        return "redirect:/consultas";
    }
    
    @GetMapping("/visualizar/{id}")
    public String visualizarProntuario(@PathVariable Long id, Model model) {
        Paciente paciente = pacienteService.buscarPorId(id);
        if (paciente == null) {
            return "redirect:/pacientes";
        }

        model.addAttribute("paciente", paciente);
        return "visualizarProntuario";
    }
    
    @GetMapping("/cancelar/{id}")
    public String cancelarConsulta(@PathVariable Long id) {
        String usuario = obterUsuarioLogado();
        consultaService.cancelarConsulta(id, usuario);
        return "redirect:/consultas";
    }
    
    private String obterUsuarioLogado() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        }
        return "Desconhecido";
    }

}
