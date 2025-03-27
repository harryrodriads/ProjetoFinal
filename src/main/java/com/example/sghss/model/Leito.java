package com.example.sghss.model;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "leitos")
public class Leito {

    @Id
    @JsonIgnore
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    @NotBlank(message = "O número do leito é obrigatório")
    private String numero;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull(message = "O status do leito é obrigatório")
    private StatusLeito status;

    public Leito() {
    }

    public Leito(String numero, StatusLeito status) {
        this.numero = numero;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public StatusLeito getStatus() {
        return status;
    }

    public void setStatus(StatusLeito status) {
        this.status = status;
    }
}
