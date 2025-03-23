package com.example.sghss.service;
import com.example.sghss.model.Profissional;
import com.example.sghss.repository.ProfissionalRepository;
import com.example.sghss.repository.ConsultaRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class ProfissionalServiceTest {

    @Autowired
    private ProfissionalService profissionalService;

    @MockBean
    private ProfissionalRepository profissionalRepository;

    @MockBean
    private ConsultaRepository consultaRepository;

    @MockBean
    private AuditoriaService auditoriaService;

    // RF010
    @Test
    public void CT012_deveListarProfissionais() {
        when(profissionalRepository.findAll()).thenReturn(List.of(new Profissional(), new Profissional()));

        List<Profissional> lista = profissionalService.listarTodos();

        assertEquals(2, lista.size());
    }

    @Test
    public void CT013_deveCadastrarProfissionalComRegistro() {
        Profissional profissional = new Profissional();
        profissional.setNome("João");
        profissional.setRegistroProf("CRM123");

        when(profissionalRepository.existsByRegistroProf("CRM123")).thenReturn(false);
        when(profissionalRepository.save(any())).thenReturn(profissional);

        boolean resultado = profissionalService.salvar(profissional, "admin");

        assertTrue(resultado);
        verify(auditoriaService).registrarAcao(contains("Cadastro: João"), eq("Profissional"), eq("admin"));
    }

    @Test
    public void CT014_naoDeveCadastrarProfissionalComRegistroDuplicado() {
        Profissional profissional = new Profissional();
        profissional.setRegistroProf("CRM123");

        when(profissionalRepository.existsByRegistroProf("CRM123")).thenReturn(true);

        boolean resultado = profissionalService.salvar(profissional, "admin");

        assertFalse(resultado);
        verify(profissionalRepository, never()).save(any());
    }
}
