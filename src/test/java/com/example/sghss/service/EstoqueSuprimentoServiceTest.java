package com.example.sghss.service;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.util.List;
import com.example.sghss.model.EstoqueSuprimento;
import com.example.sghss.repository.EstoqueSuprimentoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
public class EstoqueSuprimentoServiceTest {

    @Autowired
    private EstoqueSuprimentoService estoqueService;

    @MockBean
    private EstoqueSuprimentoRepository estoqueRepository;

    @Test
    public void CT032_deveCadastrarSuprimento() {
        EstoqueSuprimento s = new EstoqueSuprimento();
        s.setProduto("Álcool 70%");
        s.setQuantidade(20);

        when(estoqueRepository.save(any())).thenReturn(s);

        EstoqueSuprimento salvo = estoqueService.salvar(s);
        assertEquals("Álcool 70%", salvo.getProduto());
    }

    @Test
    public void CT033_deveEditarSuprimento() {
        EstoqueSuprimento s = new EstoqueSuprimento();
        s.setId(1L);
        s.setProduto("Álcool 70%");
        s.setQuantidade(20);

        s.setQuantidade(50);

        when(estoqueRepository.save(any())).thenReturn(s);

        EstoqueSuprimento atualizado = estoqueService.salvar(s);
        assertEquals(50, atualizado.getQuantidade());
    }

    @Test
    public void CT034_deveExcluirSuprimento() {
        estoqueService.deletar(1L);
        verify(estoqueRepository).deleteById(1L);
    }

    @Test
    public void CT035_deveListarTodosSuprimentos() {
        when(estoqueRepository.findAll()).thenReturn(List.of(new EstoqueSuprimento(), new EstoqueSuprimento()));
        assertEquals(2, estoqueService.listarTodos().size());
    }
}
