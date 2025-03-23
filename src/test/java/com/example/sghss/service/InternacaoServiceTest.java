package com.example.sghss.service;
import com.example.sghss.model.Internacao;
import com.example.sghss.model.Paciente;
import com.example.sghss.repository.InternacaoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class InternacaoServiceTest {

    @Autowired
    private InternacaoService internacaoService;

    @MockBean
    private InternacaoRepository internacaoRepository;

    @MockBean
    private AuditoriaService auditoriaService;

    @Test
    public void CT020_deveCadastrarInternacao() {
        Internacao internacao = new Internacao();
        Paciente paciente = new Paciente();
        paciente.setNome("João");
        internacao.setPaciente(paciente);

        when(internacaoRepository.save(any())).thenReturn(internacao);

        Internacao salva = internacaoService.salvar(internacao, "admin");

        assertEquals("João", salva.getPaciente().getNome());
    }

    @Test
    public void CT021_deveExcluirInternacaoComSucesso() {
        Internacao internacao = new Internacao();
        internacao.setId(1L);
        Paciente paciente = new Paciente();
        paciente.setNome("Maria");
        internacao.setPaciente(paciente);

        when(internacaoRepository.findById(1L)).thenReturn(Optional.of(internacao));

        internacaoService.deletar(1L, "admin");

        verify(internacaoRepository).deleteById(1L);
        verify(auditoriaService).registrarAcao(contains("Exclusão: Maria"), eq("Internação"), eq("admin"));
    }
}
