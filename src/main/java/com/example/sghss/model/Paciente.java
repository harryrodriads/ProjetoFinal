package com.example.sghss.model;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "pacientes")
public class Paciente extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @NotBlank(message = "O nome é obrigatório")
    @Size(min = 3, max = 100, message = "O nome deve ter entre 3 e 100 caracteres")
    private String nome;

    @Column(unique = true, nullable = false)
    @NotBlank(message = "CPF é obrigatório")
    @Pattern(
      regexp = "^\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}$",
      message = "Formato de CPF inválido!"
    )
    private String cpf;
    @Past(message = "A data de nascimento deve ser anterior ao dia de hoje.")
    private LocalDate dataNascimento;
    
    @NotBlank(message = "O telefone é obrigatório")
    @Pattern(
        regexp = "\\(\\d{2}\\) \\d{4,5}-\\d{4}",
        message = "Formato de telefone inválido!"
    )
    private String telefone;
    
    @OneToMany(mappedBy = "paciente", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Consulta> consultas;
    
    @OneToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;


    public Paciente() {
    }

    public Paciente(String nome, String cpf, LocalDate dataNascimento, String telefone) {
        this.nome = nome;
        this.cpf = cpf;
        this.dataNascimento = dataNascimento;
        this.telefone = telefone;
    }

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

	public String getCpf() {
		return cpf;
	}

	public void setCpf(String cpf) {
		this.cpf = cpf;
	}

	public LocalDate getDataNascimento() {
		return dataNascimento;
	}

	public void setDataNascimento(LocalDate dataNascimento) {
		this.dataNascimento = dataNascimento;
	}

	public String getTelefone() {
		return telefone;
	}

	public void setTelefone(String telefone) {
		this.telefone = telefone;
	}
	
	public int getIdade() {
	    if (this.dataNascimento == null) {
	        return 0;
	    }
	    return java.time.Period.between(this.dataNascimento, java.time.LocalDate.now()).getYears();
	}

}
