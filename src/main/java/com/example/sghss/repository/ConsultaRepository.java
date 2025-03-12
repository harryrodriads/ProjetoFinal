package com.example.sghss.repository;
import com.example.sghss.model.Consulta;
import com.example.sghss.model.Profissional;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ConsultaRepository extends JpaRepository<Consulta, Long> {
	@Transactional
    void deleteAllByProfissional(Profissional profissional);
	boolean existsByProfissionalId(Long profissionalId);
	@Query(value = """
		    SELECT c.id, c.data_hora, c.status, p.nome AS profissionalNome
		    FROM consultas c
		    LEFT JOIN profissionais p ON c.profissional_id = p.id
		    WHERE c.paciente_id = :pacienteId
		""", nativeQuery = true)
		List<Object[]> findConsultasByPacienteId(@Param("pacienteId") Long pacienteId);
		
		@Query("SELECT c FROM Consulta c JOIN FETCH c.profissional WHERE c.paciente.id = :pacienteId")
		List<Consulta> findByPacienteId(@Param("pacienteId") Long pacienteId);
}

