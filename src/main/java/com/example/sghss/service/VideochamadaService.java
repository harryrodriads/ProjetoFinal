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

    @Autowired
    private AuditoriaService auditoriaService;

    public List<Videochamada> listarTodas() {
        return videochamadaRepository.findAll();
    }

    public Optional<Videochamada> buscarPorId(Long id) {
        return videochamadaRepository.findById(id);
    }

    public Videochamada salvar(Videochamada videochamada, String usuario) {
        boolean isNovaVideochamada = videochamada.getId() == null;

        Videochamada novaVideochamada = videochamadaRepository.save(videochamada);

        String nomePaciente = (novaVideochamada.getConsulta() != null && novaVideochamada.getConsulta().getPaciente() != null)
                ? novaVideochamada.getConsulta().getPaciente().getNome()
                : "Paciente Desconhecido";

        String acao = isNovaVideochamada ? "Cadastro: "  + nomePaciente : "Edição: "+ nomePaciente;
        String entidade = "Videochamada";

        auditoriaService.registrarAcao(acao, entidade, usuario);

        return novaVideochamada;
    }

    public void deletar(Long id, String usuario) {
        Optional<Videochamada> videochamadaOpt = videochamadaRepository.findById(id);

        if (videochamadaOpt.isPresent()) {
            Videochamada videochamada = videochamadaOpt.get();

            String nomePaciente = (videochamada.getConsulta() != null && videochamada.getConsulta().getPaciente() != null)
                    ? videochamada.getConsulta().getPaciente().getNome()
                    : "Paciente Desconhecido";

            String entidade = "Videochamada";

            videochamadaRepository.deleteById(id);
            auditoriaService.registrarAcao("Exclusão: " + nomePaciente, entidade, usuario);
        }
    }
}
