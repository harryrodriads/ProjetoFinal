package com.example.sghss.service;
import com.example.sghss.model.Paciente;
import com.example.sghss.model.Prontuario;
import com.example.sghss.repository.ProntuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ProntuarioService {

    @Autowired
    private ProntuarioRepository prontuarioRepository;

    public Optional<Prontuario> buscarPorPacienteId(Long pacienteId) {
        return prontuarioRepository.findByPacienteId(pacienteId);
    }

    public Prontuario salvar(Prontuario prontuario) {
        return prontuarioRepository.save(prontuario);
    }
    
    public Optional<Prontuario> buscarPorPaciente(Paciente paciente) {
        return prontuarioRepository.findByPaciente(paciente);
    }
}
