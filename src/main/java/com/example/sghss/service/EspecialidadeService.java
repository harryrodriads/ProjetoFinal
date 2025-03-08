package com.example.sghss.service;
import com.example.sghss.model.Especialidade;
import com.example.sghss.repository.EspecialidadeRepository;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EspecialidadeService {

	@Autowired
    private EspecialidadeRepository especialidadeRepository;

    public List<Especialidade> listarTodas() {
        return especialidadeRepository.findAll();
    }

    public Optional<Especialidade> buscarPorId(Long id) {
        return especialidadeRepository.findById(id);
    }

    public void salvar(Especialidade especialidade) {
        especialidadeRepository.save(especialidade);
    }

    @Transactional
    public void excluir(Long id) {
            especialidadeRepository.deleteById(id);
        }
}
