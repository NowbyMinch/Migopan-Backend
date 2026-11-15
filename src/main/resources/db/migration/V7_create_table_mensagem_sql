CREATE TABLE mensagem (
    id BIGSERIAL PRIMARY KEY,
    grupo_id BIGINT NOT NULL REFERENCES grupo(id) ON DELETE CASCADE,
    usuario_id BIGINT NOT NULL REFERENCES usuario(id) ON DELETE CASCADE,
    texto VARCHAR(2000) NOT NULL,
    data_envio TIMESTAMP NOT NULL DEFAULT now(),
    CONSTRAINT chk_mensagem_texto CHECK (length(btrim(texto)) > 0)
);

CREATE INDEX idx_mensagem_grupo_id ON mensagem (grupo_id, id DESC);
