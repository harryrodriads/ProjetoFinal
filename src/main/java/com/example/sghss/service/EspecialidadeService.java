package com.example.sghss.service;
import com.example.sghss.model.Especialidade;
import com.example.sghss.repository.EspecialidadeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class EspecialidadeService {

    @Autowired
    private EspecialidadeRepository especialidadeRepository;

    @Autowired
    private AuditoriaService auditoriaService;

    public List<Especialidade> listarTodas() {
        return especialidadeRepository.findAll();
    }

    public Optional<Especialidade> buscarPorId(Long id) {
        return especialidadeRepository.findById(id);
    }

    public Especialidade salvar(Especialidade especialidade, String usuario) {
        boolean isEdicao = especialidade.getId() != null;
        Especialidade novaEspecialidade = especialidadeRepository.save(especialidade);
        
        String acao = isEdicao ? "Edição: " + novaEspecialidade.getNome(): "Cadastro: " + novaEspecialidade.getNome();
        auditoriaService.registrarAcao(acao, "Especialidade", usuario);
        
        return novaEspecialidade;
    }

    public void deletar(Long id, String usuario) {
        Optional<Especialidade> especialidadeOpt = especialidadeRepository.findById(id);

        if (especialidadeOpt.isPresent()) {
            Especialidade especialidade = especialidadeOpt.get();

            if (!especialidade.getProfissionais().isEmpty()) {
                throw new IllegalStateException("Não é possível excluir a especialidade pois existem profissionais associados.");
            }

            especialidadeRepository.deleteById(id);
            auditoriaService.registrarAcao("Exclusão: " + especialidade.getNome(), "Especialidade", usuario);
        }
    }
}
