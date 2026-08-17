CREATE TABLE grupo_membro (
    usuario_id BIG INT NOT NULL REFERENCES usuario(id) ON DELETE CASCADE,
    grupo_id BIG INT NOT NULL REFERENCES grupo(id) ON DELETE CASCADE,
    data_entrada TIMESTAMP NOT NULL DEFAULT now(),
    bloqueado BOOLEAN NOT NULL DEFAULT false,
    PRIMARY KEY (usuario_id, grupo_id)
);