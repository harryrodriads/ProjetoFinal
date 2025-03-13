package com.example.sghss.model;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "estoque_suprimentos")
public class EstoqueSuprimento {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String produto;
    private Integer quantidade;
    private LocalDateTime dataAtualizacao;

    @PrePersist
    @PreUpdate
    private void atualizarData() {
        this.dataAtualizacao = LocalDateTime.now();
    }

    // Getters e setters
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getProduto() {
        return produto;
    }
    public void setProduto(String produto) {
        this.produto = produto;
    }

    public Integer getQuantidade() {
        return quantidade;
    }
    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }

    public LocalDateTime getDataAtualizacao() {
        return dataAtualizacao;
    }
    public void setDataAtualizacao(LocalDateTime dataAtualizacao) {
        this.dataAtualizacao = dataAtualizacao;
    }
}
