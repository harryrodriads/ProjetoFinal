package com.example.sghss.repository;
import com.example.sghss.model.Especialidade;
import com.example.sghss.model.Profissional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProfissionalRepository extends JpaRepository<Profissional, Long> {
	List<Profissional> findByEspecialidade(Especialidade especialidade);
    boolean existsByRegistroProf(String registroProf);
}

