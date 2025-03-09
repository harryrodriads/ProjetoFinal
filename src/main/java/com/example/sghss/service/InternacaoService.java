package com.example.sghss.service;
import com.example.sghss.model.Internacao;
import com.example.sghss.repository.InternacaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class InternacaoService {

    @Autowired
    private InternacaoRepository internacaoRepository;

    public List<Internacao> listarTodas() {
        return internacaoRepository.findAll();
    }

    public Optional<Internacao> buscarPorId(Long id) {
        return internacaoRepository.findById(id);
    }

    public Internacao salvar(Internacao internacao) {
        return internacaoRepository.save(internacao);
    }

    public void deletar(Long id) {
        internacaoRepository.deleteById(id);
    }
}
