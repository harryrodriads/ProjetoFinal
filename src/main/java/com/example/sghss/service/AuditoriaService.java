package com.example.sghss.service;
import com.example.sghss.model.Auditoria;
import com.example.sghss.repository.AuditoriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class AuditoriaService {

    @Autowired
    private AuditoriaRepository auditoriaRepository;

    public void registrarAcao(String acao, String entidade, String usuario) {
        Auditoria auditoria = new Auditoria(acao, entidade, usuario);
        auditoriaRepository.save(auditoria);
    }

    public List<Auditoria> listarTodas() {
        return auditoriaRepository.findAll();
    }
}
