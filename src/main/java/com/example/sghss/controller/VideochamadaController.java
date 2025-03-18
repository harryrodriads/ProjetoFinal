package com.example.sghss.controller;
import com.example.sghss.model.StatusVideo;
import com.example.sghss.model.Videochamada;
import com.example.sghss.service.ConsultaService;
import com.example.sghss.service.VideochamadaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/videochamadas") 
public class VideochamadaController {

    @Autowired
    private VideochamadaService videochamadaService;

    @Autowired
    private ConsultaService consultaService;

    // Rotas para o Front-end (HTML)

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
            if (videochamada.getConsulta() != null) {
                model.addAttribute("consultaSelecionada", videochamada.getConsulta().getId());
            } else {
                model.addAttribute("consultaSelecionada", null);
            }
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
    public String excluir(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String usuarioLogado = authentication.getName();
        videochamadaService.deletar(id, usuarioLogado);
        return "redirect:/videochamadas";
    }

    @GetMapping("/salaVideo")
    public String salaVideo() {
        return "salaVideo";
    }

    // Rotas para a API (JSON)

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