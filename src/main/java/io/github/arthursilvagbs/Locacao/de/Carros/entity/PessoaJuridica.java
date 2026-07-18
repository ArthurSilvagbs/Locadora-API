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
@Table(name = "pessoa_juridica")
@PrimaryKeyJoinColumn(name = "idCliente")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PessoaJuridica extends Cliente{

    @Column(nullable = false, unique = true, length = 18)
    private String cnpj;

    public PessoaJuridica(
            String nome,
            String email,
            String telefone,
            String endereco,
            String cnpj
    ) {
        super(nome, email, telefone, endereco);
        this.cnpj = cnpj;
    }
}
