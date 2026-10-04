ALTER TABLE manutencao
    ADD COLUMN status_manutencao VARCHAR(255);

UPDATE manutencao manutencao
SET status_manutencao = CASE
    WHEN veiculo.status_veiculo = 'EM_MANUTENCAO' THEN 'EM_ANDAMENTO'
    ELSE 'CONCLUIDA'
END
FROM veiculo
WHERE veiculo.id_veiculo = manutencao.veiculo;

ALTER TABLE manutencao
    ALTER COLUMN status_manutencao SET NOT NULL;

ALTER TABLE manutencao
    ADD CONSTRAINT ck_manutencao_status_manutencao
    CHECK (
        status_manutencao IN (
            'EM_ANDAMENTO',
            'CONCLUIDA'
        )
    );