CREATE TABLE cliente (
    id_cliente UUID NOT NULL,
    created_at TIMESTAMP(6) NOT NULL,
    updated_at TIMESTAMP(6) NOT NULL,
    nome VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    telefone VARCHAR(12) NOT NULL,
    endereco VARCHAR(255) NOT NULL,
    CONSTRAINT pk_cliente PRIMARY KEY (id_cliente),
    CONSTRAINT uk_cliente_email UNIQUE (email)
);

CREATE TABLE filial_locacao (
    id_locadora UUID NOT NULL,
    created_at TIMESTAMP(6) NOT NULL,
    updated_at TIMESTAMP(6) NOT NULL,
    nome_filial VARCHAR(50) NOT NULL,
    cnpj_filial VARCHAR(18) NOT NULL,
    uf VARCHAR(2) NOT NULL,
    cidade VARCHAR(30) NOT NULL,
    endereco VARCHAR(255) NOT NULL,
    telefone VARCHAR(12) NOT NULL,
    email VARCHAR(255) NOT NULL,
    CONSTRAINT pk_filial_locacao PRIMARY KEY (id_locadora)
);

CREATE TABLE pessoa_fisica (
    id_cliente UUID NOT NULL,
    cpf VARCHAR(14) NOT NULL,
    CONSTRAINT pk_pessoa_fisica PRIMARY KEY (id_cliente),
    CONSTRAINT uk_pessoa_fisica_cpf UNIQUE (cpf),
    CONSTRAINT fk_pessoa_fisica_cliente FOREIGN KEY (id_cliente) REFERENCES cliente (id_cliente)
);

CREATE TABLE pessoa_juridica (
    id_cliente UUID NOT NULL,
    cnpj VARCHAR(18) NOT NULL,
    CONSTRAINT pk_pessoa_juridica PRIMARY KEY (id_cliente),
    CONSTRAINT uk_pessoa_juridica_cnpj UNIQUE (cnpj),
    CONSTRAINT fk_pessoa_juridica_cliente FOREIGN KEY (id_cliente) REFERENCES cliente (id_cliente)
);

CREATE TABLE veiculo (
    id_veiculo UUID NOT NULL,
    created_at TIMESTAMP(6) NOT NULL,
    updated_at TIMESTAMP(6) NOT NULL,
    numero_chassi VARCHAR(17) NOT NULL,
    placa_veiculo VARCHAR(7) NOT NULL,
    renavam VARCHAR(11) NOT NULL,
    modelo VARCHAR(50) NOT NULL,
    marca VARCHAR(50) NOT NULL,
    ano INTEGER NOT NULL,
    cor VARCHAR(30),
    categoria_veiculo VARCHAR(255),
    quilometragem DOUBLE PRECISION NOT NULL,
    filial_atual UUID NOT NULL,
    status_veiculo VARCHAR(255),
    valor_diaria NUMERIC(6, 2),
    CONSTRAINT pk_veiculo PRIMARY KEY (id_veiculo),
    CONSTRAINT uk_veiculo_numero_chassi UNIQUE (numero_chassi),
    CONSTRAINT uk_veiculo_placa_veiculo UNIQUE (placa_veiculo),
    CONSTRAINT uk_veiculo_renavam UNIQUE (renavam),
    CONSTRAINT ck_veiculo_categoria CHECK (
        categoria_veiculo IS NULL
        OR categoria_veiculo IN (
            'HATCH',
            'SEDAN',
            'PICK_UP',
            'SUV',
            'MINI_VAN',
            'VAN',
            'FURGAO',
            'BLINDADO'
        )
    ),
    CONSTRAINT ck_veiculo_status CHECK (
        status_veiculo IS NULL
        OR status_veiculo IN (
            'DISPONIVEL',
            'LOCADO',
            'EM_MANUTENCAO',
            'INATIVO'
        )
    ),
    CONSTRAINT fk_veiculo_filial_atual FOREIGN KEY (filial_atual) REFERENCES filial_locacao (id_locadora)
);

CREATE TABLE manutencao (
    id_manutencao UUID NOT NULL,
    created_at TIMESTAMP(6) NOT NULL,
    updated_at TIMESTAMP(6) NOT NULL,
    veiculo UUID NOT NULL,
    data_manutencao TIMESTAMP(6) NOT NULL,
    descricao VARCHAR(255) NOT NULL,
    quilometragem_veiculo INTEGER,
    valor NUMERIC(7, 2) NOT NULL,
    CONSTRAINT pk_manutencao PRIMARY KEY (id_manutencao),
    CONSTRAINT fk_manutencao_veiculo FOREIGN KEY (veiculo) REFERENCES veiculo (id_veiculo)
);

CREATE TABLE locacao (
    id_locacao UUID NOT NULL,
    updated_at TIMESTAMP(6) NOT NULL,
    cliente_id_cliente UUID NOT NULL,
    veiculo_id_veiculo UUID NOT NULL,
    valor_locacao NUMERIC (8, 2) NOT NULL,
    filial_retirada_id_locadora UUID NOT NULL,
    filial_devolucao_id_locadora UUID NOT NULL,
    forma_pagamento VARCHAR(255),
    status_locacao VARCHAR(255),
    km_retirada DOUBLE PRECISION,
    km_devolucao DOUBLE PRECISION,
    data_retirada TIMESTAMP(6) NOT NULL,
    data_devolucao TIMESTAMP(6) NOT NULL,
    CONSTRAINT pk_locacao PRIMARY KEY (id_locacao),
    CONSTRAINT ck_locacao_forma_pagamento CHECK (
        forma_pagamento IS NULL
        OR forma_pagamento IN (
            'CARTAO_DE_CREDITO',
            'CARTAO_DE_DEBITO',
            'BOLETO',
            'PIX',
            'DINHEIRO'
        )
    ),
    CONSTRAINT ck_locacao_status_locacao CHECK (
        status_locacao IS NULL
        OR status_locacao IN (
            'PENDENTE_DE_RETIRADA',
            'RETIRADO',
            'DEVOLVIDO',
            'CANCELADA'
        )
    ),
    CONSTRAINT fk_locacao_cliente FOREIGN KEY (cliente_id_cliente) REFERENCES cliente(id_cliente),
    CONSTRAINT fk_locacao_veiculo FOREIGN KEY (veiculo_id_veiculo) REFERENCES veiculo(id_veiculo),
    CONSTRAINT fk_locacao_filial_retirada FOREIGN KEY (filial_retirada_id_locadora) REFERENCES filial_locacao(id_locadora),
    CONSTRAINT fk_locacao_filial_devolucao FOREIGN KEY (filial_devolucao_id_locadora) REFERENCES filial_locacao(id_locadora)
);