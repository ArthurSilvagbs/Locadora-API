package io.github.arthursilvagbs.Locacao.de.Carros.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "locacao")
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class Locacao {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Setter
    @ManyToOne
    @JoinColumn(nullable = false)
    private Cliente cliente;

    @Setter
    @ManyToOne
    @JoinColumn(nullable = false)
    private Veiculo veiculo;

    @Setter
    @Column(nullable = false, precision = 8, scale = 2)
    private BigDecimal valorLocacao;

    @Setter
    @ManyToOne
    @JoinColumn(nullable = false)
    private FilialLocadora filialRetirada;

    @Setter
    @ManyToOne
    @JoinColumn(nullable = false)
    private FilialLocadora filialDevolucao;

    @Setter
    @Enumerated(EnumType.STRING)
    private FormaPagamento formaPagamento;

    @Setter
    @Enumerated(EnumType.STRING)
    private StatusLocacao statusLocacao;

    @Setter
    @Column(nullable = false)
    private LocalDateTime dataRetirada;

    @Setter
    @Column(nullable = false)
    private LocalDateTime dataDevolucao;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public Locacao(
            Cliente cliente,
            Veiculo veiculo,
            BigDecimal valorLocacao,
            FilialLocadora filialRetirada,
            FilialLocadora filialDevolucao,
            FormaPagamento formaPagamento,
            LocalDateTime dataRetirada,
            LocalDateTime dataDevolucao
    ) {
        this.cliente = cliente;
        this.veiculo = veiculo;
        this.valorLocacao = valorLocacao;
        this.filialRetirada = filialRetirada;
        this.filialDevolucao = filialDevolucao;
        this.formaPagamento = formaPagamento;
        this.dataRetirada = dataRetirada;
        this.dataDevolucao = dataDevolucao;
        this.statusLocacao = StatusLocacao.PENDENTE_DE_RETIRADA;
    }
}
