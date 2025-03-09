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
        Especialidade novaEspecialidade = especialidadeRepository.save(especialidade);
        auditoriaService.registrarAcao(especialidade.getId() != null ? "Edição" : "Cadastro", "Especialidade", usuario);
        return novaEspecialidade;
    }

    public void deletar(Long id, String usuario) {
        especialidadeRepository.deleteById(id);
        auditoriaService.registrarAcao("Exclusão", "Especialidade", usuario);
    }
}
