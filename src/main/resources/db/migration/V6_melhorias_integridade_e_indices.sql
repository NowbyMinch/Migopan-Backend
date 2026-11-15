CREATE INDEX IF NOT EXISTS idx_lista_amigos_amigo ON lista_amigos(amigo_id);

ALTER TABLE grupo_membro
    ADD CONSTRAINT chk_grupo_membro_papel CHECK (papel IN ('ADMIN', 'MEMBRO'));
ALTER TABLE lista_amigos
    ADD CONSTRAINT chk_lista_amigos_status CHECK (status_amizade IN ('PENDENTE', 'ACEITA', 'RECUSADA'));

UPDATE usuario SET email = LOWER(email);
CREATE UNIQUE INDEX IF NOT EXISTS uq_usuario_email_lower ON usuario (LOWER(email));

DROP INDEX IF EXISTS idx_grupo_membro_usuario;
