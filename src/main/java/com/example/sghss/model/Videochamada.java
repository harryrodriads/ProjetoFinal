package com.example.sghss.model;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "videochamadas")
public class Videochamada extends BaseEntity {

    @Id
    @JsonIgnore
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "consulta_id", nullable = false)
    @NotNull(message = "A consulta associada é obrigatória")
    private Consulta consulta;

    @Column(nullable = false)
    @NotBlank(message = "A URL da sala de videochamada é obrigatória")
    private String urlSala;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull(message = "O status da videochamada é obrigatório")
    private StatusVideo status;

    public Videochamada() {
    }

    public Videochamada(Consulta consulta, String urlSala, StatusVideo status) {
        this.consulta = consulta;
        this.urlSala = urlSala;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Consulta getConsulta() {
        return consulta;
    }

    public void setConsulta(Consulta consulta) {
        this.consulta = consulta;
    }

    public String getUrlSala() {
        return urlSala;
    }

    public void setUrlSala(String urlSala) {
        this.urlSala = urlSala;
    }

    public StatusVideo getStatus() {
        return status;
    }

    public void setStatus(StatusVideo status) {
        this.status = status;
    }
}
