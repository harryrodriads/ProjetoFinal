package com.example.sghss.service;
import com.example.sghss.model.Consulta;
import com.example.sghss.repository.ConsultaRepository;
import com.example.sghss.repository.VideochamadaRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ConsultaService {

    @Autowired
    private ConsultaRepository consultaRepository;
    
    @Autowired
    private AuditoriaService auditoriaService;
    
    @Autowired
    private VideochamadaRepository videochamadaRepository;

    public List<Consulta> listarTodas() {
        return consultaRepository.findAll();
    }

    public Optional<Consulta> buscarPorId(Long id) {
        return consultaRepository.findById(id);
    }

    public boolean existeConsultaPorProfissional(Long profissionalId) {
        return consultaRepository.existsByProfissionalId(profissionalId);
    }
    
    @Transactional
    public Consulta salvar(Consulta consulta, String usuario) {
        boolean isNovaConsulta = consulta.getId() == null;

        Consulta novaConsulta = consultaRepository.save(consulta);
        String nomePaciente = (novaConsulta.getPaciente() != null) ? novaConsulta.getPaciente().getNome() : "Paciente Desconhecido";
        String acao = isNovaConsulta ? "Cadastro: "  + nomePaciente : "Edição: " + nomePaciente;
        String entidade = "Consulta";
        
        auditoriaService.registrarAcao(acao, entidade, usuario);

        return novaConsulta;
    }


    @Transactional
    public void deletar(Long id, String usuario) {
        Optional<Consulta> consultaOpt = consultaRepository.findById(id);

        if (consultaOpt.isPresent()) {
            Consulta consulta = consultaOpt.get();
            String entidade = "Consulta do Paciente: " + consulta.getPaciente().getNome();
            videochamadaRepository.deleteAllByConsulta(consulta);
            consultaRepository.deleteById(id);
            auditoriaService.registrarAcao("Exclusão", entidade, usuario);
        }
    }

    public List<Map<String, Object>> buscarConsultasPorPaciente(Long pacienteId) {
        List<Object[]> resultados = consultaRepository.findConsultasByPacienteId(pacienteId);

        List<Map<String, Object>> consultas = new ArrayList<>();
        for (Object[] row : resultados) {
            Map<String, Object> consulta = Map.of(
                "id", row[0],
                "dataHora", row[1],
                "status", row[2],
                "profissionalNome", row[3]
            );
            consultas.add(consulta);
        }

        return consultas;
    }
}
