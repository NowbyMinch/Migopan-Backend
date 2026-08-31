CREATE TABLE lista_amigos (
    usuario_id BIGINT NOT NULL REFERENCES usuario(id) ON DELETE CASCADE,
    amigo_id BIGINT NOT NULL REFERENCES usuario(id) ON DELETE CASCADE,
    status_amizade VARCHAR(20) NOT NULL DEFAULT 'PENDENTE' -- PENDENTE, ACEITA, RECUSADA
    data_amizade TIMESTAMP NOT NULL DEFAULT now(),
    PRIMARY KEY (usuario_id, amigo_id),
    CONSTRAINT chk_nao_amigo_de_si CHECK (usuario_id <> amigo_id)
);