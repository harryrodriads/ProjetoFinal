package com.example.sghss.repository;
import com.example.sghss.model.Paciente;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PacienteRepository extends JpaRepository<Paciente, Long> {
	Optional<Paciente> findByUsuarioUsername(String username);
}
