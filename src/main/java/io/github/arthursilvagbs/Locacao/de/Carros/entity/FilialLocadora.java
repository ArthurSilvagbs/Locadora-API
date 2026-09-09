package io.github.arthursilvagbs.Locacao.de.Carros.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "filial_locacao")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class FilialLocadora {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID idLocadora;

    @Setter
    @Column(name = "nome_filial",nullable = false, length = 50)
    private String nomeFilial;

    @Setter
    @Column(name = "cnpj_filial", nullable = false, length = 18)
    private String cnpjFilial;

    @Setter
    @Column(nullable = false, length = 2)
    private String uf;

    @Setter
    @Column(nullable = false, length = 30)
    private String cidade;

    @Setter
    @Column(nullable = false)
    private String endereco;

    @Setter
    @Column(nullable = false, length = 12)
    private String telefone;

    @Setter
    @Column(nullable = false)
    private String email;

    @Setter
    @OneToMany(mappedBy = "filialRetirada")
    private List<Locacao> locacoesRetiradas;

    @Setter
    @OneToMany(mappedBy = "filialDevolucao")
    private List<Locacao> locacoesDevolucoes;

    @Setter
    @OneToMany(mappedBy = "filialAtual")
    private List<Veiculo> veiculos;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public FilialLocadora(
            String nomeFilial,
            String cnpjFilial,
            String uf,
            String cidade,
            String endereco,
            String telefone,
            String email
    ) {
        this.nomeFilial = nomeFilial;
        this.cnpjFilial = cnpjFilial;
        this.uf = uf;
        this.cidade = cidade;
        this.endereco = endereco;
        this.telefone = telefone;
        this.email = email;
    }

}
