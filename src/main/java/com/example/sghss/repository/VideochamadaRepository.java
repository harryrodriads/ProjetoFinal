package com.example.sghss.repository;
import com.example.sghss.model.Consulta;
import com.example.sghss.model.Videochamada;
import jakarta.transaction.Transactional;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface VideochamadaRepository extends JpaRepository<Videochamada, Long> {
    @Transactional
    void deleteAllByConsulta(Consulta consulta);
    List<Videochamada> findByConsultaPacienteId(Long pacienteId);
}
