package com.example.sghss.service;
import com.example.sghss.model.Videochamada;
import com.example.sghss.repository.VideochamadaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class VideochamadaService {

    @Autowired
    private VideochamadaRepository videochamadaRepository;

    public List<Videochamada> listarTodas() {
        return videochamadaRepository.findAll();
    }

    public Optional<Videochamada> buscarPorId(Long id) {
        return videochamadaRepository.findById(id);
    }

    public Videochamada salvar(Videochamada videochamada) {
        return videochamadaRepository.save(videochamada);
    }

    public void deletar(Long id) {
        videochamadaRepository.deleteById(id);
    }
}
