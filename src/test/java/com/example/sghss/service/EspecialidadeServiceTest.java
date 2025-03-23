package com.example.sghss.service;
import com.example.sghss.model.Especialidade;
import com.example.sghss.repository.EspecialidadeRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class EspecialidadeServiceTest {

    @Autowired
    private EspecialidadeService especialidadeService;

    @MockBean
    private EspecialidadeRepository especialidadeRepository;

    @MockBean
    private AuditoriaService auditoriaService;

    // RF012
    @Test
    public void CT015_deveListarEspecialidades() {
        when(especialidadeRepository.findAll()).thenReturn(List.of(new Especialidade(), new Especialidade()));

        assertEquals(2, especialidadeService.listarTodas().size());
    }

    @Test
    public void CT016_deveCadastrarEspecialidade() {
        Especialidade esp = new Especialidade();
        esp.setNome("Cardiologia");

        when(especialidadeRepository.save(any())).thenReturn(esp);

        Especialidade salvo = especialidadeService.salvar(esp, "admin");

        assertEquals("Cardiologia", salvo.getNome());
        verify(auditoriaService).registrarAcao(contains("Cadastro: Cardiologia"), eq("Especialidade"), eq("admin"));
    }

    @Test
    public void CT017_deveEditarEspecialidade() {
        Especialidade esp = new Especialidade();
        esp.setId(1L);
        esp.setNome("Antigo");

        when(especialidadeRepository.save(any())).thenReturn(esp);

        esp.setNome("Atualizado");
        Especialidade atualizado = especialidadeService.salvar(esp, "admin");

        assertEquals("Atualizado", atualizado.getNome());
        verify(auditoriaService).registrarAcao(contains("Edição: Atualizado"), eq("Especialidade"), eq("admin"));
    }

    @Test
    public void CT018_deveExcluirEspecialidadeSemProfissionais() {
        Especialidade esp = new Especialidade();
        esp.setId(1L);
        esp.setNome("Ginecologia");
        esp.setProfissionais(Collections.emptyList());

        when(especialidadeRepository.findById(1L)).thenReturn(Optional.of(esp));

        especialidadeService.deletar(1L, "admin");

        verify(especialidadeRepository).deleteById(1L);
        verify(auditoriaService).registrarAcao(contains("Exclusão: Ginecologia"), eq("Especialidade"), eq("admin"));
    }

    @Test
    public void CT019_naoDeveExcluirEspecialidadeComProfissionais() {
        Especialidade esp = new Especialidade();
        esp.setId(1L);
        esp.setNome("Ortopedia");
        esp.setProfissionais(List.of(new com.example.sghss.model.Profissional()));
        when(especialidadeRepository.findById(1L)).thenReturn(Optional.of(esp));

        Exception e = assertThrows(IllegalStateException.class, () -> {
            especialidadeService.deletar(1L, "admin");
        });

        assertEquals("Não é possível excluir a especialidade pois existem profissionais associados.", e.getMessage());
    }
}
