package com.example.sghss.service;
import com.example.sghss.model.Leito;
import com.example.sghss.repository.LeitoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class LeitoServiceTest {

    @Autowired
    private LeitoService leitoService;

    @MockBean
    private LeitoRepository leitoRepository;

    @MockBean
    private AuditoriaService auditoriaService;
    
    @Test
    public void CT022_deveListarLeitos() {
        when(leitoRepository.findAll()).thenReturn(List.of(new Leito(), new Leito()));

        List<Leito> lista = leitoService.listarTodos();

        assertEquals(2, lista.size());
    }

    @Test
    public void CT023_deveCadastrarNovoLeito() {
        Leito leito = new Leito();
        leito.setNumero("A101");

        when(leitoRepository.save(any())).thenReturn(leito);

        Leito salvo = leitoService.salvar(leito, "admin");

        assertEquals("A101", salvo.getNumero());
        verify(auditoriaService).registrarAcao(contains("Cadastro: A101"), eq("Leito"), eq("admin"));
    }
    
    @Test
    public void CT024_deveEditarLeitoComSucesso() {
        Leito leito = new Leito();
        leito.setId(1L);
        leito.setNumero("A101");

        when(leitoRepository.save(any())).thenReturn(leito);

        leito.setNumero("A202");
        Leito atualizado = leitoService.salvar(leito, "admin");

        assertEquals("A202", atualizado.getNumero());
        verify(auditoriaService).registrarAcao(contains("Edição: A202"), eq("Leito"), eq("admin"));
    }
}
