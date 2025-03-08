package com.example.sghss.service;
import com.example.sghss.model.Profissional;
import com.example.sghss.repository.ProfissionalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Optional;

@Service
public class ProfissionalService {

    @Autowired
    private ProfissionalRepository profissionalRepository;

    public List<Profissional> listarTodos() {
        return profissionalRepository.findAll();
    }

    public Optional<Profissional> buscarPorId(Long id) {
        return profissionalRepository.findById(id);
    }

    public boolean salvar(Profissional profissional) {
        if (profissional.getId() == null && profissionalRepository.existsByRegistroProf(profissional.getRegistroProf())) {
            return false;
        }
        profissionalRepository.save(profissional);
        return true;
    }

    
    public void excluir(Long id) {
        profissionalRepository.deleteById(id);
    }
}
