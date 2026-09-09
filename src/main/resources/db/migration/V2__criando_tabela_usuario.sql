CREATE TABLE usuario (
    usuario_id UUID NOT NULL,
    created_at TIMESTAMP(6) NOT NULL,
    updated_at TIMESTAMP(6) NOT NULL,
    email VARCHAR(255) NOT NULL,
    senha_hash VARCHAR(255) NOT NULL,
    roles VARCHAR(255) NOT NULL,


    CONSTRAINT pk_usuario PRIMARY KEY (usuario_id),
    CONSTRAINT uk_usuario_email UNIQUE (email),
    CONSTRAINT ck_usuario_roles CHECK (
        roles IS NULL
        OR roles IN (
            'ADMIN',
            'GERENTE',
            'FUNCIONARIO',
            'CLIENTE'
        )
    )
);