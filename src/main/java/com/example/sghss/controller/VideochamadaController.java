package com.example.sghss.controller;
import com.example.sghss.model.Consulta;
import com.example.sghss.model.Paciente;
import com.example.sghss.model.Profissional;
import com.example.sghss.model.StatusVideo;
import com.example.sghss.model.Usuario;
import com.example.sghss.model.Videochamada;
import com.example.sghss.service.ConsultaService;
import com.example.sghss.service.ProfissionalService;
import com.example.sghss.service.UsuarioService;
import com.example.sghss.service.VideochamadaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/videochamadas")
public class VideochamadaController {

    @Autowired
    private VideochamadaService videochamadaService;

    @Autowired
    private ConsultaService consultaService;
    
    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private ProfissionalService profissionalService;

    // FRONT-END HTML

    @GetMapping
    public String listarTodas(Model model) {
        List<Videochamada> videochamadas = videochamadaService.listarTodas();
        model.addAttribute("videochamadas", videochamadas);
        return "videochamadas"; 
    }

    @GetMapping("/{id}")
    public String visualizar(@PathVariable Long id, Model model) {
        Optional<Videochamada> videochamada = videochamadaService.buscarPorId(id);
        videochamada.ifPresent(v -> model.addAttribute("videochamada", v));
        return "detalhesVideochamada"; 
    }

    @GetMapping("/cadastrar")
    public String cadastrarForm(Model model) {
        model.addAttribute("videochamada", new Videochamada());
        model.addAttribute("consultas", consultaService.listarTodas());
        model.addAttribute("statusList", StatusVideo.values());
        return "cadastrarVideochamada"; 
    }

    @GetMapping("/editar/{id}")
    public String editarForm(@PathVariable Long id, Model model) {
        Optional<Videochamada> videochamadaOpt = videochamadaService.buscarPorId(id);
        if (videochamadaOpt.isPresent()) {
            Videochamada videochamada = videochamadaOpt.get();
            model.addAttribute("videochamada", videochamada);
            model.addAttribute("consultas", consultaService.listarTodas());
            model.addAttribute("consultaSelecionada", 
                videochamada.getConsulta() != null ? videochamada.getConsulta().getId() : null);
            model.addAttribute("statusList", StatusVideo.values());
            return "cadastrarVideochamada";
        }
        return "redirect:/videochamadas";
    }

    @PostMapping("/salvar")
    public String salvar(@ModelAttribute Videochamada videochamada, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String usuarioLogado = authentication.getName();

        if (videochamada.getId() != null) {
            Optional<Videochamada> existenteOpt = videochamadaService.buscarPorId(videochamada.getId());
            if (existenteOpt.isPresent()) {
                Videochamada existente = existenteOpt.get();
                existente.setUrlSala(videochamada.getUrlSala());
                existente.setStatus(videochamada.getStatus());
                existente.setConsulta(videochamada.getConsulta());
                videochamadaService.salvar(existente, usuarioLogado);
                model.addAttribute("successMessage", "Videochamada atualizada com sucesso!");
            }
        } else {
            videochamadaService.salvar(videochamada, usuarioLogado);
            model.addAttribute("successMessage", "Videochamada cadastrada com sucesso!");
        }

        model.addAttribute("redirectUrl", "/videochamadas");
        return "mensagemSucesso"; 
    }

    @GetMapping("/excluir/{id}")
    public String excluir(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String usuarioLogado = authentication.getName();

        try {
            videochamadaService.deletar(id, usuarioLogado);
            redirectAttributes.addFlashAttribute("sucesso", "Videochamada excluída com sucesso.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", "Erro ao excluir videochamada: " + e.getMessage());
        }

        return "redirect:/videochamadas";
    }


    @GetMapping("/salaVideo")
    public String salaVideo() {
        return "salaVideo";
    }
    
