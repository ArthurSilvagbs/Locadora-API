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
import java.util.UUID;

@Entity
@Table(name = "manutencao")
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class Manutencao {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID idManutencao;

    @Setter
    @ManyToOne
    @JoinColumn(name = "veiculo", nullable = false)
    private Veiculo veiculo;

    @Setter
    @Column(nullable = false)
    private LocalDateTime dataManutencao;

    @Setter
    @Column(nullable = false)
    private String descricao;

    @Setter
    @Column(name = "quilometragem_veiculo", precision = 7, scale = 1)
    private Integer quilimetragemVeiculo;

    @Setter
    @Column(precision = 7, scale = 2, nullable = false)
    private BigDecimal valor;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public Manutencao(
            Veiculo veiculo,
            LocalDateTime dataManutencao,
            String descricao,
            BigDecimal valor
    ) {
        this.veiculo = veiculo;
        this.dataManutencao = dataManutencao;
        this.descricao = descricao;
        this.valor = valor;
    }
}
