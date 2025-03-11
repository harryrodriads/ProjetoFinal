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
        boolean isNovaInternacao = internacao.getId() == null;

        Internacao novaInternacao = internacaoRepository.save(internacao);
        String nomePaciente = (novaInternacao.getPaciente() != null) ? novaInternacao.getPaciente().getNome() : "Paciente Desconhecido";

        String acao = isNovaInternacao ? "Cadastro: " + nomePaciente : "Edição: " + nomePaciente;
        String entidade = "Internação";

        auditoriaService.registrarAcao(acao, entidade, usuario);

        return novaInternacao;
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
