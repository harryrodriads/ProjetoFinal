package com.example.sghss.service;
import com.example.sghss.model.EstoqueSuprimento;
import com.example.sghss.repository.EstoqueSuprimentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class EstoqueSuprimentoService {

    @Autowired
    private EstoqueSuprimentoRepository estoqueSuprimentoRepository;

    public List<EstoqueSuprimento> listarTodos() {
        return estoqueSuprimentoRepository.findAll();
    }

    public Optional<EstoqueSuprimento> buscarPorId(Long id) {
        return estoqueSuprimentoRepository.findById(id);
    }

    public EstoqueSuprimento salvar(EstoqueSuprimento estoque) {
        return estoqueSuprimentoRepository.save(estoque);
    }

    public void deletar(Long id) {
        estoqueSuprimentoRepository.deleteById(id);
    }
}
