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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class UsuarioService implements UserDetailsService {

	 private final UsuarioRepository usuarioRepository;
	 private final PasswordEncoder passwordEncoder;

	 public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
	        this.usuarioRepository = usuarioRepository;
	        this.passwordEncoder = passwordEncoder;
	}  
	 
    private static final Logger logger = LoggerFactory.getLogger(UsuarioService.class);

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

    public Usuario salvar(Usuario usuario) {
        if (!usuario.getPassword().startsWith("$2a$")) { 
            usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        }
        return usuarioRepository.save(usuario);
    }


    public void deletar(Long id) {
        usuarioRepository.deleteById(id);
    }
    
    public boolean verificarSenha(String username, String senhaDigitada) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByUsername(username);
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            return passwordEncoder.matches(senhaDigitada, usuario.getPassword());
        }
        return false;
    }
    
    public boolean validarSenha(String senhaDigitada, String senhaArmazenada) {
        boolean senhaValida = passwordEncoder.matches(senhaDigitada, senhaArmazenada);
        logger.info("Senha digitada: " + senhaDigitada);
        logger.info("Senha válida? " + senhaValida);
        return senhaValida;
    }
}
