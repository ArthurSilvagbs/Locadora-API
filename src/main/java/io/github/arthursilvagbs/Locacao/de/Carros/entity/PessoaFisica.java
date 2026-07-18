package io.github.arthursilvagbs.Locacao.de.Carros.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "pessoa_fisica")
@PrimaryKeyJoinColumn(name = "idCliente")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PessoaFisica extends Cliente{

    @Column(nullable = false, unique = true, length = 14)
    private String cpf;

    public PessoaFisica(
            String nome,
            String email,
            String telefone,
            String endereco,
            String cpf
    ) {
        super(nome, email, telefone, endereco);
        this.cpf = cpf;
    }
}
