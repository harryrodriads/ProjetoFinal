package com.example.sghss.controller;
import com.example.sghss.model.Consulta;
import com.example.sghss.model.Paciente;
import com.example.sghss.model.Profissional;
import com.example.sghss.model.Prontuario;
import com.example.sghss.service.ConsultaService;
import com.example.sghss.service.PacienteService;
import com.example.sghss.service.ProfissionalService;
import com.example.sghss.service.ProntuarioService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

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
    
    @Autowired
    private ProntuarioService prontuarioService;

    // VERIFICAÇÃO VIA API
    
    @RestController
    @RequestMapping("/api/consultas")
    public class ConsultaApiController {

        @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
        public ResponseEntity<List<Consulta>> listarConsultasApi() {
            return ResponseEntity.ok(consultaService.listarTodas());
        }

        @PostMapping(value = "/cadastrar", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
        public ResponseEntity<?> cadastrarConsultaApi(@Valid @RequestBody Consulta consulta, BindingResult result) {
            if (result.hasErrors()) {
                return ResponseEntity.badRequest().body("Erro de validação: " + result.getAllErrors());
            }
            String usuario = obterUsuarioLogado();
            consultaService.salvar(consulta, usuario);
            return ResponseEntity.status(HttpStatus.CREATED).body(consulta);
        }

        @PutMapping(value = "/editar/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
        public ResponseEntity<?> editarConsultaApi(@PathVariable Long id, @Valid @RequestBody Consulta consulta, BindingResult result) {
            if (result.hasErrors()) {
                return ResponseEntity.badRequest().body("Erro de validação: " + result.getAllErrors());
            }
            Optional<Consulta> consultaExistente = consultaService.buscarPorId(id);
            if (consultaExistente.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Consulta não encontrada.");
            }
            consulta.setId(id);
            String usuario = obterUsuarioLogado();
            consultaService.salvar(consulta, usuario);
            return ResponseEntity.ok(consulta);
        }

        @DeleteMapping(value = "/excluir/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
        public ResponseEntity<?> excluirConsultaApi(@PathVariable Long id) {
            String usuario = obterUsuarioLogado();
            consultaService.deletar(id, usuario);
            return ResponseEntity.ok("Consulta excluída com sucesso.");
        }
    }

    // FRONT-END HTML

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
        Prontuario prontuario = prontuarioService.buscarPorPacienteId(pacienteId).orElse(new Prontuario());
        prontuario.setPaciente(paciente);
        prontuario.setDataAtualizacao(LocalDateTime.now());

        prontuarioService.salvar(prontuario, usuario);

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