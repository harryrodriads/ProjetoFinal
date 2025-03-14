package com.example.sghss.repository;
import com.example.sghss.model.Internacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface InternacaoRepository extends JpaRepository<Internacao, Long> {
    @Query("SELECT COUNT(i) FROM Internacao i WHERE i.paciente.id = :pacienteId")
    long countInternacoesPorPaciente(@Param("pacienteId") Long pacienteId);
}

