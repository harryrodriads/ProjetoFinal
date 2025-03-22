package com.example.sghss.service;
import com.example.sghss.model.Consulta;
import com.example.sghss.model.Videochamada;
import com.example.sghss.repository.ConsultaRepository;
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
    
    @Autowired
    private ConsultaRepository consultaRepository;

    public List<Videochamada> listarTodas() {
        return videochamadaRepository.findAll();
    }

    public Optional<Videochamada> buscarPorId(Long id) {
        return videochamadaRepository.findById(id);
    }

    public Videochamada salvar(Videochamada videochamada, String usuario) {
        boolean isNovaVideochamada = videochamada.getId() == null || !videochamadaRepository.existsById(videochamada.getId());

        Videochamada novaVideochamada = videochamadaRepository.save(videochamada);
        Optional<Consulta> consultaOpt = consultaRepository.findById(novaVideochamada.getConsulta().getId());
        
        String nomePaciente = "Paciente Desconhecido";
        if (consultaOpt.isPresent()) {
            Consulta consulta = consultaOpt.get();
            if (consulta.getPaciente() != null && consulta.getPaciente().getNome() != null) {
                nomePaciente = consulta.getPaciente().getNome();
            }
        }

        String acao = isNovaVideochamada ? "Cadastro: " + nomePaciente : "Edição: " + nomePaciente;
        String entidade = "Videochamada";

        auditoriaService.registrarAcao(acao, entidade, usuario);

        return novaVideochamada;
    }
    
    public void deletar(Long id, String usuario) {
        Optional<Videochamada> videochamadaOpt = videochamadaRepository.findById(id);

        if (videochamadaOpt.isPresent()) {
            Videochamada videochamada = videochamadaOpt.get();
            Optional<Consulta> consultaOpt = consultaRepository.findById(videochamada.getConsulta().getId());

            String nomePaciente = "Paciente Desconhecido";
            if (consultaOpt.isPresent()) {
                Consulta consulta = consultaOpt.get();
                if (consulta.getPaciente() != null && consulta.getPaciente().getNome() != null) {
                    nomePaciente = consulta.getPaciente().getNome();
                }
            }

            String entidade = "Videochamada";
            videochamadaRepository.deleteById(id);
            auditoriaService.registrarAcao("Exclusão: " + nomePaciente, entidade, usuario);
        }
    }
    
    public List<Videochamada> buscarPorPacienteId(Long pacienteId) {
        return videochamadaRepository.findByConsultaPacienteId(pacienteId);
    }
}
