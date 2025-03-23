package com.example.sghss.service;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import com.example.sghss.model.Auditoria;
import com.example.sghss.repository.AuditoriaRepository;

@SpringBootTest
public class AuditoriaServiceTest {

    @Autowired
    private AuditoriaRepository auditoriaRepository;

    @Autowired
    private AuditoriaService auditoriaService;

    @BeforeEach
    public void limparBancoDados() {
        auditoriaRepository.deleteAll();
    }

    @Test
    public void CT036_deveListarTodasAuditorias() {
        Auditoria auditoria1 = new Auditoria();
        auditoria1.setAcao("Criação");
        auditoria1.setEntidade("Paciente");
        auditoria1.setUsuario("admin");

        Auditoria auditoria2 = new Auditoria();
        auditoria2.setAcao("Edição");
        auditoria2.setEntidade("Consulta");
        auditoria2.setUsuario("joao.silva");

        auditoriaRepository.saveAll(List.of(auditoria1, auditoria2));

        List<Auditoria> auditorias = auditoriaService.listarTodas();

        assertEquals(2, auditorias.size());
        assertEquals("Criação", auditorias.get(0).getAcao());
        assertEquals("Edição", auditorias.get(1).getAcao());
    }
}
