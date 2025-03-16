package com.example.sghss.service;
import com.example.sghss.model.Usuario;
import com.example.sghss.repository.UsuarioRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditoriaService auditoriaService;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder, AuditoriaService auditoriaService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.auditoriaService = auditoriaService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + username));

        return User.withUsername(usuario.getUsername())
            .password(usuario.getPassword()) 
            .roles(usuario.getPerfil().name())
            .build();
    }

    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    public Optional<Usuario> buscarPorId(Long id) {
        return usuarioRepository.findById(id);
    }

    public Usuario salvar(Usuario usuario, String usuarioLogado) {
        boolean isNovoUsuario = usuario.getId() == null;

        if (!usuario.getPassword().startsWith("$2a$")) { 
            usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        }

        Usuario usuarioSalvo = usuarioRepository.save(usuario);

        String acao = isNovoUsuario ? "Cadastro: " + usuarioSalvo.getUsername() : "Edição: " + usuarioSalvo.getUsername();
        String entidade = "Usuário";

        auditoriaService.registrarAcao(acao, entidade, usuarioLogado);

        return usuarioSalvo;
    }

    public void deletar(Long id, String usuarioLogado) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(id);

        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            String entidade = "Usuário";

            usuarioRepository.deleteById(id);
            auditoriaService.registrarAcao("Exclusão: " + usuario.getUsername(), entidade, usuarioLogado);
        }
    }

    public boolean verificarSenha(String username, String senhaDigitada) {
        return usuarioRepository.findByUsername(username)
                .map(usuario -> passwordEncoder.matches(senhaDigitada, usuario.getPassword()))
                .orElse(false);
    }
    
    public Optional<Usuario> buscarPorUsername(String username) {
        return usuarioRepository.findByUsername(username);
    }
}
