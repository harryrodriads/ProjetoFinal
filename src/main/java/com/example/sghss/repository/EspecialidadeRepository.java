package com.example.sghss.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.sghss.model.Especialidade;

@Repository
public interface EspecialidadeRepository extends JpaRepository<Especialidade, Long> {
}