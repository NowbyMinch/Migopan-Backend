-- =====================================================================
-- SCHEMA POSTGRESQL — App gamificado de tarefas (Usuario, Migo, Tarefa,
-- Loja, Grupos, Amigos, Acessorios)
-- =====================================================================

-- ---------------------------------------------------------------------
-- USUARIO
-- ---------------------------------------------------------------------
CREATE TABLE usuario (
    id              BIGSERIAL PRIMARY KEY,
    nome            VARCHAR(100) NOT NULL,
    email           VARCHAR(255) NOT NULL UNIQUE,
    email_verificado BOOLEAN NOT NULL DEFAULT false,
    senha_hash      VARCHAR(255) NOT NULL,
    streak          INTEGER NOT NULL DEFAULT 0,
    dinheiro        NUMERIC(12,2) NOT NULL DEFAULT 0,
    data_criacao    TIMESTAMP NOT NULL DEFAULT now(),
    ativo           BOOLEAN NOT NULL DEFAULT true
);

-- ---------------------------------------------------------------------
-- GRUPO
-- ---------------------------------------------------------------------
CREATE TABLE grupo (
    id              BIGSERIAL PRIMARY KEY,
    nome            VARCHAR(100) NOT NULL,
    descricao       TEXT,
    ativo           BOOLEAN NOT NULL DEFAULT true,
    data_criacao    TIMESTAMP NOT NULL DEFAULT now()
);

-- Relacionamento N:N Usuario <-> Grupo
CREATE TABLE grupo_membro (
    usuario_id      BIGINT NOT NULL REFERENCES usuario(id) ON DELETE CASCADE,
    grupo_id        BIGINT NOT NULL REFERENCES grupo(id) ON DELETE CASCADE,
    data_entrada    TIMESTAMP NOT NULL DEFAULT now(),
    bloqueado       BOOLEAN NOT NULL DEFAULT false,
    PRIMARY KEY (usuario_id, grupo_id)
);

CREATE INDEX idx_grupo_membro_grupo ON grupo_membro(grupo_id);

-- ---------------------------------------------------------------------
-- ACESSORIO
-- ---------------------------------------------------------------------
CREATE TABLE acessorio (
    id              BIGSERIAL PRIMARY KEY,
    tipo            VARCHAR(50) NOT NULL, -- CABECA, ROSTO, PEITO, MAO
    nome            VARCHAR(100) NOT NULL,
    custo           NUMERIC(12,2) NOT NULL DEFAULT 0
);

-- Relacionamento N:N Usuario <-> Acessorio (inventário)
CREATE TABLE inventario_usuario (
    usuario_id      BIGINT NOT NULL REFERENCES usuario(id) ON DELETE CASCADE,
    acessorio_id    BIGINT NOT NULL REFERENCES acessorio(id) ON DELETE CASCADE,
    data_aquisicao  TIMESTAMP NOT NULL DEFAULT now(),
    PRIMARY KEY (usuario_id, acessorio_id)
);

-- ---------------------------------------------------------------------
-- LISTA_AMIGOS (auto-relacionamento N:N em Usuario)
-- ---------------------------------------------------------------------
CREATE TABLE lista_amigos (
    usuario_id      BIGINT NOT NULL REFERENCES usuario(id) ON DELETE CASCADE,
    amigo_id        BIGINT NOT NULL REFERENCES usuario(id) ON DELETE CASCADE,
    status_amizade  VARCHAR(20) NOT NULL DEFAULT 'PENDENTE', -- PENDENTE, ACEITA, RECUSADA
    data_amizade    TIMESTAMP NOT NULL DEFAULT now(),
    PRIMARY KEY (usuario_id, amigo_id),
    CONSTRAINT chk_nao_amigo_de_si CHECK (usuario_id <> amigo_id)
);

CREATE INDEX idx_lista_amigos_amigo ON lista_amigos(amigo_id);

-- ---------------------------------------------------------------------
-- LOJA_ITEM
-- ---------------------------------------------------------------------
CREATE TABLE loja_item (
    id              BIGSERIAL PRIMARY KEY,
    acessorio_id    BIGINT NOT NULL REFERENCES acessorio(id) ON DELETE CASCADE,
    desconto        NUMERIC(5,2) NOT NULL DEFAULT 0 -- percentual 0-100
);

CREATE INDEX idx_loja_item_acessorio ON loja_item(acessorio_id);

-- ---------------------------------------------------------------------
-- TAREFA
-- ---------------------------------------------------------------------
CREATE TABLE tarefa (
    id                      BIGSERIAL PRIMARY KEY,
    usuario_criador_id      BIGINT NOT NULL REFERENCES usuario(id) ON DELETE CASCADE,
    usuario_atribuido_id    BIGINT REFERENCES usuario(id) ON DELETE CASCADE,
    grupo_id                BIGINT REFERENCES grupo(id) ON DELETE CASCADE,
    nome                    VARCHAR(150) NOT NULL,
    descricao               TEXT,
    repeticao               VARCHAR(20) NOT NULL DEFAULT 'NENHUMA', -- NENHUMA, DIARIA, SEMANAL, MENSAL
    horario_resolucao       TIME,
    data_criacao            TIMESTAMP NOT NULL DEFAULT now(),
    data_resolucao          TIMESTAMP,
    localizacao             VARCHAR(255),
    concluida               BOOLEAN NOT NULL DEFAULT false,
    -- Tarefa é OU pessoal (usuario_atribuido, sem grupo) OU de grupo
    -- (grupo, sem usuario_atribuido — vale para todos, conclusão compartilhada)
    CONSTRAINT chk_tarefa_pessoal_ou_grupo CHECK (
        (usuario_atribuido_id IS NOT NULL AND grupo_id IS NULL) OR
        (usuario_atribuido_id IS NULL AND grupo_id IS NOT NULL)
    )
);

CREATE INDEX idx_tarefa_grupo ON tarefa(grupo_id);
CREATE INDEX idx_tarefa_atribuido ON tarefa(usuario_atribuido_id);
CREATE INDEX idx_tarefa_criador ON tarefa(usuario_criador_id);

-- ---------------------------------------------------------------------
-- MIGO (o "bichinho de estimação" do usuário)
-- ---------------------------------------------------------------------
CREATE TABLE migo (
    id                      BIGSERIAL PRIMARY KEY,
    usuario_id              BIGINT NOT NULL REFERENCES usuario(id) ON DELETE CASCADE,
    nome                    VARCHAR(100) NOT NULL,
    vivo                    BOOLEAN NOT NULL DEFAULT true,
    personalidade           VARCHAR(50),
    cor_principal           VARCHAR(20),
    cor_secundaria          VARCHAR(20),
    data_criacao            TIMESTAMP NOT NULL DEFAULT now(),
    data_morte              TIMESTAMP,
    cosmetico_cabeca_id     BIGINT REFERENCES acessorio(id),
    cosmetico_rosto_id      BIGINT REFERENCES acessorio(id),
    cosmetico_peito_id      BIGINT REFERENCES acessorio(id),
    cosmetico_mao_id        BIGINT REFERENCES acessorio(id)
);

CREATE INDEX idx_migo_usuario ON migo(usuario_id);