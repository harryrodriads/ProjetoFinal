package com.example.sghss.service;

import com.example.sghss.model.Leito;
import com.example.sghss.repository.LeitoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

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
        boolean isNovoLeito = leito.getId() == null;

        Leito novoLeito = leitoRepository.save(leito);

        String acao = isNovoLeito ? "Cadastro: "  + novoLeito.getNumero() : "Edição: "  + novoLeito.getNumero();
        String entidade = "Leito";

        auditoriaService.registrarAcao(acao, entidade, usuario);

        return novoLeito;
    }

    public void deletar(Long id, String usuario) {
        Optional<Leito> leitoOpt = leitoRepository.findById(id);

        if (leitoOpt.isPresent()) {
            Leito leito = leitoOpt.get();
            String entidade = "Leito";

            leitoRepository.deleteById(id);
            auditoriaService.registrarAcao("Exclusão: " + leito.getNumero(), entidade, usuario);
        }
    }
}
