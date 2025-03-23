package com.example.sghss.repository;
import com.example.sghss.model.Paciente;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PacienteRepository extends JpaRepository<Paciente, Long> {
	@Query("SELECT u.paciente FROM Usuario u WHERE u.username = :username")
	Optional<Paciente> findByUsuarioUsername(String username);
}
