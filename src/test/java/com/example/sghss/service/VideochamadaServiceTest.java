package com.example.sghss.service;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import com.example.sghss.model.Consulta;
import com.example.sghss.model.Paciente;
import com.example.sghss.model.StatusVideo;
import com.example.sghss.model.Videochamada;
import com.example.sghss.repository.ConsultaRepository;
import com.example.sghss.repository.VideochamadaRepository;

@SpringBootTest
public class VideochamadaServiceTest {

    @Autowired
    private VideochamadaService videochamadaService;

    @MockBean
    private VideochamadaRepository videochamadaRepository;

    @MockBean
    private ConsultaRepository consultaRepository;

    @MockBean
    private AuditoriaService auditoriaService;

    @Test
    public void CT025_deveCadastrarVideochamadaComConsulta() {
        Videochamada chamada = new Videochamada();
        Consulta consulta = new Consulta();
        consulta.setId(1L);
        chamada.setConsulta(consulta);
        chamada.setUrlSala("https://teleconsultas.sghss.com/#1112");
        chamada.setStatus(StatusVideo.AGENDADA); 

        when(videochamadaRepository.save(any())).thenReturn(chamada);
        when(consultaRepository.findById(1L)).thenReturn(Optional.of(consulta));

        Videochamada salva = videochamadaService.salvar(chamada, "admin");

        assertNotNull(salva);
        assertEquals("https://teleconsultas.sghss.com/#1112", salva.getUrlSala());
        verify(auditoriaService).registrarAcao(contains("Cadastro"), eq("Videochamada"), eq("admin"));
    }
    
    @Test
    public void CT026_deveListarTodasVideochamadas() {
        Videochamada v1 = new Videochamada();
        v1.setId(1L);
        v1.setUrlSala("https://teleconsultas.sghss.com/#1113");
        v1.setStatus(StatusVideo.AGENDADA);

        Videochamada v2 = new Videochamada();
        v2.setId(2L);
        v2.setUrlSala("https://teleconsultas.sghss.com/#1118");
        v2.setStatus(StatusVideo.FINALIZADA);

        when(videochamadaRepository.findAll()).thenReturn(List.of(v1, v2));

        List<Videochamada> chamadas = videochamadaService.listarTodas();

        assertEquals(2, chamadas.size());
        assertEquals("https://teleconsultas.sghss.com/#1113", chamadas.get(0).getUrlSala());
        assertEquals("https://teleconsultas.sghss.com/#1118", chamadas.get(1).getUrlSala());
    }

    
    @Test
    public void CT027_deveEditarVideochamada() {
        Videochamada chamada = new Videochamada();
        chamada.setId(1L);
        chamada.setUrlSala("https://teleconsultas.sghss.com/#1119");

        Consulta consulta = new Consulta();
        consulta.setId(1L);
        chamada.setConsulta(consulta);

        when(videochamadaRepository.existsById(1L)).thenReturn(true);
        when(videochamadaRepository.save(any())).thenReturn(chamada);
        when(consultaRepository.findById(1L)).thenReturn(Optional.of(consulta));	

        chamada.setUrlSala("https://teleconsultas.sghss.com/#1129");

        Videochamada atualizada = videochamadaService.salvar(chamada, "admin");

        assertEquals("nova-url", atualizada.getUrlSala());
        verify(auditoriaService).registrarAcao(contains("Edição"), eq("Videochamada"), eq("admin"));
    }
    
    @Test
    public void CT028_deveExcluirVideochamada() {
        Videochamada chamada = new Videochamada();
        chamada.setId(1L);

        Consulta consulta = new Consulta();
        Paciente paciente = new Paciente();
        paciente.setNome("João");
        consulta.setPaciente(paciente);
        consulta.setId(1L);
        chamada.setConsulta(consulta);

        when(videochamadaRepository.findById(1L)).thenReturn(Optional.of(chamada));
        when(consultaRepository.findById(1L)).thenReturn(Optional.of(consulta));

        videochamadaService.deletar(1L, "admin");

        verify(videochamadaRepository).deleteById(1L);
        verify(auditoriaService).registrarAcao(contains("Exclusão: João"), eq("Videochamada"), eq("admin"));
    }
}
