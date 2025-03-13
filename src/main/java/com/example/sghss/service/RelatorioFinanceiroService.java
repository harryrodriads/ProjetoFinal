package com.example.sghss.service;
import com.example.sghss.model.RelatorioFinanceiro;
import com.example.sghss.repository.RelatorioFinanceiroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class RelatorioFinanceiroService {

    @Autowired
    private RelatorioFinanceiroRepository relatorioFinanceiroRepository;
    
    @Autowired
    private RelatorioFinanceiroRepository relatorioRepository;

    public List<RelatorioFinanceiro> listarTodos() {
        return relatorioFinanceiroRepository.findAll();
    }

    public RelatorioFinanceiro salvar(RelatorioFinanceiro relatorio) {
        return relatorioFinanceiroRepository.save(relatorio);
    }

    public void deletar(Long id) {
        relatorioFinanceiroRepository.deleteById(id);
    }
    
    public Optional<RelatorioFinanceiro> buscarPorId(Long id) {
        return relatorioRepository.findById(id);
    }
}
