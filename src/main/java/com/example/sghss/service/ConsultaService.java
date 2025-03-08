package com.example.sghss.service;
import com.example.sghss.model.Consulta;
import com.example.sghss.repository.ConsultaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ConsultaService {

    @Autowired
    private ConsultaRepository consultaRepository;

    public List<Consulta> listarTodas() {
        return consultaRepository.findAll();
    }

    public Optional<Consulta> buscarPorId(Long id) {
        return consultaRepository.findById(id);
    }

    public void salvar(Consulta consulta) {
        consultaRepository.save(consulta);
    }

    public void excluir(Long id) {
        consultaRepository.deleteById(id);
    }
    
    public List<Map<String, Object>> buscarConsultasPorPaciente(Long pacienteId) {
        List<Object[]> resultados = consultaRepository.findConsultasByPacienteId(pacienteId);
        
        List<Map<String, Object>> consultas = new ArrayList<>();
        for (Object[] row : resultados) {
            Map<String, Object> consulta = new HashMap<>();
            consulta.put("id", row[0]);
            consulta.put("dataHora", row[1]);
            consulta.put("status", row[2]);
            consulta.put("profissionalNome", row[3]);
            consultas.add(consulta);
        }
        
        return consultas;
    }
}
