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
@Table(name = "cliente")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Inheritance(strategy = InheritanceType.JOINED)
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID idClienteq;

    @Setter
    @Column(nullable = false, length = 80)
    private String nome;

    @Setter
    @Column(nullable = false, length = 100, unique = true)
    private String email;

    @Setter
    @Column(nullable = false, length = 12)
    private String telefone;

    @Setter
    @Column(nullable = false)
    private String endereco;

    @Setter
    @OneToMany(mappedBy = "cliente")
    private List<Locacao> locacoes;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public Cliente(String nome, String email, String telefone, String endereco) {
        this.nome = nome;
        this.email = email;
        this.telefone = telefone;
        this.endereco = endereco;
    }
}
