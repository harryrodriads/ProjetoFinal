package com.example.sghss.service;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.util.Optional;
import com.example.sghss.model.Usuario;
import com.example.sghss.repository.UsuarioRepository;

@SpringBootTest
public class UsuarioServiceTest {

    @Autowired
    private UsuarioService usuarioService;

    @MockBean
    private UsuarioRepository usuarioRepository;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @MockBean
    private AuditoriaService auditoriaService;

    @Test
    public void CT037_deveAutenticarUsuarioComSenhaCorreta() {
        Usuario u = new Usuario();
        u.setUsername("mariaPinheiro");
        u.setPassword("$2a$10$hash");

        when(usuarioRepository.findByUsername("mariaPinheiro")).thenReturn(Optional.of(u));
        when(passwordEncoder.matches("senha123", u.getPassword())).thenReturn(true);

        boolean autenticado = usuarioService.verificarSenha("maria", "senha123");

        assertTrue(autenticado);
    }

    @Test
    public void CT038_deveCadastrarUsuarioComSenhaCriptografada() {
        Usuario u = new Usuario();
        u.setUsername("brunoSantos");
        u.setPassword("senha123");

        when(passwordEncoder.encode("senha123")).thenReturn("$2a$hash");
        when(usuarioRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Usuario salvo = usuarioService.salvar(u, "admin");

        assertEquals("brunoSantos", salvo.getUsername());
        assertTrue(salvo.getPassword().startsWith("$2a$"));
        verify(auditoriaService).registrarAcao(contains("Cadastro"), eq("Usuário"), eq("admin"));
    }
}
