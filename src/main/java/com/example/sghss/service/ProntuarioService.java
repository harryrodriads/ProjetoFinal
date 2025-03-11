package com.example.sghss.service;
import com.example.sghss.model.Prontuario;
import com.example.sghss.repository.ProntuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class ProntuarioService {

    @Autowired
    private ProntuarioRepository prontuarioRepository;

    @Autowired
    private AuditoriaService auditoriaService;

    public Optional<Prontuario> buscarPorPacienteId(Long pacienteId) {
        return prontuarioRepository.findByPacienteId(pacienteId);
    }

    public Prontuario salvar(Prontuario prontuario, String usuario) {
        boolean isNovoProntuario = prontuario.getId() == null;

        Prontuario novoProntuario = prontuarioRepository.save(prontuario);

        String nomePaciente = (novoProntuario.getPaciente() != null) ? novoProntuario.getPaciente().getNome() : "Paciente Desconhecido";

        String acao = isNovoProntuario ? "Cadastro: " + nomePaciente : "Edição: " + nomePaciente;
        String entidade = "Prontuário" ;

        auditoriaService.registrarAcao(acao, entidade, usuario);

        return novoProntuario;
    }
}