    @GetMapping("/paciente/agendar")
    @PreAuthorize("hasRole('PACIENTE')")
    public String exibirFormularioAgendamentoVideochamada(Model model, Principal principal) {
        String username = principal.getName();
        Usuario usuario = usuarioService.buscarPorUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        Paciente paciente = usuario.getPaciente();

        Videochamada videochamada = new Videochamada();
        Consulta consulta = new Consulta();
        consulta.setPaciente(paciente);
        videochamada.setConsulta(consulta);

        model.addAttribute("videochamada", videochamada);
        model.addAttribute("profissionais", profissionalService.listarTodos());

        return "videochamadaPaciente";
    }

    
    @PostMapping("/paciente/salvar")
    @PreAuthorize("hasRole('PACIENTE')")
    public String salvarVideochamada(
            @ModelAttribute("videochamada") Videochamada videochamada,
            @RequestParam("dataStr") String dataStr,
            @RequestParam("horaStr") String horaStr,
            @RequestParam("consulta.profissional") Long profissionalId,
            Principal principal,
            Model model
    ) {
        String username = principal.getName();
        Usuario usuario = usuarioService.buscarPorUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        Paciente paciente = usuario.getPaciente();
        Profissional profissional = profissionalService.buscarPorId(profissionalId)
                .orElseThrow(() -> new RuntimeException("Profissional não encontrado"));

        Consulta consulta = new Consulta();
        consulta.setDataHora(LocalDateTime.of(LocalDate.parse(dataStr), LocalTime.parse(horaStr)));
        consulta.setPaciente(paciente);
        consulta.setProfissional(profissional);
        consulta.setStatus("Agendada");

        consultaService.salvar(consulta, username);

        videochamada.setConsulta(consulta);
        videochamada.setStatus(StatusVideo.AGENDADA);

        videochamadaService.salvar(videochamada, username);

        model.addAttribute("successMessage", "Videochamada agendada com sucesso!");
        model.addAttribute("redirectUrl", "/");

        return "mensagemSucesso";
    }

    // VERIFICAÇÃO VIA API

    @RestController
    @RequestMapping("/api/videochamadas")
    public class VideochamadasApiController {

        @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
        public ResponseEntity<List<Videochamada>> listarVideochamadasApi() {
            return ResponseEntity.ok(videochamadaService.listarTodas());
        }

        @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
        public ResponseEntity<?> visualizarVideochamadaApi(@PathVariable Long id) {
            Videochamada videochamada = videochamadaService.buscarPorId(id).orElse(null);
            if (videochamada == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Videochamada não encontrada.");
            }
            return ResponseEntity.ok(videochamada);
        }

        @PostMapping(value = "/cadastrar", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
        public ResponseEntity<?> cadastrarVideochamadaApi(@Valid @RequestBody Videochamada videochamada, BindingResult result) {
            if (result.hasErrors()) {
                return ResponseEntity.badRequest().body("Erro de validação: " + result.getAllErrors());
            }
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String usuarioLogado = authentication.getName();
            videochamadaService.salvar(videochamada, usuarioLogado);
            return ResponseEntity.status(HttpStatus.CREATED).body(videochamada);
        }

        @PutMapping(value = "/editar/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
        public ResponseEntity<?> editarVideochamadaApi(@PathVariable Long id, @Valid @RequestBody Videochamada videochamada, BindingResult result) {
            if (result.hasErrors()) {
                return ResponseEntity.badRequest().body("Erro de validação: " + result.getAllErrors());
            }
            Videochamada existente = videochamadaService.buscarPorId(id).orElse(null);
            if (existente == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Videochamada não encontrada.");
            }
            existente.setUrlSala(videochamada.getUrlSala());
            existente.setStatus(videochamada.getStatus());
            existente.setConsulta(videochamada.getConsulta());
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String usuarioLogado = authentication.getName();
            videochamadaService.salvar(existente, usuarioLogado);
            return ResponseEntity.ok(existente);
        }

        @DeleteMapping(value = "/excluir/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
        public ResponseEntity<?> excluirVideochamadaApi(@PathVariable Long id) {
            Videochamada videochamada = videochamadaService.buscarPorId(id).orElse(null);
            if (videochamada == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Videochamada não encontrada.");
            }
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String usuarioLogado = authentication.getName();
            videochamadaService.deletar(id, usuarioLogado);
            return ResponseEntity.ok("Videochamada excluída com sucesso.");
        }
    }
}
