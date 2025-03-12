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

    @Autowired
    private AuditoriaService auditoriaService;

    public List<Internacao> listarTodas() {
        return internacaoRepository.findAll();
    }

    public Optional<Internacao> buscarPorId(Long id) {
        return internacaoRepository.findById(id);
    }

    public Internacao salvar(Internacao internacao, String usuario) {
        if (internacao.getId() != null) {
            Optional<Internacao> existente = internacaoRepository.findById(internacao.getId());
            if (existente.isPresent()) {
                Internacao internacaoAtualizada = existente.get();
                internacaoAtualizada.setPaciente(internacao.getPaciente());
                internacaoAtualizada.setProfissional(internacao.getProfissional());
                internacaoAtualizada.setLeito(internacao.getLeito());
                return internacaoRepository.save(internacaoAtualizada);
            }
        }
        return internacaoRepository.save(internacao);
    }

    public void deletar(Long id, String usuario) {
        Optional<Internacao> internacaoOpt = internacaoRepository.findById(id);

        if (internacaoOpt.isPresent()) {
            Internacao internacao = internacaoOpt.get();
            String entidade = "Internação";
            String nomePaciente = (internacao.getPaciente() != null) ? internacao.getPaciente().getNome() : "Paciente Desconhecido";

            internacaoRepository.deleteById(id);
            auditoriaService.registrarAcao("Exclusão: " + nomePaciente, entidade, usuario);
        }
    }
}
