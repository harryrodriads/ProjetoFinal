package com.example.sghss.model;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "profissionais")
public class Profissional extends BaseEntity{

    @Id
    @JsonIgnore
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O nome é obrigatório")
    private String nome;

    @NotBlank(message = "O registro profissional é obrigatório")
    @Column(unique = true, nullable = false)
    private String registroProf;

    @NotBlank(message = "O cargo é obrigatório")
    private String cargo;

    @ManyToOne
    @JoinColumn(name = "especialidade_id")
    @JsonBackReference
    private Especialidade especialidade;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getRegistroProf() {
        return registroProf;
    }

    public void setRegistroProf(String registroProf) {
        this.registroProf = registroProf;
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

	public Especialidade getEspecialidade() {
		return especialidade;
	}

	public void setEspecialidade(Especialidade especialidade) {
		this.especialidade = especialidade;
	}
    
 }
