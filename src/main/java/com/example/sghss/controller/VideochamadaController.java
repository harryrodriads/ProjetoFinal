package com.example.sghss.controller;
import com.example.sghss.model.StatusVideo;
import com.example.sghss.model.Videochamada;
import com.example.sghss.service.ConsultaService;
import com.example.sghss.service.VideochamadaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
}
