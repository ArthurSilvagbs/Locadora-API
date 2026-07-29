package io.github.arthursilvagbs.Locacao.de.Carros.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static io.github.arthursilvagbs.Locacao.de.Carros.entity.StatusVeiculo.DISPONIVEL;

@Entity
@Table(name = "veiculo")
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class Veiculo {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID idVeiculo;

    @Setter
    @Column(length = 17, unique = true, nullable = false)
    private String numeroChassi;

    @Setter
    @Column(length = 7, unique = true, nullable = false)
    private String placaVeiculo;

    @Setter
    @Column(length = 11, unique = true, nullable = false)
    private String renavam;

    @Setter
    @Column(length = 50, nullable = false)
    private String modelo;

    @Setter
    @Column(length = 50, nullable = false)
    private String marca;

    @Setter
    @Column(nullable = false)
    private LocalDateTime ano;

    @Setter
    @Column
    private String cor;

    @Setter
    @Enumerated(EnumType.STRING)
    private CategoriaVeiculo categoriaVeiculo;

    @Setter
    @Column(precision = 7, scale = 1, nullable = false)
    private Integer quilometragem;

    @Setter
    @OneToMany(mappedBy = "veiculo")
    private List<Manutencao> manutencoes;

    @Setter
    @OneToMany(mappedBy = "veiculo")
    private List<Locacao> historicoLocacoes;

    @Setter
    @ManyToOne
    @JoinColumn(name = "filial_atual", nullable = false)
    private FilialLocadora filialAtual;

    @Setter
    @Enumerated(EnumType.STRING)
    private StatusVeiculo statusVeiculo;

    @Setter
    @Column(precision = 6, scale = 2)
    private BigDecimal valorDiaria;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public Veiculo(
            String numeroChassi,
            String placaVeiculo,
            String renavam,
            String modelo,
            String marca,
            LocalDateTime ano,
            String cor,
            CategoriaVeiculo categoriaVeiculo,
            Integer quilometragem,
            FilialLocadora filialAtual
    ) {
        this.numeroChassi = numeroChassi;
        this.placaVeiculo = placaVeiculo;
        this.renavam = renavam;
        this.modelo = modelo;
        this.marca = marca;
        this.ano = ano;
        this.cor = cor;
        this.categoriaVeiculo = categoriaVeiculo;
        this.statusVeiculo = StatusVeiculo.DISPONIVEL;
        this.quilometragem = quilometragem;
        this.filialAtual = filialAtual;
    }
}
