package com.example.sghss.controller;
import com.example.sghss.model.Consulta;
import com.example.sghss.model.Paciente;
import com.example.sghss.model.Profissional;
import com.example.sghss.model.Prontuario;
import com.example.sghss.model.Usuario;
import com.example.sghss.service.ConsultaService;
import com.example.sghss.service.PacienteService;
import com.example.sghss.service.ProfissionalService;
import com.example.sghss.service.ProntuarioService;
import com.example.sghss.service.UsuarioService;
import com.example.sghss.service.VideochamadaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import java.security.Principal;
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
    
    @Autowired
    private UsuarioService usuarioService;
    
    @Autowired
    private VideochamadaService videochamadaService;

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

    @GetMapping("/cancelar/{id}")
    public String cancelarConsulta(@PathVariable Long id) {
        String usuario = obterUsuarioLogado();
        consultaService.cancelarConsulta(id, usuario);
        return "redirect:/consultas";
    }
    
    @GetMapping("/paciente/cancelar/{id}")
    @PreAuthorize("hasRole('PACIENTE')")
    public String cancelarConsultaComoPaciente(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        String usuario = obterUsuarioLogado();
        consultaService.cancelarConsulta(id, usuario);

        redirectAttributes.addFlashAttribute("successMessage", "Consulta cancelada com sucesso.");
        return "redirect:/";
    }

    
    private String obterUsuarioLogado() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        }
        return "Desconhecido";
    }
    
    @GetMapping("/paciente/agendar-consulta")
    @PreAuthorize("hasRole('PACIENTE')")
    public String exibirFormularioConsultaComCadastro(Model model, Principal principal) {
        String username = principal.getName();
        Usuario usuario = usuarioService.buscarPorUsername(username)
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado: " + username));

        Paciente paciente = usuario.getPaciente();

        if (paciente == null) {
            paciente = new Paciente();
            paciente.setNome(usuario.getNome());
            paciente.setCpf(usuario.getCpf());
            paciente.setTelefone(usuario.getTelefone());
            paciente.setDataNascimento(usuario.getDataNascimento());

            paciente = pacienteService.salvar(paciente, username);

            usuario.setPaciente(paciente);
            usuarioService.salvar(usuario, username);
        }

        Consulta consulta = new Consulta();
        consulta.setPaciente(paciente);

        model.addAttribute("consulta", consulta);
        model.addAttribute("profissionais", profissionalService.listarTodos());

        return "formAgendamentoComCadastro";
    }


    
    @PostMapping("/paciente/salvar-agendamento")
    @PreAuthorize("hasRole('PACIENTE')")
    public String salvarAgendamentoComCadastro(
            @ModelAttribute("consulta") Consulta consulta,
            @RequestParam("dataStr") String dataStr,
            @RequestParam("horaStr") String horaStr,
            @RequestParam("profissional") Long profissionalId,
            Model model,
            Principal principal
    ) {
        String username = principal.getName();
        Usuario usuario = usuarioService.buscarPorUsername(username)
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado: " + username));
        Paciente pacienteSalvo = usuario.getPaciente();

        if (pacienteSalvo == null) {
            model.addAttribute("errorMessage", "Erro: Paciente não vinculado ao usuário.");
            return "mensagemErro";
        }

        consulta.setPaciente(pacienteSalvo);
        consulta.setDataHora(LocalDateTime.of(LocalDate.parse(dataStr), LocalTime.parse(horaStr)));
        consulta.setStatus("Agendada");

        Optional<Profissional> profissionalOpt = profissionalService.buscarPorId(profissionalId);
        if (profissionalOpt.isEmpty()) {
            model.addAttribute("errorMessage", "Erro: Profissional não encontrado.");
            return "mensagemErro";
        }

        consulta.setProfissional(profissionalOpt.get());

        boolean isEdit = (consulta.getId() != null);
        consultaService.salvar(consulta, username);

        Prontuario prontuario = prontuarioService.buscarPorPacienteId(pacienteSalvo.getId())
            .orElse(new Prontuario());
        prontuario.setPaciente(pacienteSalvo);
        prontuario.setDataAtualizacao(LocalDateTime.now());
        prontuarioService.salvar(prontuario, username);

        model.addAttribute("successMessage", isEdit ? "Consulta atualizada com sucesso!" : "Consulta agendada com sucesso!");
        model.addAttribute("redirectUrl", "/");
        return "mensagemSucesso";
    }
    
    @GetMapping("/paciente/index")
    @PreAuthorize("hasRole('PACIENTE')")
    public String exibirPortalPaciente(Model model, Principal principal) {
        String username = principal.getName();

        Usuario usuario = usuarioService.buscarPorUsername(username)
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        Paciente paciente = usuario.getPaciente();
        if (paciente == null) {
            throw new RuntimeException("Paciente não vinculado ao usuário.");
        }

        List<Consulta> consultas = consultaService.buscarPorPacienteId(paciente.getId());

        model.addAttribute("consultas", consultas);
        model.addAttribute("prontuario", prontuarioService.buscarPorPacienteId(paciente.getId()).orElse(null));
        model.addAttribute("videochamadas", videochamadaService.buscarPorPacienteId(paciente.getId()));

        System.out.println("Consultas encontradas: " + consultas.size());
        for (Consulta c : consultas) {
            System.out.println("Consulta: ID=" + c.getId() + " | Data=" + c.getDataHora());
        }

        return "paciente/index";
    }
}
