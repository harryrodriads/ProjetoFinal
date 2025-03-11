package com.example.sghss.service;
import com.example.sghss.model.Profissional;
import com.example.sghss.repository.ConsultaRepository;
import com.example.sghss.repository.ProfissionalRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class ProfissionalService {

    @Autowired
    private ProfissionalRepository profissionalRepository;

    @Autowired
    private AuditoriaService auditoriaService;

    public List<Profissional> listarTodos() {
        return profissionalRepository.findAll();
    }

    public Optional<Profissional> buscarPorId(Long id) {
        return profissionalRepository.findById(id);
    }

    public boolean salvar(Profissional profissional, String usuario) {
        boolean isNovoProfissional = profissional.getId() == null;

        if (isNovoProfissional && profissionalRepository.existsByRegistroProf(profissional.getRegistroProf())) {
            return false; 
        }

        Profissional novoProfissional = profissionalRepository.save(profissional);

        String acao = isNovoProfissional ? "Cadastro: " + novoProfissional.getNome() : "Edição: " + novoProfissional.getNome();
        String entidade = "Profissional";

        auditoriaService.registrarAcao(acao, entidade, usuario);

        return true;
    }

    @Autowired
    private ConsultaRepository consultaRepository;

    @Transactional
    public void excluir(Long id, String usuario) {
        Optional<Profissional> profissionalOpt = profissionalRepository.findById(id);

        if (profissionalOpt.isPresent()) {
            Profissional profissional = profissionalOpt.get();
            String entidade = "Profissional";

            consultaRepository.deleteAllByProfissional(profissional);
            profissionalRepository.delete(profissional);
            auditoriaService.registrarAcao("Exclusão: " + profissional.getNome(), entidade, usuario);
        }
    }

}
