CREATE TABLE tarefa (
    id BIGSERIAL PRIMARY KEY, 
    usuario_criador_id BIGINT NOT NULL REFERENCES usuario(id) ON DELETE CASCADE, 
    usuario_atribuido_id BIGINT REFERENCES usuario(id) ON DELETE CASCADE,
    grupo_id BIGINT REFERENCES grupo(id) ON DELETE CASCADE,
    titulo VARCHAR(150) NOT NULL,
    descricao TEXT,
    repeticao VARCHAR(20) NOT NULL DEFAULT 'NENHUMA', -- NENHUMA, DIARIA, SEMANAL, MENSAL
    horario_resolucao TIME,
    data_criacao TIMESTAMP NOT NULL DEFAULT now(),
    data_resolucao TIMESTAMP,
    concluida BOOLEAN NOT NULL DEFAULT false,
    -- Tarefa é OU pessoal (tem usuario_atribuido, não tem grupo) OU de grupo
    -- (tem grupo, não tem usuario_atribuido — vale para todos os membros, com
    -- uma única conclusão compartilhada)
    CONSTRAINT chk_tarefa_repeticao CHECK (
        repeticao IN ('NENHUMA','DIARIA','SEMANAL','MENSAL')
    ),

    CONSTRAINT chk_tarefa_pessoal_ou_grupo CHECK (
        (usuario_atribuido_id IS NOT NULL AND grupo_id IS NULL) OR
        (usuario_atribuido_id IS NULL AND grupo_id IS NOT NULL)
    )
);

CREATE INDEX idx_tarefa_grupo ON tarefa(grupo_id);
CREATE INDEX idx_tarefa_atribuido ON tarefa(usuario_atribuido_id);
CREATE INDEX idx_tarefa_criador ON tarefa(usuario_criador_id);


