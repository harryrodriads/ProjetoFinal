package com.example.sghss.service;
import com.example.sghss.model.Paciente;
import com.example.sghss.repository.InternacaoRepository;
import com.example.sghss.repository.PacienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class PacienteService {

    @Autowired
    private PacienteRepository pacienteRepository;

    @Autowired
    private AuditoriaService auditoriaService;
    
    @Autowired
    private InternacaoRepository internacaoRepository;

    public List<Paciente> listarTodos() {
        return pacienteRepository.findAll();
    }

    public Paciente salvar(Paciente paciente, String usuario) {
        boolean isNovoPaciente = paciente.getId() == null;

        Paciente novoPaciente = pacienteRepository.save(paciente);
        String acao = isNovoPaciente ? "Cadastro: " + novoPaciente.getNome() : "Edição: " + novoPaciente.getNome();
        String entidade = "Paciente";

        auditoriaService.registrarAcao(acao, entidade, usuario);

        return novoPaciente;
    }

    public void excluir(Long id, String usuario) {
        Optional<Paciente> pacienteOpt = pacienteRepository.findById(id);

        if (pacienteOpt.isPresent()) {
            long qtdInternacoes = internacaoRepository.countInternacoesPorPaciente(id);
            if (qtdInternacoes > 0) {
                throw new DataIntegrityViolationException("Não pode excluir um paciente que tem uma Internação 'Em Andamento'.");
            }
            Paciente paciente = pacienteOpt.get();
            pacienteRepository.deleteById(id);
            auditoriaService.registrarAcao("Exclusão: " + paciente.getNome(), "Paciente", usuario);
        }
    }

    public Paciente buscarPorUsuario(String username) {
        return pacienteRepository.findByUsuarioUsername(username)
            .orElseThrow(() -> new RuntimeException("Paciente não encontrado para o usuário: " + username));
    }


    public Paciente buscarPorId(Long id) {
        return pacienteRepository.findById(id).orElse(null);
    }
}
