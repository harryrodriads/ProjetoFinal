package com.example.sghss.service;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import com.example.sghss.model.RelatorioFinanceiro;
import com.example.sghss.repository.RelatorioFinanceiroRepository;

@SpringBootTest
public class RelatorioFinanceiroServiceTest {

    @Autowired
    private RelatorioFinanceiroService relatorioService;

    @MockBean
    private RelatorioFinanceiroRepository relatorioRepository;

    @Test
    public void CT029_deveSalvarRelatorioFinanceiro() {
        RelatorioFinanceiro relatorio = new RelatorioFinanceiro();
        relatorio.setTitulo("Relatório Março");

        when(relatorioRepository.save(any())).thenReturn(relatorio);

        RelatorioFinanceiro salvo = relatorioService.salvar(relatorio);

        assertEquals("Relatório Março", salvo.getTitulo());
    }

    @Test
    public void CT030_deveExcluirRelatorio() {
        relatorioService.deletar(1L);
        verify(relatorioRepository).deleteById(1L);
    }

    @Test
    public void CT031_deveBuscarRelatorioPorId() {
        RelatorioFinanceiro relatorio = new RelatorioFinanceiro();
        relatorio.setId(1L);
        relatorio.setTitulo("Resumo Geral");

        when(relatorioRepository.findById(1L)).thenReturn(Optional.of(relatorio));

        Optional<RelatorioFinanceiro> encontrado = relatorioService.buscarPorId(1L);

        assertTrue(encontrado.isPresent());
        assertEquals("Resumo Geral", encontrado.get().getTitulo());
    }
}
