ALTER TABLE locacao
    ADD COLUMN categoria_veiculo VARCHAR(255);

UPDATE locacao l
SET categoria_veiculo = v.categoria_veiculo
FROM veiculo v
WHERE v.id_veiculo = l.veiculo_id_veiculo;

ALTER TABLE locacao
    ALTER COLUMN categoria_veiculo SET NOT NULL,
    ALTER COLUMN veiculo_id_veiculo DROP NOT NULL;

ALTER TABLE locacao
    ADD CONSTRAINT ck_locacao_categoria_veiculo
    CHECK (
        categoria_veiculo IN (
            'HATCH',
            'SEDAN',
            'PICK_UP',
            'SUV',
            'MINI_VAN',
            'VAN',
            'FURGAO',
            'BLINDADO'
        )
    );