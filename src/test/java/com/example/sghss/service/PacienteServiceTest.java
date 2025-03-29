package com.example.sghss.service;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import com.example.sghss.model.Paciente;
import com.example.sghss.model.Usuario;
import com.example.sghss.repository.PacienteRepository;
import com.example.sghss.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import com.example.sghss.repository.InternacaoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;

@SpringBootTest
@Transactional
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PacienteServiceTest {

    @Autowired
    private PacienteService pacienteService;

    @MockBean
    private PacienteRepository pacienteRepository;

    @MockBean
    private InternacaoRepository internacaoRepository;

    @MockBean
    private AuditoriaService auditoriaService;
    
    @MockBean
    private UsuarioRepository usuarioRepository;
    
    @Test
    public void CT001_deveCadastrarPacienteValido() {
        Paciente paciente = new Paciente();
        paciente.setNome("Harry Rodrigues Canedo");
        paciente.setCpf("35214523650");
        paciente.setTelefone("34984236956");
        paciente.setDataNascimento(LocalDate.parse("1994-02-04"));

        when(pacienteRepository.save(any())).thenReturn(paciente);

        Paciente salvo = pacienteService.salvar(paciente, "admin");

        assertNotNull(salvo);
        assertEquals("Harry Rodrigues Canedo", salvo.getNome());
        assertEquals("35214523650", salvo.getCpf());

        verify(auditoriaService, times(1))
            .registrarAcao(contains("Cadastro"), eq("Paciente"), eq("admin"));
    }
    
    @Test
    public void CT002_naoDeveCadastrarPacienteSemCpf() {
        Paciente paciente = new Paciente();
        paciente.setNome("Harry Rodrigues Canedo");
        paciente.setCpf(null); 
        paciente.setTelefone("34984236956");
        paciente.setDataNascimento(LocalDate.parse("1994-02-04"));

        when(pacienteRepository.save(any()))
            .thenThrow(new DataIntegrityViolationException("CPF é obrigatório"));

        assertThrows(DataIntegrityViolationException.class, () -> {
            pacienteService.salvar(paciente, "admin");
        });
    }
    
    @Test
    public void CT003_deveListarTodosPacientes() {
        pacienteRepository.deleteAll();

        Paciente paciente1 = new Paciente();
        paciente1.setNome("Carlos Andrade da Silva");
        paciente1.setCpf("35214523610");
        paciente1.setTelefone("34965321452");
        paciente1.setDataNascimento(LocalDate.parse("1980-11-01"));
        Paciente salvo1 = pacienteRepository.save(paciente1);

        Paciente paciente2 = new Paciente();
        paciente2.setNome("Maria Júlia Souza");
        paciente2.setCpf("32985432100");
        paciente2.setTelefone("31988887777");
        paciente2.setDataNascimento(LocalDate.parse("2005-07-20"));
        Paciente salvo2 = pacienteRepository.save(paciente2);

        List<Paciente> pacientes = pacienteRepository.findAll();

        assertEquals(2, pacientes.size());
        assertTrue(pacientes.stream().anyMatch(p -> p.getCpf().equals(salvo1.getCpf())));
        assertTrue(pacientes.stream().anyMatch(p -> p.getCpf().equals(salvo2.getCpf())));
    }

    @Test
    public void CT004_deveBuscarPacientePorUsername() {
        Paciente paciente = new Paciente();
        paciente.setNome("João da Silva");
        paciente.setCpf("12798312019");
        paciente.setTelefone("34999750990");
        paciente.setDataNascimento(LocalDate.parse("1962-10-12"));
        paciente = pacienteRepository.save(paciente);

        Usuario usuario = new Usuario();
        usuario.setUsername("joaoSilva");
        usuario.setPassword("senhaSegura");
        usuario.setPaciente(paciente);
        usuario = usuarioRepository.save(usuario);

        Optional<Paciente> encontrado = pacienteRepository.findByUsuarioUsername("joaoSilva");

        assertTrue(encontrado.isPresent());
        assertEquals("João da Silva", encontrado.get().getNome());
        assertEquals("12798312019", encontrado.get().getCpf());
        assertEquals(LocalDate.of(1962, 10, 12), encontrado.get().getDataNascimento());
    }

    @Test
    public void CT005_deveEditarPaciente() {
        Paciente pacienteExistente = new Paciente();
        pacienteExistente.setId(1L);
        pacienteExistente.setNome("Carla Maria Bri");
        pacienteExistente.setCpf("12345678901");
        pacienteExistente.setTelefone("31338142565");
        pacienteExistente.setDataNascimento(LocalDate.parse("1970-11-05"));

        Paciente pacienteAtualizado = new Paciente();
        pacienteAtualizado.setId(1L);
        pacienteAtualizado.setNome("Carla Maria Bright");
        pacienteAtualizado.setCpf("12345678901");
        pacienteAtualizado.setTelefone("31338142565");
        pacienteAtualizado.setDataNascimento(LocalDate.parse("1970-11-05"));

        when(pacienteRepository.save(any(Paciente.class))).thenReturn(pacienteAtualizado);

        pacienteExistente.setNome("Carla Maria Bright");
        Paciente atualizado = pacienteService.salvar(pacienteExistente, "admin");

        assertEquals("Carla Maria Bright", atualizado.getNome());
        assertEquals("12345678901", atualizado.getCpf());
        assertEquals("31338142565", atualizado.getTelefone());

        verify(auditoriaService, times(1))
            .registrarAcao(contains("Edição"), eq("Paciente"), eq("admin"));
    }

    @Test
    public void CT006_deveExcluirPacienteSemInternacoes() {
        Paciente paciente = new Paciente();
        paciente.setId(1L);
        paciente.setNome("Harry Rodrigues Canedo");
        paciente.setCpf("35214523650");
        paciente.setTelefone("34984236956");
        paciente.setDataNascimento(LocalDate.parse("1994-02-04"));

        when(pacienteRepository.findById(1L)).thenReturn(Optional.of(paciente));
        when(internacaoRepository.countInternacoesPorPaciente(1L)).thenReturn(0L);
        pacienteService.excluir(1L, "admin");
        verify(pacienteRepository, times(1)).deleteById(1L);
        verify(auditoriaService, times(1))
            .registrarAcao(contains("Exclusão"), eq("Paciente"), eq("admin"));
    }

    @Test
    public void CT007_naoDeveExcluirPacienteComInternacao() {
        Paciente paciente = new Paciente();
        paciente.setId(1L);

        when(pacienteRepository.findById(1L)).thenReturn(Optional.of(paciente));
        when(internacaoRepository.countInternacoesPorPaciente(1L)).thenReturn(2L);
        assertThrows(DataIntegrityViolationException.class, () -> {
            pacienteService.excluir(1L, "admin");
        });
        
        verify(pacienteRepository, never()).deleteById(anyLong());
    }
}
