package com.example.sghss.repository;
import com.example.sghss.model.Paciente;
import com.example.sghss.model.Prontuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ProntuarioRepository extends JpaRepository<Prontuario, Long> {
    Optional<Prontuario> findByPacienteId(Long pacienteId);
    Optional<Prontuario> findByPaciente(Paciente paciente);
}
