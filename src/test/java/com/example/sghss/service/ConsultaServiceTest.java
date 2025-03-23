package com.example.sghss.service;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import com.example.sghss.model.Consulta;
import com.example.sghss.model.Paciente;
import com.example.sghss.repository.ConsultaRepository;
import com.example.sghss.repository.VideochamadaRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import java.time.LocalDateTime;
import java.util.*;

@SpringBootTest
public class ConsultaServiceTest {

    @Autowired
    private ConsultaService consultaService;

    @MockBean
    private ConsultaRepository consultaRepository;

    @MockBean
    private VideochamadaRepository videochamadaRepository;

    @MockBean
    private AuditoriaService auditoriaService;

    @Test
    public void CT008_deveBuscarHistoricoConsultasDoPaciente() {
        Long pacienteId = 1L;

        Consulta c1 = new Consulta();
        c1.setId(1L);
        c1.setDataHora(LocalDateTime.now().minusDays(10));

        Consulta c2 = new Consulta();
        c2.setId(2L);
        c2.setDataHora(LocalDateTime.now().minusDays(2));

        when(consultaRepository.findByPacienteIdOrderByDataHora(pacienteId)).thenReturn(List.of(c1, c2));

        List<Consulta> historico = consultaService.buscarPorPacienteId(pacienteId);

        assertEquals(2, historico.size());
    }

    @Test
    public void CT009_deveAgendarConsultaComSucesso() {
        Consulta novaConsulta = new Consulta();
        Paciente paciente = new Paciente();
        paciente.setNome("Maria");

        novaConsulta.setPaciente(paciente);
        novaConsulta.setDataHora(LocalDateTime.of(2025, 3, 30, 14, 0));
        novaConsulta.setStatus("Agendada");

        when(consultaRepository.save(any())).thenReturn(novaConsulta);

        Consulta salva = consultaService.salvar(novaConsulta, "admin");

        assertNotNull(salva);
        assertEquals("Maria", salva.getPaciente().getNome());
        verify(auditoriaService).registrarAcao(contains("Cadastro"), eq("Consulta"), eq("admin"));
    }

    @Test
    public void CT010_deveEditarConsulta() {
        Consulta consulta = new Consulta();
        Paciente paciente = new Paciente();
        paciente.setNome("Carlos");
        consulta.setPaciente(paciente);
        consulta.setId(1L);
        consulta.setDataHora(LocalDateTime.of(2025, 4, 1, 10, 0));
        consulta.setStatus("Agendada");

        consulta.setDataHora(LocalDateTime.of(2025, 4, 2, 15, 0));

        when(consultaRepository.save(any())).thenReturn(consulta);

        Consulta atualizada = consultaService.salvar(consulta, "admin");

        assertEquals(LocalDateTime.of(2025, 4, 2, 15, 0), atualizada.getDataHora());
        verify(auditoriaService).registrarAcao(contains("Edição"), eq("Consulta"), eq("admin"));
    }

    @Test
    public void CT011_deveCancelarConsulta() {
        Consulta consulta = new Consulta();
        Paciente paciente = new Paciente();
        paciente.setNome("João");
        consulta.setId(1L);
        consulta.setPaciente(paciente);
        consulta.setStatus("Agendada");

        when(consultaRepository.findById(1L)).thenReturn(Optional.of(consulta));
        when(consultaRepository.save(any())).thenReturn(consulta);

        consultaService.cancelarConsulta(1L, "admin");

        assertEquals("Cancelada", consulta.getStatus());
        verify(auditoriaService).registrarAcao(contains("Cancelamento"), eq("Consulta"), eq("admin"));
    }
}
