package com.example.sghss.service;
import com.example.sghss.model.Leito;
import com.example.sghss.repository.LeitoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LeitoService {

    @Autowired
    private LeitoRepository leitoRepository;

    @Autowired
    private AuditoriaService auditoriaService;

    public List<Leito> listarTodos() {
        return leitoRepository.findAll();
    }

    public Leito salvar(Leito leito, String usuario) {
        Leito novoLeito = leitoRepository.save(leito);
        auditoriaService.registrarAcao("Cadastro", "Leito", usuario);
        return novoLeito;
    }

    public void deletar(Long id, String usuario) {
        leitoRepository.deleteById(id);
        auditoriaService.registrarAcao("Exclusão", "Leito", usuario);
    }
}
